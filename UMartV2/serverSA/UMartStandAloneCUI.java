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
package serverSA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
 * 大量加速実験用CUIクラスです．スタンドアロンで動作します．
 */
public class UMartStandAloneCUI {

  /** 実験に必要なファイルが置いてあるディレクトリ名 */
  private static final String RESOURCE_DIRECTORY = "resources";

  /** エージェント設定ファイル名 */
  private static final String MEMBERS_FILE = "Members.csv";

  /** 価格系列の設定ファイル名 */
  private static final String TIME_SERIES_DEFINITIONS_FILE = "TimeSeriesDefinitions.csv";

  /** 実験用のワーキングディレクトリ */
  private String fBaseDirectory = "";

  /** 取引所 */
  private UMartStandAlone fUMart;

  /**
   * コンストラクタです．
   * @param baseDir ログを作成するディレクトリ
   * @param isSimpleLog 簡易ログにするか？
   * @param randomSeed 乱数の種
   * @throws ParseException
   * @throws IOException
   */
  public UMartStandAloneCUI(String baseDir, boolean isSimpleLog, int randomSeed) throws ParseException, IOException {
    fBaseDirectory = baseDir;
    String resDir = fBaseDirectory + File.separator + UMartStandAloneCUI.RESOURCE_DIRECTORY;
    UTimeSeriesDefinitions timeSeriesDefinitions = new UTimeSeriesDefinitions();
    readTimeSeriesDefinitions(timeSeriesDefinitions,
                              resDir + File.separator
                              + UMartStandAloneCUI.TIME_SERIES_DEFINITIONS_FILE);
    HashMap info = (HashMap)timeSeriesDefinitions.getTimeSeriesDefinitions().next();
    String pricesFile = extractPricesFileName(info);
    int startPoint = ((Integer)info.get(UTimeSeriesDefinitions.INT_START_STEP)).intValue();
    int noOfDays = ((Integer)info.get(UTimeSeriesDefinitions.INT_MAX_DATE)).intValue();
    int sessionsPerDay = ((Integer)info.get(UTimeSeriesDefinitions.INT_NO_OF_SESSIONS_PER_DAY)).intValue();
    UPriceInfoDB priceInfoDB = new UPriceInfoDB();
    readPriceInfoDB(priceInfoDB, resDir + File.separator + pricesFile);
    UMemberList memberInfo = new UMemberList();
    readMemberLog(memberInfo, resDir + File.separator + UMartStandAloneCUI.MEMBERS_FILE);
    fUMart = new UMartStandAlone(memberInfo, priceInfoDB,
                                 startPoint, randomSeed, noOfDays, sessionsPerDay);
    initAgents(fUMart, memberInfo);
    fUMart.initLog(fBaseDirectory, isSimpleLog);
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
   */
  public void doLoop() {
    while (true) {
      UServerStatus status = fUMart.nextStatus();
      if (status.getState() == UServerStatus.ACCEPT_ORDERS) {
        fUMart.recieveOrdersFromLocalAgents();
      } else if (status.getState() == UServerStatus.AFTER_SETTLEMENT) {
        break;
      }
    }
  }

  /**
     UMartのCUI用のメインメソッドです．
     @param args コマンドライン引数
   */
  public static void main(String args[]) {
    if (args.length != 3) {
      System.err.println("usage: java serverSA.UStandAloneCUI simple/detail experimentDirectoryName randomSeed");
      System.exit(1);
    }
    String logFlagString = args[0];
    boolean isSimpleLog = false;
    if (logFlagString.equals("simple")) {
      isSimpleLog = true;
    } else if (logFlagString.equals("detail")) {
      isSimpleLog = false;
    } else {
      System.err.println("usage: java serverSA.UStandAloneCUI simple/detail experimentDirectoryName randomSeed");
      System.exit(1);
    }
    String baseDir = args[1];
    int randomSeed = Integer.parseInt(args[2]);
    try {
      UMartStandAloneCUI cui = new UMartStandAloneCUI(baseDir, isSimpleLog, randomSeed);
      cui.doLoop();
      System.out.println("Finished " + baseDir + "!!");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }

}
