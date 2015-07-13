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
public class UTopPanel extends UPanel {

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

  /** 現金 */
  UValueLabel fCashTextField = new UValueLabel();

  JLabel fCashLabel = new JLabel();

  /** 未実現損益 */
  UValueLabel fUnrealizedProfitTextField = new UValueLabel();

  JLabel fUnrealizedProfitLabel = new JLabel();

  /** ポジション */
  UValueLabel fPositionTextField = new UValueLabel();

  JLabel fPositionLabel = new JLabel();

  /** 更新管理 */
  JPanel fAutoUpdatePanel = new JPanel();

  JButton fUpdateButton = new JButton();

  JTextField fIntervalTextField = new JTextField();

  //protected static final String fInitInterval = "5";
  JCheckBox fAutoUpdateCheckBox = new JCheckBox();

  javax.swing.Timer fUpdateTimer;

  protected boolean fIsAuto;

  JLabel fSecond = new JLabel();

  JLabel fSec = new JLabel();

  /** 利益率 */
  JLabel fMarginLabel = new JLabel();

  UValueLabel fMarginTextField = new UValueLabel();

  JLabel fSurplusLabel = new JLabel();

  UValueLabel fSurplusTextField = new UValueLabel();

  protected NumberFormat numFormat = NumberFormat.getNumberInstance();

  protected boolean fISBankrupted = false;

