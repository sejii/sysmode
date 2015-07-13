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
 * 板寄せ結果を保持するクラスです．
 * このクラスは, ServerManagerやGUIClientなどが板寄せ結果を
 * 可視化するための問い合わせ対応するために用いられます．
 */
public class UBoardInformation {

  /** 板寄せが行われた時刻(実時間) */
  private Date fLastUpdateTime;

  /** 板情報(UBoardInfoElement)を格納するためのテーブル */
  private Hashtable fTable;

  /** 買い注文数の合計 */
  private long fTotalBuyVolume;

  /** 最低の注文価格 */
  private long fMinPrice;

  /** 最高の注文価格 */
  private long fMaxPrice;

  /** 約定価格 */
  private long fContractPrice;

  /** 約定数量 */
  private long fContractVolume;

  /**
   * UBoardInformationオブジェクトの生成および初期化を行います．
   */
  public UBoardInformation() {
    fLastUpdateTime = null;
    fTable = new Hashtable();
    clear();
  }

  /**
   * UBoardInformationオブジェクトの複製を返します．
   * @return UBoardInformationオブジェクトの複製
   */
  public Object clone() {
    UBoardInformation result = new UBoardInformation();
    result.setLastUpdateTime(fLastUpdateTime);
    Enumeration e = fTable.elements();
    while (e.hasMoreElements()) {
      UBoardInfoElement element = (UBoardInfoElement) e.nextElement();
      UBoardInfoElement copy = (UBoardInfoElement) element.clone();
      Long key = new Long(copy.getPrice());
      result.fTable.put(key, copy);
    }
    return result;
  }

  /**
   * 板寄せが完了した時刻(実時間)を返します．
   * @return 板寄せが完了した時刻(実時間)
   */
  public Date getLastUpdateTime() {
    return fLastUpdateTime;
  }

  /**
   * 板寄せが完了した時刻(実時間)を設定します．
   * @param time 板寄せが完了した時刻(実時間)
   */
  public void setLastUpdateTime(Date time) {
    fLastUpdateTime = time;
  }

  /**
   * 約定数量を設定します．
   * @param volume 約定数量
   */
  public void setContractVolume(long volume) {
    fContractVolume = volume;
  }

  /**
   * 約定数量を返します．
   * @return 約定数量
   */
  public long getContractVolume() {
    return fContractVolume;
  }

  /**
   * 約定価格を設定します．
   * @param price 約定価格
   */
  public void setContractPrice(long price) {
    fContractPrice = price;
  }

  /**
   * 約定価格を返します．
   * @return 約定価格
   */
  public long getContractPrice() {
    return fContractPrice;
  }

  /**
   * 初期化を行います．
   */
  public void clear() {
    fTable.clear();
    Long key = new Long( -1);
    UBoardInfoElement element = new UBoardInfoElement( -1, 0, 0); //成行
    fTable.put(key, element);
    fTotalBuyVolume = 0;
    fMinPrice = -1;
    fMaxPrice = -1;
    fContractPrice = -1;
    fContractVolume = 0;
  }

  /**
   * 買い注文数の合計を返します．
   * @return 買い注文数の合計
   */
  public long getTotalBuyVolume() {
    return fTotalBuyVolume;
  }

  /**
   * 最低の注文価格を返します．
   * @return 最低の注文価格
   */
  public long getMinPrice() {
    return fMinPrice;
  }

  /**
   * 最高の注文価格を返します．
   * @return 最高の注文価格
   */
  public long getMaxPrice() {
    return fMaxPrice;
  }

  /**
   * 板情報を登録します．
   * @param sellBuy 売買区分 (売: UOrder.SELL, 買: UOrder.BUY)
   * @param marketLimit 指値成行区分 (指値: UOrder.LIMIT,
   *                                  成行: UOrder.MARKET)
   * @param price 注文価格
   * @param volume 注文数量
   */
  public void addInformation(int sellBuy, int marketLimit, long price,
                             long volume) {
    if (sellBuy == UOrder.BUY) {
      fTotalBuyVolume += volume;
    }
    if (marketLimit == UOrder.LIMIT
        && (fMinPrice < 0 || fMinPrice > price)) {
      fMinPrice = price;
    }
    if (marketLimit == UOrder.LIMIT && fMaxPrice < price) {
      fMaxPrice = price;
    }
    if (marketLimit == UOrder.MARKET) {
      price = -1;
    }
    updateTable(sellBuy, price, volume);
  }

