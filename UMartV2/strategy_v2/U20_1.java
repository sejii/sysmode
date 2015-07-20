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
 * �Ǝ��G�[�W�F���g�p�̃e���v���[�g�ł��D
 * ���̃G�[�W�F���g�̃R�s�[���쐬���āC���O��U<�w�Дԍ�>�ɏC�����ĉ������D
 */
public class U20_1 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D

  /** �������i�̕��̃f�t�H���g�l */
  public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** �����ʂ̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** �����ʂ̍ŏ��l�̃f�t�H���g�l */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** �|�W�V�����̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** �������臒l�̃f�t�H���g�l */
  public static final double DEFAULT_SPREAD_RATIO_THRESHOLD = 0.01;

  /** �����ʂ̍ő�l */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** �����ʂ̍ŏ��l */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** �|�W�V�����̍ő�l */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U20_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//�����Ńt�B�[���h�ϐ��̒l�����������Ă��������D
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
		//println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + ", futures=" + futurePrices[futurePrices.length - 1]);
		
		// �����敪���u�������Ȃ��v�ɐݒ肵�������[���쐬���ĕԂ��D
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		double raito = getRaito(spotPrices,futurePrices);
    forms[0].setBuySell(chooseAction(raito));
    //
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
    forms[0].setPrice(determinePrice(forms[0].getBuySell(),futurePrices));
    int q = chooseQuantity(raito,day,session);
    if (q != 0){
    	forms[0].setQuantity(q);
    }else{
      forms[0].setBuySell(UOrderForm.NONE);
      return forms;
    }
    println(day*8+session + " => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
           + ", quantity=" + forms[0].getQuantity());
    return forms;
  }
	
	private int chooseQuantity(double raito, int day, int session){
		double para = 0.5 + 0.5 * (double) (day*8 + session) / 240.0;
		int q=(int)(1000 * para * Math.abs(raito));
		if (q > fMaxQuant){
			q=fMaxQuant;
		}else if (q < fMinQuant){
			q=0;
		}
		return q;
	}

  private int determinePrice(int action, int[] prices) {
  	int price = UOrderForm.INVALID_PRICE;
  	if (action == UOrderForm.BUY) {
      price = prices[prices.length - 1];
    } else if (action == UOrderForm.SELL) {
      price = prices[prices.length - 1];
    }
  	if (price < 0) {
  		price = 1;
  	}
  	return price;
  }
	
	private int chooseAction(double raito) {
		if(raito == 0.0){
			return UOrderForm.NONE;
		} else if (raito > DEFAULT_SPREAD_RATIO_THRESHOLD){
			return UOrderForm.SELL;
		} else if (-raito < DEFAULT_SPREAD_RATIO_THRESHOLD){
			return UOrderForm.BUY;
		} else{
			return UOrderForm.NONE;
		}
	}
	
	private double getRaito (int[] spotPrices ,int[] futurePrices) {
		double spot = (double) spotPrices[spotPrices.length - 1];
		double future = (double) futurePrices[futurePrices.length - 1];
		if (futurePrices[futurePrices.length - 1] == -1){
			return 0.0;
		}
		return (future - spot) / future;
	}

  /**
   * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肵�܂��D
   * @param args �V�X�e���p�����[�^
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //���̃��\�b�h���I�[�o�[���C�h����K�v�͂���܂���D
  }
}



















