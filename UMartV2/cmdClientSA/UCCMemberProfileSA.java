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
 * ローカルで動作するクライアントが利用する，会員情報を取得するためのSVMPコマンドクラスです．
 */
public class UCCMemberProfileSA extends UCMemberProfileCore implements
    IClientCmdSA {

  /** U-Martオブジェクトへの参照 */
  private UMart fUMart;

  /** ユーザID */
  private int fUserID;

  /**
   * コンストラクタです．
   */
  UCCMemberProfileSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /*
   * (non-Javadoc)
   * @see cmdClientSA.IClientCmdSA#setConnection(serverCore.UMart, int)
   */
  public void setConnection(UMart umart, int userID) {
    fUMart = umart;
    fUserID = userID;
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fData.clear();
    fCommandStatus = fUMart.doMemberProfile(fData, fUserID, fTargetUserId);
    return fCommandStatus;
  }

}
