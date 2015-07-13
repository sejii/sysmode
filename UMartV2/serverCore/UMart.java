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
import java.net.*;
import java.text.*;
import java.util.*;


import cmdClientSA.*;
import cmdCore.*;
import log.*;
import strategy.*;
import strategyCore.UBaseAgent;

/**
 * 取引所全体の管理を行うクラスです．
 * ネットワーク環境およびスタンドアロン環境に共通の属性およびメソッドが定義されています．
 * ネットワーク環境で利用するUMartNetworkクラス，および，スタンドアロン環境で利用するUMartStandAloneクラスの親クラスとなっています．
 */
public abstract class UMart {
	
	/** 直近の価格に対する最大の変動比率 */
	public static double MAX_RATE = 0.2;

  /** スーパーユーザーのID */
  public static final int SU_ID = 0;

  /** サーバー運用日数のデフォルト値 */
  public static final int DEFAULT_MAX_DATE = 30;

  /** 一日あたりの節数のデフォルト値 */
  public static final int DEFAULT_NO_OF_SESSIONS_PER_DAY = 8;

  /** 単位取り引きあたりの手数料のデフォルト値 */
  public static final long DEFAULT_FEE_PER_UNIT = 0;

  /** 単位取り引きあたりの証拠金のデフォルト値 */
  public static final long DEFAULT_MARGIN_RATE = 300000;

  /** 借入れ限度額のデフォルト値 */
  public static final long DEFAULT_MAX_LOAN = 30000000;

  /** 借入れ金利のデフォルト値 */
  public static final double DEFAULT_INTEREST = 0.1;

  /** 取引単位のデフォルト値 */
  public static final long DEFAULT_TRADING_UNIT = 1000;

  /** 初期資産のデフォルト値 */
  public static final long DEFAULT_INITIAL_CASH = 1000000000;

  /** 現物価格系列ファイル名のデフォルト値 */
  public static final String DEFAULT_SPOT_PRICE_FILE = "j30";

  /** 現物価格データの更新頻度(板寄せ何回分)のデフォルト値 */
  public static final int DEFAULT_SPOT_INTERVAL = 1;

  /** 注文依頼・取消コマンドのログを保存するディレクトリ名 */
  public final static String ORDER_LOG_DIR = "order";

  /** 約定情報のログを保存するディレクトリ名 */
  public final static String EXECUTION_LOG_DIR = "execution";

  /** 板情報のログを保存するディレクトリ名 */
  public final static String BOARD_LOG_DIR = "order_book";

  /** 口座情報のログを保存するディレクトリ名 */
  public final static String ACCOUNT_LOG_DIR = "account";

  /** クライアントのログイン管理 */
  protected ULoginManager fLoginManager;

  /** 注文管理 */
  protected UOrderManager fOrderManager;

  /** サーバー状態 */
  protected UServerStatus fStatus;

  /** サーバー状態用Read-Write Lock */
  protected UReadWriteLock fStateLock;

  /** 価格系列データベース */
  protected UPriceInfoDB fPriceInfoDB;

  /** 価格系列データベースにおける開始ポイント */
  protected int fStartPoint;

  /** サーバー運用日数 */
  protected int fMaxDate = UMart.DEFAULT_MAX_DATE;

  /** 1日あたりの板寄せ回数 */
  protected int fNoOfSessionsPerDay = UMart.DEFAULT_NO_OF_SESSIONS_PER_DAY;

  /** 口座管理 */
  protected UAccountManager fAccountManager;

  /** 板 */
  protected UBoard fBoard;

  /** 価格推移データベース */
  protected UQuoteDB fQuoteDB;

  /** 板情報 */
  protected UBoardInformation fBoardInformation;

  /** エージェント(UBaseStrategy)の配列 */
  protected ArrayList fStrategyArray;

  /** 1節分の注文依頼・取消の履歴 */
  protected UOrderCommandLog fOrderCommandLog;

  /** 会員情報 */
  protected UMemberList fMemberLog;

  /** ログファイルを生成するディレクトリ名 */
  protected String fLogDir;

  /** ログを生成しない */
  public static final int NO_LOG = 0;

  /** 詳細ログを生成する */
  public static final int DETAILED_LOG = 1;

  /** 簡易ログ（口座履歴，板情報の履歴，約定履歴，注文履歴を省略）を生成する */
  public static final int SIMPLE_LOG = 2;

  /** ログの生成方法(NO_LOG or DETAILED_LOG or SIMPLE_LOG)の指定 */
  protected int fLogFlag;

  /** コマンド実行可能チェック用オブジェクト */
  protected UCmdExecutableChecker fCmdExecutableChecker;

