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

public class U17_2 extends UAgent {

	/** �Z���̐ߐ��̃f�t�H���g�l */
	public static final int DEFAULT_SHORT_TERM = 8;

	/** �����̐ߐ��̃f�t�H���g�l */
	public static final int DEFAULT_MEDIUM_TERM = 16;

	/** �����ʂ̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_QUANT = 100;

  /** �����ʂ̍ŏ��l�̃f�t�H���g�l */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** �|�W�V�����̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** ��������瓾����S�Ẳ��i��񂪖����̂Ƃ��Ɏg�����i�̃f�t�H���g�l */
  public static final int DEFAULT_NOMINAL_PRICE = 2200;

  /** 1�ߑO����̌��݂̐߉��i�̕ω������啝�����̏ꍇ */
  //private static final int CASE_PRICE_LARGE_FALL = 0;
  /** 1�ߑO����̌��݂̐߉��i�̕ω����������̏ꍇ */
  private static final int CASE_PRICE_FALL = 0;
  /** 1�ߑO����̌��݂̐߉��i�̕ω��������ω��̏ꍇ */
  private static final int CASE_PRICE_NOT_CHANGE = 1;
  /** 1�ߑO����̌��݂̐߉��i�̕ω������㏸�̏ꍇ */
  private static final int CASE_PRICE_RISE = 2;
  /** 1�ߑO����̌��݂̐߉��i�̕ω������啝�����̏ꍇ */
  //private static final int CASE_PRICE_LARGE_RISE = 4;

  /** �p�ӂ����ꍇ�̐� */
  private static final int CASE_NUMBER = 3;

  /** ���i�ω����啝�����̏ꍇ�̕ω����̍ŏ��l臒l */
  //private static final double THRESHOLD_LARGE_FALL = -3.0;
  /** ���i�ω��������̏ꍇ�̕ω����̍ŏ��l臒l */
  private static final double THRESHOLD_FALL = -0.3;
  /** ���i�ω������ω��̏ꍇ�̕ω����̍ŏ��l臒l */
  private static final double THRESHOLD_NOT_CHANGE = 0.3;
  /** ���i�ω����㏸�̏ꍇ�̕ω����̍ŏ��l臒l */
  //private static final double THRESHOLD_RISE = 3.0;

  /** ���������� */
  private Random fRandom;

  /** 1�ߑO�̌������i */
  private int prevspotPrice = DEFAULT_NOMINAL_PRICE;

  /** 1�ߑO�̐敨���i */
  private int prevfuturePrice = DEFAULT_NOMINAL_PRICE;

  /** �Z���̐ߐ� */
	private int fShortTerm = DEFAULT_SHORT_TERM;

	/** �����̐ߐ� */
	private int fMediumTerm = DEFAULT_MEDIUM_TERM;

  /** �����ʂ̍ő�l */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** �����ʂ̍ŏ��l */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** �|�W�V�����̍ő�l */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** ��������瓾����S�Ẳ��i��񂪌������̂Ƃ��Ɏg�����i */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

  /** 1�ߑO�ɂ�����Z���ړ����ϒl */
	private double fPreviousShortTermMovingAverage = UOrderForm.INVALID_PRICE;

	/** 1�ߑO�ɂ����钆���ړ����ϒl */
	private double fPreviousMediumTermMovingAverage = UOrderForm.INVALID_PRICE;

	/** 1�ߑO�ɂ����錻�����i�̕ω��� */
	private double[] prevspotpricechange = new double[3];

	/** 1�ߑO�̉��i�̕ω����ƌ��݂̌������i�̕ω����̏ꍇ�̐����J�E���g*/
	private int[][] pre1changecounter = new int[3][3];

	/** 2�ߑO�̉��i�̕ω����ƌ��݂̌������i�̕ω����̏ꍇ�̐����J�E���g*/
	private int[][] pre2changecounter = new int[3][3];

