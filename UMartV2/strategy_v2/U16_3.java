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
public class U16_3 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D
	public static final int DEFAULT_MAX_QUANT = 500;
	public static final int DEFAULT_MIN_QUANT = 50;
	public static final int DEFAULT_MAX_POSITION = 1000;
	public static final int LEVEL = 0;
	public static final int INCREASE = 1;
	public static final int DECREASE = 2;
	private int fMaxQuant = DEFAULT_MAX_QUANT;
	private int fMinQuant = DEFAULT_MIN_QUANT;
	private int fMaxPosition = DEFAULT_MAX_POSITION;
	public static final String SHORT_TERM_KEY = " ShortTerm ";
	public static final String MEDIUM_TERM_KEY = " MediumTerm ";
	public static final String MAX_QUANT_KEY = " MaxQuant ";
	public static final String MIN_QUANT_KEY = " MinQuant ";
	public static final String MAX_POSITION_KEY = " MaxPosition ";

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U16_3(String loginName, String passwd, String realName, int seed) {
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

	public UOrderForm[] makeOrderForms(int day, int session, int maxDays,
			int noOfSessionsPerDay, int[] spotPrices, int[] futuresPrices,
			int position, long money) {
		int[] prices = futuresPrices;
		int range = determineQuant(prices, day, spotPrices);
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		forms[0].setBuySell(chooseAction(prices,  day, spotPrices));
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
		forms[0].setPrice(determinePrice(forms[0].getBuySell(), prices,spotPrices));
		forms[0].setQuantity(fMinQuant + range);
		println("day =" + day + "session =" + session);
		println(" => " + forms[0].getBuySellByString() + ", price ="
				+ forms[0].getPrice() + ", quantity =" + forms[0].getQuantity());
		return forms;
	}


/**
 * �����ʂ̌���
 * @param prices
 * @param day
 * @param spotPrices
 * @return
 */
	private int determineQuant(int[] prices,int day,int[] spotPrices){
		int range = 0;
		//����ڂ͎�������Ȃ�
		if (day == 1){
			return range;
		}
		 if ( getChangeTrend(prices, day) != 0){
			 range +=getChangeTrend(prices, day);
		 }
		 if (getTopBottmPrice( prices, day, spotPrices) != 0){
			 range +=getTopBottmPrice( prices, day, spotPrices);
		 }
		 if ( getSpotPriceTrend(spotPrices) != LEVEL){
			 range += spotPrices[spotPrices.length-1]-spotPrices[spotPrices.length-2];
		 }
		 if ( range > fMaxQuant - fMinQuant ){
			 range = fMaxQuant - fMinQuant;
		 }
		return range;
	}

	/**
	 * �������i�̌���
	 * @param action
	 * @param prices
	 * @param spotPrices
	 * @return
	 */
	private int determinePrice(int action, int[] prices,int[] spotPrices) {
		Random rand = getRandom();
		int price = UOrderForm.INVALID_PRICE;
		if (action == UOrderForm.BUY) {
			price = spotPrices[spotPrices.length-1]+(int)(rand.nextGaussian()
					*Math.abs(spotPrices[spotPrices.length-1]-spotPrices[spotPrices.length-2]));
		} else if (action == UOrderForm.SELL) {
			price = spotPrices[spotPrices.length-1]-(int)(rand.nextGaussian()
					*Math.abs(spotPrices[spotPrices.length-1]-spotPrices[spotPrices.length-2]));
		}
		if (price < 0) {
			price = 1;
		}
		return price;
	}


	/**
	 * �s���̑I��
	 * @param prices
	 * @param day
	 * @param spotPrices
	 * @return
	 */
	private int chooseAction(int[] prices, int day, int[] spotPrices) {
		int action = UOrderForm.NONE;
		if (day == 1){
		   return action;
		}
		if ( getShortTrend( prices ) == INCREASE && getMediumTrend( prices ) == INCREASE ||
				        getSpotPriceTrend(spotPrices) == INCREASE){
			action = UOrderForm.BUY;
		}
		if ( getShortTrend( prices ) == DECREASE && getMediumTrend( prices ) == DECREASE ||
				        getSpotPriceTrend(spotPrices) == DECREASE){
			action = UOrderForm.SELL;
		}
		return action;
	}


	/**
	 * �������i�̃g�����h
	 * @param spotPrices
	 * @return
	 */
	public int getSpotPriceTrend(int[] spotPrices){
		int trend = LEVEL;
			if ( spotPrices[spotPrices.length-1] <= spotPrices[spotPrices.length-2]  ){
				trend = DECREASE;
		}
			if ( spotPrices[spotPrices.length-1] > spotPrices[spotPrices.length-2]  ){
				trend = INCREASE;
		}
		return trend;
	}


	/**
	 * �敨���i�̒Z���g�����h
	 * @param prices
	 * @return
	 */
	public int getShortTrend(int[] prices ){
	  int ShortTrend = LEVEL;
	  int fp=0,mp=0,lp=0;
		 fp = prices[prices.length - 3];
		 mp = prices[prices.length - 2];
		 lp = prices[prices.length - 1];
		if (  mp <= lp){
			ShortTrend = INCREASE;
		}
		if (  mp > lp){
			ShortTrend = DECREASE;
		}
		if ( fp <= mp && mp >= lp){
				ShortTrend = LEVEL;
		}
		if ( fp > mp && mp < lp){
				ShortTrend = LEVEL;
		}
		return ShortTrend;
	}


	/**
	 * �敨���i�̒����g�����h
	 * @param prices
	 * @return
	 */
	public int getMediumTrend(int[] prices){
	  int MediumTrend = LEVEL;
	  int sp=0,mp=0,cp=0;
	  for (int i=0; i<3; i++){
		  sp += prices[prices.length - 7 - i];
	      mp += prices[prices.length - 4 - i];
	      cp += prices[prices.length - 1 - i];
	  }
	  if ( sp <= mp && mp <= cp){
		  MediumTrend = INCREASE;
		}
		if ( sp >= mp && mp >= cp){
			MediumTrend = DECREASE;
		}
		if ( sp < mp && mp > cp){
			if ( sp < cp ){
				MediumTrend = INCREASE;
			}
			if ( sp > cp ){
				MediumTrend = DECREASE;
			}
		}
		if ( sp > mp && mp < cp){
			if ( sp < cp ){
				MediumTrend = INCREASE;
			}
			if ( sp > cp ){
				MediumTrend = DECREASE;
			}
		}
		return MediumTrend;
	}

	/**
	 * �敨���i�̃g�����h�̕ω��𒲂ׂ�
	 * @param prices
	 * @param day
	 * @return
	 */
	public int getChangeTrend(int[] prices, int day){
		int fp=0,mp=0,lp=0,changeT=0;
		 for ( int i=0; i < 3 ; i++ ){
			fp += prices[prices.length - 7 - i];
			mp += prices[prices.length - 4 - i];
			lp += prices[prices.length - 1 - i];
		 }
		 if ( fp < mp && mp > lp && fp < lp ){
				changeT = Math.abs( fp - lp);
		 }
		 if ( fp > mp && mp < lp && lp < fp){
				changeT = Math.abs( fp - lp);
		 }
			return changeT;
	}

	/**
	 * �敨���i�̍ň��l�ƍō��l�̌v�Z
	 * @param prices
	 * @param day
	 * @param spotPrices
	 * @return
	 */
	public int getTopBottmPrice(int[] prices,int day, int[] spotPrices){
		int TopBottmPrice = 0;
		if (day == 1){
			return TopBottmPrice;
		}
		int TopP = prices[8];
		int BottmP = prices[8];
		if (TopP <  prices[prices.length - 1] ){
			TopP = prices[prices.length - 1];
			if ( chooseAction(prices,day,spotPrices) == UOrderForm.BUY ){
			return TopBottmPrice = (TopP - prices[8]) + day;
			}
		}
		if (BottmP >  prices[prices.length - 1] ){
			BottmP = prices[prices.length - 1];
			if ( chooseAction(prices,day,spotPrices) == UOrderForm.SELL ){
			return TopBottmPrice = (prices[8] - BottmP) + day;
			}
		}
		 return TopBottmPrice;
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
			if (key.equals(U16_3.SHORT_TERM_KEY)) {
			} else if (key.equals(U16_3.MIN_QUANT_KEY)) {
				fMinQuant = Integer.parseInt(value);
				println(" MinQuant has been changed to " + fMinQuant);
			} else if (key.equals(U16_3.MAX_QUANT_KEY)) {
				fMaxQuant = Integer.parseInt(value);
				println(" MaxQuant has been changed to " + fMaxQuant);
			} else if (key.equals(U16_3.MAX_POSITION_KEY)) {
				fMaxPosition = Integer.parseInt(value);
				println(" MaxPosition has been changed to " + fMaxPosition);
			} else {
				println(" Unknown parameter :" + key
						+ " in UMovingAverageAgent . setParameters ");
			}
		}
	}
}
