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
package strategyCore;

import java.io.*;
import java.util.*;

import cmdCore.*;

/**
 * マシンエージェントのルートクラスです．
 * 全てのマシンエージェントはUBaseAgentを継承して，
 * doActionsメソッドとsetParametersメソッドをオーバーライドする必要があります．
 * サーバとの通信には，getUmcpメソッドでCProtocolCoreオブジェクトを取得した後，
 * CProtocolCoreオブジェクトからSVMPコマンドオブジェクトを取得して
 * 適切な引数を設定した後に実行します．
 */
public class UBaseAgent {

  /** ログイン名 */
  protected String fLoginName;

  /** パスワード */
  protected String fPasswd;

  /** 実際の名前（Humanの場合：氏名，Machineの場合：クラス名） */
  protected String fRealName;

  /** ユーザID */
  protected int fUserID;

  /** 乱数の種 */
  protected int fSeed;

  /** 乱数 */
  protected Random fRandom;

  /** プロトコル・ライブラリ */
  protected UProtocolCore fUmcp;

  /** システムパラメータ */
  protected String[] fSystemParameters;

  /** 出力ストリーム */
  protected OutputStream fOutputStream = null;

  /** fOutputStreamから生成されるPrintWriterオブジェクト */
  protected PrintWriter fPrintWriter;

  /**
   * UBaseAgentオブジェクトの生成および初期化を行います．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実際の名前（Humanの場合：氏名，Machineの場合：クラス名）
   * @param seed 乱数の種
   */
  public UBaseAgent(String loginName, String passwd, String realName,
                       int seed) {
    fLoginName = loginName;
    fPasswd = passwd;
    fRealName = realName;
    fSeed = seed;
    fRandom = new Random(seed);
    fUmcp = null;
    fUserID = -1;
    fSystemParameters = new String[0];
    setOutputStream(System.out); // デフォルトでは，標準出力を利用する．
  }

  /**
   * エージェントのシステムパラメータを設定します．
   * このメソッドをオーバーライドする場合，必ず，最初にsuper.setParametersを読んでください．
   * @param args システムパラメータ
   */
  public void setParameters(String[] args) {
    fSystemParameters = new String[args.length];
    message("**** Parameters given by CSV file ****");
    for (int i = 0; i < args.length; ++i) {
      fSystemParameters[i] = args[i];
      message(args[i]);
    }
    message("****************************************");
  }

  /**
   * メッセージを出力します．
   * @param msg String メッセージ
   */
  public void message(String msg) {
    if (fPrintWriter != null) {
      fPrintWriter.println(msg);
      fPrintWriter.flush();
    }
  }
  
  /**
   * メッセージを改行なしで出力します．
   * @param msg メッセージ
   */
  public void print(String msg) {
    if (fPrintWriter != null) {
      fPrintWriter.print(msg);
      fPrintWriter.flush();
    }  	
  }

  /**
   * メッセージを改行ありで出力します．
   * @param msg メッセージ
   */
  public void println(String msg) {
    if (fPrintWriter != null) {
      fPrintWriter.println(msg);
      fPrintWriter.flush();
    }  	
  }

  /**
   * 例外を出力します．
   * @param ex Exception 例外
   */
  public void message(Exception ex) {
    if (fPrintWriter != null) {
      ex.printStackTrace(fPrintWriter);
    }
  }

  /**
   * 出力ストリームを設定します．
   * @param os 出力ストリーム
   */
  public void setOutputStream(OutputStream os) {
    fOutputStream = os;
    if (fOutputStream == null) {
      fPrintWriter = null;
    } else {
      fPrintWriter = new PrintWriter(os, true);
    }
  }

  /**
   * 出力ストリームを返します．
   * @return 出力ストリーム
   */
  public OutputStream getOutputStream() {
    return fOutputStream;
  }

  /**
   * エージェントのシステムパラメータを返します．
   * @return システムパラメータ
   */
  public String[] getParameters() {
    return fSystemParameters;
  }

  /**
   * CProtocolオブジェクトを設定します．
   * @param umcp CProtocolオブジェクト
   */
  public void setCProtocol(UProtocolCore umcp) {
    fUmcp = umcp;
  }

  /**
   * 注文依頼，キャンセルなどの動作を行います．
   * @param date 日
   * @param session 節数
   * @param serverState サーバー状態
   * @param maxDate 運用日数
   * @param noOfSessionsPerDay 一日当たりの節数
   */
  public void doActions(int date, int session, int serverState,
                          int maxDate, int noOfSessionsPerDay) {
  }

  /**
   * プロトコル・ライブラリを返します．
   * @return プロトコル・ライブラリ．
   */
  public UProtocolCore getUmcp() {
    return fUmcp;
  }

  /**
   * パスワードを返します．
   * @return パスワード
   */
  public String getPasswd() {
    return fPasswd;
  }

  /**
   * パスワードを設定します．
   * @param passwd パスワード
   */
  public void setPasswd(String passwd) {
    fPasswd = passwd;
  }

  /**
     ユーザIDを返します．
     @return ユーザID．
   */
  public int getUserID() {
    return fUserID;
  }

  /**
   * ユーザーIDを設定します．
   * @param userID ユーザーID
   */
  public void setUserID(int userID) {
    fUserID = userID;
  }

  /**
   * 乱数の種を返します．
   * @return 乱数の種
   */
  public int getSeed() {
    return fSeed;
  }

  /**
   * 乱数の種を設定します．
   * @param seed 乱数の種
   */
  public void setSeed(int seed) {
    fSeed = seed;
    fRandom = new Random(seed);
  }

  /**
   * ログイン名を返します．
   * @return ログイン名
   */
  public String getLoginName() {
    return fLoginName;
  }

  /**
   * 実際の名前（Humanの場合：氏名，Machineの場合：クラス名）を返します．
   * @return 実際の名前
   */
  public String getRealName() {
    return fRealName;
  }

  /**
   * ログイン名を設定します．
   * @param string ログイン名
   */
  public void setLoginName(String string) {
    fLoginName = string;
  }

  /**
   * 実際の名前（Humanの場合：氏名，Machineの場合：クラス名）を設定します．
   * @param string 実際に名前
   */
  public void setRealName(String string) {
    fRealName = string;
  }
  
  /**
   * 乱数オブジェクトを返します．
   * @return 乱数オブジェクト
   */
  public Random getRandom() {
  	return fRandom;
  }

}
