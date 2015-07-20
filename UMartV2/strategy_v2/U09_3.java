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

public class U09_3 extends UAgent {
	public static final int DEFAULT_MAX_QUANT = 30;
	public static final int DEFAULT_MIN_QUANT = 29;
	public static final int DEFAULT_MAX_POSITION = 300;
	private int fMaxQuant = DEFAULT_MAX_QUANT;
	private int fMinQuant = DEFAULT_MIN_QUANT;
	private int fMaxPosition = DEFAULT_MAX_POSITION;
	public static final String MAX_QUANT_KEY = "MaxQuant";
	public static final String MIN_QUANT_KEY = "MinQuant";
	public static final String MAX_POSITION_KEY = "MaxPosition";

	public U09_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	public int getMinQuant() {
		return fMinQuant;
	}

	public int getMaxQuant() {
		return fMaxQuant;
	}

	public int getMaxPosition() {
		return fMaxPosition;
	}

	public UOrderForm[] makeOrderForms(int day, int session, int maxDays,
			int noOfSessionsPerDay, int[] spotPrices, int[] futuresPrices,
			int position, long money) {
		Random rand = getRandom();
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		int[] prices = futuresPrices;
		forms[0].setBuySell(chooseAction(prices, position));
		println("");
		print("day=" + day + ", session=" + session + ", futures="
				+ prices[prices.length - 1]);
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
		forms[0].setQuantity(fMinQuant
				+ rand.nextInt(fMaxQuant - fMinQuant + 1));
		print(" => " + forms[0].getBuySellByString() + ", price="
				+ forms[0].getPrice() + ", quantity=" + forms[0].getQuantity());
		return forms;
	}

	private int determinePrice(int action, int[] prices) {
		int price = UOrderForm.INVALID_PRICE;
		int widthOfPrice = Math.abs(prices[prices.length - 1]
				- prices[prices.length - 2]);
		if (action == UOrderForm.BUY) {
			price = prices[prices.length - 1]
					+ widthOfPrice
					+ (int) ((double) widthOfPrice / 4.0 * getRandom()
							.nextGaussian());
		} else if (action == UOrderForm.SELL) {
			price = prices[prices.length - 1]
					- widthOfPrice
					+ (int) ((double) widthOfPrice / 4.0 * getRandom()
							.nextGaussian());
		}
		if (price < 0) {
			price = 1;
		}
		return price;
	}

	private int chooseAction(int[] prices, int position) {
		int action = UOrderForm.NONE;
		double previousfutureprice6 = prices[prices.length - 6];
		double previousfutureprice5 = prices[prices.length - 5];
		double previousfutureprice4 = prices[prices.length - 4];
		double previousfutureprice3 = prices[prices.length - 3];
		double previousfutureprice2 = prices[prices.length - 2];
		double previousfutureprice1 = prices[prices.length - 1];
		if (previousfutureprice3 < previousfutureprice2
				&& previousfutureprice2 < previousfutureprice1
				// 安くなる傾向にあると判断
				&& ((previousfutureprice6 + previousfutureprice5
						+ previousfutureprice2 + previousfutureprice1) / 4) > ((previousfutureprice4 + previousfutureprice3) / 2))// 先物価格のカーブの形状が下に凸のグラフと判断
		{
			action = UOrderForm.BUY;
			if (position > fMaxPosition) {
				action = UOrderForm.NONE;
			}// ポジションがマックスポジションより大きくなければ買わない
		} else if (previousfutureprice3 > previousfutureprice2
				&& previousfutureprice2 > previousfutureprice1
				// 高くなる傾向にあると判断
				&& ((previousfutureprice6 + previousfutureprice5
						+ previousfutureprice2 + previousfutureprice1) / 4) < ((previousfutureprice4 + previousfutureprice3) / 2))// カーブの形状が上に凸のグラフと判断
		{
			action = UOrderForm.SELL;
			if (position < -fMaxPosition) {
				action = UOrderForm.NONE;
			}// ポジションがマックスポジションより小さくなければ売らない
		}
		return action;
	}

	public void setParameters(String[] args) {
		super.setParameters(args);
		for (int i = 0; i < args.length; ++i) {
			StringTokenizer st = new StringTokenizer(args[i], "= ");
			String key = st.nextToken();
			String value = st.nextToken();
			if (key.equals(U09_3.MIN_QUANT_KEY)) {
				fMinQuant = Integer.parseInt(value);
				println("MinQuant has been changed to " + fMinQuant);
			} else if (key.equals(U09_3.MAX_QUANT_KEY)) {
				fMaxQuant = Integer.parseInt(value);
				println("MaxQuant has been changed to " + fMaxQuant);
			} else if (key.equals(U09_3.MAX_POSITION_KEY)) {
				fMaxPosition = Integer.parseInt(value);
				println("MaxPosition has been changed to " + fMaxPosition);
			} else {
				println("Unknown parameter:" + key
						+ " in U09_3.setParameters");
			}
		}
	}
}