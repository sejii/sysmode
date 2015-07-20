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
public class U14_2 extends UAgent {

	//ここにフィールド変数を定義してください．

	/** 売/買ポジションの最大値のデフォルト値 */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** 取引間時間のデフォルト値(節数) */
  public static final int DEFAULT_TRADE_INTERVAL = 2;

  /** 使用する資金の割合のデフォルト値 */
  public static final double DEFAULT_BUDGET_PERCENTAGE = 0.0001;

  /** 開始直後の資金のデフォルト値 */
  public static final int DEFAULT_BUDGET = 1000000000;

  /** 前回の取引時の金額のデフォルト値 */
  public static final int DEFAULT_BEFORE_VALUE = 0;

  /** 損失限界割合のデフォルト値 */
  public static final double DEFAULT_LOSS_LIMIT_PERCENTAGE = 0.2;

  /** 市場価格が未定の時の注文価格のデフォルト値 */
  public static final int DEFAULT_NOMINAL_PRICE = 3000;

	public static final int SESSIONS_PER_DAY = 8;

  /** 売/買ポジションの最大値 */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** 取引間時間のデフォルト値(節数) */
  private int fTradeInterval = DEFAULT_TRADE_INTERVAL;

  /** 使用する資金の割合のデフォルト値 */
  private double fBudgetPercentage = 0.001;

  /** 開始直後の資金のデフォルト値 */
  private int fBudget = 1000000000;

  /** 前回の取引時の金額のデフォルト値 */
  private int fBeforeValue = UOrderForm.INVALID_PRICE;

  /** 損失限界割合のデフォルト値 */
  private double fLossLimitPercentage = 0.2;

  /** 市場価格が未定の時の注文価格のデフォルト値 */
  private int fNominalPrice = 3000;

  /** 前回の取引を行った節 */
	private int fBeforeSession = 0;


	//システムパラメータ名
  /** 取引間時間のプロパティ名 */
  public static final String TRADE_INTERVAL_KEY = "TradeInterval";

  /** 使用する資金の割合のプロパティ名 */
  public static final String BUDGET_PERCENTAGE_KEY = "BudgetPercentage";

  /** 開始直後の資金のプロパティ名 */
  public static final String BUDGET_KEY = "Budget";

  /** 前回の取引時の金額のプロパティ名 */
  public static final String BEFORE_VALUE_KEY = "BeforeValue";

  /** 損失限界割合のプロパティ名 */
  public static final String LOSS_LIMIT_PERCENTAGE_KEY = "LossLimitPercentage";

  /** 市場価格が未定の時の注文価格のプロパティ名 */
  public static final String NOMINAL_PRICE_KEY = "NominalPrice";

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U14_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

  //getter
	public int getfTradeInterval() {
		return fTradeInterval;
	}

	public double getfBudgetPercentage() {
		return fBudgetPercentage;
	}

	public int getfBudget() {
		return fBudget;
	}

	public int getfBeforeValue() {
		return fBeforeValue;
	}

	public double getfLossLimitPercentage() {
		return fLossLimitPercentage;
	}

	public int getfNominalPrice() {
		return fNominalPrice;
	}


	//注文区分を作成して返すメソッド
	//引数pricesは先物価格系列
	public int chooseAction(int[] prices, int day, int session){
		Random rand = getRandom();
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();

		//最初の一回の取引では注文区分をランダムに決定
		if(day == 1 && session == 1){
			forms[0].setBuySell(rand.nextInt(2) + 1);	//rand.nextInt(2) + 1：1(BUY)か2(SELL)を返す。
			fBeforeSession = session;			//fBeforeSessionを更新
		}else{	//初回以外の取引の場合
//			println("(fBeforeSession + SESSIONS_PER_DAY * (day - 1))=" + (fBeforeSession + SESSIONS_PER_DAY * (day - 1)) +
//					", (session + SESSIONS_PER_DAY * (day - 1))=" + (session + SESSIONS_PER_DAY * (day - 1)) +
//					", fTradeInterval=" + fTradeInterval);
			println("(session + SESSIONS_PER_DAY * (day - 1))=" + (session + SESSIONS_PER_DAY * (day - 1)) + "fBeforeSession" + fBeforeSession);
			if((session + SESSIONS_PER_DAY * (day - 1)) - fBeforeSession == fTradeInterval){	//前回の取引時からfTradeInterval節だけ時間が経っていたら(節数が日を跨いでもリセットされないように調整)
				println("注文区分を設定");
				if(fBeforeValue >= prices[prices.length - 1]){	//現在の現物価格が前回の取引価格より高かったら
					forms[0].setBuySell(UOrderForm.BUY);
				}else if(fBeforeValue < prices[prices.length - 1]){	//現在の現物価格が前回の取引価格より安かったら
					forms[0].setBuySell(UOrderForm.SELL);
				}else{	//現在の現物価格が前回の取引価格と同額だったら
					forms[0].setBuySell(UOrderForm.NONE);
				}
				fBeforeValue = prices[prices.length - 1];	//fBeforeValue, fBeforeSessionの更新
				fBeforeSession = (session + SESSIONS_PER_DAY * (day - 1));	//節数が日を跨いでもリセットされないように調整
			}else{	//前回の取引時から時間が経っていないなら
				forms[0].setBuySell(UOrderForm.NONE);
			}
		}
		return forms[0].getBuySell();	//決めた注文区分をreturn
	}

