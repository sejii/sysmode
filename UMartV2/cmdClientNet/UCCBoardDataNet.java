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
package cmdClientNet;

import java.io.*;
import java.util.*;

import cmdCore.*;

/**
 * Network版のクライアント上で実行される，取引開始時から現在までの板情報を取得するためのSVMPコマンドクラスです．
 */
public class UCCBoardDataNet extends UCBoardDataCore implements IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * コンストラクタです．
   */
  public UCCBoardDataNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fBoardDataArray.clear();
      fBoardDataInfo.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        StringTokenizer hst = new StringTokenizer(fIn.readLine());
        fBoardDataInfo.put(LONG_TOTAL_BUY_VOLUME, Long.valueOf(hst.nextToken()));
        fBoardDataInfo.put(LONG_MIN_PRICE, Long.valueOf(hst.nextToken()));
        fBoardDataInfo.put(LONG_MAX_PRICE, Long.valueOf(hst.nextToken()));
        fBoardDataInfo.put(LONG_CONTRACT_PRICE, Long.valueOf(hst.nextToken()));
        fBoardDataInfo.put(LONG_CONTRACT_VOLUME, Long.valueOf(hst.nextToken()));
        String s = "";
        while (! (s = fIn.readLine()).equals("+OK")) {
          HashMap os = new HashMap();
          StringTokenizer ast = new StringTokenizer(s);
          os.put(LONG_PRICE, Long.valueOf(ast.nextToken()));
          os.put(STRING_SELL_BUY, ast.nextToken());
          os.put(LONG_VOLUME, Long.valueOf(ast.nextToken()));
          fBoardDataArray.add(os);
        }
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
        String s = fIn.readLine();
        if (!s.equals("+OK")) {
          System.err.println("Unexpected token in UCCBoardDataNet: " + s);
          System.exit(5);
        }
      } else {
        System.err.println("Unexpected token in UCCBoardDataNet: " + token);
        System.exit(5);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
    return fStatus;
  }

  /**
   * @see cmdClientNet.IClientCmdNet#setConnection(BufferedReader, PrintWriter)
   */
  public void setConnection(BufferedReader br, PrintWriter pw) {
    fIn = br;
    fOut = pw;
  }
}
