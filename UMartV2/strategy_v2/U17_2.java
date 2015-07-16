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

import java.util.ArrayList;
import java.util.Random;

public class U17_2 extends UAgent {

	/** �������i�̕��̃f�t�H���g�l */
	public static final int DEFAULT_WIDTH_OF_PRICE = 20;

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
	// private static final int CASE_PRICE_LARGE_FALL = 0;
	/** 1�ߑO����̌��݂̐߉��i�̕ω����������̏ꍇ */
	private static final int CASE_PRICE_FALL = 1;

	/** 1�ߑO����̌��݂̐߉��i�̕ω��������ω��̏ꍇ */
	private static final int CASE_PRICE_NOT_CHANGE = 0;

	/** 1�ߑO����̌��݂̐߉��i�̕ω������㏸�̏ꍇ */
	private static final int CASE_PRICE_RISE = 2;

	/** 1�ߑO����̌��݂̐߉��i�̕ω������啝�����̏ꍇ */
	// private static final int CASE_PRICE_LARGE_RISE = 4;

	/** �p�ӂ����ꍇ�̐� */
	private static final int CASE_NUMBER = 3;

	/** �g�p����ߋ��̐ߐ� */
	private static final int TERM_NUMBER = 3;

	/** ���i�ω����啝�����̏ꍇ�̕ω����̍ŏ��l臒l */
	// private static final double THRESHOLD_LARGE_FALL = -3.0;
	/** ���i�ω��������̏ꍇ�̕ω����̍ŏ��l臒l */
	private static final double THRESHOLD_FALL = -0.1;

	/** ���i�ω������ω��̏ꍇ�̕ω����̍ŏ��l臒l */
	private static final double THRESHOLD_NOT_CHANGE = 0.1;

	/** ���i�ω����㏸�̏ꍇ�̕ω����̍ŏ��l臒l */
	// private static final double THRESHOLD_RISE = 3.0;

	/** �������i�̕� */
	private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE;

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
	private double[] prevspotpricechange = new double[CASE_NUMBER];

	/** 1�ߑO�ɂ�����敨���i�̕ω��� */
	private double[] prevfuturepricechange = new double[CASE_NUMBER];

	/** 1�ߑO�̉��i�̕ω����ƌ��݂̌������i�̕ω����̏ꍇ�̐����J�E���g */
	private int[][] pre1spotchangecounter = new int[TERM_NUMBER][TERM_NUMBER];

	/** 2�ߑO�̉��i�̕ω����ƌ��݂̌������i�̕ω����̏ꍇ�̐����J�E���g */
	private int[][] pre2spotchangecounter = new int[TERM_NUMBER][TERM_NUMBER];

	/** 3�ߑO�̉��i�̕ω����ƌ��݂̌������i�̕ω����̏ꍇ�̐����J�E���g */
	private int[][] pre3spotchangecounter = new int[TERM_NUMBER][TERM_NUMBER];

	/** 1�ߑO�̉��i�̕ω����ƌ��݂̌������i�̕ω����̏ꍇ�̐����J�E���g */
	private int[][] pre1futurechangecounter = new int[TERM_NUMBER][TERM_NUMBER];

	/** 2�ߑO�̉��i�̕ω����ƌ��݂̌������i�̕ω����̏ꍇ�̐����J�E���g */
	private int[][] pre2futurechangecounter = new int[TERM_NUMBER][TERM_NUMBER];

	/** 3�ߑO�̉��i�̕ω����ƌ��݂̌������i�̕ω����̏ꍇ�̐����J�E���g */
	private int[][] pre3futurechangecounter = new int[TERM_NUMBER][TERM_NUMBER];

