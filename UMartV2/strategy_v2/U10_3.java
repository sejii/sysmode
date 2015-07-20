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

public class U10_3 extends UAgent {

	/** �Z���̐ߐ��̃f�t�H���g�l */
	public static final int DEFAULT_SHORT_TERM = 8;

	/** �����̐ߐ��̃f�t�H���g�l */
	public static final int DEFAULT_MEDIUM_TERM = 16;

  /** �������ʂ̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** �������ʂ̍ŏ��l�̃f�t�H���g�l */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** ��/���|�W�V�����̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_POSITION = 300;

	/** �Z���̐ߐ� */
	private int fShortTerm = DEFAULT_SHORT_TERM;

	/** �����̐ߐ� */
	private int fMediumTerm = DEFAULT_MEDIUM_TERM;

  /** �������ʂ̍ő�l */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** �������ʂ̍ŏ��l */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** ��/���|�W�V�����̍ő�l */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** �Z���̐ߐ��̃v���p�e�B��(ShortTerm) */
  public static final String SHORT_TERM_KEY = "ShortTerm";

  /** �����̐ߐ��̃v���p�e�B��(MediumTerm) */
  public static final String MEDIUM_TERM_KEY = "MediumTerm";

  /** �������ʂ̍ő�l�̃v���p�e�B��(MaxQuant) */
	public static final String MAX_QUANT_KEY = "MaxQuant";

	/** �������ʂ̍ŏ��l�̃v���p�e�B��(MinQuant) */
	public static final String MIN_QUANT_KEY = "MinQuant";

	/** ��/���|�W�V�����̍ő�l�̃v���p�e�B��(MaxPosition) */
	public static final String MAX_POSITION_KEY = "MaxPosition";

  /** 1�ߑO�ɂ�����Z���ړ����ϒl */
	private double fPreviousShortTermMovingAverage = UOrderForm.INVALID_PRICE;

	/** 1�ߑO�ɂ����钆���ړ����ϒl */
	private double fPreviousMediumTermMovingAverage = UOrderForm.INVALID_PRICE;

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U10_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
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


  	UOrderForm[] forms = new UOrderForm[1];
  	forms[0] = new UOrderForm();
  	int[] prices =futurePrices;
    forms[0].setBuySell(chooseAction(prices));
    println("");
    print("day=" + day + ", session=" + session
          + ", futures=" + prices[prices.length - 1]
          + ", shortTerm=" + fPreviousShortTermMovingAverage
          + ", mediumTerm=" + fPreviousMediumTermMovingAverage);
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
    forms[0].setPrice(determinePrice(forms[0].getBuySell(), prices));
    forms[0].setQuantity(determineQuantity(prices,chooseAction(prices)));
    print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
           + ", quantity=" + forms[0].getQuantity());
    return forms;
  }

  //���̃G�[�W�F���g��������������A�ړ����ς̍����傫���Ƃ��͑傫��
	//�������Ƃ��͏��������킹��悤�Ƀ��\�b�h��ǉ�����

  private int determineQuantity(int[] prices,int action){
  	double shortTermMovingAverage = calculateMovingAverage(prices, fShortTerm);
  	double mediumTermMovingAverage = calculateMovingAverage(prices, fMediumTerm);
		int quantity=0;
		if (action == UOrderForm.BUY){
		if (shortTermMovingAverage>mediumTermMovingAverage+10){
			quantity=100;
		}else{
			quantity=50;
		}
		}
		if (action == UOrderForm.SELL){
		if (shortTermMovingAverage+10<mediumTermMovingAverage){
			quantity=100;
		}else{
			quantity=50;
		}
		}

  	return quantity;
  }


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

	 //���̃G�[�W�F���g�̔����̋@��𑝂₷���߂ɁA��ߑO�̈ړ����ς̍��ɂ������𖳂������B

	  private int chooseAction(int[] prices) {
	  	int action = UOrderForm.NONE;
	  	double shortTermMovingAverage = calculateMovingAverage(prices, fShortTerm);
	  	double mediumTermMovingAverage = calculateMovingAverage(prices, fMediumTerm);
	  	if (fPreviousShortTermMovingAverage != UOrderForm.INVALID_PRICE
	  			&& fPreviousMediumTermMovingAverage != UOrderForm.INVALID_PRICE
	  			&& shortTermMovingAverage != UOrderForm.INVALID_PRICE
	  			&& mediumTermMovingAverage != UOrderForm.INVALID_PRICE) {
	  		if (mediumTermMovingAverage < shortTermMovingAverage) {
	  			action = UOrderForm.BUY;
	  		} else if(shortTermMovingAverage < mediumTermMovingAverage) {
	  			action = UOrderForm.SELL;
	  		}
	  	}
	  	fPreviousShortTermMovingAverage = shortTermMovingAverage;
	  	fPreviousMediumTermMovingAverage = mediumTermMovingAverage;
	  	return action;
	  }

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
	    for (int i = 0; i < args.length; ++i) {
	    	String[] strArray = args[i].split("=");
	      String key = strArray[0];
	      String value = strArray[1];
	      if (key.equals(UMovingAverageAgent.SHORT_TERM_KEY)) {
	        fShortTerm = Integer.parseInt(value);
	        println("ShortTerm has been changed to " + fShortTerm);
	      } else if (key.equals(UMovingAverageAgent.MEDIUM_TERM_KEY)) {
	      	fMediumTerm = Integer.parseInt(value);
	      	println("MediumTerm has been changed to " + fMediumTerm);
	      } else if (key.equals(UMovingAverageAgent.MIN_QUANT_KEY)) {
	        fMinQuant = Integer.parseInt(value);
	        println("MinQuant has been changed to " + fMinQuant);
	      } else if (key.equals(UMovingAverageAgent.MAX_QUANT_KEY)) {
	        fMaxQuant = Integer.parseInt(value);
	        println("MaxQuant has been changed to " + fMaxQuant);
	      } else if (key.equals(UMovingAverageAgent.MAX_POSITION_KEY)) {
	        fMaxPosition = Integer.parseInt(value);
	        println("MaxPosition has been changed to " + fMaxPosition);
	      } else {
	      	println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
	      }
	    }
	  }
	}