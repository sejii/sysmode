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
 * Network版のクライアント上で実行される，会員情報を取得するためのSVMPコマンドクラスです．
 */
public class UCCMemberProfileNet extends UCMemberProfileCore implements
    IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * コンストラクタです．
   */
  UCCMemberProfileNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /*
   * (non-Javadoc)
   * @see cmdClientNet.IClientCmdNet#setConnection(java.io.BufferedReader, java.io.PrintWriter)
   */
  public void setConnection(BufferedReader br, PrintWriter pw) {
    fIn = br;
    fOut = pw;
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fData.clear();
      fOut.println(CMD_NAME + " " + fTargetUserId);
      fOut.flush();
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fCommandStatus.setStatus(true);
        fData.put(UCMemberProfileCore.STRING_LOGIN_NAME, fIn.readLine()); // 会員名
        fData.put(UCMemberProfileCore.STRING_PASSWORD, fIn.readLine()); // パスワード
        fData.put(UCMemberProfileCore.STRING_ATTRIBUTE, fIn.readLine()); // エージェント属性(Human or Machine)
        fData.put(UCMemberProfileCore.STRING_CONNECTION, fIn.readLine()); // コネクション(Remote or Local)
        fData.put(UCMemberProfileCore.ARRAY_LIST_ACCESS,
                  UCMemberProfileCore.stringToArrayList(fIn.readLine())); // アクセス制限
        fData.put(UCMemberProfileCore.STRING_REAL_NAME, fIn.readLine()); // 実際の名前
        fData.put(UCMemberProfileCore.ARRAY_LIST_SYSTEM_PARAMETERS,
                  UCMemberProfileCore.stringToArrayList(fIn.readLine())); // システムパラメータ
        fData.put(UCMemberProfileCore.INT_SEED, Integer.valueOf(fIn.readLine())); // 乱数の種
        fData.put(UCMemberProfileCore.LONG_INITIAL_CASH,
                  Long.valueOf(fIn.readLine())); // 初期資産
        fData.put(UCMemberProfileCore.LONG_TRADING_UNIT,
                  Long.valueOf(fIn.readLine())); // 取引単位
        fData.put(UCMemberProfileCore.LONG_FEE_PER_UNIT,
                  Long.valueOf(fIn.readLine())); // 単位取引あたりの手数料
        fData.put(UCMemberProfileCore.LONG_MARGIN_RATE,
                  Long.valueOf(fIn.readLine())); // 証拠金率
        fData.put(UCMemberProfileCore.LONG_MAX_LOAN, Long.valueOf(fIn.readLine())); // 借り入れ限度額
        fData.put(UCMemberProfileCore.DOUBLE_INTEREST,
                  Double.valueOf(fIn.readLine())); // 借り入れ金利
        fData.put(UCMemberProfileCore.INT_STATUS, Integer.valueOf(fIn.readLine())); // 取引可能(+1),取引不可能(-1)
        fData.put(UCMemberProfileCore.INT_NO_OF_LOGIN_AGENTS,
                  Integer.valueOf(fIn.readLine())); // ログイン中のエージェント数
        HashMap yesterdayBalance = new HashMap(); // 前日収支
        yesterdayBalance.put(UCMemberProfileCore.LONG_INITIAL_CASH,
                             Long.valueOf(fIn.readLine())); // 初期所持金
        yesterdayBalance.put(UCMemberProfileCore.LONG_LOAN,
                             Long.valueOf(fIn.readLine())); // 借入金
        yesterdayBalance.put(UCMemberProfileCore.LONG_UNREALIZED_PROFIT,
                             Long.valueOf(fIn.readLine())); // 未実現損益
        yesterdayBalance.put(UCMemberProfileCore.LONG_MARGIN,
                             Long.valueOf(fIn.readLine())); // 預託証拠金
        yesterdayBalance.put(UCMemberProfileCore.LONG_SUM_OF_FEE,
                             Long.valueOf(fIn.readLine())); // 総支払い手数料
        yesterdayBalance.put(UCMemberProfileCore.LONG_SUM_OF_INTEREST,
                             Long.valueOf(fIn.readLine())); // 総支払い金利
        yesterdayBalance.put(UCMemberProfileCore.LONG_CASH,
                             Long.valueOf(fIn.readLine())); // 保有現金
        yesterdayBalance.put(UCMemberProfileCore.LONG_PROFIT,
                             Long.valueOf(fIn.readLine())); // 実現損益
        fData.put(HASHMAP_YESTERDAY_BALANCE, yesterdayBalance);
        HashMap todayBalance = new HashMap(); // 当日収支
        todayBalance.put(UCMemberProfileCore.LONG_INITIAL_CASH,
                         Long.valueOf(fIn.readLine())); // 初期所持金
        todayBalance.put(UCMemberProfileCore.LONG_LOAN,
                         Long.valueOf(fIn.readLine())); // 借入金
        todayBalance.put(UCMemberProfileCore.LONG_UNREALIZED_PROFIT,
                         Long.valueOf(fIn.readLine())); // 未実現損益
        todayBalance.put(UCMemberProfileCore.LONG_MARGIN,
                         Long.valueOf(fIn.readLine())); // 預託証拠金
        todayBalance.put(UCMemberProfileCore.LONG_SUM_OF_FEE,
                         Long.valueOf(fIn.readLine())); // 総支払い手数料
        todayBalance.put(UCMemberProfileCore.LONG_SUM_OF_INTEREST,
                         Long.valueOf(fIn.readLine())); // 総支払い金利
        todayBalance.put(UCMemberProfileCore.LONG_CASH,
                         Long.valueOf(fIn.readLine())); // 保有現金
        todayBalance.put(UCMemberProfileCore.LONG_PROFIT,
                         Long.valueOf(fIn.readLine())); // 実現損益
        fData.put(HASHMAP_TODAY_BALANCE, todayBalance);
        HashMap position = new HashMap(); // ポジション
        position.put(UCMemberProfileCore.
                     LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY,
                     Long.valueOf(fIn.readLine())); // 前日までの売ポジションの合計
        position.put(UCMemberProfileCore.
                     LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY,
                     Long.valueOf(fIn.readLine())); // 前日までの買ポジションの合計
        position.put(UCMemberProfileCore.LONG_TODAY_SELL_POSITIONS,
                     Long.valueOf(fIn.readLine())); // 当日の売ポジションの合計
        position.put(UCMemberProfileCore.LONG_TODAY_BUY_POSITIONS,
                     Long.valueOf(fIn.readLine())); // 当日の買ポジションの合計
        fData.put(HASHMAP_POSITION, position);
        String okMsg = fIn.readLine();
        if (!okMsg.equals("+OK")) {
          System.err.println("Unexpected token in UCCMemberProfileNet: " +
                             okMsg);
          System.exit(5);
        }
      } else if (token.equals("+ERROR")) {
        fCommandStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fCommandStatus.setErrorCode(errCode);
        fCommandStatus.setErrorMessage(fIn.readLine());
        String okMsg = fIn.readLine();
        if (!okMsg.equals("+OK")) {
          System.err.println("Unexpected token in UCCMemberProfileNet: " +
                             okMsg);
          System.exit(5);
        }
      } else {
        System.err.println("Unexpected token in UCCMemberProfileNet: " + token);
        System.exit(5);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
    return fCommandStatus;
  }

}
