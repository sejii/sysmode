package strategy_v2;


/**
 * 独自エージェント用のテンプレートです．
 * このエージェントのコピーを作成して，名前をU<学籍番号>に修正して下さい．
 */


import java.util.Random;

public class U12_2 extends UAgent {

	//ここにフィールド変数を定義してください．

	/** 短期の節数のデフォルト値 */
	public static final int DEFAULT_SHORT_TERM = 6;

	/** 中期の節数のデフォルト値 */
	public static final int DEFAULT_MEDIUM_TERM = 12;

  /** 注文数量の最大値のデフォルト値 */
  public static final int DEFAULT_MAX_QUANT = 5000;

  /** 注文数量の最小値のデフォルト値 */
  public static final int DEFAULT_MIN_QUANT = 300;

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

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U12_2(String loginName, String passwd, String realName, int seed) {
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

		/****
		// 現在の日，現在の節，現在の現物価格を表示する．
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]);

		// 注文区分を「何もしない」に設定した注文票を作成して返す．
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
    forms[0].setBuySell(UOrderForm.NONE);
    return forms;
		****/

  	Random rand = getRandom();
  	UOrderForm[] forms = new UOrderForm[1];
  	forms[0] = new UOrderForm();
  	int[] prices = futurePrices;
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
    forms[0].setPrice(determinePrice(forms[0].getBuySell(), prices));
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
  private int chooseAction(int[] prices) {
  	int action = UOrderForm.NONE;
  	double shortTermMovingAverage = calculateMovingAverage(prices, fShortTerm);
  	double mediumTermMovingAverage = calculateMovingAverage(prices, fMediumTerm);
  	if (fPreviousShortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& fPreviousMediumTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& shortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& mediumTermMovingAverage != UOrderForm.INVALID_PRICE) {
  		if (fPreviousShortTermMovingAverage < fPreviousMediumTermMovingAverage
  				&& mediumTermMovingAverage < shortTermMovingAverage) {
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
    //このメソッドをオーバーライドする必要はありません．
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
