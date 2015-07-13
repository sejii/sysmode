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

import java.text.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cmdCore.*;

/**
 * UOrderButtonPanel の隣の小さなパネル．現在は気配値を表示．
 */
public class USubPanel extends UPanel implements ActionListener {
  JLabel fTimeLabel = new JLabel();
  JTextField fTimeTextField = new JTextField();
  JLabel fAskedQuotationLabel = new JLabel();
  JTextField fAskedQuotationTextField = new JTextField();
  JTextField fBidQuotationTextField = new JTextField();
  JLabel fBidQuotatiionLabel = new JLabel();
  javax.swing.Timer clockTimer;
  protected java.util.Date fDate = new java.util.Date();

  public USubPanel() {
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * interface IGUIObserver を持つものは UGUIUpdateManager の
   * addDayObserver か addStepObserver を呼ぶ必要あり．すべてのUpanelで必要．
   */
  public void addUpdateObserverList() {
    UGUIUpdateManager.addStepObserver(this);
  }

  void jbInit() throws Exception {
    fTimeLabel.setText(fRb.getString("CURRENT_TIME"));
    fTimeLabel.setForeground(Color.black);
    fTimeLabel.setBounds(new Rectangle(4, 10, 77, 17));
    this.setLayout(null);
    fTimeTextField.setBounds(new Rectangle(80, 10, 70, 21));
    fAskedQuotationLabel.setText(fRb.getString("ASKED_QUOTATION"));
    fAskedQuotationLabel.setForeground(Color.black);
    fAskedQuotationLabel.setBounds(new Rectangle(4, 39, 83, 17));
    fAskedQuotationTextField.setBounds(new Rectangle(81, 37, 70, 21));
    fBidQuotationTextField.setBounds(new Rectangle(81, 63, 70, 21));
    fBidQuotatiionLabel.setBounds(new Rectangle(5, 65, 76, 17));
    fBidQuotatiionLabel.setText(fRb.getString("BID_QUOTATION"));
    fBidQuotatiionLabel.setForeground(Color.black);
    fTimeTextField.setBackground(Color.white);
    fTimeTextField.setFont(new java.awt.Font("Dialog", 1, 11));
    fTimeTextField.setForeground(Color.black);
    fTimeTextField.setEditable(false);
    fTimeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fAskedQuotationTextField.setBackground(Color.white);
    fAskedQuotationTextField.setFont(new java.awt.Font("Dialog", 1, 11));
    fAskedQuotationTextField.setForeground(Color.black);
    fAskedQuotationTextField.setEditable(false);
    fAskedQuotationTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fBidQuotationTextField.setBackground(Color.white);
    fBidQuotationTextField.setFont(new java.awt.Font("Dialog", 1, 11));
    fBidQuotationTextField.setForeground(Color.black);
    fBidQuotationTextField.setEditable(false);
    fBidQuotationTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    this.setFont(new java.awt.Font("MS Sans Serif", 0, 11));
    this.add(fTimeLabel, null);
    this.add(fAskedQuotationLabel, null);
    this.add(fBidQuotatiionLabel, null);
    this.add(fBidQuotationTextField, null);
    this.add(fTimeTextField, null);
    this.add(fAskedQuotationTextField, null);
    clockTimer = new javax.swing.Timer(500, this);
    clockTimer.start();
  }

  public void dataUpdate() {
    UCTodaysQuotesCore cTodaysQuates =
        (UCTodaysQuotesCore) fCProtocol.getCommand(
        UCTodaysQuotesCore.CMD_NAME);
    cTodaysQuates.setArguments("j30", 1);
    cTodaysQuates.doIt();
    ArrayList array = cTodaysQuates.getResults();
    if (array.size() > 0) {
      HashMap hm = (HashMap) (array.get(0));
      /*
          String askq = ((Long) (hm.get("LONG_ASKED_QUOTATION"))).toString();
          String bidq = ((Long) (hm.get("LONG_BID_QUOTATION"))).toString();
       */
      UFormatLong askq = new UFormatLong( (Long) (hm.get("LONG_ASKED_QUOTATION")));
      UFormatLong bidq = new UFormatLong( (Long) (hm.get("LONG_BID_QUOTATION")));
      if (askq.longValue() == -1
          && bidq.longValue() == -1) { // すべてが成行の場合の処理
        fAskedQuotationTextField.setText("-");
        fBidQuotationTextField.setText("-");
      } else {
        fAskedQuotationTextField.setText(askq.toString());
        fBidQuotationTextField.setText(bidq.toString());
      }
    } else {
      fAskedQuotationTextField.setText("-");
      fBidQuotationTextField.setText("-");
    }
  }

  public void actionPerformed(ActionEvent e) {
    if (fTimeTextField.isVisible()) {
      fTimeTextField.setText(
          DateFormat.getTimeInstance().format(new Date()));
    }
  }

  public static void main(String[] args) {
    JFrame f = new JFrame();
    f.setSize(new Dimension(100, 100));
    f.getContentPane().add(new USubPanel());
    f.setVisible(true);
  }
}
