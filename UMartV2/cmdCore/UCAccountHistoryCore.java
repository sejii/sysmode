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
 * 過去の口座情報を問い合わせるSVMPコマンドの抽象クラスです．
 */
public abstract class UCAccountHistoryCore implements ICommand {

  /** コマンド名 */
  public static final String CMD_NAME = "AccountHistory";

  /** コマンドへの引数：対象ユーザーのユーザーID（-1：自分自身） */
  protected int fTargetUserId;

  /** コマンドへの引数：過去何日分の情報が必要か？（-1：全ての情報） */
  protected long fNoOfDays;

  /** コマンドの実行結果の状態 */
  protected UCommandStatus fCommandStatus;

  /** 過去の口座情報の配列 */
  protected ArrayList fArray;

  /** 口座情報を引くためのキー */
  public static final String HASHMAP_DATA = "HASHMAP_DATA";

  /** ユーザー名を引くためのキー */
  public static final String STRING_NAME = "STRING_NAME";

  /** ユーザーIDを引くためのキー */
  public static final String INT_USER_ID = "INT_USER_ID";

  /** ステップ数を引くためのキー */
  public static final String INT_STEP = "INT_STEP";

  /** 未実現利益を引くためのキー */
  public static final String LONG_UNREALIZED_PROFIT = "LONG_UNREALIZED_PROFIT";

  /** 買ポジションの合計を引くためのキー */
  public static final String LONG_SELL_POSITION = "LONG_SELL_POSITION";

  /** 売ポジションの合計を引くためのキー */
  public static final String LONG_BUY_POSITION = "LONG_BUY_POSITION";

  /**
   * コンストラクタです．
   */
  public UCAccountHistoryCore() {
    super();
    fCommandStatus = new UCommandStatus();
    fArray = new ArrayList();
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
      fNoOfDays = Long.parseLong(st.nextToken());
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * コマンドを実行するために必要が引数を設定する．
   * @param targetUserId ユーザID
   * @param noOfDays 何日分の情報が必要か？
   */
  public void setArguments(int targetUserId, long noOfDays) {
    fTargetUserId = targetUserId;
    fNoOfDays = noOfDays;
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println(fArray.toString());
  }

  /**
   * コマンドを実行した結果である過去の口座情報(HashMap型)が格納された配列を返す．
   * @return 過去の口座情報(HashMap型)が格納された配列
   */
  public ArrayList getData() {
    return fArray;
  }

  /*
   * (non-Javadoc)
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String returnStr = "";
    Iterator fArrayItr = fArray.iterator();
    while (fArrayItr.hasNext()) {
      HashMap data = (HashMap) fArrayItr.next();
      returnStr += data.get(UCAccountHistoryCore.STRING_NAME).toString() + "\n";
      returnStr += data.get(UCAccountHistoryCore.INT_USER_ID).toString() + "\n";
      returnStr += data.get(UCAccountHistoryCore.INT_STEP).toString() + "\n";
      returnStr += data.get(UCAccountHistoryCore.LONG_UNREALIZED_PROFIT).toString() + "\n";
      returnStr += data.get(UCAccountHistoryCore.LONG_SELL_POSITION).toString() + "\n";
      returnStr += data.get(UCAccountHistoryCore.LONG_BUY_POSITION).toString() + "\n";
    }
    return returnStr;
  }

}