	//注文価格を決定
	public int determinePrice(int action, int[] prices){
		int price = 1;	//注文価格
		//直近の先物価格：prices[prices.length -1]。一節前の先物価格：prices[prices.length -2]
		/* 注文価格は、直近の現物価格を用いる。直近の現物価格が存在しない場合、市場価格が未定の時の注文価格を用いるものとする。 */
		if(prices[prices.length - 1] != UOrderForm.INVALID_PRICE){
			price = prices[prices.length - 1];
		}else{
			price = fNominalPrice;		//市場価格が未定の時の注文価格
		}
		return price;
	}

	//注文数量を決定
	//注文数量は、あらかじめ与えられた使用金額の割合ぶんの金額で買うことのできる最大の数とする。
	public int determineQuantity(int action, int price){
		int quantity = 10;	//注文数量
		int tradeValue = (int)(fBudget * fBudgetPercentage);	//今回の取引で使う金額
		quantity = tradeValue / price;
		return quantity;
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
                                      int[] spotPrices, int[] futuresPrices,
                                      int position, long money) {
//		// 注文区分を「何もしない」に設定した注文票を作成して返す．
//		UOrderForm[] forms = new UOrderForm[1];
//		forms[0] = new UOrderForm();
//    forms[0].setBuySell(UOrderForm.NONE);
//    return forms;
//		// 現在の日，現在の節，現在の現物価格を表示する．
//		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]
//				+ ", futures=" + futuresPrices[futuresPrices.length - 1]);

		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();

		/* 注文区分の決定 */
		forms[0].setBuySell(chooseAction(futuresPrices, day, session));	//先物価格系列から注文区分を決定

		if(forms[0].getBuySell() == UOrderForm.BUY){
			if(position > fMaxPosition){			//「買い」でロングで最大ポジションを超える-->強制的に注文区分をNONEに
				forms[0].setBuySell(UOrderForm.NONE);
				return forms;
			}
		}else if(forms[0].getBuySell() == UOrderForm.SELL){
			if(position < -fMaxPosition){			//「売り」でショートで最大ポジションを超える-->強制的に注文区分をNONEに
				forms[0].setBuySell(UOrderForm.NONE);
				return forms;
			}
		}
		/* 注文価格の決定 */
		forms[0].setPrice(determinePrice(forms[0].getBuySell(), futuresPrices));

		/* 注文数量の決定 */
		forms[0].setQuantity(determineQuantity(forms[0].getBuySell(), forms[0].getPrice()));

		println("day=" + day + ", session=" + session + ", latestPrice=" + getLatestPrice(futuresPrices)
				+ ", " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
				+ ", quantity=" + forms[0].getQuantity());
		return forms;	//作成した注文票を返す
  }

  /**
   * エージェントのシステムパラメータを設定します．
   * @param args システムパラメータ
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //このメソッドをオーバーライドする必要はありません．
		for(int i = 0; i < args.length; ++i){
			String[] strArray = args[i].split("=");	// =が分割記号
			String key = strArray[0];	//パラメータ名(key)と値(value)のトークンに分割して代入
			String value = strArray[1];
			//パラメータ名にしたがってvalueをint型に変換して対応する変数へ代入
			if(key.equals(U14_2.TRADE_INTERVAL_KEY)){	//文字列比較：==不可。Stringクラスのequalsメソッド使用
				fTradeInterval = Integer.parseInt(value);
				println("ShortTerm has been changed to " + fTradeInterval);
			}else if(key.equals(U14_2.BUDGET_PERCENTAGE_KEY)){	//文字列比較：==不可。Stringクラスのequalsメソッド使用
				fBudgetPercentage = Integer.parseInt(value);
				println("MediumTerm has been changed to " + fBudgetPercentage);
			}else if(key.equals(U14_2.BUDGET_KEY)){
				fBudget = Integer.parseInt(value);
				println("MinQuant has been changed to " + fBudget);
			}else if(key.equals(U14_2.BEFORE_VALUE_KEY)){
				fBeforeValue = Integer.parseInt(value);
				println("MaxQuant has been changed to " + fBeforeValue);
			}else if(key.equals(U14_2.LOSS_LIMIT_PERCENTAGE_KEY)){
				fLossLimitPercentage = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fLossLimitPercentage);
			}else if(key.equals(U14_2.NOMINAL_PRICE_KEY)){
				fNominalPrice = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fNominalPrice);
			}else{
				println("Unknown parameter:" + key + "in U14_2.setParameters");
			}
		}
  }
}
