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
public class U15_2 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D

	public static final int DEFAULT_WAIT = 3; // �҂����Ԃ̃f�t�H���g�l�i1�`5���炢���ǂ������j

	/** �������ʂ̍ő�l�̃f�t�H���g�l */
	public static final int DEFAULT_MAX_QUANT = 50;

	/** �������ʂ̍ŏ��l�̃f�t�H���g�l */
	public static final int DEFAULT_MIN_QUANT = 10;

	/** ��/���|�W�V�����̍ő�l�̃f�t�H���g�l */
	public static final int DEFAULT_MAX_POSITION = 300;

	private int fMaxData = UOrderForm.INVALID_PRICE; // �ő�̒l�i

	private int fMinData = UOrderForm.INVALID_PRICE; // �ŏ��̒l�i

	private int FirstFlag = 1; // ����̃t���O

	private int fWaitTime = DEFAULT_WAIT; // �҂�����

	/** �������ʂ̍ő�l */
	private int fMaxQuant = DEFAULT_MAX_QUANT;

	/** �������ʂ̍ŏ��l */
	private int fMinQuant = DEFAULT_MIN_QUANT;

	/** ��/���|�W�V�����̍ő�l */
	private int fMaxPosition = DEFAULT_MAX_POSITION;

	/** �������ʂ̍ő�l�̃v���p�e�B��(MaxQuant) */
	public static final String MAX_QUANT_KEY = "MaxQuant";

	/** �������ʂ̍ŏ��l�̃v���p�e�B��(MinQuant) */
	public static final String MIN_QUANT_KEY = "MinQuant";

	/** ��/���|�W�V�����̍ő�l�̃v���p�e�B��(MaxPosition) */
	public static final String MAX_POSITION_KEY = "MaxPosition";

	/**
	 * �R���X�g���N�^�ł��D
	 * @param loginName ���O�C����
	 * @param passwd �p�X���[�h
	 * @param realName ����
	 * @param seed �����̎�
	 */
	public U15_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//�����Ńt�B�[���h�ϐ��̒l�����������Ă��������D

	}

	public int getMaxData() { // �ő�̒l�i��Ԃ�
		return fMaxData;
	}

	public int getMinData() { // �ŏ��̒l�i��Ԃ�
		return fMinData;
	}

	public int getWaitTime() { // �҂����Ԃ�Ԃ�
		return fWaitTime;
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

		Random rand = getRandom(); // �����_���g�p����
		UOrderForm[] forms = new UOrderForm[1]; // �t�H�[���쐬
		forms[0] = new UOrderForm(); // �t�H�[���쐬
		int[] prices = futurePrices; // �敨���i�n�������

		//futures[59]�����߂̒l�Aprices[prices.length - 1]�ɂ���΂悢�i-1�ɂ͒��Ӂj

		/////////////////////////////////////�f�o�b�O�p
//		println("");
//		for(int i=0; i < 10; i++){
//			println("prices[" + i + "]" + prices[i]);
//		}
		/////////////////////////////////////�f�o�b�O�p

		if (FirstFlag == 1) { // ����̂ݍő�E�ŏ��l�̐ݒ�
			fMaxData = prices[0];
			fMinData = prices[0]; // ���������l�����邪�AchooseAction�ŕЕ��ς��i�H�j
			FirstFlag = 0;
		}

		forms[0].setBuySell(chooseAction(prices)); // �A�N�V���������߂�
		println("");
		print("day=" + day + ", session=" + session
				+ ", futures=" + prices[prices.length - 1]
						+ ", maxdata=" + fMaxData
						+ ", mindata=" + fMinData
						+ ", waittime=" + fWaitTime); // ���A�Z�b�V�����A���߂̐敨���i�ȂǕ\��

		if (fWaitTime > 0) { // �A���Ŕ��蔃�����Ȃ��悤�ɑ҂����Ԃ�����
			fWaitTime--;
			forms[0].setBuySell(UOrderForm.NONE);
			return forms;
		}

		if (forms[0].getBuySell() == UOrderForm.NONE) { // �������Ȃ�
			return forms;
		}
		if (forms[0].getBuySell() == UOrderForm.BUY) { // ����
			if (position > fMaxPosition) {
				forms[0].setBuySell(UOrderForm.NONE);// �������Ȃ�
				return forms;
			}
		} else if (forms[0].getBuySell() == UOrderForm.SELL) { // ����
			if (position < -fMaxPosition) {
				forms[0].setBuySell(UOrderForm.NONE);	// �������Ȃ�
				return forms;
			}
		}

		forms[0].setPrice(determinePrice(forms[0].getBuySell(), prices));

		/////////////////////////////////////////////////////////�����ŃL�����Z�����鎞�̗v������
		if (forms[0].getPrice() == 1) { // 1�Ȃ�L�����Z��
			forms[0].setBuySell(UOrderForm.NONE);	// �������Ȃ�
			return forms;
		}
		/////////////////////////////////////////////////////////

		forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));

		fWaitTime = DEFAULT_WAIT; // �҂����Ԃ�߂�
		print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
				+ ", quantity=" + forms[0].getQuantity());
		return forms;

		//		// ���݂̓��C���݂̐߁C���݂̌������i��\������D
		//		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + ", futures=" + futurePrices[futurePrices.length -1]);
		//		println("spot size:" + spotPrices.length + ", futures size:" + futurePrices.length);
		//
		//		// �����敪���u�������Ȃ��v�ɐݒ肵�������[���쐬���ĕԂ��D
		//		UOrderForm[] forms = new UOrderForm[1];
		//		forms[0] = new UOrderForm();
		//    forms[0].setBuySell(UOrderForm.NONE);
		//    return forms;
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
			price = 1; // ���̂����ŃG���[�x�����o��
		}

		return price;
	}

	private int chooseAction(int[] prices) { // �ߋ��̍ő�l���傫���l�Ȃ甄��A�ŏ��l��菬�����l�Ȃ甃��
		int action = UOrderForm.NONE;

		if (fMaxData < prices[prices.length - 1] && prices[prices.length - 1] != -1) {
			action = UOrderForm.SELL; // ����I��
			fMaxData = prices[prices.length -1]; // �ő�l�X�V

		} else if (fMinData > prices[prices.length - 1] && prices[prices.length - 1] != -1) {
			action = UOrderForm.BUY; // �����I��
			fMinData = prices[prices.length -1]; // �ŏ��l�X�V
		}

		return action;
	}

	/**
	 * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肵�܂��D
	 * @param args �V�X�e���p�����[�^
	 */
	public void setParameters(String[] args) {
		super.setParameters(args);
		//���̃��\�b�h���I�[�o�[���C�h����K�v�͂���܂���D

		//    for (int i = 0; i < args.length; ++i) {
		//    	String[] strArray = args[i].split("=");
		//      String key = strArray[0];
		//      String value = strArray[1];
		//      if (key.equals(UMovingAverageAgent.MIN_QUANT_KEY)) {
		//        fMinQuant = Integer.parseInt(value);
		//        println("MinQuant has been changed to " + fMinQuant);
		//      } else if (key.equals(UMovingAverageAgent.MAX_QUANT_KEY)) {
		//        fMaxQuant = Integer.parseInt(value);
		//        println("MaxQuant has been changed to " + fMaxQuant);
		//      } else if (key.equals(UMovingAverageAgent.MAX_POSITION_KEY)) {
		//        fMaxPosition = Integer.parseInt(value);
		//        println("MaxPosition has been changed to " + fMaxPosition);
		//      } else {
		//      	println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
		//      }
		//    }

	}
}