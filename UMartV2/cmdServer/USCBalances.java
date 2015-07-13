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
 * サーバー上においてUAgentにより実行される，現金残高照会のためのSVMPコマンドクラスです．
 */
public class USCBalances extends UCBalancesCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * コンストラクタです．
   */
  public USCBalances() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      int userID = fAgent.getLoginStatus().getUserID();
      fTodayInfo.clear();
      fYesterdayInfo.clear();
      fStatus = fUMart.doBalances(fTodayInfo, fYesterdayInfo, userID);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fTodayInfo.get(LONG_CASH).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_MARGIN).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_UNREALIZED_PROFIT).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_SETTLED_PROFIT).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_FEE).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_INTEREST).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_LOAN).toString());
        fAgent.sendMessage(fTodayInfo.get(LONG_SURPLUS).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_CASH).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_MARGIN).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_UNREALIZED_PROFIT).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_SETTLED_PROFIT).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_FEE).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_INTEREST).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_LOAN).toString());
        fAgent.sendMessage(fYesterdayInfo.get(LONG_SURPLUS).toString());
      } else {
        fAgent.sendMessage("+ERROR " + fStatus.getErrorCode());
        fAgent.sendMessage(fStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: BALANCES");
    }
    fAgent.flushMessage();
    return fStatus;
  }

  /**
   * @see cmdServer.IServerCmd#setConnection(UAgentForNetworkClient, UMartNetwork)
   */
  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    fAgent = agent;
    fUMart = umart;
  }

}
