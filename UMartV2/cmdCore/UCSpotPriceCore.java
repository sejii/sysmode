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
package cmdCore;

import java.util.*;

/**
 * 現物価格を取得するためのSVMPコマンドの抽象クラスです．
 */
public abstract class UCSpotPriceCore implements ICommand {

  /** 銘柄名を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** 日付を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_DAY = "INT_DAY";

  /** 板寄せ回数を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_BOARD_NO = "INT_BOARD_NO";

  /** ステップ数を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_STEP = "INT_STEP";

  /** 現物価格を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** コマンド名 */
  public static final String CMD_NAME = "SpotPrice";

  /** 銘柄名 */
  protected String fBrandName;

  /** 必要な情報のステップ数 */
  protected int fNoOfSteps;

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのArrayList */
  protected ArrayList fSpotArray;

  /**
   * コンストラクタです．
   */
  public UCSpotPriceCore() {
    super();
    fBrandName = "";
    fNoOfSteps = -1;
    fSpotArray = new ArrayList();
    fStatus = new UCommandStatus();
  }

  /**
   * @see cmdCore.ICommand#isNameEqualTo(String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @see cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /**
   * @see cmdCore.ICommand#setArguments(StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    try {
      fBrandName = st.nextToken();
      fNoOfSteps = Integer.parseInt(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * コマンド実行に必要な引数を設定します．
   * @param brandName 銘柄名
   * @param noOfSteps ステップ数
   */
  public void setArguments(String brandName, int noOfSteps) {
    fBrandName = brandName;
    fNoOfSteps = noOfSteps;
  }

  /**
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    Iterator itr = fSpotArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_BRAND_NAME)
          + " "
          + os.get(INT_DAY)
          + " "
          + os.get(INT_BOARD_NO)
          + " "
          + os.get(INT_STEP)
          + " "
          + os.get(LONG_PRICE)
          + " ";
    }
    return result;
  }

  /**
   * 現物価格を返します．
   * @return 現物価格．先頭の要素は，直近の価格情報．
   */
  public ArrayList getResults() {
    return fSpotArray;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<SpotPrice " + fBrandName + " " + fNoOfSteps + ">>");
    Iterator itr = fSpotArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println(os.toString());
    }
  }
}
