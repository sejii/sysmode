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
 * 独自エージェント用のテンプレートです． このエージェントのコピーを作成して，名前をU<学籍番号>に修正して下さい．
 */
public class U01_1 extends UAgent {

	// ここにフィールド変数を定義してください．
	private static final int DEFAULT_MAX_POSITION = 300;

	private static final int DEFAULT_MIN_QUANT = 10;

	private static final int DEFAULT_MAX_QUANT = 50;

	private int fMaxPosition = DEFAULT_MAX_POSITION;

	private int fMinQuant = DEFAULT_MIN_QUANT;

	private int fMaxQuant = DEFAULT_MAX_QUANT;

	private static final String MAX_POSITION_KEY = "MaxPosition";

	private static final String MIN_QUANT_KEY = "MinQuant";

	private static final String MAX_QUANT_KEY = "MaxQuant";

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
	public U01_1(String loginName, String passwd, String realName, int seed) {
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

		// 現在の日，現在の節，現在の現物価格を表示する．
		println("day=" + day + ", session=" + session + ", spot="
				+ spotPrices[spotPrices.length - 1] + ",futures=" + futurePrices[futurePrices.length - 1]);
		println("spot size:" + spotPrices.length + ",futures size:" + futurePrices.length);

		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		forms[0].setBuySell(chooseAction(futurePrices));
		if (forms[0].getBuySell() == UOrderForm.SELL && -position > fMaxPosition) {
			forms[0].setBuySell(UOrderForm.NONE);
			return forms;
		}
		if (forms[0].getBuySell() == UOrderForm.BUY && position > fMaxPosition) {
			forms[0].setBuySell(UOrderForm.NONE);
			return forms;
		}
		forms[0].setPrice(determinPrice(forms[0].getBuySell(), futurePrices));
		forms[0].setQuantity(fMinQuant
				+ getRandom().nextInt(fMaxQuant - fMinQuant + 1));

		return forms;
	}

	/**
	 * エージェントのシステムパラメータを設定します．
	 * 
	 * @param args
	 *          システムパラメータ
	 */
	public void setParameters(String[] args) {
		super.setParameters(args);
		// このメソッドをオーバーライドする必要はありません．
		for (int i = 0; i < args.length; i++) {
			String[] strArray = args[i].split("=");
			String key = strArray[0];
			String value = strArray[1];
			if (key.equals(U01_1.MAX_POSITION_KEY)) {
				fMaxPosition = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fMaxPosition);
			} else if (key.equals(U01_1.MAX_QUANT_KEY)) {
				fMaxQuant = Integer.parseInt(value);
				println("MaxQuant has been changed to " + fMaxQuant);
			} else if (key.equals(U01_1.MIN_QUANT_KEY)) {
				fMinQuant = Integer.parseInt(value);
				println("MinQuant has been changed to " + fMinQuant);
			} else {
				println("Unknown parameter:" + key + " in U01_1.setParameters");
			}
		}
	}

	public int getMaxPosition() {
		return fMaxPosition;
	}

	public int getMinQuant() {
		return fMinQuant;
	}

	public int getMaxQuant() {
		return fMaxQuant;
	}

	private int chooseAction(int[] prices) {
		int presentPrice;
		int previousPrice;
		int prepreviousPrice;
		if (prices[prices.length - 1] < 0) {
			presentPrice = UOrderForm.INVALID_PRICE;
		} else {
			presentPrice = prices[prices.length - 1];
		}
		if (prices[prices.length - 2] < 0) {
			previousPrice = UOrderForm.INVALID_PRICE;
		} else {
			previousPrice = prices[prices.length - 2];
		}
		if (prices[prices.length - 3] < 0) {
			prepreviousPrice = UOrderForm.INVALID_PRICE;
		} else {
			prepreviousPrice = prices[prices.length - 3];
		}

		if ((presentPrice - previousPrice) - (previousPrice - prepreviousPrice) > 0) {
			if ((presentPrice - previousPrice) + (presentPrice - previousPrice)
					- (previousPrice - prepreviousPrice) > 0) {
				return UOrderForm.BUY;
			} else {
				return UOrderForm.NONE;
			}
		}

		if ((presentPrice - previousPrice) - (previousPrice - prepreviousPrice) < 0) {
			if ((presentPrice - previousPrice) + (presentPrice - previousPrice)
					- (previousPrice - prepreviousPrice) < 0) {
				return UOrderForm.SELL;
			} else {
				return UOrderForm.NONE;
			}
		}
		return UOrderForm.NONE;
	}

	private int determinPrice(int action, int[] prices) {
		int price;
		if (action == UOrderForm.BUY) {
			int subPrice = (prices[prices.length - 1] - prices[prices.length - 2])
					+ (prices[prices.length - 1] - prices[prices.length - 2])
					- (prices[prices.length - 2] - prices[prices.length - 3]);
			price = prices[prices.length - 1]
					+ subPrice
					+ (int) (((double) subPrice / (double) 4) * getRandom()
							.nextGaussian());
			if (price <= 0) {
				price = 1;
			} else if (price > prices[prices.length - 1] * 1.2) {
				price = prices[prices.length - 1] - 1;
			}
			return price;
		} else if (action == UOrderForm.SELL) {
			int subPrice = (prices[prices.length - 1] - prices[prices.length - 2])
					+ (prices[prices.length - 1] - prices[prices.length - 2])
					- (prices[prices.length - 2] - prices[prices.length - 3]);
			price = prices[prices.length - 1]
					- subPrice
					- (int) (((double) subPrice / (double) 4) * getRandom()
							.nextGaussian());
			if (price <= 0) {
				price = 1;
			} else if (price > prices[prices.length - 1] * 1.2) {
				price = prices[prices.length - 1] - 1;
			}
			return price;
		} else {
			return 0;
		}
	}
}
