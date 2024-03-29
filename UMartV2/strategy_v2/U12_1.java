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

import strategy.Order;
import strategy.Strategy;


public class U12_1 extends Strategy {

  /** 注文価格の幅のデフォルト値 */
  public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** 注文量の最大値のデフォルト値 */
  public static final int DEFAULT_MAX_QUANT = 800;

  /** 注文量の最小値のデフォルト値 */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** ポジションの最大値のデフォルト値 */
  public static final int DEFAULT_MAX_POSITION = 2000;

  /** 取引所から得られる全ての価格情報が無効のときに使う価格のデフォルト値 */
  public static final int DEFAULT_NOMINAL_PRICE = 3000;

  /** RSIを計算する際の参照期間のデフォルト値　　大きすぎるとだめ */
  public static final int DEFAULT_REFERENCE_TERM = 60;

  /** エッジバンドのデフォルト値 */
  public static final double DEFAULT_EDGE_BAND = 0.35;

  /** 乱数生成器 */
  private Random fRandom;

  /** 注文価格の幅 */
  private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE;

  /** 注文量の最大値 */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** 注文量の最小値 */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** ポジションの最大値 */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** 取引所から得られる全ての価格情報が向こうのときに使う価格 */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

  /** RSIを計算する際の参照期間 */
  private int fReferenceTerm = DEFAULT_REFERENCE_TERM;

  /** エッジバンド */
  private double fEdgeBand = DEFAULT_EDGE_BAND;

  /**
   * コンストラクタです．
   * @param seed 乱数の種
   */
  public U12_1(int seed) {
    fRandom = new Random(seed); // Initialization of random sequence.
  }

  /*
   * (non-Javadoc)
   * @see strategy.Strategy#action(int[], int[], int, long, int)
   */
  public void action(int[] spotPrices, int[] futurePrices,
                     int pos, long money, int restDay) {
    Order order = getOrder(spotPrices, futurePrices, pos, money, restDay);
    orderRequest(order);
  }

  /*
   * (non-Javadoc)
   * @see strategy.Strategy#getOrder(int[], int[], int, long, int)
   */
  public Order getOrder(int[] spotPrices, int[] futurePrices,
                        int pos, long money, int restDay) {
    Order order = new Order(); // Object to return values.
    // Measure and accumulate upward and downward price changes
    int oldPrice = -1;
    int fcnt = futurePrices.length - fReferenceTerm;
    // go back fcnt by referenceTerm
    while (true) { // Find the well defined oldest price in the time window
      if (fcnt == futurePrices.length) {
        // All the prices are invalid, no order is made
        order.buysell = Order.NONE;
        return order;
      }
      oldPrice = futurePrices[fcnt];
      if (oldPrice >= 0) {
        break;
      }
      ++fcnt;
    }
    int upSum = 0; // accumulated upward price change
    int downSum = 0; // accumulated downward price change
    while (true) {
      if (fcnt == futurePrices.length) {
        break;
      }
      if (futurePrices[fcnt] >= 0) {
        // If price is valid, get up/downward price change
        if (futurePrices[fcnt] - oldPrice > 0) {
          upSum += futurePrices[fcnt] - oldPrice;
        } else if (futurePrices[fcnt] - oldPrice < 0) {
          downSum += oldPrice - futurePrices[fcnt];
        }
        oldPrice = futurePrices[fcnt];
      }
      ++fcnt;
    }
    if (upSum + downSum == 0) { // If there is no price change in the
      // time window, no order is made.
      order.buysell = Order.NONE;
      return order;
    }
    // Calculate RSI
    double rsi = (double) upSum / (double) (upSum + downSum);
    // Decide buy or sell based on RSI
    if (rsi < fEdgeBand) {
      order.buysell = Order.BUY;
      // Make a buy order because upward change
      // is maturing.
    } else if (rsi > 1.0 - fEdgeBand) {
      order.buysell = Order.SELL;
      // Make a sell order because downward change
      // is maturing.
    } else {
      order.buysell = Order.NONE; //
      return order;
    }
    // Cancel decision if it may increase absolute value of the position
    if (order.buysell == Order.BUY) {
      if (pos > fMaxPosition) {
        order.buysell = Order.NONE;
        return order;
      }
    } else if (order.buysell == Order.SELL) {
      if (pos < -fMaxPosition) {
        order.buysell = Order.NONE;
        return order;
      }
    }
    int prevPrice = getLatestPrice(futurePrices);
    while (true) {
      order.price = prevPrice + (int) (fWidthOfPrice * fRandom.nextGaussian());
      if (order.price > 0) {
        break;
      }
    } // Add normal random number around prevPrice
    // with the standard deviation widthOfPrice.
    // If it gets negative, repeat again.
    // Decide quantity of order between [minQuant, maxQuant] randomly.
    order.quant = fMinQuant + fRandom.nextInt(fMaxQuant - fMinQuant + 1);
    // Message
    message(Order.buySellToString(order.buysell)
            + ", price = " + order.price
            + ", volume = " + order.quant
            + " (RSI = " + rsi + " )");
    return order;
  }

  /* (non-Javadoc)
   * @see strategy.UBaseStrategy#setParameters(java.lang.String[])
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    for (int i = 0; i < args.length; ++i) {
      StringTokenizer st = new StringTokenizer(args[i], "= ");
      String key = st.nextToken();
      String value = st.nextToken();
      if (key.equals("WidthOfPrice")) {
        fWidthOfPrice = Integer.parseInt(value);
      } else if (key.equals("MinQuant")) {
        fMinQuant = Integer.parseInt(value);
      } else if (key.equals("MaxQuant")) {
        fMaxQuant = Integer.parseInt(value);
      } else if (key.equals("MaxPosition")) {
        fMaxPosition = Integer.parseInt(value);
      } else if (key.equals("ReferenceTerm")) {
        fReferenceTerm = Integer.parseInt(value);
      } else if (key.equals("EdgeBand")) {
        fEdgeBand = Double.parseDouble(value);
      } else {
        message("Unknown parameter:" + key + " in RsiStrategy.setParameters");
      }
    }
  }

}
