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
import serverNet.*;
import strategyCore.UBaseAgent;

/**
 * UServerManager はサーバの管理GUIである．
 */
public class UServerManager implements ActionListener, IGUI {

  protected UServerManagerMainWindow fGui;
  protected UMartNetwork fServer;
  protected UProtocolCore fCProtocol;
  protected UParameters fParam = UParameters.getInstance();
  protected UParametersNet fParamNet = UParametersNet.getInstance();
  protected UServerManagerParamDialog fParamDialog;
  protected HashMap fCommandHashMap;
  protected UBaseAgent fUGeneralStrategy;
  protected int fHumanID = 1;
  protected ResourceBundle fRb = UParameters.fRb;

  /** サーバー状態更新用のタイマー */
  protected javax.swing.Timer fUpdateTimer;

  /** サーバー状態更新の時間間隔（秒） */
  private int fInterval = 10;

  /** 表示更新用のタイマー */
  protected javax.swing.Timer fGuiUpdateTimer;

  /** 表示更新の時間間隔（秒） */
  public static final int TIMER_INTERVAL = 1;

  protected String fName = "Market Server";

  public UServerManager() {
    try {
      ULogoWindow logow = new ULogoWindow();
      logow.setVisible(true);
      Thread.sleep(2500);
      logow.dispose();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    fParam.setConnectionType(UParameters.CONNECTION_TYPE_NETWORK);
    fParamDialog = new UServerManagerParamDialog();
    fParamDialog.setLocationRelativeTo( (Frame)null);
    fParamDialog.setVisible(true);
    if (!fParamDialog.getStatus()) {
      System.exit(0);
      // 以下の部分はサーバの初期化による出力を拾えないための方策．
      // 非常にまずいが仕方ない．標準出力タブは必ず入れる．
    }
    System.setOut(UParameters.fPrintStream);
    fUpdateTimer = new javax.swing.Timer(fInterval * 1000, this);
    fUpdateTimer.setInitialDelay(0);
    fServer = new UMartNetwork(fParam.getMemberLog(), fParam.getPriceInfoDB(),
                               fParam.getStartPoint(), fParam.getSeed(),
                               fParam.getDays(), fParam.getBoardPerDay(),
                               fParamNet.getPort());
    if (fParam.isLogCreate()) {
      try {
        fServer.initLog();
      } catch (IOException ioex) {
        JOptionPane.showMessageDialog(null,
                                      fRb.getString("ERROR_CANNOT_CREATE_LOGS"),
                                      fRb.getString("ERROR_DIALOG_TITLE"),
                                      JOptionPane.ERROR_MESSAGE);
        System.exit(5);
      } catch (Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        JOptionPane.showMessageDialog(null,
                                      fRb.getString("ERROR_FATAL") + "\n" +
                                      sw.toString(),
                                      fRb.getString("ERROR_DIALOG_TITLE"),
                                      JOptionPane.ERROR_MESSAGE);
        System.exit(5);
      }
    }
    fServer.startLoginManager();
    initAgents(fParam.getMachineAgentArray());
    fGui = new UServerManagerMainWindow(this, fParam.getTabs());
    fParam.setMainComponet(fGui);
    fGui.setTimer(fUpdateTimer);
    fGui.mainImpl();
    ActionListener guiUpdater = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fGui.gUpdate();
      }
    };
    fGuiUpdateTimer = new javax.swing.Timer(TIMER_INTERVAL * 1000, guiUpdater);
    fGuiUpdateTimer.start();
    // １日目の１回目の注文期間に進めておく
    fUpdateTimer.setRepeats(false);
    fUpdateTimer.start();
  }

  public void actionPerformed(ActionEvent e) {
    UServerStatus status = fServer.nextStatus();
    switch (status.getState()) {
      case UServerStatus.AFTER_SETTLEMENT:
        fUpdateTimer.stop();
        break;
      case UServerStatus.ACCEPT_ORDERS:
        fServer.recieveOrdersFromLocalAgents();
        break;
    }
    fGui.gUpdate();
  }

  public void initAgents(ArrayList machineAgentArray) {
    fUGeneralStrategy = new UBaseAgent("su", "supasswd", "Super User", 0);
    fServer.appendStrategy(fUGeneralStrategy);
    fCProtocol = fUGeneralStrategy.getUmcp();
    fCommandHashMap = fCProtocol.getCommandHashMap();
    Iterator iter = machineAgentArray.iterator();
    while (iter.hasNext()) {
      fServer.appendStrategy( (UBaseAgent) iter.next());
    }
  }

  public UMartNetwork getServer() {
    return fServer;
  }

  public UProtocolCore getCProtocol() {
    return fCProtocol;
  }

  public UProtocolCore getSuperUserCProtocol() {
    return fCProtocol;
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
      UServerManager ugui = new UServerManager();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
