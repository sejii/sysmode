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
public class U14_2 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D

	/** ��/���|�W�V�����̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** ����Ԏ��Ԃ̃f�t�H���g�l(�ߐ�) */
  public static final int DEFAULT_TRADE_INTERVAL = 2;

  /** �g�p���鎑���̊����̃f�t�H���g�l */
  public static final double DEFAULT_BUDGET_PERCENTAGE = 0.0001;

  /** �J�n����̎����̃f�t�H���g�l */
  public static final int DEFAULT_BUDGET = 1000000000;

  /** �O��̎�����̋��z�̃f�t�H���g�l */
  public static final int DEFAULT_BEFORE_VALUE = 0;

  /** �������E�����̃f�t�H���g�l */
  public static final double DEFAULT_LOSS_LIMIT_PERCENTAGE = 0.2;

  /** �s�ꉿ�i������̎��̒������i�̃f�t�H���g�l */
  public static final int DEFAULT_NOMINAL_PRICE = 3000;

	public static final int SESSIONS_PER_DAY = 8;

  /** ��/���|�W�V�����̍ő�l */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** ����Ԏ��Ԃ̃f�t�H���g�l(�ߐ�) */
  private int fTradeInterval = DEFAULT_TRADE_INTERVAL;

  /** �g�p���鎑���̊����̃f�t�H���g�l */
  private double fBudgetPercentage = 0.001;

  /** �J�n����̎����̃f�t�H���g�l */
  private int fBudget = 1000000000;

  /** �O��̎�����̋��z�̃f�t�H���g�l */
  private int fBeforeValue = UOrderForm.INVALID_PRICE;

  /** �������E�����̃f�t�H���g�l */
  private double fLossLimitPercentage = 0.2;

  /** �s�ꉿ�i������̎��̒������i�̃f�t�H���g�l */
  private int fNominalPrice = 3000;

  /** �O��̎�����s������ */
	private int fBeforeSession = 0;


	//�V�X�e���p�����[�^��
  /** ����Ԏ��Ԃ̃v���p�e�B�� */
  public static final String TRADE_INTERVAL_KEY = "TradeInterval";

  /** �g�p���鎑���̊����̃v���p�e�B�� */
  public static final String BUDGET_PERCENTAGE_KEY = "BudgetPercentage";

  /** �J�n����̎����̃v���p�e�B�� */
  public static final String BUDGET_KEY = "Budget";

  /** �O��̎�����̋��z�̃v���p�e�B�� */
  public static final String BEFORE_VALUE_KEY = "BeforeValue";

  /** �������E�����̃v���p�e�B�� */
  public static final String LOSS_LIMIT_PERCENTAGE_KEY = "LossLimitPercentage";

  /** �s�ꉿ�i������̎��̒������i�̃v���p�e�B�� */
  public static final String NOMINAL_PRICE_KEY = "NominalPrice";

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U14_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

  //getter
	public int getfTradeInterval() {
		return fTradeInterval;
	}

	public double getfBudgetPercentage() {
		return fBudgetPercentage;
	}

	public int getfBudget() {
		return fBudget;
	}

	public int getfBeforeValue() {
		return fBeforeValue;
	}

	public double getfLossLimitPercentage() {
		return fLossLimitPercentage;
	}

	public int getfNominalPrice() {
		return fNominalPrice;
	}


	//�����敪���쐬���ĕԂ����\�b�h
	//����prices�͐敨���i�n��
	public int chooseAction(int[] prices, int day, int session){
		Random rand = getRandom();
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();

		//�ŏ��̈��̎���ł͒����敪�������_���Ɍ���
		if(day == 1 && session == 1){
			forms[0].setBuySell(rand.nextInt(2) + 1);	//rand.nextInt(2) + 1�F1(BUY)��2(SELL)��Ԃ��B
			fBeforeSession = session;			//fBeforeSession���X�V
		}else{	//����ȊO�̎���̏ꍇ
//			println("(fBeforeSession + SESSIONS_PER_DAY * (day - 1))=" + (fBeforeSession + SESSIONS_PER_DAY * (day - 1)) +
//					", (session + SESSIONS_PER_DAY * (day - 1))=" + (session + SESSIONS_PER_DAY * (day - 1)) +
//					", fTradeInterval=" + fTradeInterval);
			println("(session + SESSIONS_PER_DAY * (day - 1))=" + (session + SESSIONS_PER_DAY * (day - 1)) + "fBeforeSession" + fBeforeSession);
			if((session + SESSIONS_PER_DAY * (day - 1)) - fBeforeSession == fTradeInterval){	//�O��̎��������fTradeInterval�߂������Ԃ��o���Ă�����(�ߐ��������ׂ��ł����Z�b�g����Ȃ��悤�ɒ���)
				println("�����敪��ݒ�");
				if(fBeforeValue >= prices[prices.length - 1]){	//���݂̌������i���O��̎�����i��荂��������
					forms[0].setBuySell(UOrderForm.BUY);
				}else if(fBeforeValue < prices[prices.length - 1]){	//���݂̌������i���O��̎�����i������������
					forms[0].setBuySell(UOrderForm.SELL);
				}else{	//���݂̌������i���O��̎�����i�Ɠ��z��������
					forms[0].setBuySell(UOrderForm.NONE);
				}
				fBeforeValue = prices[prices.length - 1];	//fBeforeValue, fBeforeSession�̍X�V
				fBeforeSession = (session + SESSIONS_PER_DAY * (day - 1));	//�ߐ��������ׂ��ł����Z�b�g����Ȃ��悤�ɒ���
			}else{	//�O��̎�������玞�Ԃ��o���Ă��Ȃ��Ȃ�
				forms[0].setBuySell(UOrderForm.NONE);
			}
		}
		return forms[0].getBuySell();	//���߂������敪��return
	}

	//�������i������
	public int determinePrice(int action, int[] prices){
		int price = 1;	//�������i
		//���߂̐敨���i�Fprices[prices.length -1]�B��ߑO�̐敨���i�Fprices[prices.length -2]
		/* �������i�́A���߂̌������i��p����B���߂̌������i�����݂��Ȃ��ꍇ�A�s�ꉿ�i������̎��̒������i��p������̂Ƃ���B */
		if(prices[prices.length - 1] != UOrderForm.INVALID_PRICE){
			price = prices[prices.length - 1];
		}else{
			price = fNominalPrice;		//�s�ꉿ�i������̎��̒������i
		}
		return price;
	}

	//�������ʂ�����
	//�������ʂ́A���炩���ߗ^����ꂽ�g�p���z�̊����Ԃ�̋��z�Ŕ������Ƃ̂ł���ő�̐��Ƃ���B
	public int determineQuantity(int action, int price){
		int quantity = 10;	//��������
		int tradeValue = (int)(fBudget * fBudgetPercentage);	//����̎���Ŏg�����z
		quantity = tradeValue / price;
		return quantity;
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
//		// �����敪���u�������Ȃ��v�ɐݒ肵�������[���쐬���ĕԂ��D
//		UOrderForm[] forms = new UOrderForm[1];
//		forms[0] = new UOrderForm();
//    forms[0].setBuySell(UOrderForm.NONE);
//    return forms;
//		// ���݂̓��C���݂̐߁C���݂̌������i��\������D
//		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]
//				+ ", futures=" + futuresPrices[futuresPrices.length - 1]);

		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();

		/* �����敪�̌��� */
		forms[0].setBuySell(chooseAction(futuresPrices, day, session));	//�敨���i�n�񂩂璍���敪������

		if(forms[0].getBuySell() == UOrderForm.BUY){
			if(position > fMaxPosition){			//�u�����v�Ń����O�ōő�|�W�V�����𒴂���-->�����I�ɒ����敪��NONE��
				forms[0].setBuySell(UOrderForm.NONE);
				return forms;
			}
		}else if(forms[0].getBuySell() == UOrderForm.SELL){
			if(position < -fMaxPosition){			//�u����v�ŃV���[�g�ōő�|�W�V�����𒴂���-->�����I�ɒ����敪��NONE��
				forms[0].setBuySell(UOrderForm.NONE);
				return forms;
			}
		}
		/* �������i�̌��� */
		forms[0].setPrice(determinePrice(forms[0].getBuySell(), futuresPrices));

		/* �������ʂ̌��� */
		forms[0].setQuantity(determineQuantity(forms[0].getBuySell(), forms[0].getPrice()));

		println("day=" + day + ", session=" + session + ", latestPrice=" + getLatestPrice(futuresPrices)
				+ ", " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
				+ ", quantity=" + forms[0].getQuantity());
		return forms;	//�쐬���������[��Ԃ�
  }

  /**
   * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肵�܂��D
   * @param args �V�X�e���p�����[�^
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //���̃��\�b�h���I�[�o�[���C�h����K�v�͂���܂���D
		for(int i = 0; i < args.length; ++i){
			String[] strArray = args[i].split("=");	// =�������L��
			String key = strArray[0];	//�p�����[�^��(key)�ƒl(value)�̃g�[�N���ɕ������đ��
			String value = strArray[1];
			//�p�����[�^���ɂ���������value��int�^�ɕϊ����đΉ�����ϐ��֑��
			if(key.equals(U14_2.TRADE_INTERVAL_KEY)){	//�������r�F==�s�BString�N���X��equals���\�b�h�g�p
				fTradeInterval = Integer.parseInt(value);
				println("ShortTerm has been changed to " + fTradeInterval);
			}else if(key.equals(U14_2.BUDGET_PERCENTAGE_KEY)){	//�������r�F==�s�BString�N���X��equals���\�b�h�g�p
				fBudgetPercentage = Integer.parseInt(value);
				println("MediumTerm has been changed to " + fBudgetPercentage);
			}else if(key.equals(U14_2.BUDGET_KEY)){
				fBudget = Integer.parseInt(value);
				println("MinQuant has been changed to " + fBudget);
			}else if(key.equals(U14_2.BEFORE_VALUE_KEY)){
				fBeforeValue = Integer.parseInt(value);
				println("MaxQuant has been changed to " + fBeforeValue);
			}else if(key.equals(U14_2.LOSS_LIMIT_PERCENTAGE_KEY)){
				fLossLimitPercentage = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fLossLimitPercentage);
			}else if(key.equals(U14_2.NOMINAL_PRICE_KEY)){
				fNominalPrice = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fNominalPrice);
			}else{
				println("Unknown parameter:" + key + "in U14_2.setParameters");
			}
		}
  }
}
