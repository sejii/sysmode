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
package log;

import java.io.*;
import java.util.*;

import serverCore.*;

/**
 * １節分の板情報のログを扱うクラスです．
 */
public class UBoardLog {

  /** 日付を引くためのキー */
  public static final String INT_DATE = "INT_DATE";

  /** 節を引くためのキー */
  public static final String INT_SESSION = "INT_SESSION";

  /** 価格を引くためのキー */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** 売注文数量を引くためのキー */
  public static final String LONG_SELL_VOLUME = "LONG_SELL_VOLUME";

  /** 買注文数量を引くためのキー */
  public static final String LONG_BUY_VOLUME = "LONG_BUY_VOLUME";

  /** 日 */
  private int fDate;

  /** 節 */
  private int fSession;

  /** 板情報 (SVMPコマンドBoardInformationに準じた形で格納) */
  private ArrayList fBoardInformation;

  /**
   * コンストラクタです．
   * @param date 日
   * @param session 節
   */
  public UBoardLog(int date, int session) {
    fDate = date;
    fSession = session;
    fBoardInformation = new ArrayList();
  }

  /**
   * コンストラクタです．
   * @param date 日
   * @param session 節
   * @param boardInfo 板情報
   */
  public UBoardLog(int date, int session, UBoardInformation boardInfo) {
    this(date, session);
    Enumeration elements = boardInfo.getBoardInfoElements().elements();
    while (elements.hasMoreElements()) {
      UBoardInfoElement element = (UBoardInfoElement) elements.nextElement();
      long price = element.getPrice();
      long sellVolume = element.getSellVolume();
      long buyVolume = element.getBuyVolume();
      fBoardInformation.add(makeElement(date, session, price, sellVolume,
                                        buyVolume));
    }
  }

  /**
   * 板情報の要素を生成します．
   * @param date 日
   * @param session 節
   * @param price 価格
   * @param sellVolume 売注文数
   * @param buyVolume 買注文数
   * @return 板情報の要素
   */
  private HashMap makeElement(int date, int session, long price,
                              long sellVolume, long buyVolume) {
    HashMap hash = new HashMap();
    hash.put(UBoardLog.INT_DATE, new Integer(date));
    hash.put(UBoardLog.INT_SESSION, new Integer(session));
    hash.put(UBoardLog.LONG_PRICE, new Long(price));
    hash.put(UBoardLog.LONG_SELL_VOLUME, new Long(sellVolume));
    hash.put(UBoardLog.LONG_BUY_VOLUME, new Long(buyVolume));
    return hash;
  }

  /**
   * 出力ストリームへ書き出します．
   * @param pw 出力ストリーム
   * @throws IOException
   */
  public void writeTo(PrintWriter pw) throws IOException {
    pw.println("Date,Session,Price,SellVolume,BuyVolume");
    Iterator itr = fBoardInformation.iterator();
    while (itr.hasNext()) {
      HashMap hash = (HashMap) itr.next();
      pw.print(hash.get(UBoardLog.INT_DATE).toString() + ",");
      pw.print(hash.get(UBoardLog.INT_SESSION).toString() + ",");
      pw.print(hash.get(UBoardLog.LONG_PRICE).toString() + ",");
      pw.print(hash.get(UBoardLog.LONG_SELL_VOLUME).toString() + ",");
      pw.println(hash.get(UBoardLog.LONG_BUY_VOLUME).toString());
    }
  }

  /**
   * 入力ストリームから読み込みます．
   * @param br 入力ストリーム
   * @throws IOException
   */
  public void readFrom(BufferedReader br) throws IOException {
    fBoardInformation.clear();
    br.readLine(); // skip the header
    String line = null;
    while ( (line = br.readLine()) != null) {
      StringTokenizer st = new StringTokenizer(line, ",");
      int date = Integer.parseInt(st.nextToken());
      int boardNo = Integer.parseInt(st.nextToken());
      long price = Long.parseLong(st.nextToken());
      long sellVolume = Long.parseLong(st.nextToken());
      long buyVolume = Long.parseLong(st.nextToken());
      HashMap hash = makeElement(date, boardNo, price, sellVolume, buyVolume);
      fBoardInformation.add(hash);
    }
  }

}
