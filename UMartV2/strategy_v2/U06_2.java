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
 * �敨���i��p���郉���_���G�[�W�F���g�N���X�ł��D
 */
public class U06_2 extends UAgent {

  /** �������i�̕��̃f�t�H���g�l */
  public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** �������ʂ̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** �������ʂ̍ŏ��l�̃f�t�H���g�l */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** �ŏ���5���Ԃ̒����� */
  public static final int QUANT_IN_EARLY_TERM = 40;

  /** ��/���|�W�V�����̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i�̃f�t�H���g�l */
  public static final int DEFAULT_NOMINAL_PRICE = 3000;

  /** �������i�̕� */
  private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE;

  /** �������ʂ̍ő�l */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** �������ʂ̍ŏ��l */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** ��/���|�W�V�����̍ő�l */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

	/** �������i�̃v���p�e�B��(WidthOfPrice) */
	public static final String WIDTH_OF_PRICE_KEY = "WidthOfPrice";

	/** �������ʂ̍ő�l�̃v���p�e�B��(MaxQuant) */
	public static final String MAX_QUANT_KEY = "MaxQuant";

	/** �������ʂ̍ŏ��l�̃v���p�e�B��(MinQuant) */
	public static final String MIN_QUANT_KEY = "MinQuant";

	/** ��/���|�W�V�����̍ő�l�̃v���p�e�B��(MaxPosition) */
	public static final String MAX_POSITION_KEY = "MaxPosition";

	/** ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i�̃v���p�e�B��(NominalPrice) */
	public static final String NOMINAL_PRICE_KEY = "NominalPrice";

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U06_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	/**
	 * ���i����Ԃ��܂��D
	 * @return ���i��
	 */
	public int getWidthOfPrice() {
		return fWidthOfPrice;
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
   * �f�t�H���g�ł́u�������Ȃ��v�����[��Ԃ������Ȃ̂ŁC�q�N���X�ŕK���I�[�o�[���C�h���Ă��������D
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

    if (day < 5) {
      UOrderForm[] forms = new UOrderForm[1];
      forms[0] = new UOrderForm();
      forms[0].setBuySell(UOrderForm.BUY);
      int latestPrice = getLatestPrice(spotPrices);
      int price = latestPrice + 1;
      forms[0].setPrice(price);
      forms[0].setQuantity(QUANT_IN_EARLY_TERM);
      println("day=" + day + ", session=" + session + ", latestPrice="  + latestPrice
              + ", " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
              + ", quantity=" + forms[0].getQuantity());
      return forms;
    }

  	Random rand = getRandom();
  	UOrderForm[] forms = new UOrderForm[1];
  	forms[0] = new UOrderForm();
    forms[0].setBuySell(rand.nextInt(2) + 1);
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
    int latestPrice = getLatestPrice(futurePrices);
    if (latestPrice == UOrderForm.INVALID_PRICE) {
      latestPrice = getLatestPrice(spotPrices);
    }
    if (latestPrice == UOrderForm.INVALID_PRICE) {
      latestPrice = fNominalPrice;
    }
    int price = latestPrice + (int)((double)fWidthOfPrice * rand.nextGaussian());
    if (price <= 0) {
    	price = 1;
    }
    forms[0].setPrice(price);
    forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));

    println("day=" + day + ", session=" + session + ", latestPrice="  + latestPrice
    		    + ", " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
            + ", quantity=" + forms[0].getQuantity());
    return forms;
  }

  /**
   * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肵�܂��D
   * @param args �V�X�e���p�����[�^
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    for (int i = 0; i < args.length; ++i) {
    	String[] strArray = args[i].split("=");
      String key = strArray[0];
      String value = strArray[1];
      if (key.equals(URandomAgent.WIDTH_OF_PRICE_KEY)) {
        fWidthOfPrice = Integer.parseInt(value);
        println("WidthOfPrice has been changed to " + fWidthOfPrice);
      } else if (key.equals(URandomAgent.MIN_QUANT_KEY)) {
        fMinQuant = Integer.parseInt(value);
        println("MinQuant has been changed to " + fMinQuant);
      } else if (key.equals(URandomAgent.MAX_QUANT_KEY)) {
        fMaxQuant = Integer.parseInt(value);
        println("MaxQuant has been changed to " + fMaxQuant);
      } else if (key.equals(URandomAgent.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else if (key.equals(URandomAgent.NOMINAL_PRICE_KEY)) {
      	fNominalPrice = Integer.parseInt(value);
      	println("NominalPrice has been changed to " + fNominalPrice);
      } else {
      	println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
      }
    }
  }
}
