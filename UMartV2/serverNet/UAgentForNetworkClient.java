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
package serverNet;

import java.io.*;
import java.net.*;
import java.util.*;

import cmdCore.*;
import cmdServer.*;
import serverCore.*;

/**
 * ネットワーク越しのクライアントと一対一の通信を行い, 適当なコマンドオブジェクトを実行するクラスです．
 */
public class UAgentForNetworkClient implements Runnable {

  /** クライアントとの通信用ソケット */
  private Socket fSocket;

  /** クライアントとの通信用の出力ストリーム */
  private PrintWriter fOut;

  /** クライアントとの通信用の入力ストリーム */
  private BufferedReader fIn;

  /** スレッドオブジェクト */
  private Thread fThread;

  /** プロトコルオブジェクト */
  private UProtocolForServer fProtocol;

  /** このエージェントのログイン状態 */
  private ULoginStatus fLoginStatus;

  /** U-Martオブジェクトへの参照 */
  private UMartNetwork fUMartNetwork;

  /** スレッドの終了フラグ */
  private boolean fDoneFlag;

  /**
   * コンストラクタです．
   * 必要なコマンドオブジェクトの登録, スレッドの生成も行っています．
   * @param s ソケット
   * @param umart ネットワーク版UMartオブジェクト
   */
  public UAgentForNetworkClient(Socket s, UMartNetwork umart) {
    try {
      fSocket = s;
      fSocket.setTcpNoDelay(true);
      fProtocol = new UProtocolForServer();
      fProtocol.setConnection(this, umart);
      fLoginStatus = null;
      fUMartNetwork = umart;
      OutputStream os = s.getOutputStream();
      fOut = new PrintWriter(os);
      InputStream is = s.getInputStream();
      fIn = new BufferedReader(new InputStreamReader(is));
      fThread = new Thread(this);
      fThread.start();
    } catch (IOException e) {
      System.out.println("Exception: " + e + " in UAgent");
      System.exit(5);
    }
  }

  /**
   * ログイン状態を返します．
   * @return ログイン状態
   */
  public ULoginStatus getLoginStatus() {
    return fLoginStatus;
  }

  /**
   * 引数で指定された文字列sをクライアントへ送ります．
   * @param s 転送文字列
   */
  public void sendMessage(String s) {
    fOut.println(s);
  }

  /**
   * クライアントへの出力ストリームをフラッシュします．
   *
   */
  public void flushMessage() {
    fOut.flush();
  }

  /**
   * ログアウト処理を行う。具体的には, ストリームおよびソケットを閉じて,
   * 終了フラグを立てます．
   */
  public void logout() {
    try {
      fDoneFlag = true;
      fOut.close();
      fIn.close();
      fSocket.close();
      fSocket = null;
      fThread = null;
      System.out.println("Logout: ID=" + fLoginStatus.getUserID());
    } catch (Exception e) {
      sendMessage("+ERROR");
      flushMessage();
    }
  }

  /**
   * ログイン処理を行います．
   * @param st 入力ストリーム
   * @return
   */
  private boolean handleLogin(StringTokenizer st) {
    try {
      String name = "NotInitialized";
      String passwd = "NotInitialized";
      name = st.nextToken();
      passwd = st.nextToken();
      fLoginStatus = fUMartNetwork.doLogin(name, passwd);
      if (fLoginStatus != null) {
        System.out.println("Login Success: " + name);
        sendMessage("+ACCEPT");
        flushMessage();
        return true;
      } else {
        System.err.println("Login Failiar: " + name);
        UServerStatus status = fUMartNetwork.getStatus();
        if (status.getState() == UServerStatus.SU_LOGIN) {
          sendMessage("+ERROR " + ICommand.UNACCEPTABLE_COMMAND);
          sendMessage("MARKET STATE IS " + status.getStateString());
        } else {
          sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
          sendMessage("USAGE: LOGIN <MEMBERID> <PASSWD> [<NICKNAME> <CLASSNAME>  <SEED> [<PARAM1:PARAM2:...>]]");
        }
        flushMessage();
        return false;
      }
    } catch (NoSuchElementException nsee) {
      nsee.printStackTrace();
      sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      sendMessage("USAGE: LOGIN <MEMBERID> <PASSWD> [<NICKNAME> <CLASSNAME> <SEED> [<PARAM1:PARAM2:...>]]");
      flushMessage();
      return false;
    } catch (Exception e) {
      System.out.println("Exception: " + e + " in UAgent.handleLogin");
      System.exit(5);
      return false;
    }
  }

