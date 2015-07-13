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

import java.util.*;

import cmdCore.*;
import serverCore.*;

/**
 * 取引所ローカルで動作するクライアント用のプロトコルクラスです．
 */
public class UProtocolForLocalClient extends UProtocolCore {

	/**
	 * コンストラクタです．
	 */
  public UProtocolForLocalClient() {
  	super();
  	fCommandHash.put(UCMarketStatusCore.CMD_NAME, new UCCMarketStatusSA());
    fCommandHash.put(UCServerTimeCore.CMD_NAME, new UCCServerTimeSA());
    fCommandHash.put(UCOrderRequestCore.CMD_NAME, new UCCOrderRequestSA());
    fCommandHash.put(UCOrderCancelCore.CMD_NAME, new UCCOrderCancelSA());
    fCommandHash.put(UCOrderCheckCore.CMD_NAME, new UCCOrderCheckSA());
    fCommandHash.put(UCOrderIDHistoryCore.CMD_NAME, new UCCOrderIDHistorySA());
    fCommandHash.put(UCOrderStatusCore.CMD_NAME, new UCCOrderStatusSA());
    fCommandHash.put(UCExecutionsCore.CMD_NAME, new UCCExecutionsSA());
    fCommandHash.put(UCBalancesCore.CMD_NAME, new UCCBalancesSA());
    fCommandHash.put(UCTodaysQuotesCore.CMD_NAME, new UCCTodaysQuotesSA());
    fCommandHash.put(UCBoardInformationCore.CMD_NAME, new UCCBoardInformationSA());
    fCommandHash.put(UCSpotPriceCore.CMD_NAME, new UCCSpotPriceSA());
    fCommandHash.put(UCFuturePriceCore.CMD_NAME, new UCCFuturePriceSA());
    fCommandHash.put(UCHistoricalQuotesCore.CMD_NAME, new UCCHistoricalQuotesSA());
    fCommandHash.put(UCServerDateCore.CMD_NAME, new UCCServerDateSA());
    fCommandHash.put(UCScheduleCore.CMD_NAME, new UCCScheduleSA());
    fCommandHash.put(UCPositionCore.CMD_NAME, new UCCPositionSA());
    fCommandHash.put(UCAllBalancesCore.CMD_NAME, new UCCAllBalancesSA());
    fCommandHash.put(UCAllPositionsCore.CMD_NAME, new UCCAllPositionsSA());
    fCommandHash.put(UCBoardDataCore.CMD_NAME, new UCCBoardDataSA());
    fCommandHash.put(UCSpotIntervalCore.CMD_NAME, new UCCSpotIntervalSA());
    fCommandHash.put(UCSetSpotDateCore.CMD_NAME, new UCCSetSpotDateSA());
    fCommandHash.put(UCServerStatusCore.CMD_NAME, new UCCServerStatusSA());
    fCommandHash.put(UCMemberProfileCore.CMD_NAME, new UCCMemberProfileSA());
    fCommandHash.put(UCExchangeProfileCore.CMD_NAME, new UCCExchangeProfileSA());
    fCommandHash.put(UCAccountHistoryCore.CMD_NAME, new UCCAccountHistorySA());
  }

  /**
   * 取引所への通信経路を確保する．
   * @param umart 取引所
   * @param userID ユーザID
   */
  public void setConnection(UMart umart, int userID) {
    Collection c = fCommandHash.values();
    Iterator itr = c.iterator();
    while (itr.hasNext()) {
      IClientCmdSA isc = (IClientCmdSA) itr.next();
      isc.setConnection(umart, userID);
    }
  }

}
