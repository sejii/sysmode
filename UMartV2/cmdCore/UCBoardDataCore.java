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
 * 取引開始時から現在までの板情報を取得するためのSVMPコマンドの抽象クラスです．
 */
public abstract class UCBoardDataCore implements ICommand {

  /** 買い注文数の合計を引くためのキー(実体はLongオブジェクト) */
  public static final String LONG_TOTAL_BUY_VOLUME = "LONG_TOTAL_BUY_VOLUME";

  /** 最低の注文価格を引くためのキー(実体はLongオブジェクト) */
  public static final String LONG_MIN_PRICE = "LONG_MIN_PRICE";

  /** 最高の注文価格を引くためのキー(実体はLongオブジェクト) */
  public static final String LONG_MAX_PRICE = "LONG_MAX_PRICE";

  /** 約定価格を引くためのキー(実体はLongオブジェクト) */
  public static final String LONG_CONTRACT_PRICE = "LONG_CONTRACT_PRICE";

  /** 約定数量を引くためのキー(実体はLongオブジェクト) */
  public static final String LONG_CONTRACT_VOLUME = "LONG_CONTRACT_VOLUME";

  /** 価格を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_PRICE = "LONG_PRICE";

  /** 売り買いを引くためのキー(値はIntegerオブジェクト) */
  public static final String STRING_SELL_BUY = "STRING_SELL_BUY";

  /** 注文数量を引くためのキー(値はLongオブジェクト) */
  public static final String LONG_VOLUME = "LONG_VOLUME";

  /** コマンド名 */
  public static final String CMD_NAME = "BoardData";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 結果を格納するためのArrayList */
  protected ArrayList fBoardDataArray;

  /** 結果を格納するためのHashMap */
  protected HashMap fBoardDataInfo;

  /**
   * コンストラクタです．
   */
  public UCBoardDataCore() {
    fBoardDataArray = new ArrayList();
    fBoardDataInfo = new HashMap();
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
    result += fBoardDataInfo.get(LONG_TOTAL_BUY_VOLUME) + " "
              + fBoardDataInfo.get(LONG_MIN_PRICE) + " "
              + fBoardDataInfo.get(LONG_MAX_PRICE) + " "
              + fBoardDataInfo.get(LONG_CONTRACT_PRICE) + " "
              + fBoardDataInfo.get(LONG_CONTRACT_VOLUME) + " ";
    Iterator itr = fBoardDataArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      result += os.get(LONG_PRICE) + " " + os.get(STRING_SELL_BUY)
                 + " " + os.get(LONG_VOLUME) + " ";
    }
    return result;
  }

  /**
   * 約定情報(ArrayList)を返します．
   * @return 約定情報
   */
  public ArrayList getBoardDataArray() {
    return fBoardDataArray;
  }

  /**
   * 約定情報(HashMap)を返します．
   * @return 約定情報
   */
  public HashMap getBoardDataInfo() {
    return fBoardDataInfo;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<BoardData>>");
    System.out.println("TotalBuyVolume:" + fBoardDataInfo.get(LONG_TOTAL_BUY_VOLUME) + ","
                        + "MinPrice:" + fBoardDataInfo.get(LONG_MIN_PRICE) + ","
                        + "MaxPrice:" + fBoardDataInfo.get(LONG_MAX_PRICE) + ","
                        + "ContractPrice:" + fBoardDataInfo.get(LONG_CONTRACT_PRICE) + ","
                        + "ContractVolume:" + fBoardDataInfo.get(LONG_CONTRACT_VOLUME));
    Iterator itr = fBoardDataArray.iterator();
    while (itr.hasNext()) {
      HashMap os = (HashMap) itr.next();
      System.out.println("Price:" + os.get(LONG_PRICE) + "," 
      		               + "SellBuy:" + os.get(STRING_SELL_BUY) + ","
                         + "Volume:" + os.get(LONG_VOLUME));
    }
  }
}
