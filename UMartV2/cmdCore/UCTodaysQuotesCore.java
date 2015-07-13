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
 * 当日価格照会のためのSVMPコマンドの抽象クラスです．
 */
public abstract class UCTodaysQuotesCore implements ICommand {

  /** 銘柄名を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** 日付を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_DATE = "INT_DATE";

  /** 現在の板寄せ回数を引くためのキー(値はIntegerオブジェクト) */
  public static final String INT_BOARD_NO = "INT_BOARD_NO";

  /** 時刻を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_TIME = "STRING_TIME";

  /** 約定価格を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** 約定数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_VOLUME = "INT_VOLUME";

  /** 売り気配を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_ASKED_QUOTATION = "LONG_ASKED_QUOTATION";

  /** 買い気配を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_BID_QUOTATION = "LONG_BID_QUOTATION";

  /** コマンド名 */
  public static final String CMD_NAME = "TodaysQuotes";

  /** 別名 */
  public static final String CMD_ALIAS = "301";

  /** 銘柄名 */
  protected String fBrandName;

  /** 必要な情報の板寄せ数 */
  protected int fNoOfBoards;

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのArrayList */
  protected ArrayList fQuotesArray;

  /**
   * コンストラクタです．
   */
  public UCTodaysQuotesCore() {
    super();
    fBrandName = "";
    fNoOfBoards = -1;
    fQuotesArray = new ArrayList();
    fStatus = new UCommandStatus();
  }

  /**
   * @see cmdCore.ICommand#isNameEqualTo(String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME) || name.equals(CMD_ALIAS)) {
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
      fNoOfBoards = Integer.parseInt(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * コマンド実行に必要な引数を設定します．
   * @param brandName 銘柄名
   * @param noOfBoards 節数
   */
  public void setArguments(String brandName, int noOfBoards) {
    fBrandName = brandName;
    fNoOfBoards = noOfBoards;
  }

  /**
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    Iterator itr = fQuotesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_BRAND_NAME)
          + " "
          + os.get(INT_DATE)
          + " "
          + os.get(INT_BOARD_NO)
          + " "
          + os.get(STRING_TIME)
          + " "
          + os.get(LONG_PRICE)
          + " "
          + os.get(LONG_VOLUME)
          + " "
          + os.get(LONG_ASKED_QUOTATION)
          + " "
          + os.get(LONG_BID_QUOTATION)
          + " ";
    }
    return result;
  }

  /**
   * 当日価格照会を返します．
   * @return 当日価格照会
   */
  public ArrayList getResults() {
    return fQuotesArray;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<TodaysQuotes " + fBrandName + " " + fNoOfBoards +
                       ">>");
    Iterator itr = fQuotesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println(os.toString());
    }
  }
}
