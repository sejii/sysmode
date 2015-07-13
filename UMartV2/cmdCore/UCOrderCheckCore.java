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
 * 注文情報を取得するためのSVMPコマンドの抽象クラスです．
 */

public abstract class UCOrderCheckCore implements ICommand {

  /** コマンド名 */
  public static final String CMD_NAME = "OrderCheck";

  /** コマンドの実行結果の状態 */
  protected UCommandStatus fCommandStatus;

  /** ユーザーIDを引くためのキー */
  public static final String INT_USER_ID = "INT_USER_ID";

  /** 銘柄名を引くためのキー */
  public static final String STRING_BRAND_NAME = "STRING_BRAND_NAME";

  /** 注文IDを引くためのキー */
  public static final String LONG_ORDER_ID = "LONG_ORDER_ID";

  /** 注文時刻（実時間）を引くためのキー */
  public static final String STRING_ORDER_TIME = "STRING_ORDER_TIME";

  /** 注文日を引くためのキー */
  public static final String INT_ORDER_DATE = "INT_ORDER_DATE";

  /** 注文板寄せ回数を引くためのキー */
  public static final String INT_ORDER_BOARD_NO = "INT_ORDER_BOARD_NO";

  /** 売買区分を引くためのキー */
  public static final String INT_SELL_BUY = "INT_SELL_BUY";

  /** 成行指値区分を引くためのキー */
  public static final String INT_MARKET_LIMIT = "INT_MARKET_LIMIT";

  /** 新規返済区分を引くためのキー */
  public static final String INT_NEW_REPAY = "INT_NEW_REPAY";

  /** 注文価格を引くためのキー */
  public static final String LONG_ORDER_PRICE = "LONG_ORDER_PRICE";

  /** 注文数量を引くためのキー */
  public static final String LONG_ORDER_VOLUME = "LONG_ORDER_VOLUME";

  /** 合計約定数量を引くためのキー */
  public static final String LONG_SUM_OF_CONTRACT_VOLUME =
      "LONG_SUM_OF_CONTRACT_VOLUME";

  /** 取消数量を引くためのキー */
  public static final String LONG_CANCEL_VOLUME = "LONG_CANCEL_VOLUME";

  /** 約定情報を引くためのキー　*/
  public static final String ARRAYLIST_CONTRACT_INFORMATION_ARRAY =
      "ARRAYLIST_CONTRACT_INFORMATION_ARRAY";

  /** 約定情報内の約定IDを引くためのキー */
  public static final String LONG_CONTRACT_ID = "LONG_CONTRACT_ID";

  /** 約定情報内の約定時刻（実時間）を引くためのキー */
  public static final String STRING_CONTRACT_TIME = "STRING_CONTRACT_TIME";

  /** 約定情報内の約定価格を引くためのキー */
  public static final String LONG_CONTRACT_PRICE = "LONG_CONTRACT_PRICE";

  /** 約定情報内の約定数量を引くためのキー */
  public static final String LONG_CONTRACT_VOLUME = "LONG_CONTRACT_VOLUME";

  /** 約定情報内の約定日を引くためのキー */
  public static final String INT_CONTRACT_DATE = "INT_CONTRACT_DATE";

  /** 約定情報内の約定板寄せ回数を引くためのキー */
  public static final String INT_CONTRACT_BOARD_NO = "INT_CONTRACT_BOARD_NO";

  /** 調べたい注文のID */
  protected long fOrderID;

  /** 注文IDに該当する注文情報 */
  protected HashMap fData;

  /**
   * コンストラクタです．
   */
  public UCOrderCheckCore() {
    super();
    fOrderID = -1;
    fCommandStatus = new UCommandStatus();
    fData = new HashMap();
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
   * @see cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String returnStr = "";
    returnStr += fData.get(UCOrderCheckCore.INT_USER_ID).toString() + "\n";
    returnStr += fData.get(UCOrderCheckCore.LONG_ORDER_ID).toString() + "\n";
    returnStr += fData.get(UCOrderCheckCore.STRING_ORDER_TIME).toString() +
        "\n";
    returnStr += fData.get(UCOrderCheckCore.STRING_BRAND_NAME).toString() +
        "\n";
    returnStr += fData.get(UCOrderCheckCore.INT_ORDER_DATE).toString() + "\n";
    returnStr += fData.get(UCOrderCheckCore.INT_ORDER_BOARD_NO).toString() +
        "\n";
    returnStr += fData.get(UCOrderCheckCore.INT_NEW_REPAY).toString() + "\n";
    returnStr += fData.get(UCOrderCheckCore.INT_SELL_BUY).toString() + "\n";
    returnStr += fData.get(UCOrderCheckCore.INT_MARKET_LIMIT).toString() + "\n";
    returnStr += fData.get(UCOrderCheckCore.LONG_ORDER_PRICE).toString() + "\n";
    returnStr += fData.get(UCOrderCheckCore.LONG_ORDER_VOLUME).toString() +
        "\n";
    returnStr += fData.get(UCOrderCheckCore.LONG_SUM_OF_CONTRACT_VOLUME).
        toString() + "\n";
    returnStr += fData.get(UCOrderCheckCore.LONG_CANCEL_VOLUME).toString() +
        "\n";
    ArrayList contractInfoArray = (ArrayList) fData.get(UCOrderCheckCore.
        ARRAYLIST_CONTRACT_INFORMATION_ARRAY);
    returnStr += contractInfoArray.size() + "\n";
    Iterator itr = contractInfoArray.iterator();
    while (itr.hasNext()) {
      HashMap contractInfo = (HashMap) itr.next();
      returnStr += contractInfo.get(UCOrderCheckCore.LONG_CONTRACT_ID).toString() +
          "\n";
      returnStr += contractInfo.get(UCOrderCheckCore.STRING_CONTRACT_TIME).
          toString() + "\n";
      returnStr += contractInfo.get(UCOrderCheckCore.INT_CONTRACT_DATE).
          toString() + "\n";
      returnStr += contractInfo.get(UCOrderCheckCore.INT_CONTRACT_BOARD_NO).
          toString() + "\n";
      returnStr += contractInfo.get(UCOrderCheckCore.LONG_CONTRACT_PRICE).
          toString() + "\n";
      returnStr += contractInfo.get(UCOrderCheckCore.LONG_CONTRACT_VOLUME).
          toString() + "\n";
    }
    return returnStr;
  }

