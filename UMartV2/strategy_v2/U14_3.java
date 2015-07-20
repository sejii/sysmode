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
 * memo sasaki@ic.dis.titech.ac.jp
 */
public class U14_3 extends UAgent {

	//ここにフィールド変数を定義してください．
	private int fShortTerm;
	private int fMediumTerm;
	private int fMaxPosition;

	public static final int DEFAULT_SHORT_TERM=8;
	public static final int DEFAULT_MEDIUM_TERM=16;
	public static final int DEFAULT_MIN_QUANT =10;
	public static final int DEFAULT_MAX_QUANT=50;
	public static final int DEFAULT_MAX_POSITION=300;
	public static final int DEFAULT_RSI_TERM = 8;
	private int fRSITerm=DEFAULT_RSI_TERM;
	 /** 注文数量の最大値 */
  private int fMaxQuant = DEFAULT_MAX_QUANT;
  /** 注文数量の最小値 */
  private int fMinQuant = DEFAULT_MIN_QUANT;
	public static final String SHORT_TERM_KEY="ShortTerm";
	public static final String MIN_MIDIUM_TERM_KEY ="MediumTerm";
	public static final String MIN_QUANT_KEY="MinQuant";
	public static final String MAX_QUANT_KEY="MaxQuant";
	public static final String MAX_POSITION_KEY="MaxPosition";
  public static final String RSI_TERM_KEY = "RSITerm";

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U14_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//ここでフィールド変数の値を初期化してください．

	}
	/**
	 * 短期の節数を返します．
	 * @return 短期の節数
	 */
	public int getShortTerm() {
		return fShortTerm;
	}

	/**
	 * 中期の節数を返します．
	 * @return 中期の節数
	 */
	public int getMediumTerm() {
		return fMediumTerm;
	}

	/**
	 * 最小注文数量を返します．
	 * @return 最小注文数量
	 */
	public int getMinQuant() {
		return fMinQuant;
	}

	/**
	 * 最大注文数量を返します．
	 * @return 最大注文数量
	 */
	public int getMaxQuant() {
		return fMaxQuant;
	}

	/**
	 * 最大ポジションを返します．
	 * @return 最大ポジション
	 */
	public int getMaxPosition() {
		return fMaxPosition;
	}
	/**
	 * RSIの節数を返します．
	 * @return 最大ポジション
	 */
	public int getRSITerm() {
		return fRSITerm;
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
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]+", futuresPrice"+futurePrices[futurePrices.length-1]);
		println("spot size:" +spotPrices.length+", futures size:"+futurePrices.length);

		// 注文区分を「何もしない」に設定した注文票を作成して返す．
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		Random rand = getRandom();
		int[] prices =futurePrices;
		int randomnum = rand.nextInt(2);
		if(randomnum==0)
		forms[0].setBuySell(chooseAction(prices));
		if(randomnum==1)
		forms[0].setBuySell(chooseAction1(prices));


		if(day==1){ //1日目は何もしない
			forms[0].setBuySell(UOrderForm.NONE);
		}

    println("");
    print("day=" + day + ", session=" + session
          + ", futures=" + prices[prices.length - 1]
          + ", spot="+spotPrices[spotPrices.length-1]);

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

    //注文価格は2パターン
    randomnum = rand.nextInt(2);
    //1節前の現物価格
    if(randomnum==0)
    	forms[0].setPrice(spotPrices[spotPrices.length-2]);
    //determinePriceメソッドを使用
    if(randomnum==0)
    	forms[0].setPrice(determinePrice(forms[0].getBuySell(), prices));

    print("fMax"+fMaxQuant+"  fMin"+fMinQuant);
    forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));
    print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
           + ", quantity=" + forms[0].getQuantity());

    return forms;
  }

  /**
   * 注文価格を決定して返します．
   * @param action 売買区分
   * @param prices 価格系列．ただし，prices[prices.length]を直近としてください．
   * @return 注文価格
   */
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


  /**
   * 売買区分を選んで返します．
   * @param prices 価格系列．ただし，prices[prices.length]を直近としてください．
   * @return 売買区分
   */


//   RSI=upSum/(upSum+downSum) upSumは上昇したときの価格変動の合計、
//  downSumは下降したときの価格変動の合計
//
  private double calculateRSI(int[] prices, int term) {
  	double RSI_up = 0.0;
  	double RSI_down = 0.0;
  	for (int i = 0; i < term; ++i) {
  		if (prices[prices.length - 1 - i] < 0) {
  			return (double)UOrderForm.INVALID_PRICE;
  		}
  		else if (prices[prices.length - 1 - i] - prices[prices.length - 1 - term] > 0){
  			RSI_up += prices[prices.length - 1 - i] - prices[prices.length - 1 - term -1];
  		}
  		else if (prices[prices.length - 1 - i] - prices[prices.length - 1 - term] < 0){
  			RSI_down += Math.abs(prices[prices.length - 1 - i] - prices[prices.length - 1 - term -1]);
  		}
  	}
  	return RSI_up / (RSI_up + RSI_down);
  }
//  RSIが上限値よりも高ければ売り注文、RSIが下限値よりも低ければ買い注文
  private int chooseAction(int[] prices) {
  	int action = UOrderForm.NONE;
  	double RSI = calculateRSI(prices, fRSITerm);
  	if (RSI < 0.15) {
  			action = UOrderForm.BUY;
  		} else if (RSI > 0.85) {
  			action = UOrderForm.SELL;
  		}
  	return action;
  }

  public int chooseAction1(int[] prices){
		int action = UOrderForm.NONE;
		if(prices[prices.length - 3] > prices[prices.length - 2]
				&& prices[prices.length - 2] < prices[prices.length - 1]){
			action = UOrderForm.BUY;
		}else if (prices[prices.length - 3] < prices[prices.length - 2]
				&& prices[prices.length - 2] > prices[prices.length - 1]){
			action = UOrderForm.SELL;
		}

		return action;
	}

  /**
   * エージェントのシステムパラメータを設定します．
   * @param args システムパラメータ
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //このメソッドをオーバーライドする必要はありません．
    for (int i = 0; i < args.length; ++i) {
    	String[] strArray = args[i].split("=");
      String key = strArray[0];
      String value = strArray[1];
      if (key.equals(U14_3.SHORT_TERM_KEY)) {
        fShortTerm = Integer.parseInt(value);
        println("ShortTerm has been changed to " + fShortTerm);
      } else if (key.equals(U14_3.MIN_QUANT_KEY)) {
        fMinQuant = Integer.parseInt(value);
        println("MinQuant has been changed to " + fMinQuant);
      } else if (key.equals(U14_3.MAX_QUANT_KEY)) {
        fMaxQuant = Integer.parseInt(value);
        println("MaxQuant has been changed to " + fMaxQuant);
      } else if (key.equals(U14_3.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else if (key.equals(U14_3.RSI_TERM_KEY)) {
        fRSITerm = Integer.parseInt(value);
        println("RSITerm has been changed to " + fRSITerm);
      } else {
      	println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
      }
    }
  }
 }
