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
 * Network版のクライアント上で実行される，板情報を取得するためのSVMPコマンドクラスです．
 */
public class UCCBoardInformationNet extends UCBoardInformationCore implements
    IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * コンストラクタです．
   */
  public UCCBoardInformationNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fBoardInfo.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        fBoardInfo.put(STRING_LAST_UPDATE_TIME, fIn.readLine());
        ArrayList boardArray = new ArrayList();
        fBoardInfo.put(ARRAYLIST_BOARD, boardArray);
        st = new StringTokenizer(fIn.readLine());
        HashMap marketPriceInfo = new HashMap();
        marketPriceInfo.put(LONG_PRICE, new Long( -1));
        marketPriceInfo.put(LONG_SELL_VOLUME, Long.valueOf(st.nextToken()));
        marketPriceInfo.put(LONG_BUY_VOLUME, Long.valueOf(st.nextToken()));
        boardArray.add(marketPriceInfo);
        String s = null;
        while (! (s = fIn.readLine()).equals("+OK")) {
          HashMap limitPriceInfo = new HashMap();
          StringTokenizer ast = new StringTokenizer(s);
          limitPriceInfo.put(LONG_PRICE, Long.valueOf(ast.nextToken()));
          limitPriceInfo.put(LONG_SELL_VOLUME, Long.valueOf(ast.nextToken()));
          limitPriceInfo.put(LONG_BUY_VOLUME, Long.valueOf(ast.nextToken()));
          boardArray.add(limitPriceInfo);
        }
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println("Unexpected token in UCCBoardInformationNet: " +
                           token);
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