	/**
	 * �R���X�g���N�^�ł��D
	 * 
	 * @param loginName
	 *          ���O�C����
	 * @param passwd
	 *          �p�X���[�h
	 * @param realName
	 *          ����
	 * @param seed
	 *          �����̎�
	 */
	public U17_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		for (int i = 0; i < CASE_NUMBER; i++) {
			for (int j = 0; j < CASE_NUMBER; j++) {
				pre1spotchangecounter[i][j] = 0;
				pre2spotchangecounter[i][j] = 0;
				pre3spotchangecounter[i][j] = 0;
				pre1futurechangecounter[i][j] = 0;
				pre2futurechangecounter[i][j] = 0;
				pre3futurechangecounter[i][j] = 0;
			}
		}
		for (int i = 0; i < TERM_NUMBER; i++) {
			prevspotpricechange[i] = 0;
			prevfuturepricechange[i] = 0;
		}
	}

	/**
	 * �Z���̐ߐ���Ԃ��܂��D
	 * 
	 * @return �Z���̐ߐ�
	 */
	public int getShortTerm() {
		return fShortTerm;
	}

	/**
	 * �����̐ߐ���Ԃ��܂��D
	 * 
	 * @return �����̐ߐ�
	 */
	public int getMediumTerm() {
		return fMediumTerm;
	}

	/**
	 * �ŏ��������ʂ�Ԃ��܂��D
	 * 
	 * @return �ŏ���������
	 */
	public int getMinQuant() {
		return fMinQuant;
	}

	/**
	 * �ő咍�����ʂ�Ԃ��܂��D
	 * 
	 * @return �ő咍������
	 */
	public int getMaxQuant() {
		return fMaxQuant;
	}

	/**
	 * �ő�|�W�V������Ԃ��܂��D
	 * 
	 * @return �ő�|�W�V����
	 */
	public int getMaxPosition() {
		return fMaxPosition;
	}

	/**
	 * �s�ꉿ�i������̂Ƃ��̒������i��Ԃ��܂��D
	 * 
	 * @return �s�ꉿ�i������̂Ƃ��̒������i
	 */
	public int getNominalPrice() {
		return fNominalPrice;
	}

	/**
	 * �����[���쐬���܂��D
	 * 
	 * @param day
	 *          ��
	 * @param session
	 *          ��
	 * @param maxDays
	 *          �������
	 * @param noOfSessionsPerDay
	 *          1���̐ߐ�
	 * @param spotPrices
	 *          �������i�n��DspotPrices[0]����spotPrices[119]�܂ł�120�ߕ��̃f�[�^���i�[����Ă���D
	 *          spotPrices[119]�����߂̉��i�ł��D�������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��Ă��������D
	 * @param futurePrices
	 *          �敨���i�n��DfuturePrices[0]����futurePrices[59]�܂ł�60�ߕ��̃f�[�^���i�[����Ă��܂��D
	 *          futurePrices[59]�����߂̉��i�ł��D�������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��Ă��������D�܂��C
	 *          ����J�n�߂��O�͌������i���i�[����Ă��܂��D
	 * @param position
	 *          �|�W�V�����D���Ȃ�Δ����z��(�����O�E�|�W�V����)�C���Ȃ�Δ���z���i�V���[�g�E�|�W�V�����j��\���܂��D
	 * @param money
	 *          �����c���D�^��long�ł��邱�Ƃɒ��ӂ��Ă��������D
	 * @return UOrderForm[] �����[�̔z��
	 */
	public UOrderForm[] makeOrderForms(int day, int session, int maxDays,
			int noOfSessionsPerDay, int[] spotPrices, int[] futurePrices,
			int position, long money) {
		Random rand = getRandom();
		// �����[���쐬����D
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		int buysell = UOrderForm.NONE;
		int movingaveragebuysell = chooseAction(futurePrices);
		// �������i�̑��������v�Z����D(�l��-1�ł����1�ߑO�̒l������)
		int spotPrice = getLatestPrice(spotPrices);
		if (spotPrice == UOrderForm.INVALID_PRICE)
			spotPrice = prevspotPrice;
		double spotpricechange = (spotPrice - prevspotPrice) * 100.0
				/ prevspotPrice;
		// �敨���i�̑��������v�Z����D(�l��-1�ł����1�ߑO�̒l������)
		int futurePrice = getLatestPrice(futurePrices);
		if (futurePrice == UOrderForm.INVALID_PRICE)
			futurePrice = prevspotPrice;
		double futurepricechange = (futurePrice - prevfuturePrice) * 100.0
				/ prevfuturePrice;
		// 5�߈ȍ~�ł���Εω����̃J�E���g���J�n(����J�n���͕ϓ����傫�����ߎ�菜��)
		if ((day - 1) * noOfSessionsPerDay + session > 4) {
			// ����,1�ߑO,2�ߑO,3�ߑO�̑��������v�Z���A1~3�ߑO�ƌ��݂̑������̕ω��̃y�A���J�E���g
			int spotstate = determinChangeCase(spotpricechange);
			int pre1spotstate = determinChangeCase(prevspotpricechange[0]);
			int pre2spotstate = determinChangeCase(prevspotpricechange[1]);
			int pre3spotstate = determinChangeCase(prevspotpricechange[2]);
			pre1spotchangecounter[pre1spotstate][spotstate] += 1;
			pre2spotchangecounter[pre2spotstate][spotstate] += 1;
			pre3spotchangecounter[pre3spotstate][spotstate] += 1;
			int futurestate = determinChangeCase(futurepricechange);
			int pre1futurestate = determinChangeCase(prevfuturepricechange[0]);
			int pre2futurestate = determinChangeCase(prevfuturepricechange[1]);
			int pre3futurestate = determinChangeCase(prevfuturepricechange[2]);
			pre1futurechangecounter[pre1futurestate][futurestate] += 1;
			pre2futurechangecounter[pre2futurestate][futurestate] += 1;
			pre3futurechangecounter[pre3futurestate][futurestate] += 1;
			println("�����敪" + spotstate);
			println("�敨�敪" + futurestate);
			// 2�ߑO���猻�݂܂ł̕ω��̏ꍇ������p���Č������i�̎��̕ω���\�z
			double[] nextspotstate = { 1.0, 1.0, 1.0 };
			for (int i = 0; i < CASE_NUMBER; i++) {
				nextspotstate[i] *= calcurateProbability(pre1spotchangecounter, i,
						spotstate);
			}
			for (int i = 0; i < CASE_NUMBER; i++) {
				nextspotstate[i] *= calcurateProbability(pre2spotchangecounter, i,
						pre1spotstate);
			}
			for (int i = 0; i < CASE_NUMBER; i++) {
				nextspotstate[i] *= calcurateProbability(pre3spotchangecounter, i,
						pre2spotstate);
			}
			// 2�ߑO���猻�݂܂ł̕ω��̏ꍇ������p���Đ敨���i�̎��̕ω���\�z
			double[] nextfuturestate = { 1.0, 1.0, 1.0 };
			for (int i = 0; i < CASE_NUMBER; i++) {
				nextfuturestate[i] *= calcurateProbability(pre1futurechangecounter, i,
						futurestate);
			}
			for (int i = 0; i < CASE_NUMBER; i++) {
				nextfuturestate[i] *= calcurateProbability(pre2futurechangecounter, i,
						pre1futurestate);
			}
			for (int i = 0; i < CASE_NUMBER; i++) {
				nextfuturestate[i] *= calcurateProbability(pre3futurechangecounter, i,
						pre2futurestate);
			}
			println("�������i�ω���" + spotpricechange + "%");
			println("�ς��Ȃ�" + nextspotstate[CASE_PRICE_NOT_CHANGE] + "����"
					+ nextspotstate[CASE_PRICE_FALL] + "�㏸"
					+ nextspotstate[CASE_PRICE_RISE]);
			println("�敨���i�ω���" + futurepricechange + "%");
			println("�ς��Ȃ�" + nextfuturestate[CASE_PRICE_NOT_CHANGE] + "����"
					+ nextfuturestate[CASE_PRICE_FALL] + "�㏸"
					+ nextfuturestate[CASE_PRICE_RISE]);
			buysell = determineAction(nextspotstate, nextfuturestate);
			println("�����敪" + buysell);
		}
		// 1�ߑO�̌������i��ۑ�
		prevspotPrice = spotPrice;
		// 1�ߑO�̐敨���i��ۑ�
		prevfuturePrice = futurePrice;

		// �@�������i��3�ߑO�܂ł̑��������X�V����D
		double tmp = spotpricechange;
		for (int i = 0; i < TERM_NUMBER; i++) {
			prevspotpricechange[i] = tmp;
			tmp = prevspotpricechange[i];
		}
		// �@�敨���i��3�ߑO�܂ł̑��������X�V����D
		tmp = futurepricechange;
		for (int i = 0; i < TERM_NUMBER; i++) {
			prevfuturepricechange[i] = tmp;
			tmp = prevfuturepricechange[i];
		}
		// ���݂̓��C���݂̐߁C���݂̌������i��\������D
		println("day=" + day + ", session=" + session + ", spot="
				+ spotPrices[spotPrices.length - 1] + "future="
				+ futurePrices[futurePrices.length - 1]);
		// �����敪�̍ŏI���������D
		if (movingaveragebuysell != UOrderForm.NONE) {
			forms[0].setBuySell(movingaveragebuysell);
		} else {
			forms[0].setBuySell(buysell);
		}
		// ���݂̃|�W�V�����ƍő�|�W�V�������r����
		if (forms[0].getBuySell() == UOrderForm.NONE) {
			return forms;
		}
		if (forms[0].getBuySell() == UOrderForm.BUY) {
			if (position > fMaxPosition) {
				forms[0].setBuySell(UOrderForm.NONE);
				return forms;
			}
		} else if (forms[0].getBuySell() == UOrderForm.SELL) {
			if (position < -fMaxPosition) {
				forms[0].setBuySell(UOrderForm.NONE);
				return forms;
			}
		}
		// ���i�Ɛ��ʂ����肷��
		int latestPrice = getLatestPrice(futurePrices);
		if (latestPrice == UOrderForm.INVALID_PRICE) {
			latestPrice = getLatestPrice(spotPrices);
		}
		if (latestPrice == UOrderForm.INVALID_PRICE) {
			latestPrice = fNominalPrice;
		}
		forms[0].setPrice(determinePrice(forms[0].getBuySell(), futurePrices));
		forms[0]
				.setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));

		println(" => " + forms[0].getBuySellByString() + ", price="
				+ forms[0].getPrice() + ", quantity=" + forms[0].getQuantity());
		println("");

		return forms;
	}

	/**
	 * ���̒����敪���m�����f������Ɍ��肷��
	 * 
	 * @param spricechange
	 * @param fpricechange
	 * @return �����敪
	 */
	private int determineAction(double[] spricechange, double[] fpricechange) {
		// �m���̍ő�l����������D
		double fmax = 0.0;
		double smax = 0.0;
		for (int i = 0; i < CASE_NUMBER; i++) {
			fmax = Math.max(fmax, fpricechange[i]);
			smax = Math.max(smax, spricechange[i]);
		}
		int fmaxcount = 0;
		int smaxcount = 0;
		for (int i = 0; i < CASE_NUMBER; i++) {
			if (fpricechange[i] == fmax)
				fmaxcount++;
			if (spricechange[i] == smax)
				smaxcount++;
		}
		if (fmaxcount == 1) {
			for (int i = 0; i < CASE_NUMBER; i++) {
				if (fpricechange[i] == fmax)
					return i;
			}
		} else if (smaxcount == 1) {
			for (int i = 0; i < CASE_NUMBER; i++) {
				if (spricechange[i] == smax)
					return i;
			}
		}
		/* ���X�g�Ōv�Z������@
		 * ArrayList<Integer> fmaxList = new ArrayList<Integer>();
		 * ArrayList<Integer> smaxList = new ArrayList<Integer>(); for(int
		 * i=0;i<CASE_NUMBER; i++){ if(fpricechange[i] == fmax){ fmaxList.add(i); }
		 * if(spricechange[i] == smax){ smaxList.add(i); } }
		 * //�敨���i�̑������\�z����ԍ������̂𒍕��敪�Ƃ���D if(smaxList.size() == 1){ return (Integer)
		 * smaxList.get(0); //�敨���i�̑������\�z����ԍ������̂������̏ꍇ }else if(smaxList.size() >=
		 * 2){ //�������i�̑������\�z����ԍ������̂𒍕��敪�Ƃ���D if(fmaxList.size() == 1){ return
		 * (Integer) fmaxList.get(0); } }
		 */
		return UOrderForm.NONE;
	}

	/**
	 * �O�̐�(prestate)�̑���������Ɍ��݂̑�����(state)�̊m�����v�Z
	 * 
	 * @param counter
	 * @param state
	 * @param prestate
	 * @return �m��
	 */
	private double calcurateProbability(int[][] counter, int state, int prestate) {
		int sum = 0;
		for (int i = 0; i < CASE_NUMBER; i++) {
			sum += counter[prestate][i];
		}
		if (sum == 0) {
			return 1.0;
		}
		return (double) counter[prestate][state] / sum;
	}

	/**
	 * ���i�ω�������u�����v���u�ω��Ȃ��v���u�㏸�v���ꍇ��������
	 * 
	 * @param pricechange
	 * @return �ꍇ
	 */
	private int determinChangeCase(double pricechange) {
		if (pricechange < THRESHOLD_FALL) {
			return CASE_PRICE_FALL;
		} else if (pricechange < THRESHOLD_NOT_CHANGE) {
			return CASE_PRICE_NOT_CHANGE;
		} else {
			return CASE_PRICE_RISE;
		}
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
	 * 
	 * @param prices
	 *          ���i�n��D�������Cprices[prices.length]�𒼋߂Ƃ��Ă��������D
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
	 * ���߂���term�ߕ��̉��i�n��̈ړ����ϒl���v�Z���ĕԂ��܂��D �������C���i���������Ă��Ȃ��ꍇ�́CUOrderForm.INVALID_PRICE
	 * (=-1)��Ԃ��܂��D
	 * 
	 * @param prices
	 *          ���i�n��D�������Cprices[prices.length - 1]�𒼋߂Ƃ��Ă��������D
	 * @param term
	 *          �ړ����ς��Ƃ����
	 * @return ���߂���term�ߕ��̉��i�n��̈ړ����ϒl
	 */
	private double calculateMovingAverage(int[] prices, int term) {
		double sum = 0.0;
		for (int i = 0; i < term; ++i) {
			if (prices[prices.length - 1 - i] < 0) {
				return (double) UOrderForm.INVALID_PRICE;
			}
			sum += (double) prices[prices.length - 1 - i];
		}
		return sum / (double) term;
	}

	/**
	 * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肵�܂��D
	 * 
	 * @param args
	 *          �V�X�e���p�����[�^
	 */
	public void setParameters(String[] args) {
		super.setParameters(args);
		// ���̃��\�b�h���I�[�o�[���C�h����K�v�͂���܂���D
	}
}