	/** 2�ߑO�̉��i�̕ω����ƌ��݂̌������i�̕ω����̏ꍇ�̐����J�E���g*/
	private int[][] pre3changecounter = new int[3][3];

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U17_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		for (int i = 0;i < CASE_NUMBER; i++){
			for (int j = 0;j < CASE_NUMBER; j++){
				pre1changecounter[i][j] = 0;
				pre2changecounter[i][j] = 0;
				pre3changecounter[i][j] = 0;
			}
		}
		for (int i = 0;i < prevspotpricechange.length; i++){
			prevspotpricechange[i] = 0;
		}
	}

	/**
	 * �Z���̐ߐ���Ԃ��܂��D
	 * @return �Z���̐ߐ�
	 */
	public int getShortTerm() {
		return fShortTerm;
	}

	/**
	 * �����̐ߐ���Ԃ��܂��D
	 * @return �����̐ߐ�
	 */
	public int getMediumTerm() {
		return fMediumTerm;
	}

	/**
	 * �ŏ��������ʂ�Ԃ��܂��D
	 * @return �ŏ���������
	 */
	public int getMinQuant() {
		return fMinQuant;
	}

	/**
	 * �ő咍�����ʂ�Ԃ��܂��D
	 * @return �ő咍������
	 */
	public int getMaxQuant() {
		return fMaxQuant;
	}

	/**
	 * �ő�|�W�V������Ԃ��܂��D
	 * @return �ő�|�W�V����
	 */
	public int getMaxPosition() {
		return fMaxPosition;
	}

	/**
	 * �s�ꉿ�i������̂Ƃ��̒������i��Ԃ��܂��D
	 * @return �s�ꉿ�i������̂Ƃ��̒������i
	 */
	public int getNominalPrice() {
		return fNominalPrice;
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
		double spotpricechange = (getLatestPrice(spotPrices)-prevspotPrice)*100.0/prevspotPrice;
		// 5�߈ȍ~�ł���Εω����̃J�E���g���J�n
		if((day-1)*noOfSessionsPerDay + session > 4){
			int state = determinChangeCase(spotpricechange);
			int pre1state =determinChangeCase(prevspotpricechange[0]);
			int pre2state =determinChangeCase(prevspotpricechange[1]);
			int pre3state =determinChangeCase(prevspotpricechange[2]);
			pre1changecounter[pre1state][state] += 1;
			pre2changecounter[pre2state][state] += 1;
			pre3changecounter[pre3state][state] += 1;

			double[] predictnextstate= {1.0,1.0,1.0};
			//3�ߑO����̕ω�����p���Ď��̕ω���\�z
			for( int i = 0; i < CASE_NUMBER; i++){
				predictnextstate[i] *= calcurateProbability(pre1changecounter,i,state);
			}
			for( int i = 0; i < CASE_NUMBER; i++){
				predictnextstate[i] *= calcurateProbability(pre2changecounter,i,pre1state);
			}
			for( int i = 0; i < CASE_NUMBER; i++){
				predictnextstate[i] *= calcurateProbability(pre3changecounter,i,pre1state);
			}
			println("�ω���" + spotpricechange + "%");
			println("����" + predictnextstate[0] + "�ς��Ȃ�" + predictnextstate[1] + "�㏸" + predictnextstate[2]);
		}

		// ���݂̓��C���݂̐߁C���݂̌������i��\������D
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + "future=" + futurePrices[futurePrices.length -1]);
    // 1�ߑO�̌������i���擾����
    prevspotPrice = getLatestPrice(spotPrices);
    // 1�ߑO�̐敨���i���擾����
    prevfuturePrice = getLatestPrice(futurePrices);
    if (prevfuturePrice == -1) {
      prevfuturePrice = UOrderForm.INVALID_PRICE;
    }
    double tmp = spotpricechange;
    for (int i=0; i<3; i++){
    	prevspotpricechange[i] = tmp;
    	tmp = prevspotpricechange[i];
    }
    // �����敪���u�������Ȃ��v�ɐݒ肵�������[���쐬���ĕԂ��D
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
    forms[0].setBuySell(UOrderForm.NONE);
    return forms;
  }

	private double calcurateProbability(int[][] counter,int state, int prestate){
		int sum = 0;
		for (int i=0;i<CASE_NUMBER; i++){
			sum += counter[prestate][i];
		}
		if(sum == 0){
			return 1.0;
		}
		return (double)counter[prestate][state]/sum;
	}

	/**
	 * ���i�ω�������u�����v���u�ω��Ȃ��v���u�㏸�v���ꍇ��������
	 * @param pricechange
	 * @return �ꍇ
	 */
	private int determinChangeCase(double pricechange){
		int state = CASE_PRICE_NOT_CHANGE;
		if (pricechange < THRESHOLD_FALL){
			state = CASE_PRICE_FALL;
		}else if (pricechange < THRESHOLD_NOT_CHANGE){
			state = CASE_PRICE_NOT_CHANGE;
		}else{
			state = CASE_PRICE_RISE;
		}
		return state;
	}

	 /**
   * �������i�����肵�ĕԂ��܂��D
   * @param action �����敪
   * @param prices ���i�n��D�������Cprices[prices.length]�𒼋߂Ƃ��Ă��������D
   * @return �������i
   */
  private int determinePrice(int action, int[] prices) {
  	int price = UOrderForm.INVALID_PRICE;
  	int widthOfPrice = Math.abs(prices[prices.length - 1] - prices[prices.length - 2]);
  	if (action == UOrderForm.BUY) {
      price = prices[prices.length - 1] + widthOfPrice + (int)((double)widthOfPrice / 4.0 * getRandom().nextGaussian());
    } else if (action == UOrderForm.SELL) {
      price = prices[prices.length - 1] - widthOfPrice + (int)((double)widthOfPrice / 4.0 * getRandom().nextGaussian());
    }
  	if (price < 0) {
  		price = 1;
  	}
  	return price;
  }

  /**
   * �����敪��I��ŕԂ��܂��D
   * @param prices ���i�n��D�������Cprices[prices.length]�𒼋߂Ƃ��Ă��������D
   * @return �����敪
   */
  private int chooseAction(int[] prices) {
  	int action = UOrderForm.NONE;
  	double shortTermMovingAverage = calculateMovingAverage(prices, fShortTerm);
  	double mediumTermMovingAverage = calculateMovingAverage(prices, fMediumTerm);
  	if (fPreviousShortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& fPreviousMediumTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& shortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& mediumTermMovingAverage != UOrderForm.INVALID_PRICE) {
  		if (fPreviousShortTermMovingAverage < fPreviousMediumTermMovingAverage
  				&& mediumTermMovingAverage < shortTermMovingAverage) {
  			action = UOrderForm.BUY;
  		} else if (fPreviousMediumTermMovingAverage < fPreviousShortTermMovingAverage
  				        && shortTermMovingAverage < mediumTermMovingAverage) {
  			action = UOrderForm.SELL;
  		}
  	}
  	fPreviousShortTermMovingAverage = shortTermMovingAverage;
  	fPreviousMediumTermMovingAverage = mediumTermMovingAverage;
  	return action;
  }

  /**
   * ���߂���term�ߕ��̉��i�n��̈ړ����ϒl���v�Z���ĕԂ��܂��D
   * �������C���i���������Ă��Ȃ��ꍇ�́CUOrderForm.INVALID_PRICE (=-1)��Ԃ��܂��D
   * @param prices ���i�n��D�������Cprices[prices.length - 1]�𒼋߂Ƃ��Ă��������D
   * @param term �ړ����ς��Ƃ����
   * @return ���߂���term�ߕ��̉��i�n��̈ړ����ϒl
   */
  private double calculateMovingAverage(int[] prices, int term) {
  	double sum = 0.0;
  	for (int i = 0; i < term; ++i) {
  		if (prices[prices.length - 1 - i] < 0) {
  			return (double)UOrderForm.INVALID_PRICE;
  		}
  		sum += (double)prices[prices.length - 1 - i];
  	}
  	return sum / (double)term;
  }

  /**
   * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肵�܂��D
   * @param args �V�X�e���p�����[�^
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //���̃��\�b�h���I�[�o�[���C�h����K�v�͂���܂���D
  }
}
