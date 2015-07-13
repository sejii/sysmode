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
package gui;

import java.io.*;
import java.util.*;

import log.UCsvUtility;

import serverCore.*;

/**
 * 制度設定を扱うクラスです．
 */

public class UInstitutionSetting {

  /** 初期資産 */
  private long fInitialCash = UMart.DEFAULT_INITIAL_CASH;

  /** 取引単位 */
  private long fTradingUnit = UMart.DEFAULT_TRADING_UNIT;

  /** 単位取引あたりの手数料 */
  private long fFeePerUnit = UMart.DEFAULT_FEE_PER_UNIT;

  /** 証拠金率 */
  private long fMarginRate = UMart.DEFAULT_MARGIN_RATE;

  /** 借り入れ限度額 */
  private long fMaxLoan = UMart.DEFAULT_MAX_LOAN;

  /** 借り入れ金利 */
  private double fInterest = UMart.DEFAULT_INTEREST;

  public UInstitutionSetting() {
  }

  public void readFrom(BufferedReader br) throws IOException {
    br.readLine(); // skip header
    ArrayList array = UCsvUtility.split(br.readLine());
    Iterator itr = array.iterator();
    fInitialCash = Long.parseLong( (String) itr.next());
    fTradingUnit = Long.parseLong( (String) itr.next());
    fFeePerUnit = Long.parseLong( (String) itr.next());
    fMarginRate = Long.parseLong( (String) itr.next());
    fMaxLoan = Long.parseLong( (String) itr.next());
    fInterest = Double.parseDouble( (String) itr.next());
  }

  public void writeTo(PrintWriter pw) throws IOException {
    pw.println("InitialCash,TradingUnit,FeePerUnit,MarginRate,MaxLoan,Interest");
    pw.println(fInitialCash + "," + fTradingUnit + "," + fFeePerUnit + ","
               + fMarginRate + "," + fMaxLoan + "," + fInterest);
  }

  public void printOn() {
    System.out.println(
        "InitialCash,TradingUnit,FeePerUnit,MarginRate,MaxLoan,Interest");
    System.out.println(fInitialCash + "," + fTradingUnit + "," + fFeePerUnit +
                       ","
                       + fMarginRate + "," + fMaxLoan + "," + fInterest);
  }

  public long getInitialCash() {
    return fInitialCash;
  }

  public long getTradingUnit() {
    return fTradingUnit;
  }

  public long getFeePerUnit() {
    return fFeePerUnit;
  }

  public long getMarginRate() {
    return fMarginRate;
  }

  public long getMaxLoan() {
    return fMaxLoan;
  }

  public double getInterest() {
    return fInterest;
  }

  public void setInitialCash(long initialCash) {
    fInitialCash = initialCash;
  }

  public void setTradingUnit(long tradingUnit) {
    fTradingUnit = tradingUnit;
  }

  public void setFeePerUnit(long feePerUnit) {
    fFeePerUnit = feePerUnit;
  }

  public void setMarginRate(long marginRate) {
    fMarginRate = marginRate;
  }

  public void setMaxLoan(long maxLoan) {
    fMaxLoan = maxLoan;
  }

  public void setInterest(double interest) {
    fInterest = interest;
  }

  public static void main(String[] args) {
    UInstitutionSetting institutionLog = new UInstitutionSetting();
    try {
      BufferedReader br = new BufferedReader(new FileReader(
          "resources/csv/Institutions.csv"));
      institutionLog.readFrom(br);
      br.close();
      PrintWriter pw = new PrintWriter(System.out, true);
      institutionLog.writeTo(pw);
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(5);
    }
  }

}