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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * メインウィンドウで表示するタブを決定用ダイアログ．
 */
public class UInstitutionSettingDialog extends JDialog {
  JPanel fInstitutionPanel = new JPanel();
  protected UParameters fParam = UParameters.getInstance();
  JButton fOKButton = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();
  ResourceBundle fRb = UParameters.fRb;
  JLabel fLowCashLabel = new JLabel();
  JLabel fInitCashLabel = new JLabel();
  JLabel fMinCashUnitLabel = new JLabel();
  JComboBox fCashComboBox = new JComboBox();
  JLabel fFeeLabel = new JLabel();
  JTextField fFeeTextField = new JTextField();
  JTextField fMarginTextField = new JTextField();
  JLabel fMarginLabel = new JLabel();
  JTextField fMaxLoanTextField = new JTextField();
  JTextField fTradingUnitTextField = new JTextField();
  JLabel fMaxLoanLabel = new JLabel();
  JLabel fTradingUnitLabel = new JLabel();
  JTextField fInterestTextField = new JTextField();
  JLabel fInterestLabel = new JLabel();
  JButton fCancelButton = new JButton();
  protected long fMinimuxCashUnit = 10000;
  JButton fDefaultButton = new JButton();

  public UInstitutionSettingDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public UInstitutionSettingDialog() {
    this(null, "", true);
  }

  /*
      public UInstitutionSettingDialog(UParameters pm) {
   this(null, UParameters.fRb.getString("TAB_SETTING"), true);
   fParam = pm;
      }
   */
  void jbInit() throws Exception {
    fInstitutionPanel.setLayout(null);
    fInstitutionPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    fInstitutionPanel.setPreferredSize(new Dimension(400, 280));
    fOKButton.setBounds(new Rectangle(51, 198, 79, 27));
    fOKButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fOKButton.setText("OK");
    fOKButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          fParam.setInitCash(Long.parseLong(fCashComboBox.getSelectedItem().
                                            toString()) * fMinimuxCashUnit);
          fParam.setFeePerUnit(Long.parseLong(fFeeTextField.getText()));
          fParam.setMarginRate(Long.parseLong(fMarginTextField.getText()));
          fParam.setMaxLoan(Long.parseLong(fMaxLoanTextField.getText()));
          fParam.setTradingUnit(Long.parseLong(fTradingUnitTextField.getText()));
          fParam.setInterest(Double.parseDouble(fInterestTextField.getText()));
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(UInstitutionSettingDialog.this,
                                        "A valid numerical value is needed.");
          return;
        }
        setVisible(false);
        dispose();
      }
    });
    this.setResizable(false);
    fLowCashLabel.setText(fRb.getString("LOWER_LIMIT_LABEL"));
    fLowCashLabel.setForeground(Color.black);
    fLowCashLabel.setBounds(new Rectangle(14, 62, 167, 21));
    fInitCashLabel.setText(fRb.getString("INITIAL_AMOUNT_OF_CASH"));
    fInitCashLabel.setForeground(Color.black);
    fInitCashLabel.setBounds(new Rectangle(13, 15, 150, 17));
    fMinCashUnitLabel.setBounds(new Rectangle(120, 42, 57, 17));
    fMinCashUnitLabel.setText("x " + String.valueOf(fMinimuxCashUnit));
    fMinCashUnitLabel.setForeground(Color.black);
    fCashComboBox.setBounds(new Rectangle(13, 39, 101, 22));
    fCashComboBox.setBackground(Color.white);
    fCashComboBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
      }
    });
    fCashComboBox.addItem("100");
    fCashComboBox.addItem("1000");
    fCashComboBox.addItem("10000");
    fCashComboBox.addItem("100000");