  /**
   * UMartオブジェクトの生成および初期化を行います：<br>
   * - エージェントは誰もログインしていない状態<br>
   * - 日付は1，板寄せ回数は1<br>
   * - 状態はBEFORE_TRADING<br>
   * @param members 会員リスト
   * @param priceInfoDB 価格系列データベース
   * @param startPoint 価格系列の何番目のデータを初日のデータとするか
   * @param seed 乱数の種
   * @param maxDate サーバー運用日数
   * @param noOfSessionsPerDay 一日あたりの節数
   */
  public UMart(UMemberList members, UPriceInfoDB priceInfoDB, int startPoint,
               long seed, int maxDate, int noOfSessionsPerDay) {
    fPriceInfoDB = priceInfoDB;
    fStartPoint = startPoint + Strategy.NO_OF_SPOT_PRICES - 1;
    URandom.setSeed(seed);
    fMaxDate = maxDate;
    fNoOfSessionsPerDay = noOfSessionsPerDay;
    // fInterest = interest / 365.0;
    fAccountManager = new UAccountManager(members, fNoOfSessionsPerDay);
    fBoard = new UBoard();
    fQuoteDB = new UQuoteDB();
    fBoardInformation = new UBoardInformation();
    fOrderManager = new UOrderManager();
    setupOrderManager();
    fPriceInfoDB.initializePtr(fStartPoint, fMaxDate, fNoOfSessionsPerDay);
    fStatus = new UServerStatus();
    fStateLock = new UReadWriteLock();
    fStrategyArray = new ArrayList();
    fOrderCommandLog = new UOrderCommandLog();
    fStatus.setState(UServerStatus.BEFORE_TRADING);
    setupLoginManager();
    fMemberLog = members;
    fLogFlag = UMart.NO_LOG;
    try {
      fCmdExecutableChecker = new UCmdExecutableChecker();
      URL dataURL = getClass().getResource("/cmdCore/SVMP.csv");
      BufferedReader br = new BufferedReader(new InputStreamReader(dataURL.openStream()));
      fCmdExecutableChecker.readFrom(br);
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }

  /**
   * ローカルホストで動作するエージェントを登録します．
   * @param agent UBaseStrategyを継承したエージェント
   */
  public boolean appendStrategy(UBaseAgent agent) {
    String name = agent.getLoginName();
    String passwd = agent.getPasswd();
    ULoginStatus loginStatus = doLogin(name, passwd);
    if (loginStatus == null) {
      System.err.println("Can't find the office with (" + name + "," + passwd +
                         ")");
      return false;
    }
    if (loginStatus.getNoOfLoginAgents() > 0) {
      System.err.println("The agent has already logined!!");
      return false;
    }
    int id = loginStatus.getUserID();
    UProtocolForLocalClient umcp = new UProtocolForLocalClient();
    umcp.setConnection(this, id);
    agent.setCProtocol(umcp);
    agent.setUserID(id);
    fStrategyArray.add(agent);
    loginStatus.incrementNoOfLoginAgents();
    return true;
  }

  /**
   * 取引所の口座情報をハッシュマップをに格納します．
   * @param hash 各情報を格納するためのハッシュマップ
   * @param ea 取引所の口座
   */
  private void setExchangeBalanceToHashMap(HashMap hash, UExchangeAccount ea) {
    hash.put(UCAllBalancesCore.STRING_NAME, "su");
    hash.put(UCAllBalancesCore.LONG_CASH, new Long(ea.getCash()));
    hash.put(UCAllBalancesCore.LONG_MARGIN, "0");
    hash.put(UCAllBalancesCore.LONG_UNREALIZED_PROFIT, "0");
    hash.put(UCAllBalancesCore.LONG_SETTLED_PROFIT, "0");
    hash.put(UCAllBalancesCore.LONG_FEE, "0");
    hash.put(UCAllBalancesCore.LONG_INTEREST, "0");
    hash.put(UCAllBalancesCore.LONG_LOAN, "0");
    hash.put(UCAllBalancesCore.LONG_SURPLUS, "0");
    hash.put(UCAllBalancesCore.LONG_SELL_POSITION, new Long(ea.getSellPosition()));
    hash.put(UCAllBalancesCore.LONG_BUY_POSITION, new Long(ea.getBuyPosition()));
  }

  /**
   * UBalanceの情報をハッシュマップに格納します．
   * @param hash 各情報を格納するためのハッシュマップ
   * @param balance 収支情報
   */
  private void setBalanceToHashMap(HashMap hash, UBalance balance) {
    balance.updateCash();
    hash.put(UCBalancesCore.LONG_CASH,
             new Long(balance.getCash() + balance.getMargin()));
    hash.put(UCBalancesCore.LONG_MARGIN, new Long(balance.getMargin()));
    hash.put(UCBalancesCore.LONG_UNREALIZED_PROFIT,
             new Long(balance.getUnrealizedProfit()));
    hash.put(UCBalancesCore.LONG_SETTLED_PROFIT, new Long(balance.getProfit()));
    hash.put(UCBalancesCore.LONG_FEE, new Long(balance.getSumOfFee()));
    hash.put(UCBalancesCore.LONG_INTEREST, new Long(balance.getSumOfInterest()));
    hash.put(UCBalancesCore.LONG_LOAN, new Long(balance.getLoan()));
    hash.put(UCBalancesCore.LONG_SURPLUS, new Long(balance.getCash()));
  }

  /**
   * Balancesコマンドへの応答を処理します．
   * 注文受付時間帯, 取引後時間帯, 決済後時間帯以外はエラーを返します．
   * @param todayHash 当日分の残高照会情報
   * @param yesterdayHash 前日分の残高照会情報
   * @param userID ユーザID
   */
  public UCommandStatus doBalances(HashMap todayHash,
                                   HashMap yesterdayHash, int userID) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCBalancesCore.CMD_NAME, fStatus,
                                              userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      UAccount account = fAccountManager.getAccount(userID);
      if (account == null) {
        System.err.println("Account not found in UMart.doBalances: UserID=" +
                           userID);
        throw new Exception();
        // System.exit( -5);
        // return ucs;
      }
      setBalanceToHashMap(todayHash, account.getTodayBalance());
      setBalanceToHashMap(yesterdayHash, account.getYesterdayBalance());
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Exception: " + e + " in UMart.doBalances");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * AllBalancesコマンドへの応答を処理します．
   * @param arrayList ハッシュマップを格納するためのリスト
   * @param userID ユーザID
   */
  public UCommandStatus doAllBalances(ArrayList arrayList, int userID) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCAllBalancesCore.CMD_NAME,
                                              fStatus, userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      UExchangeAccount ea = fAccountManager.getExchangeAccount();
      HashMap hash = new HashMap();
      setExchangeBalanceToHashMap(hash, ea);
      arrayList.add(hash);
      Enumeration accounts = fAccountManager.getAccounts();
      while (accounts.hasMoreElements()) {
        hash = new HashMap();
        UAccount account = (UAccount) accounts.nextElement();
        hash.put(UCAllBalancesCore.STRING_NAME, account.getUserName());
        setBalanceToHashMap(hash, account.getTodayBalance());
        UPosition pos = account.getPosition();
        long sell = pos.getSumOfSellPositionUntilYesterday();
        long buy = pos.getSumOfBuyPositionUntilYesterday();
        hash.put(UCAllBalancesCore.LONG_SELL_POSITION, new Long(sell));
        hash.put(UCAllBalancesCore.LONG_BUY_POSITION, new Long(buy));
        arrayList.add(hash);
      }
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doAllBalances");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * AllPositionsコマンドへの応答を処理します．
   * 注文受付時間帯, 取引後時間帯, 決済後時間帯以外はエラーを返します．
   * @param arrayList ハッシュマップを格納するためのリスト
   * @param userID ユーザID
   */
  public UCommandStatus doAllPositions(ArrayList arrayList, int userID) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCAllPositionsCore.CMD_NAME,
                                              fStatus, userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      {
        HashMap hashOfSu = new HashMap();
        UExchangeAccount ea = fAccountManager.getExchangeAccount();
        String name = ea.getUserName();
        hashOfSu.put(UCAllPositionsCore.STRING_NAME, name);
        long sell = ea.getSellPosition();
        hashOfSu.put(UCAllPositionsCore.LONG_SELL_POSITION, new Long(sell));
        long buy = ea.getBuyPosition();
        hashOfSu.put(UCAllPositionsCore.LONG_BUY_POSITION, new Long(buy));
        arrayList.add(hashOfSu);
      }
      Enumeration accounts = fAccountManager.getAccounts();
      while (accounts.hasMoreElements()) {
        UAccount account = (UAccount) accounts.nextElement();
        HashMap hash = new HashMap();
        hash.put(UCAllPositionsCore.STRING_NAME, account.getUserName());
        UPosition pos = account.getPosition();
        long sell = pos.getSumOfTodaySellPosition();
        long buy = pos.getSumOfTodayBuyPosition();
        hash.put(UCAllPositionsCore.LONG_SELL_POSITION, new Long(sell));
        hash.put(UCAllPositionsCore.LONG_BUY_POSITION, new Long(buy));
        arrayList.add(hash);
      }
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doAllPositions");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * 一般ユーザーの収支情報をhashに格納します．UMart.doMemberProfileから呼ばれます．
   * @param hash 一般ユーザーの収支情報を格納するためのHashMap
   * @param bal 一般ユーザーの収支情報
   */
  private void setBalanceToHashMapForMemberProfile(HashMap hash, UBalance bal) {
    long initialCash = bal.getInitialCash();
    hash.put(UCMemberProfileCore.LONG_INITIAL_CASH, new Long(initialCash)); // 初期所持金
    long loan = bal.getLoan();
    hash.put(UCMemberProfileCore.LONG_LOAN, new Long(loan)); // 借入金
    long unrealizedProfit = bal.getUnrealizedProfit();
    hash.put(UCMemberProfileCore.LONG_UNREALIZED_PROFIT,
             new Long(unrealizedProfit)); // 未実現損益
    long margin = bal.getMargin();
    hash.put(UCMemberProfileCore.LONG_MARGIN, new Long(margin)); // 預託証拠金
    long sumOfFee = bal.getSumOfFee();
    hash.put(UCMemberProfileCore.LONG_SUM_OF_FEE, new Long(sumOfFee)); // 総支払い手数料
    long sumOfInterest = bal.getSumOfInterest();
    hash.put(UCMemberProfileCore.LONG_SUM_OF_INTEREST, new Long(sumOfInterest)); // 総支払い金利
    long cash = bal.getCash();
    hash.put(UCMemberProfileCore.LONG_CASH, new Long(cash)); // 保有現金
    long profit = bal.getProfit();
    hash.put(UCMemberProfileCore.LONG_PROFIT, new Long(profit)); // 実現損益
  }

  /**
   * 一般ユーザーのポジション情報をhashに格納します．UMart.doMemberProfileから呼ばれます．
   * @param hash 一般ユーザーのポジション情報を格納するためのHashMap
   * @param pos 一般ユーザーのポジション情報
   */
  private void setPositionToHashMapForMemberProfile(HashMap hash, UPosition pos) {
    long sumOfSellPositionsUntilYesterday = pos.
        getSumOfSellPositionUntilYesterday();
    hash.put(UCMemberProfileCore.LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY,
             new Long(sumOfSellPositionsUntilYesterday)); // 前日までの売ポジションの合計
    long sumOfBuyPositionsUntilYesterday = pos.
        getSumOfBuyPositionUntilYesterday();
    hash.put(UCMemberProfileCore.LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY,
             new Long(sumOfBuyPositionsUntilYesterday)); // 前日までの買ポジションの合計
    long todaySellPositions = pos.getSumOfTodaySellPosition();
    hash.put(UCMemberProfileCore.LONG_TODAY_SELL_POSITIONS,
             new Long(todaySellPositions)); // 当日の売ポジションの合計
    long todayBuyPositions = pos.getSumOfTodayBuyPosition();
    hash.put(UCMemberProfileCore.LONG_TODAY_BUY_POSITIONS,
             new Long(todayBuyPositions)); // 当日の買ポジションの合計
  }

  /**
   * MemberProfileコマンドへの応答を処理します．
   * @param data targetUserIDで指定されたユーザーの情報を格納するためのHashMap
   * @param userID このコマンドを実行したユーザーのユーザーID
   * @param targetUserId 調べたいユーザーのユーザーID（-1の場合，userIDが代入される）
   * @return 実行状態
   */
  public UCommandStatus doMemberProfile(HashMap data, int userID,
                                        int targetUserId) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCMemberProfileCore.CMD_NAME,
                                              fStatus, userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      if (targetUserId < 0) {
        targetUserId = userID;
      }
      UAccount account = fAccountManager.getAccount(targetUserId);
      ULoginStatus office = fLoginManager.findLoginStatus(targetUserId);
      if (account == null || office == null) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.INVALID_ARGUMENTS);
        ucs.setErrorMessage("TARGET_USER_ID(=" + targetUserId +
                            ") IS OUT OF RANGE.");
        return ucs;
      }
      data.put(UCMemberProfileCore.STRING_LOGIN_NAME, account.getUserName()); // 会員名
      data.put(UCMemberProfileCore.STRING_PASSWORD, account.getPasswd()); // パスワード
      data.put(UCMemberProfileCore.STRING_ATTRIBUTE, account.getAttribute()); // エージェント属性(Human or Machine)
      data.put(UCMemberProfileCore.STRING_CONNECTION, account.getConnection()); // コネクション(Remote or Local)
      data.put(UCMemberProfileCore.ARRAY_LIST_ACCESS, account.getAccess()); // アクセス制限
      data.put(UCMemberProfileCore.STRING_REAL_NAME, account.getRealName()); // 実際の名前
      data.put(UCMemberProfileCore.ARRAY_LIST_SYSTEM_PARAMETERS,
               account.getSystemParameters()); // システムパラメータ
      data.put(UCMemberProfileCore.INT_SEED, new Integer(account.getSeed())); // 乱数の種
      data.put(UCMemberProfileCore.LONG_INITIAL_CASH,
               new Long(account.getInitialCash())); // 初期資産
      data.put(UCMemberProfileCore.LONG_TRADING_UNIT,
               new Long(account.getTradingUnit())); // 取引単位
      data.put(UCMemberProfileCore.LONG_FEE_PER_UNIT,
               new Long(account.getFeePerUnit())); // 単位取引あたりの手数料
      data.put(UCMemberProfileCore.LONG_MARGIN_RATE,
               new Long(account.getMarginRate())); // 証拠金率
      data.put(UCMemberProfileCore.LONG_MAX_LOAN, new Long(account.getMaxLoan())); // 借り入れ限度額
      data.put(UCMemberProfileCore.DOUBLE_INTEREST,
               new Double(account.getInterestPerYear())); // 借り入れ金利
      data.put(UCMemberProfileCore.INT_STATUS, new Integer(account.getStatus())); // 取引可能(+1),取引不可能(-1)
      data.put(UCMemberProfileCore.INT_NO_OF_LOGIN_AGENTS,
               new Integer(office.getNoOfLoginAgents())); // ログイン中のエージェント数
      HashMap yesterdayBalanceHash = new HashMap();
      UBalance yesterdayBalancel = account.getYesterdayBalance();
      setBalanceToHashMapForMemberProfile(yesterdayBalanceHash,
                                          yesterdayBalancel);
      data.put(UCMemberProfileCore.HASHMAP_YESTERDAY_BALANCE,
               yesterdayBalanceHash); // 前日収支
      HashMap todayBalanceHash = new HashMap();
      UBalance todayBalance = account.getTodayBalance();
      setBalanceToHashMapForMemberProfile(todayBalanceHash, todayBalance);
      data.put(UCMemberProfileCore.HASHMAP_TODAY_BALANCE, todayBalanceHash); // 当日収支
      HashMap positionHash = new HashMap();
      UPosition pos = account.getPosition();
      setPositionToHashMapForMemberProfile(positionHash, pos);
      data.put(UCMemberProfileCore.HASHMAP_POSITION, positionHash); // ポジション
      ucs.setStatus(true);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * ExchangeProfileコマンドを処理します．
   * @param data 取引所情報を格納するためのHashMap
   * @param userID このコマンドを実行したユーザーのユーザーID
   * @return 実行状態
   */
  public UCommandStatus doExchangeProfile(HashMap data, int userID) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCExchangeProfileCore.CMD_NAME,
                                              fStatus, userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      UExchangeAccount ea = fAccountManager.getExchangeAccount();
      long cash = ea.getCash();
      data.put(UCExchangeProfileCore.LONG_CASH, new Long(cash)); // 保有現金
      long sellPosition = ea.getSellPosition();
      data.put(UCExchangeProfileCore.LONG_SELL_POSITION, new Long(sellPosition)); // 取引所の管理下の売ポジション
      long buyPosition = ea.getBuyPosition();
      data.put(UCExchangeProfileCore.LONG_BUY_POSITION, new Long(buyPosition)); // 取引所の管理下の買ポジション
      int noOfMembers = fAccountManager.getNoOfAccounts();
      data.put(UCExchangeProfileCore.INT_NO_OF_MEMBERS, new Integer(noOfMembers)); // 会員数
      ucs.setStatus(true);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * BoardDataコマンド(過去の板寄せを行った直後の板を得るためのコマンド)への応答を処理します．
   * 注文受付時間帯, 板寄せ後時間帯，取引後時間帯以外はエラーを返します．
   * ただし, 1日目の１回目の注文期間はエラーを返します．
   * @param hash 各情報を格納するためのハッシュマップ
   * @param arrayList ハッシュマップを格納するためのリスト
   */
  public UCommandStatus doBoardData(HashMap hash, ArrayList arrayList) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCBoardDataCore.CMD_NAME, fStatus,
                                              UMart.SU_ID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      if (fStatus.getDate() == 1 && fStatus.getSession() == 1
          && fStatus.getState() == UServerStatus.ACCEPT_ORDERS) { // １日目の１回目の注文期間
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage("THE BOARD HAS NEVER UPDATED");
        return ucs;
      }
      ucs.setStatus(true);
      hash.put(UCBoardDataCore.LONG_TOTAL_BUY_VOLUME,
               new Long(fBoardInformation.getTotalBuyVolume()));
      hash.put(UCBoardDataCore.LONG_MIN_PRICE,
               new Long(fBoardInformation.getMinPrice()));
      hash.put(UCBoardDataCore.LONG_MAX_PRICE,
               new Long(fBoardInformation.getMaxPrice()));
      hash.put(UCBoardDataCore.LONG_CONTRACT_PRICE,
               new Long(fBoardInformation.getContractPrice()));
      hash.put(UCBoardDataCore.LONG_CONTRACT_VOLUME,
               new Long(fBoardInformation.getContractVolume()));
      Enumeration elements = fBoardInformation.getBoardInfoElements().elements();
      // 以下,成行の情報
      UBoardInfoElement info = (UBoardInfoElement) elements.nextElement();
      if (info.getSellVolume() > 0) {
        HashMap marketSell = new HashMap();
        marketSell.put(UCBoardDataCore.LONG_PRICE,
                       new Long(fBoardInformation.getMinPrice()));
        marketSell.put(UCBoardDataCore.STRING_SELL_BUY, "sell");
        marketSell.put(UCBoardDataCore.LONG_VOLUME, new Long(info.getSellVolume()));
        arrayList.add(marketSell);
      }
      long marketBuyVolume = info.getBuyVolume();
      // 以下, 指値の情報
      while (elements.hasMoreElements()) {
        info = (UBoardInfoElement) elements.nextElement();
        if (info.getSellVolume() > 0) {
          HashMap limitSell = new HashMap();
          limitSell.put(UCBoardDataCore.LONG_PRICE, new Long(info.getPrice()));
          limitSell.put(UCBoardDataCore.STRING_SELL_BUY, "sell");
          limitSell.put(UCBoardDataCore.LONG_VOLUME,
                        new Long(info.getSellVolume()));
          arrayList.add(limitSell);
        }
        if (info.getBuyVolume() > 0) {
          HashMap limitBuy = new HashMap();
          limitBuy.put(UCBoardDataCore.LONG_PRICE, new Long(info.getPrice()));
          limitBuy.put(UCBoardDataCore.STRING_SELL_BUY, "buy");
          limitBuy.put(UCBoardDataCore.LONG_VOLUME, new Long(info.getBuyVolume()));
          arrayList.add(limitBuy);
        }
      }
      if (marketBuyVolume > 0) {
        HashMap marketBuy = new HashMap();
        marketBuy.put(UCBoardDataCore.LONG_PRICE,
                      new Long(fBoardInformation.getMaxPrice()));
        marketBuy.put(UCBoardDataCore.STRING_SELL_BUY, "buy");
        marketBuy.put(UCBoardDataCore.LONG_VOLUME, new Long(marketBuyVolume));
        arrayList.add(marketBuy);
      }
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doBoardData");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * BoardInformationコマンド（最新の板情報を得るためのコマンド）への応答を処理します．
   * 注文受付時間帯, 板寄せ後時間帯，取引後時間帯以外はエラーを返します．
   * ただし, 1日目の１回目の注文期間はエラーを返します．
   * @param data 板の最終更新時刻，成行注文数量，各価格ごとの注文数量
   */
  public UCommandStatus doBoardInformation(HashMap data) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCBoardInformationCore.CMD_NAME,
                                              fStatus, SU_ID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      UBoardInformation boardInfo = new UBoardInformation();
      fBoard.setBoardInfo(boardInfo);
