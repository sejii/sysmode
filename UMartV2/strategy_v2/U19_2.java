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

import java.util.Arrays;
import java.util.Random;


/**
 * 独自エージェント用のテンプレートです．
 * このエージェントのコピーを作成して，名前をU<学籍番号>に修正して下さい．
 */
public class U19_2 extends UAgent {

	//ここにフィールド変数を定義してください．
	public static final int DEFAULT_SHORT_TERM = 7;
	public static final int DEFAULT_MEDIUM_TERM = 15;
	public static final int DEFAULT_MIN_QUANT = 10;
	public static final int DEFAULT_MAX_QUANT = 100;
	public static final int DEFAULT_MAX_POSITION = 300;
	public static final String SHORT_TERM_KEY = "ShortTerm";
	public static final String MEDIUM_TERM_KEY = "MediumTerm";
	public static final String MIN_QUANT_KEY = "MinQuant";
	public static final String MAX_QUANT_KEY = "MaxQuant";
	public static final String MAX_POSITION_KEY = "MaxPosition";

	public static final int[] WEIGHT_VECTOR_SHORT = { -2, 3, 6, 7, 6, 3, -2};
	public static final int[] WEIGHT_VECTOR_MEDIUM = {-78, -13, 42, 87, 122, 147, 162, 167, 162, 147, 122, 87, 42, -13, -78};

