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
package serverCore;

import java.util.*;

/**
 * 板寄せを行うクラスです．使用手順は以下のとおりです：
 * <ul>
 * <li>板寄せの対象としたい全ての注文をUBoard.appendOrderメソッドにより
 *     登録する。
 * <li>UBoard.makeContractsメソッドを呼び出して板寄せを行う。
 * <li>UBoard.getOrderArrayメソッドを呼び出して注文(UOrder)を取りだす。
 * </ul>
 */
public class UBoard {

  /** 注文(UOrder)を格納するためのツリーセット. 2分木を利用しています. */
  private TreeSet fOrderTreeSet;

  /** 約定した売り注文のうち最も高い注文価格。価格決定に用いられます． */
  private long fMaxSellPrice;

  /** 約定しなかった売り注文のうち最も低い注文価格。価格決定に用いられます． */
  private long fMinNotSellPrice;

  /** 約定した買い注文のうち最も低い注文価格。価格決定に用いられます．*/
  private long fMinBuyPrice;

  /** 約定しなかった買い注文のうち最も高い注文価格。価格決定に用いられます． */
  private long fMaxNotBuyPrice;

  /** 約定数量 */
  private long fContractVolume;

  /** 約定価格 */
  private long fContractPrice;

  /** 板の最終更新時刻 */
  private Date fLastUpdateTime;

  /** 約定ID生成のためのカウンタ */
  private static long fContractID = 1;

  /**
   * 次の約定IDを返します．
   * @return 約定ID
   */
  private static long getNextContractID() {
    long result = fContractID;
    ++fContractID;
    return result;
  }

  /**
   * 板寄せクラスを生成, 初期化します．
   */
  public UBoard() {
    fOrderTreeSet = new TreeSet(new UOrderComparator());
    fMaxSellPrice = 0;
    fMinNotSellPrice = 0;
    fMinBuyPrice = 0;
    fMaxNotBuyPrice = 0;
    fContractVolume = 0;
    fLastUpdateTime = new Date();
  }

  /**
   * 注文(UOrder)を格納した反復子を返します．
   * @return 注文(UOrder)を格納した反復子
   */
  public final Iterator getOrderArray() {
    return fOrderTreeSet.iterator();
  }

  /**
   * 板情報をinfoに設定して返します．
   * @param info 板情報
   */
  public final void setBoardInfo(UBoardInformation info) {
    synchronized (fOrderTreeSet) {
      info.setLastUpdateTime(this.getLastUpdateTime());
      Iterator orders = fOrderTreeSet.iterator();
      while (orders.hasNext()) {
        UOrder o = (UOrder) orders.next();
        info.addInformation(o.getSellBuy(), o.getMarketLimit(),
                            o.getPrice(), o.getVolume());
      }
    }
  }

  /**
   * 板の最終更新時刻を返します．
   * @return 板の最終更新時刻
   */
  public Date getLastUpdateTime() {
    return fLastUpdateTime;
  }

  /**
   * 約定数量を返します．
   * @return 約定数量
   */
  public long getContractVolume() {
    return fContractVolume;
  }

  /**
   * 約定価格を返します．
   * @return 約定価格
   */
  public long getContractPrice() {
    return fContractPrice;
  }

