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
 * １節分の全ての注文情報のログを取り扱うクラスです．
 */
public class UOrderCommandLog {

  /** 日を引くキー */
  public static final String INT_DATE = "INT_DATE";

  /** 節を引くキー */
  public static final String INT_SESSION = "INT_SESSION";

  /** 注文時刻(実時間)を引くキー */
  public static final String STRING_REAL_TIME = "STRING_REAL_TIME";

  /** ログイン名を引くためのキー */
  public static final String STRING_LOGIN_NAME = "STRING_LOGIN_NAME";

  /** コマンド名を引くキー */
  public static final String STRING_COMMAND_NAME = "STRING_COMMAND_NAME";

  /** 注文IDを引くキー */
  public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

  /** 銘柄名を引くキー */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** 新規返済区分を引くキー */
  public static final String INT_NEW_REPAY = "INT_NEW_REPAY";

  /** 売買区分を引くキー */
  public static final String INT_SELL_BUY = "INT_SELL_BUY";

  /** 成行指値区分を引くキー */
  public static final String INT_MARKET_LIMIT = "INT_MARKET_LIMIT";

  /** 注文価格を引くキー */
  public static final String LONG_ORDER_PRICE = "LONG_ORDER_PRICE";

  /** 注文数量を引くキー */
  public static final String LONG_ORDER_VOLUME = "LONG_ORDER_VOLUME";

  /** 同一板寄せ期間内の注文をソートするために利用される乱数を引くためのキー */
  public static final String INT_RANDOM_NUMBER = "INT_RANDOM_NUMBER";

  /** 注文情報の配列 */
  private ArrayList fOrderCommandArray;

  /**
   * コンストラクタです．
   */
  public UOrderCommandLog() {
    fOrderCommandArray = new ArrayList();
  }

  /**
   * 注文情報を登録します．
   * @param o 注文
   */
  public synchronized void registerOrderRequest(UOrder o) {
    HashMap hash = new HashMap();
    hash.put(UOrderCommandLog.INT_DATE, new Integer(o.getDate()));
    hash.put(UOrderCommandLog.INT_SESSION, new Integer(o.getSession()));
    hash.put(UOrderCommandLog.STRING_REAL_TIME, o.getTime());
    hash.put(UOrderCommandLog.STRING_LOGIN_NAME, o.getUserName());
    hash.put(UOrderCommandLog.STRING_COMMAND_NAME, "Request");
    hash.put(UOrderCommandLog.LONG_ORDER_ID, new Long(o.getOrderID()));
    hash.put(UOrderCommandLog.STRING_BRAND_NAME, o.getBrandName());
    hash.put(UOrderCommandLog.INT_NEW_REPAY, new Integer(o.getNewRepay()));
    hash.put(UOrderCommandLog.INT_SELL_BUY, new Integer(o.getSellBuy()));
    hash.put(UOrderCommandLog.INT_MARKET_LIMIT, new Integer(o.getMarketLimit()));
    hash.put(UOrderCommandLog.LONG_ORDER_PRICE, new Long(o.getPrice()));
    hash.put(UOrderCommandLog.LONG_ORDER_VOLUME, new Long(o.getVolume()));
    hash.put(UOrderCommandLog.INT_RANDOM_NUMBER, new Integer(o.getRandomNumber()));
    fOrderCommandArray.add(hash);
  }

  /**
   * キャンセル情報を登録します．
   * @param date 日
   * @param session 節
   * @param userName ユーザ名
   * @param orderID 注文ID
   * @param cancelVolume キャンセル数量
   */
  public synchronized void registerOrderCancel(int date, int session,
                                               String userName, long orderID,
                                               long cancelVolume) {
    HashMap hash = new HashMap();
    hash.put(UOrderCommandLog.INT_DATE, new Integer(date));
    hash.put(UOrderCommandLog.INT_SESSION, new Integer(session));
    hash.put(UOrderCommandLog.STRING_REAL_TIME, new Date());
    hash.put(UOrderCommandLog.STRING_LOGIN_NAME, userName);
    hash.put(UOrderCommandLog.STRING_COMMAND_NAME, "Cancel");
    hash.put(UOrderCommandLog.LONG_ORDER_ID, new Long(orderID));
    hash.put(UOrderCommandLog.LONG_ORDER_VOLUME, new Long(cancelVolume));
    fOrderCommandArray.add(hash);
  }

