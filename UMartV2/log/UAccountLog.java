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
 * １節分の口座情報のログを扱うクラスです．
 */
public class UAccountLog {

  /** 日付を引くためのキー */
  public static final String INT_DATE = "INT_DATE";

  /** 節を引くためのキー */
  public static final String INT_SESSION = "INT_SESSION";

  /** ログイン名を引くためのキー */
  public static final String STRING_LOGIN_NAME = "STRING_LOGIN_NAME";

  /** パスワードを引くためのキー */
  public static final String STRING_PASSWORD = "STRING_PASSWORD";

  /** 当日の売建玉数を引くためのキー */
  public static final String LONG_TODAY_SELL = "LONG_TODAY_SELL";

  /** 当日の買建玉数を引くためのキー */
  public static final String LONG_TODAY_BUY = "LONG_TODAY_BUY";

  /** 前日までの売建玉数を引くためのキー */
  public static final String LONG_YESTERDAY_SELL = "LONG_YESTERDAY_SELL";

  /** 前日までの買建玉数を引くためのキー */
  public static final String LONG_YESTERDAY_BUY = "LONG_YESTERDAY_BUY";

  /** 売建玉数の合計を引くためのキー */
  public static final String LONG_SELL = "LONG_SELL";

  /** 買建玉数の合計を引くためのキー */
  public static final String LONG_BUY = "LONG_BUY";

  /** 初期所持金を引くためのキー */
  public static final String LONG_INITIAL_CASH = "LONG_INITIAL_CASH";

  /** 未実現損益を引くためのキー */
  public static final String LONG_UNREALIZED_PROFIT = "LONG_UNREALIZED_PROFIT";

  /** 実現利益を引くためのキー */
  public static final String LONG_PROFIT = "LONG_PROFIT";

  /** 証拠金額を引くためのキー */
  public static final String LONG_MARGIN = "LONG_MARGIN";

  /** 会費を引くためのキー */
  public static final String LONG_FEE = "LONG_FEE";

  /** 取引所融資額を引くためのキー */
  public static final String LONG_LOAN = "LONG_LOAN";

  /** 融資利息を引くためのキー */
  public static final String LONG_INTEREST = "LONG_INTEREST";

  /** 余裕金額を引くためのキー */
  public static final String LONG_SURPLUS = "LONG_SURPLUS";

  /** 現金残高を引くためのキー */
  public static final String LONG_CASH = "LONG_CASH";

  /** 取引可能状態(UAccount.AVAILABLE/UAccount.UNAVAILABLE)を引くためのキー */
  public static final String INT_STATUS = "INT_STATUS";

  /** 日付 */
  private int fDate;

  /** 節 */
  private int fSession;

  /** 口座情報 */
  private ArrayList fAccountArray;

  /**
   * コンストラクタです．
   * @param date 日付
   * @param session 節
   */
  public UAccountLog(int date, int session) {
    fDate = date;
    fSession = session;
    fAccountArray = new ArrayList();
  }

  /**
   * コンストラクタです．
   * @param date 日付
   * @param session 節
   * @param am 口座管理者
   */
  public UAccountLog(int date, int session, UAccountManager am) {
    this(date, session);
    UExchangeAccount ea = am.getExchangeAccount();
    String userName = ea.getUserName();
    String passwd = ea.getPasswd();
    long todaysSellPosition = 0;
    long todaysBuyPosition = 0;
    long sellPositionUntilYesterday = 0;
    long buyPositionUntilYesterday = 0;
    long sumOfSellPosition = ea.getSellPosition();
    long sumOfBuyPosition = ea.getBuyPosition();
    long initialCash = 0;
    long unrealizedProfit = 0;
    long profit = 0;
    long margin = 0;
    long sumOfFee = 0;
    long loan = 0;
    long sumOfInterest = 0;
    long surplus = 0;
    long cash = ea.getCash();
    int status = UAccount.AVAILABLE;
    HashMap info = makeAccountInfo(date, session, userName, passwd,
                                   todaysSellPosition, todaysBuyPosition,
                                   sellPositionUntilYesterday,
                                   buyPositionUntilYesterday,
                                   sumOfSellPosition, sumOfBuyPosition,
                                   initialCash, unrealizedProfit, profit,
                                   margin, sumOfFee, loan, sumOfInterest,
                                   surplus, cash, status);
    fAccountArray.add(info);
    Enumeration accounts = am.getAccounts();
    while (accounts.hasMoreElements()) {
      UAccount account = (UAccount) accounts.nextElement();
      userName = account.getUserName();
      passwd = account.getPasswd();
      UPosition pos = account.getPosition();
      todaysSellPosition = pos.getSumOfTodaySellPosition();
      todaysBuyPosition = pos.getSumOfTodayBuyPosition();
      sellPositionUntilYesterday = pos.getSumOfSellPositionUntilYesterday();
      buyPositionUntilYesterday = pos.getSumOfBuyPositionUntilYesterday();
      sumOfSellPosition = todaysSellPosition + sellPositionUntilYesterday;
      sumOfBuyPosition = todaysBuyPosition + buyPositionUntilYesterday;
      UBalance bal = account.getTodayBalance();
      initialCash = bal.getInitialCash();
      unrealizedProfit = bal.getUnrealizedProfit();
      profit = bal.getProfit();
      margin = bal.getMargin();
      sumOfFee = bal.getSumOfFee();
      loan = bal.getLoan();
      sumOfInterest = bal.getSumOfInterest();
      surplus = bal.getCash();
      cash = surplus + margin;
      status = account.getStatus();
      info = makeAccountInfo(date, session, userName, passwd,
                             todaysSellPosition, todaysBuyPosition,
                             sellPositionUntilYesterday,
                             buyPositionUntilYesterday,
                             sumOfSellPosition, sumOfBuyPosition,
                             initialCash, unrealizedProfit, profit,
                             margin, sumOfFee, loan, sumOfInterest,
                             surplus, cash, status);
      fAccountArray.add(info);
    }
  }

