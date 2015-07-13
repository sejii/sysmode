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
 * 全メンバーのログインを管理します．
 */

public class ULoginManager {

  /** 全メンバーのログイン状態を格納するためのベクタ */
  private Vector fLoginStatusArray;

  /**
   * ULoginStatusManagerオブジェクトの生成および初期化を行います．
   */
  public ULoginManager() {
    fLoginStatusArray = new Vector();
  }

  /**
   * 引数で与えられるユーザー情報にしたがって新しいログイン状態を
   * 生成, 登録を行います．ただし, 同一ユーザー名の接続状態を作ることは
   * できません．
   * @param id ユーザーID
   * @return true: 成功, false: 失敗
   */
  public boolean creatLoginStatus(int id) {
    Enumeration e = fLoginStatusArray.elements();
    while (e.hasMoreElements()) {
      ULoginStatus x = (ULoginStatus) e.nextElement();
      if (x.getUserID() == id) {
        System.out.print("ERROR:ULoginStatusManager::createLoginStatus: " + id);
        System.err.println(" has already existed.");
        return false;
      }
    }
    fLoginStatusArray.addElement(new ULoginStatus(id));
    return true;
  }

  /**
   * 引数で与えられるユーザーIDのログイン状態を返します．
   * 見つからない場合は, nullを返します．
   * @param userID ユーザーID
   * @return 対応するログイン状態オブジェクト。ただし, 対応する事務所が
   *         見つからない場合はnull。
   */
  public ULoginStatus findLoginStatus(int userID) {
    Enumeration e = fLoginStatusArray.elements();
    while (e.hasMoreElements()) {
      ULoginStatus x = (ULoginStatus) e.nextElement();
      if (x.getUserID() == userID) {
        return x;
      }
    }
    return null;
  }

  /**
   * 登録されている全てのログイン状態を含むベクタを返します．
   * @return ログイン状態を含むベクタ
   */
  public Vector getLoginStatuses() {
    return fLoginStatusArray;
  }

}
