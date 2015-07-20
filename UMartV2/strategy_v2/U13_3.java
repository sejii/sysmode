package strategy_v2;

import java.util.Random;


public class U13_3 extends UAgent {

	/**�f�t�H���g�l�̐ݒ�**/
  /* �������ʂ̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_QUANT = 200;
  /* �������ʂ̍ŏ��l�̃f�t�H���g�l */
  public static final int DEFAULT_MIN_QUANT = 30;
  /* ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i�̃f�t�H���g�l */
  public static final int DEFAULT_NOMINAL_PRICE = 3000;
	/*���蔃���|�W�V�����̍ő�l�̃f�t�H���g�l*/
	public static final int DEFAULT_MAX_POSITION = 300;

	/*���Ή��l�*/
	private double DEFAULT_RELATIVE_VALUE = 0.001;
	/*���Ή��l�ϓ����*/
	private double DEFAULT_CHANGE_VALUE = 0.002;

	/* �������ʂ̍ő�l�̃v���p�e�B��(MaxQuant) */
 	public static final String MAX_QUANT_KEY = "MaxQuant";
 	/* �������ʂ̍ŏ��l�̃v���p�e�B��(MinQuant) */
 	public static final String MIN_QUANT_KEY = "MinQuant";
 	/* ��/���|�W�V�����̍ő�l�̃v���p�e�B��(MaxPosition) */
 	public static final String MAX_POSITION_KEY = "MaxPosition";
	/* ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i�̃v���p�e�B��(NominalPrice) */
	public static final String NOMINAL_PRICE_KEY = "NominalPrice";

	/**�t�B�[���h�ϐ����`**/
  /* �������ʂ̍ő�l */
  private int fMaxQuant = DEFAULT_MAX_QUANT;
  /* �������ʂ̍ŏ��l */
  private int fMinQuant = DEFAULT_MIN_QUANT;
  /* ��/���|�W�V�����̍ő�l */
  private int fMaxPosition = DEFAULT_MAX_POSITION;
  /* ���߂̉��i�������Ȃ��Ƃ��ɗ��p���鉿�i */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

  /*�O��̑��Ή��l*/
  /*�O�X��̑��Ή��l*/
  /*���Ή��l�ϓ���*/
  /*���Z���i*/
  /*�������i*/

	/**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */

	public U13_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	/*getter/setter*/
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
	public int getMaxPosition(){
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

  	Random rand = getRandom();

		// ���݂̓��C���݂̐߁C���݂̌������i��\������D
    println("");
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + ", futures=" + futurePrices[futurePrices.length - 1]);
		println("spot size:" + spotPrices.length + ", future size:" + futurePrices.length);

		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();

		/*�����敪������*/
		forms[0].setBuySell(chooseAction(spotPrices, futurePrices));

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

    /*�������i������*/
    forms[0].setPrice(determinePrice(spotPrices, futurePrices));

    /*�������ʂ�����*/
    forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));


    /*���蔃�����s����΁A�ω��������Ƃ��o�͂���*/
    print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
        + ", quantity=" + forms[0].getQuantity());

    return forms;
  }


  /**�����敪��I��ŕԂ�*/
	private int chooseAction(int[] spotprices, int[] futureprices){
  	int action = UOrderForm.NONE;

		/*���߂̐敨���i�ƌ������i�̍����A�������i�Ŋ������l*/
		double nowRelativeValue = relativeValue(spotprices, futureprices, 0);
		/*�O�̐敨���i�ƌ������i�̍����A�������i�Ŋ������l*/
		double previousRelativeValue = relativeValue(spotprices, futureprices, 1);
		/*���ΓI�ȉ��l�̕ω��̒l*/
		double changeValue = (nowRelativeValue - previousRelativeValue)/previousRelativeValue;

		if ( (nowRelativeValue <= -DEFAULT_RELATIVE_VALUE) && ( changeValue <= -DEFAULT_CHANGE_VALUE) ){
			action = UOrderForm.BUY;
		}else if ( (nowRelativeValue >= DEFAULT_RELATIVE_VALUE) && ( changeValue >= DEFAULT_CHANGE_VALUE) ){
			action = UOrderForm.SELL;
		}else {
			action = UOrderForm.NONE;
		}

		return action;
	}

  /**�������i�����肵�ĕԂ�*/
	private int determinePrice(int[] spotprices, int[] futureprices) {
		int price = UOrderForm.INVALID_PRICE;
		price = (int)( ((double)spotprices[spotprices.length - 1] + (double)futureprices[futureprices.length - 1])/2
				+ ( ((double)spotprices[spotprices.length - 1] - (double)futureprices[futureprices.length - 1])/2 ) * getRandom().nextGaussian() );

  	if (price < 0) {
  		price = 1;
  	}

		return price;
	}


  /**�����敪�����肷��̂ɕK�v�ȑ��Ή��l�����肵�ĕԂ�*/
  private double relativeValue(int[] spotprices, int[] futureprices, int i){
  	 double RELATIVE_VALUE = ((double)futureprices[futureprices.length - 1 - i] - (double)spotprices[spotprices.length -1 - i])/(double)spotprices[spotprices.length - 1 - i];
  	 return RELATIVE_VALUE;
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
      if (key.equals(UMovingAverageAgent.MIN_QUANT_KEY)) {
        fMinQuant = Integer.parseInt(value);
        println("MinQuant has been changed to " + fMinQuant);
      } else if (key.equals(UMovingAverageAgent.MAX_QUANT_KEY)) {
        fMaxQuant = Integer.parseInt(value);
        println("MaxQuant has been changed to " + fMaxQuant);
      } else if (key.equals(UMovingAverageAgent.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else if (key.equals(URandomAgent.NOMINAL_PRICE_KEY)) {
      	fNominalPrice = Integer.parseInt(value);
      	println("NominalPrice has been changed to " + fNominalPrice);
      } else {
      	println("Unknown parameter:" + key + " in U13_3_Strategy.setParameters");
      }
    }
  }
}
