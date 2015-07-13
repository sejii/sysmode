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
 * 全てのユーザーの注文とその履歴を管理するクラスです．
 * 注文は，ユーザーごとにUOrderArrayを用いて管理されています．
 * 一方，注文履歴は，ArrayListを用いて一括管理されています．
 */
public class UOrderManager {

  /** 注文履歴 */
  private ArrayList fOrderHistory;

  /** 各ユーザーの注文を管理するUOrderArrayオブジェクトのベクタ */
  private Vector fOrderArrays;

  /** 注文IDを発生するためのカウンタ */
  static private long fOrderID = 1;

  /** 同一板寄せ期間内の注文をソートするための乱数発生器 */
  private Random fRandom;

  /**
   * UOrderManagerを初期化します．
   */
  public UOrderManager() {
    fOrderArrays = new Vector();
    fOrderHistory = new ArrayList();
    fRandom = URandom.getInstance();
  }

  /**
   * 注文IDを返します．全注文を通して注文IDが重ならないように，
   * この注文IDを利用するとよいです．
   * @return 注文ID
   */
  public static long getOrderID() {
    long result = fOrderID;
    ++fOrderID;
    return result;
  }

  /**
   * 正の整数の乱数を返します．
   * @return 正の整数の乱数
   */
  public final int getRandomInteger() {
    return Math.abs(fRandom.nextInt());
  }

  /**
   * userIDをもつメンバーの全ての注文を取り消します．
   * @param userID 注文を取り消したいメンバーのユーザーID
   */
  public void cancelAllOrdersOfMember(int userID) {
    UOrderArray orderArray = getOrderArray(userID);
    if (orderArray == null) {
      System.out.print("Member" + userID + " cannot be found ");
      System.out.println("in UOrderManager.cancelAllOrdersOfMember");
      return;
    }
    Enumeration orders = orderArray.getUncontractedOrders().elements();
    while (orders.hasMoreElements()) {
      UOrder o = (UOrder) orders.nextElement();
      o.cancel();
      if (o.getContractVolume() > 0) {
        orderArray.getContractedOrders().addElement(o);
      }
    }
    orderArray.getUncontractedOrders().removeAllElements();
  }

  /**
   * userIDをもつメンバーの全ての注文をVectorに入れて返します．
   * @param userID ユーザーID
   * @return 指定されたメンバーの全ての注文を含むVector
   */
  public Vector getAllOrders(int userID) {
    Vector result = new Vector();
    UOrderArray orderArray = getOrderArray(userID);
    if (orderArray == null) {
      System.out.print("Member" + userID + " cannot be found ");
      System.out.println("in UOrderManager.getExecutions");
      return result;
    }
    Enumeration contOrders = orderArray.getContractedOrders().elements();
    while (contOrders.hasMoreElements()) {
      UOrder o = (UOrder) contOrders.nextElement();
      result.addElement(o);
    }
    Enumeration uncontOrders = orderArray.getUncontractedOrders().elements();
    while (uncontOrders.hasMoreElements()) {
      UOrder o = (UOrder) uncontOrders.nextElement();
      result.addElement(o);
    }
    return result;
  }

  /**
   * userIDをもつメンバーのorderIDで指定される注文を取り消します．
   * @param userID ユーザーID
   * @param orderID 注文ID
   * @return 取り消された注文．指定した注文が存在しない場合，nullを返します．
   */
  public UOrder cancelOrder(int userID, long orderID) {
    // 指定されたorderIDの注文が見つからなければnullを返す
    UOrderArray orderArray = getOrderArray(userID);
    if (orderArray == null) {
      System.out.print("Member" + userID + " cannot be found ");
      System.out.println("in UOrderManager.cancelOrder");
      return null;
    }
    Vector uncontractedOrders = orderArray.getUncontractedOrders();
    int size = uncontractedOrders.size();
    int targetIndex = -1;
    for (int i = 0; i < size; ++i) {
      UOrder o = (UOrder) uncontractedOrders.elementAt(i);
      if (o.getOrderID() == orderID) {
        targetIndex = i;
        break;
      }
    }
    if (targetIndex < 0) {
      return null;
    }
    UOrder order = (UOrder) uncontractedOrders.remove(targetIndex);
    order.cancel();
    if (order.getContractVolume() > 0) {
      orderArray.getContractedOrders().addElement(order);
    }
    return order;
  }

  /**
   * すべてのメンバーの全ての注文を取り除きます．
   */
  public void removeAllOrders() {
    Enumeration e = fOrderArrays.elements();
    while (e.hasMoreElements()) {
      UOrderArray array = (UOrderArray) e.nextElement();
      array.removeAllContractedOrders();
      array.removeAllUncontractedOrders();
    }
  }

