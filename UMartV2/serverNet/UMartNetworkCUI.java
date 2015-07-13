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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import serverCore.UMart;
import serverCore.UMemberList;
import serverCore.UPriceInfoDB;
import serverCore.UServerStatus;
import serverCore.UAgentFactory;
import serverCore.UTimeSeriesDefinitions;
import strategyCore.UBaseAgent;

/**
 * マーケットサーバCUIクラスです．ネットワーク環境で動作します．
 */
public class UMartNetworkCUI {

  /** 実験に必要なファイルが置いてあるディレクトリ名 */
  private static final String RESOURCE_DIRECTORY = "resources" + File.separator + "csv" + File.separator;

  /** エージェント設定ファイル名 */
  private static final String MEMBERS_FILE = "MembersNet.csv";

  /** 価格系列の設定ファイル名 */
  private static final String TIME_SERIES_DEFINITIONS_FILE = "TimeSeriesDefinitions.csv";

  /** 取引所 */
  private UMartNetwork fUMart;
  
  /** 注文受付時間間隔 */
  private long fIntervalTime;

  /**
   * コンストラクタです．
   * @param randomSeed 乱数の種
   * @param port ポート番号
   * @param interval 板寄せ間隔(秒)
   * @throws ParseException
   * @throws IOException
   */
  public UMartNetworkCUI(int randomSeed, int port, long interval) throws ParseException, IOException {
    UTimeSeriesDefinitions timeSeriesDefinitions = new UTimeSeriesDefinitions();
    readTimeSeriesDefinitions(timeSeriesDefinitions,
                              RESOURCE_DIRECTORY + UMartNetworkCUI.TIME_SERIES_DEFINITIONS_FILE);
    HashMap info = (HashMap)timeSeriesDefinitions.getTimeSeriesDefinitions().next();
    String pricesFile = extractPricesFileName(info);
    int startPoint = ((Integer)info.get(UTimeSeriesDefinitions.INT_START_STEP)).intValue();
    int noOfDays = ((Integer)info.get(UTimeSeriesDefinitions.INT_MAX_DATE)).intValue();
    int sessionsPerDay = ((Integer)info.get(UTimeSeriesDefinitions.INT_NO_OF_SESSIONS_PER_DAY)).intValue();
    UPriceInfoDB priceInfoDB = new UPriceInfoDB();
    readPriceInfoDB(priceInfoDB, RESOURCE_DIRECTORY + pricesFile);
    UMemberList memberInfo = new UMemberList();
    readMemberLog(memberInfo, RESOURCE_DIRECTORY + UMartNetworkCUI.MEMBERS_FILE);
    fUMart = new UMartNetwork(memberInfo, priceInfoDB, startPoint, randomSeed, noOfDays, sessionsPerDay, port);
    fIntervalTime = interval;
    initAgents(fUMart, memberInfo);
    fUMart.initLog();
    fUMart.startLoginManager();
  }

  /**
   * 会員リスト情報に従い，エージェントを生成し，取引所に登録します．
   * @param umart 取引所
   * @param members 会員リスト
   */
  private void initAgents(UMart umart, UMemberList members) {
    Iterator iter = members.getMembers();
    while (iter.hasNext()) {
      HashMap memberInfo = (HashMap) iter.next();
      String loginName = (String) memberInfo.get(UMemberList.STRING_LOGIN_NAME);
      String passwd = (String) memberInfo.get(UMemberList.STRING_PASSWORD);
      String attribute = (String) memberInfo.get(UMemberList.STRING_ATTRIBUTE);
      String connection = (String) memberInfo.get(UMemberList.STRING_CONNECTION);
      String realName = (String) memberInfo.get(UMemberList.STRING_REAL_NAME);
      String paramString = UMemberList.arrayListToString((ArrayList)memberInfo.get(UMemberList.ARRAY_LIST_SYSTEM_PARAMETERS));
      int seed = ( (Integer) memberInfo.get(UMemberList.INT_SEED)).intValue();
      if (attribute.equals("Machine") && connection.equals("Local")) {
        try {
          UBaseAgent strategy = UAgentFactory.makeAgent(loginName, passwd, realName, paramString, seed, null);
          umart.appendStrategy(strategy);
        } catch (IllegalArgumentException iae) {
          System.err.println("Cannot initialize " + loginName);
          System.exit(5);
        }
      }
    }
  }

