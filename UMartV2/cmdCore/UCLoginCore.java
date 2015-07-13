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
 * ログインするためのSVMPコマンドの抽象クラスです．
 */
abstract public class UCLoginCore implements ICommand {

  /** コマンド名 */
  public static final String CMD_NAME = "Login";

  /** 別名 */
  public static final String CMD_ALIAS = "101";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** ユーザー名 */
  protected String fUserName;

  /** パスワード */
  protected String fPasswd;

  /**
   * コンストラクタです．
   */
  public UCLoginCore() {
    super();
    fStatus = new UCommandStatus();
    fUserName = null;
    fPasswd = null;
  }

  /* (non-Javadoc)
   * @see cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /* (non-Javadoc)
   * @see cmdCore.ICommand#isNameEqualTo(java.lang.String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME) || name.equals(CMD_ALIAS)) {
      return true;
    } else {
      return false;
    }
  }

  /* (non-Javadoc)
   * @see cmdCore.ICommand#setArguments(java.util.StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    try {
      fUserName = st.nextToken();
      fPasswd = st.nextToken();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 引数を設定します．
   * @param userName ユーザ名
   * @param passwd パスワード
   * @return true:成功, false:失敗
   */
  public boolean setArguments(String userName, String passwd) {
    fUserName = userName;
    fPasswd = passwd;
    return true;
  }

  /* (non-Javadoc)
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    if (fStatus.getStatus()) {
      System.out.println("Login as " + fUserName + " succeeded.");
    } else {
      System.out.println("Login as " + fUserName + " failed.");
    }
  }

  /* (non-Javadoc)
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    return null;
  }

}
