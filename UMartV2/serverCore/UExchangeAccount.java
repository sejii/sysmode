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

/**
 * 取引所(su)の口座情報を扱うクラスです．
 * 口座情報としては，現金残高，売りポジション，買いポジションを
 * 扱っています．
 */
public class UExchangeAccount {

  /** ユーザーID */
  private final int fUserID = 0;

  /** ユーザー名 */
  private final String fUserName = "su";

  /** パスワード */
  private final String fPasswd = "supasswd";

  /** 現金残高 */
  private long fCash;

  /** 売りポジション (破産者の売りポジションを引き継ぐ) */
  private long fSellPosition;

  /** 買いポジション (破産者の買いポジションを引き継ぐ) */
  private long fBuyPosition;

  /**
   * 初期化された取引所の口座を生成します．
   */
  public UExchangeAccount() {
    fCash = 0;
    fSellPosition = fBuyPosition = 0;
  }

  /**
   * UExchangeAccountの複製を生成します．
   * @return UExchangeAccountオブジェクトの複製
   */
  public Object clone() {
    UExchangeAccount result = new UExchangeAccount();
    result.fCash = fCash;
    result.fSellPosition = fSellPosition;
    result.fBuyPosition = fBuyPosition;
    return result;
  }

  /**
   * 内部情報を出力します．
   * @param pw 出力先
   */
  public void printOn(PrintWriter pw) {
    try {
      pw.println("fUserID = " + fUserID);
      pw.println("fUserName = " + fUserName);
      pw.println("fPasswd = " + fPasswd);
      pw.println("fCash = " + fCash);
      pw.println("fSellPosition = " + fSellPosition);
      pw.println("fBuyPosition = " + fBuyPosition);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
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
    return fUserName;
  }

  /**
   * パスワードを返します．
   * @return パスワード
   */
  public String getPasswd() {
    return fPasswd;
  }

  /**
   * 買いポジションを返します．
   * @return 買いポジション
   */
  public long getBuyPosition() {
    return fBuyPosition;
  }

  /**
   * 売りポジションを返します．
   * @return 売りポジション
   */
  public long getSellPosition() {
    return fSellPosition;
  }

  /**
   * 買いポジションを設定します．
   * @param buyPosition 買いポジション
   */
  public void setBuyPosition(long buyPosition) {
    fBuyPosition = buyPosition;
  }

  /**
   * 売りポジションを設定します．
   * @param sellPosition 売りポジション
   */
  public void setSellPosition(long sellPosition) {
    fSellPosition = sellPosition;
  }

  /**
   * 現金残高を返します．
   * @return 現金残高
   */
  public synchronized long getCash() {
    // System.err.println(Thread.currentThread().getName());
    return fCash;
  }

  /**
   * 現金残高を設定します．
   * @param cash 現金残高
   */
  public synchronized void setCash(long cash) {
    // System.err.println(Thread.currentThread().getName());
    fCash = cash;
  }

  /**
   * cashを現在の現金残高に加えます．
   * @param cash 現金残高の増分
   */
  public synchronized void addCash(long cash) {
    // System.err.println(Thread.currentThread().getName());
    fCash += cash;
    //System.out.println(cash + "," + fCash);
  }

  /**
   * sellPositionを現在の売りポジションに加えます．
   * @param sellPosition 売りポジションの増分
   */
  public void addSellPosition(long sellPosition) {
    fSellPosition += sellPosition;
  }

  /**
   * buyPositionを現在の買いポジションに加えます．
   * @param buyPosition 買いポジションの増分
   */
  public void addBuyPosition(long buyPosition) {
    fBuyPosition += buyPosition;
  }

  /**
   * 売り/買いポジションを0に初期化します．
   */
  public void clearPosition() {
    fSellPosition = 0;
    fBuyPosition = 0;
  }

}