  /**
   * 価格系列ファイル名を抽出します．
   * @param info 価格系列の定義
   * @return 価格系列ファイル名
   */
  private String extractPricesFileName(HashMap info) {
    StringTokenizer st = new StringTokenizer((String)info.get(UTimeSeriesDefinitions.STRING_FILENAME), ":");
    st.nextToken();
    return st.nextToken();
  }

  /**
   * 価格系列定義をファイルから読み込みます．
   * @param def 価格系列定義を管理するオブジェクト
   * @param filename ファイル名
   * @throws ParseException
   * @throws IOException
   */
  private void readTimeSeriesDefinitions(UTimeSeriesDefinitions def, String filename) 
                                                     throws ParseException, IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    def.readFrom(br);
    br.close();
  }

  /**
   * 価格系列をファイルから読み込みます．
   * @param db 価格系列データベース
   * @param filename ファイル名
   * @throws ParseException
   * @throws IOException
   */
  private void readPriceInfoDB(UPriceInfoDB db, String filename) throws ParseException, IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    db.readFrom(br);
    br.close();
  }

  /**
   * 会員リストをファイルから読み込みます．
   * @param memberInfo 会員リスト
   * @param filename ファイル名
   * @throws ParseException
   * @throws IOException
   */
  private void readMemberLog(UMemberList memberInfo, String filename) throws ParseException, IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    memberInfo.readFrom(br);
    br.close();
  }

  /**
   * メインループです．
   * @throws InterruptedException 
   *
   */
  public void doLoop() throws InterruptedException {
    UServerStatus status = fUMart.nextStatus();
    status.printOn();
  	System.out.println("After all users login, enter 'start'.");
  	waitStartCommand();
    while (true) {
      status = fUMart.nextStatus();
      status.printOn();
      if (status.getState() == UServerStatus.ACCEPT_ORDERS) {
        fUMart.recieveOrdersFromLocalAgents();
        Thread.sleep(fIntervalTime * 1000);
      } else if (status.getState() == UServerStatus.AFTER_MARKING_TO_MARKET) {
        Thread.sleep(fIntervalTime * 1000);      	
      } else if (status.getState() == UServerStatus.AFTER_SETTLEMENT) {
        break;
      }
    }
    System.out.println("Finished!!");
    waitKillCommand();
  }
  
  /**
   * startコマンドが入力されるまで待機します．
   */
  private void waitStartCommand() {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      while (true) {
        System.out.print("umart> ");
        String line = in.readLine();
        StringTokenizer st = new StringTokenizer(line);
        if (!st.hasMoreTokens()) {
          continue;
        }
        String cmdStr = st.nextToken();
        if (cmdStr.equalsIgnoreCase("start")) {
          return;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }  	
  }
  
  /**
   * quitコマンドが入力されるまで待機します．
   */
  private void waitKillCommand() {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      while (true) {
        System.out.print("umart> ");
        String line = in.readLine();
        StringTokenizer st = new StringTokenizer(line);
        if (!st.hasMoreTokens()) {
          continue;
        }
        String cmdStr = st.nextToken();
        if (cmdStr.equalsIgnoreCase("quit")) {
          System.exit(5);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }

  /**
   * UMartのCUI用のメインメソッドです．
   * @param args コマンドライン引数
   */
  public static void main(String args[]) {
    if (args.length != 2 && args.length != 3) {
      System.err.println("usage: java serverNet.UMartNetworkCUI randomSeed interval [port]");
      System.exit(1);
    }
    int randomSeed = Integer.parseInt(args[0]);
    long interval = Long.parseLong(args[1]);
    int port = 5010;
    if (args.length == 3) {
    	port = Integer.parseInt(args[2]);
    }
    try {
      UMartNetworkCUI cui = new UMartNetworkCUI(randomSeed, port, interval);
      cui.doLoop();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }

}
