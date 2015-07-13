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
package cmdCore;

import java.util.*;

/**
 * 会員情報を取得するためのSVMPコマンドの抽象クラスです．
 */
public abstract class UCMemberProfileCore implements ICommand {

  /** コマンド名 */
  public static final String CMD_NAME = "MemberProfile";

  /** コマンドへの引数：対象会員のID(-1の場合，自分自身) */
  protected int fTargetUserId;

  /** コマンドの実行結果の状態 */
  protected UCommandStatus fCommandStatus;

  /** 会員情報 */
  protected HashMap fData;

  /** ログイン名を引くためのキー */
  public static final String STRING_LOGIN_NAME = "STRING_LOGIN_NAME";

  /** パスワードを引くためのキー */
  public static final String STRING_PASSWORD = "STRING_PASSWORD";

  /** エージェント属性(Human or Machine)を引くためのキー */
  public static final String STRING_ATTRIBUTE = "STRING_ATTRIBUTE";

  /** コネクション(Remote or Local)を引くためのキー */
  public static final String STRING_CONNECTION = "STRING_CONNECTION";

  /** アクセス制限を引くためのキー */
  public static final String ARRAY_LIST_ACCESS = "ARRAY_LIST_ACCESS";

  /** 実際の名前を引くためのキー */
  public static final String STRING_REAL_NAME = "STRING_REAL_NAME";

  /** システムパラメータを格納したArrayListを引くためのキー */
  public static final String ARRAY_LIST_SYSTEM_PARAMETERS =
      "ARRAY_LIST_SYSTEM_PARAMETERS";

  /** 乱数の種を引くためのキー */
  public static final String INT_SEED = "INT_SEED";

  /** 初期資産を引くためのキー */
  public static final String LONG_INITIAL_CASH = "LONG_INITIAL_CASH";

  /** 取引単位を引くためのキー */
  public static final String LONG_TRADING_UNIT = "LONG_TRADING_UNIT";

  /** 単位取引あたりの手数料を引くためのキー */
  public static final String LONG_FEE_PER_UNIT = "LONG_FEE_PER_UNIT";

  /** 証拠金率を引くためのキー */
  public static final String LONG_MARGIN_RATE = "LONG_MARGIN_RATE";

  /** 借り入れ限度額を引くためのキー */
  public static final String LONG_MAX_LOAN = "LONG_MAX_LOAN";

  /** 借り入れ金利を引くためのキー */
  public static final String DOUBLE_INTEREST = "DOUBLE_INTEREST";

  /** 取引可能(+1),取引不可能(-1)を引くためのキー */
  public static final String INT_STATUS = "INT_STATUS";

  /** ログインしているエージェントの数 */
  public static final String INT_NO_OF_LOGIN_AGENTS = "INT_NO_OF_LOGIN_AGENTS";

  /** 前日収支を引くためのキー */
  public static final String HASHMAP_YESTERDAY_BALANCE =
      "HASHMAP_YESTERDAY_BALANCE";

  /** 当日収支を引くためのキー */
  public static final String HASHMAP_TODAY_BALANCE = "HASHMAP_TODAY_BALANCE";

  /** 借入金を引くためのキー */
  public static final String LONG_LOAN = "LONG_LOAN";

  /** 未実現損益を引くためのキー */
  public static final String LONG_UNREALIZED_PROFIT = "LONG_UNREALIZED_PROFIT";

  /** 預託証拠金を引くためのキー */
  public static final String LONG_MARGIN = "LONG_MARGIN";

  /** 総支払い手数料を引くためのキー */
  public static final String LONG_SUM_OF_FEE = "LONG_SUM_OF_FEE";

  /** 総支払い金利を引くためのキー */
  public static final String LONG_SUM_OF_INTEREST = "LONG_SUM_OF_INTEREST";

  /** 保有現金を引くためのキー */
  public static final String LONG_CASH = "LONG_CASH";

  /** 実現損益を引くためのキー */
  public static final String LONG_PROFIT = "LONG_PROFIT";

  /** ポジションを引くためのキー */
  public static final String HASHMAP_POSITION = "HASHMAP_POSITION";

  /** 前日までの売ポジションの合計を引くためのキー */
  public static final String LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY =
      "LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY";

  /** 前日までの買ポジションの合計を引くためのキー */
  public static final String LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY =
      "LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY";

  /** 当日の売ポジションの合計を引くためのキー */
  public static final String LONG_TODAY_SELL_POSITIONS =
      "LONG_TODAY_SELL_POSITIONS";

  /** 当日の買ポジションの合計を引くためのキー */
  public static final String LONG_TODAY_BUY_POSITIONS =
      "LONG_TODAY_BUY_POSITIONS";