  public UTopPanel() {
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
    fDayTextField.setBounds(new Rectangle(53, 11, 44, 21));
    fDayTextField.setForeground(Color.black);
    fBoardTextField.setBounds(new Rectangle(160, 13, 31, 22));
    fBoardTextField.setForeground(Color.black);
    fBoardLabel.setText(fRb.getString("SESSION") + ":");
    fBoardLabel.setForeground(Color.black);
    fBoardLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fBoardLabel.setBounds(new Rectangle(107, 13, 51, 17));

    fSpotPriceTextField.setBounds(new Rectangle(125, 36, 79, 21));
    fSpotPriceTextField.setForeground(Color.black);
    fSpotPriceLabel.setText(fRb.getString("CURRENT_SPOT_PRICE"));
    fSpotPriceLabel.setForeground(Color.black);
    fSpotPriceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fSpotPriceLabel.setBounds(new Rectangle(14, 37, 108, 17));
    fFuturePriceTextField.setBounds(new Rectangle(125, 60, 79, 21));
    fFuturePriceTextField.setForeground(Color.black);
    fFuturePriceLabel.setText(fRb.getString("CURRENT_U-MART_PRICE"));
    fFuturePriceLabel.setForeground(Color.black);
    fFuturePriceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fFuturePriceLabel.setHorizontalTextPosition(SwingConstants.TRAILING);
    fFuturePriceLabel.setBounds(new Rectangle(14, 60, 108, 17));

    fCashTextField.setBounds(new Rectangle(333, 11, 125, 21));
    fCashTextField.setForeground(Color.black);
    fCashLabel.setText(fRb.getString("CASH"));
    fCashLabel.setForeground(Color.black);
    fCashLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fCashLabel.setBounds(new Rectangle(214, 13, 118, 17));

    fUnrealizedProfitTextField.setBounds(new Rectangle(333, 60, 125, 21));
    fUnrealizedProfitTextField.setForeground(Color.black);
    fUnrealizedProfitLabel.setText(fRb.getString("UNREALIZED_PROFIT"));
    fUnrealizedProfitLabel.setForeground(Color.black);
    fUnrealizedProfitLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fUnrealizedProfitLabel.setBounds(new Rectangle(214, 63, 118, 17));

    fPositionTextField.setBounds(new Rectangle(103, 84, 101, 21));
    fPositionTextField.setForeground(Color.black);
    fPositionLabel.setText(fRb.getString("POSITION"));
    fPositionLabel.setForeground(Color.black);
    fPositionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fPositionLabel.setHorizontalTextPosition(SwingConstants.TRAILING);
    fPositionLabel.setBounds(new Rectangle(14, 86, 88, 17));
    fAutoUpdatePanel.setLayout(null);
    fAutoUpdatePanel.setBounds(new Rectangle(461, 9, 123, 93));
    fAutoUpdatePanel.setBorder(BorderFactory.createEtchedBorder());
    fUpdateButton.setBounds(new Rectangle(10, 57, 98, 21));
    fUpdateButton.setText(fRb.getString("UPDATE"));
    // 自動更新でない場合には更新ボタンはアクティブ
    fUpdateButton.setEnabled(!fParam.isAutoRun());
    fUpdateButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fUpdateTimer.setRepeats(false);
        fUpdateTimer.setDelay(0);
        fUpdateTimer.setInitialDelay(1);
        fUpdateTimer.start();
        UGUIUpdateManager.update();
      }
    });
    fAutoUpdateCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
    fAutoUpdateCheckBox.setToolTipText("");
    fAutoUpdateCheckBox.setActionCommand(fRb.getString("AUTO_UPDATE"));
    fAutoUpdateCheckBox.setText(fRb.getString("AUTO_UPDATE"));
    fAutoUpdateCheckBox.setBounds(new Rectangle(4, 3, 100, 25));
    fAutoUpdateCheckBox.setSelected(fParam.isAutoRun());
    fAutoUpdateCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (fAutoUpdateCheckBox.isSelected()) {
          fUpdateButton.setEnabled(false);
          fUpdateTimer.setRepeats(true);
          setUpdateTimerDelay();
          fUpdateTimer.start();
        } else {
          fUpdateButton.setEnabled(true);
          fUpdateTimer.stop();
        }
        UGUIUpdateManager.update(); //これは不要かもしれないけど念のため．        
      }
    });
    fIntervalTextField.setForeground(Color.black);
    fIntervalTextField.setFont(new java.awt.Font("SansSerif", 1, 12));
    fIntervalTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fIntervalTextField.setBounds(new Rectangle(34, 29, 50, 21));
    fIntervalTextField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setUpdateTimerDelay();
        fAutoUpdatePanel.requestFocus();
        UGUIUpdateManager.update(); //これは不要かもしれないけど念のため．                
      }
    });
    fSecond.setText(fRb.getString("SEC"));
    fSecond.setForeground(Color.black);
    fSecond.setBounds(new Rectangle(90, 30, 41, 17));
    fSec.setText(fRb.getString("SEC"));
    fSec.setForeground(Color.black);
    fSec.setBounds(new Rectangle(94, 32, 31, 17));
    this.setPreferredSize(new Dimension(603, 112));
    fMarginLabel.setBounds(new Rectangle(214, 88, 118, 17));
    fMarginLabel.setText(fRb.getString("MARGIN"));
    fMarginLabel.setForeground(Color.black);
    fMarginLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fMarginTextField.setBounds(new Rectangle(333, 85, 125, 21));
    fMarginTextField.setForeground(Color.black);
    fSurplusLabel.setBounds(new Rectangle(214, 38, 118, 17));
    fSurplusLabel.setText(fRb.getString("AVAILABLE_CASH"));
    fSurplusLabel.setForeground(Color.black);
    fSurplusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fSurplusTextField.setBounds(new Rectangle(333, 35, 125, 21));
    fSurplusTextField.setForeground(Color.black);
    fDayLabel.setBounds(new Rectangle(9, 13, 40, 17));
    fDayLabel.setText(fRb.getString("DAY") + ":");
    fDayLabel.setForeground(Color.black);
    fDayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    fDayLabel.setHorizontalTextPosition(SwingConstants.TRAILING);
    fAutoUpdatePanel.add(fAutoUpdateCheckBox, null);
    fAutoUpdatePanel.add(fIntervalTextField, null);
    fAutoUpdatePanel.add(fSec, null);
    fAutoUpdatePanel.add(fUpdateButton, null);

    this.add(fAutoUpdatePanel, null);
    this.add(fSpotPriceTextField, null);
    this.add(fSpotPriceLabel, null);
    this.add(fFuturePriceLabel, null);
    this.add(fFuturePriceTextField, null);
    this.add(fCashTextField, null);
    this.add(fDayTextField, null);
    this.add(fBoardTextField, null);
    this.add(fBoardLabel, null);
    this.add(fPositionLabel, null);
    this.add(fPositionTextField, null);
    this.add(fMarginTextField, null);
    this.add(fUnrealizedProfitLabel, null);
    this.add(fMarginLabel, null);
    this.add(fUnrealizedProfitTextField, null);
    this.add(fSurplusTextField, null);
    this.add(fSurplusLabel, null);
    this.add(fCashLabel, null);
    this.add(fDayLabel, null);
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
      //			StringTokenizer st = new StringTokenizer((String) iter.next());
      //			st.nextToken();
      //			st.nextToken();
      //			fSpotPriceTextField.setText(numFormat.format(new
      // Long(st.nextToken())));
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
      //			StringTokenizer st = new StringTokenizer((String) iter.next());
      //			st.nextToken();
      //			st.nextToken();
      //			Long l = new Long(st.nextToken());
      HashMap elem = (HashMap) iter.next();
      Long l = (Long) elem.get(UCFuturePriceCore.LONG_PRICE);
      if (l.longValue() != UPriceInfo.INVALID_PRICE) {
        fFuturePriceTextField.setText(numFormat.format(l));
      } else {
        fFuturePriceTextField.setText("--");
      }
    }
    //現金，未実現損益
    long cash, surplus, unrp, margin;
    double profitability;
    UCBalancesCore cBalances = (UCBalancesCore) fCProtocol
        .getCommand(UCBalancesCore.CMD_NAME);
    cBalances.doIt();
    hm = cBalances.getTodayResults();
    if (hm.containsKey(UCBalancesCore.LONG_CASH)) {
      cash = ( (Long) hm.get(UCBalancesCore.LONG_CASH)).longValue();
      if (cash >= 0) {
        fCashTextField.setForeground(Color.black);
        fCashTextField.setText(numFormat.format(cash));
      } else {
        fCashTextField.setForeground(Color.red);
        fCashTextField.setText(numFormat.format(cash));
      }
    }
    if (hm.containsKey(UCBalancesCore.LONG_SURPLUS)) {
      surplus = ( (Long) hm.get(UCBalancesCore.LONG_SURPLUS)).longValue();
      if (surplus >= 0) {
        fSurplusTextField.setForeground(Color.black);
        fSurplusTextField.setText(numFormat.format(surplus));
      } else {
        fSurplusTextField.setForeground(Color.red);
        fSurplusTextField.setText(numFormat.format(surplus));
        //破産時一度だけ破産告知
        if (!fISBankrupted) {
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              JOptionPane.showMessageDialog(UParameters.getInstance().
                                            getMainComponet(),
                                            fRb.getString("YOU_ARE_BANKRUPTED"));
            }
          });
        }
        //破産フラグ！
        fISBankrupted = true;
      }
    }
    if (hm.containsKey(UCBalancesCore.LONG_UNREALIZED_PROFIT)) {
      unrp = ( (Long) hm.get(UCBalancesCore.LONG_UNREALIZED_PROFIT))
          .longValue();
      if (unrp >= 0) {
        fUnrealizedProfitTextField.setForeground(Color.black);
        fUnrealizedProfitTextField.setText(numFormat.format(unrp));
      } else {
        fUnrealizedProfitTextField.setForeground(Color.red);
        fUnrealizedProfitTextField.setText(numFormat.format(unrp));
      }
    }
    if (hm.containsKey(UCBalancesCore.LONG_MARGIN)) {
      margin = ( (Long) hm.get(UCBalancesCore.LONG_MARGIN)).longValue();
      fMarginTextField.setText(numFormat.format(margin));
    }
    /*
     * //利益率 手数料とか考えてないので問題あり // 利益率 = current_cash/initial_cash - 1
     * profitability = 0; if(profitability >= 0){
     * fProfitabilityTextField.setForeground(Color.black);
     * fProfitabilityTextField.setText(percentFormat.format(profitability));
     * }else{ fProfitabilityTextField.setForeground(Color.red);
     * fProfitabilityTextField.setText(percentFormat.format(profitability)); }
     */
    //ポジション
    long todayBpos, todaySpos, yesterdayBpos, yesterdaySpos, totalPos;
    String strPos;
    UCPositionCore cPosition = (UCPositionCore) fCProtocol
        .getCommand(UCPositionCore.CMD_NAME);
    cPosition.doIt();
    HashMap position = cPosition.getResults();
    // 売り越し:Short 買い越し:Long
    todayBpos = ( (Long) position.get(UCPositionCore.LONG_TODAY_BUY))
        .longValue();
    todaySpos = ( (Long) position.get(UCPositionCore.LONG_TODAY_SELL))
        .longValue();
    yesterdayBpos = ( (Long) position.get(UCPositionCore.LONG_YESTERDAY_BUY))
        .longValue();
    yesterdaySpos = ( (Long) position
                     .get(UCPositionCore.LONG_YESTERDAY_SELL)).longValue();
    totalPos = (todayBpos + yesterdayBpos) - (todaySpos + yesterdaySpos);
    if (totalPos > 0) {
      strPos = "Long:  " + String.valueOf(totalPos);
    } else if (totalPos < 0) {
      strPos = "Short: " + String.valueOf( -totalPos);
    } else {
      strPos = "0";
    }
    fPositionTextField.setText(strPos);
  }

  public void setTimer(javax.swing.Timer timer) {
    fUpdateTimer = timer;
    fIntervalTextField.setText(String.valueOf(timer.getDelay() / 1000));
    fUpdateTimer.setRepeats(fParam.isAutoRun());
  }

  /**
   *
   */
  public void setUpdateTimerDelay() {
    double d;
    try {
      d = Double.parseDouble(fIntervalTextField.getText().trim());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(UParameters.getInstance().getMainComponet(),
                                    fRb
                                    .getString("NUMERICAL_VALUE_NEEDED"));
      return;
    }
    if (d <= 0) {
      JOptionPane.showMessageDialog(UParameters.getInstance().getMainComponet(),
                                    fRb
                                    .getString("POSITIVE_VALUE_NEEDED"));
      return;
    }
    fUpdateTimer.setDelay( (int) (1000 * d));
    return;
  }

  /*
   * public static void main(String[] args) { final JFrame f = new
   * JFrame("UTopPanel"); f.addWindowListener(new WindowAdapter() { public
   * void windowClosing(WindowEvent e) { f.setVisible(false); f.dispose();
   * System.exit(0); } }); UTopPanel utp = new UTopPanel();
   * f.getContentPane().add(utp); f.setSize(new Dimension(700, 150));
   * f.setVisible(true); }
   */
}