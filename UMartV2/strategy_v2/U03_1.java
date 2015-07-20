/**
 * Copyright (c) 2001-2008 U-Mart Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * ---------------------------------------------------------------------
 */
package strategy_v2;

import java.util.Random;


/**
 * �Ǝ��G�[�W�F���g�p�̃e���v���[�g�ł��D
 * ���̃G�[�W�F���g�̃R�s�[���쐬���āC���O��U<�w�Дԍ�>�ɏC�����ĉ������D
 */
public class U03_1 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D
	private static final int noOfMovingAverage = 3;	//��ԂƂ��ė��p����敨���i�⌻�����i�̈ړ����ϒl�̐��D*2+1�ŏ��x�̐��ɂȂ�D
	private static final int noOfTerms = 4;	//�ړ����ς̌v�Z�ɗp����ߐ�
	private static final int noOfMiddleNode = 8;	//���ԑw�̃m�[�h���D
	private static final double DEFAULT_ALPHA = 0.1;	//Q�̊w�K���̃f�t�H���g�l
	private static final double DEFAULT_GAMMA = 0.8;	//Q�̊������̃f�t�H���g�l
	private static final double DEFAULT_ETA = 0.05;	//�d��w�̊w�K���̃f�t�H���g�l
	private static final double DEFAULT_GAIN_A = 0.001; //�V�O���C�h�֐��̃Q�C���̃f�t�H���g�l
	private static final double DEFAULT_GAIN_B = 10.0;
	private static final double DEFAULT_REWARDGAIN = 100.0;	//��V�̔{���̃f�t�H���g�l
	private static final int DEFAULT_MAX_QUANT = 100;	//�ő咍�����ʂ̃f�t�H���g�l
	private static final int DEFAULT_PRICE_WIDTH = 50;	//���i���̃f�t�H���g�l
	
	private Random fRandom;
	private double[] fX;	//��ԁiposition,pf[t],pf[t-1],pf[t-2],pr[t],pr[t-1],pr[t-2]�j�DnoOfMovingAverage*2+1�̗v�f���ɂȂ�D
	private double[] fMiddleNodeIn;	//���ԑw�ɓ�����`�a
	private double[] fMiddleNodeOut; //���ԑw�̏o�́i�V�O���C�h�֐���ʂ����l�j
	private double[] fOutputNode;	//�o�͑w�D
	private double[] fQ;	//���x�ł̊e�s���̕]���l�D�o�͑w�ɃV�O���C�h�֐���ʂ����l�ɂȂ�D
	private double[][] fWa;	//fWa[i][j]:���ԑwi�ւ̓���j����̏d��
	private double[][] fWb;	//fWb[i][j]:�o��i(�����C����C�����Ȃ���3��)�ւ̒��ԑwj����̏d�݁D
	private int fActionOld;	//1��O�ɑI�������s���DQ�̍X�V�ɗp����D
	private long fMoneyOld;	//1��O�̌����c���D
	private double fAlpha;	//Q'�̊w�K���D
	private double fGamma;	//Q'�̊������D
	private double fEta;	//�d�݂̊w�K���D
	private double fGainA;	//�V�O���C�h�֐��̃Q�C���D���ԑw�̏o�͗p�D
	private double fGainB;	//�V�O���C�h�֐��̃Q�C���D�o�͑w�̏o�͗p�D
	private double fRewardGain;	//��V�̃Q�C���D
	private int fMaxQuant;	//�ő咍�����ʁD
	private int fPriceWidth;	//���i���D
	
	private static double calculateMovingAverage(int[] prices, int term, int old) {
		int noOfSum = 0;
		double sum = 0.0;
		for(int i = 0; i < term; i++) {
			if(prices[prices.length - 1 - old*term - i] >= 0) {
				noOfSum++;
				sum += (double)prices[prices.length - 1 - old*term -  i];
			}
		}
		if(noOfSum == 0) {
			return UOrderForm.INVALID_PRICE;
		}
		return sum / (double)noOfSum;
	}
	
	private double sigmoid(double x, double gain) {
		return 1.0 / (1.0 + Math.exp(- gain * x));
	}
	
	/**
	 * �o�͑w�⒆�ԑw�ւ̐��`�a���v�Z����D
	 * @param w �d��
	 * @param input ����
	 * @return
	 */
	private static double calculateLinearSum(double[] w, double[] input) {
		double sum = 0;
		for(int i = 0; i < input.length; i++) {
			sum += w[i] * input[i];
		}
		return sum;
	}
	
	/**
	 * ���fX�Ɋ�Â��āC���ԑw�C�o�͑w�CQ�l���v�Z����D
	 */
	private void calculateNeuralNetwork() {
		for(int i = 0; i < fMiddleNodeIn.length; i++) {
			fMiddleNodeIn[i] = calculateLinearSum(fWa[i], fX);
		}
		for(int i = 0; i < fMiddleNodeOut.length; i++) {
			fMiddleNodeOut[i] = sigmoid(fMiddleNodeIn[i], fGainA);
		}
		for(int i = 0; i < fOutputNode.length; i++) {
			fOutputNode[i] = calculateLinearSum(fWb[i], fMiddleNodeOut);
		}
		for(int i = 0; i < fQ.length; i++) {
			fQ[i] = sigmoid(fOutputNode[i], fGainB);
		}
	}
	
	/**
	 * Q�l�Ɋ�Â��čs�������[���b�g�I������D
	 * @return �s���ԍ� UorderForm.NONE,SELL,BUY
	 */
	private int chooseAction() {
		int action = UOrderForm.NONE;
		//���[���b�g�I��
		double sumOfQ = 0.0;
		for(int i = 0; i < fQ.length; i++) {
			sumOfQ += fQ[i];
		}
		double randomDouble = fRandom.nextDouble() * sumOfQ;
		double tmpDouble = 0.0;
		for(int i = 0; i < fQ.length; i++) {
			tmpDouble += fQ[i];
			if(randomDouble < tmpDouble) {
				action = i;
				break;
			}
		}
		return action;
	}
	
	private void updateX(int position, int[] spotPrices, int[] futurePrices) {
		fX[0] = position;
		for(int i = 0; i < noOfMovingAverage; i++) {
			fX[i + 1] = calculateMovingAverage(futurePrices, noOfTerms, i);
			fX[noOfMovingAverage + i + 1] = calculateMovingAverage(spotPrices, noOfTerms, i);
		}
	}
	
	private double calculateReward(long money) {
		if(fMoneyOld == UOrderForm.INVALID_PRICE || money == UOrderForm.INVALID_PRICE || fMoneyOld == 0) {
			return 0;
		}
		return fRewardGain*((double)money / fMoneyOld - 1.0);
	}
	
	/**
	 * qTarget�Ƃ̌덷���������Ȃ�悤�ɁC1��O�ɑI�������s���Ɋւ��d�݂��X�V����D
	 * @param action 1��O�ɑI�������s��
	 * @param qTarget Q�l�̖ڕW�l
	 */
	private void updateWOf(int action, double qTarget) {
		double gradient;
		final double gradC = 2.0 * (fQ[action] - qTarget) * fGainB * (1.0 - fQ[action]) * fQ[action]; 
		for(int i = 0; i < fWa.length; i++) {
			for(int j = 0; j < fWa[i].length; j++) {
				gradient = gradC * fGainA * (1.0 - fMiddleNodeOut[i])*fMiddleNodeOut[i] * fWb[action][i]*fX[j];
				fWa[i][j] = fWa[i][j] - fEta * gradient;
			}
		}
		for(int j = 0; j < fWb[action].length; j++) {
			gradient = gradC * fMiddleNodeOut[j];
			fWb[action][j] = fWb[action][j] - fEta * gradient;
		}
	}
	
	private int determinePrice(int[] futurePrices, int[] spotPrices, int action) {
		int price = UOrderForm.INVALID_PRICE;
		if(futurePrices[futurePrices.length - 1] != UOrderForm.INVALID_PRICE) {
			price = futurePrices[futurePrices.length - 1];
		} else if(spotPrices[spotPrices.length - 1] != UOrderForm.INVALID_PRICE) {
			price = spotPrices[spotPrices.length - 1];
		}
		return (int)Math.round(price + fPriceWidth * (fQ[action] - 0.5));
	}
	
	private int determineQuantity(int action) {
		return (int)Math.round(fMaxQuant * fQ[action]);
	}
	
	private double getMaxQ() {
		double tmp = Math.max(fQ[UOrderForm.NONE], fQ[UOrderForm.SELL]);
		return Math.max(tmp, fQ[UOrderForm.BUY]);
	}

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U03_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//�����Ńt�B�[���h�ϐ��̒l�����������Ă��������D
		fRandom = getRandom();
		fX = new double[noOfMovingAverage * 2 + 1];
		for(int i = 0; i < fX.length; i++) {
			fX[i] = UOrderForm.INVALID_PRICE;
		}
		fMiddleNodeIn = new double[noOfMiddleNode];
		fMiddleNodeOut = new double[noOfMiddleNode];
		fOutputNode = new double[3];
		fQ = new double[fOutputNode.length];
		fWa = new double[fMiddleNodeIn.length][fX.length];
		for(int i = 0; i < fWa.length; i++) {
			for(int j = 0; j < fWa[i].length; j++) {
				fWa[i][j] = fRandom.nextDouble() - 0.5;
			}
		}
		fWb = new double[fOutputNode.length][fMiddleNodeOut.length];
		for(int i = 0; i < fWb.length; i++) {
			for(int j = 0; j < fWb[i].length; j++) {
				fWb[i][j] = fRandom.nextDouble() - 0.5;
			}
		}
		fActionOld = UOrderForm.NONE;
		fMoneyOld = UOrderForm.INVALID_PRICE;
		fAlpha = DEFAULT_ALPHA;
		fGamma = DEFAULT_GAMMA;
		fEta = DEFAULT_ETA;
		fGainA = DEFAULT_GAIN_A;
		fGainB = DEFAULT_GAIN_B;
		fRewardGain = DEFAULT_REWARDGAIN;
		fMaxQuant = DEFAULT_MAX_QUANT;
		fPriceWidth = DEFAULT_PRICE_WIDTH;
	}

  /**
   * �����[���쐬���܂��D
   * @param day ��
   * @param session ��
   * @param maxDays �������
   * @param noOfSessionsPerDay 1���̐ߐ�
   * @param spotPrices �������i�n��DspotPrices[0]����spotPrices[119]�܂ł�120�ߕ��̃f�[�^���i�[����Ă���DspotPrices[119]�����߂̉��i�ł��D�������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��Ă��������D
   * @param futurePrices �敨���i�n��DfuturePrices[0]����futurePrices[59]�܂ł�60�ߕ��̃f�[�^���i�[����Ă��܂��DfuturePrices[59]�����߂̉��i�ł��D�������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��Ă��������D�܂��C����J�n�߂��O�͌������i���i�[����Ă��܂��D
   * @param position �|�W�V�����D���Ȃ�Δ����z��(�����O�E�|�W�V����)�C���Ȃ�Δ���z���i�V���[�g�E�|�W�V�����j��\���܂��D
   * @param money �����c���D�^��long�ł��邱�Ƃɒ��ӂ��Ă��������D
   * @return UOrderForm[] �����[�̔z��
   */
	public UOrderForm[] makeOrderForms(int day, int session,
                                      int maxDays, int noOfSessionsPerDay,
                                      int[] spotPrices, int[] futurePrices,
                                      int position, long money) {

		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		
		// 1��O�ɑI�񂾍s���ɂ��ƂÂ��ăj���[�����l�b�g���[�N�̏d�݂��X�V����D
		final double qOld = fQ[fActionOld];	//1��O�ɑI�������s����Q�l�D
		final double reward = calculateReward(money);	//�����c���̍����V�Ƃ���D
		updateX(position, spotPrices, futurePrices);	//���x�̍X�V�D
		calculateNeuralNetwork();	//���݂̏��x�ƍX�V�O�̏d�݂��璆�ԑw�C�o�͑w�CQ�l���v�Z����D
		final double qTarget = (1 - fAlpha)*qOld + fAlpha*(reward + fGamma*getMaxQ());// Q'�l(Q�l�̖ڕW�l)�̌v�Z
		updateWOf(fActionOld, qTarget);	//1��O�ɑI�������s���Ɋւ��d�݂��X�V����D
		
		// �����敪�̌���D
		calculateNeuralNetwork();	//���݂̏��x�ƍX�V��̏d�݂��璆�ԑw�C�o�͑w�CQ�l���v�Z����D
		int action = chooseAction();	//Q�l�ɂ��ƂÂ��Ē����敪��I������D
		forms[0].setBuySell(action);	
		
		//�������i�̌���D
		forms[0].setPrice(determinePrice(futurePrices, spotPrices, action));
		
		//�������ʂ̌���D
		forms[0].setQuantity(determineQuantity(action));
		
		fActionOld = action;
		fMoneyOld = money;
		
		// ���݂̓��C���݂̐߁C���݂̌������i��\������D
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + ", future=" + futurePrices[futurePrices.length - 1]
					+ ", action=" + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice() + ", quantity=" + forms[0].getQuantity() + ", reward=" + reward + ", position=" + position);
		//println(arrayString("x=", fX));
		//println(arrayString("Mid=", fMiddleNodeOut));
		//println(arrayString("Out=", fOutputNode));
		//println(arrayString("Q=", fQ));
		//println(qOld + ", " + qTarget + "," + fQ[fActionOld]);
    return forms;
  }

  /**
   * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肵�܂��D
   * @param args �V�X�e���p�����[�^
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //���̃��\�b�h���I�[�o�[���C�h����K�v�͂���܂���D
  }
  
  public static String arrayString(String start, double[] src) {
  	String str = start;
  	str += src[0];
 		for(int i = 1; i < src.length; i++) {
  		str += "," + src[i];
  	}
  	return str;
  }
}
