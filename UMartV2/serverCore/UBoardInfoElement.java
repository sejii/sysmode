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

/**
 * ある価格における売り注文数と買い注文数を保持するクラスです．
 * UBoardInformationクラスが利用します．
 */
public class UBoardInfoElement {

  /** 価格。ただし, 成行注文のときは-1が設定されます． */
  private long fPrice;

  /** 売り注文数 */
  private long fSellVolume;

  /** 買い注文数 */
  private long fBuyVolume;

  /**
   * UBoardInfoElementの生成および初期化(成行注文,
   * 売り/買い注文数:0)を行います．
   */
  public UBoardInfoElement() {
    fPrice = -1;
    fSellVolume = 0;
    fBuyVolume = 0;
  }

  /**
   * UBoardInfoElementの生成および初期化を行います．
   * @param price 価格
   * @param sellVolume 売り注文数
   * @param buyVolume 買い注文数
   */
  public UBoardInfoElement(long price, long sellVolume, long buyVolume) {
    fPrice = price;
    fSellVolume = sellVolume;
    fBuyVolume = buyVolume;
  }

  /**
   * UBoardInfoElementオブジェクトの複製を返します．
   * @return UBoardInfoElementオブジェクト
   */
  public Object clone() {
    return new UBoardInfoElement(fPrice, fSellVolume, fBuyVolume);
  }

  /**
   * 価格を返します．
   * @return 価格
   */
  public long getPrice() {
    return fPrice;
  }

  /**
   * 売り注文数を返します．
   * @return 売り注文数
   */
  public long getSellVolume() {
    return fSellVolume;
  }

  /**
   * 買い注文数を返します．
   * @return 買い注文数
   */
  public long getBuyVolume() {
    return fBuyVolume;
  }

  /**
   * 価格を設定します．
   * @param price 価格
   */
  public void setPrice(long price) {
    fPrice = price;
  }

  /**
   * 売り注文数を設定します．
   * @param sellVolume 売り注文数
   */
  public void setSellVolume(long sellVolume) {
    fSellVolume = sellVolume;
  }

  /**
   * 売り注文数にvolumeを加えます．
   * @param volume 売り注文数の増分
   */
  public void addToSellVolume(long volume) {
    fSellVolume += volume;
  }

  /**
   * 買い注文数を設定します．
   * @param buyVolume 買い注文数
   */
  public void setBuyVolume(long buyVolume) {
    fBuyVolume = buyVolume;
  }

  /**
   * 買い注文数にvolumeを加えます．
   * @param volume 買い注文数の増分
   */
  public void addToBuyVolume(long volume) {
    fBuyVolume += volume;
  }

  /**
   * 成行注文(価格:-1), 売り/買い注文数を0に初期化します．
   */
  public void clear() {
    fPrice = -1;
    fBuyVolume = fSellVolume = 0;
  }

  /**
   * 内部情報を出力します．
   */
  public void printOn() {
    System.out.print("Price:" + fPrice);
    System.out.print(", fSellVolume:" + fSellVolume);
    System.out.println(", fBuyVolume:" + fBuyVolume);
  }

}
