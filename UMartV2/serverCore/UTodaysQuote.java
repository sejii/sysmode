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
 * 板寄せごとの約定価格と約定数量を保持するためのクラスです．
 * また, 約定なしの節については売り気配と買い気配を
 * 保持しています．
 */

public class UTodaysQuote {

  /** 銘柄名 */
  private String fBrandName;

  /** 日 */
  private int fDate;

  /** 節 */
  private int fSession;

  /** 約定価格 */
  private long fPrice;

  /** 約定数量 */
  private long fVolume;

  /** 売り気配 */
  private long fAskedQuotation;

  /** 買い気配 */
  private long fBidQuotation;

  /**
   * UTodaysQuoteオブジェクトの生成, 初期化を行います．
   */
  public UTodaysQuote() {
    fBrandName = null;
    fDate = -1;
    fSession = -1;
    fPrice = -1;
    fVolume = -1;
    fAskedQuotation = -1;
    fBidQuotation = -1;
  }

  /**
   * UTodaysQuoteオブジェクトの生成, 初期化を行います．
   * @param brandName 銘柄名
   * @param date 日付
   * @param session 節
   * @param price 約定価格
   * @param volume 約定数量
   * @param askedQuotation 売り気配
   * @param bidQuotation 買い気配
   */
  public UTodaysQuote(String brandName, int date, int session, long price,
                       long volume, long askedQuotation, long bidQuotation) {
    fBrandName = brandName;
    fDate = date;
    fSession = session;
    fPrice = price;
    fVolume = volume;
    fAskedQuotation = askedQuotation;
    fBidQuotation = bidQuotation;
  }

  /**
   * UTodaysQuoteオブジェクトの複製を返します．
   * @return UTodaysQuoteオブジェクトの複製
   */
  public Object clone() {
    return new UTodaysQuote(fBrandName, fDate, fSession, fPrice,
                              fVolume, fAskedQuotation, fBidQuotation);
  }

  /**
   * 内部情報を出力します．
   */
  public void printOn() {
    System.out.print("BrandName:" + fBrandName);
    System.out.print(", Date:" + fDate);
    System.out.print(", BoardNo:" + fSession);
    System.out.print(", Price:" + fPrice);
    System.out.print(", Volume:" + fVolume);
    System.out.print(", AskedQuotation:" + fAskedQuotation);
    System.out.println(", BidQuotation:" + fBidQuotation);
  }

  /**
   * 銘柄名を返します．
   * @return 銘柄名
   */
  public String getBrandName() {
    return fBrandName;
  }

  /**
   * 日付を返します．
   * @return 日付
   */
  public int getDate() {
    return fDate;
  }

  /**
   * 節を返します．
   * @return 節
   */
  public int getSession() {
    return fSession;
  }

  /**
   * 約定価格を返します．
   * @return 約定価格
   */
  public long getPrice() {
    return fPrice;
  }

  /**
   * 約定数量を返します．
   * @return 約定数量
   */
  public long getVolume() {
    return fVolume;
  }

  /**
   * 売り気配を返します．
   * @return 売り気配
   */
  public long getAskedQuotation() {
    return fAskedQuotation;
  }

  /**
   * 買い気配を返します．
   * @return 買い気配
   */
  public long getBidQuotation() {
    return fBidQuotation;
  }

  /**
   * 銘柄名を設定します．
   * @param brandName 銘柄名
   */
  public void setBrandName(String brandName) {
    fBrandName = brandName;
  }

  /**
   * 日付を設定します．
   * @param date 日付
   */
  public void setDate(int date) {
    fDate = date;
  }

  /**
   * 節を設定します．
   * @param session 節
   */
  public void setSession(int session) {
    fSession = session;
  }

  /**
   * 約定価格を設定します．
   * @param price 約定価格
   */
  public void setPrice(long price) {
    fPrice = price;
  }

  /**
   * 約定数量を設定します．
   * @param volume 約定数量
   */
  public void setVolume(long volume) {
    fVolume = volume;
  }

  /**
   * 売り気配を設定します．
   * @param askedQuotation 売り気配
   */
  public void setAskedQuotation(long askedQuotation) {
    fAskedQuotation = askedQuotation;
  }

  /**
   * 買い気配を設定します．
   * @param bidQuotation 買い気配
   */
  public void setBidQuotation(long bidQuotation) {
    fBidQuotation = bidQuotation;
  }

}
