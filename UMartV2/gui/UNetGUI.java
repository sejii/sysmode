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
import java.awt.event.*;
import cmdClientNet.*;
import cmdCore.*;
import serverCore.*;

/**
 * UNetGUI はスタンドアローン版GUIの中心クラスである.
 */
public class UNetGUI implements ActionListener, IGUI {

  protected UGUIMainWindow fGui;
  protected UProtocolForNetClient fCProtocol;
  protected UParameters fParam = UParameters.getInstance();
  protected int fInterval = 2000;
  static final int fInitDelay = 1000;
  protected int fID = 1;
  protected javax.swing.Timer fUpdateTimer;
  protected String fName = UParameters.fRb.getString("TRADING_TERMINAL_PRODUCT");
  protected int ftmpStep = 0;
  protected UConnectionDialog fConnectionDialog;
  protected ULoginDialog fLoginDialog;
  protected UProtocolForNetClient fSuperUserCProtocol;

  public UNetGUI(String defaultServerName) {
    try {
      ULogoWindow logow = new ULogoWindow();
      logow.setVisible(true);
      Thread.sleep(2500);
      logow.dispose();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    try {
      /*
       * 以下の部分はサーバの初期化による出力を拾えないための方策．あまり重要じゃないので省略．
       * ただし，標準出力タブで初期化時のメッセージを拾えないので注意のこと．
       */
      //System.setOut(UParameters.fPrintStream); // 非常にまずいが仕方ない．標準出力タブは必ず入れる．
      fUpdateTimer = new javax.swing.Timer(fInterval, this);
      fUpdateTimer.setInitialDelay(fInitDelay);
      fCProtocol = new UProtocolForNetClient();
      fSuperUserCProtocol = new UProtocolForNetClient();
      //Server
      fConnectionDialog = new UConnectionDialog(fCProtocol, fSuperUserCProtocol,
                                                defaultServerName);
      fConnectionDialog.setVisible(true);
      UCCLoginNet login = (UCCLoginNet) fSuperUserCProtocol.getCommand(
          UCLoginCore.CMD_NAME);
      login.setArguments("su", "supasswd");
      UCommandStatus suLoginStatus = login.doIt();
      if (!suLoginStatus.getStatus()) {
        System.err.println(suLoginStatus.getErrorMessage());
        System.exit(5);
      }
      fLoginDialog = new ULoginDialog(fCProtocol,
                                      fConnectionDialog.getServerName(),
                                      fConnectionDialog.getServerPort());
      fLoginDialog.setVisible(true);
      UCScheduleCore cSchedule = (UCScheduleCore) fCProtocol.getCommand(
          UCScheduleCore.CMD_NAME);
      cSchedule.doIt();
      HashMap hm = cSchedule.getResults();
      fParam.setBoardPerDay( ( (Integer) hm.get(UCScheduleCore.INT_NO_OF_BOARDS)).
                            intValue());
      fParam.setDays( ( (Integer) hm.get(UCScheduleCore.INT_MAX_DAY)).intValue());
      setupTabs();
      int state;
      UWaitingFrame fWaitFrame = new UWaitingFrame();
      fWaitFrame.setVisible(true);
      do {
        UCMarketStatusCore ucmStatus = (UCMarketStatusCore) fCProtocol.
            getCommand(UCMarketStatusCore.CMD_NAME);
        ucmStatus.doIt();
        HashMap hmMInfo = ucmStatus.getMarketInfo();
        state = ( (Integer) (hmMInfo.get(UCMarketStatusCore.INT_MARKET_STATUS))).
            intValue();
        try {
          Thread.sleep(500);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      } while (state == UServerStatus.BEFORE_TRADING);
      fWaitFrame.setVisible(false);
      fWaitFrame.dispose();
      fGui = new UGUIMainWindow(this, fParam.getTabs());
      fParam.setMainComponet(fGui);
      fGui.setTimer(fUpdateTimer);
      fGui.mainImpl();
      fUpdateTimer.start();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void setupTabs() {
    fParam.fTabs.clear();
    UCMemberProfileCore cMemberProf = (UCMemberProfileCore) fCProtocol.
        getCommand(UCMemberProfileCore.CMD_NAME);
    cMemberProf.setArguments( -1);
    cMemberProf.doIt();
    HashMap memProf = cMemberProf.getData();
    ArrayList access = (ArrayList) memProf.get(UCMemberProfileCore.
                                               ARRAY_LIST_ACCESS);
    for (int i = 0; i < UPanelFactory.PANEL_TYPES.length; ++i) {
      if (isPanelAvailable(UPanelFactory.PANEL_TYPES[i], access)) {
        fParam.fTabs.add(UPanelFactory.PANEL_TYPES[i]);
      }
    }
  }

  private boolean isPanelAvailable(String panelName, ArrayList access) {
    Iterator itr = access.iterator();
    while (itr.hasNext()) {
      String str = (String) itr.next();
      if (str.equals(panelName)) {
        return false;
      }
    }
    return true;
  }

  public void actionPerformed(ActionEvent e) {
    fGui.gUpdate();
  }

  public UProtocolCore getCProtocol() {
    return fCProtocol;
  }

  public UProtocolCore getSuperUserCProtocol() {
    return fSuperUserCProtocol;
  }

  public javax.swing.Timer getUpdateTimer() {
    return fUpdateTimer;
  }

  public int getID() {
    return fID;
  }

  private void login(String userName, String passwd) {
    UCLoginCore loginCmd = (UCLoginCore) fCProtocol.getCommand(UCLoginCore.
        CMD_NAME);
    loginCmd.setArguments(userName, passwd);
    UCommandStatus cmdStatus = loginCmd.doIt();
    if (!cmdStatus.getStatus()) {
      System.err.println("Can't login with (" + userName + ", " + passwd + ")");
      System.exit(5);
    }
  }

  public String getName() {
    return fName;
  }

  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        UNetGUI ugui = new UNetGUI("localhost");
      } else {
        UNetGUI ugui = new UNetGUI(args[0]);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
