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
 * Network版のクライアント上で実行される，注文情報を取得するためのSVMPコマンドクラスです．
 */
public class UCCOrderCheckNet extends UCOrderCheckCore implements IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * コンストラクタです．
   */
  public UCCOrderCheckNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /* (non-Javadoc)
   * @see cmdClientNet.IClientCmdNet#setConnection(java.io.BufferedReader, java.io.PrintWriter)
   */
  public void setConnection(BufferedReader br, PrintWriter pw) {
    fIn = br;
    fOut = pw;
  }

  /* (non-Javadoc)
   * @see cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fData.clear();
      fOut.println(CMD_NAME + " " + fOrderID);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fCommandStatus.setStatus(true);
        fData.put(UCOrderCheckCore.INT_USER_ID, Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.LONG_ORDER_ID, Long.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.STRING_ORDER_TIME, fIn.readLine());
        fData.put(UCOrderCheckCore.STRING_BRAND_NAME, fIn.readLine());
        fData.put(UCOrderCheckCore.INT_ORDER_DATE, Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.INT_ORDER_BOARD_NO,
                  Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.INT_NEW_REPAY, Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.INT_SELL_BUY, Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.INT_MARKET_LIMIT,
                  Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.LONG_ORDER_PRICE, Long.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.LONG_ORDER_VOLUME, Long.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.LONG_SUM_OF_CONTRACT_VOLUME,
                  Long.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.LONG_CANCEL_VOLUME,
                  Long.valueOf(fIn.readLine()));
        ArrayList contractInfoArray = new ArrayList();
        fData.put(UCOrderCheckCore.ARRAYLIST_CONTRACT_INFORMATION_ARRAY,
                  contractInfoArray);
        String s = null;
        while (! (s = fIn.readLine()).equals("+OK")) {
          HashMap contractInfo = new HashMap();
          contractInfo.put(UCOrderCheckCore.LONG_CONTRACT_ID, Long.valueOf(s));
          contractInfo.put(UCOrderCheckCore.STRING_CONTRACT_TIME, fIn.readLine());
          contractInfo.put(UCOrderCheckCore.INT_CONTRACT_DATE,
                           Integer.valueOf(fIn.readLine()));
          contractInfo.put(UCOrderCheckCore.INT_CONTRACT_BOARD_NO,
                           Integer.valueOf(fIn.readLine()));
          contractInfo.put(UCOrderCheckCore.LONG_CONTRACT_PRICE,
                           Long.valueOf(fIn.readLine()));
          contractInfo.put(UCOrderCheckCore.LONG_CONTRACT_VOLUME,
                           Long.valueOf(fIn.readLine()));
          contractInfoArray.add(contractInfo);
        }
      } else if (token.equals("+ERROR")) {
        fCommandStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fCommandStatus.setErrorCode(errCode);
        fCommandStatus.setErrorMessage(fIn.readLine());
        String okMsg = fIn.readLine();
        if (!okMsg.equals("+OK")) {
          System.err.println("Unexpected token in UCCScheduleNet: " + okMsg);
          System.exit(5);
        }
      } else {
        System.err.println("Unexpected token in UCCOrderCheckNet: " + token);
        System.exit(5);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
    return fCommandStatus;
  }

}
