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

import java.util.Arrays;
import java.util.Random;


/**
 * �Ǝ��G�[�W�F���g�p�̃e���v���[�g�ł��D
 * ���̃G�[�W�F���g�̃R�s�[���쐬���āC���O��U<�w�Дԍ�>�ɏC�����ĉ������D
 */
public class U19_2 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D
	public static final int DEFAULT_SHORT_TERM = 7;
	public static final int DEFAULT_MEDIUM_TERM = 15;
	public static final int DEFAULT_MIN_QUANT = 10;
	public static final int DEFAULT_MAX_QUANT = 100;
	public static final int DEFAULT_MAX_POSITION = 300;
	public static final String SHORT_TERM_KEY = "ShortTerm";
	public static final String MEDIUM_TERM_KEY = "MediumTerm";
	public static final String MIN_QUANT_KEY = "MinQuant";
	public static final String MAX_QUANT_KEY = "MaxQuant";
	public static final String MAX_POSITION_KEY = "MaxPosition";

	public static final int[] WEIGHT_VECTOR_SHORT = { -2, 3, 6, 7, 6, 3, -2};
	public static final int[] WEIGHT_VECTOR_MEDIUM = {-78, -13, 42, 87, 122, 147, 162, 167, 162, 147, 122, 87, 42, -13, -78};

	private int fShortTerm = DEFAULT_SHORT_TERM;
	private int fMediumTerm = DEFAULT_MEDIUM_TERM;
	private int fMinQuant = DEFAULT_MIN_QUANT;
	private int fMaxQuant = DEFAULT_MAX_QUANT;
	private int fMaxPosition = DEFAULT_MAX_POSITION;
	private double fPreviousShortTermMovingAverage;
	private double fPreviousMediumTermMovingAverage;

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U19_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//�����Ńt�B�[���h�ϐ��̒l�����������Ă��������D

	}


	/**
	 * �Z���̐ߐ���Ԃ�
	 * @return
	 */
	public int getShortTerm(){
		return fShortTerm;
	}

	/**
	 * �����̐ߐ���Ԃ�
	 * @return
	 */
	public int getMediumTerm(){
		return fMediumTerm;
	}

	/**
	 * �ŏ��������ʂ�Ԃ�
	 * @return
	 */
	public int getMinQuant(){
		return fMinQuant;
	}

	/**
	 * �ő咍�����ʂ�Ԃ�
	 * @return
	 */
	public int getMaxQuant(){
		return fMaxQuant;
	}
	/**
	 * �ő�|�W�V������Ԃ�
	 * @return
	 */
	public int getMaxPosition(){
		return fMaxPosition;
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
                                      int[] spotPrices, int[] futuresPrices,
                                      int position, long money) {

		Random rand = getRandom();
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();

		int action = chooseAction(futuresPrices);
		if (action == UOrderForm.SELL
				&& position < 0
				&& Math.abs(position) > fMaxPosition) {
			action = UOrderForm.NONE;
		}else if(action == UOrderForm.BUY
				&& position > 0
				&& Math.abs(position) > fMaxPosition){
			action = UOrderForm.NONE;
		}
		if(action == UOrderForm.NONE && Math.abs(position)>=150){
			action = (position>0)?UOrderForm.SELL:UOrderForm.BUY;
		}
		forms[0].setBuySell(action);

		forms[0].setPrice(determinPrice(action, futuresPrices));
		forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));

		println("day=" + day + ",\tsession=" + session
				+ ",\tPreviousMediumTermMovingAverage="
					+ (int)fPreviousMediumTermMovingAverage
				+ ",\tPreviousShortTermMovingAverage="
					+ (int)fPreviousShortTermMovingAverage
				+ ",\t" + forms[0].getBuySellByString()
				+ ",\tprice=" + forms[0].getPrice()
				+ ",\tquantity=" + forms[0].getQuantity());
		return forms;
  }

  /**
   * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肵�܂��D
   * @param args �V�X�e���p�����[�^
   */
  public void setParameters(String[] args) {
  	super.setParameters(args);
		for(int i = 0; i < args.length; ++i){
			String[] strArray = args[i].split("=");
			String key = strArray[0];
			String value = strArray[1];
			if(key.equals(UMovingAverageAgent.SHORT_TERM_KEY)){
				fShortTerm = Integer.parseInt(value);
				println("ShortTerm has been changed to " + fShortTerm);
			}else if(key.equals(UMovingAverageAgent.MEDIUM_TERM_KEY)){
				fMediumTerm = Integer.parseInt(value);
				println("MediumTerm has been changed to " + fMediumTerm);
			}else if(key.equals(UMovingAverageAgent.MIN_QUANT_KEY)){
				fMinQuant = Integer.parseInt(value);
				println("MinQuant has been changed to " + fMinQuant);
			}else if(key.equals(UMovingAverageAgent.MAX_QUANT_KEY)){
				fMaxQuant = Integer.parseInt(value);
				println("MaxQuant has benn changed to " + fMaxQuant);
			}else if(key.equals(UMovingAverageAgent.MAX_POSITION_KEY)){
				fMaxPosition = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fMaxPosition);
			}else{
				println("Unknown parameter: " + key
						+ " in UMovingAverageAgent.setParameters");
			}
		}
  }
  /**
	 * term ��-�ړ����ϒl���v�Z���ĕԂ����\�b�h
	 * @param prices
	 * @param term
	 * @return
	 */
	double calculateMovingAverage(int[] prices, int term){
		double sum = 0.0;
		for(int i = 0; i < term; ++i){
			if(prices[prices.length - 1 - i] < 0){
				return (double)UOrderForm.INVALID_PRICE;
			}
			sum += (double)prices[prices.length - 1 - i];
		}
		return sum / (double)term;
	}

	double calculateWeightedMovingAverage(int[] prices, int term){
		double sum = 0.0;

		for(int i = 0; i < term; ++i){
			if(prices[prices.length - 1 - i] < 0){
				return (double)UOrderForm.INVALID_PRICE;
			}
			if(term == fShortTerm){
				sum += (double)prices[prices.length - 1 - i] * WEIGHT_VECTOR_SHORT[i];
			}
			if(term == fMediumTerm){
				sum += (double)prices[prices.length - 1 - i] * WEIGHT_VECTOR_MEDIUM[i];
			}
		}
		if(term == fShortTerm){
			return sum / 21.0;
		}
		if(term == fMediumTerm){
			return sum / 1105.0;
		}
		return -1.0;
	}

	/**
	 * �����敪�����肵�ĕԂ����\�b�h
	 * @param prices
	 * @return
	 */
	int chooseAction(int[] prices){
		int action = UOrderForm.NONE;

		int[] previousPrices = Arrays.copyOfRange(prices, 1, prices.length);

		// �ړ����ς̌v�Z
		fPreviousMediumTermMovingAverage
			= calculateWeightedMovingAverage(previousPrices, fMediumTerm);
		fPreviousShortTermMovingAverage
			= calculateWeightedMovingAverage(previousPrices, fShortTerm);
		double currentMediumTermMovingAverage
			= calculateWeightedMovingAverage(prices, fMediumTerm);
		double currentShortTermMovingAverage
			= calculateWeightedMovingAverage(prices, fShortTerm);

		// �ړ����ς��v�Z�ł��Ȃ��ꍇ
		if (fPreviousMediumTermMovingAverage == (double)UOrderForm.INVALID_PRICE
			|| fPreviousShortTermMovingAverage == (double)UOrderForm.INVALID_PRICE
			|| currentMediumTermMovingAverage == (double)UOrderForm.INVALID_PRICE
			|| currentShortTermMovingAverage == (double)UOrderForm.INVALID_PRICE){
			return UOrderForm.NONE;
		}
		// �����敪��I������
		if ((fPreviousShortTermMovingAverage < fPreviousMediumTermMovingAverage)
				&& (currentShortTermMovingAverage < currentMediumTermMovingAverage)) {
			action = UOrderForm.BUY;
		}else if ((fPreviousShortTermMovingAverage > fPreviousMediumTermMovingAverage)
				&& (currentShortTermMovingAverage > currentMediumTermMovingAverage)) {
			action = UOrderForm.SELL;
		}else{
			action = UOrderForm.NONE;
		}
		return action;
	}

	/**
	 * �����敪action �Ɛ敨���i�n��prices ���璍�����i�����肵�C�Ԃ��܂��D
	 * @param action
	 * @param prices
	 * @return
	 */
	int determinPrice(int action, int[] prices){
		Random rand = getRandom();
		// ���݂�1�ߑO�̉��i��
		int priceDiff = prices[0] - prices[1];
		// �������i
		int price = UOrderForm.INVALID_PRICE;
		switch(action){
		// �����敪���u�����v�̏ꍇ
			case UOrderForm.BUY:
				price = prices[0]
						+ (int)(priceDiff + 0.25 * (double)priceDiff * rand.nextGaussian());
				break;
		// �����敪���u����v�̏ꍇ
			case UOrderForm.SELL:
				price = prices[0]
						- (int)(priceDiff + 0.25 * (double)priceDiff * rand.nextGaussian());
				break;
			default:
				break;
		}
		if(price <= 0){
			price = 1;
		}
		return price;
	}
}