  /**
   * @see cmdCore.ICommand#setArguments(StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    try {
      fOrderID = Long.parseLong(st.nextToken());
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 引数を設定します．
   * @param orderID 注文ID
   */
  public void setArguments(long orderID) {
    fOrderID = orderID;
  }

  /**
   * @see cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("UserID:" +
                       fData.get(UCOrderCheckCore.INT_USER_ID).toString());
    System.out.println("OrderID:" +
                       fData.get(UCOrderCheckCore.LONG_ORDER_ID).toString());
    System.out.println("OrderTime:" +
                       fData.get(UCOrderCheckCore.STRING_ORDER_TIME).toString());
    System.out.println("BrandName:" +
                       fData.get(UCOrderCheckCore.STRING_BRAND_NAME).toString());
    System.out.println("OrderDate:" +
                       fData.get(UCOrderCheckCore.INT_ORDER_DATE).toString());
    System.out.println("OrderBoardNo:" +
                       fData.get(UCOrderCheckCore.INT_ORDER_BOARD_NO).toString());
    System.out.println("NewRepay:" +
                       fData.get(UCOrderCheckCore.INT_NEW_REPAY).toString());
    System.out.println("SellBuy:" +
                       fData.get(UCOrderCheckCore.INT_SELL_BUY).toString());
    System.out.println("MarketLimit:" +
                       fData.get(UCOrderCheckCore.INT_MARKET_LIMIT).toString());
    System.out.println("OrderPrice:" +
                       fData.get(UCOrderCheckCore.LONG_ORDER_PRICE).toString());
    System.out.println("OrderVolume:" +
                       fData.get(UCOrderCheckCore.LONG_ORDER_VOLUME).toString());
    System.out.println("SumOfContractVolume:" +
                       fData.get(UCOrderCheckCore.LONG_SUM_OF_CONTRACT_VOLUME).
                       toString());
    System.out.println("CancelVolume:" +
                       fData.get(UCOrderCheckCore.LONG_CANCEL_VOLUME).toString());
    ArrayList contractInfoArray = (ArrayList) fData.get(UCOrderCheckCore.
        ARRAYLIST_CONTRACT_INFORMATION_ARRAY);
    System.out.println("NoOfContractInformation:" + contractInfoArray.size());
    if (contractInfoArray.size() > 0) {
      System.out.println("{");
      Iterator itr = contractInfoArray.iterator();
      while (itr.hasNext()) {
        HashMap contractInfo = (HashMap) itr.next();
        System.out.println("  ContractID:" +
                           contractInfo.get(UCOrderCheckCore.LONG_CONTRACT_ID).
                           toString());
        System.out.println("  ContarctTime:" +
                           contractInfo.get(UCOrderCheckCore.
                                            STRING_CONTRACT_TIME).toString());
        System.out.println("  ContarctDate:" +
                           contractInfo.get(UCOrderCheckCore.INT_CONTRACT_DATE).
                           toString());
        System.out.println("  ContractBoardNo:" +
                           contractInfo.get(UCOrderCheckCore.
                                            INT_CONTRACT_BOARD_NO).toString());
        System.out.println("  ContractPrice:" +
                           contractInfo.get(UCOrderCheckCore.
                                            LONG_CONTRACT_PRICE).toString());
        System.out.println("  ContractVolue:" +
                           contractInfo.get(UCOrderCheckCore.
                                            LONG_CONTRACT_VOLUME).toString());
      }
      System.out.println("}");
    }
  }

  /**
   * 注文IDに該当する注文情報を返します．
   * @return 注文IDに該当する注文情報
   */
  public HashMap getData() {
    return fData;
  }

}
