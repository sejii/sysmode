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
import java.awt.event.*;
import javax.swing.*;
import cmdCore.*;
import serverCore.*;
import serverSA.*;
import strategyCore.UBaseAgent;

/**
 * UStandAloneGUI はスタンドアローン版GUIの中心クラスである.
 */
public class UStandAloneGUI implements ActionListener, IGUI {
  protected UGUIMainWindow fGui;
  protected UMartStandAlone fServer;
  protected UParameters fParam = UParameters.getInstance();
  protected UParamDialog fParamDialog;
  protected UBaseAgent fHumanPlayer;
  protected UProtocolCore fCProtocol;
  protected HashMap fCommandHashMap;
  protected UBaseAgent fSuperUser;
  protected int fInterval = 3000;
  protected int fInitDelay = 200;
  protected int fHumanID = 1;
  protected javax.swing.Timer fUpdateTimer;
  protected int ftmpStep = 0;
  protected String fName = UParameters.fRb.getString("MARKET_SIMULATOR_PRODUCT");
  protected ResourceBundle fRb = UParameters.fRb;

  public UStandAloneGUI() {
    try {
      ULogoWindow logow = new ULogoWindow();
      logow.setVisible(true);
      Thread.sleep(2500);
      logow.dispose();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    initStandAlone();
  }

  public void initStandAlone() {
    fParam.setConnectionType(UParameters.CONNECTION_TYPE_STAND_ALONE);
    fParamDialog = new UParamDialog();
    fParamDialog.setLocationRelativeTo( (Frame)null);
    fParamDialog.setVisible(true);
    if (!fParamDialog.getStatus()) {
      System.exit(0);
    }
    /*
     * 以下の部分はサーバの初期化による出力を拾えないための方策．あまり重要じゃないので省略．
     * ただし，標準出力タブで初期化時のメッセージを拾えないので注意のこと．
     */
    // System.setOut(UParameters.fPrintStream); // 非常にまずいが仕方ない．標準出力タブは必ず入れる．
    fUpdateTimer = new javax.swing.Timer(fInterval, this);
    fUpdateTimer.setInitialDelay(fInitDelay);
    fServer = new UMartStandAlone(fParam.getMemberLog(), fParam.getPriceInfoDB(),
                                  fParam.getStartPoint(),
                                  fParam.getSeed(), fParam.getDays(),
                                  fParam.getBoardPerDay());
    initAgents(fParam.getMachineAgentArray());
    if (fParam.isLogCreate()) {
      try {
        fServer.initLog();
      } catch (Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        JOptionPane.showMessageDialog(fParam.getMainComponet(),
                                      fRb.getString("ERROR_FATAL") + "\n" +
                                      sw.toString(),
                                      fRb.getString("ERROR_DIALOG_TITLE"),
                                      JOptionPane.ERROR_MESSAGE);
        System.exit(5);
      }
    }
    fGui = new UGUIMainWindow(this, fParam.getTabs());
    /* JOptionPane のための標準親コンポーネント */
    fParam.setMainComponet(fGui);
    fGui.setTimer(fUpdateTimer);
    fGui.mainImpl();
    fUpdateTimer.start();
  }

  public void actionPerformed(ActionEvent e) {
    UServerStatus status = fServer.nextStatus();
    if (status.getState() == UServerStatus.ACCEPT_ORDERS) {
      fServer.recieveOrdersFromLocalAgents();
    }
    fGui.gUpdate();
  }

  public void initAgents(ArrayList machineAgentArray) {
    fHumanPlayer = new UBaseAgent("You", "passwd" + fHumanID, "You", 1);
    fServer.appendStrategy(fHumanPlayer);
    fCProtocol = fHumanPlayer.getUmcp();
    fCommandHashMap = fCProtocol.getCommandHashMap();
    fSuperUser = new UBaseAgent("su", "supasswd", "SuperUser", 0);
    fServer.appendStrategy(fSuperUser);
    Iterator iter = machineAgentArray.iterator();
    while (iter.hasNext()) {
      fServer.appendStrategy( (UBaseAgent) iter.next());
    }
  }

  public UMartStandAlone getServer() {
    return fServer;
  }

  public UProtocolCore getCProtocol() {
    return fCProtocol;
  }

  public UProtocolCore getSuperUserCProtocol() {
    return fSuperUser.getUmcp();
  }

  public int getInterval() {
    return fInterval;
  }

  public void setInterval(int i) {
    fInterval = i;
  }

  public javax.swing.Timer getUpdateTimer() {
    return fUpdateTimer;
  }

  public String getName() {
    return fName;
  }

  public static void main(String[] args) {
    try {
      UStandAloneGUI ugui = new UStandAloneGUI();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
