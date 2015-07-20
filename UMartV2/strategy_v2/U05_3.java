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
public class U05_3 extends UAgent {

	/** 注文価格の幅のデフォルト値 */
  public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** 注文数量の最大値のデフォルト値 */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** 注文数量の最小値のデフォルト値 */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** 売/買ポジションの最大値のデフォルト値 */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** 直近の価格が得られないときに利用する価格のデフォルト値 */
  public static final int DEFAULT_NOMINAL_PRICE = 3000;

  /** 注文価格の幅 */
  private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE;

  /** 注文数量の最大値 */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** 注文数量の最小値 */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** 売/買ポジションの最大値 */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** 直近の価格が得られないときに利用する価格 */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

	/** 注文価格のプロパティ名(WidthOfPrice) */
	public static final String WIDTH_OF_PRICE_KEY = "WidthOfPrice";

	/** 注文数量の最大値のプロパティ名(MaxQuant) */
	public static final String MAX_QUANT_KEY = "MaxQuant";

	/** 注文数量の最小値のプロパティ名(MinQuant) */
	public static final String MIN_QUANT_KEY = "MinQuant";

	/** 売/買ポジションの最大値のプロパティ名(MaxPosition) */
	public static final String MAX_POSITION_KEY = "MaxPosition";

	/** 直近の価格が得られないときに利用する価格のプロパティ名(NominalPrice) */
	public static final String NOMINAL_PRICE_KEY = "NominalPrice";


  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U05_3(String loginName, String passwd, String realName, int seed) {
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

		Random rand = getRandom();
  	UOrderForm[] forms = new UOrderForm[1];
  	forms[0] = new UOrderForm();
  	int[] prices = futurePrices;
  	int[] sPrices = spotPrices;
  	
  	
  	//////////SFSpreadStrategy///////////////////
  	if(rand.nextInt(2) == 0)
  	{
      forms[0].setBuySell(chooseAction(prices, sPrices));
      println("");
      print("day=" + day + ", session=" + session
            + ", futures=" + prices[prices.length - 1]);
      
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
      
      forms[0].setPrice(determinePrice(prices, spotPrices));
      forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));
      
      print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
          + ", quantity=" + forms[0].getQuantity());
      
      
      return forms;
  	}
  	/////////RandomStrategy/////////////////////
  	else{
  		forms[0].setBuySell(rand.nextInt(2) + 1);
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
      int latestPrice = getLatestPrice(futurePrices);
      if (latestPrice == UOrderForm.INVALID_PRICE) {
        latestPrice = getLatestPrice(spotPrices);
      }
      if (latestPrice == UOrderForm.INVALID_PRICE) {
        latestPrice = fNominalPrice;
      }
      int price = latestPrice + (int)((double)fWidthOfPrice * rand.nextGaussian());
      if (price <= 0) {
      	price = 1;
      }
      forms[0].setPrice(price);
      forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));
      println("day=" + day + ", session=" + session + ", latestPrice="  + latestPrice
      		    + ", " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
              + ", quantity=" + forms[0].getQuantity());
      return forms;
  	}
  }
	
	public int chooseAction(int[] prices, int[] spotPrices)
	{
		
		Random rand = getRandom();
		int action = UOrderForm.NONE;
		int spreadRetio = (prices[prices.length-1] - spotPrices[spotPrices.length-1])/spotPrices[spotPrices.length-1];
		int random = (rand.nextInt(5) + 1)/100;
		
		if(spreadRetio <= -random)
		{
			return UOrderForm.SELL;
			
		}
		else if(spreadRetio >= random)
		{
			return UOrderForm.BUY;
		}
		
		return action;
	}
	
	public int determinePrice(int[] prices, int[] spotPrices)
	{
		Random rand = getRandom();
		int minePrice = (prices[prices.length-1] + spotPrices[spotPrices.length-1])/2;
		int sigma = Math.abs(prices[prices.length-1] - spotPrices[spotPrices.length-1])/2;
		int random = rand.nextInt(15) + 1;
		
		return minePrice + sigma*random;
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
