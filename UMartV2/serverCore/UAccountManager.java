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
 * 取引所と全ての一般ユーザーの口座を管理するクラスです．
 */
public class UAccountManager {

  /** 板寄せで価格が決まらなかった場合に使われる定数 */
  public static final long UNCONTRACTED_PRICE = -1;

  /** 取引所の口座 */
  private UExchangeAccount fExchangeAccount;

  /** 一般ユーザーの口座の格納されているベクタ */
  private Vector fAccountArray;

  /** 破産したユーザーの口座の格納されているベクタ */
  private Vector fBankruptedAccountArray;

  /** 一日あたりの板寄せ回数 */
  private int fNoOfBoardsPerDay;

  /** 前日の終値 */
  private long fYesterdayClosingPrice;

  /** 最新の約定価格 */
  private long fLatestContractedPrice;

  /** 当日の約定価格 (板寄せ回数分の約定価格が格納されている */
  private long fContractedPrice[];

  /**
   * UAccountManagerを初期化します．
   * @param members 全ての会員情報
   * @param noOfBoardsPerDay 一日あたりの板寄せ回数
   */
  public UAccountManager(UMemberList members, int noOfBoardsPerDay) {
    fExchangeAccount = new UExchangeAccount();
    fAccountArray = new Vector();
    fBankruptedAccountArray = new Vector();
    fYesterdayClosingPrice = fLatestContractedPrice = -1;
    fNoOfBoardsPerDay = noOfBoardsPerDay;
    fContractedPrice = new long[fNoOfBoardsPerDay];
    for (int i = 1; i <= fNoOfBoardsPerDay; ++i) {
      fContractedPrice[i - 1] = -1;
    }
    int userID = 1;
    Iterator memberItr = members.getMembers();
    while (memberItr.hasNext()) {
      HashMap memberInfo = (HashMap)memberItr.next();
      UAccount account = new UAccount(userID, memberInfo, noOfBoardsPerDay);
      fAccountArray.add(account);
      ++userID;
    }
  }

  /**
   * 引数で指定されたユーザー名とパスワードをもつユーザー(取引所も
   * 含む)のユーザーIDを返します．もし, 該当するユーザーが存在しなければ
   * -1を返します．
   * @param userName ユーザー名
   * @param passwd パスワード
   * @return ユーザーID。もし該当するユーザーが存在しなければ, -1。
   */
  public int getUserID(String userName, String passwd) {
    if (userName.equals(fExchangeAccount.getUserName()) &&
        passwd.equals(fExchangeAccount.getPasswd())) {
      return fExchangeAccount.getUserID();
    }
    Enumeration accounts = fAccountArray.elements();
    while (accounts.hasMoreElements()) {
      UAccount account = (UAccount) accounts.nextElement();
      if (userName.equals(account.getUserName()) &&
          passwd.equals(account.getPasswd())) {
        return account.getUserID();
      }
    }
    return -1;
  }

  /**
   * 引数で指定されたユーザーIDをもつユーザーの口座を返します．
   * ただし, 指定されたユーザーIDをもつ口座が見つからなければnullを返します．
   * @param id ユーザーID
   * @return 口座オブジェクト。指定されたユーザーIDをもつ口座がない場合は
   *         null。
   */
  public UAccount getAccount(int id) {
    Enumeration e = fAccountArray.elements();
    while (e.hasMoreElements()) {
      UAccount account = (UAccount) e.nextElement();
      if (id == account.getUserID()) {
        return account;
      }
    }
    return null;
  }

  /**
   * 全てのユーザーの口座オブジェクト(UAccount)を列挙するための
   * Enumerationを返します．
   * @return 全てのユーザーの口座オブジェクト(UAccount)を列挙するための
   *         Enumeration
   */
  public Enumeration getAccounts() {
    return fAccountArray.elements();
  }

  /**
   * 取引所の口座を返します．
   * @return 取引所の口座
   */
  public UExchangeAccount getExchangeAccount() {
    return fExchangeAccount;
  }

