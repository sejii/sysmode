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
package strategy;

import java.util.*;

import cmdCore.*;
import serverCore.*;
import strategyCore.UBaseAgent;

/**
 * 戦略クラスです．
 */
public class Strategy extends UBaseAgent {

	/** 取得する現物価格系列の長さ */
  public static final int NO_OF_SPOT_PRICES = 120;

  /** 取得する先物価格系列の長さ */
  public static final int NO_OF_FUTURE_PRICES = 60;

  /**
   * コンストラクタです．
   */
  public Strategy() {
    super("NotInitialized", "NotInitialized", "NotInitialized", 1);
  }

  /**
   * 現物価格系列を返します．
   * @return 現物価格系列
   */
  private int[] getSpotPrices() {
    UCSpotPriceCore cmd = (UCSpotPriceCore) fUmcp.getCommand("SpotPrice");
    cmd.setArguments("j30", Strategy.NO_OF_SPOT_PRICES);
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() +
                         " in Strategy.getSpotPrices");
      System.exit(5);
    }
    ArrayList spotList = cmd.getResults();
    if (spotList.size() != Strategy.NO_OF_SPOT_PRICES) {
      System.err.println(
          "spotList.size() != this.NO_OF_SPOT_PRICES in Strategy.doActions");
      System.exit(5);
    }
    int[] spotPrices = new int[Strategy.NO_OF_SPOT_PRICES];
    for (int i = 0; i < Strategy.NO_OF_SPOT_PRICES; ++i) {
      HashMap elem = (HashMap) spotList.get(i);
      spotPrices[Strategy.NO_OF_SPOT_PRICES - i -
          1] = (int) ( (Long) elem.get(UCSpotPriceCore.LONG_PRICE)).longValue();
    }
    return spotPrices;
  }

  /**
   * 先物価格系列を返します．
   * @return 先物価格系列
   */
  private int[] getFuturePrices() {
    UCFuturePriceCore cmd =
        (UCFuturePriceCore) fUmcp.getCommand("FuturePrice");
    cmd.setArguments("j30", Strategy.NO_OF_FUTURE_PRICES);
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() +
                         " in Strategy.getFuturePrices");
      System.exit(5);
    }
    ArrayList futureList = cmd.getResults();
    if (futureList.size() != Strategy.NO_OF_FUTURE_PRICES) {
      System.err.println("futureList.size():" + futureList.size() +
                         " != this.NO_OF_FUTURE_PRICES" + NO_OF_FUTURE_PRICES +
                         " in Strategy.getFuturePrices");
      System.exit(5);
    }
    int[] futurePrices = new int[Strategy.NO_OF_FUTURE_PRICES];
    for (int i = 0; i < Strategy.NO_OF_FUTURE_PRICES; ++i) {
      HashMap elem = (HashMap) futureList.get(i);
      futurePrices[Strategy.NO_OF_FUTURE_PRICES - i -
          1] = (int) ( (Long) elem.get(UCFuturePriceCore.LONG_PRICE)).longValue();
    }
    return futurePrices;
  }

  /**
   * ポジションを返します．
   * @return ポジション
   */
  private int getPosition() {
    UCPositionCore cmd = (UCPositionCore) fUmcp.getCommand("Position");
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID +
                         " in Strategy.getPosition");
      System.err.println(status.getErrorMessage() + " in Strategy.getPosition");
      System.exit(5);
    }
    HashMap result = cmd.getResults();
    long todayBuy = ( (Long) result.get(UCPositionCore.LONG_TODAY_BUY)).
        longValue();
    long todaySell = ( (Long) result.get(UCPositionCore.LONG_TODAY_SELL)).
        longValue();
    long yesterdayBuy = ( (Long) result.get(UCPositionCore.LONG_YESTERDAY_BUY)).
        longValue();
    long yesterdaySell = ( (Long) result.get(UCPositionCore.LONG_YESTERDAY_SELL)).
        longValue();
    long buy = todayBuy + yesterdayBuy;
    long sell = todaySell + yesterdaySell;
    return (int) (buy - sell);
  }

  /**
   * 現金残高を返します．
   * @return 現金残高
   */
  private long getMoney() {
    UCBalancesCore cmd = (UCBalancesCore) fUmcp.getCommand("Balances");
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() + " in Strategy.getMoney");
      System.exit(5);
    }
    HashMap bal = cmd.getTodayResults();
    return ( (Long) bal.get(UCBalancesCore.LONG_SURPLUS)).longValue();
  }

  /**
   * 決算日までの節数を返します．
   * @param date　日
   * @param boardNo 節数
   * @param maxDate 取引日数
   * @param noOfBoardsPerDay １日あたりの節数
   * @return
   */
  private int getRestDay(int date, int boardNo,
                         int maxDate, int noOfBoardsPerDay) {
    return (maxDate - date) * noOfBoardsPerDay + noOfBoardsPerDay - boardNo + 1;
  }

  /**
   * 直近の価格を返します．
   * @param prices 価格系列
   * @return 直近の価格
   */
  public int getLatestPrice(int[] prices) {
    for (int i = prices.length - 1; i >= 0; --i) {
      if (prices[i] >= 0) {
        return prices[i];
      }
    }
    return -1;
  }

  /**
   * @see strategyCore.UBaseAgent#doActions(int, int, int, int, int)
   */
  public void doActions(int date, int session, int serverState,
                        int maxDate, int noOfSessionsPerDay) {
    if (serverState != UServerStatus.ACCEPT_ORDERS) {
      return;
    }
    int[] spotPrices = getSpotPrices();
    int[] futurePrices = getFuturePrices();
    int pos = getPosition();
    long money = getMoney();
    int restDay = getRestDay(date, session, maxDate, noOfSessionsPerDay);
    action(spotPrices, futurePrices, pos, money, restDay);
  }

  /**
   * 単一の注文を決定するメソッドです．これを書き換えて取引戦略を実装します．このメソッドは，actionメソッドを書き換えない場合，1節あたり1回コールされます．
   * @param spotPrices 現物価格の系列．配列要素の0番目から119番目までの120期分が格納されている．119番目が最新の現物価格．
   * @param futurePrices 先物価格の系列．配列要素の0番目から59番目までの60期分が格納されている．59番目が最新の価格．市場が開く前は現物価格が変わりに代入されている．また，市場で取引が成立せずに価格が決定しなかった場合は値'-1'が代入されている．
   * @param position エージェントのポジション．正ならば買い越し，負ならば売り越しを表す．
   * @param money エージェントの保有する現金残高．型がlongであることに注意すること．
   * @param restSessions 市場が閉じるまでの残っている節数（板寄せの回数）
   * @return 注文
   */
  public Order getOrder(int[] spotPrices, int[] futurePrices, int position,
                        long money, int restSessions) {
    return null;
  }

  /**
   * 注文をするためのメソッドです．
   * もし１回で１つだけの注文をするのならば，そのままにしておきます．
   * 複数の注文を行いたいのならば，getOrder以外のメソッドを用意し，orderRequestメソッドを複数回呼べばよい．
   * @param spotPrices 現物価格の系列．配列要素の0番目から119番目までの120期分が格納されている．119番目が最新の現物価格．
   * @param futurePrices 先物価格の系列．配列要素の0番目から59番目までの60期分が格納されている．59番目が最新の価格．市場が開く前は現物価格が変わりに代入されている．また，市場で取引が成立せずに価格が決定しなかった場合は値'-1'が代入されている．
   * @param position エージェントのポジション．正ならば買い越し，負ならば売り越しを表す．
   * @param money エージェントの保有する現金残高．型がlongであることに注意すること．
   * @param restSessions 市場が閉じるまでの残っている節数（板寄せの回数）
   */
  public void action(int[] spotPrices, int[] futurePrices, int position, long money, int restSessions) {
    Order o = getOrder(spotPrices, futurePrices, position, money, restSessions);
    if (o == null) {
      System.err.println("Strategy.getOrder returned null in Strategy.action");
      return;
    }
    orderRequest(o);
  }

  /**
   * サーバへ注文を送ります．
   * @param o 注文
   */
  public void orderRequest(Order o) {
    UCOrderRequestCore cmd = (UCOrderRequestCore) fUmcp.getCommand("OrderRequest");
    int sellBuy;
    if (o.buysell == Order.BUY) {
      sellBuy = UOrder.BUY;
    } else if (o.buysell == Order.SELL) {
      sellBuy = UOrder.SELL;
    } else {
      return;
    }
    if (o.price <= 0 || o.quant <= 0) {
      // System.err.println("ERORR!!!");
      // System.err.println("price=" + o.price + ", quant=" + o.quant);
      try {
        throw new Exception();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        // e.printStackTrace();
        return;
      }
    }
    cmd.setArguments("j30", UOrder.NEW, sellBuy, UOrder.LIMIT, o.price, o.quant);
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() + " in Strategy.orderRequest");
    }
  }

}
