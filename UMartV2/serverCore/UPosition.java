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
 * ポジション情報を扱うクラスです．
 * ポジション情報としては，前日までの売りポジションの合計，
 * 前日までの買いポジションの合計，一日あたりの板寄せ回数，
 * 当日の各板寄せごとの売りポジション, 当日の各板寄せごとの買いポジションを
 * 扱っています．
 */
public class UPosition {

  /** 前日までの売りポジションの合計 */
  private long fSumOfSellPositionUntilYesterday;

  /** 前日までの買いポジションの合計 */
  private long fSumOfBuyPositionUntilYesterday;

  /** 一日あたりの節数 */
  private int fNoOfSessionsPerDay;

  /** 当日の各節ごとの売りポジション */
  private long fTodaySellPositions[];

  /** 当日の各節ごとの買いポジション */
  private long fTodayBuyPositions[];

  /**
   * 空のポジション情報を生成します．後で，setNoOfBoardsメソッドを
   * 呼び出して正しく初期化する必要があります．
   */
  public UPosition() {
    fNoOfSessionsPerDay = 0;
    fTodaySellPositions = null;
    fTodayBuyPositions = null;
    fSumOfSellPositionUntilYesterday = 0;
    fSumOfBuyPositionUntilYesterday = 0;
  }

  /**
   * ポジション情報を初期化します．
   * @param noOfSessionsPerDay 一日あたりの節数
   */
  public UPosition(int noOfSessionsPerDay) {
    fNoOfSessionsPerDay = noOfSessionsPerDay;
    fTodaySellPositions = new long[fNoOfSessionsPerDay];
    fTodayBuyPositions = new long[fNoOfSessionsPerDay];
    fSumOfSellPositionUntilYesterday = 0;
    fSumOfBuyPositionUntilYesterday = 0;
    clear();
  }

  /**
   * 複製を返します．
   * @return 複製
   */
  public Object clone() {
    UPosition result = new UPosition(fNoOfSessionsPerDay);
    for (int i = 1; i <= fNoOfSessionsPerDay; ++i) {
      result.fTodaySellPositions[i - 1] = fTodaySellPositions[i - 1];
      result.fTodayBuyPositions[i - 1] = fTodayBuyPositions[i - 1];
    }
    result.fSumOfSellPositionUntilYesterday = fSumOfSellPositionUntilYesterday;
    result.fSumOfBuyPositionUntilYesterday = fSumOfBuyPositionUntilYesterday;
    return result;
  }

  /**
   * 前日までの売りポジションの合計を返します．
   * @return 前日までの売りポジションの合計
   */
  public long getSumOfSellPositionUntilYesterday() {
    return fSumOfSellPositionUntilYesterday;
  }

  /**
   * 前日までの買いポジションの合計を返します．
   * @return 前日までの買いポジションの合計
   */
  public long getSumOfBuyPositionUntilYesterday() {
    return fSumOfBuyPositionUntilYesterday;
  }

  /**
   * 一日あたりの板寄せ回数を返します．
   * @return 一日あたりの板寄せ回数
   */
  public int getNoOfSessionsPerDay() {
    return fNoOfSessionsPerDay;
  }

  /**
   * 引数で指定された節の売りポジションを返します．
   * @param session 節
   * @return 売りポジション
   */
  public long getTodaySellPosition(int session) {
    return fTodaySellPositions[session - 1];
  }

  /**
   * 引数で指定された節の買いポジションを返します．
   * @param session 節
   * @return 買いポジション
   */
  public long getTodayBuyPosition(int session) {
    return fTodayBuyPositions[session - 1];
  }

  /**
   * 前日までの売りポジションの合計を設定します．
   * @param sellPosition 前日までの売りポジション
   */
  public void setSumOfSellPositionUntilYesterday(long sellPosition) {
    fSumOfSellPositionUntilYesterday = sellPosition;
  }

  /**
   * 前日までの買いポジションの合計を設定します．
   * @param buyPosition 前日までの買いポジション
   */
  public void setSumOfBuyPositionUntilYesterday(long buyPosition) {
    fSumOfBuyPositionUntilYesterday = buyPosition;
  }

  /**
   * 一日あたりの節数を設定します．
   * @param noOfSessionsPerDay 一日あたりの節数
   */
  public void setNoOfSessionsPerDay(int noOfSessionsPerDay) {
    fNoOfSessionsPerDay = noOfSessionsPerDay;
    fTodaySellPositions = new long[fNoOfSessionsPerDay];
    fTodayBuyPositions = new long[fNoOfSessionsPerDay];
    clear();
  }