  /**
   * テーブルの更新を行います．
   * @param sellBuy 売買区分 (売: UOrder.SELL, 買: UOrder.BUY)
   * @param price 注文価格 (成行注文の場合, -1)
   * @param volume 注文数量
   */
  private void updateTable(int sellBuy, long price, long volume) {
    Long key = new Long(price);
    UBoardInfoElement x = (UBoardInfoElement)fTable.get(key);
    if (x == null) {
      long sellVolume = 0;
      long buyVolume = 0;
      if (sellBuy == UOrder.SELL) {
        sellVolume = volume;
      } else {
        buyVolume = volume;
      }
      UBoardInfoElement newElement =
          new UBoardInfoElement(price, sellVolume, buyVolume);
      fTable.put(key, newElement);
    } else {
      if (sellBuy == UOrder.SELL) {
        x.addToSellVolume(volume);
      } else {
        x.addToBuyVolume(volume);
      }
    }
  }

  /**
   * 内部情報を出力します．
   */
  public void printOn() {
    System.out.println("Last update time: " + fLastUpdateTime);
    Vector v = getBoardInfoElements();
    Enumeration e = v.elements();
    while (e.hasMoreElements()) {
      UBoardInfoElement element = (UBoardInfoElement) e.nextElement();
      element.printOn();
    }
  }

  /**
   * UBoardInfoElementComparatorは, UBoardInfoElementをソートするために
   * 用いられるクラスです．
   */
  private class UBoardInfoElementComparator implements Comparator {
    /**
       UBoardInfoElementオブジェクトであるo1とo2の価格を比較する。<br>
       @param o1 UBoardInfoElementオブジェクト
       @param o2 UBoardInfoElementオブジェクト
       @return -1: o1よりもo2の方が価格が小さい場合,
                0: o1とo2の価格が同じ場合,
               +1: o1よりもo2の方が価格が大きい場合
     */
    public int compare(Object o1, Object o2) {
      UBoardInfoElement e1 = (UBoardInfoElement) o1;
      UBoardInfoElement e2 = (UBoardInfoElement) o2;
      if (e1.getPrice() < e2.getPrice()) {
        return -1;
      } else if (e1.getPrice() > e2.getPrice()) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  /**
   * テーブルに登録されている全UBoardInfoElementオブジェクトが
   * 格納されたベクタを返します．UBoardInfoElementオブジェクトは,
   * 価格の昇順でソートされています<br>
   * @return UBoardInfoElementの格納されているベクタ
   */
  public Vector getBoardInfoElements() {
    Vector result = new Vector();
    Enumeration e = fTable.elements();
    while (e.hasMoreElements()) {
      result.addElement(e.nextElement());
    }
    Collections.sort(result, new UBoardInfoElementComparator());
    return result;
  }

  /**
   * テスト用メインメソッドです．
   */
  public static void main(String args[]) {
    UBoardInformation bi = new UBoardInformation();
    bi.setLastUpdateTime(new Date());
    bi.printOn();
    bi.addInformation(UOrder.SELL, UOrder.MARKET, -1, 50);
    bi.addInformation(UOrder.BUY, UOrder.MARKET, 3101, 20);
    bi.addInformation(UOrder.BUY, UOrder.LIMIT, 3000, 10);
    bi.addInformation(UOrder.BUY, UOrder.LIMIT, 3100, 50);
    bi.addInformation(UOrder.BUY, UOrder.LIMIT, 3000, 150);
    bi.setLastUpdateTime(new Date());
    bi.printOn();
    UBoardInformation copy = (UBoardInformation) bi.clone();
    bi.clear();
    bi.setLastUpdateTime(new Date());
    bi.printOn();
    System.out.println("****** clone ******");
    copy.printOn();
  }

}
