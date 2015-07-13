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
import serverCore.*;

/**
 * メインウィンドウ上部のパネル．いろいろなデータを表示する. 
 */
public class UServerManagerTopPanel extends UPanel {

  /** 日付 */
  UValueLabel fDayTextField = new UValueLabel();

  JLabel fDayLabel = new JLabel();

  /** 板寄番号 */
  UValueLabel fBoardTextField = new UValueLabel();

  JLabel fBoardLabel = new JLabel();

  /** SpotPrice */
  UValueLabel fSpotPriceTextField = new UValueLabel();

  JLabel fSpotPriceLabel = new JLabel();

  /** Future(U-Mart) Price */
  UValueLabel fFuturePriceTextField = new UValueLabel();

  JLabel fFuturePriceLabel = new JLabel();

  /* 更新 */
  JPanel fAutoUpdatePanel = new JPanel();

  JButton fUpdateButton = new JButton();

  JTextField fIntervalTextField = new JTextField();

  JCheckBox fAutoUpdateCheckBox = new JCheckBox();

  javax.swing.Timer fUpdateTimer;

  protected boolean fIsAuto;

  JLabel fSecond = new JLabel();

  JLabel fSec = new JLabel();

  public UServerManagerTopPanel() {
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * interface IGUIObserver を持つものは UGUIUpdateManager の addDayObserver か
   * addStepObserver を呼ぶ必要あり．すべてのUpanelで必要．
   */
  public void addUpdateObserverList() {
    UGUIUpdateManager.addStepObserver(this);
  }

  void jbInit() throws Exception {
    this.setLayout(null);
    fDayTextField.setBounds(new Rectangle(49, 13, 44, 21));
    fDayTextField.setForeground(Color.black);
    fBoardTextField.setBounds(new Rectangle(62, 46, 31, 22));
    fBoardTextField.setForeground(Color.black);
    fBoardLabel.setText(fRb.getString("SESSION") + ":");
    fBoardLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fBoardLabel.setForeground(Color.black);
    fBoardLabel.setBounds(new Rectangle(5, 49, 51, 17));

    fSpotPriceTextField.setBounds(new Rectangle(213, 13, 79, 21));
    fSpotPriceTextField.setForeground(Color.black);
    fSpotPriceLabel.setText(fRb.getString("CURRENT_SPOT_PRICE"));
    fSpotPriceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fSpotPriceLabel.setForeground(Color.black);
    fSpotPriceLabel.setBounds(new Rectangle(109, 15, 95, 17));
    fFuturePriceTextField.setBounds(new Rectangle(213, 47, 79, 21));
    fFuturePriceTextField.setForeground(Color.black);
    fFuturePriceLabel.setText(fRb.getString("CURRENT_U-MART_PRICE"));
    fFuturePriceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fFuturePriceLabel.setForeground(Color.black);
    fFuturePriceLabel.setBounds(new Rectangle(109, 49, 95, 17));

    fAutoUpdatePanel.setLayout(null);
    fAutoUpdatePanel.setBounds(new Rectangle(318, 10, 201, 62));
    fAutoUpdatePanel.setBorder(BorderFactory.createEtchedBorder());
    fUpdateButton.setBounds(new Rectangle(63, 32, 98, 21));
    fUpdateButton.setText(fRb.getString("UPDATE"));
    fUpdateButton.setEnabled(true);
    fUpdateButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fUpdateTimer.setRepeats(false);
        fUpdateTimer.start();
      }
    });
    fAutoUpdateCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
    fAutoUpdateCheckBox.setToolTipText("");
    fAutoUpdateCheckBox.setActionCommand(fRb.getString("AUTO_UPDATE"));
    fAutoUpdateCheckBox.setText(fRb.getString("AUTO_UPDATE"));
    fAutoUpdateCheckBox.setBounds(new Rectangle(4, 3, 95, 25));
    fAutoUpdateCheckBox.setSelected(false);
    fAutoUpdateCheckBox.addActionListener(new ActionListener() {
      //TODO isao ここでサーバーへ通達する
      public void actionPerformed(ActionEvent e) {
        if (fAutoUpdateCheckBox.isSelected()) {
          fUpdateButton.setEnabled(false);
          fUpdateTimer.setRepeats(true);
          // mori 数字の形式問題解決処理追加 int -> double に変更
          double interval;
          try {
            interval = Double.parseDouble(fIntervalTextField
                                          .getText());
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(UParameters.getInstance()
                                          .getMainComponet(), fRb
                                          .getString("NUMERICAL_VALUE_NEEDED"));
            return;
          }
          if (interval < 0) {
            JOptionPane.showMessageDialog(UParameters.getInstance()
                                          .getMainComponet(), fRb
                                          .getString(
                "NON-NEGATIVE_VALUE_NEEDED"));
            return;
          }
          if (interval <= 0) {
            fUpdateTimer.setDelay(0);
          } else {
            fUpdateTimer.setDelay( (int) (interval * 1000));
          }
          fUpdateTimer.start();
        } else {
          fUpdateButton.setEnabled(true);
          fUpdateTimer.stop();
        }
      }
    });
    fIntervalTextField.setForeground(Color.black);
    fIntervalTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fIntervalTextField.setBounds(new Rectangle(105, 4, 50, 21));
    fIntervalTextField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        double d;
        try {
          d = Double.parseDouble(fIntervalTextField.getText().trim());
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(UParameters.getInstance()
                                        .getMainComponet(), fRb
                                        .getString("NUMERICAL_VALUE_NEEDED"));
          return;
        }
        if (d < 0) {
          JOptionPane.showMessageDialog(UParameters.getInstance()
                                        .getMainComponet(), fRb
                                        .getString("NON-NEGATIVE_VALUE_NEEDED"));
          return;
        }
        fUpdateTimer.setDelay( (int) (1000 * d)); // set -> msec
        fAutoUpdatePanel.requestFocus();
      }
    });
    fSecond.setText(fRb.getString("SEC"));
    fSecond.setForeground(Color.black);
    fSecond.setBounds(new Rectangle(90, 30, 41, 17));
    fSec.setText(fRb.getString("SEC"));
    fSec.setForeground(Color.black);
    fSec.setBounds(new Rectangle(161, 8, 31, 17));
    this.setPreferredSize(new Dimension(603, 112));
    fDayLabel.setBounds(new Rectangle(5, 15, 40, 17));
    fDayLabel.setText(fRb.getString("DAY") + ":");
    fDayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fDayLabel.setForeground(Color.black);
    fAutoUpdatePanel.add(fAutoUpdateCheckBox, null);
    fAutoUpdatePanel.add(fSec, null);
    fAutoUpdatePanel.add(fIntervalTextField, null);
    fAutoUpdatePanel.add(fUpdateButton, null);
    this.add(fFuturePriceLabel, null);
    this.add(fDayLabel, null);
    this.add(fDayTextField, null);
    this.add(fBoardTextField, null);
    this.add(fBoardLabel, null);
    this.add(fFuturePriceTextField, null);
    this.add(fSpotPriceTextField, null);
    this.add(fSpotPriceLabel, null);

    this.add(fAutoUpdatePanel, null);
  }

  public void dataUpdate() {
    Iterator iter;
    HashMap hm;
    NumberFormat numFormat = NumberFormat.getNumberInstance();
    NumberFormat percentFormat = NumberFormat.getPercentInstance();
    percentFormat.setMaximumFractionDigits(3);
    // 日付関係はすべてUGUIUpdateManager 経由で取得するように変更
    // マーケットが閉じているときは Session に "--" を出すように変更
    int status = UGUIUpdateManager.getMarketStatus();
    if (status == UServerStatus.ACCEPT_ORDERS) {
      fDayTextField.setText(String.valueOf(UGUIUpdateManager.getDate()));
      fBoardTextField.setText(String
                              .valueOf(UGUIUpdateManager.getBoard()));
    } else if (status == UServerStatus.AFTER_MARKING_TO_MARKET) {
      fDayTextField.setText(String.valueOf(UGUIUpdateManager.getDate()));
      fBoardTextField.setText("--");
    }
    //現物，先物価格更新
    UCSpotPriceCore cSpotPrice = (UCSpotPriceCore) fCProtocol
        .getCommand(UCSpotPriceCore.CMD_NAME);
    cSpotPrice.setArguments(new StringTokenizer("j30 1"));
    cSpotPrice.doIt();
    iter = cSpotPrice.getResults().iterator();
    if (iter.hasNext()) { // null check の代わり．イマイチ．
      HashMap elem = (HashMap) iter.next();
      fSpotPriceTextField.setText(numFormat.format( (Long) elem
          .get(UCSpotPriceCore.LONG_PRICE)));
    }
    UCFuturePriceCore cFuturePrice = (UCFuturePriceCore) fCProtocol
        .getCommand(UCFuturePriceCore.CMD_NAME);
    cFuturePrice.setArguments(new StringTokenizer("j30 1"));
    cFuturePrice.doIt();
    iter = cFuturePrice.getResults().iterator();
    if (iter.hasNext()) {
      HashMap elem = (HashMap) iter.next();
      Long l = (Long) elem.get(UCFuturePriceCore.LONG_PRICE);
      if (l.longValue() != UPriceInfo.INVALID_PRICE) {
        fFuturePriceTextField.setText(numFormat.format(l));
      } else {
        fFuturePriceTextField.setText("--");
      }
    }
  }

  public void setTimer(javax.swing.Timer timer) {
    fUpdateTimer = timer;
    fIntervalTextField.setText(Integer
                               .toString(fUpdateTimer.getDelay() / 1000));
  }

  public static void main(String[] args) {
    final JFrame f = new JFrame("UServerManagerTopPanel");
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        f.setVisible(false);
        f.dispose();
        System.exit(0);
      }
    });
    UServerManagerTopPanel utp = new UServerManagerTopPanel();
    f.getContentPane().add(utp);
    f.setSize(new Dimension(700, 150));
    f.setVisible(true);
  }
}