  /**
   * 全注文の履歴から該当注文を返します．
   * @param orderID 注文ID
   * @return 注文
   */
  public UOrder getOrderFromHistory(long orderID) {
    int index = (int) orderID - 1;
    if (index < 0 || index >= fOrderHistory.size()) {
      return null;
    }
    return (UOrder) fOrderHistory.get(index);
  }

  /**
   * 注文を履歴へ登録します．
   * @param o 注文
   */
  public void registerOrderToHistory(UOrder o) {
    fOrderHistory.add(o);
    UOrderArray orderArray = getOrderArray(o.getUserID());
    orderArray.getHistory().add(o);
  }

  /**
   * userIDの注文管理クラスUOrderArrayを生成します．
   * @param userID ユーザーID
   * @return 成功：true, 失敗: false (同一メンバーが存在する場合)
   */
  public boolean createOrderArray(int userID) {
    Enumeration e = fOrderArrays.elements();
    while (e.hasMoreElements()) {
      UOrderArray x = (UOrderArray) e.nextElement();
      if (x.getUserID() == userID) {
        System.err.println("The same UserID exists.");
        return false;
      }
    }
    fOrderArrays.addElement(new UOrderArray(userID));
    return true;
  }

  /**
   * 注文を追加します．
   * @param o 追加注文
   * @return 成功：true, 失敗: false (対応するメンバーが見つからないとき)
   */
  public boolean addOrder(UOrder o) {
    Enumeration e = fOrderArrays.elements();
    while (e.hasMoreElements()) {
      UOrderArray x = (UOrderArray) e.nextElement();
      if (x.getUserID() == o.getUserID()) {
        x.addOrder(o);
        return true;
      }
    }
    return false;
  }

  /**
   * 引数で指定された注文を新たに作成します．
   * 成行注文の場合, 注文価格は0に設定されます．
   * 通常, このUOrder.createOrderメソッドで新規注文を作成してから,
   * UOrder.addOrderメソッドを用いて注文を追加します．
   * @param userID ユーザーID
   * @param userName ユーザ名
   * @param brandName 銘柄名
   * @param sellBuy 売買区分 (売: UOrder.SELL, 買: UOrder.BUY)
   * @param marketLimit 成行指値区分 (成行: UOrder.MARKET, 指値: UOrder.LIMIT)
   * @param newRepay 新規返済区分 (新規: UOrder.NEW, 返済: UOrder.LIMIT )
   * @param price 注文価格
   * @param volume 注文数量
   * @param date 注文日
   * @param session 注文節
   * @return 注文
   */
  public UOrder createOrder(int userID, String userName, String brandName,
                            int sellBuy, int marketLimit, int newRepay, 
                            long price, long volume, int date, int session) {
    UOrder o = new UOrder();
    o.setUserID(userID);
    o.setUserName(userName);
    o.setBrandName(brandName);
    o.setOrderID(UOrderManager.getOrderID());
    o.setTime(new Date());
    o.setDate(date);
    o.setSession(session);
    o.setSellBuy(sellBuy);
    o.setMarketLimit(marketLimit);
    o.setNewRepay(newRepay);
    if (marketLimit == UOrder.MARKET) {
      o.setPrice(0);
    } else {
      o.setPrice(price);
    }
    o.setVolume(volume);
    o.setRandomInteger(this.getRandomInteger());
    return o;
  }

  /**
   * 引数で指定されたメンバーの注文集合を返します．
   * @param userID ユーザーID
   * @return 注文集合を管理するUOrderArrayオブジェクト．
   *          対応するメンバーのUOrderArrayオブジェクトが
   *          存在しない場合, nullを返すので注意すること．
   */
  public UOrderArray getOrderArray(int userID) {
    Enumeration e = fOrderArrays.elements();
    while (e.hasMoreElements()) {
      UOrderArray x = (UOrderArray) e.nextElement();
      if (x.getUserID() == userID) {
        return x;
      }
    }
    return null;
  }

  /**
   * 何人分のメンバーのUOrderArrayを管理しているか返します．
   * @return 管理しているUOrderArrayの数
   */
  public int getNoOfOrderArrays() {
    return fOrderArrays.size();
  }

  /**
   * 管理しているUOrderArrayを列挙するためのEnumerationを返します．
   * @return 管理しているUOrderArrayを列挙するためのEnumeration
   */
  public Enumeration getOrderArrays() {
    return fOrderArrays.elements();
  }

}
