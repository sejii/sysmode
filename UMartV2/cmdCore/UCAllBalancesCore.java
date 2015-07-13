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
 * すべての参加者の現金残高を照会するためのSVMPコマンドの抽象クラスです．
 */
public abstract class UCAllBalancesCore implements ICommand {

  /** エージェント名を引くためのキー(値はStringオブジェクト) */
  public static final String STRING_NAME = "STRING_NAME";

  /** 現金残高を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_CASH = "LONG_CASH";

  /** 証拠金額を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_MARGIN = "LONG_MARGIN";

  /** 未実現損益を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_UNREALIZED_PROFIT =
      "LONG_UNREALIZED_PROFIT";

  /** 実現利益を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_SETTLED_PROFIT = "LONG_SETTLED_PROFIT";

  /** 会費を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_FEE = "LONG_FEE";

  /** 融資利息を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_INTEREST = "LONG_INTEREST";

  /** 取引所融資額を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_LOAN = "LONG_LOAN";

  /** 余裕金額を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_SURPLUS = "LONG_SURPLUS";

  /** 昨日までの売り建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_SELL_POSITION = "LONG_SELL_POSITION";

  /** 昨日までの買い建玉数を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_BUY_POSITION = "LONG_BUY_POSITION";

  /** コマンド名 */
  public static final String CMD_NAME = "AllBalances";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのArrayList */
  protected ArrayList fAllBalancesArray;

  /**
   * コンストラクタです．
   */
  public UCAllBalancesCore() {
    super();
    fAllBalancesArray = new ArrayList();
    fStatus = new UCommandStatus();
  }

  /**
   * @see cmdCore.ICommand#isNameEqualTo(String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @see cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /**
   * @see cmdCore.ICommand#setArguments(StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    return true;
  }

  /**
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    Iterator itr = fAllBalancesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(STRING_NAME) + " "
          + os.get(LONG_CASH) + " "
          + os.get(LONG_MARGIN) + " "
          + os.get(LONG_UNREALIZED_PROFIT) + " "
          + os.get(LONG_SETTLED_PROFIT) + " "
          + os.get(LONG_FEE) + " "
          + os.get(LONG_INTEREST) + " "
          + os.get(LONG_LOAN) + " "
          + os.get(LONG_SURPLUS) + " "
          + os.get(LONG_SELL_POSITION) + " "
          + os.get(LONG_BUY_POSITION);
    }
    return result;
  }

  /**
   * すべての参加者の現金残高を返す．
   * @return すべての参加者の現金残高
   */
  public ArrayList getResults() {
    return fAllBalancesArray;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<AllBalances>>");
    Iterator itr = fAllBalancesArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("Name:" + os.get(STRING_NAME) + ","
                         + "Cash:" + os.get(LONG_CASH) + ","
                         + "Margin:" + os.get(LONG_MARGIN) + ","
                         + "UnrealizedProfit:" + os.get(LONG_UNREALIZED_PROFIT) +
                         ","
                         + "SettledProfit:" + os.get(LONG_SETTLED_PROFIT) + ","
                         + "Fee:" + os.get(LONG_FEE) + ","
                         + "Interest:" + os.get(LONG_INTEREST) + ","
                         + "Loan:" + os.get(LONG_LOAN) + ","
                         + "Surplus:" + os.get(LONG_SURPLUS) + ","
                         + "SellPosition:"
                         + os.get(LONG_SELL_POSITION) + ","
                         + "BuyPosition:" + os.get(LONG_BUY_POSITION));
    }
  }
}
