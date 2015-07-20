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
 * 独自エージェント用のテンプレートです．
 * このエージェントのコピーを作成して，名前をU<学籍番号>に修正して下さい．
 */
public class U20_1 extends UAgent {

	//ここにフィールド変数を定義してください．

  /** 注文価格の幅のデフォルト値 */
  public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** 注文量の最大値のデフォルト値 */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** 注文量の最小値のデフォルト値 */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** ポジションの最大値のデフォルト値 */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** 乖離比の閾値のデフォルト値 */
  public static final double DEFAULT_SPREAD_RATIO_THRESHOLD = 0.01;

  /** 注文量の最大値 */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** 注文量の最小値 */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** ポジションの最大値 */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U20_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//ここでフィールド変数の値を初期化してください．
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
		//println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + ", futures=" + futurePrices[futurePrices.length - 1]);
		
		// 注文区分を「何もしない」に設定した注文票を作成して返す．
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
   * エージェントのシステムパラメータを設定します．
   * @param args システムパラメータ
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //このメソッドをオーバーライドする必要はありません．
  }
}



















