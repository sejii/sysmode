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
 * ポジション照会のためのSVMPコマンドの抽象クラスです．
 */
public abstract class UCPositionCore implements ICommand {

  /** 当日の売り建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_TODAY_SELL = "LONG_TODAY_SELL";

  /** 当日の買い建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_TODAY_BUY = "LONG_TODAY_BUY";

  /** 前日までの売り建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_YESTERDAY_SELL = "LONG_YESTERDAY_SELL";

  /** 前日までの買い建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_YESTERDAY_BUY = "LONG_YESTERDAY_BUY";

  /** コマンド名 */
  public static final String CMD_NAME = "Position";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのHashMap */
  protected HashMap fPositionInfo;

  /**
   * コンストラクタです．
   */
  public UCPositionCore() {
    super();
    fPositionInfo = new HashMap();
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
    return true;
  }

  /**
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = fPositionInfo.get(LONG_TODAY_SELL) + " "
        + fPositionInfo.get(LONG_TODAY_BUY) + " "
        + fPositionInfo.get(LONG_YESTERDAY_SELL) + " "
        + fPositionInfo.get(LONG_YESTERDAY_BUY);
    return result;
  }

  /**
   * 注文依頼情報を返します．
   * @return 注文依頼情報
   */
  public HashMap getResults() {
    return fPositionInfo;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<Position>>");
    System.out.println("TodaySell:" + fPositionInfo.get(LONG_TODAY_SELL) + ","
                       + "TodayBuy:" + fPositionInfo.get(LONG_TODAY_BUY) + ","
                       + "YesterdaySell:" +
                       fPositionInfo.get(LONG_YESTERDAY_SELL) + ","
                       + "YesterdayBuy:" + fPositionInfo.get(LONG_YESTERDAY_BUY));
  }
}
