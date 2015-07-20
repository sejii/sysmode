package strategy_v2;

import java.util.Random;


public class U13_3 extends UAgent {

	/**デフォルト値の設定**/
  /* 注文数量の最大値のデフォルト値 */
  public static final int DEFAULT_MAX_QUANT = 200;
  /* 注文数量の最小値のデフォルト値 */
  public static final int DEFAULT_MIN_QUANT = 30;
  /* 直近の価格が得られないときに利用する価格のデフォルト値 */
  public static final int DEFAULT_NOMINAL_PRICE = 3000;
	/*売り買いポジションの最大値のデフォルト値*/
	public static final int DEFAULT_MAX_POSITION = 300;

	/*相対価値基準*/
	private double DEFAULT_RELATIVE_VALUE = 0.001;
	/*相対価値変動率基準*/
	private double DEFAULT_CHANGE_VALUE = 0.002;

	/* 注文数量の最大値のプロパティ名(MaxQuant) */
 	public static final String MAX_QUANT_KEY = "MaxQuant";
 	/* 注文数量の最小値のプロパティ名(MinQuant) */
 	public static final String MIN_QUANT_KEY = "MinQuant";
 	/* 売/買ポジションの最大値のプロパティ名(MaxPosition) */
 	public static final String MAX_POSITION_KEY = "MaxPosition";
	/* 直近の価格が得られないときに利用する価格のプロパティ名(NominalPrice) */
	public static final String NOMINAL_PRICE_KEY = "NominalPrice";

	/**フィールド変数を定義**/
  /* 注文数量の最大値 */
  private int fMaxQuant = DEFAULT_MAX_QUANT;
  /* 注文数量の最小値 */
  private int fMinQuant = DEFAULT_MIN_QUANT;
  /* 売/買ポジションの最大値 */
  private int fMaxPosition = DEFAULT_MAX_POSITION;
  /* 直近の価格が得られないときに利用する価格 */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

  /*前回の相対価値*/
  /*前々回の相対価値*/
  /*相対価値変動率*/
  /*加算価格*/
  /*差分価格*/

	/**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */

	public U13_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	/*getter/setter*/
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
	public int getMaxPosition(){
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

		// 現在の日，現在の節，現在の現物価格を表示する．
    println("");
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + ", futures=" + futurePrices[futurePrices.length - 1]);
		println("spot size:" + spotPrices.length + ", future size:" + futurePrices.length);

		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();

		/*注文区分を決定*/
		forms[0].setBuySell(chooseAction(spotPrices, futurePrices));

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

    /*注文価格を決定*/
    forms[0].setPrice(determinePrice(spotPrices, futurePrices));

    /*注文数量を決定*/
    forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));


    /*売り買いが行われれば、変化したことを出力する*/
    print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
        + ", quantity=" + forms[0].getQuantity());

    return forms;
  }


  /**売買区分を選んで返す*/
	private int chooseAction(int[] spotprices, int[] futureprices){
  	int action = UOrderForm.NONE;

		/*直近の先物価格と現物価格の差を、現物価格で割った値*/
		double nowRelativeValue = relativeValue(spotprices, futureprices, 0);
		/*前の先物価格と現物価格の差を、現物価格で割った値*/
		double previousRelativeValue = relativeValue(spotprices, futureprices, 1);
		/*相対的な価値の変化の値*/
		double changeValue = (nowRelativeValue - previousRelativeValue)/previousRelativeValue;

		if ( (nowRelativeValue <= -DEFAULT_RELATIVE_VALUE) && ( changeValue <= -DEFAULT_CHANGE_VALUE) ){
			action = UOrderForm.BUY;
		}else if ( (nowRelativeValue >= DEFAULT_RELATIVE_VALUE) && ( changeValue >= DEFAULT_CHANGE_VALUE) ){
			action = UOrderForm.SELL;
		}else {
			action = UOrderForm.NONE;
		}

		return action;
	}

  /**注文価格を決定して返す*/
	private int determinePrice(int[] spotprices, int[] futureprices) {
		int price = UOrderForm.INVALID_PRICE;
		price = (int)( ((double)spotprices[spotprices.length - 1] + (double)futureprices[futureprices.length - 1])/2
				+ ( ((double)spotprices[spotprices.length - 1] - (double)futureprices[futureprices.length - 1])/2 ) * getRandom().nextGaussian() );

  	if (price < 0) {
  		price = 1;
  	}

		return price;
	}


  /**売買区分を決定するのに必要な相対価値を決定して返す*/
  private double relativeValue(int[] spotprices, int[] futureprices, int i){
  	 double RELATIVE_VALUE = ((double)futureprices[futureprices.length - 1 - i] - (double)spotprices[spotprices.length -1 - i])/(double)spotprices[spotprices.length - 1 - i];
  	 return RELATIVE_VALUE;
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
      if (key.equals(UMovingAverageAgent.MIN_QUANT_KEY)) {
        fMinQuant = Integer.parseInt(value);
        println("MinQuant has been changed to " + fMinQuant);
      } else if (key.equals(UMovingAverageAgent.MAX_QUANT_KEY)) {
        fMaxQuant = Integer.parseInt(value);
        println("MaxQuant has been changed to " + fMaxQuant);
      } else if (key.equals(UMovingAverageAgent.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else if (key.equals(URandomAgent.NOMINAL_PRICE_KEY)) {
      	fNominalPrice = Integer.parseInt(value);
      	println("NominalPrice has been changed to " + fNominalPrice);
      } else {
      	println("Unknown parameter:" + key + " in U13_3_Strategy.setParameters");
      }
    }
  }
}