//			synchronized ( fBoard ) {
//				boardInfo.setLastUpdateTime(fBoard.getLastUpdateTime());
//				Iterator orders = fBoard.getOrderArray();
//				while (orders.hasNext()) {
//					UOrder o = (UOrder) orders.next();
//					boardInfo.addInformation( o.getSellBuy(), o.getMarketLimit(),
//				                            o.getPrice(), o.getVolume() );
//				}
//			}
      String lastUpdateTime = extractTime(boardInfo.getLastUpdateTime());
      data.put(UCBoardInformationCore.STRING_LAST_UPDATE_TIME, lastUpdateTime);
      ArrayList arrayList = new ArrayList();
      Enumeration elements = boardInfo.getBoardInfoElements().elements();
      while (elements.hasMoreElements()) {
        UBoardInfoElement info = (UBoardInfoElement) elements.nextElement();
        HashMap hash = new HashMap();
        hash.put(UCBoardInformationCore.LONG_PRICE, new Long(info.getPrice()));
        hash.put(UCBoardInformationCore.LONG_SELL_VOLUME,
                 new Long(info.getSellVolume()));
        hash.put(UCBoardInformationCore.LONG_BUY_VOLUME,
                 new Long(info.getBuyVolume()));
        arrayList.add(hash);
      }
      data.put(UCBoardInformationCore.ARRAYLIST_BOARD, arrayList);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * dateを"hh:mm:ss"形式に変換します．
   * @param date 実時間
   */
  private String extractTime(Date date) {
    return new SimpleDateFormat("HH:mm:ss").format(date);
  }

  /**
   * OrderCancelコマンドへの応答を処理します．
   * 注文受付時間帯以外はエラーを返す。
   * @param hash 各情報を格納するためのハッシュマップ
   * @param userID ユーザID
   * @param orderID 取り消したい注文のID
   */
  public UCommandStatus doOrderCancel(HashMap hash, int userID, long orderID) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCOrderCancelCore.CMD_NAME,
                                              fStatus, userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      synchronized (fOrderManager) {
        boolean flag = fBoard.removeOrder(userID, orderID);
        UOrder o = fOrderManager.cancelOrder(userID, orderID);
        if (o != null) {
          if (!flag) {
            System.err.println("Found inconsistent in UMartStandAlone.doOrderCancel");
            System.exit(5);
          }
          long uncontractVolume = o.getCancelVolume();
          long contractVolume = o.getContractVolume();
          String time = extractTime(new Date());
          ucs.setStatus(true);
          hash.put(UCOrderCancelCore.LONG_ORDER_ID, new Long(orderID));
          hash.put(UCOrderCancelCore.STRING_CANCEL_TIME, time);
          hash.put(UCOrderCancelCore.LONG_CANCEL_VOLUME,
                   Long.toString(uncontractVolume));
          hash.put(UCOrderCancelCore.LONG_UNCANCEL_VOLUME,
                   Long.toString(contractVolume));
          fOrderCommandLog.registerOrderCancel(fStatus.getDate(),
                                               fStatus.getSession(),
                                               o.getUserName(),
                                               orderID, uncontractVolume);
        } else {
          ucs.setStatus(false);
          ucs.setErrorCode(ICommand.INVALID_ARGUMENTS);
          ucs.setErrorMessage("THE ORDER WITH ID:" + orderID +
                              " CANNOT BE FOUND");
        }
      }
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doOrderCancel");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * CheckOrderコマンドへの応答を処理します．
   * @param orderID 注文ID
   * @param hash 各情報を格納するためのハッシュマップ
   * @return 処理した結果
   */
  public UCommandStatus doOrderCheck(long orderID, HashMap hash) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCOrderCheckCore.CMD_NAME,
                                              fStatus, SU_ID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      UOrder o = null;
      synchronized (fOrderManager) {
        o = fOrderManager.getOrderFromHistory(orderID);
      }
      if (o == null) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.INVALID_ARGUMENTS);
        ucs.setErrorMessage("orderID is invalid.");
        return ucs;
      }
      ucs.setStatus(true);
      synchronized (o) {
        hash.put(UCOrderCheckCore.INT_USER_ID, new Integer(o.getUserID()));
        hash.put(UCOrderCheckCore.STRING_BRAND_NAME, o.getBrandName());
        hash.put(UCOrderCheckCore.LONG_ORDER_ID, new Long(o.getOrderID()));
        hash.put(UCOrderCheckCore.STRING_ORDER_TIME, this.extractTime(o.getTime()));
        hash.put(UCOrderCheckCore.INT_ORDER_DATE, new Integer(o.getDate()));
        hash.put(UCOrderCheckCore.INT_ORDER_BOARD_NO, new Integer(o.getSession()));
        hash.put(UCOrderCheckCore.INT_SELL_BUY, new Integer(o.getSellBuy()));
        hash.put(UCOrderCheckCore.INT_MARKET_LIMIT,
                 new Integer(o.getMarketLimit()));
        hash.put(UCOrderCheckCore.INT_NEW_REPAY, new Integer(o.getNewRepay()));
        hash.put(UCOrderCheckCore.LONG_ORDER_PRICE, new Long(o.getPrice()));
        hash.put(UCOrderCheckCore.LONG_ORDER_VOLUME, new Long(o.getVolume()));
        hash.put(UCOrderCheckCore.LONG_SUM_OF_CONTRACT_VOLUME,
                 new Long(o.getContractVolume()));
        hash.put(UCOrderCheckCore.LONG_CANCEL_VOLUME,
                 new Long(o.getCancelVolume()));
        ArrayList contractInfoArray = new ArrayList();
        Enumeration enumeration = o.getContractInformationArray().elements();
        while (enumeration.hasMoreElements()) {
          UContractInformation info = (UContractInformation) enumeration.
              nextElement();
          HashMap infoHash = new HashMap();
          infoHash.put(UCOrderCheckCore.LONG_CONTRACT_ID,
                       new Long(info.getContractID()));
          infoHash.put(UCOrderCheckCore.STRING_CONTRACT_TIME,
                       this.extractTime(info.getTime()));
          infoHash.put(UCOrderCheckCore.LONG_CONTRACT_PRICE,
                       new Long(info.getPrice()));
          infoHash.put(UCOrderCheckCore.LONG_CONTRACT_VOLUME,
                       new Long(info.getVolume()));
          infoHash.put(UCOrderCheckCore.INT_CONTRACT_DATE,
                       new Integer(info.getDate()));
          infoHash.put(UCOrderCheckCore.INT_CONTRACT_BOARD_NO,
                       new Integer(info.getSession()));
          contractInfoArray.add(infoHash);
        }
        hash.put(UCOrderCheckCore.ARRAYLIST_CONTRACT_INFORMATION_ARRAY,
                 contractInfoArray);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * OrderRequestコマンドへの応答を処理します．
   * 注文受付時間帯以外はエラーを返す。
   * @param hash 各情報を格納するためのハッシュマップ
   * @param userID ユーザID
   * @param brandName 銘柄名
   * @param newRepay 新規返済区分
   * @param sellBuy 売買区分
   * @param marketLimit 成行指値区分
   * @param price 希望取引価格
   * @param volume 希望取引数量
   */
  public UCommandStatus doOrderRequest(HashMap hash, int userID,
                                       String brandName, int newRepay,
                                       int sellBuy, int marketLimit,
                                       long price, long volume) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCOrderRequestCore.CMD_NAME,
                                              fStatus, userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      UAccount account = fAccountManager.getAccount(userID);
      if (account.getStatus() == UAccount.UNAVAILABLE) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage("YOU ARE BANKRUPTED!!");
        return ucs;
      }
      long latestFuturePrice = fPriceInfoDB.getLatestFuturePrice();
      long delta = (long)((double)latestFuturePrice * MAX_RATE);
      if (Math.abs(price - latestFuturePrice) >= delta) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage("Your order price exceeds the limit!!");
        return ucs;      	
      }
      ucs.setStatus(true);
      String userName = account.getUserName();
      int date = fStatus.getDate();
      int boardNo = fStatus.getSession();
      UOrder o;
      synchronized (fOrderManager) {
      	o = fOrderManager.createOrder(userID, userName,	brandName, 
    		sellBuy, marketLimit, newRepay, price, volume, date, boardNo);
        fOrderManager.addOrder(o);
        fBoard.appendOrder(o);
        fOrderManager.registerOrderToHistory(o);
        fOrderCommandLog.registerOrderRequest(o);
      }
      hash.put(UCOrderRequestCore.LONG_ORDER_ID, Long.toString(o.getOrderID()));
      hash.put(UCOrderRequestCore.STRING_ORDER_TIME, extractTime(o.getTime()));
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doOrderRequest");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * ServerTimeコマンドへの応答を処理します．
   * @param hash 各情報を格納するためのハッシュマップ
   */
  public UCommandStatus doServerTime(HashMap hash) {
    UCommandStatus ucs = new UCommandStatus();
    ucs.setStatus(true);
    hash.put(UCServerTimeCore.STRING_SERVER_TIME, extractTime(new Date()));
    return ucs;
  }

  /**
   * TodaysQuotesコマンドへの応答を処理する。
   * 注文受付時間帯, 取引後時間帯, 決裁後時間帯以外はエラーを返す。
   * @param arrayList ハッシュマップを格納するためのリスト
   * @param brandName 銘柄名
   * @param noOfBoards 過去板寄せ何回分のの情報が必要か?
   */
  public UCommandStatus doTodaysQuotes(ArrayList arrayList, String brandName,
                                       int noOfBoards) {
    // noOfBoards < 0 で全ての板寄せについての情報を返す
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCTodaysQuotesCore.CMD_NAME,
                                              fStatus, SU_ID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      int startIndex = fQuoteDB.getNoOfTodaysQuotes() - 1;
      if (startIndex < 0) {
        // agent.sendMessage( "j30 -1 00:00:00:+09 -1 -1 -1 -1" );
        return ucs;
      }
      int endIndex = startIndex;
      if (noOfBoards < 0) {
        endIndex = 0;
      } else {
        endIndex -= (noOfBoards - 1);
      }
      if (endIndex < 0) {
        endIndex = 0;
      }
      for (int i = startIndex; i >= endIndex; --i) {
        UTodaysQuote tq = fQuoteDB.getTodaysQuote(i);
        HashMap hash = new HashMap();
        hash.put(UCTodaysQuotesCore.STRING_BRAND_NAME, tq.getBrandName());
        hash.put(UCTodaysQuotesCore.INT_DATE, new Integer(tq.getDate()));
        hash.put(UCTodaysQuotesCore.INT_BOARD_NO, new Integer(tq.getSession()));
        hash.put(UCTodaysQuotesCore.STRING_TIME, extractTime(new Date()));
        hash.put(UCTodaysQuotesCore.LONG_PRICE, new Long(tq.getPrice()));
        hash.put(UCTodaysQuotesCore.LONG_VOLUME, new Long(tq.getVolume()));
        hash.put(UCTodaysQuotesCore.LONG_ASKED_QUOTATION,
                 new Long(tq.getAskedQuotation()));
        hash.put(UCTodaysQuotesCore.LONG_BID_QUOTATION,
                 new Long(tq.getBidQuotation()));
        arrayList.add(hash);
      }
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doTodaysQuotes");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * ハッシュマップhashに注文情報oの各データを格納します．
   * @param hash 情報を格納するためのハッシュマップ
   * @param o 注文
   */
  private void sendOrderMessage(HashMap hash, UOrder o) {
    hash.put(UCOrderStatusCore.LONG_ORDER_ID, Long.toString(o.getOrderID()));
    hash.put(UCOrderStatusCore.STRING_ORDER_TIME, extractTime(o.getTime()));
    hash.put(UCOrderStatusCore.STRING_BRAND_NAME, o.getBrandName());
    hash.put(UCOrderStatusCore.INT_NEW_REPAY, Integer.toString(o.getNewRepay()));
    hash.put(UCOrderStatusCore.INT_SELL_BUY, Integer.toString(o.getSellBuy()));
    hash.put(UCOrderStatusCore.INT_MARKET_LIMIT,
             Integer.toString(o.getMarketLimit()));
    hash.put(UCOrderStatusCore.LONG_PRICE, Long.toString(o.getPrice()));
    hash.put(UCOrderStatusCore.LONG_VOLUME, Long.toString(o.getVolume()));
    hash.put(UCOrderStatusCore.LONG_CONTRACTED_VOLUME,
             Long.toString(o.getContractVolume()));
    hash.put(UCOrderStatusCore.LONG_CANCEL_VOLUME,
             Long.toString(o.getCancelVolume()));
  }

  /**
   * OrderStatusコマンドへの応答を処理します．
   * 注文受付時間帯, 取引後時間帯以外はエラーを返します．
   * @param arrayList ハッシュマップを格納するためのリスト
   * @param userID ユーザID
   */
  public UCommandStatus doOrderStatus(ArrayList arrayList, int userID) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCOrderStatusCore.CMD_NAME,
                                              fStatus, userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      synchronized (fOrderManager) {
        UOrderArray array = fOrderManager.getOrderArray(userID);
        if (array == null) {
          System.err.print("OrderArray not found ");
          System.err.println("in UMart.doOrderStatus");
          System.exit( -5);
        }
        for (int i = 0; i < array.getNoOfContractedOrders(); ++i) {
          UOrder o = array.getContractedOrderAt(i);
          HashMap hash = new HashMap();
          sendOrderMessage(hash, o);
          arrayList.add(hash);
        }
        for (int i = 0; i < array.getNoOfUncontractedOrders(); ++i) {
          UOrder o = array.getUncontractedOrderAt(i);
          HashMap hash = new HashMap();
          sendOrderMessage(hash, o);
          arrayList.add(hash);
        }
      }
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doOrderStatus");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * OrderStatusコマンドへの応答を処理します．
   * 注文受付時間帯, 取引後時間帯以外はエラーを返します．
   * @param hash 各情報を格納するためのハッシュマップ
   * @param o オーダー
   * @param info 約定情報
   */
  private void setContractInfoToHash(HashMap hash, UOrder o,
                                     UContractInformation info) {
    hash.put(UCExecutionsCore.LONG_CONTRACT_ID, new Long(info.getContractID()));
    hash.put(UCExecutionsCore.STRING_CONTRACT_TIME, extractTime(info.getTime()));
    hash.put(UCExecutionsCore.LONG_ORDER_ID, new Long(o.getOrderID()));
    hash.put(UCExecutionsCore.STRING_BRAND_NAME, o.getBrandName());
    hash.put(UCExecutionsCore.INT_NEW_REPAY, new Integer(o.getNewRepay()));
    hash.put(UCExecutionsCore.INT_SELL_BUY, new Integer(o.getSellBuy()));
    hash.put(UCExecutionsCore.LONG_PRICE, new Long(info.getPrice()));
    hash.put(UCExecutionsCore.LONG_VOLUME, new Long(info.getVolume()));
  }

  /**
   * Executionsコマンドへの応答を処理します．
   * 注文受付時間帯, 取引後時間帯以外はエラーを返します．
   * @param arrayList ハッシュマップを格納するためのリスト
   * @param userID ユーザID
   */
  public UCommandStatus doExecutions(ArrayList arrayList, int userID) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCExecutionsCore.CMD_NAME,
                                              fStatus, userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      synchronized (fOrderManager) {
        Vector orderArray = fOrderManager.getAllOrders(userID);
        Enumeration orders = orderArray.elements();
        while (orders.hasMoreElements()) {
          UOrder o = (UOrder) orders.nextElement();
          Enumeration infos = o.getContractInformationArray().elements();
          while (infos.hasMoreElements()) {
            UContractInformation info = (UContractInformation) infos.
                nextElement();
            HashMap hash = new HashMap();
            setContractInfoToHash(hash, o, info);
            arrayList.add(hash);
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doExecutions");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * Loginコマンドへの応答を処理します．
   * @param loginName ユーザー名
   * @param passwd パスワード
   * @return 対応するログイン状態(ULoginStatus)
   */
  public ULoginStatus doLogin(String loginName, String passwd) {
    int userID = fAccountManager.getUserID(loginName, passwd);
    if (userID < 0) {
      return null;
    }
    return fLoginManager.findLoginStatus(userID);
  }

  /**
   * FuturePriceコマンドへの応答を処理します．
   * 取引前時間帯, 注文受付時間帯, 取引後時間帯, 決済後時間帯以外は
   * エラーを返す。
   * @param arrayList ハッシュマップを格納するためのリスト
   * @param brandName 銘柄名
   * @param noOfSteps 過去何ステップ分の先物価格が必要か?
   */
  public UCommandStatus doFuturePrice(ArrayList arrayList, String brandName,
                                      int noOfSteps) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCFuturePriceCore.CMD_NAME,
                                              fStatus, SU_ID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      long[] prices = fPriceInfoDB.getFuturePrices(noOfSteps);
      int date = fStatus.getDate();
      int boardNo = fStatus.getSession();
      int steps = (date - 1) * fNoOfSessionsPerDay + boardNo;
      for (int i = 0; i < prices.length; ++i) {
        HashMap elem = new HashMap();
        elem.put(UCFuturePriceCore.STRING_BRAND_NAME, brandName);
        if (steps > 0) {
          date = (steps - 1) / fNoOfSessionsPerDay + 1;
          boardNo = steps - (date - 1) * fNoOfSessionsPerDay;
        } else {
          date = - (Math.abs(steps) / fNoOfSessionsPerDay);
          boardNo = fNoOfSessionsPerDay + steps - (date * fNoOfSessionsPerDay);
        }
        elem.put(UCFuturePriceCore.INT_DAY, new Integer(date));
        elem.put(UCFuturePriceCore.INT_BOARD_NO, new Integer(boardNo));
        elem.put(UCFuturePriceCore.INT_STEP, new Integer(steps));
        elem.put(UCFuturePriceCore.LONG_PRICE, new Long(prices[i]));
        arrayList.add(elem);
        --steps;
//				arrayList.add(brandName + " YYYY-MM-DD " + prices[i]);
      }
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doFuturePrice");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * HistoricalQuotesコマンドへの応答を処理します．
   * 注文受付時間帯, 取引後時間帯, 決裁後時間帯以外はエラーを返します．
   * @param arrayList ハッシュマップを格納するためのリスト
   * @param brandName 銘柄名
   * @param noOfDays 過去何日分の情報が必要か?
   */
  public UCommandStatus doHistoricalQuotes(ArrayList arrayList,
                                           String brandName, int noOfDays) {
    // noOfDays < 0 で過去全てを返す
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCHistoricalQuotesCore.CMD_NAME,
                                              fStatus, SU_ID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      int startIndex = fQuoteDB.getNoOfHistoricalQuotes() - 1;
      if (startIndex < 0) {
        // agent.sendMessage( "j30 -1 00:00:00:+09 -1 -1 -1 -1 -1" );
        return ucs;
      }
      int endIndex = startIndex;
      if (noOfDays < 0) {
        endIndex = 0;
      } else {
        endIndex -= (noOfDays - 1);
      }
      if (endIndex < 0) {
        endIndex = 0;
      }
      for (int i = startIndex; i >= endIndex; --i) {
        UHistoricalQuote hq = fQuoteDB.getHistoricalQuote(i);
        HashMap hash = new HashMap();
        hash.put(UCHistoricalQuotesCore.STRING_BRAND_NAME, hq.getBrandName());
        hash.put(UCHistoricalQuotesCore.INT_DATE, new Integer(hq.getDate()));
        hash.put(UCHistoricalQuotesCore.LONG_START_PRICE,
                 new Long(hq.getStartPrice()));
        hash.put(UCHistoricalQuotesCore.LONG_HIGHEST_PRICE,
                 new Long(hq.getHighestPrice()));
        hash.put(UCHistoricalQuotesCore.LONG_LOWEST_PRICE,
                 new Long(hq.getLowestPrice()));
        hash.put(UCHistoricalQuotesCore.LONG_END_PRICE, new Long(hq.getEndPrice()));
        hash.put(UCHistoricalQuotesCore.LONG_VOLUME, new Long(hq.getVolume()));
        arrayList.add(hash);
      }
    } catch (Exception e) {
      System.err.println(
          "Exception: " + e + " in UMart.doHistoricalQuotes");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * MarketStatusコマンドへの応答を処理します．
   * @param hash 各情報を格納するためのハッシュマップ
   */
  public UCommandStatus doMarketStatus(HashMap hash) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      ucs.setStatus(true);
      hash.put(UCMarketStatusCore.INT_MARKET_STATUS,
               new Integer(fStatus.getState()));
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doMarketStatus");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * OrderIDHistoryコマンドへの応答を処理します．
   * @param selfUserID このコマンドを実行したユーザーのID
   * @param arrayList 指定されたユーザーIDの過去noOfSteps分の注文依頼のIDを格納するためのArrayList
   * @param targetUserID 指定ユーザーID (-1の場合，selfUserIDが代入される）
   * @param noOfSteps 過去板寄せ何回分の情報がほしいか？
   * @return コマンドの処理結果
   */
  public UCommandStatus doOrderIDHistory(int selfUserID,
                                         ArrayList arrayList, int targetUserID,
                                         int noOfSteps) {
    arrayList.clear();
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (targetUserID < 0) {
        targetUserID = selfUserID;
      }
      if ( (selfUserID != targetUserID && selfUserID != UMart.SU_ID)
          || !fCmdExecutableChecker.isExecutable(UCOrderIDHistoryCore.CMD_NAME,
                                                 fStatus, targetUserID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      int sumOfSteps = (fStatus.getDate() - 1) * fNoOfSessionsPerDay +
          fStatus.getSession();
      sumOfSteps -= (noOfSteps - 1);
      int date = (sumOfSteps - 1) / fNoOfSessionsPerDay + 1;
      int boardNo = sumOfSteps - (date - 1) * fNoOfSessionsPerDay;
      if (date < 1) {
        date = 1;
      }
      if (boardNo < 1) {
        boardNo = 1;
        // System.err.println( "Greater than Day" + date + " Board" + boardNo );
      }
      synchronized (fOrderManager) {
        UOrderArray orderArray = fOrderManager.getOrderArray(targetUserID);
        ArrayList history = orderArray.getHistory();
        // System.err.println(history.toString());
        int index = 0;
        for (int i = history.size() - 1; i >= 0; --i) {
          UOrder o = (UOrder) history.get(i);
          if (o.getDate() >= date && o.getSession() >= boardNo) {
            arrayList.add(new Long(o.getOrderID()));
          }
        }
      }
      ucs.setStatus(true);
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doOrderIDHistory");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * Positionコマンドへの応答を処理します．
   * 注文受付時間帯, 取引後時間帯, 決済後時間帯以外はエラーを返します．
   * @param hash 各情報を格納するためのハッシュマップ
   * @param userID ユーザID
   */
  public UCommandStatus doPosition(HashMap hash, int userID) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCPositionCore.CMD_NAME, fStatus,
                                              userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      UAccount account = fAccountManager.getAccount(userID);
      if (account == null) {
        System.err.println("Account not found in UMart.doPosition");
        System.exit( -5);
      }
      UPosition pos = account.getPosition();
      long todaySell = 0;
      long todayBuy = 0;
      for (int boardNo = 1; boardNo <= fStatus.getSession(); ++boardNo) {
        todaySell += pos.getTodaySellPosition(boardNo);
        todayBuy += pos.getTodayBuyPosition(boardNo);
      }
      hash.put(UCPositionCore.LONG_TODAY_SELL, new Long(todaySell));
      hash.put(UCPositionCore.LONG_TODAY_BUY, new Long(todayBuy));
      long yesterdaySell = pos.getSumOfSellPositionUntilYesterday();
      long yesterdayBuy = pos.getSumOfBuyPositionUntilYesterday();
      hash.put(UCPositionCore.LONG_YESTERDAY_SELL, new Long(yesterdaySell));
      hash.put(UCPositionCore.LONG_YESTERDAY_BUY, new Long(yesterdayBuy));
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doPosition");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * Scheduleコマンドへの応答を処理します．
   * @param hash 各情報を格納するためのハッシュマップ
   */
  public UCommandStatus doSchedule(HashMap hash) {
    UCommandStatus ucs = new UCommandStatus();
    ucs.setStatus(true);
    hash.put(UCScheduleCore.INT_MAX_DAY, new Integer(fMaxDate));
    hash.put(UCScheduleCore.INT_NO_OF_BOARDS, new Integer(fNoOfSessionsPerDay));
    return ucs;
  }

  /**
   * ServerDateコマンドへの応答を処理します．
   * @param hash 各情報を格納するためのハッシュマップ
   */
  public UCommandStatus doServerDate(HashMap hash) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      int date = fStatus.getDate();
      int boardNo = fStatus.getSession();
      ucs.setStatus(true);
      hash.put(UCServerDateCore.INT_DAY, new Integer(date));
      hash.put(UCServerDateCore.INT_BOARD_NO, new Integer(boardNo));
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doServerDate");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * SetSpotDateコマンドへの応答を処理します．
   * 取引前時間帯以外はエラーを返す。
   * @param userID ユーザID
   * @param startPoint 現物価格開始位置
   */
  public UCommandStatus doSetSpotDate(int userID, int startPoint) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCSetSpotDateCore.CMD_NAME,
                                              fStatus, userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      fPriceInfoDB.initializePtr(startPoint, fMaxDate, fNoOfSessionsPerDay);
    } catch (Exception e) {
      System.err.println(
          "Exception:SetSchedule " + e + " in UMart.doSetSpotDate");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * SpotIntervalコマンドへの応答を処理します．
   * @param hash 各情報を格納するためのハッシュマップ
   */
  public UCommandStatus doSpotInterval(HashMap hash) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCSpotIntervalCore.CMD_NAME,
                                              fStatus, SU_ID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      hash.put(UCSpotIntervalCore.INT_SPOT_INTERVAL,
               new Integer(UMart.DEFAULT_SPOT_INTERVAL));
      return ucs;
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doSpotInterval");
      System.exit(5);
      return ucs;
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * SpotPriceコマンドへの応答を処理します．
   * 取引前時間帯, 注文受付時間帯, 取引後時間帯, 決済後時間帯以外は
   * エラーを返します．
   * @param arrayList ハッシュマップを格納するためのリスト．先頭の要素は直近の価格情報．
   * @param brandName 銘柄名
   * @param noOfSteps 過去何ステップ分の現物価格が必要か?
   */
  public UCommandStatus doSpotPrice(ArrayList arrayList, String brandName,
                                    int noOfSteps) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCSpotPriceCore.CMD_NAME, fStatus,
                                              SU_ID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      ucs.setStatus(true);
      long[] prices = fPriceInfoDB.getSpotPrices(noOfSteps);
      int date = fStatus.getDate();
      int boardNo = fStatus.getSession();
      int steps = (date - 1) * fNoOfSessionsPerDay + boardNo;
      for (int i = 0; i < prices.length; ++i) {
        HashMap elem = new HashMap();
        elem.put(UCSpotPriceCore.STRING_BRAND_NAME, brandName);
        if (steps > 0) {
          date = (steps - 1) / fNoOfSessionsPerDay + 1;
          boardNo = steps - (date - 1) * fNoOfSessionsPerDay;
        } else {
          date = - (Math.abs(steps) / fNoOfSessionsPerDay);
          boardNo = fNoOfSessionsPerDay + steps - (date * fNoOfSessionsPerDay);
        }
        elem.put(UCSpotPriceCore.INT_DAY, new Integer(date));
        elem.put(UCSpotPriceCore.INT_BOARD_NO, new Integer(boardNo));
        elem.put(UCSpotPriceCore.INT_STEP, new Integer(steps));
        elem.put(UCSpotPriceCore.LONG_PRICE, new Long(prices[i]));
        arrayList.add(elem);
        --steps;
        // arrayList.add(brandName + " YYYY-MM-DD " + prices[i]);
      }
    } catch (Exception e) {
      System.err.println("Exception: " + e + " in UMart.doSpotPrice");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * ServerStatusコマンドを処理します．
   * @param data サーバー状態を格納するためのHashMap
   * @param userID このコマンドを実行したユーザーのユーザーID
   * @return 実行状態
   */
  public UCommandStatus doServerStatus(HashMap data, int userID) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCServerStatusCore.CMD_NAME,
                                              fStatus, userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      int date = fStatus.getDate();
      data.put(UCServerStatusCore.INT_DATE, new Integer(date));
      // 現在の日付（U-Mart暦)
      int boardNo = fStatus.getSession();
      data.put(UCServerStatusCore.INT_BOARD_NO, new Integer(boardNo));
      // 現在の板寄せ回数
      int state = fStatus.getState();
      data.put(UCServerStatusCore.INT_STATE, new Integer(state)); // 時間帯
      ucs.setStatus(true);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * AccountHistoryコマンドを処理します．
   * @param array 口座履歴要素(HashMap)が格納される配列
   * @param userID このコマンドを実行したユーザーのユーザーID
   * @param targetUserId このユーザーID（-1の場合，userIDが代入される）をもつユーザーの口座履歴をarrayに格納します．
   * @param noOfSteps 指定されたステップ数分の直近の口座情報を返します．
   * @return 実行状態
   */
  public UCommandStatus doAccountHistory(ArrayList array, int userID,
                                         int targetUserId, long noOfSteps) {
    UCommandStatus ucs = new UCommandStatus();
    try {
      fStateLock.readLock();
      if (!fCmdExecutableChecker.isExecutable(UCAccountHistoryCore.CMD_NAME,
                                              fStatus, userID)) {
        ucs.setStatus(false);
        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
        return ucs;
      }
      if (targetUserId < 0) {
        targetUserId = userID;
      }
      ArrayList history = fAccountManager.getAccount(targetUserId).
          getAccountHistory();
      if (noOfSteps < 0) {
        for (int i = history.size() - 1; i >= 0; --i) {
          array.add(history.get(i));
        }
      } else {
        for (int i = 0; i < noOfSteps; ++i) {
          int index = history.size() - i - 1;
          if (index < 0) {
            break;
          }
          array.add(history.get(index));
        }
      }
      ucs.setStatus(true);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return ucs;
    }
  }

  /**
   * 現在のサーバー状態を返します．
   * @return サーバー状態
   */
  public UServerStatus getStatus() {
    UServerStatus result = null;
    try {
      fStateLock.readLock();
      result = (UServerStatus) fStatus.clone();
    } catch (Exception ex) {
      System.err.println("Exception: " + ex
                         + " in UMart.getMarketStatus");
      System.exit(5);
    } finally {
      fStateLock.readUnlock();
      return result;
    }
  }

  /**
   * U-Martサーバの設定を標準出力します．
   */
  public void printOn() {
    System.out.println("UMart is ready");
    System.out.println("No of boards per day: " + fNoOfSessionsPerDay);
  }

  /**
   * 次の状態へ進めます．
   * 状態遷移は以下のとおりです:<BR>
   * 板寄せ1，注文受付(UServerStatus.ACCEPT_ORDERS)　・・・(1)<br>
   * 板寄せ1, 板寄せ(UServerStatus.ITAYOSE) ・・・内部状態<br>
   * 板寄せ1, 板寄せ後(UServerStatus.AFTER_ITAYOSE)<br>
   * ：<br>
   * ：<br>
   * 板寄せn，注文受付(UServerStatus.ACCEPT_ORDERS)
   * 板寄せn, 板寄せ(UServerStatus.ITAYOSE) ・・・内部状態
   * 板寄せn, 板寄せ後(UServerStatus.AFTER_ITAYOSE)<br>
   * 板寄せn, 清算(UServerStatus.MARKING_TO_MARKET) ・・・内部状態<br>
   * 板寄せn, 清算後(UServerStatus.AFTER_MARKING_TO_MARKET)<br>
   * 最終日(MaxDate)でなければ(1)へ．<br>
   * 日付MaxDate+1, 板寄せ1, 決算(UServerStatus.SETTLEMENT) ・・・内部状態<br>
   * 日付MaxDate+1, 板寄せ1, 決算(UServerStatus.AFTER_SETTLEMENT)<br>
   * @return サーバー状態
   */
  public UServerStatus nextStatus() {
    try {
      fStateLock.writeLock();
      switch (fStatus.getState()) {
        case UServerStatus.BEFORE_TRADING:
          fStatus.setDate(1);
          fStatus.setSession(1);
          fStatus.setState(UServerStatus.ACCEPT_ORDERS);
          break;
        case UServerStatus.ACCEPT_ORDERS:
          fStatus.setState(UServerStatus.ITAYOSE);
          makeContracts();
          updateLog(fStatus.getDate(), fStatus.getSession(), fStatus.getState());
          if (fStatus.getSession() == fNoOfSessionsPerDay) {
            fStatus.setState(UServerStatus.MARKING_TO_MARKET);
            markToMarket();
            fAccountManager.backupAllBalances( (fStatus.getDate() - 1) *
                                              fNoOfSessionsPerDay +
                                              fStatus.getSession());
            fStatus.setState(UServerStatus.AFTER_MARKING_TO_MARKET);
            updateLog(fStatus.getDate(), fStatus.getSession(), fStatus.getState());
          } else {
            fAccountManager.backupAllBalances( (fStatus.getDate() - 1) *
                                              fNoOfSessionsPerDay +
                                              fStatus.getSession());
            fStatus.incrementSession();
            fStatus.setState(UServerStatus.ACCEPT_ORDERS);
          }
          break;
        case UServerStatus.AFTER_MARKING_TO_MARKET:
          fBoard.clear();
          fOrderManager.removeAllOrders();
          fAccountManager.updateYesterdayClosingPrice();
          fQuoteDB.update();
          fStatus.incrementDate();
          fStatus.resetSession();
          if (fStatus.getDate() == fMaxDate + 1) {
            settlement();
            fStatus.setState(UServerStatus.AFTER_SETTLEMENT);
            updateLog(fStatus.getDate(), fStatus.getSession(), fStatus.getState());
          } else {
            fStatus.setState(UServerStatus.ACCEPT_ORDERS);
          }
          break;
        case UServerStatus.AFTER_SETTLEMENT:
          break;
        default:
          System.err.println("Unknown status in UMartStandAlone.nextStatus");
          System.exit(5);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    } finally {
      fStateLock.writeUnlock();
      // System.out.println(fStatus.getStateString());
      return (UServerStatus) fStatus.clone();
    }
  }

  /**
   * 取引所の状態を「板寄せ時間帯」に設定し, 板寄せ処理を行います．
   * 具体的な手順は以下のとおりです．：<br>
   * 1. 全注文(UOrder)をUOrderManagerからUBoardへ移動する。<br>
   * 2. UBoard.makeContractsメソッドにより約定処理を行う。<br>
   * 3. UQuoteDBへTodaysQuoteを登録する。<br>
   * 4. UAccountManagerに約定価格を登録する。<br>
   * 5. UAccountManagerへ約定情報を登録する。<br>
   * 6. 破産チェックを行う。<br>
   * 7. 板情報を更新する。<br>
   * 8. 全注文(UOrder)をUBoardからUOrderManagerへ移動する。<br>
   * 9. 破産した会員の注文を全てキャンセルする。<br>
   * 10. UPriceInfoDBへ約定価格を登録する。<br>
   */
  private void makeContracts() {
    int date = fStatus.getDate();
    int boardNo = fStatus.getSession();
    removeUncontractedOrdersFromOrderManager();
    saveBoardShape();
    fBoard.makeContracts(date, boardNo);
    long price = fBoard.getContractPrice();
    // System.out.println("Contracted price is " + price);
    long volume = fBoard.getContractVolume();
    // System.out.println("Contracted volume is " + volume);
    long askedQuotation = fBoard.getAskedQuotation();
    long bidQuotation = fBoard.getAskedQuotation();
    fQuoteDB.addTodaysQuote("j30", date, boardNo, price, volume,
                            askedQuotation, bidQuotation);
    fAccountManager.setContractedPrice(price, boardNo);
    registerContractsToAccountManager();
    fAccountManager.checkBankruptForAllAccounts();
    if (!fAccountManager.checkConsistency()) {
      System.err.print("Error: Account consistency violation ");
      System.err.println("in UMart.makeContracts");
      System.err.println("Random Seed:" + URandom.getSeed());
      System.exit(5);
    }
    updateBoardInformation(price, volume);
    moveOrdersFromBoardToOrderManager();
    cancelOrdersOfBankruptedMembers();
    registerUncontractedOrdersToBoard();
    fPriceInfoDB.addCurrentPriceAndIncrementPtr(price);
  }

  /**
   * UOrderManagerの未約定注文配列(UncontractedOrderArray)から
   * 全注文(UOrder)の参照を外します．
   */
  private void removeUncontractedOrdersFromOrderManager() {
    Enumeration arrays = fOrderManager.getOrderArrays();
    while (arrays.hasMoreElements()) {
      UOrderArray orderArray = (UOrderArray) arrays.nextElement();
      Vector uncontractedOrderArray = orderArray.getUncontractedOrders();
      uncontractedOrderArray.removeAllElements();
    }
  }

  /**
   * 現在の板の形状(売注文と買い注文の情報)をUBoardInformationに保存します．
   */
  private void saveBoardShape() {
    fBoardInformation.clear();
    Iterator orders = fBoard.getOrderArray();
    while (orders.hasNext()) {
      UOrder o = (UOrder) orders.next();
      fBoardInformation.addInformation(o.getSellBuy(), o.getMarketLimit(),
                                       o.getPrice(), o.getUncontractOrderVolume());
    }
  }

  /**
   * UAccountManagerへ全約定情報を登録します．
   */
  private void registerContractsToAccountManager() {
    Iterator orders = fBoard.getOrderArray();
    while (orders.hasNext()) {
      UOrder o = (UOrder) orders.next();
      int userID = o.getUserID();
      int sellBuy = o.getSellBuy();
      Vector contractInformationArray = o.getContractInformationArray();
      Enumeration infos = contractInformationArray.elements();
      while (infos.hasMoreElements()) {
        UContractInformation info = (UContractInformation) infos.nextElement();
        if (info.getDate() != fStatus.getDate()) {
          System.err.println("Error: info.getDate() != fDate ");
          System.err.println("in UMart.markToMarket");
          System.exit(5);
        }
        if (info.getSession() == fStatus.getSession()) {
          long volume = info.getVolume();
          if (!fAccountManager.registerContract(userID, sellBuy, volume,
                                                fStatus.getSession())) {
            System.err.print("Error: Can't append the ");
            System.err.print("contract information for ");
            System.err.println("Member" + userID);
            System.exit(5);
          }
        }
      }
    }
  }

  /**
   * 板情報(UBoardInformation)を更新します．
   * @param contractPrice 約定価格
   * @param contractVolume 約定数量
   */
  private void updateBoardInformation(long contractPrice,
                                      long contractVolume) {
    fBoardInformation.setLastUpdateTime(new Date());
    fBoardInformation.setContractPrice(contractPrice);
    fBoardInformation.setContractVolume(contractVolume);
  }

  /**
   * 全注文(UOrder)をUBoardからUOrderManagerへ移動します．
   */
  private void moveOrdersFromBoardToOrderManager() {
    Iterator orders = fBoard.getOrderArray();
    while (orders.hasNext()) {
      UOrder o = (UOrder) orders.next();
      fOrderManager.addOrder(o);
    }
    fBoard.clear();
  }

  /**
   * 破産した会員の注文を全てキャンセルします．
   */
  private void cancelOrdersOfBankruptedMembers() {
    Enumeration accounts = fAccountManager.getBankruptedAccounts();
    while (accounts.hasMoreElements()) {
      UAccount account = (UAccount) accounts.nextElement();
      int userID = account.getUserID();
      fOrderManager.cancelAllOrdersOfMember(userID);
    }
  }

  /**
   * UOrderManager中のすべての未約定注文をUBoardに登録します．
   */
  private void registerUncontractedOrdersToBoard() {
    Enumeration arrays = fOrderManager.getOrderArrays();
    while (arrays.hasMoreElements()) {
      UOrderArray orderArray = (UOrderArray) arrays.nextElement();
      Vector uncontractedOrderArray = orderArray.getUncontractedOrders();
      Enumeration orders = uncontractedOrderArray.elements();
      while (orders.hasMoreElements()) {
        UOrder o = (UOrder) orders.nextElement();
        fBoard.appendOrder(o);
      }
    }
  }

  /**
   * 価格系列のログファイルの内容を更新します．
   * @param path String 価格系列のログファイル名
   * @throws IOException
   */
  private void updatePriceInfoDBFile(String path) throws IOException {
    PrintWriter pw = new PrintWriter(new FileOutputStream(path, true));
    fPriceInfoDB.writeLatestPriceInfo(pw);
    pw.close();
  }

  /**
   * 注文/取消コマンドの履歴をファイルに書き出します．
   * @param path String ファイル名
   * @throws IOException
   */
  private void writeOrderCommandLog(String path) throws IOException {
    PrintWriter pw = new PrintWriter(new FileOutputStream(path));
    fOrderCommandLog.writeTo(pw);
    pw.close();
  }

  /**
   * 約定の履歴をファイルに書き出します．
   * @param path String ファイル名
   * @param date int 日
   * @param session int 節
   * @param orderManager UOrderManager 注文管理者
   * @throws IOException
   */
  private void writeExecutionLog(String path, int date, int session,
                                 UOrderManager orderManager) throws IOException {
    PrintWriter pw = new PrintWriter(new FileOutputStream(path));
    UExecutionLog log = new UExecutionLog(date, session, orderManager);
    log.writeTo(pw);
    pw.close();
  }

  /**
   * 板の履歴をファイルに書き出します．
   * @param path String ファイル名
   * @param date int 日
   * @param session int 節
   * @param boardInfo UBoardInformation 板情報
   * @throws IOException
   */
  private void writeBoardLog(String path, int date, int session,
                             UBoardInformation boardInfo) throws IOException {
    PrintWriter pw = new PrintWriter(new FileOutputStream(path));
    UBoardLog log = new UBoardLog(date, session, boardInfo);
    log.writeTo(pw);
    pw.close();
  }

  /**
   * 口座の履歴をファイルに書き出します．
   * @param path String ファイル名
   * @param date int 日
   * @param session int 節
   * @param accountManager UAccountManager 口座管理者
   * @throws IOException
   */
  private void writeAccountLog(String path, int date, int session,
                               UAccountManager accountManager) throws IOException {
    PrintWriter pw = new PrintWriter(new FileOutputStream(path));
    UAccountLog log = new UAccountLog(date, session, accountManager);
    log.writeTo(pw);
    pw.close();
  }

  /**
   * ログを更新します．
   * @param date 現在の日付
   * @param session 現在の節
   * @param state マーケット状態
   */
  private void updateLog(int date, int session, int state) throws IOException {
    if (fLogFlag == UMart.NO_LOG) {
      return;
    }
    switch (state) {
      case UServerStatus.ITAYOSE:
        updatePriceInfoDBFile(fLogDir + File.separator + "price.csv");
        if (fLogFlag == UMart.DETAILED_LOG) {
          writeOrderCommandLog(fLogDir + File.separator + UMart.ORDER_LOG_DIR
                               + File.separator + "Day" + date + "Session" +
                               session
                               + "_" + UMart.ORDER_LOG_DIR + ".csv");
          fOrderCommandLog.clear();
          writeExecutionLog(fLogDir + File.separator + UMart.EXECUTION_LOG_DIR
                            + File.separator + "Day" + date + "Session" + session
                            + "_" + UMart.EXECUTION_LOG_DIR + ".csv",
                            date, session, fOrderManager);
          writeBoardLog(fLogDir + File.separator + UMart.BOARD_LOG_DIR
                        + File.separator + "Day" + date + "Session" + session
                        + "_" + UMart.BOARD_LOG_DIR + ".csv",
                        date, session, fBoardInformation);
          writeAccountLog(fLogDir + File.separator + UMart.ACCOUNT_LOG_DIR
                          + File.separator + "Day" + date + "Session" + session
                          + "_" + UMart.ACCOUNT_LOG_DIR + ".csv",
                          date, session, fAccountManager);
        } else {
          fOrderCommandLog.clear();
        }
        break;
      case UServerStatus.AFTER_MARKING_TO_MARKET:
        if (fLogFlag == UMart.DETAILED_LOG) {
          writeAccountLog(fLogDir + File.separator + UMart.ACCOUNT_LOG_DIR
                          + File.separator + "Day" + date
                          + "_MM" + "_" + UMart.ACCOUNT_LOG_DIR + ".csv",
                          date, session, fAccountManager);
        }
        break;
      case UServerStatus.AFTER_SETTLEMENT:
        updatePriceInfoDBFile(fLogDir + File.separator + "price.csv");
        writeAccountLog(fLogDir + File.separator + "Settlement_" +
                        UMart.ACCOUNT_LOG_DIR + ".csv",
                        date, session, fAccountManager);
        if (fLogFlag == UMart.DETAILED_LOG) {
          writeAccountLog(fLogDir + File.separator + UMart.ACCOUNT_LOG_DIR
                          + File.separator + "Settlement_" +
                          UMart.ACCOUNT_LOG_DIR + ".csv",
                          date, session, fAccountManager);
        }
        break;
      default:
        System.err.println("Invalid state:" + state +
                           " in UMartStandAlone.updateLog");
    }
  }

  /**
   * 取引所の状態を「値洗い時間帯」に設定し, 値洗い処理を行います．
   */
  private void markToMarket() {
    fAccountManager.markToMarket();
    if (!fAccountManager.checkConsistency()) {
      System.err.print("Error: Account consistency violation ");
      System.err.println("in UMart.markToMarket");
      System.exit(5);
    }
  }

  /**
   * 取引所の状態を「決算時間帯」に設定し, 決算処理を行います．
   * 日付は, 最終決算日+1に設定される。<br>
   */
  private void settlement() {
    long[] spotPrice = fPriceInfoDB.getSpotPrices(1);
    fAccountManager.settlement(spotPrice[0]);
    if (!fAccountManager.checkConsistency()) {
      System.err.print("Error: Account consistency violation ");
      System.err.println("in UMart.markToMarket");
      System.exit(5);
    }
//		fPriceInfoDB.addCurrentPriceAndIncrementPtr(spotPrice[0]);
    fPriceInfoDB.setSettlementPrice();
    fQuoteDB.addTodaysQuote("j30", fMaxDate + 1, 1, spotPrice[0], 1, 0, 0);
  }

  /**
   * 詳細ログの初期化を行います．
   * 具体的には，ログを出力するディレクトリを作成し，price.csv, TimeSeriesDefinitions.csv, Member.csvを作成する．
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void initLog() throws FileNotFoundException, IOException {
    initLog(null, false);
  }

  /**
   * 出力先ディレクトリを指定して，詳細ログの初期化を行います．
   * @param baseDir String 出力先ディレクトリ名
   * @param simpleFlag boolean 簡易ログならばtrue, 詳細ログならばfalse
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void initLog(String baseDir, boolean simpleFlag) throws FileNotFoundException, IOException {
    if (!makeLogDirectory(baseDir, simpleFlag)) {
      throw new IOException();
    }
    writePriceInfoDB(fLogDir + File.separator + "price.csv");
    writeTimeSeriesDefinitionLog(fLogDir + File.separator +
                                 "TimeSeriesDefinitions.csv");
    writeMemberLog(fLogDir + File.separator + "Member.csv");
    if (simpleFlag) {
      fLogFlag = UMart.SIMPLE_LOG;
    } else {
      fLogFlag = UMart.DETAILED_LOG;
    }
  }

  /**
   * PrinceInfoDBをファイルに書き出します．
   * @param path String 出力ファイル名
   * @throws IOException
   */
  private void writePriceInfoDB(String path) throws IOException {
    PrintWriter pw = new PrintWriter(new FileOutputStream(path));
    fPriceInfoDB.writePriceInfoBeforeCurrentPtr(pw);
    pw.close();
  }

  /**
   * TimeSeriesDefinitionLogをファイルに書き出します．
   * @param path String 出力ファイル名
   * @throws IOException
   */
  private void writeTimeSeriesDefinitionLog(String path) throws IOException {
    PrintWriter pw = new PrintWriter(new FileWriter(path));
    UTimeSeriesDefinitions timeSeriesDefinitionLog = new UTimeSeriesDefinitions();
    timeSeriesDefinitionLog.addDefinition("Default", "price.csv", "j30",
                                          fStartPoint -
                                          (Strategy.NO_OF_SPOT_PRICES - 1),
                                          fMaxDate, fNoOfSessionsPerDay);
    timeSeriesDefinitionLog.writeTo(pw);
    pw.close();
  }

  /**
   * MemberLogをファイルに書き出します．
   * @param path String 出力ファイル名
   * @throws IOException
   */
  private void writeMemberLog(String path) throws IOException {
    PrintWriter pw = new PrintWriter(new FileOutputStream(path));
    fMemberLog.writeTo(pw);
    pw.close();
  }

  /**
   * ログを出力するデフォルトのディレクトリ名を作成して返します．
   * @return String ディレクトリ名（UMART＋年＋月＋日＋時＋分＋秒＋ミリ秒）
   */
  private String makeDefaultDirectoryName() {
    String result = "./UMART";
    Calendar cal = Calendar.getInstance();
    int year = cal.get(Calendar.YEAR);
    result += year;
    int month = cal.get(Calendar.MONTH) + 1;
    if (month < 10) {
      result += "0";
    }
    result += month;
    int day = cal.get(Calendar.DATE);
    if (day < 10) {
      result += "0";
    }
    result += day;
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    if (hour < 10) {
      result += "0";
    }
    result += hour;
    int minute = cal.get(Calendar.MINUTE);
    if (minute < 10) {
      result += "0";
    }
    result += minute;
    int second = cal.get(Calendar.SECOND);
    if (second < 10) {
      result += "0";
    }
    result += second;
    int millisecond = cal.get(Calendar.MILLISECOND);
    if (millisecond < 100) {
      result += "0";
    }
    if (millisecond < 10) {
      result += "0";
    }
    result += millisecond;
    return result;
  }

  /**
   * ログ出力用のディレクトリを作成します．
   * @param baseDir String ログ出力用のディレクトリ名．nullの場合，デフォルトのディレクトリが生成されます．
   * @param simpleFlag boolean 簡易ログならばtrue, 詳細ログならばfalse
   */
  private boolean makeLogDirectory(String baseDir, boolean simpleFlag) {
    if (baseDir == null) {
      fLogDir = makeDefaultDirectoryName();
    } else {
      fLogDir = baseDir;
    }
    File directory = new File(fLogDir);
    directory.mkdir();
    if (simpleFlag) {
      return true;
    }
    if (!makeSubDirectory(UMart.ORDER_LOG_DIR)) {
      return false;
    }
    if (!makeSubDirectory(UMart.EXECUTION_LOG_DIR)) {
      return false;
    }
    if (!makeSubDirectory(UMart.BOARD_LOG_DIR)) {
      return false;
    }
    if (!makeSubDirectory(UMart.ACCOUNT_LOG_DIR)) {
      return false;
    }
    return true;
  }

  /**
   * fLogDirで表されるログ出力用ディレクトリの下にサブディレクトリnameを作成します．
   * @param name サブディレクトリ名（相対）
   * @return サブディレクトリの生成に成功した場合：true, 失敗した場合：false
   */
  private boolean makeSubDirectory(String name) {
    String path = fLogDir + File.separator + name;
    File directory = new File(path);
    return directory.mkdir();
  }

  /**
   * 口座情報にしたがって, fOrderManagerの初期設定を行います．
   * したがって, このメソッドを呼び出す前に, fAccountManagerが
   * 正しく初期化されている必要があります．
   */
  private void setupOrderManager() {
    Enumeration accounts = fAccountManager.getAccounts();
    while (accounts.hasMoreElements()) {
      UAccount account = (UAccount) accounts.nextElement();
      fOrderManager.createOrderArray(account.getUserID());
    }
  }

  /**
   * fLoginMangerの初期化を行います．
   * 具体的には，ログインマネージャーを生成します．
   * したがって，このメソッドを呼び出す前に，fAccountManagerが正しく
   * 初期化されている必要があります．
   */
  private void setupLoginManager() {
    fLoginManager = this.createLoginManager();
    fLoginManager.creatLoginStatus(UMart.SU_ID);
    Enumeration accounts = fAccountManager.getAccounts();
    while (accounts.hasMoreElements()) {
      UAccount account = (UAccount) accounts.nextElement();
      fLoginManager.creatLoginStatus(account.getUserID());
    }
  }

  /**
   * ネットワーク環境用またはスタンドアロン環境用のログインマネージャを生成します．
   * @return ログインマネージャ
   */
  protected abstract ULoginManager createLoginManager();

  /**
   * 取引所の状態を「注文受付時間帯」に設定し, エージェントごとに
   * 注文を受けつけます．
   */
  public void recieveOrdersFromLocalAgents() {
    Iterator itr = fStrategyArray.iterator();
    while (itr.hasNext()) {
      UBaseAgent strategy = (UBaseAgent) itr.next();
      // System.err.println(strategy.getLoginName());
      int date = fStatus.getDate();
      int boardNo = fStatus.getSession();
      int state = fStatus.getState();
      strategy.doActions(date, boardNo, state, fMaxDate, fNoOfSessionsPerDay);
    }
  }

}