  /**
   * 売り気配値(売り注文価格の最安値)を返します．もし約定があれば0を返します．
   * また, 全ての売り注文が成行きならば-1を返します．
   * @return 売り注文価格の最安値。ただし, もし約定があれば0。
   *         全ての売り注文が成行きならば-1。
   */
  public long getAskedQuotation() {
    if (fContractVolume > 0) {
      return 0;
    }
    Iterator itr = fOrderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder o = (UOrder) itr.next();
      if (o.getSellBuy() == UOrder.SELL && o.getMarketLimit() == UOrder.LIMIT) {
        return o.getPrice();
      }
    }
    return -1;
  }

  /**
   * 買い気配値(買い注文価格の最高値)を返します．もし約定があれば0を返します．
   * また, 全ての買い注文が成行きならば-1を返します．
   * @return 買い注文価格の最安値。ただし, もし約定があれば0。
   *         全ての買い注文が成行きならば-1。
   */
  public long getBidQuotation() {
    if (fContractVolume > 0) {
      return 0;
    }
    Iterator itr = fOrderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder o = (UOrder) itr.next();
      if (o.getSellBuy() == UOrder.BUY && o.getMarketLimit() == UOrder.LIMIT) {
        return o.getPrice();
      }
    }
    return -1;
  }

  /**
   * 注文(UOrderオブジェクト)を登録します．
   * 新たな注文はUOrderComparatorクラスに従って2分木に挿入される.<br>
   * @param order 注文
   */
  public final void appendOrder(UOrder order) {
    synchronized (fOrderTreeSet) {
      if (!fOrderTreeSet.add(order)) {
        System.err.println("Error in UBoard.appendOrder.");
        System.exit(1);
      }
      fLastUpdateTime = new Date();
    }
  }

  /**
   * 指定された注文を削除します．
   * @param userID ユーザーID
   * @param orderID 注文ID
   * @return true:成功，false:失敗
   */
  public boolean removeOrder(int userID, long orderID) {
    synchronized (fOrderTreeSet) {
      Iterator itr = fOrderTreeSet.iterator();
      while (itr.hasNext()) {
        UOrder o = (UOrder) itr.next();
        if (o.getUserID() == userID && o.getOrderID() == orderID) {
          itr.remove();
          fLastUpdateTime = new Date();
          return true;
        }
      }
      return false;
    }
  }

  /**
   * 登録されている全ての注文を削除します．
   */
  public final void clear() {
    fOrderTreeSet.clear();
    fLastUpdateTime = new Date();
  }

  /**
   * 板寄せを行います．
   * @param date 日
   * @param session 節
   * @return true: 約定あり, false: 約定なし
   */
  public boolean makeContracts(int date, int session) {
    fContractPrice = UPriceInfo.INVALID_PRICE;
    if (fOrderTreeSet.size() < 2) { // 注文数が2より小さい
      return false;
    }
    long maxPrice = searchMaxPrice(fOrderTreeSet);
    if (maxPrice == 0) { // 全て成行注文
      return false;
    }
    executeContract(maxPrice, date, session);
    if (fContractVolume == 0) {
      return false;
    }
    fContractPrice = determineContractPrice(maxPrice);
    setContractPrice(fContractPrice);
    return true;
  }

  /**
   * 板寄せの結果から約定価格を決定します．
   * @param maxPrice 登録されている注文における最高注文価格
   * @return 約定価格
   */
  private long determineContractPrice(long maxPrice) {
    if (Math.max(fMaxSellPrice, fMaxNotBuyPrice) == 0) {
      return Math.min(fMinBuyPrice, fMinNotSellPrice);
    } else if (Math.min(fMinBuyPrice, fMinNotSellPrice) == maxPrice + 1) {
      return Math.max(fMaxSellPrice, fMaxNotBuyPrice);
    } else {
      return (Math.max(fMaxSellPrice, fMaxNotBuyPrice)
              + Math.min(fMinBuyPrice, fMinNotSellPrice) + 1) / 2;
    }
  }

  /**
   * 約定操作を行います．
   * @param maxPrice 登録されている注文における最高注文価格
   * @param date 日
   * @param session 節
   */
  private void executeContract(long maxPrice, int date, int session) {
    long prevBuyVolume = calcTotalBuy(fOrderTreeSet);
    long prevSellVolume = 0;
    long currentBuyVolume = prevBuyVolume;
    long currentSellVolume = 0;
    Date time = new Date();
    fMaxSellPrice = 0;
    fMinNotSellPrice = maxPrice + 1;
    fMinBuyPrice = maxPrice + 1;
    fMaxNotBuyPrice = 0;
    fContractVolume = 0;
    ArrayList marketBuyOrders = new ArrayList();
    Iterator itr = fOrderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder o = (UOrder) itr.next();
      if (o.getMarketLimit() == UOrder.MARKET && o.getSellBuy() == UOrder.BUY) {
        o.setPrice(maxPrice + 1);
        marketBuyOrders.add(o);
      }
      long volume = o.getUncontractOrderVolume();
      if (o.getSellBuy() == UOrder.BUY) {
        currentBuyVolume -= volume;
      } else if (o.getSellBuy() == UOrder.SELL) {
        currentSellVolume += volume;
      } else {
        System.out.println("Error in UBoard.executeContract");
        System.out.println("o.getSellBuy=" + o.getSellBuy());
        System.exit(5);
      }
      if (prevBuyVolume > prevSellVolume &&
          currentBuyVolume >= currentSellVolume) {
        doCase1(currentBuyVolume, currentSellVolume, o, time, date, session);
      } else if (prevBuyVolume > prevSellVolume &&
                 currentBuyVolume < currentSellVolume) {
        doCase2(prevBuyVolume, currentBuyVolume, prevSellVolume,
                currentSellVolume, o, time, date, session);
      } else if (prevBuyVolume <= prevSellVolume &&
                 currentBuyVolume < currentSellVolume) {
        doCase3(currentBuyVolume, currentSellVolume, o, time, date, session);
      } else {
        System.out.println("Error in UBoard.executeContract");
        System.out.println("prevBuyVolume=" + prevBuyVolume);
        System.out.println("prevSellVolume=" + prevSellVolume);
        System.out.println("currentBuyVolume=" + currentBuyVolume);
        System.out.println("currentSellVolume=" + currentSellVolume);
        System.exit(5);
      }
      prevBuyVolume = currentBuyVolume;
      prevSellVolume = currentSellVolume;
    }
    Iterator itr2 = marketBuyOrders.iterator();
    while (itr2.hasNext()) {
      UOrder o = (UOrder) itr2.next();
      if (o.getMarketLimit() != UOrder.MARKET) {
        System.err.println(
            "o.getMarketLimit() != o.MARKET in UBoard.executeContract");
        System.exit(5);
      }
      o.setPrice(0);
    }
  }

  /**
   * 需要曲線(買)が供給曲線(売)より高い場合の注文について約定操作を行います．
   * @param buyVolume 現在の買い注文数量
   * @param sellVolume 現在の売り注文数量
   * @param o 対象となる注文
   * @param time 実時間
   * @param date 日
   * @param session 節
   */
  private void doCase1(long buyVolume, long sellVolume, UOrder o,
                       Date time, int date, int session) {
    if (o.getSellBuy() == UOrder.BUY) { // 買い注文ならば不約定
      if (fMaxNotBuyPrice < o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMaxNotBuyPrice = o.getPrice();
      }
    } else { // 売り注文ならば約定
      long contractID = getNextContractID();
      o.addContractInformation(contractID, time, o.getUncontractOrderVolume(),
                               date, session);
      if (fMaxSellPrice < o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMaxSellPrice = o.getPrice();
      }
      if (fContractVolume < sellVolume) {
        fContractVolume = sellVolume;
      }
    }
  }

  /**
   * 需要曲線(買)と供給曲線(売)が交差する場合の注文について
   * 約定操作を行います．
   * @param prevBuyVolume 一ステップ前の買い注文数量
   * @param currentBuyVolume 現在の買い注文数量
   * @param prevSellVolume 一ステップ前の売り注文数量
   * @param currentSellVolume 現在の売り注文数量
   * @param o 対象となる注文
   * @param time 実時間
   * @param date 日
   * @param session 節
   */
  private void doCase2(long prevBuyVolume, long currentBuyVolume,
                       long prevSellVolume, long currentSellVolume,
                       UOrder o, Date time, int date, int session) {
    if (o.getSellBuy() == UOrder.BUY) { // 需要曲線(買)が供給曲線(売)を
      // 上から下へ突き抜けた場合
      long volume = currentSellVolume - currentBuyVolume;
      long contractID = getNextContractID();
      o.addContractInformation(contractID, time, volume, date, session);
      if (fMinBuyPrice > o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMinBuyPrice = o.getPrice();
      }
      if (fContractVolume < currentSellVolume) {
        fContractVolume = currentSellVolume;
      }
    } else { // 供給曲線(売)が需要曲線(買)を下から上に突き抜けた場合
      long volume = currentBuyVolume - prevSellVolume;
      long contractID = getNextContractID();
      o.addContractInformation(contractID, time, volume, date, session);
      if (fMaxSellPrice < o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMaxSellPrice = o.getPrice();
      }
      if (fContractVolume < currentBuyVolume) {
        fContractVolume = currentBuyVolume;
      }
    }
  }

  /**
   * 需要曲線(買)が供給曲線(売)より低い場合の注文について
   * 約定操作を行います．
   * @param buyVolume 現在の買い注文数量
   * @param sellVolume 現在の売り注文数量
   * @param o 対象となる注文
   * @param time 実時間
   * @param date 日
   * @param session 節
   */
  private void doCase3(long buyVolume, long sellVolume,
                       UOrder o, Date time, int date, int session) {
    if (o.getSellBuy() == UOrder.BUY) { // 買い注文
      long contractID = getNextContractID();
      long volume = o.getUncontractOrderVolume();
      o.addContractInformation(contractID, time, volume, date, session);
      if (fMinBuyPrice > o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMinBuyPrice = o.getPrice();
      }
    } else { // 売り注文
      if (fMinNotSellPrice > o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMinNotSellPrice = o.getPrice();
      }
    }
  }

  /**
   * 登録されている注文に約定価格を設定します．
   * @param contractPrice 約定価格
   */
  private void setContractPrice(long contractPrice) {
    Iterator itr = fOrderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder o = (UOrder) itr.next();
      o.setLatestContractPrice(contractPrice);
    }
  }

  /**
   * 引数で与えられたベクタに登録されている注文の中で
   * 最も高い注文価格を返します．
   * @param orderArray 注文の登録されたベクタ
   * @return 最高注文価格
   */
  private static long searchMaxPrice(TreeSet orderTreeSet) {
    long maxprice = 0;
    Iterator itr = orderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder u = (UOrder) itr.next();
      if (u.getPrice() > maxprice) {
        maxprice = u.getPrice();
      }
    }
    return maxprice;
  }

  /**
   * 引数で与えられたベクタに登録されている買い注文の数量の合計を返します．
   * @param orderArray 注文の登録されたベクタ
   * @return 買い注文の数量の合計
   */
  private static long calcTotalBuy(TreeSet orderTreeSet) {
    long totalBuy = 0;
    Iterator itr = orderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder u = (UOrder) itr.next();
      if (u.getSellBuy() == UOrder.BUY) {
        totalBuy += u.getUncontractOrderVolume();

      }
    }
    return totalBuy;
  }

  /**
   * テスト用メイン関数です．
   */
  public static void main(String args[]) {
    UBoard board = new UBoard();
    UOrderManager orderManager = new UOrderManager();
    UOrder o1 =
        orderManager.createOrder(1, "User1", "j30", UOrder.SELL, UOrder.LIMIT,
                                 UOrder.NEW, 3000, 10, 1, 1);
    board.appendOrder(o1);
    UOrder o2 =
        orderManager.createOrder(2, "User2,", "j30", UOrder.BUY, UOrder.MARKET,
                                 UOrder.NEW, 3100, 10, 1, 1);
    board.appendOrder(o2);
    UOrder o3 =
        orderManager.createOrder(3, "User3", "j30", UOrder.BUY, UOrder.LIMIT,
                                 UOrder.NEW, 2800, 10, 1, 1);
    board.appendOrder(o3);
    UOrder o4 =
        orderManager.createOrder(4, "User4", "j30", UOrder.SELL, UOrder.MARKET,
                                 UOrder.NEW, 2700, 10, 1, 1);
    board.appendOrder(o4);
    UOrder o5 =
        orderManager.createOrder(5, "User5", "j30", UOrder.SELL, UOrder.LIMIT,
                                 UOrder.NEW, 3300, 10, 1, 1);
    board.appendOrder(o5);
    UOrder o6 =
        orderManager.createOrder(6, "User6", "j30", UOrder.BUY, UOrder.MARKET,
                                 UOrder.NEW, 3200, 10, 1, 1);
    board.appendOrder(o6);
    Iterator itr1 = board.getOrderArray();
    while (itr1.hasNext()) {
      UOrder o = (UOrder) itr1.next();
      System.out.println(o.getUserID());
    }
    System.out.println("*******************");
    if (!board.removeOrder(o1.getUserID(), o1.getOrderID())) {
      System.err.println("Can't remove o1");
      System.exit(5);
    }
    Iterator itr2 = board.getOrderArray();
    while (itr2.hasNext()) {
      UOrder o = (UOrder) itr2.next();
      System.out.println(o.getUserID());
    }
  }

}
