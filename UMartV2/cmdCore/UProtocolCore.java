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
 * SVMPコマンドオブジェクト集合を保持するプロトコルクラスです．
 * SVMPコマンド名をキーとして使って適切なSVMPオブジェクトを取得することができます．
 */
public abstract class UProtocolCore {

	/** SVMPコマンドオブジェクトのデータベース */
  protected HashMap fCommandHash;

  /**
   * コンストラクタです．
   */
  public UProtocolCore() {
    fCommandHash = new HashMap();
  }

  /**
   * SVMPコマンドオブジェクトのデータベースを返します．
   * @return SVMPコマンドオブジェクトのデータベース(HashMap)
   */
  public HashMap getCommandHashMap() {
    return fCommandHash;
  }

  /**
   * コマンド名がcmdStrのSVMPコマンドオブジェクトを返します．
   * @param cmdStr コマンド名
   * @return SVMPコマンドオブジェクト
   */
  public ICommand getCommand(String cmdStr) {
    return (ICommand)fCommandHash.get(cmdStr);
  }
}
