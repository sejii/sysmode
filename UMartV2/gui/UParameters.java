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
package gui;

import java.io.*;
import java.util.*;

import java.awt.*;

import serverCore.*;

/**
 * UMart GUI のパラメータ管理クラス. 
 */
public class UParameters {
  static private UParameters fParameters = new UParameters();

  //Singlton 一度だけ初期化
  static public final String DEFAULT_BRAND = UMart.DEFAULT_SPOT_PRICE_FILE;

  static public final int DEFAULT_START_POINT = 1;

  static public final long DEFAULT_SEED = 0; //TODO seed をどうするか？

  static public final boolean DEFAULT_IS_SEED = true;

  static public final boolean DEFAULT_IS_LOG_CREATE = false;

  // GUI が自動的にスタートするか否か．
  static public final boolean DEFAULT_IS_AUTO_RUN = true;

  static public final int DEFAULT_DAYS = UMart.DEFAULT_MAX_DATE;

  static public final int DEFAULT_BOARD_PER_DAY = UMart.
      DEFAULT_NO_OF_SESSIONS_PER_DAY;

  static public final long DEFAULT_INIT_CASH = UMart.DEFAULT_INITIAL_CASH;

  static public final long DEFAULT_FEE_PER_UNIT = UMart.DEFAULT_FEE_PER_UNIT;

  static public final long DEFAULT_MARGIN_RATE = UMart.DEFAULT_MARGIN_RATE;

  static public final long DEFAULT_MAX_LOAN = UMart.DEFAULT_MAX_LOAN;

  static public final double DEFAULT_INTEREST = UMart.DEFAULT_INTEREST;

  static public final long DEFAULT_TRADING_UNIT = UMart.DEFAULT_TRADING_UNIT;

  static public final int DEFAULT_NUM_OF_EACH_AGENTS = 1;

  static public final int CONNECTION_TYPE_STAND_ALONE = 0;

  static public final int CONNECTION_TYPE_NETWORK = 1;

  static public final int GUI_TYPE_STAND_ALONE = 0;

  static public final int GUI_TYPE_NETWORK = 1;

  static public final String TIME_SERIES_DEFINITIONS =
      "/resources/csv/TimeSeriesDefinitions.csv";

  static public final String PRICE_FILE = "/resources/csv/j30.csv";

  static public final String MEMBERS_SA = "/resources/csv/MembersSA.csv";

  static public final String MEMBERS_NETWORK = "/resources/csv/MembersNet.csv";

  static public final String INSTITUTION = "/resources/csv/Institution.csv";

  static public final String ENGLISH_TUTORIAL_FILE =
      "/resources/tutorial/en/index.html";

  static public final String JAPANESE_TUTORIAL_FILE =
      "/resources/tutorial/jp/index.html";

  static public final String DOWNLOAD_SITE =
      "http://www.u-mart.org/html/u-mart-kit/index-e.html";

  static public final String SMTP_SERVER = "smtp.mail.yahoo.co.jp";

  static public final String MAIL_FROM =
      "u-mart-serv@u-mart.econ.kyoto-u.ac.jp";

  static public final String MAIL_TO1 = "isao@dis.titech.ac.jp";

  static public final String MAIL_TO2 = "mori@cs.osakafu-u.ac.jp";

  static public final String MAIL_TO3 = "hsato@nda.ac.jp";

  static public ResourceBundle fRb = ResourceBundle.getBundle(
      "resources.UGUIResources", Locale.getDefault());

  static UByteArrayOutputStream fByteArrayOutputStream = new
      UByteArrayOutputStream();

  static PrintStream fPrintStream = new PrintStream(fByteArrayOutputStream,
      true);

  protected String fPriceFile = PRICE_FILE;

  protected String fBrand = DEFAULT_BRAND;

  protected int fStartPoint = DEFAULT_START_POINT;

  protected long fSeed = DEFAULT_SEED;

  protected boolean fIsSeed = DEFAULT_IS_SEED;

  protected int fDays = DEFAULT_DAYS;

  protected int fBoardPerDay = DEFAULT_BOARD_PER_DAY;

  protected int fNumOfAgents = 0;

  protected ArrayList fTabs = new ArrayList();

  protected boolean fIsLogCreate = DEFAULT_IS_LOG_CREATE;

  protected boolean fIsAutoRun = DEFAULT_IS_AUTO_RUN;

  protected long fInitCash = DEFAULT_INIT_CASH;

  protected long fFeePerUnit = DEFAULT_FEE_PER_UNIT;

  protected long fMarginRate = DEFAULT_MARGIN_RATE;

  protected long fMaxLoan = DEFAULT_MAX_LOAN;

  protected double fInterest = DEFAULT_INTEREST;

  protected long fTradingUnit = DEFAULT_TRADING_UNIT;

  protected int fConnectionType = CONNECTION_TYPE_STAND_ALONE;

  protected UMemberList fMemberLog = new UMemberList();

  protected UTimeSeriesDefinitions fTimeSeriesDefinitionLog = new
      UTimeSeriesDefinitions();

  protected UPriceInfoDB fPriceInfoDB = new UPriceInfoDB();

