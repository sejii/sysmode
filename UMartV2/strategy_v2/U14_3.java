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
 * memo sasaki@ic.dis.titech.ac.jp
 */
public class U14_3 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D
	private int fShortTerm;
	private int fMediumTerm;
	private int fMaxPosition;

	public static final int DEFAULT_SHORT_TERM=8;
	public static final int DEFAULT_MEDIUM_TERM=16;
	public static final int DEFAULT_MIN_QUANT =10;
	public static final int DEFAULT_MAX_QUANT=50;
	public static final int DEFAULT_MAX_POSITION=300;
	public static final int DEFAULT_RSI_TERM = 8;
	private int fRSITerm=DEFAULT_RSI_TERM;
	 /** �������ʂ̍ő�l */
  private int fMaxQuant = DEFAULT_MAX_QUANT;
  /** �������ʂ̍ŏ��l */
  private int fMinQuant = DEFAULT_MIN_QUANT;
	public static final String SHORT_TERM_KEY="ShortTerm";
	public static final String MIN_MIDIUM_TERM_KEY ="MediumTerm";
	public static final String MIN_QUANT_KEY="MinQuant";
	public static final String MAX_QUANT_KEY="MaxQuant";
	public static final String MAX_POSITION_KEY="MaxPosition";
  public static final String RSI_TERM_KEY = "RSITerm";

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U14_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//�����Ńt�B�[���h�ϐ��̒l�����������Ă��������D

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
	 * RSI�̐ߐ���Ԃ��܂��D
	 * @return �ő�|�W�V����
	 */
	public int getRSITerm() {
		return fRSITerm;
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

		// ���݂̓��C���݂̐߁C���݂̌������i��\������D
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]+", futuresPrice"+futurePrices[futurePrices.length-1]);
		println("spot size:" +spotPrices.length+", futures size:"+futurePrices.length);

		// �����敪���u�������Ȃ��v�ɐݒ肵�������[���쐬���ĕԂ��D
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		Random rand = getRandom();
		int[] prices =futurePrices;
		int randomnum = rand.nextInt(2);
		if(randomnum==0)
		forms[0].setBuySell(chooseAction(prices));
		if(randomnum==1)
		forms[0].setBuySell(chooseAction1(prices));


		if(day==1){ //1���ڂ͉������Ȃ�
			forms[0].setBuySell(UOrderForm.NONE);
		}

    println("");
    print("day=" + day + ", session=" + session
          + ", futures=" + prices[prices.length - 1]
          + ", spot="+spotPrices[spotPrices.length-1]);

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

    //�������i��2�p�^�[��
    randomnum = rand.nextInt(2);
    //1�ߑO�̌������i
    if(randomnum==0)
    	forms[0].setPrice(spotPrices[spotPrices.length-2]);
    //determinePrice���\�b�h���g�p
    if(randomnum==0)
    	forms[0].setPrice(determinePrice(forms[0].getBuySell(), prices));

    print("fMax"+fMaxQuant+"  fMin"+fMinQuant);
    forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));
    print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
           + ", quantity=" + forms[0].getQuantity());

    return forms;
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


//   RSI=upSum/(upSum+downSum) upSum�͏㏸�����Ƃ��̉��i�ϓ��̍��v�A
//  downSum�͉��~�����Ƃ��̉��i�ϓ��̍��v
//
  private double calculateRSI(int[] prices, int term) {
  	double RSI_up = 0.0;
  	double RSI_down = 0.0;
  	for (int i = 0; i < term; ++i) {
  		if (prices[prices.length - 1 - i] < 0) {
  			return (double)UOrderForm.INVALID_PRICE;
  		}
  		else if (prices[prices.length - 1 - i] - prices[prices.length - 1 - term] > 0){
  			RSI_up += prices[prices.length - 1 - i] - prices[prices.length - 1 - term -1];
  		}
  		else if (prices[prices.length - 1 - i] - prices[prices.length - 1 - term] < 0){
  			RSI_down += Math.abs(prices[prices.length - 1 - i] - prices[prices.length - 1 - term -1]);
  		}
  	}
  	return RSI_up / (RSI_up + RSI_down);
  }
//  RSI������l����������Δ��蒍���ARSI�������l�����Ⴏ��Δ�������
  private int chooseAction(int[] prices) {
  	int action = UOrderForm.NONE;
  	double RSI = calculateRSI(prices, fRSITerm);
  	if (RSI < 0.15) {
  			action = UOrderForm.BUY;
  		} else if (RSI > 0.85) {
  			action = UOrderForm.SELL;
  		}
  	return action;
  }

  public int chooseAction1(int[] prices){
		int action = UOrderForm.NONE;
		if(prices[prices.length - 3] > prices[prices.length - 2]
				&& prices[prices.length - 2] < prices[prices.length - 1]){
			action = UOrderForm.BUY;
		}else if (prices[prices.length - 3] < prices[prices.length - 2]
				&& prices[prices.length - 2] > prices[prices.length - 1]){
			action = UOrderForm.SELL;
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
    for (int i = 0; i < args.length; ++i) {
    	String[] strArray = args[i].split("=");
      String key = strArray[0];
      String value = strArray[1];
      if (key.equals(U14_3.SHORT_TERM_KEY)) {
        fShortTerm = Integer.parseInt(value);
        println("ShortTerm has been changed to " + fShortTerm);
      } else if (key.equals(U14_3.MIN_QUANT_KEY)) {
        fMinQuant = Integer.parseInt(value);
        println("MinQuant has been changed to " + fMinQuant);
      } else if (key.equals(U14_3.MAX_QUANT_KEY)) {
        fMaxQuant = Integer.parseInt(value);
        println("MaxQuant has been changed to " + fMaxQuant);
      } else if (key.equals(U14_3.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else if (key.equals(U14_3.RSI_TERM_KEY)) {
        fRSITerm = Integer.parseInt(value);
        println("RSITerm has been changed to " + fRSITerm);
      } else {
      	println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
      }
    }
  }
 }
