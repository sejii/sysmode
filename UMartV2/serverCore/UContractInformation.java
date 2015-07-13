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

/**
 * 約定情報を扱うクラスです．
 * 約定情報には, 約定ID, 約定時刻, 約定価格, 約定数量, 約定日,
 * 約定板寄せ回数が含まれます．
 */
public class UContractInformation {

  /** 約定ID */
  private long fContractID;

  /** 約定時間(実時間) */
  private Date fTime;

  /** 約定価格 */
  private long fPrice;

  /** 約定数量 */
  private long fVolume;

  /** 約定日 */
  private int fDate;

  /** 約定節 */
  private int fSession;

  /**
   * 引数で指定された約定ID，約定時刻(実時間)，約定数量，約定日，
   * 約定板寄せ回数で初期化します．
   * ここでは，約定価格は-1に初期化されるため，
   * 後でsetPriceメソッドを用いて適切に設定する必要があります．
   * @param contractID 約定ID
   * @param time 約定時刻(実時間)
   * @param volume 約定数量
   * @param date 約定日
   * @param session 約定節
   */
  public UContractInformation(long contractID, Date time, long volume,
                              int date, int session) {
    fContractID = contractID;
    fTime = time;
    fVolume = volume;
    fPrice = -1;
    fDate = date;
    fSession = session;
  }

  /**
   * 複製を作成します．
   * @return UContractInformationオブジェクトの複製
   */
  public Object clone() {
    UContractInformation info = new UContractInformation(fContractID, fTime, fVolume, fDate, fSession);
    info.setPrice(fPrice);
    return info;
  }

  /**
   * 内部情報を出力します．
   * @param pw 出力先を指定
   */
  void printOn(PrintWriter pw) {
    pw.println("ContractID: " + fContractID);
    pw.println("Time: " + fTime.toString());
    pw.println("Price: " + fPrice);
    pw.println("Date: " + fDate);
    pw.println("Session: " + fSession);
  }

  /**
   * 約定IDを返します．
   * @return 約定ID
   */
  public long getContractID() {
    return fContractID;
  }

  /**
   * 約定時刻を返します．
   * @return 約定時刻(実時間)
   */
  public Date getTime() {
    return fTime;
  }

  /**
   * 約定数量を返します．
   * @return 約定数量
   */
  public long getVolume() {
    return fVolume;
  }

  /**
   * 約定価格を返します．
   * @return 約定価格
   */
  public long getPrice() {
    return fPrice;
  }

  /**
   * 約定日を返します．
   * @return 約定日
   */
  public int getDate() {
    return fDate;
  }

  /**
   * 約定したときの節を返します．
   * @return 約定節
   */
  public int getSession() {
    return fSession;
  }

  /**
   * 約定価格を設定します．
   * @param price 約定価格
   */
  public void setPrice(long price) {
    fPrice = price;
  }

  /**
   * テスト用メインメソッドです．
   */
  public static void main(String args[]) {
    UContractInformation ui = new UContractInformation(1, new Date(), 10, 3, 2);
    ui.setPrice(3000);
    PrintWriter pw = new PrintWriter(System.out, true);
    ui.printOn(pw);
  }

}
