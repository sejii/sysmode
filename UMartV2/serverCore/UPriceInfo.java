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
package serverCore;

/**
 * UPriceInfoDBで用いられる一ステップ分の価格情報を取り扱うクラスです．
 * 日，節，現物価格，先物価格を保持しています．
 */
public class UPriceInfo {

	/** 価格が成立していないときに用いる定数 */
  public static final long INVALID_PRICE = -1;

  /** 日 */
  private int fDate;

  /** 節 */
  private int fSession;

  /** 現物価格 */
  private long fSpotPrice;

  /** 先物価格。ただし, 価格が存在しない場合は, -1が設定されます．*/
  private long fFuturePrice;

  /**
   * UPriceInfoオブジェクトの生成と初期化を行します．
   */
  public UPriceInfo() {
    fDate = 0;
    fSession = 0;
    fSpotPrice = UPriceInfo.INVALID_PRICE;
    fFuturePrice = UPriceInfo.INVALID_PRICE;
  }

  /**
   * 現物価格を返します．
   * @return 現物価格
   */
  public long getSpotPrice() {
    return fSpotPrice;
  }

  /**
   * 先物価格を返します．
   * @return 先物価格。価格が存在しない場合は, -1が返ります．
   */
  public long getFuturePrice() {
    return fFuturePrice;
  }

  /**
   * 現物価格を設定します．
   * @param spotPrice 現物価格
   */
  public void setSpotPrice(long spotPrice) {
    fSpotPrice = spotPrice;
  }

  /**
   * 先物価格を設定します．
   * @param futurePrice 先物価格。価格が存在しない場合は -1を設定します．
   */
  public void setFuturePrice(long futurePrice) {
    fFuturePrice = futurePrice;
  }

  /**
   * 節を返します．
   * @return 節
   */
  public int getSession() {
    return fSession;
  }

  /**
   * 日を返します．
   * @return 日
   */
  public int getDate() {
    return fDate;
  }

  /**
   * 節を設定します．
   * @param session 節
   */
  public void setSession(int session) {
    fSession = session;
  }

  /**
   * 日を設定します．
   * @param date 日
   */
  public void setDate(int date) {
    fDate = date;
  }

}
