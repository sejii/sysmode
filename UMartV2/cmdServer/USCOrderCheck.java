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

import java.util.*;

import cmdCore.*;
import serverNet.*;

/**
 * サーバー上においてUAgentにより実行される，注文情報を取得するためのSVMPコマンドクラスです．
 */
public class USCOrderCheck extends UCOrderCheckCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * コンストラクタです．
   */
  public USCOrderCheck() {
    super();
    fAgent = null;
    fUMart = null;
  }

  /* (non-Javadoc)
   * @see cmdServer.IServerCmd#setConnection(serverNet.UAgent, serverNet.UMartNetwork)
   */
  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    fAgent = agent;
    fUMart = umart;
  }

  /* (non-Javadoc)
   * @see cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      int userID = fAgent.getLoginStatus().getUserID();
      fData.clear();
      fCommandStatus = fUMart.doOrderCheck(fOrderID, fData);
      if (fCommandStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_USER_ID).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.LONG_ORDER_ID).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.STRING_ORDER_TIME).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.STRING_BRAND_NAME).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_ORDER_DATE).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_ORDER_BOARD_NO).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_NEW_REPAY).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_SELL_BUY).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.INT_MARKET_LIMIT).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.LONG_ORDER_PRICE).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.LONG_ORDER_VOLUME).
                           toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.
                                     LONG_SUM_OF_CONTRACT_VOLUME).toString());
        fAgent.sendMessage(fData.get(UCOrderCheckCore.LONG_CANCEL_VOLUME).
                           toString());
        ArrayList contractInfoArray = (ArrayList) fData.get(UCOrderCheckCore.
            ARRAYLIST_CONTRACT_INFORMATION_ARRAY);
        Iterator itr = contractInfoArray.iterator();
        while (itr.hasNext()) {
          HashMap contractInfo = (HashMap) itr.next();
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.LONG_CONTRACT_ID).
                             toString());
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.
                                              STRING_CONTRACT_TIME).toString());
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.
                                              INT_CONTRACT_DATE).toString());
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.
                                              INT_CONTRACT_BOARD_NO).toString());
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.
                                              LONG_CONTRACT_PRICE).toString());
          fAgent.sendMessage(contractInfo.get(UCOrderCheckCore.
                                              LONG_CONTRACT_VOLUME).toString());
        }
      } else {
        fAgent.sendMessage("+ERROR " + fCommandStatus.getErrorCode());
        fAgent.sendMessage(fCommandStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: ORDERCANCEL <ORDERID>");
    }
    fAgent.flushMessage();
    return fCommandStatus;
  }

}
