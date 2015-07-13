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
 * 各ユーザー(メンバー)に対する接続中のクライアント数を管理します．
 * U-Martシステムでは，同一ユーザが複数ログインすることを許しているため，
 * 接続中のクライアント数を保持しています．
 */

public class ULoginStatus {

  /** 接続中のクライアント数 */
  private int fNoOfLoginAgents;

  /** ユーザーID */
  private int fUserID;

  /**
   * ULoginStatusオブジェクトを生成, 初期化します．
   * @param userID ユーザーID
   */
  public ULoginStatus(int userID) {
    fUserID = userID;
    fNoOfLoginAgents = 0;
  }

  /**
   * ユーザーIDを返します．
   * @return ユーザーID
   */
  public int getUserID() {
    return fUserID;
  }

  /**
   * ログイン中のユーザー数を1だけ増します．
   */
  synchronized public void incrementNoOfLoginAgents() {
    fNoOfLoginAgents++;
    // System.out.println("No of userID" + fUserID + " is " + fNoOfLoginAgents);
  }

  /**
   * ログイン中のユーザー数を1だけ減らします．
   */
  synchronized public void decrementNoOfLoginAgents() {
    if (fNoOfLoginAgents-- < 0) {
      System.out.println("Agent number is minus");
      System.exit(1);
    }
    System.out.println("No of userID" + fUserID + " is " + fNoOfLoginAgents);
  }

  /**
   * ログイン中のユーザー数を返します．
   * @return ログイン中のユーザー数
   */
  synchronized public int getNoOfLoginAgents() {
    return fNoOfLoginAgents;
  }

}
