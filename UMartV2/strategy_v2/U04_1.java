
package strategy_v2;

import java.util.Random;

public class U04_1 extends UAgent {

	/** 平均取得のための節数のデフォルト値 */
	public static final int DFAULR_AVERAGE_TERM = 8;

  /** 注文数量の最大値のデフォルト値 */
  public static final int DEFAULT_MAX_QUANT = 100;

  /** 注文数量の最小値のデフォルト値 */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** 売/買ポジションの最大値のデフォルト値 */
  public static final int DEFAULT_MAX_POSITION = 300;

	/**平均取得のための節数 */
	private int fAverageTerm = DFAULR_AVERAGE_TERM;

  /** 注文数量の最大値 */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** 注文数量の最小値 */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** 売/買ポジションの最大値 */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** 平均取得のための節数のプロパティ名(ShortTerm) */
  public static final String AVERAGE_TERM_KEY = "AverageTerm";

  /** 注文数量の最大値のプロパティ名(MaxQuant) */
	public static final String MAX_QUANT_KEY = "MaxQuant";

	/** 注文数量の最小値のプロパティ名(MinQuant) */
	public static final String MIN_QUANT_KEY = "MinQuant";

	/** 売/買ポジションの最大値のプロパティ名(MaxPosition) */
	public static final String MAX_POSITION_KEY = "MaxPosition";

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U04_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	/**
	 * 平均取得のための節数を返します．
	 * @return 短期の節数
	 */
	public int getAverageTerm() {
		return fAverageTerm;
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
    forms[0].setBuySell(chooseAction(spotPrices, futuresPrices));
    println("");
    print("day=" + day + ", session=" + session
          + ", spots=" + spotPrices[spotPrices.length - 1] + ", futures=" + futuresPrices[futuresPrices.length - 1]);
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
    forms[0].setPrice(determinePrice(forms[0].getBuySell(), spotPrices, futuresPrices));
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
  private int determinePrice(int action, int[] tSpotPrices, int[] tFuturePrices) {
  	int price = UOrderForm.INVALID_PRICE;
  	double[] spotAvSd = new double[2];
  	spotAvSd = calculateAvSd(tSpotPrices, fAverageTerm);

  	//判断基準2のとき
  	if(tSpotPrices[tSpotPrices.length -1]<spotAvSd[0]-spotAvSd[1]){

  		int widthOfPrice = Math.abs(tSpotPrices[tSpotPrices.length -1]-(int)spotAvSd[0]);
  		return tSpotPrices[tSpotPrices.length -1] - widthOfPrice + (int)((double)widthOfPrice / 4.0 * getRandom().nextGaussian());

  	}else if(tSpotPrices[tSpotPrices.length -1]>spotAvSd[0]+spotAvSd[1]){

  		int widthOfPrice = Math.abs(tSpotPrices[tSpotPrices.length -1]-(int)spotAvSd[0]);
  		return tSpotPrices[tSpotPrices.length -1] + widthOfPrice + (int)((double)widthOfPrice / 4.0 * getRandom().nextGaussian());

  	}

  	//判断基準1のとき
  	if (action == UOrderForm.BUY) {

  		int widthOfPrice = Math.abs(tSpotPrices[tSpotPrices.length -1]-tFuturePrices[tFuturePrices.length -1]);
  		return tSpotPrices[tSpotPrices.length -1] - widthOfPrice + (int)((double)widthOfPrice / 4.0 * getRandom().nextGaussian());

    } else if (action == UOrderForm.SELL) {

    	int widthOfPrice = Math.abs(tSpotPrices[tSpotPrices.length -1]-tFuturePrices[tFuturePrices.length -1]);
  		return tSpotPrices[tSpotPrices.length -1] + widthOfPrice + (int)((double)widthOfPrice / 4.0 * getRandom().nextGaussian());


    }

  	if (price < 0) {
  		price = 1;
  	}

  	return price;
  }

  /**
   * 売買区分を選んで返します．
   * @param tSpotPrices 現物価格系列．tFururePrices 先物価格系列．ただし，prices[prices.length]を直近としてください．
   * @return 売買区分
   */
  private int chooseAction(int[] tSpotPrices, int[] tFuturePrices) {
  	int action = UOrderForm.NONE;
  	double[] spotAvSd = new double[2];
  	spotAvSd = calculateAvSd(tSpotPrices, fAverageTerm);

  	if (tSpotPrices[tSpotPrices.length -1] != UOrderForm.INVALID_PRICE
  			&& tFuturePrices[tFuturePrices.length -1] != UOrderForm.INVALID_PRICE){
  		if(tSpotPrices[tSpotPrices.length -1]<tFuturePrices[tFuturePrices.length -1]){
  			 action = UOrderForm.BUY;
  			 print(" => Evaluation criteria1");
  		}else{
  			action = UOrderForm.SELL;
 			 print(" => Evaluation criteria1");
  		}
  	}

  	if(spotAvSd[0] != UOrderForm.INVALID_PRICE && spotAvSd[1] != UOrderForm.INVALID_PRICE){
  		if(tSpotPrices[tSpotPrices.length -1]<spotAvSd[0]-spotAvSd[1]){
  			action = UOrderForm.BUY;
 			 print(" => Evaluation criteria2");
  		}
  		else if(tSpotPrices[tSpotPrices.length -1]>spotAvSd[0]+spotAvSd[1]){
  			action = UOrderForm.SELL;
 			 print(" => Evaluation criteria2");
  		}
  	}


  	return action;
  }

  /**
   * 直近からterm節分の現物価格系列の平均値と標準偏差を計算して返します．
   * ただし，価格が成立していない場合は，UOrderForm.INVALID_PRICE (=-1)を返します．
   * @param prices 現物価格系列．ただし，prices[prices.length - 1]を直近としてください．
   * @param term 平均をとる期間
   * @return double配列. 0成分：直近からterm節分の現物価格系列の平均値．1成分：標準偏差
   */
  private double[] calculateAvSd(int[] prices, int term) {
  	double sum = 0.0;
  	double sum2 = 0.0;
  	double[] AvSd = null;
  	AvSd = new double[2];
  	AvSd[0] = AvSd[1] = UOrderForm.INVALID_PRICE;

  	for (int i = 0; i < term; ++i) {
  		if (prices[prices.length - 1 - i] < 0) {
  			return AvSd;
  		}
  		sum += (double)prices[prices.length - 1 - i];
  		sum2 += (double)prices[prices.length - 1 - i]*(double)prices[prices.length - 1 - i];
  	}

  	AvSd[0] = sum / (double)term;
  	AvSd[1] = Math.sqrt(sum2/(double)term-AvSd[0]*AvSd[0]);
  	return AvSd;
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
      if (key.equals(U04_1.AVERAGE_TERM_KEY)) {
        fAverageTerm = Integer.parseInt(value);
        println("AverageTerm has been changed to " + fAverageTerm);
      }  else if (key.equals(U04_1.MIN_QUANT_KEY)) {
        fMinQuant = Integer.parseInt(value);
        println("MinQuant has been changed to " + fMinQuant);
      } else if (key.equals(U04_1.MAX_QUANT_KEY)) {
        fMaxQuant = Integer.parseInt(value);
        println("MaxQuant has been changed to " + fMaxQuant);
      } else if (key.equals(U04_1.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else {
      	println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
      }
    }
  }
}