  /**
   * 当日の売りポジションを設定します．
   * @param sellPosition 売りポジション
   * @param session 節
   */
  public void setTodaySellPosition(long sellPosition, int session) {
    fTodaySellPositions[session - 1] = sellPosition;
  }

  /**
   * 当日の買いポジションを設定します．
   * @param buyPosition 買いポジション
   * @param session 節
   */
  public void setTodayBuyPosition(long buyPosition, int session) {
    fTodayBuyPositions[session - 1] = buyPosition;
  }

  /**
   * 当日のsession節の買いポジションにbuyPositionを加算します．
   * @param buyPosition 買いポジションの増分
   * @param session 節
   */
  public void addToTodayBuyPosition(long buyPosition, int session) {
    fTodayBuyPositions[session - 1] += buyPosition;
  }

  /**
   * 当日のsession節の売りポジションにsellPositionを加算します．
   * @param sellPosition 売りポジションの増分
   * @param session 節
   */
  public void addToTodaySellPosition(long sellPosition, int session) {
    fTodaySellPositions[session - 1] += sellPosition;
  }

  /**
   * 当日の売りポジションの合計を返します．
   * @return 当日の売りポジションの合計
   */
  public long getSumOfTodaySellPosition() {
    long result = 0;
    for (int i = 1; i <= fNoOfSessionsPerDay; ++i) {
      result += getTodaySellPosition(i);
    }
    return result;
  }

  /**
   * 当日の買いポジションの合計を返します．
   * @return 当日の買いポジションの合計
   */
  public long getSumOfTodayBuyPosition() {
    long result = 0;
    for (int i = 1; i <= fNoOfSessionsPerDay; ++i) {
      result += getTodayBuyPosition(i);
    }
    return result;
  }

  /**
   * 現在の売りポジションの合計を返します．
   * @return 現在の売りポジションの合計
   */
  public long getTotalSellPosition() {
    return getSumOfSellPositionUntilYesterday() + getSumOfTodaySellPosition();
  }

  /**
   * 現在の買いポジションの合計を返します．
   * @return 現在の買いポジションの合計
   */
  public long getTotalBuyPosition() {
    return getSumOfBuyPositionUntilYesterday() + getSumOfTodayBuyPosition();
  }

  /**
   * 当日の売り/買いポジションを前日までの売り/買いポジションに
   * 加算した後, ゼロにクリアします．
   */
  public void collect() {
    long totalSell = getSumOfSellPositionUntilYesterday();
    long totalBuy = getSumOfBuyPositionUntilYesterday();
    for (int i = 1; i <= fNoOfSessionsPerDay; i++) {
      totalSell += getTodaySellPosition(i);
      totalBuy += getTodayBuyPosition(i);
      setTodaySellPosition(0, i);
      setTodayBuyPosition(0, i);
    }
    setSumOfSellPositionUntilYesterday(totalSell);
    setSumOfBuyPositionUntilYesterday(totalBuy);
  }

  /**
   * 前日までの売り/買いポジションの合計と当日の売り/買いポジションを
   * ゼロにクリアします．
   */
  public void clear() {
    setSumOfSellPositionUntilYesterday(0);
    setSumOfBuyPositionUntilYesterday(0);
    for (int i = 1; i <= fNoOfSessionsPerDay; ++i) {
      setTodaySellPosition(0, i);
      setTodayBuyPosition(0, i);
    }
  }

  /**
   * 内部情報を出力します．
   * @param w 出力先
   */
  public void printOn(Writer w) {
    try {
      w.write("fSumOfSellPositionUntilYesterday = "
              + fSumOfSellPositionUntilYesterday + "\n");
      w.write("fSumOfBuyPositionUntilYesterday = "
              + fSumOfBuyPositionUntilYesterday + "\n");
      w.write("fNoOfBoards = " + fNoOfSessionsPerDay + "\n");
      for (int i = 1; i <= fNoOfSessionsPerDay; i++) {
        w.write("fTodaySellPosition[" + i + "] = "
                + fTodaySellPositions[i - 1] + ", ");
        w.write("fTodayBuyPosition[" + i + "] = "
                + fTodayBuyPositions[i - 1] + "\n");
      }
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }

}