  /**
   * コンストラクタです．
   */
  public UCMemberProfileCore() {
    super();
    fCommandStatus = new UCommandStatus();
    fData = new HashMap();
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#isNameEqualTo(java.lang.String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME)) {
      return true;
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#setArguments(java.util.StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    try {
      fTargetUserId = Integer.parseInt(st.nextToken());
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 引数を設定します．
   * @param targetUserId ユーザID
   */
  public void setArguments(int targetUserId) {
    fTargetUserId = targetUserId;
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println(fData.toString());
  }

  /**
   * 取得した会員情報を返します．
   * @return 会員情報
   */
  public HashMap getData() {
    return fData;
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String returnStr = "";
    returnStr += fData.get(UCMemberProfileCore.STRING_LOGIN_NAME).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.STRING_PASSWORD).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.STRING_ATTRIBUTE).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.STRING_CONNECTION).toString() +
        "\n";
    returnStr +=
        arrayListToString( (ArrayList) fData.get(UCMemberProfileCore.ARRAY_LIST_ACCESS)) +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.STRING_REAL_NAME).toString() +
        "\n";
    returnStr +=
        arrayListToString( (ArrayList) fData.get(UCMemberProfileCore.ARRAY_LIST_SYSTEM_PARAMETERS)) +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.INT_SEED).toString() + "\n";
    returnStr += fData.get(UCMemberProfileCore.LONG_INITIAL_CASH).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.LONG_TRADING_UNIT).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.LONG_FEE_PER_UNIT).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.LONG_MARGIN_RATE).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.LONG_MAX_LOAN).toString() + "\n";
    returnStr += fData.get(UCMemberProfileCore.DOUBLE_INTEREST).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.INT_STATUS).toString() + "\n";
    returnStr += fData.get(UCMemberProfileCore.INT_NO_OF_LOGIN_AGENTS).toString() +
        "\n";
    HashMap yesterdayBalance = (HashMap) fData.get(UCMemberProfileCore.
        HASHMAP_YESTERDAY_BALANCE);
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_INITIAL_CASH).
        toString() + "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_LOAN).toString() +
        "\n";
    returnStr +=
        yesterdayBalance.get(UCMemberProfileCore.LONG_UNREALIZED_PROFIT).
        toString() + "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_MARGIN).toString() +
        "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_SUM_OF_FEE).
        toString() + "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_SUM_OF_INTEREST).
        toString() + "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_CASH).toString() +
        "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_PROFIT).toString() +
        "\n";
    HashMap todayBalance = (HashMap) fData.get(UCMemberProfileCore.
                                               HASHMAP_TODAY_BALANCE);
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_INITIAL_CASH).
        toString() + "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_LOAN).toString() +
        "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_UNREALIZED_PROFIT).
        toString() + "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_MARGIN).toString() +
        "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_SUM_OF_FEE).toString() +
        "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_SUM_OF_INTEREST).
        toString() + "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_CASH).toString() +
        "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_PROFIT).toString() +
        "\n";
    HashMap position = (HashMap) fData.get(UCMemberProfileCore.HASHMAP_POSITION);
    returnStr +=
        position.get(UCMemberProfileCore.LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY).
        toString() + "\n";
    returnStr +=
        position.get(UCMemberProfileCore.LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY).
        toString() + "\n";
    returnStr += position.get(UCMemberProfileCore.LONG_TODAY_SELL_POSITIONS).
        toString() + "\n";
    returnStr += position.get(UCMemberProfileCore.LONG_TODAY_BUY_POSITIONS).
        toString() + "\n";
    return returnStr;
  }

  /**
   * ArrayList型の情報をString（コロン区切り）にして返します．
   * @param list ArrayList型の情報
   * @return　ArrayList型の情報をString（コロン区切り）にした文字列
   */
  static public String arrayListToString(ArrayList list) {
    String result = "";
    Iterator itr = list.iterator();
    while (itr.hasNext()) {
      result += (String) itr.next();
      if (itr.hasNext()) {
        result += ":";
      }
    }
    return result;
  }

  /**
   * String（コロン区切り）の情報をArrayList型にして返します．
   * @param str コロン区切りの情報
   * @return　tring（コロン区切り）の情報をArrayList型にした結果
   */
  static public ArrayList stringToArrayList(String str) {
    ArrayList result = new ArrayList();
    StringTokenizer st = new StringTokenizer(str, ":");
    while (st.hasMoreTokens()) {
      String parameter = st.nextToken();
      result.add(parameter);
    }
    return result;
  }

}
