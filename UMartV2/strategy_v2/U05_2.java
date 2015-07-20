package strategy_v2;

import java.util.Random;

public class U05_2 extends UAgent {

	/** 短期の節数のデフォルト値 */
	public static final int DEFAULT_SHORT_TERM = 8;

	/** 中期の節数のデフォルト値 */
	public static final int DEFAULT_MEDIUM_TERM = 16;

  /** 注文数量の最大値のデフォルト値 */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** 注文数量の最小値のデフォルト値 */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** 売/買ポジションの最大値のデフォルト値 */
  public static final int DEFAULT_MAX_POSITION = 300;

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


	public U05_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}


	//短期の節数を返す
	public int getShortTerm() {
		return fShortTerm;
	}

	//中期の節数を返す
	public int getMediumTerm() {
		return fMediumTerm;
	}

	//最小注文数量を返す
	public int getMinQuant() {
		return fMinQuant;
	}

	//最大注文数量を返す
	public int getMaxQuant() {
		return fMaxQuant;
	}

	//最大ポジションを返す
	public int getMaxPosition() {
		return fMaxPosition;
	}

  /**
   * 注文票を作成
   */
  public UOrderForm[] makeOrderForms(int day, int session,
                                      int maxDays, int noOfSessionsPerDay,
                                      int[] spotPrices, int[] futuresPrices,
                                      int position, long money) {
  	Random rand = getRandom();
  	UOrderForm[] forms = new UOrderForm[1];
  	forms[0] = new UOrderForm();
  	int[] prices = futuresPrices;
  	int[] prices2 = spotPrices;
    forms[0].setBuySell(chooseAction(prices));
    println("");
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
    forms[0].setPrice(determinePrice(forms[0].getBuySell(), prices, prices2));
    forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));
    print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
           + ", quantity=" + forms[0].getQuantity());
    return forms;
  }

  /**
   * 注文価格を決定して返す
   */
  private int determinePrice(int action, int[] prices, int[] prices2) {

  	int price = UOrderForm.INVALID_PRICE;
  	int widthOfPrice = Math.abs(prices[prices.length - 1] - prices[prices.length - 2]);
  	if (action == UOrderForm.BUY) {
      price = (Math.abs(prices[prices.length - 1] + prices2[prices2.length - 1]) / 2)  + ((prices[prices.length - 1] - prices2[prices2.length - 1]) / 2);
    } else if (action == UOrderForm.SELL) {
      price = (Math.abs(prices[prices.length - 1] + prices2[prices2.length - 1]) / 2)  + ((prices[prices.length - 1] - prices2[prices2.length - 1]) / 2);
    }
  	if (price < 0) {
  		price = 1;
  	}
  	return price;
  }

  /**
   * 売買区分を選んで返す
   */
  private int chooseAction(int[] prices) {
  	int action = UOrderForm.NONE;
  	double shortTermMovingAverage = calculateMovingAverage(prices, fShortTerm);
  	double mediumTermMovingAverage = calculateMovingAverage(prices, fMediumTerm);
  	if (fPreviousShortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& fPreviousMediumTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& shortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& mediumTermMovingAverage != UOrderForm.INVALID_PRICE) {
  		if (fPreviousShortTermMovingAverage < fPreviousMediumTermMovingAverage
  				|| mediumTermMovingAverage < shortTermMovingAverage) {
  			action = UOrderForm.BUY;
  		} else if (fPreviousMediumTermMovingAverage < fPreviousShortTermMovingAverage
  				        && shortTermMovingAverage < mediumTermMovingAverage) {
  			action = UOrderForm.SELL;
  		}
  	}
  	fPreviousShortTermMovingAverage = shortTermMovingAverage;
  	fPreviousMediumTermMovingAverage = mediumTermMovingAverage;
  	return action;
  }

  /**
   * 直近からterm節分の価格系列の移動平均値を計算して返す
   * ただし，価格が成立していない場合は，UOrderForm.INVALID_PRICE (=-1)を返します．
   * @param prices 価格系列．
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
  	return sum * 1.2 / (double)term;
  }

  /**
   * エージェントのシステムパラメータ
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