	private int fShortTerm = DEFAULT_SHORT_TERM;
	private int fMediumTerm = DEFAULT_MEDIUM_TERM;
	private int fMinQuant = DEFAULT_MIN_QUANT;
	private int fMaxQuant = DEFAULT_MAX_QUANT;
	private int fMaxPosition = DEFAULT_MAX_POSITION;
	private double fPreviousShortTermMovingAverage;
	private double fPreviousMediumTermMovingAverage;

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U19_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//ここでフィールド変数の値を初期化してください．

	}


	/**
	 * 短期の節数を返す
	 * @return
	 */
	public int getShortTerm(){
		return fShortTerm;
	}

	/**
	 * 中期の節数を返す
	 * @return
	 */
	public int getMediumTerm(){
		return fMediumTerm;
	}

	/**
	 * 最小注文数量を返す
	 * @return
	 */
	public int getMinQuant(){
		return fMinQuant;
	}

	/**
	 * 最大注文数量を返す
	 * @return
	 */
	public int getMaxQuant(){
		return fMaxQuant;
	}
	/**
	 * 最大ポジションを返す
	 * @return
	 */
	public int getMaxPosition(){
		return fMaxPosition;
	}

  /**
   * 注文票を作成します．
   * @param day 日
   * @param session 節
   * @param maxDays 取引日数
   * @param noOfSessionsPerDay 1日の節数
   * @param spotPrices 現物価格系列．spotPrices[0]からspotPrices[119]までの120節分のデータが格納されている．spotPrices[119]が直近の価格です．ただし，価格が成立していない場合，-1が入っているので注意してください．
   * @param futurePrices 先物価格系列．futurePrices[0]からfuturePrices[59]までの60節分のデータが格納されています．futurePrices[59]が直近の価格です．ただし，価格が成立していない場合，-1が入っているので注意してください．また，取引開始節より前は現物価格が格納されています．
   * @param position ポジション．正ならば買い越し(ロング・ポジション)，負ならば売り越し（ショート・ポジション）を表します．
   * @param money 現金残高．型がlongであることに注意してください．
   * @return UOrderForm[] 注文票の配列
   */
	public UOrderForm[] makeOrderForms(int day, int session,
                                      int maxDays, int noOfSessionsPerDay,
                                      int[] spotPrices, int[] futuresPrices,
                                      int position, long money) {

		Random rand = getRandom();
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();

		int action = chooseAction(futuresPrices);
		if (action == UOrderForm.SELL
				&& position < 0
				&& Math.abs(position) > fMaxPosition) {
			action = UOrderForm.NONE;
		}else if(action == UOrderForm.BUY
				&& position > 0
				&& Math.abs(position) > fMaxPosition){
			action = UOrderForm.NONE;
		}
		if(action == UOrderForm.NONE && Math.abs(position)>=150){
			action = (position>0)?UOrderForm.SELL:UOrderForm.BUY;
		}
		forms[0].setBuySell(action);

		forms[0].setPrice(determinPrice(action, futuresPrices));
		forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));

		println("day=" + day + ",\tsession=" + session
				+ ",\tPreviousMediumTermMovingAverage="
					+ (int)fPreviousMediumTermMovingAverage
				+ ",\tPreviousShortTermMovingAverage="
					+ (int)fPreviousShortTermMovingAverage
				+ ",\t" + forms[0].getBuySellByString()
				+ ",\tprice=" + forms[0].getPrice()
				+ ",\tquantity=" + forms[0].getQuantity());
		return forms;
  }

  /**
   * エージェントのシステムパラメータを設定します．
   * @param args システムパラメータ
   */
  public void setParameters(String[] args) {
  	super.setParameters(args);
		for(int i = 0; i < args.length; ++i){
			String[] strArray = args[i].split("=");
			String key = strArray[0];
			String value = strArray[1];
			if(key.equals(UMovingAverageAgent.SHORT_TERM_KEY)){
				fShortTerm = Integer.parseInt(value);
				println("ShortTerm has been changed to " + fShortTerm);
			}else if(key.equals(UMovingAverageAgent.MEDIUM_TERM_KEY)){
				fMediumTerm = Integer.parseInt(value);
				println("MediumTerm has been changed to " + fMediumTerm);
			}else if(key.equals(UMovingAverageAgent.MIN_QUANT_KEY)){
				fMinQuant = Integer.parseInt(value);
				println("MinQuant has been changed to " + fMinQuant);
			}else if(key.equals(UMovingAverageAgent.MAX_QUANT_KEY)){
				fMaxQuant = Integer.parseInt(value);
				println("MaxQuant has benn changed to " + fMaxQuant);
			}else if(key.equals(UMovingAverageAgent.MAX_POSITION_KEY)){
				fMaxPosition = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fMaxPosition);
			}else{
				println("Unknown parameter: " + key
						+ " in UMovingAverageAgent.setParameters");
			}
		}
  }
  /**
	 * term 節-移動平均値を計算して返すメソッド
	 * @param prices
	 * @param term
	 * @return
	 */
	double calculateMovingAverage(int[] prices, int term){
		double sum = 0.0;
		for(int i = 0; i < term; ++i){
			if(prices[prices.length - 1 - i] < 0){
				return (double)UOrderForm.INVALID_PRICE;
			}
			sum += (double)prices[prices.length - 1 - i];
		}
		return sum / (double)term;
	}

	double calculateWeightedMovingAverage(int[] prices, int term){
		double sum = 0.0;

		for(int i = 0; i < term; ++i){
			if(prices[prices.length - 1 - i] < 0){
				return (double)UOrderForm.INVALID_PRICE;
			}
			if(term == fShortTerm){
				sum += (double)prices[prices.length - 1 - i] * WEIGHT_VECTOR_SHORT[i];
			}
			if(term == fMediumTerm){
				sum += (double)prices[prices.length - 1 - i] * WEIGHT_VECTOR_MEDIUM[i];
			}
		}
		if(term == fShortTerm){
			return sum / 21.0;
		}
		if(term == fMediumTerm){
			return sum / 1105.0;
		}
		return -1.0;
	}

	/**
	 * 注文区分を決定して返すメソッド
	 * @param prices
	 * @return
	 */
	int chooseAction(int[] prices){
		int action = UOrderForm.NONE;

		int[] previousPrices = Arrays.copyOfRange(prices, 1, prices.length);

		// 移動平均の計算
		fPreviousMediumTermMovingAverage
			= calculateWeightedMovingAverage(previousPrices, fMediumTerm);
		fPreviousShortTermMovingAverage
			= calculateWeightedMovingAverage(previousPrices, fShortTerm);
		double currentMediumTermMovingAverage
			= calculateWeightedMovingAverage(prices, fMediumTerm);
		double currentShortTermMovingAverage
			= calculateWeightedMovingAverage(prices, fShortTerm);

		// 移動平均が計算できない場合
		if (fPreviousMediumTermMovingAverage == (double)UOrderForm.INVALID_PRICE
			|| fPreviousShortTermMovingAverage == (double)UOrderForm.INVALID_PRICE
			|| currentMediumTermMovingAverage == (double)UOrderForm.INVALID_PRICE
			|| currentShortTermMovingAverage == (double)UOrderForm.INVALID_PRICE){
			return UOrderForm.NONE;
		}
		// 注文区分を選択する
		if ((fPreviousShortTermMovingAverage < fPreviousMediumTermMovingAverage)
				&& (currentShortTermMovingAverage < currentMediumTermMovingAverage)) {
			action = UOrderForm.BUY;
		}else if ((fPreviousShortTermMovingAverage > fPreviousMediumTermMovingAverage)
				&& (currentShortTermMovingAverage > currentMediumTermMovingAverage)) {
			action = UOrderForm.SELL;
		}else{
			action = UOrderForm.NONE;
		}
		return action;
	}

	/**
	 * 注文区分action と先物価格系列prices から注文価格を決定し，返します．
	 * @param action
	 * @param prices
	 * @return
	 */
	int determinPrice(int action, int[] prices){
		Random rand = getRandom();
		// 現在と1節前の価格差
		int priceDiff = prices[0] - prices[1];
		// 注文価格
		int price = UOrderForm.INVALID_PRICE;
		switch(action){
		// 注文区分が「買い」の場合
			case UOrderForm.BUY:
				price = prices[0]
						+ (int)(priceDiff + 0.25 * (double)priceDiff * rand.nextGaussian());
				break;
		// 注文区分が「売り」の場合
			case UOrderForm.SELL:
				price = prices[0]
						- (int)(priceDiff + 0.25 * (double)priceDiff * rand.nextGaussian());
				break;
			default:
				break;
		}
		if(price <= 0){
			price = 1;
		}
		return price;
	}
}
