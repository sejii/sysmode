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
 * 買われすぎ/売られすぎの指標となるRSIと、移動平均から発展させたBollinger Bandを組み合わせたモデル
 * 爆発的に勝てないが、負けづらい。
 * しかしそれではコンテスト的に意味が無いので、購入量を増やしています。
 */
public class U23_3 extends UAgent {

	//ここにフィールド変数を定義してください．

	public static final int DEFAULT_RSI_TERM = 12; //RSI指数を計算するのに用いるタームのデフォルト
	public static final int DEFAULT_BOL_TERM = 21 ; //Bollinger Bandを計算するのに用いるタームのデフォルト
	public static final int DEFAULT_MAX_QUANT = 500; //最大取引量のデフォルト
	public static final int DEFAULT_MIN_QUANT = 200; //最小取引量のデフォルト
	public static final int DEFAULT_MAX_POSITION = 400; //最大ポジション数のデフォルト

	//以下フィールド変数にデフォルト値を割り当て
	private int fRsiTerm = DEFAULT_RSI_TERM;
	private int fBolTerm = DEFAULT_BOL_TERM;
	private int fMaxQuant= DEFAULT_MAX_QUANT;
	private int fMinQuant = DEFAULT_MIN_QUANT;
	private int fMaxPosition = DEFAULT_MAX_POSITION;

	//setParameterで用いるために変数名を割り当てておく
	public static final String RSI_TERM_KEY = "RsiTerm";
	public static final String BOL_TERM_KEY = "BolTerm";
	public static final String MAX_QUANT_KEY = "MaxQuant";
	public static final String MIN_QUANT_KEY = "MinQuant";
	public static final String Max_POSITION_KEY = "MaxPosition";