  /**
   * 口座情報(HashMap)を生成します．
   * @param date 日付
   * @param session 節
   * @param userName ユーザ名
   * @param passwd パスワード
   * @param todaysSellPosition 当日売ポジション
   * @param todaysBuyPosition 当日買ポジション
   * @param sellPositionUntilYesterday 前日までの売ポジション
   * @param buyPositionUntilYesterday 前日までの買ポジション
   * @param sumOfSellPosition 売ポジションの合計
   * @param sumOfBuyPosition 買ポジションの合計
   * @param initialCash 現金の初期値
   * @param unrealizedProfit 未実現損益
   * @param profit 実現損益
   * @param margin 証拠金
   * @param sumOfFee 手数料の合計
   * @param loan 借入金
   * @param sumOfInterest 利子の合計
   * @param surplus 余裕金額
   * @param cash 現金残高
   * @param status 口座状態
   * @return 口座情報
   */
  private HashMap makeAccountInfo(int date, int session,
                                  String userName, String passwd,
                                  long todaysSellPosition,
                                  long todaysBuyPosition,
                                  long sellPositionUntilYesterday,
                                  long buyPositionUntilYesterday,
                                  long sumOfSellPosition, long sumOfBuyPosition,
                                  long initialCash, long unrealizedProfit,
                                  long profit,
                                  long margin, long sumOfFee, long loan,
                                  long sumOfInterest,
                                  long surplus, long cash, int status) {
    HashMap hash = new HashMap();
    hash.put(UAccountLog.INT_DATE, new Integer(date));
    hash.put(UAccountLog.INT_SESSION, new Integer(session));
    hash.put(UAccountLog.STRING_LOGIN_NAME, userName);
    hash.put(UAccountLog.STRING_PASSWORD, passwd);
    hash.put(UAccountLog.LONG_TODAY_SELL, new Long(todaysSellPosition));
    hash.put(UAccountLog.LONG_TODAY_BUY, new Long(todaysBuyPosition));
    hash.put(UAccountLog.LONG_YESTERDAY_SELL,
             new Long(sellPositionUntilYesterday));
    hash.put(UAccountLog.LONG_YESTERDAY_BUY, new Long(buyPositionUntilYesterday));
    hash.put(UAccountLog.LONG_SELL, new Long(sumOfSellPosition));
    hash.put(UAccountLog.LONG_BUY, new Long(sumOfBuyPosition));
    hash.put(UAccountLog.LONG_INITIAL_CASH, new Long(initialCash));
    hash.put(UAccountLog.LONG_UNREALIZED_PROFIT, new Long(unrealizedProfit));
    hash.put(UAccountLog.LONG_PROFIT, new Long(profit));
    hash.put(UAccountLog.LONG_MARGIN, new Long(margin));
    hash.put(UAccountLog.LONG_FEE, new Long(sumOfFee));
    hash.put(UAccountLog.LONG_LOAN, new Long(loan));
    hash.put(UAccountLog.LONG_INTEREST, new Long(sumOfInterest));
    hash.put(UAccountLog.LONG_SURPLUS, new Long(surplus));
    hash.put(UAccountLog.LONG_CASH, new Long(cash));
    hash.put(UAccountLog.INT_STATUS, new Integer(status));
    return hash;
  }

