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
package cmdCore;

import java.util.*;

/**
 * 注文依頼のためのSVMPコマンドの抽象クラスです．
 */
public abstract class UCOrderRequestCore implements ICommand {

  /** 注文IDを引くためのキー(値はLongオブジェクト) */
  public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

  /** 注文時刻(実時間)を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_ORDER_TIME = "STRING_ORDER_TIME";

  /** コマンド名 */
  public static final String CMD_NAME = "OrderRequest";

  /** 別名 */
  public static final String CMD_ALIAS = "201";

  /** 銘柄名 */
  protected String fBrandName;

  /** 新規返済区分 */
  protected int fNewRepay;

  /** 売買区分 */
  protected int fSellBuy;

  /** 成行指値区分 */
  protected int fMarketLimit;

  /** 希望取引価格 */
  protected long fPrice;

  /** 希望取引数量 */
  protected long fVolume;

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのHashMap */
  protected HashMap fRequestInfo;

  /**
   * コンストラクタです．
   */
  public UCOrderRequestCore() {
    super();
    fBrandName = "";
    fNewRepay = -1;
    fSellBuy = -1;
    fMarketLimit = -1;
    fPrice = -1;
    fVolume = -1;
    fRequestInfo = new HashMap();
    fStatus = new UCommandStatus();
  }

  /**
   * @see cmdCore.ICommand#isNameEqualTo(String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME) || name.equals(CMD_ALIAS)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @see cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /**
   * @see cmdCore.ICommand#setArguments(StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    try {
      fBrandName = st.nextToken();
      fNewRepay = Integer.parseInt(st.nextToken());
      fSellBuy = Integer.parseInt(st.nextToken());
      fMarketLimit = Integer.parseInt(st.nextToken());
      fPrice = Long.parseLong(st.nextToken());
      fVolume = Long.parseLong(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * コマンド実行に必要な引数を設定します．
   * @param brandName 銘柄名
   * @param newRepay 新規返済区分
   * @param sellBuy 売買区分
   * @param marketLimit　成行指値区分
   * @param price 希望取引価格
   * @param volume　希望取引数量
   */
  public void setArguments(String brandName, int newRepay, int sellBuy,
                           int marketLimit, long price, long volume) {
    fBrandName = brandName;
    fNewRepay = newRepay;
    fSellBuy = sellBuy;
    fMarketLimit = marketLimit;
    fPrice = price;
    fVolume = volume;
  }

  /**
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = fRequestInfo.get(LONG_ORDER_ID) + " "
        + fRequestInfo.get(STRING_ORDER_TIME);
    return result;
  }

  /**
   * 注文依頼情報を返します．
   * @return 注文依頼情報
   */
  public HashMap getResults() {
    return fRequestInfo;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<OrderRequest " + fBrandName + fNewRepay + fSellBuy
                       + fMarketLimit + fPrice + fVolume + ">>");
    System.out.println("OrderID:" + fRequestInfo.get(LONG_ORDER_ID) + ","
                       + "OrderTime:" + fRequestInfo.get(STRING_ORDER_TIME));
  }
}
