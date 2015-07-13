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
 * サーバー上においてUAgentにより実行される，板情報を取得するためのSVMPコマンドクラスです．
 */
public class USCBoardInformation extends UCBoardInformationCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  private UMartNetwork fUMart;

  /**
   * コンストラクタです．
   */
  public USCBoardInformation() {
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
      fBoardInfo.clear();
      fBoardInfo.clear();
      fStatus = fUMart.doBoardInformation(fBoardInfo);
      if (fStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fBoardInfo.get(STRING_LAST_UPDATE_TIME).toString());
        ArrayList infoArray = (ArrayList) fBoardInfo.get(UCBoardInformationCore.
            ARRAYLIST_BOARD);
        // 以下，成行注文情報
        {
          Iterator itr = infoArray.iterator();
          while (itr.hasNext()) {
            HashMap hm = (HashMap) itr.next();
            if ( ( (Long) hm.get(LONG_PRICE)).longValue() < 0) {
              fAgent.sendMessage(hm.get(LONG_SELL_VOLUME).toString() + " "
                                 + hm.get(LONG_BUY_VOLUME).toString());
              break;
            }
          }
        }
        // 以下，成行注文情報以外
        {
          Iterator itr = infoArray.iterator();
          while (itr.hasNext()) {
            HashMap hm = (HashMap) itr.next();
            if ( ( (Long) hm.get(LONG_PRICE)).longValue() >= 0) {
              fAgent.sendMessage(hm.get(LONG_PRICE).toString() + " "
                                 + hm.get(LONG_SELL_VOLUME).toString() + " "
                                 + hm.get(LONG_BUY_VOLUME).toString());
            }
          }
        }
      } else {
        fAgent.sendMessage("+ERROR " + fStatus.getErrorCode());
        fAgent.sendMessage(fStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: ORDERCANCEL <ORDERID>");
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
