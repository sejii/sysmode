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
public class U19_3 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D
  /** �������i�̕��̃f�t�H���g�l */
  public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** �������ʂ̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** �������ʂ̍ŏ��l�̃f�t�H���g�l */
  public static final int DEFAULT_MIN_QUANT = 10;

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

		
	//���炩���ߌ��߂�ꂽ�Œ�̐ߐ�. ���̐ߐ��������オ��/�����葱����Ɣ���/�����I������
	public static final int SESSION_JUDGE = 10;
	
  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U19_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//�����Ńt�B�[���h�ϐ��̒l�����������Ă��������D

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
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] +", futures="+futurePrices[futurePrices.length-1]);
		println("spot size="+spotPrices.length+", futures size="+futurePrices.length);
		// �����敪���u�������Ȃ��v�ɐݒ肵�������[���쐬���ĕԂ��D
    //--------------------

		/*
		//���߂̐敨���i��\��
		for(int i=futurePrices.length-(SESSION_JUDGE+1); i<futurePrices.length; i++)
		{
			print(futurePrices[i]+ " ");
		}
		println("");
		*/
		
  	Random rand = getRandom();
  	
		UOrderForm[] forms = new UOrderForm[1];
  	forms[0] = new UOrderForm();

  	//�敨���i���オ��/�����葱���Ă��鎞�ɃJ�E���g����
  	int session_up_counter = 0;
  	int session_down_counter = 0;
  	
  	//���߂̒l�i���オ��^�����葱���Ă��邩�m�F����
  	for(int i=1; i<=SESSION_JUDGE; i++){
  		
  		//�敨���i���オ������
  		if( (futurePrices[futurePrices.length - i] -  futurePrices[futurePrices.length - (i+1)]) > 0){
  			session_up_counter++;
  		}
  		
  		//�敨���i������������
  		if( (futurePrices[futurePrices.length - i] -  futurePrices[futurePrices.length - (i+1)]) < 0){
  			session_down_counter++;
  		}
  		
  	}
  	//println("up count:"+session_up_counter+",  "+"down count:"+session_down_counter);
  	//println("------");

  	
  	//-- ���邩�������ݒ� --
  	//���i���オ�葱���Ă��鎞�A����オ�邱�Ƃ��\�z�����̂Łu�����v��I������
    if(session_up_counter > 7){
    	forms[0].setBuySell(UOrderForm.BUY);
    }
  	//���i�������葱���Ă��鎞�A���㉺���邱�Ƃ��\�z�����̂Łu����v��I������
    else if(session_down_counter > 7){
    	forms[0].setBuySell(UOrderForm.SELL);
    }
    //�ς��Ȃ����́A�\�z�����Ăɂ����̂ŉ������Ȃ�
    else{
    	forms[0].setBuySell(UOrderForm.NONE);
      return forms;
    }
  	
    //�����̎�
    if (forms[0].getBuySell() == UOrderForm.BUY) {
      if (position > fMaxPosition) {
        forms[0].setBuySell(UOrderForm.NONE);
        return forms;
      }
    }
    //����̎�
    else if (forms[0].getBuySell() == UOrderForm.SELL) {
      if (position < -fMaxPosition) {
        forms[0].setBuySell(UOrderForm.NONE);
        return forms;
      }
    }

    //�敨����̐����������߂̉��i�𓾂�
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
    //���i��ݒ�
    forms[0].setPrice(price);
    //�ʂ�ݒ�
    forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));

    //�����[�̓��e��\��
    
    println("day=" + day + ", session=" + session + ", latestPrice="  + latestPrice
    		    + ", " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
            + ", quantity=" + forms[0].getQuantity());
		
    //---------------------

    
    return forms;
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
      if (key.equals(U19_3.WIDTH_OF_PRICE_KEY)) {
        fWidthOfPrice = Integer.parseInt(value);
        println("WidthOfPrice has been changed to " + fWidthOfPrice);
      } else if (key.equals(U19_3.MIN_QUANT_KEY)) {
        fMinQuant = Integer.parseInt(value);
        println("MinQuant has been changed to " + fMinQuant);
      } else if (key.equals(U19_3.MAX_QUANT_KEY)) {
        fMaxQuant = Integer.parseInt(value);
        println("MaxQuant has been changed to " + fMaxQuant);
      } else if (key.equals(U19_3.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else if (key.equals(U19_3.NOMINAL_PRICE_KEY)) {
      	fNominalPrice = Integer.parseInt(value);
      	println("NominalPrice has been changed to " + fNominalPrice);
      } else {
      	println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
      }
    }

  }
}