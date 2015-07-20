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
import java.util.StringTokenizer;

/**
 * �Ǝ��G�[�W�F���g�p�̃e���v���[�g�ł��D ���̃G�[�W�F���g�̃R�s�[���쐬���āC���O��U<�w�Дԍ�>�ɏC�����ĉ������D
 */
public class U04_2 extends UAgent {

	/** �����ʂ̍ő�l�̃f�t�H���g�l */
	public static final int DEFAULT_MAX_QUANT = 100;

	/** �����ʂ̍ŏ��l�̃f�t�H���g�l */
	public static final int DEFAULT_MIN_QUANT = 30;

	/** �|�W�V�����̍ő�l�̃f�t�H���g�l */
	public static final int DEFAULT_MAX_POSITION = 350;

	/** ���i臒l�̃f�t�H���g�l */
	private static final int DEFAULT_STANDARD_THRESHOLD = 20;

	/** SpreadRatio�̃f�t�H���g�l */
	private static final double DEFAULT_SPREAD_RETIO_THRESHOLD = 0.01;

	/** ���������� */
	private Random fRandom;

	/** Spread�@Price */
	private int fSpreadPrice;

	/** ���i�̕��� */
	private double fMeanPrice;

	/** Sigma */
	private double fSigma;

	/** Spread�@Ratio */
	private double fSpreadRatio;

	/** �����ʂ̍ő�l */
	private int fMaxQuant = DEFAULT_MAX_QUANT;

	/** �����ʂ̍ŏ��l */
	private int fMinQuant = DEFAULT_MIN_QUANT;

	/** �|�W�V�����̍ő�l */
	private int fMaxPosition = DEFAULT_MAX_POSITION;

	/** ���i臒l�̃f�t�H���g�l */
	private int fStandardThreshold = DEFAULT_STANDARD_THRESHOLD;

	/** SpreadRatio�@臒l�̃f�t�H���g�l */
	private double fSpreadRatioThreshold = DEFAULT_SPREAD_RETIO_THRESHOLD;

	public static final String MIN_QUANT_KEY = "MinQuant";

	public static final String MAX_QUANT_KEY = "MaxQuant";

	public static final String MAX_POSITION_KEY = "MaxPosition";

	public static final String STANDARD_THRESHOLD_KEY = "StandardThreshold";

	public static final String SPREAD_RETIO_THRESHOLD_KEY = "SpreadRatioThreshold";

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
	public U04_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		// �����Ńt�B�[���h�ϐ��̒l�����������Ă��������D
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
		fRandom = getRandom();
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		forms[0].setBuySell(chooseAction(futurePrices, spotPrices));
		setMaxPosition(forms, position);
		forms[0].setPrice(determinePrice(spotPrices, futurePrices));
		forms[0].setQuantity((fMinQuant + fMaxQuant) / 2);
		println(" => " + forms[0].getBuySellByString() + ", price="
				+ forms[0].getPrice() + ", quantity=" + forms[0].getQuantity());
		return forms;
	}
	//�p�����[�^�̐ݒ�
	public void setParameters(String[] args) {
		super.setParameters(args);
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer st = new StringTokenizer(args[i], "= ");
			String key = st.nextToken();
			String value = st.nextToken();
			if (key.equals(U04_2.DEFAULT_MAX_POSITION)) {
				fMaxPosition = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fMaxPosition);
			} else if (key.equals(U04_2.DEFAULT_MAX_QUANT)) {
				fMaxQuant = Integer.parseInt(value);
				println("MediumTerm has been changed to " + fMaxQuant);
			} else if (key.equals(U04_2.DEFAULT_MIN_QUANT)) {
				fMinQuant = Integer.parseInt(value);
				println("MinQuant has been changed to " + fMinQuant);
			} else if (key.equals(U04_2.DEFAULT_SPREAD_RETIO_THRESHOLD)) {
				fSpreadRatioThreshold = Double.parseDouble(value);
				println("SpreadRatioThreshold has been changed to "
						+ fSpreadRatioThreshold);
			} else if (key.equals(U04_2.DEFAULT_STANDARD_THRESHOLD)) {
				fStandardThreshold = Integer.parseInt(value);
				println("StandardThreshold has been changed to " + fStandardThreshold);
			} else {
				println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
			}
		}
	}
	//�����敪�̌���
	private int chooseAction(int[] futurePrices, int[] spotPrices) {
		int action = UOrderForm.NONE;
		fSpreadPrice = spotPrices[spotPrices.length - 1]
				- futurePrices[futurePrices.length - 1];
		double pr = fRandom.nextDouble();
		if (fSpreadPrice > fStandardThreshold) {
			action = UOrderForm.BUY;
		} else if (fSpreadPrice < -fStandardThreshold) {
			action = UOrderForm.SELL;
		} else if (fSpreadPrice < fStandardThreshold
				&& fSpreadPrice > -fStandardThreshold) {
			fSpreadRatio = (futurePrices[futurePrices.length - 1] - spotPrices[spotPrices.length - 1])
					/ spotPrices[spotPrices.length - 1];
			if (fSpreadRatio <= -fSpreadRatioThreshold) {
				if (pr > fRandom.nextDouble()) {
					action = UOrderForm.BUY;
				} else {
					action = UOrderForm.NONE;
				}
			} else if (fSpreadRatio >= fSpreadRatioThreshold) {
				if (pr > fRandom.nextDouble()) {
					action = UOrderForm.SELL;
				} else {
					action = UOrderForm.NONE;
				}
			}
		}
		return action;
	}
	//�������i�̌���
	private int determinePrice(int[] spotPrices, int[] futurePrices) {
		int lastFuturePrice = futurePrices[futurePrices.length - 1];
		int lastSpotPrice = spotPrices[spotPrices.length - 1];
		if (lastFuturePrice <= 0) {
			lastFuturePrice = futurePrices[futurePrices.length - 2];
		}
		if (lastSpotPrice <= 0) {
			lastSpotPrice = spotPrices[spotPrices.length - 2];
		}
		fMeanPrice = (lastFuturePrice + lastSpotPrice) / 2.0;
		fSigma = ((Math.abs(lastFuturePrice - lastSpotPrice)) / 2.0);
		return (int) (fMeanPrice + fSigma * fRandom.nextGaussian());
	}

	// �|�W�V���������̏����B
	public UOrderForm[] setMaxPosition(UOrderForm[] forms, int pos) {
		if (forms[0].getBuySell() == 2) {
			if (pos > fMaxPosition) {
				forms[0].setBuySell(0);
			}
		} else if (forms[0].getBuySell() == 1) {
			if (pos < -fMaxPosition) {
				forms[0].setBuySell(0);
			}
		}
		return forms;
	}
}
