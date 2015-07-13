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

import java.io.*;
import java.util.*;

import cmdCore.*;

/**
 * 一般のメンバーの口座情報を扱うクラスです．
 * 口座情報としては, ユーザーID, ユーザー名, 当日収支, 前日収支,
 * 取引可能/不可能の状態, ポジションを扱っています．
 */
public class UAccount {

  /** 「取引き可能」状態を表す定数 */
  public static final int AVAILABLE = 1;

  /** 「取引き不可能」状態を表す定数 */
  public static final int UNAVAILABLE = -1;

  /** ユーザーID (0: 取引所(su), 1～: 一般ユーザー) */
  private int fUserID;

  /** ユーザー情報 (UMemberLogで定義されたもの) */
  private HashMap fUserInfo;

  /** 当日の収支 */
  private UBalance fTodayBalance;

  /** 前日の収支 */
  private UBalance fYesterdayBalance;

  /** 取引き可能/不可能状態 (UAccount.AVAILABLE / UAccount.UNAVAILABLE) */
  private int fStatus;

  /** ポジション */
  private UPosition fPosition;

  /** 口座履歴 */
  private ArrayList fAccountHistory;

  /**
   * 引数で指定された情報で初期化された口座を作成します．
   * 後に、initializeメソッドを呼び出して初期化する必要があります．
   * @param userID ユーザーID
   * @param userInfo ユーザー情報 (UMemberLogで定義されたもの)
   * @param noOfSessionsPerDay 一日あたりの節数
   */
  public UAccount(int userID, HashMap userInfo, int noOfSessionsPerDay) {
    fUserID = userID;
    fUserInfo = userInfo;
    long initialCash = ( (Long) fUserInfo.get(UMemberList.LONG_INITIAL_CASH)).
        longValue();
    fTodayBalance = new UBalance();
    fTodayBalance.setInitialCash(initialCash);
    fYesterdayBalance = new UBalance();
    fStatus = UAccount.AVAILABLE;
    fPosition = new UPosition(noOfSessionsPerDay);
    fAccountHistory = new ArrayList();
  }

  /**
   * UAccountオブジェクトの複製を生成します．
   * @return UAccountオブジェクトの複製
   */
  public Object clone() {
    UAccount result = new UAccount(fUserID, fUserInfo, fPosition.getNoOfSessionsPerDay());
    result.fTodayBalance = (UBalance) fTodayBalance.clone();
    result.fYesterdayBalance = (UBalance) fYesterdayBalance.clone();
    result.fStatus = fStatus;
    result.fPosition = (UPosition) fPosition.clone();
    return result;
  }

  /**
   * ユーザーIDを返します．
   * @return ユーザーID
   */
  public int getUserID() {
    return fUserID;
  }

  /**
   * ユーザー名を返します．
   * @return ユーザー名
   */
  public String getUserName() {
    return (String) fUserInfo.get(UMemberList.STRING_LOGIN_NAME);
  }

  /**
   * エージェント属性（Human or Machine）を返します．
   * @return エージェント属性（Human or Machine）
   */
  public String getAttribute() {
    return (String) fUserInfo.get(UMemberList.STRING_ATTRIBUTE);
  }

  /**
   * 接続の種類（Remote or Local）を返します．
   * @return 接続の種類（Remote or Local）
   */
  public String getConnection() {
    return (String) fUserInfo.get(UMemberList.STRING_CONNECTION);
  }

  /**
   * アクセス制限を返します．
   * @return アクセス制限
   */
  public ArrayList getAccess() {
    return (ArrayList) fUserInfo.get(UMemberList.ARRAY_LIST_ACCESS);
  }

  /**
   * 実際の名前を返します．
   * @return 実際の名前
   */
  public String getRealName() {
    return (String) fUserInfo.get(UMemberList.STRING_REAL_NAME);
  }

  /**
   * エージェントのシステムパラメータを返します．
   * @return エージェントのシステムパラメータ
   */
  public ArrayList getSystemParameters() {
    return (ArrayList) fUserInfo.get(UMemberList.ARRAY_LIST_SYSTEM_PARAMETERS);
  }

  /**
   * 乱数の種を返します．
   * @return 乱数の種
   */
  public int getSeed() {
    return ( (Integer) fUserInfo.get(UMemberList.INT_SEED)).intValue();
  }

  /**
   * 初期資産を返します．
   * @return 初期資産
   */
  public long getInitialCash() {
    return ( (Long) fUserInfo.get(UMemberList.LONG_INITIAL_CASH)).longValue();
  }

  /**
   * 借り入れ金利を返します．
   * @return 借り入れ金利（１日あたり）
   */
  public double getInterest() {
    return ( (Double) fUserInfo.get(UMemberList.DOUBLE_INTEREST)).doubleValue() /
        365.0;
  }

  /**
   * 金利を返します．
   * @return 金利
   */
  public double getInterestPerYear() {
    return ( (Double) fUserInfo.get(UMemberList.DOUBLE_INTEREST)).doubleValue();
  }

