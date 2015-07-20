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
 * ����ꂷ��/����ꂷ���̎w�W�ƂȂ�RSI�ƁA�ړ����ς��甭�W������Bollinger Band��g�ݍ��킹�����f��
 * �����I�ɏ��ĂȂ����A�����Â炢�B
 * ����������ł̓R���e�X�g�I�ɈӖ��������̂ŁA�w���ʂ𑝂₵�Ă��܂��B
 */
public class U23_3 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D

	public static final int DEFAULT_RSI_TERM = 12; //RSI�w�����v�Z����̂ɗp����^�[���̃f�t�H���g
	public static final int DEFAULT_BOL_TERM = 21 ; //Bollinger Band���v�Z����̂ɗp����^�[���̃f�t�H���g
	public static final int DEFAULT_MAX_QUANT = 500; //�ő����ʂ̃f�t�H���g
	public static final int DEFAULT_MIN_QUANT = 200; //�ŏ�����ʂ̃f�t�H���g
	public static final int DEFAULT_MAX_POSITION = 400; //�ő�|�W�V�������̃f�t�H���g

	//�ȉ��t�B�[���h�ϐ��Ƀf�t�H���g�l�����蓖��
	private int fRsiTerm = DEFAULT_RSI_TERM;
	private int fBolTerm = DEFAULT_BOL_TERM;
	private int fMaxQuant= DEFAULT_MAX_QUANT;
	private int fMinQuant = DEFAULT_MIN_QUANT;
	private int fMaxPosition = DEFAULT_MAX_POSITION;

	//setParameter�ŗp���邽�߂ɕϐ��������蓖�ĂĂ���
	public static final String RSI_TERM_KEY = "RsiTerm";
	public static final String BOL_TERM_KEY = "BolTerm";
	public static final String MAX_QUANT_KEY = "MaxQuant";
	public static final String MIN_QUANT_KEY = "MinQuant";
	public static final String Max_POSITION_KEY = "MaxPosition";

	private double fPreviousBollingerBandMinusTwoSigma = UOrderForm.INVALID_PRICE  ; //�O�̃^�[���ɂ�����Bollinger Band��+2�Ђ̒l
	private double fPreviousBollingerBandTwoSigma = UOrderForm.INVALID_PRICE ; //�O�̃^�[���ɂ�����Bollinger Band��-2�Ђ̒l
	private int fLastTradePrice = UOrderForm.INVALID_PRICE ; //�Ō�Ɏ���������ۂ̉��i

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U23_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//�����Ńt�B�[���h�ϐ��̒l�����������Ă��������D

	}
	//�t�B�[���h�ϐ���get���\�b�h
	public int getRsiTerm(){
		return fRsiTerm;
	}
	public int getBolTerm(){
		return fBolTerm;
	}
	public int getMinQuant() {
		return fMinQuant;
	}
	public int getMaxQuant() {
		return fMaxQuant;
	}
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

		// ���݂̓��C���݂̐߁C���݂̌������i, ���݂̃|�W�V�����A�����̎c�ʂ�\������D
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]);
		println("spot size:" + spotPrices.length + ", futures size:" + futurePrices.length);
		println("Contemporary position:" + position + ", Contemporary money:" + money);

		Random rand = getRandom();
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();


		//�����Ă���|�W�V�����ɂ�������炸�A���i���\�z�ʂ�ɓ����Ȃ������ꍇ�ɂ̓��X�J�b�g����
		if (position > 0){
			if (futurePrices[futurePrices.length - 1] <= fLastTradePrice ){
				//�����O���A���݂̉��i���ŏI������i��������Ă����甄�p����
				forms[0].setBuySell(UOrderForm.SELL);
				forms[0].setPrice(determinePrice(forms[0].getBuySell(), futurePrices));
				forms[0].setQuantity(Math.abs(position));
				fLastTradePrice = UOrderForm.INVALID_PRICE; //���p���ă|�W�V������0�ɖ߂�̂ōŏI������i��INVALID�ɂ���
				return forms;
			}
		}else if (position < 0){
			if (futurePrices[futurePrices.length - 1] >= fLastTradePrice ){
				//�V���[�g���A���݂̉��i���ŏI������i�������Ă�����w������
				forms[0].setBuySell(UOrderForm.BUY);
				forms[0].setPrice(determinePrice(forms[0].getBuySell(), futurePrices));
				forms[0].setQuantity(Math.abs(position));
				fLastTradePrice = UOrderForm.INVALID_PRICE; //�w�����ă|�W�V������0�ɖ߂�̂ōŏI������i��INVALID�ɂ���
				return forms;
			}
		}

		// ���X�J�b�g�͂��Ȃ��ꍇ��RSI�ƃ{�����W���[�o���h�ɏ]���Ē����[���쐬���Ԃ��B
    forms[0].setBuySell(chooseAction(futurePrices)); //�s���̌���
    if (forms[0].getBuySell() == UOrderForm.NONE){ //�������Ȃ��Ȃ炱����form��Ԃ��ďI��
    	return forms;
    }
    if (forms[0].getBuySell() == UOrderForm.BUY){ //�w������ꍇ�ɂ́A�|�W�V�����̗ʂ𒲂ׂāA�����Ă����牽�����Ȃ�
    	if (position > fMaxPosition){
    		forms[0].setBuySell(UOrderForm.NONE);
    		return forms;
    	}
    }else if (forms[0].getBuySell() == UOrderForm.SELL){ //���p������ꍇ�ɂ��A���l�Ƀ|�W�V�����̗ʂ𒲂ׂ�
    	if (position < -fMaxPosition){
    		forms[0].setBuySell(UOrderForm.NONE);
    		return forms;
    	}
    }
    //�|�W�V�����ʂ����v�Ȃ�A���i�Ɨʂ����肷��
    forms[0].setPrice(determinePrice(forms[0].getBuySell(), futurePrices));//���i������
    fLastTradePrice = determinePrice(forms[0].getBuySell(), futurePrices); //�w�����i���t�B�[���h�ϐ��֊i�[
    forms[0].setQuantity(determineQuantity(forms[0].getBuySell(), futurePrices, position)); //�ʂ�����
    return forms; //�t�H�[����Ԃ�
  }

	//���i�����肷�郁�\�b�h
	//�ړ����ϒl�Ɠ��l�Ɍ��肷��
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

  //�w���ʂ����肷�郁�\�b�h
  //�|�W�V�������Ȃ������ꍇ�ɂ́A�ŏ��l�ƍő�l�̊ԂŃ����_���Ɍ��肷��B
  //�|�W�V�����������Ă����ꍇ�ɂ́A���̃|�W�V�����͂��ׂė��m���Ă��܂��A�ǉ��ł܂��w������B
  private int determineQuantity(int action, int[] prices, int position){
  	if(position == 0){
  		return fMinQuant + getRandom().nextInt(fMaxQuant - fMinQuant + 1);
  	}else{
  		return Math.abs(position) + fMinQuant + getRandom().nextInt(fMaxQuant - fMinQuant + 1);
  	}
  }

	//�s�������肷�郁�\�b�h�B
	//RSI��70�ȏォ�{�����W���[�o���h��2�Ѓg�����h���痣�ꂽ�ꍇ�ɂ͔��p
	//RSI��30�ȉ����{�����W���[�o���h��-2�Ѓg�����h���痣�ꂽ�ꍇ�ɂ͍w������
	private int chooseAction(int[] prices){
		int action = UOrderForm.NONE;
		double RsiValue = calculateRsi(prices, fRsiTerm);
		double MovingAverage = calculatePriceAverage(prices, fBolTerm);
		double TwoSigmaBollinger = UOrderForm.INVALID_PRICE;
		double TwoMinusSigmaBollinger = UOrderForm.INVALID_PRICE;
		//�W���΍���������ƌv�Z�����Ȃ�ABollingerBand���v�Z����
		if (calculateSigma(prices, fBolTerm) != UOrderForm.INVALID_PRICE){
			TwoSigmaBollinger = MovingAverage + 2 * calculateSigma(prices, fBolTerm);
			TwoMinusSigmaBollinger = MovingAverage - 2 * calculateSigma(prices, fBolTerm);
		}
		//�p����ϐ����L�������m���߂�
		if (fPreviousBollingerBandTwoSigma != UOrderForm.INVALID_PRICE
			&& fPreviousBollingerBandMinusTwoSigma != UOrderForm.INVALID_PRICE
			&& TwoSigmaBollinger != UOrderForm.INVALID_PRICE
			&& TwoMinusSigmaBollinger != UOrderForm.INVALID_PRICE
			&& MovingAverage != UOrderForm.INVALID_PRICE
			&& RsiValue != UOrderForm.INVALID_PRICE){
			//RSI�l��70�ȏ゠��̂����m�F����
			if(RsiValue > 70){
				//70�ȏ゠�邤���ŁA���܂�Bollinger band��+2�Ђɒ���t���Ă����̂����ꂽ�牺���邱�Ƃ����҂����̂Ŕ���
				if (prices[prices.length-1] < TwoSigmaBollinger && prices[prices.length-2] > fPreviousBollingerBandTwoSigma){
					action = UOrderForm.SELL;
				}else{
					action = UOrderForm.NONE;
				}
			//RSI�l��30�ȉ��ł��邩���m�F����
			}else if(RsiValue < 30){
				//30�ȉ��ŁA���܂�Bollinger Band��-2�Ђɒ���t���Ă����̂����ꂽ��オ�邱�Ƃ����҂����̂Ŕ���
				if (prices[prices.length -1 ] > TwoSigmaBollinger && prices[prices.length - 2] < fPreviousBollingerBandMinusTwoSigma){
					action = UOrderForm.BUY;
				}else{
					action = UOrderForm.NONE;
				}
			}else{
				action = UOrderForm.NONE;
			}
		}
		//����̌��ʂ��t�B�[���h�ϐ��ɋL�^����B
		fPreviousBollingerBandTwoSigma = TwoSigmaBollinger;
		fPreviousBollingerBandMinusTwoSigma = TwoMinusSigmaBollinger;
		return action;
	}

	//RSI�w�����v�Z����B
	private double calculateRsi(int[] prices, int term){
		double posSum = 0.0;
		double negSum = 0.0;
		for (int i = 0; i < term; ++i){
			if(prices[prices.length - 1 - i] < 0){
				return (double)UOrderForm.INVALID_PRICE;
			}
			else if(prices[prices.length - 1 - i] - prices[prices.length - term] >= 0){
				posSum += prices[prices.length - 1 - i] - prices[prices.length - term];
			}else{
				negSum += prices[prices.length - 1 - i] - prices[prices.length - term];
			}
		}
		return (posSum/(posSum+negSum)) * 100;
	}

	//���i�̈ړ����ϒl���v�Z���郁�\�b�h�B���Ƃ̃{�����W���[�o���h�̒l���v�Z����̂ɗp����B
	private double calculatePriceAverage(int[] prices, int term){
		double sum = 0.0;
		for (int i=0; i< term; ++i){
			if (prices[prices.length - 1 - i] < 0) {
  			return (double)UOrderForm.INVALID_PRICE;
  		}
			sum += (double)prices[prices.length - 1 - i];
		}
		return sum/(double)term;
	}

	//�{�����W���[�o���h�ŗp���鉿�i�̈ړ����ϒl�̕W���΍����v�Z���郁�\�b�h�B
	private double calculateSigma(int[] prices, int term){
		double sum = 0.0;
		double average = calculatePriceAverage(prices,term);
		for (int i = 0; i< term; ++i){
			if(prices[prices.length - 1 - i] < 0){
				return (double)UOrderForm.INVALID_PRICE;
			}
			sum += Math.pow((double)prices[prices.length - 1 - i] - average, 2);
		}
		return Math.sqrt(sum/term - 1);
	}

  /**
   * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肵�܂��D
   * @param args �V�X�e���p�����[�^
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    for ( int i = 0; i < args.length; ++i){
    	String[] strArray = args[i].split("=");
    	String key = strArray[0];
    	String value = strArray[1];
    	if(key.equals(U23_3.RSI_TERM_KEY)){
    		fRsiTerm = Integer.parseInt(value);
    	  println("RsiTerm has been changed to " + fRsiTerm);
    	} else if(key.equals(U23_3.BOL_TERM_KEY)){
    		fBolTerm = Integer.parseInt(value);
    	  println("BolTerm has been changed to " + fBolTerm);
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
      	println("Unknown parameter:" + key + " in U23_3.setParameters");
      }
    }
  }
}