  protected UInstitutionSetting fInsitutionLog = new UInstitutionSetting();

  protected ArrayList fMachineAgentArray = new ArrayList();

  /**
   * JOptionDialog の親用
   */
  protected Component fMainComponet;

  public ArrayList getMachineAgentArray() {
    return fMachineAgentArray;
  }

  public UPriceInfoDB getPriceInfoDB() {
    return fPriceInfoDB;
  }

  public void setPriceFile(String s) {
    fPriceFile = s;
  }

  public String getPriceFile() {
    return fPriceFile;
  }

  public void setBrand(String s) {
    fBrand = s;
  }

  public String getBrand() {
    return fBrand;
  }

  public void setStartPoint(int i) {
    fStartPoint = i;
  }

  public int getStartPoint() {
    return fStartPoint;
  }

  public void setSeed(long i) {
    fSeed = i;
  }

  public long getSeed() {
    return fSeed;
  }

  public void setIsSeed(boolean b) {
    fIsSeed = b;
  }

  public boolean IsSeed() {
    return fIsSeed;
  }

  public void setDays(int i) {
    fDays = i;
  }

  public int getDays() {
    return fDays;
  }

  public void setBoardPerDay(int i) {
    fBoardPerDay = i;
  }

  public int getBoardPerDay() {
    return fBoardPerDay;
  }

  public void setNumOfAgents(int i) {
    fNumOfAgents = i;
  }

  public int getNumOfAgents() {
    return fNumOfAgents;
  }

  public ArrayList getTabs() {
    return fTabs;
  }

  public void setLogCreate(boolean b) {
    fIsLogCreate = b;
  }

  public boolean isLogCreate() {
    return fIsLogCreate;
  }

  public void setAutoRun(boolean b) {
    fIsAutoRun = b;
  }

  public boolean isAutoRun() {
    return fIsAutoRun;
  }

  public void setInitCash(long l) {
    fInitCash = l;
  }

  public long getInitCash() {
    return fInitCash;
  }

  public void setFeePerUnit(long l) {
    fFeePerUnit = l;
  }

  public long getFeePerUnit() {
    return fFeePerUnit;
  }

  public void setMarginRate(long l) {
    fMarginRate = l;
  }

  public long getMarginRate() {
    return fMarginRate;
  }

  public void setMaxLoan(long l) {
    fMaxLoan = l;
  }

  public long getMaxLoan() {
    return fMaxLoan;
  }

  public void setTradingUnit(long l) {
    fTradingUnit = l;
  }

  public long getTradingUnit() {
    return fTradingUnit;
  }

  public void setInterest(double d) {
    fInterest = d;
  }

  public int getConnectionType() {
    return fConnectionType;
  }

  public void setConnectionType(int type) {
    fConnectionType = type;
  }

  public double getInterest() {
    return fInterest;
  }

  public void setAgentDefinitionsLogAndMemberLog(UMemberList mlog) {
    fMemberLog = mlog;
    int agentNum = 0;
    Iterator iter = mlog.getMembers();
    while (iter.hasNext()) {
      HashMap hm = (HashMap) iter.next();
      agentNum++;
    }
    setNumOfAgents(agentNum);
  }

  public UMemberList getMemberLog() {
    return fMemberLog;
  }

  public UTimeSeriesDefinitions getTimeSeriesDefinitionLog() {
    return fTimeSeriesDefinitionLog;
  }

  public UInstitutionSetting getInstitutionLog() {
    return fInsitutionLog;
  }

  //Singlton コンストラクタを private に
  private UParameters() {
    for (int i = 0; i < UPanelFactory.PANEL_TYPES.length; i++) {
      fTabs.add(UPanelFactory.PANEL_TYPES[i]);
    }
  }

  public static UParameters getInstance() { // これで唯一のパラメータを持ってくる．
    return fParameters;
  }

  public void printAll() {
    System.out.println("--All Parameters--");
    System.out.println("Brand: " + fBrand);
    System.out.println("Start Point: " + fStartPoint);
    System.out.println("Seed: " + fSeed);
    System.out.println("IsSeed: " + fIsSeed);
    System.out.println("Day: " + fDays);
    System.out.println("BoardPerDay: " + fBoardPerDay);
    System.out.println("Init Cash: " + fInitCash);
    System.out.println("Fee:" + fFeePerUnit);
    System.out.println("Margin Rate:" + fMarginRate);
    System.out.println("Max Loan:" + fMaxLoan);
    System.out.println("Trading Unit:" + fTradingUnit);
    System.out.println("Interest:" + fInterest);
    System.out.println("Num of Agents:" + fNumOfAgents);
    System.out.println("Tab Setting:" + fTabs);
    System.out.println("Member Log:");
    Iterator it = fMemberLog.getMembers();
    while (it.hasNext()) {
      System.out.println( (HashMap) it.next());
    }
  }

  /**
   * @return Returns the mainComponet.
   */
  public Component getMainComponet() {
    return fMainComponet;
  }

  /**
   * @param mainComponet The mainComponet to set.
   */
  public void setMainComponet(Component mainComponet) {
    fMainComponet = mainComponet;
  }
}
