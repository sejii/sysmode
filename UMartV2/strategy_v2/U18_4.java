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

/**
 * �敨���i��p���郉���_���G�[�W�F���g�N���X�ł��D
 */
public class U18_4 extends UAgent {

  /** �������i�̕��̃f�t�H���g�l */
  //public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** �����������ʂ̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_BUY_MAX_QUANT = 100;

  /** ���蒍�����ʂ̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_SELL_MAX_QUANT = 150;

  /** ��/���|�W�V�����̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_POSITION = 2500;

  /** ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i�̃f�t�H���g�l */
  public static final int DEFAULT_NOMINAL_PRICE = 2100;

  /** ���όv�Z�̏d�݂̃f�t�H���g�l */
  public static final double DEFAULT_WEIGHT = 20.0;

  /** �������i�̕� */
  //private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE;

  /** �����������ʂ̍ő�l */
  private int fBuyMaxQuant = DEFAULT_BUY_MAX_QUANT;

  /** ���蒍�����ʂ̍ő�l */
  private int fSellMaxQuant = DEFAULT_SELL_MAX_QUANT;

  /** ��/���|�W�V�����̍ő�l */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

  /** �������i�̕��ϒl */
  private double fAverage = (double)DEFAULT_NOMINAL_PRICE;

  /** ���όv�Z�̏d�� */
  private double fWeight = DEFAULT_WEIGHT;

	/** �������i�̕��̃v���p�e�B��(WidthOfPrice) */
	public static final String WIDTH_OF_PRICE_KEY = "WidthOfPrice";

	/** �����������ʂ̍ő�l�̃v���p�e�B��(BuyMaxQuant) */
	public static final String BUY_MAX_QUANT_KEY = "BuyMaxQuant";

	/** ���蒍�����ʂ̍ő�l�̃v���p�e�B��(SellMaxQuant) */
	public static final String SELL_MAX_QUANT_KEY = "SellMaxQuant";

	/** ��/���|�W�V�����̍ő�l�̃v���p�e�B��(MaxPosition) */
	public static final String MAX_POSITION_KEY = "MaxPosition";

	/** ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i�̃v���p�e�B��(NominalPrice) */
	public static final String NOMINAL_PRICE_KEY = "NominalPrice";

	/** ���όv�Z�̏d�݂̃v���p�e�B��(Weight) */
	public static final String WEIGHT_KEY = "Weight";

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U18_4(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	/**
	 * ���i����Ԃ��܂��D
	 * @return ���i��
	 */
	/*public int getWidthOfPrice() {
		return fWidthOfPrice;
	}*/

	/**
	 * ����ő咍�����ʂ�Ԃ��܂��D
	 * @return ����ő咍������
	 */
	public int getSellMaxQuant() {
		return fSellMaxQuant;
	}

	/**
	 * �����ő咍�����ʂ�Ԃ��܂��D
	 * @return �����ő咍������
	 */
	public int getBuyMaxQuant() {
		return fBuyMaxQuant;
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
	 * ���όv�Z�̏d�݂�Ԃ��܂��D
	 * @return ���όv�Z�̏d��
	 */
	public double getWeight() {
		return fWeight;
	}

	/**
	 * �������i�̕��ϒl��Ԃ��܂��D
	 * @return �s�ꉿ�i������̂Ƃ��̒������i
	 */
	public double getAverage() {
		return fAverage;
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
		int latestSpotPrice = spotPrices[spotPrices.length - 1];
    if (latestSpotPrice == UOrderForm.INVALID_PRICE) {
      latestSpotPrice = fNominalPrice;
    }
    fWeight += 1;
    fAverage += ((double)latestSpotPrice - fAverage)/fWeight;

  	UOrderForm[] forms = new UOrderForm[1];
  	forms[0] = new UOrderForm();
  	int price = 1;
  	if (latestSpotPrice >= fAverage) {
  		forms[0].setBuySell(UOrderForm.SELL);
    } else if (latestSpotPrice < fAverage & latestSpotPrice > 0) {
    	forms[0].setBuySell(UOrderForm.BUY);
    } else {
  		forms[0].setBuySell(UOrderForm.NONE);
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
  	if (forms[0].getBuySell() == UOrderForm.SELL) {
      	price = latestSpotPrice + 180 -
      			(int)((Math.log((double)(latestSpotPrice - 2099)))/Math.log(1.029));
        forms[0].setPrice(price);
        forms[0].setQuantity(fSellMaxQuant);
    } else {
      	price = latestSpotPrice - 130 +
      			(int)((Math.log((double)(2101 - latestSpotPrice)))/Math.log(1.04));
        forms[0].setPrice(price);
        forms[0].setQuantity(fBuyMaxQuant);
      }
    if (forms[0].getBuySell() == UOrderForm.BUY) {
      if (money < (forms[0].getPrice() + 3000000) * fBuyMaxQuant * 2) {
        forms[0].setBuySell(UOrderForm.NONE);
        return forms;
      }
    } else if (forms[0].getBuySell() == UOrderForm.SELL) {
      if (money < (forms[0].getPrice() + 3000000) * fSellMaxQuant) {
        forms[0].setBuySell(UOrderForm.NONE);
        return forms;
      }
    }
    println("day=" + day + ", session=" + session + ", latestPrice="  + latestSpotPrice
    		    + ", " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
            + ", quantity=" + forms[0].getQuantity() + ", position=" + position
            + ",average" + fAverage + ",weight" + fWeight);
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
      /*if (key.equals(SPAegnt.WIDTH_OF_PRICE_KEY)) {
        fWidthOfPrice = Integer.parseInt(value);
        println("WidthOfPrice has been changed to " + fWidthOfPrice);
      } else if (key.equals(SPAegnt.MIN_QUANT_KEY)) {
        fMinQuant = Integer.parseInt(value);
        println("MinQuant has been changed to " + fMinQuant);*/
      if (key.equals(U18_4.BUY_MAX_QUANT_KEY)) {
        fBuyMaxQuant = Integer.parseInt(value);
        println("BuyMaxQuant has been changed to " + fBuyMaxQuant);
      } else if (key.equals(U18_4.SELL_MAX_QUANT_KEY)) {
        fSellMaxQuant = Integer.parseInt(value);
        println("SellMaxQuant has been changed to " + fSellMaxQuant);
      } else if (key.equals(U18_4.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else if (key.equals(U18_4.NOMINAL_PRICE_KEY)) {
      	fNominalPrice = Integer.parseInt(value);
      	println("NominalPrice has been changed to " + fNominalPrice);
      } else if (key.equals(U18_4.WEIGHT_KEY)) {
      	fWeight = Integer.parseInt(value);
      	println("Weight has been changed to " + fWeight);
      } else {
      	println("Unknown parameter:" + key + " in SPAegnt.setParameters");
      }
    }
  }
}
