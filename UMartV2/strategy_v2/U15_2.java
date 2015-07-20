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
public class U15_2 extends UAgent {

	//ここにフィールド変数を定義してください．

	public static final int DEFAULT_WAIT = 3; // 待ち時間のデフォルト値（1〜5くらいが良いかも）

	/** 注文数量の最大値のデフォルト値 */
	public static final int DEFAULT_MAX_QUANT = 50;

	/** 注文数量の最小値のデフォルト値 */
	public static final int DEFAULT_MIN_QUANT = 10;

	/** 売/買ポジションの最大値のデフォルト値 */
	public static final int DEFAULT_MAX_POSITION = 300;

	private int fMaxData = UOrderForm.INVALID_PRICE; // 最大の値段

	private int fMinData = UOrderForm.INVALID_PRICE; // 最小の値段

	private int FirstFlag = 1; // 初回のフラグ

	private int fWaitTime = DEFAULT_WAIT; // 待ち時間

	/** 注文数量の最大値 */
	private int fMaxQuant = DEFAULT_MAX_QUANT;

	/** 注文数量の最小値 */
	private int fMinQuant = DEFAULT_MIN_QUANT;

	/** 売/買ポジションの最大値 */
	private int fMaxPosition = DEFAULT_MAX_POSITION;

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
	public U15_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//ここでフィールド変数の値を初期化してください．

	}

	public int getMaxData() { // 最大の値段を返す
		return fMaxData;
	}

	public int getMinData() { // 最小の値段を返す
		return fMinData;
	}

	public int getWaitTime() { // 待ち時間を返す
		return fWaitTime;
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

		Random rand = getRandom(); // ランダム使用準備
		UOrderForm[] forms = new UOrderForm[1]; // フォーム作成
		forms[0] = new UOrderForm(); // フォーム作成
		int[] prices = futurePrices; // 先物価格系列を入れる

		//futures[59]が直近の値、prices[prices.length - 1]にすればよい（-1には注意）

		/////////////////////////////////////デバッグ用
//		println("");
//		for(int i=0; i < 10; i++){
//			println("prices[" + i + "]" + prices[i]);
//		}
		/////////////////////////////////////デバッグ用

		if (FirstFlag == 1) { // 初回のみ最大・最少値の設定
			fMaxData = prices[0];
			fMinData = prices[0]; // 両方同じ値が入るが、chooseActionで片方変わる（？）
			FirstFlag = 0;
		}

		forms[0].setBuySell(chooseAction(prices)); // アクションを決める
		println("");
		print("day=" + day + ", session=" + session
				+ ", futures=" + prices[prices.length - 1]
						+ ", maxdata=" + fMaxData
						+ ", mindata=" + fMinData
						+ ", waittime=" + fWaitTime); // 日、セッション、直近の先物価格など表示

		if (fWaitTime > 0) { // 連続で売り買いしないように待ち時間をもつ
			fWaitTime--;
			forms[0].setBuySell(UOrderForm.NONE);
			return forms;
		}

		if (forms[0].getBuySell() == UOrderForm.NONE) { // 何もしない
			return forms;
		}
		if (forms[0].getBuySell() == UOrderForm.BUY) { // 買う
			if (position > fMaxPosition) {
				forms[0].setBuySell(UOrderForm.NONE);// 何もしない
				return forms;
			}
		} else if (forms[0].getBuySell() == UOrderForm.SELL) { // 売る
			if (position < -fMaxPosition) {
				forms[0].setBuySell(UOrderForm.NONE);	// 何もしない
				return forms;
			}
		}

		forms[0].setPrice(determinePrice(forms[0].getBuySell(), prices));

		/////////////////////////////////////////////////////////ここでキャンセルする時の要ちぇき
		if (forms[0].getPrice() == 1) { // 1ならキャンセル
			forms[0].setBuySell(UOrderForm.NONE);	// 何もしない
			return forms;
		}
		/////////////////////////////////////////////////////////

		forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));

		fWaitTime = DEFAULT_WAIT; // 待ち時間を戻す
		print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
				+ ", quantity=" + forms[0].getQuantity());
		return forms;

		//		// 現在の日，現在の節，現在の現物価格を表示する．
		//		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + ", futures=" + futurePrices[futurePrices.length -1]);
		//		println("spot size:" + spotPrices.length + ", futures size:" + futurePrices.length);
		//
		//		// 注文区分を「何もしない」に設定した注文票を作成して返す．
		//		UOrderForm[] forms = new UOrderForm[1];
		//		forms[0] = new UOrderForm();
		//    forms[0].setBuySell(UOrderForm.NONE);
		//    return forms;
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
			price = 1; // このせいでエラー警告文出る
		}

		return price;
	}

	private int chooseAction(int[] prices) { // 過去の最大値より大きい値なら売る、最小値より小さい値なら買う
		int action = UOrderForm.NONE;

		if (fMaxData < prices[prices.length - 1] && prices[prices.length - 1] != -1) {
			action = UOrderForm.SELL; // 売り選択
			fMaxData = prices[prices.length -1]; // 最大値更新

		} else if (fMinData > prices[prices.length - 1] && prices[prices.length - 1] != -1) {
			action = UOrderForm.BUY; // 買い選択
			fMinData = prices[prices.length -1]; // 最小値更新
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

		//    for (int i = 0; i < args.length; ++i) {
		//    	String[] strArray = args[i].split("=");
		//      String key = strArray[0];
		//      String value = strArray[1];
		//      if (key.equals(UMovingAverageAgent.MIN_QUANT_KEY)) {
		//        fMinQuant = Integer.parseInt(value);
		//        println("MinQuant has been changed to " + fMinQuant);
		//      } else if (key.equals(UMovingAverageAgent.MAX_QUANT_KEY)) {
		//        fMaxQuant = Integer.parseInt(value);
		//        println("MaxQuant has been changed to " + fMaxQuant);
		//      } else if (key.equals(UMovingAverageAgent.MAX_POSITION_KEY)) {
		//        fMaxPosition = Integer.parseInt(value);
		//        println("MaxPosition has been changed to " + fMaxPosition);
		//      } else {
		//      	println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
		//      }
		//    }

	}
}