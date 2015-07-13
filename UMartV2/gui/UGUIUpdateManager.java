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

import java.util.*;

import cmdCore.*;
import serverCore.*;

/**
 * 時間を管理するユーティリティクラス．
 * 基本的に各コンポーネントはステップか日付で更新される．注文関係は例外．
 * このクラスは定期的にサーバに時間を問い合わせ，時間に変更があった場合には登録されている
 * 関連オブザーバに通知する．
 */

public class UGUIUpdateManager {

  protected static int fCurrentDate = -1;
  protected static int fDate = -1;
  protected static int fBoard = -1;
  protected static int fCurrentStep = -1;
  protected static int fStep = -1;
  protected static int fCurrentMarketStatus = UServerStatus.BEFORE_TRADING;
  protected static int fMarketStatus = UServerStatus.BEFORE_TRADING;
  protected static Vector fDayObservers = new Vector();
  protected static Vector fStepObservers = new Vector();
  protected static Vector fAlwaysObservers = new Vector();
  protected static Vector fFinalObservers = new Vector(); //最後に一度だけ
  protected static HashMap fTimeInfo = new HashMap();
  protected static UProtocolCore fCProtocol;
  protected static UParameters fParam = UParameters.getInstance();

  private UGUIUpdateManager() {
  } // new を禁止

  public static synchronized void update() {
    IGUIEvent iev = new IGUIEvent() {
      public void execute() {
        UCServerDateCore cServerDate = (UCServerDateCore) fCProtocol.getCommand(
            UCServerDateCore.CMD_NAME);
        UCMarketStatusCore ucmStatus = (UCMarketStatusCore) fCProtocol.
            getCommand(UCMarketStatusCore.CMD_NAME);
        cServerDate.doIt();
        ucmStatus.doIt();
        fTimeInfo = cServerDate.getResults();
        if (fTimeInfo.isEmpty()) {
          return;
        }
        fMarketStatus = ( (Integer) ucmStatus.getMarketInfo().get(
            UCMarketStatusCore.INT_MARKET_STATUS)).intValue();
        fDate = ( (Integer) fTimeInfo.get(UCServerDateCore.INT_DAY)).intValue();
        fBoard = ( (Integer) fTimeInfo.get(UCServerDateCore.INT_BOARD_NO)).
            intValue();
        fStep = (fDate - 1) * fParam.getBoardPerDay() + fBoard;
        if (fDate > fCurrentDate
            || (fMarketStatus == UServerStatus.AFTER_MARKING_TO_MARKET
                && fCurrentMarketStatus == UServerStatus.ACCEPT_ORDERS)) {
          fCurrentDate = fDate;
          //fCurrentMarketStatus = fMarketStatus; 下の step で変わるのでここで変える必要なし。
          notifyDayObservers();
        }
        if (fStep > fCurrentStep
            || (fMarketStatus == UServerStatus.AFTER_MARKING_TO_MARKET
                && fCurrentMarketStatus == UServerStatus.ACCEPT_ORDERS)) {
          fCurrentStep = fStep;
          fCurrentMarketStatus = fMarketStatus;
          notifyStepObservers();
        }
        if (fMarketStatus == UServerStatus.AFTER_SETTLEMENT) {
          notifyFinalObservers();
        }
        notifyAlwaysObservers();
      }

      public Runnable getRunnableForInvokeLater() {
        return new Runnable() {
          public void run() {
          }
        };
      }
    };
    UGUIEventQueue.getInstance().addIGUIEventToTop(iev);
  }

  public static synchronized void addDayObserver(IGUIObserver o) {
    fDayObservers.add(o);
  }

  public static synchronized void addStepObserver(IGUIObserver o) {
    fStepObservers.add(o);
  }

  public static synchronized void addAlwaysObserver(IGUIObserver o) {
    fAlwaysObservers.add(o);
  }

  public static synchronized void addFinalObserver(IGUIObserver o) {
    fFinalObservers.add(o);
  }

  public static synchronized void notifyDayObservers() {
    Iterator iter = fDayObservers.iterator();
    // System.err.println("**** DayObservers ****");
    while (iter.hasNext()) {
      IGUIObserver observer = (IGUIObserver) iter.next();
      observer.gUpdate();
      // System.err.println(observer.toString());
    }
  }

  public static synchronized void notifyStepObservers() {
    Iterator iter = fStepObservers.iterator();
    // System.err.println("**** StepObservers ****");
    while (iter.hasNext()) {
      IGUIObserver observer = (IGUIObserver) iter.next();
      observer.gUpdate();
      // System.err.println(observer.toString());
    }
  }

  public static synchronized void notifyAlwaysObservers() {
    Iterator iter = fAlwaysObservers.iterator();
    while (iter.hasNext()) {
      IGUIObserver observer = (IGUIObserver) iter.next();
      observer.gUpdate();
      // System.err.println(observer.toString());
    }
  }

  public static synchronized void notifyFinalObservers() {
    Iterator iter = fFinalObservers.iterator();
    while (iter.hasNext()) {
      ( (IGUIObserver) (iter.next())).gUpdate();
    }
  }

  public static void setCProtocol(UProtocolCore cp) {
    fCProtocol = cp;
  }

  public static int getDate() {
    return fCurrentDate;
  }

  public static int getBoard() {
    return fBoard;
  }

  public static int getStep() {
    return fCurrentStep;
  }

  public static int getMarketStatus() {
    return fMarketStatus;
  }

  public static void main(String[] args) {
    UGUIUpdateManager UGUITimeManager1 = new UGUIUpdateManager();
  }

}
