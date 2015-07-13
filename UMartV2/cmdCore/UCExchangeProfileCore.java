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
 * 取引所情報を取得するためのSVMPコマンドの抽象クラスです．
 * 取引所情報とは，取引所の保有現金，取引所の管理下の売ポジション，買ポジション，会員数のことです．
 */
public abstract class UCExchangeProfileCore implements ICommand {

  /** コマンド名 */
  public static final String CMD_NAME = "ExchangeProfile";

  /** コマンドの実行結果の状態 */
  protected UCommandStatus fCommandStatus;

  /** 取引所情報 */
  protected HashMap fData;

  /** 保有現金を引くためのキー */
  public static final String LONG_CASH = "LONG_CASH";

  /** 取引所の管理下の売ポジションを引くためのキー */
  public static final String LONG_SELL_POSITION = "LONG_SELL_POSITION";

  /** 取引所の管理下の買ポジションを引くためのキー */
  public static final String LONG_BUY_POSITION = "LONG_BUY_POSITION";

  /** 会員数を引くためのキー */
  public static final String INT_NO_OF_MEMBERS = "INT_NO_OF_MEMBERS";

  /**
   * コンストラクタです．
   */
  public UCExchangeProfileCore() {
    super();
    fCommandStatus = new UCommandStatus();
    fData = new HashMap();
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#isNameEqualTo(java.lang.String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME)) {
      return true;
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#setArguments(java.util.StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    try {
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println(fData.toString());
  }

  /**
   * 取引所情報を返します．
   * @return 取引所情報
   */
  public HashMap getData() {
    return fData;
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String returnStr = "";
    returnStr += fData.get(UCExchangeProfileCore.LONG_CASH).toString() + "\n";
    returnStr += fData.get(UCExchangeProfileCore.LONG_SELL_POSITION).toString() + "\n";
    returnStr += fData.get(UCExchangeProfileCore.LONG_BUY_POSITION).toString() + "\n";
    returnStr += fData.get(UCExchangeProfileCore.INT_NO_OF_MEMBERS).toString() + "\n";
    return returnStr;
  }

}
