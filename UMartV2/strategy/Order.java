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

/**
 * 注文クラスです．
 */
public class Order {

	/** 売り */
  public static final int SELL = 1;
  
  /** 買い */
  public static final int BUY = 2;
  
  /** 何もしない */
  public static final int NONE = 0;

  /** 注文価格 */
  public int price;
  
  /** 注文数量 */
  public int quant;
  
  /** 注文区分 */
  public int buysell; // 1: sell, 2: buy, 0: none

  /**
   * 売買区分を文字列にして返します．
   * @param buySell 売買区分
   * @return 売買区分の文字列
   */
  public static String buySellToString(int buySell) {
    if (buySell == SELL) {
      return "Sell";
    } else if (buySell == BUY) {
      return "Buy";
    } else if (buySell == NONE) {
      return "None";
    } else {
      return "Others?";
    }
  }

}
