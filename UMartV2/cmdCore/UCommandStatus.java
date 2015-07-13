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

import java.io.*;
import java.util.*;

/**
 * コマンドのサーバーでの実行結果の状態を保持するクラスです．
 */
public class UCommandStatus {

  /** 実行結果の状態 (true: 成功, false: 失敗) */
  private boolean fStatus;

  /** エラーコード */
  private int fErrorCode;

  /** エラーメッセージ */
  private String fErrorMessage;

  /**
   * コンストラクタです．
   */
  public UCommandStatus() {
  }

  /**
   * brから実行結果状態を読み込み, 解析します．
   * @param br ストリーム
   */
  public void readFrom(BufferedReader br) throws IOException {
    String line = br.readLine();
    StringTokenizer st = new StringTokenizer(line);
    String token = st.nextToken();
    if (token.equals("+ACCEPT")) {
      fStatus = true;
      return;
    } else if (token.equals("+ERROR")) {
      token = st.nextToken();
      fErrorCode = Integer.parseInt(token);
      fErrorMessage = br.readLine();
    } else {
      System.err.println("Error in UCommandStatus.readFrom");
      System.exit(5);
    }
  }

  /**
   * 実行結果の状態を設定します．
   * @param status 実行結果の状態
   */
  public void setStatus(boolean status) {
    fStatus = status;
  }

  /**
   * 実行結果の状態を返します．
   * @return 実行結果の状態
   */
  public boolean getStatus() {
    return fStatus;
  }

  /**
   * エラーコードを設定します．
   * @param code エラーコード
   */
  public void setErrorCode(int code) {
    fErrorCode = code;
  }

  /**
   * エラーコードを返します．
   * @return エラーコード
   */
  public int getErrorCode() {
    return fErrorCode;
  }

  /**
   * エラーメッセージを設定します．
   * @param msg エラーメッセージ
   */
  public void setErrorMessage(String msg) {
    fErrorMessage = msg;
  }

  /**
   * エラーメッセージを返します．
   * @return エラーメッセージ
   */
  public String getErrorMessage() {
    return fErrorMessage;
  }

}