  /**
   * 接続処理を行います．
   * @return true:成功，false：失敗
   */
  private boolean handleConnection() {
    try {
      sendMessage("+LOGIN SVMP(0.01)");
      while (true) {
        flushMessage();
        StringTokenizer st = new StringTokenizer(fIn.readLine());
        if (!st.hasMoreTokens()) {
          sendMessage("+ERROR " + ICommand.INVALID_COMMAND);
          sendMessage("USAGE: LOGIN <MEMBERID> <PASSWD> [<NICKNAME> <CLASSNAME> <SEED> [<PARAM1:PARAM2:...>]]");
          flushMessage();
          continue;
        }
        String cmd = st.nextToken();
        if (cmd.equalsIgnoreCase(UCLoginCore.CMD_NAME) ||
            cmd.equals(UCLoginCore.CMD_ALIAS)) {
          if (handleLogin(st)) {
            return true;
          } else {
            continue;
          }
        } else if (cmd.equalsIgnoreCase(UCLogoutCore.CMD_NAME) ||
                   cmd.equals(UCLogoutCore.CMD_ALIAS)) {
          System.err.println("Logout command is performed.");
          return false;
        } else {
          sendMessage("+ERROR " + ICommand.INVALID_COMMAND);
          sendMessage("USAGE: LOGIN <MEMBERID> <PASSWD> [<NICKNAME> <CLASSNAME> <SEED> [<PARAM1:PARAM2:...>]]");
          flushMessage();
          continue;
        }
      }
    } catch (NullPointerException ne) {
      System.out.println("Connection seems to be closed by peer ...");
      return false;
    } catch (Exception e) {
      System.out.println("Exception: " + e + " in UAgent.handleConnection");
      return false;
    }
  }

  /**
   * クライアントからメッセージを受け付けて, 適当なコマンドオブジェクトを
   * 実行するメインループです．
   */
  public void run() {
    try {
      if (!handleConnection()) {
        fIn.close();
        fOut.close();
        fSocket.close();
        return;
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
    try {
      fDoneFlag = false;
      fLoginStatus.incrementNoOfLoginAgents();
      while (!fDoneFlag) {
        sendMessage("+OK");
        flushMessage();
        String line = fIn.readLine();
        StringTokenizer st = new StringTokenizer(line);
        if (!st.hasMoreTokens()) {
          sendMessage("+ERROR " + ICommand.INVALID_COMMAND);
          sendMessage("UNKNOWN COMMAND");
          flushMessage();
          continue;
        }
        String str = st.nextToken();
        ICommand c = fProtocol.getCommand(str);
        if (c != null) {
          c.setArguments(st);
          c.doIt();
        } else {
          System.out.println("Unknown command: " + line);
          sendMessage("+ERROR " + ICommand.INVALID_COMMAND);
          sendMessage("UNKNOWN COMMAND");
          flushMessage();
        }
      }
    } catch (NullPointerException ne) {
      System.out.println("Logout : ID=" + fLoginStatus.getUserID());
    } catch (SocketException se) {
      System.err.println("socket exception");
      System.out.println("Logout : ID=" + fLoginStatus.getUserID());
    } catch (Exception e) {
      System.err.println(e);
    } finally {
      fLoginStatus.decrementNoOfLoginAgents();
    }
  }
}
