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
import java.util.StringTokenizer;

/**
 * 独自エージェント用のテンプレートです． このエージェントのコピーを作成して，名前をU<学籍番号>に修正して下さい．
 */
public class U04_2 extends UAgent {

	/** 注文量の最大値のデフォルト値 */
	public static final int DEFAULT_MAX_QUANT = 100;

	/** 注文量の最小値のデフォルト値 */
	public static final int DEFAULT_MIN_QUANT = 30;

	/** ポジションの最大値のデフォルト値 */
	public static final int DEFAULT_MAX_POSITION = 350;

	/** 価格閾値のデフォルト値 */
	private static final int DEFAULT_STANDARD_THRESHOLD = 20;

	/** SpreadRatioのデフォルト値 */
	private static final double DEFAULT_SPREAD_RETIO_THRESHOLD = 0.01;

	/** 乱数生成器 */
	private Random fRandom;

	/** Spread　Price */
	private int fSpreadPrice;

	/** 価格の平均 */
	private double fMeanPrice;

	/** Sigma */
	private double fSigma;

	/** Spread　Ratio */
	private double fSpreadRatio;

	/** 注文量の最大値 */
	private int fMaxQuant = DEFAULT_MAX_QUANT;

	/** 注文量の最小値 */
	private int fMinQuant = DEFAULT_MIN_QUANT;

	/** ポジションの最大値 */
	private int fMaxPosition = DEFAULT_MAX_POSITION;

	/** 価格閾値のデフォルト値 */
	private int fStandardThreshold = DEFAULT_STANDARD_THRESHOLD;

	/** SpreadRatio　閾値のデフォルト値 */
	private double fSpreadRatioThreshold = DEFAULT_SPREAD_RETIO_THRESHOLD;

	public static final String MIN_QUANT_KEY = "MinQuant";

	public static final String MAX_QUANT_KEY = "MaxQuant";

	public static final String MAX_POSITION_KEY = "MaxPosition";

	public static final String STANDARD_THRESHOLD_KEY = "StandardThreshold";

	public static final String SPREAD_RETIO_THRESHOLD_KEY = "SpreadRatioThreshold";

