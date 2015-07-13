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
package serverNet;

import serverCore.*;

/**
 * ネットワーク環境で動作する取引所です．
 */
public class UMartNetwork extends UMart {

  /** サーバーソケットのポート番号のデフォルト値 */
  public static final int DEFAULT_PORT = 5010;

  /** サーバーソケットのポート番号 */
  private int fPort = DEFAULT_PORT;

  /**
   * デフォルトポートでUMartNetworkオブジェクトの生成および初期化を行います：<br>
   * - エージェントは誰もログインしていない状態<br>
   * - 日付は1，節は1<br>
   * - 状態はBEFORE_TRADING<br>
   * @param members 会員リスト
   * @param priceInfoDB 価格系列データベース
   * @param startPoint 価格系列の開始ポイント
   * @param seed 乱数の種
   * @param maxDate 取引日数
   * @param noOfSessionsPerDay 1日あたりの節数
   */
  public UMartNetwork(UMemberList members, UPriceInfoDB priceInfoDB,
                      int startPoint, long seed,
                      int maxDate, int noOfSessionsPerDay) {
    this(members, priceInfoDB, startPoint, seed, maxDate, noOfSessionsPerDay, DEFAULT_PORT);
  }

  /**
   * UMartNetworkオブジェクトの生成および初期化を行います：<br>
   * - エージェントは誰もログインしていない状態<br>
   * - 日付は1，節は1<br>
   * - 状態はBEFORE_TRADING<br>
   * @param members 会員リスト
   * @param priceInfoDB 価格系列データベース
   * @param startPoint 価格系列の開始ポイント
   * @param seed 乱数の種
   * @param maxDate 取引日数
   * @param noOfSessionsPerDay 1日あたりの節数
   * @param port サーバーソケットのポート番号
   */
  public UMartNetwork(UMemberList members, UPriceInfoDB priceInfoDB,
                      int startPoint, long seed,
                      int maxDate, int noOfSessionsPerDay,
                      int port) {
    super(members, priceInfoDB, startPoint, seed, maxDate, noOfSessionsPerDay);
    fPort = port;
  }

  /**
   * @see serverCore.UMart#createLoginManager()
   */
  protected ULoginManager createLoginManager() {
    return new ULoginManagerNetwork(this);
  }
  
  /**
   * ログインマネージャを起動します．
   */
  public void startLoginManager() {
    ((ULoginManagerNetwork)fLoginManager).startLoop(fPort);
  }

}
