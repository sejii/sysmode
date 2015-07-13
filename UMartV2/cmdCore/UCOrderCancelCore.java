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
 * 注文取消のためのSVMPコマンドの抽象クラスです．
 */
public abstract class UCOrderCancelCore implements ICommand {

  /** 注文IDを引くためのキー(値はIntegerオブジェクト) */
  public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

  /** 取消受付時刻を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_CANCEL_TIME = "STRING_CANCEL_TIME";

  /** 取消数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_CANCEL_VOLUME = "LONG_CANCEL_VOLUME";

  /** 非取消数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_UNCANCEL_VOLUME = "LONG_UNCANCEL_VOLUME";

  /** コマンド名 */
  public static final String CMD_NAME = "OrderCancel";

  /** 別名 */
  public static final String CMD_ALIAS = "202";

  /** 注文ID */
  protected long fOrderID;

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのHashMap */
  protected HashMap fCancelInfo;

  /**
   * コンストラクタです．
   */
  public UCOrderCancelCore() {
    super();
    fOrderID = -1;
    fCancelInfo = new HashMap();
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
      fOrderID = Long.parseLong(st.nextToken());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * コマンド実行に必要な引数を設定します．
   * @param orderID 注文ID
   */
  public void setArguments(long orderID) {
    fOrderID = orderID;
  }

  /**
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = fCancelInfo.get(STRING_CANCEL_TIME) + " "
        + fCancelInfo.get(LONG_CANCEL_VOLUME) + " "
        + fCancelInfo.get(LONG_UNCANCEL_VOLUME);
    return result;
  }

  /**
   * 注文取消情報を返します．
   * @return 注文取消情報
   */
  public HashMap getResults() {
    return fCancelInfo;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<OrderCancel " + fOrderID + ">>");
    System.out.println("OrderID:" + fCancelInfo.get(LONG_ORDER_ID) + ","
                       + "CancelTime:" + fCancelInfo.get(STRING_CANCEL_TIME)
                       + ","
                       + "CancelVolume:" + fCancelInfo.get(LONG_CANCEL_VOLUME)
                       + ","
                       + "UncanceldVolume:"
                       + fCancelInfo.get(LONG_UNCANCEL_VOLUME));
  }
}
