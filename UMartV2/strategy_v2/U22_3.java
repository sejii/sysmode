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
 * 先物価格を用いる移動平均エージェントクラスです．
 */
public class U22_3 extends UAgent {

	/** 短期の節数のデフォルト値 */
	public static final int DEFAULT_SHORT_TERM = 8;

	/** 中期の節数のデフォルト値 */
	public static final int DEFAULT_MEDIUM_TERM = 16;

  /** 注文数量の最大値のデフォルト値 */
  public static final int DEFAULT_MAX_QUANT = 400;

  /** 注文数量の最小値のデフォルト値 */
  public static final int DEFAULT_MIN_QUANT = 50;

  /** 売/買ポジションの最大値のデフォルト値 */
  public static final int DEFAULT_MAX_POSITION = 2000;

	/** 短期の節数 */
	private int fShortTerm = DEFAULT_SHORT_TERM;

	/** 中期の節数 */
	private int fMediumTerm = DEFAULT_MEDIUM_TERM;

  /** 注文数量の最大値 */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** 注文数量の最小値 */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** 売/買ポジションの最大値 */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** 短期の節数のプロパティ名(ShortTerm) */
  public static final String SHORT_TERM_KEY = "ShortTerm";

  /** 中期の節数のプロパティ名(MediumTerm) */
  public static final String MEDIUM_TERM_KEY = "MediumTerm";

  /** 注文数量の最大値のプロパティ名(MaxQuant) */
	public static final String MAX_QUANT_KEY = "MaxQuant";

	/** 注文数量の最小値のプロパティ名(MinQuant) */
	public static final String MIN_QUANT_KEY = "MinQuant";

	/** 売/買ポジションの最大値のプロパティ名(MaxPosition) */
	public static final String MAX_POSITION_KEY = "MaxPosition";

  /** 1節前における短期移動平均値 */
	private double fPreviousShortTermMovingAverage = UOrderForm.INVALID_PRICE;

	/** 1節前における中期移動平均値 */
	private double fPreviousMediumTermMovingAverage = UOrderForm.INVALID_PRICE;

//	private double fBuyPrices;

	private int stop_flag1;
	private int stop_flag2;
	private int stop_flag3;

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U22_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
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
   * 注文票を作成します．
   * デフォルトでは「注文しない」注文票を返すだけなので，子クラスで必ずオーバーライドしてください．
   * @param day 日
   * @param session 節
   * @param maxDays 取引日数
   * @param noOfSessionsPerDay 1日の節数
   * @param spotPrices 現物価格系列．spotPrices[0]からspotPrices[119]までの120節分のデータが格納されています．spotPrices[119]が直近の価格です．ただし，価格が成立していない場合，-1が入っているので注意してください．
   * @param futurePrices 先物価格系列．futurePrices[0]からfuturePrices[59]までの60節分のデータが格納されています．futurePrices[59]が直近の価格です．ただし，価格が成立していない場合，-1が入っているので注意してください．また，取引開始節より前は現物価格が格納されています．
   * @param position ポジション．正ならば買い越し(ロング・ポジション)，負ならば売り越し（ショート・ポジション）を表します．
   * @param money 現金残高．型がlongであることに注意．
   * @return UOrderForm[] 注文票の配列
   */
  public UOrderForm[] makeOrderForms(int day, int session,
                                      int maxDays, int noOfSessionsPerDay,
                                      int[] spotPrices, int[] futuresPrices,
                                      int position, long money) {
  	Random rand = getRandom();
  	UOrderForm[] forms = new UOrderForm[1];
  	forms[0] = new UOrderForm();
  	int[] prices = futuresPrices;


  	int test;


//------------------------------------------------------------------売り買い判定
  	if(day==1&&session==1){
  		forms[0].setBuySell(1);
  		test=0;
  		stop_flag1=0;
      stop_flag2=0;
  	}else{
  		if(stop_flag1==0||stop_flag2==0||stop_flag2==1){
  			if(Math.abs(position)<1000&&stop_flag1==0){
  				forms[0].setBuySell(1);	//2000売るまで売り続ける
  				test=1;
  			}else{
  				forms[0].setBuySell(chooseAction(prices));
  				stop_flag1=1;
  				test=2;
  				if(chooseAction(prices)==1&&position<0&&stop_flag2==0){
  					forms[0].setBuySell(UOrderForm.NONE);//positinoが負の時は売りはしない
  					return forms;
  				}else{
  					stop_flag2=1;
  				}
  				if(position>0){
  					stop_flag2=2;
  				}
  			}
  		}else{
  			forms[0].setBuySell(chooseAction(prices));
  			test=3;
  			fMaxQuant=50;
  			fMinQuant=10;
  		}
  	}



  //------------------------------------------------------------------売り買い判定終わり


    println("");
    print("day=" + day + ", session=" + session
          + ", futures=" + prices[prices.length - 1]
          + ", shortTerm=" + fPreviousShortTermMovingAverage
          + ", mediumTerm=" + fPreviousMediumTermMovingAverage
          + ", test=" + test
          +",position=" + position);



//    int test1=0;

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




    if(day==1&&session==1){
    	forms[0].setPrice(spotPrices[spotPrices.length-1]-50);
      forms[0].setQuantity(2000);
      stop_flag3=0;
    }else{
  		if(Math.abs(position)<1000&&stop_flag3==0){
  			forms[0].setPrice(spotPrices[spotPrices.length-1]-50);
        forms[0].setQuantity(2000-Math.abs(position));
  		}else{
  			forms [0]. setPrice ( determinePrice(forms[0].getBuySell(), prices) );
    		forms [0]. setQuantity ( fMinQuant + rand . nextInt ( fMaxQuant - fMinQuant + 1));
    		stop_flag3=1;
  		}
    }


    print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
           + ", quantity=" + forms[0].getQuantity()
           );
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
  private int chooseAction(int[] prices) {
  	int action = UOrderForm.NONE;
  	double shortTermMovingAverage = calculateMovingAverage(prices, fShortTerm);
  	double mediumTermMovingAverage = calculateMovingAverage(prices, fMediumTerm);
  	if (fPreviousShortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& fPreviousMediumTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& shortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& mediumTermMovingAverage != UOrderForm.INVALID_PRICE) {
  		if (fPreviousShortTermMovingAverage-2 < fPreviousMediumTermMovingAverage
  				&& mediumTermMovingAverage-2 < shortTermMovingAverage) {
  			action = UOrderForm.BUY;
  		} else if (fPreviousMediumTermMovingAverage < fPreviousShortTermMovingAverage-2
  				        && shortTermMovingAverage < mediumTermMovingAverage-2) {
  			action = UOrderForm.SELL;
  		}
  	}
  	fPreviousShortTermMovingAverage = shortTermMovingAverage;
  	fPreviousMediumTermMovingAverage = mediumTermMovingAverage;
  	return action;
  }

  /**
   * 直近からterm節分の価格系列の移動平均値を計算して返します．
   * ただし，価格が成立していない場合は，UOrderForm.INVALID_PRICE (=-1)を返します．
   * @param prices 価格系列．ただし，prices[prices.length - 1]を直近としてください．
   * @param term 移動平均をとる期間
   * @return 直近からterm節分の価格系列の移動平均値
   */
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
