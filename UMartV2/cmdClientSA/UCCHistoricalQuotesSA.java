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
package cmdClientSA;

import cmdCore.*;
import serverCore.*;

/**
 * ローカルで動作するクライアントが利用する，過去の日足での価格推移を取得するためのSVMPコマンドクラスです．
 */
public class UCCHistoricalQuotesSA extends UCHistoricalQuotesCore implements
    IClientCmdSA {

  /** サーバーへの参照 */
  private UMart fUMart;

  /** ユーザーID */
  private int fUserID;

  /**
   * コンストラクタです．
   */
  public UCCHistoricalQuotesSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fQuotesArray.clear();
    fStatus = fUMart.doHistoricalQuotes(fQuotesArray, fBrandName, fNoOfDays);
    return fStatus;
  }

  /**
   * @see cmdClientSA.IClientCmdSA#setConnection(UMart, int)
   */
  public void setConnection(UMart umart, int userID) {
    fUMart = umart;
    fUserID = userID;
  }
}
