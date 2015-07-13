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
 * Network版のクライアント上で実行される，すべての参加者の現金残高を照会するためのSVMPコマンドクラスです．
 */
public class UCCBalancesNet extends UCBalancesCore implements IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * コンストラクタです．
   */
  public UCCBalancesNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fTodayInfo.clear();
      fYesterdayInfo.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        fTodayInfo.put(LONG_CASH, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_MARGIN, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_UNREALIZED_PROFIT, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_SETTLED_PROFIT, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_FEE, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_INTEREST, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_LOAN, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_SURPLUS, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_CASH, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_MARGIN, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_UNREALIZED_PROFIT, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_SETTLED_PROFIT, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_FEE, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_INTEREST, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_LOAN, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_SURPLUS, Long.valueOf(fIn.readLine()));
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println(
            "Unexpected token in UCCBalancesNet: " + token);
        System.exit(5);
      }
      String okMsg = fIn.readLine();
      if (!okMsg.equals("+OK")) {
        System.err.println(
            "Unexpected token in UCCBalancesNet: " + okMsg);
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
