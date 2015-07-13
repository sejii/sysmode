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
 * 収支情報を扱うクラスです．
 * 収支情報としては，初期所持金，借入金，未実現損益，預託証拠金，
 * 総支払い手数料，総支払い金利，保有現金，実現損益を扱っています．
 */
public class UBalance {

  /** 初期所持金 */
  private long fInitialCash;

  /** 借入金 */
  private long fLoan;

  /** 未実現損益 */
  private long fUnrealizedProfit;

  /** 預託証拠金 */
  private long fMargin;

  /** 総支払い手数料 */
  private long fSumOfFee;

  /** 総支払い金利 */
  private long fSumOfInterest;

  /** 保有現金 */
  private long fCash;

  /** 実現損益 */
  private long fProfit;

  /**
   * 収支情報を作成します．
   */
  public UBalance() {
    fInitialCash = 0;
    fLoan = 0;
    fUnrealizedProfit = 0;
    fMargin = 0;
    fSumOfFee = 0;
    fSumOfInterest = 0;
    fCash = 0;
    fProfit = 0;
  }

  /**
   * 複製を返します．
   * @return 複製
   */
  public Object clone() {
    UBalance result = new UBalance();
    result.fInitialCash = fInitialCash;
    result.fLoan = fLoan;
    result.fUnrealizedProfit = fUnrealizedProfit;
    result.fMargin = fMargin;
    result.fSumOfFee = fSumOfFee;
    result.fSumOfInterest = fSumOfInterest;
    result.fCash = fCash;
    result.fProfit = fProfit;
    return result;
  }

  /**
   * 内部情報を出力します．
   * @param pw 出力先
   */
  public void printOn(PrintWriter pw) {
    try {
      pw.println("fInitialCash = " + fInitialCash);
      pw.println("fCash = " + fCash);
      pw.println("fProfit = " + fProfit);
      pw.println("fUnrealizedProfit = " + fUnrealizedProfit);
      pw.println("fMargin = " + fMargin);
      pw.println("fSumOfFee = " + fSumOfFee);
      pw.println("fLoan = " + fLoan);
      pw.println("fSumOfInterest = " + fSumOfInterest);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }

  /**
   * 初期所持金を返します．
   * @return 初期所持金
   */
  public long getInitialCash() {
    return fInitialCash;
  }

  /**
   * 借入金を返します．
   * @return 借入金
   */
  public long getLoan() {
    return fLoan;
  }

  /**
   * 未実現損益を返します．
   * @return 未実現損益
   */
  public long getUnrealizedProfit() {
    return fUnrealizedProfit;
  }

  /**
   * 預託証拠金を返します．
   * @return 預託証拠金
   */
  public long getMargin() {
    return fMargin;
  }

  /**
   * 総支払い手数料を返します．
   * @return 総支払い手数料
   */
  public long getSumOfFee() {
    return fSumOfFee;
  }

  /**
   * 総支払金利を返します．
   * @return 総支払い金利
   */
  public long getSumOfInterest() {
    return fSumOfInterest;
  }

  /**
   * 保有現金を返します．
   * @return 保有現金
   */
  public long getCash() {
    return fCash;
  }

  /**
   * 実現損益を返します．
   * @return 実現損益
   */
  public long getProfit() {
    return fProfit;
  }

  /**
   * 初期所持金を設定します．
   * @param initialCash 初期所持金
   */
  public void setInitialCash(long initialCash) {
    fInitialCash = initialCash;
  }

  /**
   * 借入金を設定します．
   * @param loan 借入金
   */
  public void setLoan(long loan) {
    fLoan = loan;
  }

  /**
   * 未実現損益を設定します．
   * @param unrealizedProfit 未実現損益
   */
  public void setUnrealizedProfit(long unrealizedProfit) {
    fUnrealizedProfit = unrealizedProfit;
  }

  /**
   * 預託証拠金を設定します．
   * @param margin 預託証拠金
   */
  public void setMargin(long margin) {
    fMargin = margin;
  }

  /**
   * 総支払い手数料を設定します．
   * @param sumOfFee 総支払い手数料
   */
  public void setSumOfFee(long sumOfFee) {
    fSumOfFee = sumOfFee;
  }

  /**
   * 総支払い金利を設定します．
   * @param interest 総支払い金利
   */
  public void setSumOfInterest(long interest) {
    fSumOfInterest = interest;
  }

  /**
   * 実現損益を設定します．
   * @param profit 実現損益
   */
  public void setProfit(long profit) {
    fProfit = profit;
  }

  /**
   * 保有現金を更新します．
   */
  public void updateCash() {
    fCash = fInitialCash + fUnrealizedProfit + fProfit + fLoan
        - fMargin - fSumOfFee - fSumOfInterest;
  }

}
