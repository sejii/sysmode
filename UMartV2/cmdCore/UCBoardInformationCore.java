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
 * 板情報を取得するためのSVMPコマンドの抽象クラスです．
 */
public abstract class UCBoardInformationCore implements ICommand {

  /** 板寄せが行われた時刻(実時間)を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_LAST_UPDATE_TIME =
      "STRING_LAST_UPDATE_TIME";

  /** 板（（価格，買注文数，売注文数）の配列）を引くためのキー（値はArrayListオブジェクト） */
  public static final String ARRAYLIST_BOARD = "ARRAYLIST_BOARD";

  /** 価格を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** 買い注文数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_BUY_VOLUME = "LONG_BUY_VOLUME";

  /** 売り注文数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_SELL_VOLUME = "LONG_SELL_VOLUME";

  /** コマンド名 */
  public static final String CMD_NAME = "BoardInformation";

  /** 別名 */
  public static final String CMD_ALIAS = "302";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのHashMap */
  protected HashMap fBoardInfo;

  /**
   * コンストラクタです．
   */
  public UCBoardInformationCore() {
    fBoardInfo = new HashMap();
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
    return true;
  }

  /**
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    result += fBoardInfo.get(STRING_LAST_UPDATE_TIME);
    ArrayList board = (ArrayList) fBoardInfo.get(ARRAYLIST_BOARD);
    Iterator itr = board.iterator();
    while (itr.hasNext()) {
      HashMap elem = (HashMap) itr.next();
      result += " " + elem.get(LONG_PRICE)
          + " " + elem.get(LONG_BUY_VOLUME)
          + " " + elem.get(LONG_SELL_VOLUME);
    }
    return result;
  }

  /**
   * 板情報(HashMap)を返します．
   * @return 板情報
   */
  public HashMap getResults() {
    return fBoardInfo;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<BoardInformation>>");
    System.out.println("LastUpdateTime:" +
                       fBoardInfo.get(STRING_LAST_UPDATE_TIME));
    Iterator itr = ( (ArrayList) fBoardInfo.get(ARRAYLIST_BOARD)).iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("Price:" + os.get(LONG_PRICE) + ","
                         + "BuyVolume:" + os.get(LONG_BUY_VOLUME) + ","
                         + "SellVolume:" + os.get(LONG_SELL_VOLUME));
    }
  }
}