//    fCashComboBox.addItem("1000000");
    fCashComboBox.setSelectedIndex(3);
    fFeeLabel.setText(fRb.getString("FEE"));
    fFeeLabel.setForeground(Color.black);
    fFeeLabel.setBounds(new Rectangle(179, 19, 83, 17));
    fFeeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fFeeTextField.setBounds(new Rectangle(263, 17, 88, 21));
    fFeeTextField.setBounds(new Rectangle(263, 17, 88, 21));
    fMarginTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fMarginTextField.setBounds(new Rectangle(263, 46, 88, 21));
    fMarginLabel.setBounds(new Rectangle(179, 48, 83, 17));
    fMarginLabel.setText(fRb.getString("MARGIN"));
    fMarginLabel.setForeground(Color.black);
    fMaxLoanTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fMaxLoanTextField.setBounds(new Rectangle(263, 103, 88, 21));
    fTradingUnitTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fTradingUnitTextField.setBounds(new Rectangle(263, 74, 88, 21));
    fMaxLoanLabel.setText(fRb.getString("MAX_LOAN"));
    fMaxLoanLabel.setForeground(Color.black);
    fMaxLoanLabel.setBounds(new Rectangle(179, 105, 83, 17));
    fTradingUnitLabel.setBounds(new Rectangle(179, 76, 83, 17));
    fTradingUnitLabel.setText(fRb.getString("TRADING_UNIT"));
    fTradingUnitLabel.setForeground(Color.black);
    fInterestTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fInterestTextField.setBounds(new Rectangle(263, 131, 88, 21));
    fInterestLabel.setBounds(new Rectangle(179, 133, 83, 17));
    fInterestLabel.setText(fRb.getString("INTEREST"));
    fInterestLabel.setForeground(Color.black);
    fCancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
      }
    });
    fCancelButton.setText(fRb.getString("CANCEL"));
    fCancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fCancelButton.setBounds(new Rectangle(169, 198, 79, 27));
    fDefaultButton.setBounds(new Rectangle(287, 198, 79, 27));
    fDefaultButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fDefaultButton.setText(fRb.getString("DEFAULT"));
    fDefaultButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setDefault();
      }
    });
    this.getContentPane().add(fInstitutionPanel, BorderLayout.CENTER);
    fInstitutionPanel.add(fMinCashUnitLabel, null);
    fInstitutionPanel.add(fInitCashLabel, null);
    fInstitutionPanel.add(fCashComboBox, null);
    fInstitutionPanel.add(fLowCashLabel, null);
    fInstitutionPanel.add(fMarginTextField, null);
    fInstitutionPanel.add(fMaxLoanLabel, null);
    fInstitutionPanel.add(fTradingUnitLabel, null);
    fInstitutionPanel.add(fFeeLabel, null);
    fInstitutionPanel.add(fMarginLabel, null);
    fInstitutionPanel.add(fInterestLabel, null);
    fInstitutionPanel.add(fInterestTextField, null);
    fInstitutionPanel.add(fMaxLoanTextField, null);
    fInstitutionPanel.add(fTradingUnitTextField, null);
    fInstitutionPanel.add(fFeeTextField, null);
    fInstitutionPanel.add(fDefaultButton, null);
    fInstitutionPanel.add(fOKButton, null);
    fInstitutionPanel.add(fCancelButton, null);
    setDefault();
  }

  public void setDefault() {
    fFeeTextField.setText(String.valueOf(UParameters.DEFAULT_FEE_PER_UNIT));
    fMarginTextField.setText(String.valueOf(UParameters.DEFAULT_MARGIN_RATE));
    fMaxLoanTextField.setText(String.valueOf(UParameters.DEFAULT_MAX_LOAN));
    fTradingUnitTextField.setText(String.valueOf(UParameters.
                                                 DEFAULT_TRADING_UNIT));
    fInterestTextField.setText(String.valueOf(UParameters.DEFAULT_INTEREST));
    fCashComboBox.setSelectedIndex(3);
  }

  public static void main(String[] args) {
    UInstitutionSettingDialog id = new UInstitutionSettingDialog();
    id.setVisible(true);
  }
}
