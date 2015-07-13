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

import java.util.*;

/**
 * Quote情報(UTodaysQuoteとUHistoricalQuote)を扱うクラスです．
 */

public class UQuoteDB {

  /** UTodaysQuoteオブジェクトのベクタ */
  private Vector fTodaysQuotes;

  /** UHistoricalQuoteオブジェクトのベクタ */
  private Vector fHistoricalQuotes;

  /**
     UQuoteDBオブジェクトの生成, 初期化を行います．
   */
  public UQuoteDB() {
    fTodaysQuotes = new Vector();
    fHistoricalQuotes = new Vector();
  }

  /**
   * 1節分の情報を追加します．
   * @param brandName 銘柄名
   * @param date 日付
   * @param session 節
   * @param price 約定価格
   * @param volume 約定数量
   * @param askedQuotation 売り気配
   * @param bidQuotation 買い気配
   */
  public void addTodaysQuote(String brandName, int date, int session,
                             long price, long volume,
                             long askedQuotation, long bidQuotation) {
    UTodaysQuote quote = new UTodaysQuote(brandName, date, session, price,
                                          volume, askedQuotation, bidQuotation);
    fTodaysQuotes.addElement(quote);
  }

  /**
   * 登録されているUTodaysQuoteオブジェクトの数を返します．
   * @return UTodaysQuoteオブジェクトの数
   */
  public int getNoOfTodaysQuotes() {
    return fTodaysQuotes.size();
  }

  /**
   * index番目のUTodaysQuoteオブジェクトを返します．
   * @param index 何番目のUTodaysQuoteか?
   * @return index番目のUTodaysQuoteオブジェクト
   */
  public UTodaysQuote getTodaysQuote(int index) {
    if (index < 0 || index >= fTodaysQuotes.size()) {
      System.out.print("Error: ");
      System.out.print("index < 0 || index >= fTodaysQuotes.size() ");
      System.out.println("in UQuoteDB.getTodaysQuote");
      System.exit(5);
    }
    return (UTodaysQuote) fTodaysQuotes.elementAt(index);
  }

  /**
   * 当日分の情報を縮約してHistorical Quoteへ登録します．
   */
  public void update() {
    int size = fTodaysQuotes.size();
    if (size == 0) {
      return;
    }
    UTodaysQuote todaysQuote = getTodaysQuote(0);
    String brandName = todaysQuote.getBrandName();
    int date = todaysQuote.getDate();
    long startPrice = todaysQuote.getPrice();
    long highestPrice = startPrice;
    long lowestPrice = startPrice;
    long volume = todaysQuote.getVolume();
    for (int i = 1; i < size; ++i) {
      todaysQuote = getTodaysQuote(i);
      long price = todaysQuote.getPrice();
      if (price >= 0) {
        if (highestPrice < 0 || highestPrice < price) {
          highestPrice = price;
        }
        if (lowestPrice < 0 || lowestPrice > price) {
          lowestPrice = price;
        }
      }
      volume += todaysQuote.getVolume();
    }
    todaysQuote = getTodaysQuote(size - 1);
    long endPrice = todaysQuote.getPrice();
    UHistoricalQuote hq = new UHistoricalQuote(brandName, date, startPrice,
                                               highestPrice, lowestPrice,
                                               endPrice, volume);
    fHistoricalQuotes.addElement(hq);
    fTodaysQuotes.removeAllElements();
  }

  /**
   * 登録されているUHistoricalQuoteオブジェクトの数を返します．
   * @return UHistoricalQuoteオブジェクトの数
   */
  public int getNoOfHistoricalQuotes() {
    return fHistoricalQuotes.size();
  }

  /**
   * index番目のUHistoricalQuoteオブジェクトを返します．
   * @param index 何番目のUHistoricalQuoteか?
   * @return index番目のUHistoricalQuoteオブジェクト
   */
  public UHistoricalQuote getHistoricalQuote(int index) {
    if (index < 0 || index >= fHistoricalQuotes.size()) {
      System.out.print("Error: ");
      System.out.print("index < 0 || ");
      System.out.print("index >= fHistoricalQuotes.size() ");
      System.out.println("in UQuoteDB.getHistoricalQuote");
      System.exit(5);
    }
    return (UHistoricalQuote) fHistoricalQuotes.elementAt(index);
  }

  /**
   * 内部情報を出力します．
   */
  public void printOn() {
    System.out.println("****** Today's Quotes ********");
    for (int i = 0; i < getNoOfTodaysQuotes(); ++i) {
      getTodaysQuote(i).printOn();
    }
    System.out.println("****** Historical Quotes ********");
    for (int i = 0; i < getNoOfHistoricalQuotes(); ++i) {
      getHistoricalQuote(i).printOn();
    }
  }

  /**
   * テスト用メインメソッドです．
   */
  public static void main(String args[]) {
    UQuoteDB db = new UQuoteDB();
    db.addTodaysQuote("j30", 1, 1, 3000, 50, 0, 0);
    db.addTodaysQuote("j30", 1, 2, 3100, 20, 0, 0);
    db.addTodaysQuote("j30", 1, 3, 2950, 10, 0, 0);
    db.addTodaysQuote("j30", 1, 4, 3300, 100, 0, 0);
    db.printOn();
    db.update();
    db.printOn();
    db.addTodaysQuote("j30", 2, 1, -1, 0, 3000, 0);
    db.addTodaysQuote("j30", 2, 2, 4000, 20, 0, 0);
    db.addTodaysQuote("j30", 2, 3, 2000, 10, 0, 0);
    db.addTodaysQuote("j30", 2, 4, 3500, 100, 0, 0);
    db.printOn();
    db.update();
    db.printOn();
  }

}