  /**
   * 注文情報のイテレータを返します．
   * @return 注文情報のイテレータ
   */
  public Iterator getOrderCommands() {
    return fOrderCommandArray.iterator();
  }

  /**
   * 全ての注文情報をクリアします．
   */
  public void clear() {
    fOrderCommandArray.clear();
  }

  /**
   * 出力ストリームへデータを書き出します．
   * @param pw 出力ストリーム
   * @throws IOException
   */
  public synchronized void writeTo(PrintWriter pw) throws IOException {
    pw.println(
        "Date,Session,RealTime,LoginName,Command,OrderID,BrandName,NewRepay,"
        + "SellBuy,MarketLimit,Price,Volume,RandomNumber");
    Iterator itr = fOrderCommandArray.iterator();
    while (itr.hasNext()) {
      HashMap hash = (HashMap) itr.next();
      pw.print(hash.get(UOrderCommandLog.INT_DATE).toString() + ",");
      pw.print(hash.get(UOrderCommandLog.INT_SESSION).toString() + ",");
      pw.print(hash.get(UOrderCommandLog.STRING_REAL_TIME) + ",");
      pw.print(hash.get(UOrderCommandLog.STRING_LOGIN_NAME).toString() + ",");
      String cmdName = (String) hash.get(UOrderCommandLog.STRING_COMMAND_NAME);
      pw.print(cmdName + ",");
      pw.print(hash.get(UOrderCommandLog.LONG_ORDER_ID).toString() + ",");
      if (cmdName.equals("Request")) {
        pw.print(hash.get(UOrderCommandLog.STRING_BRAND_NAME).toString() + ",");
        pw.print(hash.get(UOrderCommandLog.INT_NEW_REPAY).toString() + ",");
        pw.print(hash.get(UOrderCommandLog.INT_SELL_BUY).toString() + ",");
        pw.print(hash.get(UOrderCommandLog.INT_MARKET_LIMIT).toString() + ",");
        pw.print(hash.get(UOrderCommandLog.LONG_ORDER_PRICE).toString() + ",");
        pw.print(hash.get(UOrderCommandLog.LONG_ORDER_VOLUME).toString() + ",");
        pw.println(hash.get(UOrderCommandLog.INT_RANDOM_NUMBER).toString());
      } else {
        pw.print(",,,,,");
        pw.println("-" + hash.get(UOrderCommandLog.LONG_ORDER_VOLUME).toString() +
                   ",");
      }
    }
  }

  /**
   * 入力ストリームからデータを読み込みます．
   * @param br 入力ストリーム
   * @throws IOException
   */
  public void readFrom(BufferedReader br) throws IOException {
    br.readLine(); // skip the header
    String line = null;
    while ( (line = br.readLine()) != null) {
      StringTokenizer st = new StringTokenizer(line, ",");
      HashMap hash = new HashMap();
      hash.put(UOrderCommandLog.INT_DATE, Integer.valueOf(st.nextToken()));
      hash.put(UOrderCommandLog.INT_SESSION, Integer.valueOf(st.nextToken()));
      hash.put(UOrderCommandLog.STRING_REAL_TIME, st.nextToken());
      hash.put(UOrderCommandLog.STRING_LOGIN_NAME, Integer.valueOf(st.nextToken()));
      String cmdName = st.nextToken();
      hash.put(UOrderCommandLog.STRING_COMMAND_NAME, cmdName);
      hash.put(UOrderCommandLog.LONG_ORDER_ID, Long.valueOf(st.nextToken()));
      if (cmdName.equals("Request")) {
        hash.put(UOrderCommandLog.STRING_BRAND_NAME, st.nextToken());
        hash.put(UOrderCommandLog.INT_NEW_REPAY, Integer.valueOf(st.nextToken()));
        hash.put(UOrderCommandLog.INT_SELL_BUY, Integer.valueOf(st.nextToken()));
        hash.put(UOrderCommandLog.INT_MARKET_LIMIT,
                 Integer.valueOf(st.nextToken()));
        hash.put(UOrderCommandLog.LONG_ORDER_PRICE, Long.valueOf(st.nextToken()));
        hash.put(UOrderCommandLog.LONG_ORDER_VOLUME, Long.valueOf(st.nextToken()));
        hash.put(UOrderCommandLog.INT_RANDOM_NUMBER,
                 Integer.valueOf(st.nextToken()));
      }
      fOrderCommandArray.add(hash);
    }
  }

}
