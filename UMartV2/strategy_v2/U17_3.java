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
 * 独自エージェント用のテンプレートです．
 * このエージェントのコピーを作成して，名前をU<学籍番号>に修正して下さい．
 */
public class U17_3 extends UAgent {
	public static final int DEFAULT_SHORT_TERM = 4;
	public static final int DEFAULT_MEDIUM_TERM = 8;
	public static final int DEFAULT_MAX_QUANT = 100;
	public static final int DEFAULT_MIN_QUANT = 10;
	public static final int DEFAULT_MAX_POSITION = 300;
	private int fShortTerm = DEFAULT_SHORT_TERM ;
	private int fMediumTerm = DEFAULT_MEDIUM_TERM ;
	private int fMaxQuant = DEFAULT_MAX_QUANT ;
	private int fMinQuant = DEFAULT_MIN_QUANT ;
	private int fMaxPosition = DEFAULT_MAX_POSITION ;

	public static final String SHORT_TERM_KEY = "ShortTerm" ;
	public static final String MEDIUM_TERM_KEY = "MediumTerm";
	public static final String MAX_QUANT_KEY = "MaxQuant";
	public static final String MIN_QUANT_KEY = "MinQuant";
	public static final String MAX_POSITION_KEY = " MaxPosition " ;

	private double fPreviousShortTermMovingAverage = UOrderForm . INVALID_PRICE ;
	private double fPreviousMediumTermMovingAverage = UOrderForm . INVALID_PRICE ;

	//ここにフィールド変数を定義してください．

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U17_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//ここでフィールド変数の値を初期化してください．

	}
public int getMinQuant(){
	return fMinQuant;
}
public int getMaxQuant(){
	return fMaxQuant;
}
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
                                      int[] spotPrices, int[] futurePrices,
                                      int position, long money) {

		// 現在の日，現在の節，現在の現物価格を表示する．


		// 注文区分を「何もしない」に設定した注文票を作成して返す．
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		int[] prices = futurePrices;
    forms[0].setBuySell(chooseAction(prices));
    println(" ");
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
	private int determineQuantity(int[]prices, int action){
		Random rand = getRandom();
		double shortTerm = calculateMovingAverage(prices, fShortTerm);
		double mediumTerm = calculateMovingAverage(prices, fMediumTerm);
		//int quantity = fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1 );
		int quantity = 0;
		if (action == UOrderForm.BUY){
			if (shortTerm > mediumTerm+10){
				quantity = fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1 ) + 50;
			}else{
				quantity = fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1 );
			}
		}
		 if(action == UOrderForm.SELL){
			if(shortTerm+10 < mediumTerm){
				quantity = fMinQuant + rand.nextInt(100) + 50;
			}else{
				quantity = fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1 );
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
  private int chooseAction(int[] prices) {
  	int action = UOrderForm.NONE;
  	double shortTermMovingAverage = calculateMovingAverage(prices, fShortTerm);
  	double mediumTermMovingAverage = calculateMovingAverage(prices, fMediumTerm);
  	if (fPreviousShortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& fPreviousMediumTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& shortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& mediumTermMovingAverage != UOrderForm.INVALID_PRICE) {
  		if ( mediumTermMovingAverage < shortTermMovingAverage) {
  			action = UOrderForm.BUY;
  		} else if ( shortTermMovingAverage < mediumTermMovingAverage) {
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
   * エージェントのシステムパラメータを設定します．
   * @param args システムパラメータ
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
