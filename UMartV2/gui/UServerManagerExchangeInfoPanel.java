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

import java.net.*;
import java.text.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

import cmdCore.*;

public class UServerManagerExchangeInfoPanel extends UPanel {

  JLabel fMemberNumLabel = new JLabel();
  UValueLabel fMemberNumTextField = new UValueLabel();
  JLabel fServerCashLabel = new JLabel();
  UValueLabel fServerCashTextField = new UValueLabel();
  JLabel fServerBuyPositionLabel = new JLabel();
  UValueLabel fServerBuyPositionTextField = new UValueLabel();
  JLabel fServerSellPositionLabel = new JLabel();
  UValueLabel fServerSellPositionTextField = new UValueLabel();
  boolean fFirstFlag = true;
  JLabel fServerPortLabel = new JLabel();
  JLabel fInfoLabel = new JLabel();
  JLabel fServerAddressLabel = new JLabel();
  UParametersNet fParamNet = UParametersNet.getInstance();

  public UServerManagerExchangeInfoPanel() {
    super();
    // Update List に追加．呼ばないとUpdateされない．
    addUpdateObserverList();
    fName = fRb.getString("EXCHANGE_INFO");
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    fServerBuyPositionLabel.setBounds(new Rectangle(20, 56, 95, 17));
    fServerBuyPositionLabel.setForeground(Color.black);
    fServerBuyPositionLabel.setText(fRb.getString("BUY_VOLUME"));
    fServerSellPositionLabel.setBounds(new Rectangle(20, 22, 95, 17));
    fServerSellPositionLabel.setForeground(Color.black);
    fServerSellPositionLabel.setText(fRb.getString("SELL_VOLUME"));
    fServerBuyPositionTextField.setForeground(Color.black);
    fServerBuyPositionTextField.setBounds(new Rectangle(124, 54, 79, 21));
    fServerSellPositionTextField.setForeground(Color.black);
    fServerSellPositionTextField.setBounds(new Rectangle(124, 20, 79, 21));
    fMemberNumLabel.setText(fRb.getString("MAX_MEMBER_NUM"));
    fMemberNumLabel.setForeground(Color.black);
    fMemberNumLabel.setBounds(new Rectangle(237, 55, 95, 17));
    fServerCashTextField.setBounds(new Rectangle(341, 19, 97, 21));
    fServerCashTextField.setForeground(Color.black);
    fServerCashLabel.setText(fRb.getString("EXCHANGE_CASH"));
    fServerCashLabel.setForeground(Color.black);
    fServerCashLabel.setBounds(new Rectangle(237, 21, 95, 17));
    fMemberNumTextField.setBounds(new Rectangle(341, 53, 79, 21));
    fMemberNumTextField.setForeground(Color.black);
    fServerPortLabel.setFont(new java.awt.Font("Dialog", 1, 32));
    fServerPortLabel.setText("Port: " + fParamNet.getPort());
    fServerPortLabel.setBounds(new Rectangle(222, 286, 372, 90));
    fInfoLabel.setFont(new java.awt.Font("Dialog", 1, 36));
    fInfoLabel.setText("Market Server");
    fInfoLabel.setBounds(new Rectangle(222, 152, 280, 64));
    fServerAddressLabel.setFont(new java.awt.Font("Dialog", 1, 32));
    fServerAddressLabel.setText("Host: " +
                                InetAddress.getLocalHost().getHostAddress());
    fServerAddressLabel.setBounds(new Rectangle(222, 229, 372, 80));
    this.add(fServerSellPositionLabel, null);
    this.add(fServerBuyPositionLabel, null);
    this.add(fServerSellPositionTextField, null);
    this.add(fServerBuyPositionTextField, null);
    this.add(fServerCashLabel, null);
    this.add(fMemberNumLabel, null);
    this.add(fServerCashTextField, null);
    this.add(fMemberNumTextField, null);
    this.add(fInfoLabel, null);
    this.add(fServerPortLabel, null);
    this.add(fServerAddressLabel, null);
  }

  /**
   * interface IGUIObserver を持つものは UGUIUpdateManager の
   * addDayObserver か addStepObserver を呼ぶ必要あり．すべてのUpanelで必要．
   */
  public void addUpdateObserverList() {
    UGUIUpdateManager.addStepObserver(this);
  }

  public void dataUpdate() {
    if (!fFirstFlag && !isVisible()) {
      return;
    }
    fFirstFlag = false;
    NumberFormat numFormat = NumberFormat.getNumberInstance();
    //取引所ポジション, 現金, 会員数
    UCExchangeProfileCore cExchangeProfile = (UCExchangeProfileCore) fCProtocol.
        getCommand(UCExchangeProfileCore.CMD_NAME);
    cExchangeProfile.doIt();
    HashMap hmep = cExchangeProfile.getData();
    fServerSellPositionTextField.setText(hmep.get(UCExchangeProfileCore.
                                                  LONG_SELL_POSITION).toString());
    fServerBuyPositionTextField.setText(hmep.get(UCExchangeProfileCore.
                                                 LONG_BUY_POSITION).toString());
    fServerCashTextField.setText(numFormat.format( (Long) hmep.get(
        UCExchangeProfileCore.LONG_CASH)));
    fMemberNumTextField.setText(hmep.get(UCExchangeProfileCore.
                                         INT_NO_OF_MEMBERS).toString());
  }

  public void repaint() {
    if (isVisible()) {
      super.repaint();
    }
  }

}
