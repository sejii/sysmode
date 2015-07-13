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
 * ユーザー一人分の注文集合を管理するクラスです．
 * 注文は, 完全に約定した注文と, 未約定分が残っている注文に分けて管理されます．
 */
public class UOrderArray {

  /** ユーザーID */
  private int fUserID;

  /** 約定済みの注文集合 */
  private Vector fContractedOrders;

  /** 未約定の注文集合 */
  private Vector fUncontractedOrders;

  /** 注文履歴 */
  private ArrayList fHistory;

  /**
   * userIDをもつユーザーのUOrderArrayを作成します．
   * @param userID ユーザーID
   */
  public UOrderArray(int userID) {
    fUserID = userID;
    fContractedOrders = new Vector();
    fUncontractedOrders = new Vector();
    fHistory = new ArrayList();
  }

  /**
   * 注文履歴を返します．
   * @return 注文履歴のリスト
   */
  public ArrayList getHistory() {
    return fHistory;
  }

  /**
   * userIDを返します．
   * @return ユーザーID
   */
  public int getUserID() {
    return fUserID;
  }

  /**
   * 約定分の注文集合を返します．
   * @return 約定分の注文集合
   */
  public Vector getContractedOrders() {
    return fContractedOrders;
  }

  /**
   * 未約定分の注文集合を返します．
   * @return 未約定分の注文集合
   */
  public Vector getUncontractedOrders() {
    return fUncontractedOrders;
  }

  /**
   * 注文を追加する．このメソッドは, 追加された注文を
   * 未約定分を含むかチェックし, 適切に分類します．
   * @param o 注文
   */
  public void addOrder(UOrder o) {
    if (o.getUncontractOrderVolume() > 0) {
      fUncontractedOrders.addElement(o);
    } else if (o.getUncontractOrderVolume() == 0) {
      fContractedOrders.addElement(o);
    } else {
      System.out.println("Order volume is under 0");
      System.exit(1);
    }
  }

  /**
   * 約定分の注文を全て取り除きます．
   */
  public void removeAllContractedOrders() {
    fContractedOrders.removeAllElements();
  }

  /**
   * 未約定分の注文を全て取り除きます．
   */
  public void removeAllUncontractedOrders() {
    fUncontractedOrders.removeAllElements();
  }

  /**
   * 約定分の注文数を返します．
   * @return 約定分の注文数
   */
  public int getNoOfContractedOrders() {
    return fContractedOrders.size();
  }

  /**
   * 未約定分の注文数を返します．
   * @return 未約定分の注文数
   */
  public int getNoOfUncontractedOrders() {
    return fUncontractedOrders.size();
  }

  /**
   * indexで指定される約定分の注文を返します．
   * @param index 添え字
   * @return 注文
   */
  public UOrder getContractedOrderAt(int index) {
    return (UOrder) fContractedOrders.elementAt(index);
  }

  /**
   * indexで指定される未約定分の注文を返します．
   * @param index 添え字
   * @return 注文
   */
  public UOrder getUncontractedOrderAt(int index) {
    return (UOrder) fUncontractedOrders.elementAt(index);
  }

  /**
   * テスト用メインメソッドです．
   */
  public static void main(String args[]) {
    UOrderArray uo = new UOrderArray(1);
    for (int i = 0; i < 10; ++i) {
      UOrder o = new UOrder();
      o.setUserID(i + 1);
      o.setBrandName("j300119991200000");
      o.setOrderID(i + 1);
      o.setDate(i + 1);
      o.setSession(i % 3 + 1);
      o.setSellBuy(UOrder.SELL);
      o.setMarketLimit(UOrder.MARKET);
      o.setNewRepay(UOrder.REPAY);
      o.setPrice(2500 + i * 10);
      o.setVolume(i + 20);
      o.setRandomInteger(1234);
      o.setContractVolume(i + 2);
      o.setCancelVolume(i * 2);
      uo.addOrder(o);
    }
    System.out.println("UserID : " + uo.getUserID());
    System.out.println("Contracted Order Number is " + uo.getNoOfContractedOrders());
    System.out.println("Uncontracted Order Number is " + uo.getNoOfUncontractedOrders());
    uo.getContractedOrderAt(0).printOn(System.out);
  }
}