	private double fPreviousBollingerBandMinusTwoSigma = UOrderForm.INVALID_PRICE  ; //前のタームにおけるBollinger Bandの+2σの値
	private double fPreviousBollingerBandTwoSigma = UOrderForm.INVALID_PRICE ; //前のタームにおけるBollinger Bandの-2σの値
	private int fLastTradePrice = UOrderForm.INVALID_PRICE ; //最後に取引をした際の価格

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U23_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//ここでフィールド変数の値を初期化してください．

	}
	//フィールド変数のgetメソッド
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
                                      int[] spotPrices, int[] futurePrices,
                                      int position, long money) {

		// 現在の日，現在の節，現在の現物価格, 現在のポジション、現金の残量を表示する．
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]);
		println("spot size:" + spotPrices.length + ", futures size:" + futurePrices.length);
		println("Contemporary position:" + position + ", Contemporary money:" + money);

		Random rand = getRandom();
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();


		//持っているポジションにもかかわらず、価格が予想通りに動かなかった場合にはロスカットする
		if (position > 0){
			if (futurePrices[futurePrices.length - 1] <= fLastTradePrice ){
				//ロングかつ、現在の価格が最終取引価格を下回っていたら売却する
				forms[0].setBuySell(UOrderForm.SELL);
				forms[0].setPrice(determinePrice(forms[0].getBuySell(), futurePrices));
				forms[0].setQuantity(Math.abs(position));
				fLastTradePrice = UOrderForm.INVALID_PRICE; //売却してポジションが0に戻るので最終取引価格をINVALIDにする
				return forms;
			}
		}else if (position < 0){
			if (futurePrices[futurePrices.length - 1] >= fLastTradePrice ){
				//ショートかつ、現在の価格が最終取引価格を上回っていたら購入する
				forms[0].setBuySell(UOrderForm.BUY);
				forms[0].setPrice(determinePrice(forms[0].getBuySell(), futurePrices));
				forms[0].setQuantity(Math.abs(position));
				fLastTradePrice = UOrderForm.INVALID_PRICE; //購入してポジションが0に戻るので最終取引価格をINVALIDにする
				return forms;
			}
		}

		// ロスカットはしない場合はRSIとボリンジャーバンドに従って注文票を作成し返す。
    forms[0].setBuySell(chooseAction(futurePrices)); //行動の決定
    if (forms[0].getBuySell() == UOrderForm.NONE){ //何もしないならここでformを返して終了
    	return forms;
    }
    if (forms[0].getBuySell() == UOrderForm.BUY){ //購入する場合には、ポジションの量を調べて、超えていたら何もしない
    	if (position > fMaxPosition){
    		forms[0].setBuySell(UOrderForm.NONE);
    		return forms;
    	}
    }else if (forms[0].getBuySell() == UOrderForm.SELL){ //売却をする場合にも、同様にポジションの量を調べる
    	if (position < -fMaxPosition){
    		forms[0].setBuySell(UOrderForm.NONE);
    		return forms;
    	}
    }
    //ポジション量が大丈夫なら、価格と量を決定する
    forms[0].setPrice(determinePrice(forms[0].getBuySell(), futurePrices));//価格を決定
    fLastTradePrice = determinePrice(forms[0].getBuySell(), futurePrices); //購入価格をフィールド変数へ格納
    forms[0].setQuantity(determineQuantity(forms[0].getBuySell(), futurePrices, position)); //量を決定
    return forms; //フォームを返す
  }

	//価格を決定するメソッド
	//移動平均値と同様に決定する
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

  //購入量を決定するメソッド
  //ポジションがなかった場合には、最小値と最大値の間でランダムに決定する。
  //ポジションを持っていた場合には、そのポジションはすべて利確してしまい、追加でまた購入する。
  private int determineQuantity(int action, int[] prices, int position){
  	if(position == 0){
  		return fMinQuant + getRandom().nextInt(fMaxQuant - fMinQuant + 1);
  	}else{
  		return Math.abs(position) + fMinQuant + getRandom().nextInt(fMaxQuant - fMinQuant + 1);
  	}
  }

	//行動を決定するメソッド。
	//RSIが70以上かつボリンジャーバンドの2σトレンドから離れた場合には売却
	//RSIが30以下かつボリンジャーバンドの-2σトレンドから離れた場合には購入する
	private int chooseAction(int[] prices){
		int action = UOrderForm.NONE;
		double RsiValue = calculateRsi(prices, fRsiTerm);
		double MovingAverage = calculatePriceAverage(prices, fBolTerm);
		double TwoSigmaBollinger = UOrderForm.INVALID_PRICE;
		double TwoMinusSigmaBollinger = UOrderForm.INVALID_PRICE;
		//標準偏差がきちんと計算されるなら、BollingerBandを計算する
		if (calculateSigma(prices, fBolTerm) != UOrderForm.INVALID_PRICE){
			TwoSigmaBollinger = MovingAverage + 2 * calculateSigma(prices, fBolTerm);
			TwoMinusSigmaBollinger = MovingAverage - 2 * calculateSigma(prices, fBolTerm);
		}
		//用いる変数が有効化を確かめる
		if (fPreviousBollingerBandTwoSigma != UOrderForm.INVALID_PRICE
			&& fPreviousBollingerBandMinusTwoSigma != UOrderForm.INVALID_PRICE
			&& TwoSigmaBollinger != UOrderForm.INVALID_PRICE
			&& TwoMinusSigmaBollinger != UOrderForm.INVALID_PRICE
			&& MovingAverage != UOrderForm.INVALID_PRICE
			&& RsiValue != UOrderForm.INVALID_PRICE){
			//RSI値が70以上あるのかを確認する
			if(RsiValue > 70){
				//70以上あるうえで、今までBollinger bandの+2σに張り付いていたのが離れたら下がることが期待されるので売る
				if (prices[prices.length-1] < TwoSigmaBollinger && prices[prices.length-2] > fPreviousBollingerBandTwoSigma){
					action = UOrderForm.SELL;
				}else{
					action = UOrderForm.NONE;
				}
			//RSI値が30以下であるかを確認する
			}else if(RsiValue < 30){
				//30以下で、今までBollinger Bandの-2σに張り付いていたのが離れたら上がることが期待されるので買う
				if (prices[prices.length -1 ] > TwoSigmaBollinger && prices[prices.length - 2] < fPreviousBollingerBandMinusTwoSigma){
					action = UOrderForm.BUY;
				}else{
					action = UOrderForm.NONE;
				}
			}else{
				action = UOrderForm.NONE;
			}
		}
		//今回の結果をフィールド変数に記録する。
		fPreviousBollingerBandTwoSigma = TwoSigmaBollinger;
		fPreviousBollingerBandMinusTwoSigma = TwoMinusSigmaBollinger;
		return action;
	}

	//RSI指数を計算する。
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

	//価格の移動平均値を計算するメソッド。あとのボリンジャーバンドの値を計算するのに用いる。
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

	//ボリンジャーバンドで用いる価格の移動平均値の標準偏差を計算するメソッド。
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
   * エージェントのシステムパラメータを設定します．
   * @param args システムパラメータ
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