	/**
	 * コンストラクタです．
	 * 
	 * @param loginName
	 *          ログイン名
	 * @param passwd
	 *          パスワード
	 * @param realName
	 *          実名
	 * @param seed
	 *          乱数の種
	 */
	public U04_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		// ここでフィールド変数の値を初期化してください．
	}

	/**
	 * 注文票を作成します．
	 * 
	 * @param day
	 *          日
	 * @param session
	 *          節
	 * @param maxDays
	 *          取引日数
	 * @param noOfSessionsPerDay
	 *          1日の節数
	 * @param spotPrices
	 *          現物価格系列．spotPrices[0]からspotPrices[119]までの120節分のデータが格納されている．
	 *          spotPrices[119]が直近の価格です．ただし，価格が成立していない場合，-1が入っているので注意してください．
	 * @param futurePrices
	 *          先物価格系列．futurePrices[0]からfuturePrices[59]までの60節分のデータが格納されています．
	 *          futurePrices[59]が直近の価格です．ただし，価格が成立していない場合，-1が入っているので注意してください．また，
	 *          取引開始節より前は現物価格が格納されています．
	 * @param position
	 *          ポジション．正ならば買い越し(ロング・ポジション)，負ならば売り越し（ショート・ポジション）を表します．
	 * @param money
	 *          現金残高．型がlongであることに注意してください．
	 * @return UOrderForm[] 注文票の配列
	 */

	public UOrderForm[] makeOrderForms(int day, int session, int maxDays,
			int noOfSessionsPerDay, int[] spotPrices, int[] futurePrices,
			int position, long money) {
		fRandom = getRandom();
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		forms[0].setBuySell(chooseAction(futurePrices, spotPrices));
		setMaxPosition(forms, position);
		forms[0].setPrice(determinePrice(spotPrices, futurePrices));
		forms[0].setQuantity((fMinQuant + fMaxQuant) / 2);
		println(" => " + forms[0].getBuySellByString() + ", price="
				+ forms[0].getPrice() + ", quantity=" + forms[0].getQuantity());
		return forms;
	}
	//パラメータの設定
	public void setParameters(String[] args) {
		super.setParameters(args);
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer st = new StringTokenizer(args[i], "= ");
			String key = st.nextToken();
			String value = st.nextToken();
			if (key.equals(U04_2.DEFAULT_MAX_POSITION)) {
				fMaxPosition = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fMaxPosition);
			} else if (key.equals(U04_2.DEFAULT_MAX_QUANT)) {
				fMaxQuant = Integer.parseInt(value);
				println("MediumTerm has been changed to " + fMaxQuant);
			} else if (key.equals(U04_2.DEFAULT_MIN_QUANT)) {
				fMinQuant = Integer.parseInt(value);
				println("MinQuant has been changed to " + fMinQuant);
			} else if (key.equals(U04_2.DEFAULT_SPREAD_RETIO_THRESHOLD)) {
				fSpreadRatioThreshold = Double.parseDouble(value);
				println("SpreadRatioThreshold has been changed to "
						+ fSpreadRatioThreshold);
			} else if (key.equals(U04_2.DEFAULT_STANDARD_THRESHOLD)) {
				fStandardThreshold = Integer.parseInt(value);
				println("StandardThreshold has been changed to " + fStandardThreshold);
			} else {
				println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
			}
		}
	}
	//注文区分の決定
	private int chooseAction(int[] futurePrices, int[] spotPrices) {
		int action = UOrderForm.NONE;
		fSpreadPrice = spotPrices[spotPrices.length - 1]
				- futurePrices[futurePrices.length - 1];
		double pr = fRandom.nextDouble();
		if (fSpreadPrice > fStandardThreshold) {
			action = UOrderForm.BUY;
		} else if (fSpreadPrice < -fStandardThreshold) {
			action = UOrderForm.SELL;
		} else if (fSpreadPrice < fStandardThreshold
				&& fSpreadPrice > -fStandardThreshold) {
			fSpreadRatio = (futurePrices[futurePrices.length - 1] - spotPrices[spotPrices.length - 1])
					/ spotPrices[spotPrices.length - 1];
			if (fSpreadRatio <= -fSpreadRatioThreshold) {
				if (pr > fRandom.nextDouble()) {
					action = UOrderForm.BUY;
				} else {
					action = UOrderForm.NONE;
				}
			} else if (fSpreadRatio >= fSpreadRatioThreshold) {
				if (pr > fRandom.nextDouble()) {
					action = UOrderForm.SELL;
				} else {
					action = UOrderForm.NONE;
				}
			}
		}
		return action;
	}
	//注文価格の決定
	private int determinePrice(int[] spotPrices, int[] futurePrices) {
		int lastFuturePrice = futurePrices[futurePrices.length - 1];
		int lastSpotPrice = spotPrices[spotPrices.length - 1];
		if (lastFuturePrice <= 0) {
			lastFuturePrice = futurePrices[futurePrices.length - 2];
		}
		if (lastSpotPrice <= 0) {
			lastSpotPrice = spotPrices[spotPrices.length - 2];
		}
		fMeanPrice = (lastFuturePrice + lastSpotPrice) / 2.0;
		fSigma = ((Math.abs(lastFuturePrice - lastSpotPrice)) / 2.0);
		return (int) (fMeanPrice + fSigma * fRandom.nextGaussian());
	}

	// ポジション制限の処理。
	public UOrderForm[] setMaxPosition(UOrderForm[] forms, int pos) {
		if (forms[0].getBuySell() == 2) {
			if (pos > fMaxPosition) {
				forms[0].setBuySell(0);
			}
		} else if (forms[0].getBuySell() == 1) {
			if (pos < -fMaxPosition) {
				forms[0].setBuySell(0);
			}
		}
		return forms;
	}
}
