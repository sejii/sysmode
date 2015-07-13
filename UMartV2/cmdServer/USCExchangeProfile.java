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
package cmdServer;

import cmdCore.*;
import serverNet.*;

/**
 * サーバー上においてUAgentにより実行される，取引所情報を取得するためのSVMPコマンドクラスです．
 * 取引所情報とは，取引所の保有現金，取引所の管理下の売ポジション，買ポジション，会員数のことです．
 */
public class USCExchangeProfile extends UCExchangeProfileCore implements
    IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  UMartNetwork fUMart;

  /**
   * コンストラクタです．
   */
  public USCExchangeProfile() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /*
   * (non-Javadoc)
   * @see cmdServer.IServerCmd#setConnection(serverNet.UAgentForNetworkClient, serverNet.UMartNetwork)
   */
  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    fAgent = agent;
    fUMart = umart;
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      int userID = fAgent.getLoginStatus().getUserID();
      fData.clear();
      fCommandStatus = fUMart.doExchangeProfile(fData, userID);
      if (fCommandStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.LONG_CASH).toString()); // 保有現金
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.LONG_SELL_POSITION).toString()); // 取引所の管理下の売ポジション
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.LONG_BUY_POSITION).toString()); // 取引所の管理下の買ポジション
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.INT_NO_OF_MEMBERS).toString()); // 会員数
      } else {
        fAgent.sendMessage("+ERROR " + fCommandStatus.getErrorCode());
        fAgent.sendMessage(fCommandStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: EXCHANGEPROFILE");
    }
    fAgent.flushMessage();
    return fCommandStatus;
  }

}