  /**
   * 破産したユーザーの口座(UAccount)を列挙するための
   * Enumerationを返します．
   * @return 破産したユーザーの口座(UAccount)を列挙するためのEnumeration
   */
  public Enumeration getBankruptedAccounts() {
    return fBankruptedAccountArray.elements();
  }

  /**
   * 口座数を返します．
   * @return 口座数
   */
  public int getNoOfAccounts() {
    return fAccountArray.size();
  }

  /**
   * 内部情報を出力します．
   * @param pw 出力先
   */
  public void printOn(PrintWriter pw) {
    try {
      pw.println("fNoOfBoards = " + fNoOfBoardsPerDay);
      pw.println("fYesterdayClosingPrice = " + fYesterdayClosingPrice);
      pw.println("fLatestContractedPrice = " + fLatestContractedPrice);
      for (int i = 1; i <= fNoOfBoardsPerDay; i++) {
        pw.println("fContractedPrice[" + i + "] = " + fContractedPrice[i - 1]);
      }
      pw.println("< Acount of echange >");
      fExchangeAccount.printOn(pw);
      pw.println("Size of fAccountArray = " + fAccountArray.size());
      Enumeration e = fAccountArray.elements();
      while (e.hasMoreElements()) {
        UAccount account = (UAccount) e.nextElement();
        pw.println("< Acount of member >");
        account.printOn(pw);
      }
      pw.print("\n");
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }

  /**
   * 約定情報を登録する。登録と同時に, 手数料の計算と支払い,
   * ポジションの再計算, 借入金の再計算が行われる。引数で指定した
   * ユーザーIDの口座がすでに破産している場合は何もせずにfalseを返します．
   * ただし, このメソッドを呼び出す前に, setContractedPriceメソッドを
   * 呼び出して約定価格を設定しておく必要があります．
   * また, このメソッドは破産のチェックは行われないので,
   * 全ての約定情報を登録した後に, checkBankruptForAllAccountsメソッドを
   * 呼び出して破産のチェックおよび処理を行う必要があります．
   * @param userID 約定した注文のユーザーID
   * @param sellbuy 売買区分
   * @param volume 約定数量
   * @param boardNo 約定したときの板寄せ回数
   * @return true: 指定したユーザーIDの口座が見つかり約定情報の登録に
   *         成功した場合, false: 指定したユーザーIDの口座が見つからず
   *         約定情報の登録に失敗した場合
   */
  public boolean registerContract(int userID, int sellbuy,
                                  long volume, int boardNo) {
    UAccount account = getAccount(userID);
    if (account.getStatus() == UAccount.UNAVAILABLE) {
      return false;
    }
    UPosition userPosition = account.getPosition();
    returnMarginToMember(account);
    payFeeToExchange(account, volume);
    if (sellbuy == UOrder.SELL) {
      userPosition.addToTodaySellPosition(volume, boardNo);
    } else if (sellbuy == UOrder.BUY) {
      userPosition.addToTodayBuyPosition(volume, boardNo);
    } else {
      System.err.println("Error in Contract! ");
      System.exit( -1);
    }
    payMarginToExchange(account);
    checkLoan(account);
    return true;
  }

  /**
   * 破産していない全ての口座の破産チェックを行い, 必要ならば
   * 破産処理を行います．
   */
  public void checkBankruptForAllAccounts() {
    Enumeration accounts = fAccountArray.elements();
    while (accounts.hasMoreElements()) {
      UAccount account = (UAccount) accounts.nextElement();
      if (account.getStatus() == UAccount.AVAILABLE) {
        checkBankrupt(account);
      }
    }
  }

  /**
   * 全ての口座の妥当性をチェックします．チェック項目は以下のとおりです:<br>
   * 1. 取引所を含め全ての会員の売りポジションと買いポジションの
   * 総和は等しい。<br>
   * 2. 全ての保有現金残高の総和はsuを含め初期状態と同じである。<br>
   * @return true: O.K., false: N.G.
   */
  public boolean checkConsistency() {
    Enumeration e = fAccountArray.elements();
    long totalSellPosition = fExchangeAccount.getSellPosition();
    long totalBuyPosition = fExchangeAccount.getBuyPosition();
    long totalCash = fExchangeAccount.getCash();
    // System.out.println("fExchangeAccount:" + totalCash);
    long totalInitialCash = 0;
    while (e.hasMoreElements()) {
      UAccount account = (UAccount) e.nextElement();
      totalSellPosition += this.getTotalSellPosition(account);
      totalBuyPosition += this.getTotalBuyPosition(account);
      UBalance balance = account.getTodayBalance();
      balance.updateCash();
      totalCash += balance.getCash();
      totalInitialCash += balance.getInitialCash();
    }
    /*
         System.out.println("No. of total sell positions: " + totalSellPosition
                       + ", No. of total buy positions: " + totalBuyPosition);
     */
    if (totalSellPosition != totalBuyPosition) {
      System.err.println("Position Check Error!");
      System.err.println("No. of total sell positions: " + totalSellPosition
                         + ", No. of total buy positions: " + totalBuyPosition);
      return false;
    } else {
      // System.out.println("Position Check OK!");
    }
    /*
     System.out.println("Total cash: " + totalCash + ", Total initial cash: " +
                       totalInitialCash);
     */
    if (totalInitialCash != totalCash) {
      System.err.println("Total Cash Check Error!");
      System.err.println("Total cash: " + totalCash + ", Total initial cash: " +
                         totalInitialCash);
      return false;
    } else {
      // System.out.println("Total Cash Check OK!");
    }
    return true;
  }

  /**
   * 値洗い処理を処理を行います．
   */
  public void markToMarket() {
    long closingPrice = fLatestContractedPrice;
    Enumeration e = fAccountArray.elements();
    while (e.hasMoreElements()) {
      UAccount account = (UAccount) e.nextElement();
      if (account.getStatus() == UAccount.AVAILABLE) {
        long profitDiff = calculateUnrealizedProfit(account, closingPrice);
        moveUnrealizedProfitFromExchangeToMember(account, profitDiff);
        account.getPosition().collect();
        long todayInterest = calculateTodayInterest(account);
        payInterestFromMemberToExchange(account, todayInterest);
        checkLoan(account);
        checkBankrupt(account);
//				account.backup();
      }
    }
  }

  /**
   * 全会員の当日の収支情報のバックアップをとります．
   */
  public void backupAllBalances(int step) {
    Enumeration e = fAccountArray.elements();
    while (e.hasMoreElements()) {
      UAccount account = (UAccount) e.nextElement();
      account.backup(step);
    }
  }

  /**
   * 最終決済処理を行います．
   * @param tomorrowSpotPrice 最終決済価格(最終日翌日の現物価格)
   */
  public void settlement(long tomorrowSpotPrice) {
    fLatestContractedPrice = tomorrowSpotPrice;
    for (int i = 1; i <= fNoOfBoardsPerDay; ++i) {
      fContractedPrice[i - 1] = -1;
    }
    Enumeration e = fAccountArray.elements();
    while (e.hasMoreElements()) {
      UAccount account = (UAccount) e.nextElement();
      UBalance balance = account.getTodayBalance();
      long profit = calculateProfit(account, tomorrowSpotPrice);
      long unrealizedProfit = account.getTodayBalance().getUnrealizedProfit();
      /*
       System.out.println("ID: " + account.getUserID() + ", Final profit = " +
                         (profit + unrealizedProfit));
       */
      balance.setProfit(profit + unrealizedProfit);
      balance.setUnrealizedProfit(0);
      fExchangeAccount.addCash( -profit);
      payFeeToExchange(account,
                       Math.abs(getTotalSellPosition(account) -
                                getTotalBuyPosition(account)));
      returnMarginToMember(account);
      account.clearPosition();
    }
    fExchangeAccount.clearPosition();
  }

  /**
   * 前日の終値を更新します．ただし, 当日の最終の板寄せで取引きが
   * 成立せずに終値がつかなかった場合は値がついている直近の約定価格を
   * 前日の終値とします．
   */
  public void updateYesterdayClosingPrice() {
    for (int i = fNoOfBoardsPerDay; i >= 1; --i) {
      if (fContractedPrice[i - 1] >= 0) {
        fYesterdayClosingPrice = fContractedPrice[i - 1];
        return;
      }
    }
  }

  /**
   * 約定価格を設定します．registerContractメソッドを呼び出す前に,
   * 必ず一度呼び出す必要があります．
   * @param price 約定価格
   * @param boards 板寄せ回数
   */
  public void setContractedPrice(long price, int boards) {
    fContractedPrice[boards - 1] = price;
    if (price >= 0) {
      fLatestContractedPrice = price;
    }
  }

  /**
   * 金利を引数で指定されたユーザーから取引所へ支払います．
   * このメソッドはmarkToMarketメソッド内から呼び出されます．
   * @param account 対象となる口座
   * @param todayInterest 支払われる金利
   */
  private void payInterestFromMemberToExchange(UAccount account,
                                               long todayInterest) {
    UBalance balance = account.getTodayBalance();
    balance.setSumOfInterest(balance.getSumOfInterest() + todayInterest);
    fExchangeAccount.addCash(todayInterest);
  }

  /**
   * 未実現損益profitDiffを取引所から引数で指定された一般ユーザーの
   * 口座へ移動する。markToMarketメソッドから呼び出されます．
   * @param account 対象となる口座
   * @param profitDiff 移動される未実現損益
   */
  private void moveUnrealizedProfitFromExchangeToMember(
      UAccount account,
      long profitDiff) {
    UBalance balance = account.getTodayBalance();
    long newUnrealizedProfit = profitDiff + balance.getUnrealizedProfit();
    balance.setUnrealizedProfit(newUnrealizedProfit);
    fExchangeAccount.addCash( -profitDiff);
    balance.updateCash();
  }

  /**
   * 引数で指定された口座に関して, 当日分の金利を計算します．
   * @param account 対象となる口座
   * @return 当日分の金利
   */
  private long calculateTodayInterest(UAccount account) {
    return (long) (account.getTodayBalance().getLoan() * account.getInterest());
  }

  /**
   * 証拠金を取引所から引数で指定された口座に戻します．
   * @param account 対象となる口座
   */
  private void returnMarginToMember(UAccount account) {
    UBalance balance = account.getTodayBalance();
    long margin = balance.getMargin();
    balance.setMargin(0);
    fExchangeAccount.addCash( -margin);
  }

  /**
   * 引数で指定された口座から証拠金を取引所へ支払します．
   * @param account 対象となる口座
   */
  private void payMarginToExchange(UAccount account) {
    UBalance balance = account.getTodayBalance();
    long margin = calculateMargin(account);
    balance.setMargin(margin);
    fExchangeAccount.addCash(margin);
  }

  /**
   * 引数で指定された口座の証拠金を計算します．
   * @param account 対象となる口座
   * @return 証拠金
   */
  private long calculateMargin(UAccount account) {
    return account.getMarginRate() *
        Math.abs(getTotalSellPosition(account) - getTotalBuyPosition(account));
  }

  /**
   * 引数で指定された口座から取引所へvolume分の手数料を支払います．
   * @param account 対象となる口座
   * @param volume 取引量
   */
  private void payFeeToExchange(UAccount account, long volume) {
    UBalance balance = account.getTodayBalance();
    long fee = account.getFeePerUnit() * volume;
    fExchangeAccount.addCash(fee);
    fee += balance.getSumOfFee();
    balance.setSumOfFee(fee);
  }

  /**
   * 引数で指定された口座について取引所からvalueだけ借入れます．
   * ただし, 借入額の合計は借入れ限度額を越えることはできません．
   * @param account 対象となる口座
   * @param value 借入額
   */
  private void borrowLoanFromExchange(UAccount account, long value) {
    UBalance balance = account.getTodayBalance();
    long totalLoan = balance.getLoan();
    if ( (totalLoan + value) > account.getMaxLoan()) {
      value = account.getMaxLoan() - totalLoan;
    }
    totalLoan += value;
    balance.setLoan(totalLoan);
    balance.updateCash();
    fExchangeAccount.addCash( -value);
  }

  /**
   * 引数で指定された口座から取引所へvalueだけ返済します．
   * @param account 対象となる口座
   * @param value 返済額
   */
  private void repayLoanToExchange(UAccount account, long value) {
    UBalance balance = account.getTodayBalance();
    long totalLoan = balance.getLoan();
    if (0 >= totalLoan) {
      System.err.println("NoLoan error! \n");
      System.exit(5);
    }
    totalLoan -= value;
    balance.setLoan(totalLoan);
    balance.updateCash();
    fExchangeAccount.addCash(value);
  }

  /**
   * 引数で指定された口座について, 所持金がマイナスならば
   * borrowLoanFromExchangeメソッドを呼び出すことにより所持金が
   * ゼロになるようにし, 所持金がプラスで借入金があるならば
   * repayLoanToExchangeメソッドを呼び出すことにより所持金の許すかぎり
   * 借入金をゼロにするように取引所に返済します．
   * @param account 対象となる口座
   */
  private void checkLoan(UAccount account) {
    UBalance balance = account.getTodayBalance();
    balance.updateCash();
    long cash = balance.getCash();
    if (0 > cash) {
      long loanValue = Math.min(account.getMaxLoan(), Math.abs(cash));
      borrowLoanFromExchange(account, loanValue);
    } else if (0 < balance.getLoan()) {
      long repayValue = Math.min(cash, balance.getLoan());
      repayLoanToExchange(account, repayValue);
    }
  }

  /**
   * 引数で指定された口座について破産処理を行います．
   * 具体的には, 取引き停止にした後, 保有ポジションを取引所へ移動, 破産
   * したユーザーの口座を格納しているベクタに登録します．
   * @param account 対象となる口座
   */
  private void bankrupt(UAccount account) {
    System.out.println("<<<<<<<< Member" + account.getUserID() +
                       " is bankrupted!!! >>>>>>>>>");
    // UBalance balance = account.getTodayBalance();
    account.setStatus(UAccount.UNAVAILABLE);
    fExchangeAccount.addSellPosition(getTotalSellPosition(account));
    fExchangeAccount.addBuyPosition(getTotalBuyPosition(account));
    account.clearPosition();
    fBankruptedAccountArray.addElement(account);
  }

  /**
   * 引数で指定された口座について破産しているかどうか調べ,
   * 必要ならばbankruptメソッドを呼び出すことにより破産処理を行います。
   * @param account 対象となる口座
   */
  private void checkBankrupt(UAccount account) {
    UBalance balance = account.getTodayBalance();
    balance.updateCash();
    long cash = balance.getCash();
    if (0 > cash) {
      bankrupt(account);
    }
  }

  /**
   * 引数で指定された口座の総売りポジションを返します．
   * @param account 対象となる口座
   * @return 総売りポジション
   */
  private long getTotalSellPosition(UAccount account) {
    UPosition userPosition = account.getPosition();
    long totalSellPosition = userPosition.getSumOfSellPositionUntilYesterday();
    for (int i = 1; i <= userPosition.getNoOfSessionsPerDay(); i++) {
      totalSellPosition += userPosition.getTodaySellPosition(i);
    }
    return totalSellPosition;
  }

  /**
   * 引数で指定された口座の総買いポジションを返します．
   * @param account 対象となる口座
   * @return 総買いポジション
   */
  private long getTotalBuyPosition(UAccount account) {
    UPosition userPosition = account.getPosition();
    long totalBuyPosition = userPosition.getSumOfBuyPositionUntilYesterday();
    for (int i = 1; i <= userPosition.getNoOfSessionsPerDay(); i++) {
      totalBuyPosition += userPosition.getTodayBuyPosition(i);
    }
    return totalBuyPosition;
  }

  /**
   * 引数で指定された口座について未実現損益を計算します．
   * @param account 対象となる口座
   * @param closingPrice 終値
   * @return 未実現損益
   */
  private long calculateUnrealizedProfit(UAccount account, long closingPrice) {
    UPosition userPosition = account.getPosition();
    long value_a = userPosition.getSumOfBuyPositionUntilYesterday()
        * (closingPrice - fYesterdayClosingPrice)
        - userPosition.getSumOfSellPositionUntilYesterday()
        * (closingPrice - fYesterdayClosingPrice);
    long value_b = 0;
    for (int i = 1; i <= userPosition.getNoOfSessionsPerDay(); i++) {
      value_b += userPosition.getTodayBuyPosition(i)
          * (closingPrice - fContractedPrice[i - 1])
          - userPosition.getTodaySellPosition(i)
          * (closingPrice - fContractedPrice[i - 1]);
    }
    return account.getTradingUnit() * (value_a + value_b);
  }

  /**
   * 引数で指定された口座について実現損益を計算します．
   * @param account 対象となる口座
   * @param settlementPrice 最終決済価格
   * @return 実現損益
   */
  private long calculateProfit(UAccount account, long settlementPrice) {
    UPosition userPosition = account.getPosition();
    long value_a = userPosition.getSumOfBuyPositionUntilYesterday()
        * (settlementPrice - fYesterdayClosingPrice)
        - userPosition.getSumOfSellPositionUntilYesterday()
        * (settlementPrice - fYesterdayClosingPrice);
    long profit = account.getTradingUnit() * value_a;
    return profit;
  }

  /**
   * テスト用のメインメソッドです．
   */
  public static void main(String args[]) {
    try {
      FileWriter fw = new FileWriter("test01");
      BufferedWriter bw = new BufferedWriter(fw);
      PrintWriter w = new PrintWriter(bw);
      UMemberList members = new UMemberList();
      members.appendMember("member1", "passwd1", "Machine", "Local",
                           new ArrayList(),
                           "member1", new ArrayList(), 1, 1000000000,
                           1000, 10000, 300000, 30000000, 0.1);
      members.appendMember("member2", "passwd2", "Machine", "Local",
                           new ArrayList(),
                           "member2", new ArrayList(), 1, 1000000000,
                           1000, 10000, 300000, 30000000, 0.1);
      members.appendMember("member3", "passwd3", "Machine", "Local",
                           new ArrayList(),
                           "member3", new ArrayList(), 1, 1000000000,
                           1000, 10000, 300000, 30000000, 0.1);
      UAccountManager uam = new UAccountManager(members, 4);
      Random r = new Random();
      uam.checkConsistency();
      for (int day = 1; day <= 120; ++day) {
        for (int board = 1; board <= 4; ++board) {
          System.out.println("****** Day " + day + ", Board " + board +
                             " *****");
          w.write("****** Day " + day + ", Board " + board + " *****\n");
          long price = 3000 + (Math.abs(r.nextInt()) % 500) - 250;
          uam.setContractedPrice(price, board);
          boolean flag = false;
          for (int i = 0; i < 10; ++i) {
            int id1 = Math.abs(r.nextInt()) % 3 + 1;
            int id2 = Math.abs(r.nextInt()) % 3 + 1;
            while (id1 == id2) {
              id2 = Math.abs(r.nextInt()) % 3 + 1;
            }
            UAccount a1 = uam.getAccount(id1);
            if (a1 == null) {
              System.err.println("Can't find account a1:" + id1);
              System.exit(5);
            }
            UAccount a2 = uam.getAccount(id2);
            if (a2 == null) {
              System.err.println("Can't find account a2:" + id2);
              System.exit(5);
            }
            if (a1.getStatus() == UAccount.AVAILABLE
                && a2.getStatus() == UAccount.AVAILABLE) {
              long volume = Math.abs(r.nextInt()) % 100 + 1;
              uam.registerContract(id1, UOrder.SELL, volume, board);
              uam.registerContract(id2, UOrder.BUY, volume, board);
              flag = true;
            }
          }
          if (!flag) {
            uam.setContractedPrice( -1, board);
          }
          uam.checkBankruptForAllAccounts();
          uam.checkConsistency();
          uam.printOn(w);
        }
        System.out.println("****** Day " + day + ", Marking to market ******");
        w.write("****** Day " + day + ", Marking to market *****\n");
        uam.markToMarket();
        uam.checkConsistency();
        uam.printOn(w);
        uam.updateYesterdayClosingPrice();
      }
      System.out.println("****** Settlement ******");
      w.write("****** Settlement ******\n");
      uam.settlement(3200);
      uam.checkConsistency();
      uam.printOn(w);
      fw.close();
      System.out.println("******** TEST END ********\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
