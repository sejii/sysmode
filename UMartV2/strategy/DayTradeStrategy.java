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
package strategy;

import java.util.*;

/**
 * デイ・トレーデイングの初歩戦略です．
 * 売り注文と買い注文を同時に出します．
 * 売り注文の指値は直前の先物価格よりも高く， 買い注文の指値は直前の先物価格よりも安くします．
 */
public class DayTradeStrategy extends Strategy {

  /** 注文価格の幅のデフォルト値 */
  public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** 注文量の最大値のデフォルト値 */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** 注文量の最小値のデフォルト値 */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** ポジションの最大値のデフォルト値 */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** 売買の乖離比のデフォルト値 */
  public static final double DEFAULT_SPREAD_RATIO = 0.01;

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

  /** 売買の乖離比 */
  private double fSpreadRatio = DEFAULT_SPREAD_RATIO;

  /**
   * コンストラクタです．
   * @param seed 乱数の種
   */
  public DayTradeStrategy(int seed) {
    fRandom = new Random(seed); // Initialization of random sequence.
  }

  /*
   * (non-Javadoc)
   * @see strategy.Strategy#action(int[], int[], int, long, int)
   */
  public void action(int[] spotPrices, int[] futurePrices, int position,
                     long money, int restSessions) {
    Order buyOrder = getBuyOrder(spotPrices, futurePrices, position, money, restSessions);
    Order sellOrder = getSellOrder(spotPrices, futurePrices, position, money, restSessions);
    orderRequest(buyOrder);
    orderRequest(sellOrder);
  }

  /**
   * 買い注文を返す．
   * @param spotPrices 現物価格系列
   * @param futurePrices 先物価格系列
   * @param position ポジション
   * @param money 現金残高
   * @param restSessions 残りの節数
   * @return　買い注文
   */
  public Order getBuyOrder(int[] spotPrices, int[] futurePrices, int position,
                           long money, int restSessions) {
    Order order = new Order(); // Object to return values.
    order.buysell = Order.BUY;
    // Cancel decision if it may increase absolute value of the position
    if (position > fMaxPosition) {
      order.buysell = Order.NONE;
      return order;
    }
    int latestFuturePrice = futurePrices[futurePrices.length - 1];
    if (latestFuturePrice < 0) {
      order.buysell = Order.NONE;
      return order;
    } // If previous price is invalid, it makes no order.
    order.price = (int) ( (double) latestFuturePrice * (1.0 - fSpreadRatio));
    if (order.price < 1) {
      order.price = 1;
    }
    order.quant = fMinQuant + fRandom.nextInt(fMaxQuant - fMinQuant + 1);
    // Message
    message("Buy" + ", price = " + order.price + ", volume = " + order.quant + "  )");
    return order;
  }

  /**
   * 売り注文を返す．
   * @param spotPrices 現物価格系列
   * @param futurePrices 先物価格系列
   * @param position ポジション
   * @param money 現金残高
   * @param restSessions 残りの節数
   * @return　売り注文
   */
  public Order getSellOrder(int[] spotPrices, int[] futurePrices, int position,
                            long money, int restSessions) {
    Order order = new Order(); // Object to return values.
    order.buysell = Order.SELL;
    // Cancel decision if it may increase the position
    if (position < -fMaxPosition) {
      order.buysell = Order.NONE;
      return order;
    }
    int latestFuturePrice = futurePrices[futurePrices.length - 1];
    if (latestFuturePrice < 0) {
      order.buysell = Order.NONE;
      return order;
    } // If previous price is invalid, it makes no order.
    order.price = (int) ( (double) latestFuturePrice * (1.0 + fSpreadRatio));
    order.quant = fMinQuant + fRandom.nextInt(fMaxQuant - fMinQuant + 1);
    // Message
    message("Sell" + ", price = " + order.price + ", volume = " + order.quant + "  )");
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
      } else if (key.equals("SpreadRatio")) {
        fSpreadRatio = Double.parseDouble(value);
      } else {
        message("Unknown parameter:" + key + " in RandomStrategy.setParameters");
      }
    }
  }

}
