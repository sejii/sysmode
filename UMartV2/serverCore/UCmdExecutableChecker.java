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

import java.io.*;
import java.util.*;

/**
 * 現在のサーバーの状態に応じて，指定されたSVMPコマンドが実行できるかチェックするクラスです．
 * 指定されたSVMPコマンドが実行できるかは，resources/csv/SVMP.csvに記述されています．
 */
public class UCmdExecutableChecker {

  /**
   * キーは”コマンド名：状態：SUフラグ”，値は”TRUE(実行可能)/FALSE(実行不可能)”
   * ただし，SUフラグは，"Su":スーパーユーザー，"Non-Su":スーパーユーザー以外
   */
  private HashMap fCmdExecutableTable;

  /** エラーメッセージ */
  private String fErrorMessage;

  /**
   * コンストラクタです．
   */
  public UCmdExecutableChecker() {
    fCmdExecutableTable = new HashMap();
  }

  /**
   * ストリームから情報を読み込みます．
   * @param br 入力ストリーム
   * @throws IOException
   */
  public void readFrom(BufferedReader br) throws IOException {
    br.readLine(); // numerical header
    String header = br.readLine();
    StringTokenizer st = new StringTokenizer(header, ",");
    st.nextToken();
    ArrayList statusArray = new ArrayList();
    while (st.hasMoreTokens()) {
      statusArray.add(st.nextToken());
    }
    String line = null;
    while ( (line = br.readLine()) != null) {
      st = new StringTokenizer(line, ",");
      String cmdName = st.nextToken();
      int su = Integer.parseInt(st.nextToken());
      int index = 0;
      while (st.hasMoreTokens()) {
        boolean cmdExecutableFlag = false;
        if (Integer.parseInt(st.nextToken()) == 1) {
          cmdExecutableFlag = true;
        }
        String key = cmdName + ":" + (String) statusArray.get(index) + ":";
        if (su == 1) {
          fCmdExecutableTable.put(key + "Su", new Boolean(cmdExecutableFlag));
        } else if (su == 0) {
          fCmdExecutableTable.put(key + "Non-Su", new Boolean(cmdExecutableFlag));
        } else {
          System.err.println("Error in UCmdExecutableCheker.readFrom");
          System.exit(5);
        }
        ++index;
      }
    }
  }

  /**
   * statusの状態で，userIDをもつユーザーが，cmdNameを実行できるかチェックします．
   * @param cmdName コマンド名
   * @param status サーバー状態
   * @param userID ユーザーID
   * @return true:実行可能，false:実行不可能
   */
  public boolean isExecutable(String cmdName, UServerStatus status, int userID) {
    String key = cmdName + ":" + status.getStateString() + ":";
    Boolean result = null;
    if (userID == UMart.SU_ID) {
      result = (Boolean) fCmdExecutableTable.get(key + "Su");
      if (result == null) {
        result = (Boolean) fCmdExecutableTable.get(key + "Non-Su");
        if (result == null) {
          System.err.print("Can't find " + cmdName + ":" +
                           status.getStateString() + ":" + userID);
          System.err.println(" in UCmdExecutableChecker.isExecutable");
          System.exit(5);
          return false;
        }
      }
      if (result.booleanValue() == false) {
        fErrorMessage = "SERVER STATE IS " + status.getStateString();
      }
      return result.booleanValue();
    } else {
      result = (Boolean) fCmdExecutableTable.get(key + "Non-Su");
      if (result == null) {
        Boolean suResult = (Boolean) fCmdExecutableTable.get(key + "Su");
        if (suResult != null) {
          fErrorMessage = "THIS COMMAND CAN USE ONLY SU";
          return false;
        } else {
          System.err.print("Can't find " + cmdName + ":" +
                           status.getStateString() + ":" + userID);
          System.err.println(" in UCmdExecutableChecker.isExecutable");
          System.exit(5);
          return false;
        }
      } else {
        if (result.booleanValue() == false) {
          fErrorMessage = "SERVER STATE IS " + status.getStateString();
        }
        return result.booleanValue();
      }
    }
  }

  /**
   * エラーメッセージを返します．
   * @return エラーメッセージ
   */
  public String getErrorMessage() {
    return fErrorMessage;
  }

  /**
   * 内部状態を標準出力に出力します．
   */
  public void printOn() {
    Iterator itr = fCmdExecutableTable.keySet().iterator();
    while (itr.hasNext()) {
      String key = (String) itr.next();
      Boolean value = (Boolean) fCmdExecutableTable.get(key);
      System.out.println(key + "=" + value);
    }
    // System.out.println(fCmdExecutableTable.toString());
  }

  /**
   * テスト用メインメソッドです．
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: java UCmdExecutableChecker csvFile");
      System.exit(1);
    }
    String filename = args[0];
    UCmdExecutableChecker chk = new UCmdExecutableChecker();
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new
          FileInputStream(filename)));
      chk.readFrom(br);
      br.close();
      BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
      String line = null;
      while (true) {
        System.out.print("cmdName status id>");
        line = br2.readLine();
        if (line.equals("exit")) {
          break;
        }
        if (line.equals("print")) {
          chk.printOn();
        } else {
          StringTokenizer st = new StringTokenizer(line);
          String cmdName = st.nextToken();
          UServerStatus status = new UServerStatus();
          status.setState(Integer.parseInt(st.nextToken()));
          int id = Integer.parseInt(st.nextToken());
          boolean flag = chk.isExecutable(cmdName, status, id);
          System.out.println(cmdName + ":" + status.getStateString() + ":" + id +
                             "=" + flag);
          if (!flag) {
            System.out.println(chk.getErrorMessage());
          }
        }
        System.out.println("cmdName status id>");
      }

    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }
}
