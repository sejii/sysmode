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

import java.util.Collection;
import java.util.Iterator;
import cmdCore.UCAccountHistoryCore;
import cmdCore.UCAllBalancesCore;
import cmdCore.UCAllPositionsCore;
import cmdCore.UCBalancesCore;
import cmdCore.UCBoardDataCore;
import cmdCore.UCBoardInformationCore;
import cmdCore.UCExchangeProfileCore;
import cmdCore.UCExecutionsCore;
import cmdCore.UCFuturePriceCore;
import cmdCore.UCHistoricalQuotesCore;
import cmdCore.UCLogoutCore;
import cmdCore.UCMarketStatusCore;
import cmdCore.UCMemberProfileCore;
import cmdCore.UCOrderCancelCore;
import cmdCore.UCOrderCheckCore;
import cmdCore.UCOrderIDHistoryCore;
import cmdCore.UCOrderRequestCore;
import cmdCore.UCOrderStatusCore;
import cmdCore.UCPositionCore;
import cmdCore.UCScheduleCore;
import cmdCore.UCServerDateCore;
import cmdCore.UCServerStatusCore;
import cmdCore.UCServerTimeCore;
import cmdCore.UCSetSpotDateCore;
import cmdCore.UCSpotIntervalCore;
import cmdCore.UCSpotPriceCore;
import cmdCore.UCTodaysQuotesCore;
import cmdCore.UProtocolCore;
import serverNet.UAgentForNetworkClient;
import serverNet.UMartNetwork;

/**
 * UProtocolForNetClientクラスと対応してサーバ上で動作するSVMPコマンドオブジェクトからなるプロトコルクラスです．
 */
public class UProtocolForServer extends UProtocolCore {
	
	/**
	 * コンストラクタです．
	 */
	public UProtocolForServer() {
		super();
    fCommandHash.put(UCLogoutCore.CMD_NAME, new USCLogout());
    fCommandHash.put(UCOrderRequestCore.CMD_NAME, new USCOrderRequest());
    fCommandHash.put(UCSpotPriceCore.CMD_NAME, new USCSpotPrice());
    fCommandHash.put(UCFuturePriceCore.CMD_NAME, new USCFuturePrice());
    fCommandHash.put(UCScheduleCore.CMD_NAME, new USCSchedule());
    fCommandHash.put(UCServerDateCore.CMD_NAME, new USCServerDate());
    fCommandHash.put(UCPositionCore.CMD_NAME, new USCPosition());
    fCommandHash.put(UCBalancesCore.CMD_NAME, new USCBalances());
    fCommandHash.put(UCOrderStatusCore.CMD_NAME, new USCOrderStatus());
    fCommandHash.put(UCHistoricalQuotesCore.CMD_NAME, new USCHistoricalQuotes());
    fCommandHash.put(UCTodaysQuotesCore.CMD_NAME, new USCTodaysQuotes());
    fCommandHash.put(UCOrderCancelCore.CMD_NAME, new USCOrderCancel());
    fCommandHash.put(UCExecutionsCore.CMD_NAME, new USCExecutions());
    fCommandHash.put(UCServerTimeCore.CMD_NAME, new USCServerTime());
    fCommandHash.put(UCBoardInformationCore.CMD_NAME, new USCBoardInformation());
    fCommandHash.put(UCMarketStatusCore.CMD_NAME, new USCMarketStatus());
    fCommandHash.put(UCSpotIntervalCore.CMD_NAME, new USCSpotInterval());
    fCommandHash.put(UCBoardDataCore.CMD_NAME, new USCBoardData());
    fCommandHash.put(UCAllBalancesCore.CMD_NAME, new USCAllBalances());
    fCommandHash.put(UCAllPositionsCore.CMD_NAME, new USCAllPositions());
    fCommandHash.put(UCSetSpotDateCore.CMD_NAME, new USCSetSpotDate());
    fCommandHash.put(UCOrderCheckCore.CMD_NAME, new USCOrderCheck());
    fCommandHash.put(UCOrderIDHistoryCore.CMD_NAME, new USCOrderIDHistory());
    fCommandHash.put(UCServerStatusCore.CMD_NAME, new USCServerStatus());
    fCommandHash.put(UCMemberProfileCore.CMD_NAME, new USCMemberProfile());
    fCommandHash.put(UCExchangeProfileCore.CMD_NAME, new USCExchangeProfile());
    fCommandHash.put(UCAccountHistoryCore.CMD_NAME, new USCAccountHistory());
	}
	
  /**
   * 取引所への通信経路を確保します．
   * @param agent エージェント
   * @param umart 取引所
   */
  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    Collection c = fCommandHash.values();
    Iterator itr = c.iterator();
    while (itr.hasNext()) {
      IServerCmd isc = (IServerCmd)itr.next();
      isc.setConnection(agent, umart);
    }
  }
	
}
