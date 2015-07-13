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
package serverSA;

import serverCore.ULoginManager;
import serverCore.UMart;
import serverCore.UMemberList;
import serverCore.UPriceInfoDB;

/**
 * スタンドアロン環境用の取引所クラスです．
 */
public class UMartStandAlone extends UMart {

  /**
   * UMartStandAloneオブジェクトの生成および初期化を行います：<br>
   * - エージェントは誰もログインしていない状態<br>
   * - 日付は1，板寄せ回数は1<br>
   * - 状態はBEFORE_TRADING<br>
   * @param members 会員リスト
   * @param priceInfoDB 価格系列データベース
   * @param startPoint 価格系列の何番目のデータを初日のデータとするか
   * @param seed 乱数の種
   * @param maxDate サーバー運用日数
   * @param noOfSessionsPerDay 一日あたりの節数
   */
  public UMartStandAlone(UMemberList members, UPriceInfoDB priceInfoDB,
                         int startPoint, long seed,
                         int maxDate, int noOfSessionsPerDay) {
    super(members, priceInfoDB, startPoint, seed,
          maxDate, noOfSessionsPerDay);
  }

  /**
   * @see serverCore.UMart#createLoginManager()
   */
  protected ULoginManager createLoginManager() {
    return new ULoginManager();
  }

}
