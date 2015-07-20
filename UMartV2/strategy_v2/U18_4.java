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
 * 先物価格を用いるランダムエージェントクラスです．
 */
public class U18_4 extends UAgent {

  /** 注文価格の幅のデフォルト値 */
  //public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** 買い注文数量の最大値のデフォルト値 */
  public static final int DEFAULT_BUY_MAX_QUANT = 100;

  /** 売り注文数量の最大値のデフォルト値 */
  public static final int DEFAULT_SELL_MAX_QUANT = 150;

  /** 売/買ポジションの最大値のデフォルト値 */
  public static final int DEFAULT_MAX_POSITION = 2500;

  /** 直近の価格が得られないときに利用する価格のデフォルト値 */
  public static final int DEFAULT_NOMINAL_PRICE = 2100;

  /** 平均計算の重みのデフォルト値 */
  public static final double DEFAULT_WEIGHT = 20.0;

  /** 注文価格の幅 */
  //private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE;

  /** 買い注文数量の最大値 */
  private int fBuyMaxQuant = DEFAULT_BUY_MAX_QUANT;

  /** 売り注文数量の最大値 */
  private int fSellMaxQuant = DEFAULT_SELL_MAX_QUANT;

  /** 売/買ポジションの最大値 */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** 直近の価格が得られないときに利用する価格 */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

  /** 現物価格の平均値 */
  private double fAverage = (double)DEFAULT_NOMINAL_PRICE;

  /** 平均計算の重み */
  private double fWeight = DEFAULT_WEIGHT;

	/** 注文価格の幅のプロパティ名(WidthOfPrice) */
	public static final String WIDTH_OF_PRICE_KEY = "WidthOfPrice";

	/** 買い注文数量の最大値のプロパティ名(BuyMaxQuant) */
	public static final String BUY_MAX_QUANT_KEY = "BuyMaxQuant";

	/** 売り注文数量の最大値のプロパティ名(SellMaxQuant) */
	public static final String SELL_MAX_QUANT_KEY = "SellMaxQuant";

	/** 売/買ポジションの最大値のプロパティ名(MaxPosition) */
	public static final String MAX_POSITION_KEY = "MaxPosition";

	/** 直近の価格が得られないときに利用する価格のプロパティ名(NominalPrice) */
	public static final String NOMINAL_PRICE_KEY = "NominalPrice";

	/** 平均計算の重みのプロパティ名(Weight) */
	public static final String WEIGHT_KEY = "Weight";

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U18_4(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	/**
	 * 価格幅を返します．
	 * @return 価格幅
	 */
	/*public int getWidthOfPrice() {
		return fWidthOfPrice;
	}*/

	/**
	 * 売り最大注文数量を返します．
	 * @return 売り最大注文数量
	 */
	public int getSellMaxQuant() {
		return fSellMaxQuant;
	}

	/**
	 * 買い最大注文数量を返します．
	 * @return 買い最大注文数量
	 */
	public int getBuyMaxQuant() {
		return fBuyMaxQuant;
	}

	/**
	 * 最大ポジションを返します．
	 * @return 最大ポジション
	 */
	public int getMaxPosition() {
		return fMaxPosition;
	}

	/**
	 * 市場価格が未定のときの注文価格を返します．
	 * @return 市場価格が未定のときの注文価格
	 */
	public int getNominalPrice() {
		return fNominalPrice;
	}

	/**
	 * 平均計算の重みを返します．
	 * @return 平均計算の重み
	 */
	public double getWeight() {
		return fWeight;
	}

	/**
	 * 現物価格の平均値を返します．
	 * @return 市場価格が未定のときの注文価格
	 */
	public double getAverage() {
		return fAverage;
	}

  /**
   * 注文票を作成します．
   * デフォルトでは「注文しない」注文票を返すだけなので，子クラスで必ずオーバーライドしてください．
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
		int latestSpotPrice = spotPrices[spotPrices.length - 1];
    if (latestSpotPrice == UOrderForm.INVALID_PRICE) {
      latestSpotPrice = fNominalPrice;
    }
    fWeight += 1;
    fAverage += ((double)latestSpotPrice - fAverage)/fWeight;

  	UOrderForm[] forms = new UOrderForm[1];
  	forms[0] = new UOrderForm();
  	int price = 1;
  	if (latestSpotPrice >= fAverage) {
  		forms[0].setBuySell(UOrderForm.SELL);
    } else if (latestSpotPrice < fAverage & latestSpotPrice > 0) {
    	forms[0].setBuySell(UOrderForm.BUY);
    } else {
  		forms[0].setBuySell(UOrderForm.NONE);
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
  	if (forms[0].getBuySell() == UOrderForm.SELL) {
      	price = latestSpotPrice + 180 -
      			(int)((Math.log((double)(latestSpotPrice - 2099)))/Math.log(1.029));
        forms[0].setPrice(price);
        forms[0].setQuantity(fSellMaxQuant);
    } else {
      	price = latestSpotPrice - 130 +
      			(int)((Math.log((double)(2101 - latestSpotPrice)))/Math.log(1.04));
        forms[0].setPrice(price);
        forms[0].setQuantity(fBuyMaxQuant);
      }
    if (forms[0].getBuySell() == UOrderForm.BUY) {
      if (money < (forms[0].getPrice() + 3000000) * fBuyMaxQuant * 2) {
        forms[0].setBuySell(UOrderForm.NONE);
        return forms;
      }
    } else if (forms[0].getBuySell() == UOrderForm.SELL) {
      if (money < (forms[0].getPrice() + 3000000) * fSellMaxQuant) {
        forms[0].setBuySell(UOrderForm.NONE);
        return forms;
      }
    }
    println("day=" + day + ", session=" + session + ", latestPrice="  + latestSpotPrice
    		    + ", " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
            + ", quantity=" + forms[0].getQuantity() + ", position=" + position
            + ",average" + fAverage + ",weight" + fWeight);
    return forms;
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
      /*if (key.equals(SPAegnt.WIDTH_OF_PRICE_KEY)) {
        fWidthOfPrice = Integer.parseInt(value);
        println("WidthOfPrice has been changed to " + fWidthOfPrice);
      } else if (key.equals(SPAegnt.MIN_QUANT_KEY)) {
        fMinQuant = Integer.parseInt(value);
        println("MinQuant has been changed to " + fMinQuant);*/
      if (key.equals(U18_4.BUY_MAX_QUANT_KEY)) {
        fBuyMaxQuant = Integer.parseInt(value);
        println("BuyMaxQuant has been changed to " + fBuyMaxQuant);
      } else if (key.equals(U18_4.SELL_MAX_QUANT_KEY)) {
        fSellMaxQuant = Integer.parseInt(value);
        println("SellMaxQuant has been changed to " + fSellMaxQuant);
      } else if (key.equals(U18_4.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else if (key.equals(U18_4.NOMINAL_PRICE_KEY)) {
      	fNominalPrice = Integer.parseInt(value);
      	println("NominalPrice has been changed to " + fNominalPrice);
      } else if (key.equals(U18_4.WEIGHT_KEY)) {
      	fWeight = Integer.parseInt(value);
      	println("Weight has been changed to " + fWeight);
      } else {
      	println("Unknown parameter:" + key + " in SPAegnt.setParameters");
      }
    }
  }
}