  /**
   * 出力ストリームへ書き出します．
   * @param pw 出力ストリーム
   * @throws IOException
   */
  public void writeTo(PrintWriter pw) throws IOException {
    pw.println(
        "Date,Session,LoginName,Password,TodaysSellPosition,TodaysBuyPosition"
        + ",SellPositionUntilYesterday,BuyPositionUntilYesterday"
        + ",SumOfSellPosition,SumOfBuyPosition"
        + ",InitialCash,UnrealizedProfit,Profit,Margin,SumOfFee"
        + ",Loan,SumOfInterest,Surplus,Cash,Status");
    Iterator itr = fAccountArray.iterator();
    while (itr.hasNext()) {
      HashMap hash = (HashMap) itr.next();
      pw.print(hash.get(UAccountLog.INT_DATE) + ",");
      pw.print(hash.get(UAccountLog.INT_SESSION) + ",");
      pw.print(hash.get(UAccountLog.STRING_LOGIN_NAME) + ",");
      pw.print(hash.get(UAccountLog.STRING_PASSWORD) + ",");
      pw.print(hash.get(UAccountLog.LONG_TODAY_SELL) + ",");
      pw.print(hash.get(UAccountLog.LONG_TODAY_BUY) + ",");
      pw.print(hash.get(UAccountLog.LONG_YESTERDAY_SELL) + ",");
      pw.print(hash.get(UAccountLog.LONG_YESTERDAY_BUY) + ",");
      pw.print(hash.get(UAccountLog.LONG_SELL) + ",");
      pw.print(hash.get(UAccountLog.LONG_BUY) + ",");
      pw.print(hash.get(UAccountLog.LONG_INITIAL_CASH) + ",");
      pw.print(hash.get(UAccountLog.LONG_UNREALIZED_PROFIT) + ",");
      pw.print(hash.get(UAccountLog.LONG_PROFIT) + ",");
      pw.print(hash.get(UAccountLog.LONG_MARGIN) + ",");
      pw.print(hash.get(UAccountLog.LONG_FEE) + ",");
      pw.print(hash.get(UAccountLog.LONG_LOAN) + ",");
      pw.print(hash.get(UAccountLog.LONG_INTEREST) + ",");
      pw.print(hash.get(UAccountLog.LONG_SURPLUS) + ",");
      pw.print(hash.get(UAccountLog.LONG_CASH) + ",");
      pw.println(hash.get(UAccountLog.INT_STATUS) + "");
    }
  }

  /**
   * 入力ストリームから読み込みます．
   * @param br BufferedReader 入力ストリーム
   * @throws IOException
   */
  public void readFrom(BufferedReader br) throws IOException {
    fAccountArray.clear();
    br.readLine(); // skip the header
    String line = null;
    while ( (line = br.readLine()) != null) {
      StringTokenizer st = new StringTokenizer(line, ",");
      int date = Integer.parseInt(st.nextToken());
      int session = Integer.parseInt(st.nextToken());
      int userID = Integer.parseInt(st.nextToken());
      String userName = st.nextToken();
      String passwd = st.nextToken();
      long todaysSellPosition = Long.parseLong(st.nextToken());
      long todaysBuyPosition = Long.parseLong(st.nextToken());
      long sellPositionUntilYesterday = Long.parseLong(st.nextToken());
      long buyPositionUntilYesterday = Long.parseLong(st.nextToken());
      long sumOfSellPosition = Long.parseLong(st.nextToken());
      long sumOfBuyPosition = Long.parseLong(st.nextToken());
      long initialCash = Long.parseLong(st.nextToken());
      long unrealizedProfit = Long.parseLong(st.nextToken());
      long profit = Long.parseLong(st.nextToken());
      long margin = Long.parseLong(st.nextToken());
      long sumOfFee = Long.parseLong(st.nextToken());
      long loan = Long.parseLong(st.nextToken());
      long sumOfInterest = Long.parseLong(st.nextToken());
      long surplus = Long.parseLong(st.nextToken());
      long cash = Long.parseLong(st.nextToken());
      int status = Integer.parseInt(st.nextToken());
      HashMap info = makeAccountInfo(date, session, userName, passwd,
                                     todaysSellPosition, todaysBuyPosition,
                                     sellPositionUntilYesterday,
                                     buyPositionUntilYesterday,
                                     sumOfSellPosition, sumOfBuyPosition,
                                     initialCash, unrealizedProfit, profit,
                                     margin, sumOfFee, loan, sumOfInterest,
                                     surplus, cash, status);
      fAccountArray.add(info);
    }
  }

  /**
   * 口座情報（HashMap）の配列を返します．
   * @return ArrayList 口座情報（HashMap）の配列
   */
  public ArrayList getAccountArray() {
    return fAccountArray;
  }

}