  /**
   * 単位取引あたりの証拠金を返します．
   * @return 証拠金
   */
  public long getMarginRate() {
    return ( (Long) fUserInfo.get(UMemberList.LONG_MARGIN_RATE)).longValue();
  }

  /**
   * 単位取引あたりの手数料を返します．
   * @return 単位取引あたりの手数料
   */
  public long getFeePerUnit() {
    return ( (Long) fUserInfo.get(UMemberList.LONG_FEE_PER_UNIT)).longValue();
  }

  /**
   * 最大借入金を返します．
   * @return 最大借入金
   */
  public long getMaxLoan() {
    return ( (Long) fUserInfo.get(UMemberList.LONG_MAX_LOAN)).longValue();
  }

  /**
   * 取引単位を返します．
   * @return 取引単位
   */
  public long getTradingUnit() {
    return ( (Long) fUserInfo.get(UMemberList.LONG_TRADING_UNIT)).longValue();
  }

  /**
   * ユーザー情報(UMemberLogで定義されたもの)を返します．
   * @return ユーザー情報
   */
  public HashMap getUserInfo() {
    return fUserInfo;
  }

  /**
   * パスワードを返します．
   * @return パスワード
   */
  public String getPasswd() {
    return (String) fUserInfo.get(UMemberList.STRING_PASSWORD);
  }

  /**
   * 当日収支をします．
   * @return 当日収支
   */
  public UBalance getTodayBalance() {
    return fTodayBalance;
  }

  /**
   * 前日収支をします．
   * @return 前日収支
   */
  public UBalance getYesterdayBalance() {
    return fYesterdayBalance;
  }

  /**
   * 口座状態をします．
   * @return 取引き可能ならばUAccount.AVAILABLE,
   *         取引き不可能ならばUAccount.UNAVAILABLE
   */
  public int getStatus() {
    return fStatus;
  }

  /**
   * ポジションをします．
   * @return ポジション
   */
  public UPosition getPosition() {
    return fPosition;
  }

  /**
   * 当日収支を設定します．
   * @param todayBalance 当日収支
   */
  public void setTodayBalance(UBalance todayBalance) {
    fTodayBalance = todayBalance;
  }

  /**
   * 前日収支を設定します．
   * @param yesterdayBalance 前日収支
   */
  public void setYesterdayBalance(UBalance yesterdayBalance) {
    fYesterdayBalance = yesterdayBalance;
  }

  /**
   * 口座状態を設定します．
   * @param status 取引き可能: UAccount.AVAILABLE,
   *               取引き不可能: UAccount.UNAVAILABLE
   */
  public void setStatus(int status) {
    fStatus = status;
  }

  /**
   * ポジションを設定します．
   * @param position ポジション
   */
  public void setPosition(UPosition position) {
    fPosition = position;
  }

  /**
   * 口座履歴へのバックアップと前日収支に当日収支のバックアップを行います．
   * この操作により, 前日収支の情報は消えるので注意してください．
   * @param step ステップ数
   */
  public void backup(int step) {
    HashMap data = new HashMap();
    String name = getUserName();
    data.put(UCAccountHistoryCore.STRING_NAME, name); // ユーザー名
    int userId = fUserID;
    data.put(UCAccountHistoryCore.INT_USER_ID, new Integer(userId)); // ユーザーID
    data.put(UCAccountHistoryCore.INT_STEP, new Integer(step)); // ステップ数
    long unrealizedProfit = fTodayBalance.getUnrealizedProfit();
    data.put(UCAccountHistoryCore.LONG_UNREALIZED_PROFIT,
             new Long(unrealizedProfit)); // 未実現利益
    long sellPosition = fPosition.getTotalSellPosition();
    data.put(UCAccountHistoryCore.LONG_SELL_POSITION, new Long(sellPosition)); // 買ポジションの合計
    long buyPosition = fPosition.getTotalBuyPosition();
    data.put(UCAccountHistoryCore.LONG_BUY_POSITION, new Long(buyPosition)); // 売ポジションの合計
    fAccountHistory.add(data); // 口座情報
    fYesterdayBalance = (UBalance) fTodayBalance.clone();
  }

  /**
   * ポジションを初期化します．
   */
  public void clearPosition() {
    fPosition.clear();
  }

  /**
   * 内部情報を出力します．
   * @param pw 出力先
   */
  public void printOn(PrintWriter pw) {
    try {
      pw.println("fUserID = " + fUserID);
      pw.println("fUserInfo = " + fUserInfo);
      pw.println("fStatus = " + fStatus);
      pw.println("[Position]");
      fPosition.printOn(pw);
      pw.println("[Today's Balance]");
      fTodayBalance.printOn(pw);
      pw.println("[Yesterday's Balance]");
      fYesterdayBalance.printOn(pw);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }

  /**
   * 口座履歴を返します．
   * @return 口座履歴
   */
  public ArrayList getAccountHistory() {
    return fAccountHistory;
  }

}
