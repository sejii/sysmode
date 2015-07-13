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
import java.net.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * メインウィンドウで表示するタブを決定用ダイアログ．
 */
public class UNewInstitutionSettingDialog extends JDialog {
  JPanel fInstitutionPanel = new JPanel();
  protected UParameters fParam = UParameters.getInstance();
  JButton fOKButton = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();
  ResourceBundle fRb = UParameters.fRb;
  JLabel fLowCashLabel = new JLabel();
  JLabel fInitCashLabel = new JLabel();
  JLabel fMinCashUnitLabel = new JLabel();
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
  protected long fMinimuxCashUnit = 1000000;
  JButton fDefaultButton = new JButton();
  JTextField fInitialCashTextField = new JTextField();
  JButton fFileButton = new JButton();
  JFileChooser fFileChooser = new JFileChooser();
  UInstitutionSetting fInstitution;

  public UNewInstitutionSettingDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    fInstitution = fParam.getInstitutionLog();
    try {
      jbInit();
      pack();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public UNewInstitutionSettingDialog() {
    this(null, "", true);
  }

  /*
      public UInstitutionSettingDialog(UParameters pm) {
   this(null, UParameters.fRb.getString("TAB_SETTING"), true);
   fParam = pm;
      }
   */
  void jbInit() throws Exception {
    // 初期資産Labelの設定
    fInitCashLabel.setText(fRb.getString("INITIAL_AMOUNT_OF_CASH"));
    fInitCashLabel.setForeground(Color.black);
    fInitCashLabel.setBounds(new Rectangle(13, 15, 150, 17));
    // 初期資産TextFieldの設定
    fInitialCashTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fInitialCashTextField.setBounds(new Rectangle(12, 38, 52, 21));
    // 単位初期資産Labelの設定
    fMinCashUnitLabel.setBounds(new Rectangle(67, 42, 106, 17));
    fMinCashUnitLabel.setText(fRb.getString("MILLION_YEN"));
    fMinCashUnitLabel.setForeground(Color.black);
    // 最小資産Labelの設定
    fLowCashLabel.setText(fRb.getString("LOWER_LIMIT_LABEL"));
    fLowCashLabel.setForeground(Color.black);
    fLowCashLabel.setBounds(new Rectangle(14, 62, 167, 21));
    // 手数料Labelの設定
    fFeeLabel.setText(fRb.getString("FEE"));
    fFeeLabel.setForeground(Color.black);
    fFeeLabel.setBounds(new Rectangle(179, 19, 83, 17));
    // 手数料TextFieldの設定
    fFeeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fFeeTextField.setBounds(new Rectangle(263, 17, 88, 21));
    // 証拠金Labelの設定
    fMarginLabel.setBounds(new Rectangle(179, 48, 83, 17));
    fMarginLabel.setText(fRb.getString("MARGIN"));
    fMarginLabel.setForeground(Color.black);
    // 証拠金TextFieldの設定
    fMarginTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fMarginTextField.setBounds(new Rectangle(263, 46, 88, 21));
    // 取引単位Labelの設定
    fTradingUnitLabel.setBounds(new Rectangle(179, 76, 83, 17));
    fTradingUnitLabel.setText(fRb.getString("TRADING_UNIT"));
    fTradingUnitLabel.setForeground(Color.black);
    // 取引単位TextFieldの設定
    fTradingUnitTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fTradingUnitTextField.setBounds(new Rectangle(263, 74, 88, 21));
    // 最大融資額Labelの設定
    fMaxLoanLabel.setText(fRb.getString("LOAN_LIMIT"));
    fMaxLoanLabel.setForeground(Color.black);
    fMaxLoanLabel.setBounds(new Rectangle(179, 105, 83, 17));
    // 最大融資額TextFieldの設定
    fMaxLoanTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fMaxLoanTextField.setBounds(new Rectangle(263, 103, 88, 21));
    // 金利Labelの設定
    fInterestLabel.setBounds(new Rectangle(179, 133, 83, 17));
    fInterestLabel.setText(fRb.getString("INTEREST_RATE"));
    fInterestLabel.setForeground(Color.black);
    // 金利TextFieldの設定
    fInterestTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fInterestTextField.setBounds(new Rectangle(263, 131, 88, 21));
    // 各TextFieldの設定
    setupTextFields();
    // ファイルボタンの設定
    fFileButton.setText(fRb.getString("FILE"));
    fFileButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fFileButton.setBounds(new Rectangle(52, 158, 79, 27));
    fFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          int state = fFileChooser.showOpenDialog(null);
          if (state == JFileChooser.CANCEL_OPTION) {
            return;
          }
          File file = fFileChooser.getSelectedFile();
          BufferedReader br = new BufferedReader(new FileReader(file));
          fInstitution.readFrom(br);
          setupTextFields();
          br.close();
        } catch (Exception ex) {
          ex.printStackTrace();
          return;
        }
      }
    });
    // OKボタンの設定
    fOKButton.setBounds(new Rectangle(51, 198, 79, 27));
    fOKButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fOKButton.setText("OK");
    fOKButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          fParam.setInitCash(Long.parseLong(fInitialCashTextField.getText()) *
                             fMinimuxCashUnit);
          fInstitution.setInitialCash(Long.parseLong(fInitialCashTextField.
              getText()) * fMinimuxCashUnit);
          fParam.setFeePerUnit(Long.parseLong(fFeeTextField.getText()));
          fInstitution.setFeePerUnit(Long.parseLong(fFeeTextField.getText()));
          fParam.setMarginRate(Long.parseLong(fMarginTextField.getText()));
          fInstitution.setMarginRate(Long.parseLong(fMarginTextField.getText()));
          fParam.setMaxLoan(Long.parseLong(fMaxLoanTextField.getText()));
          fInstitution.setMaxLoan(Long.parseLong(fMaxLoanTextField.getText()));
          fParam.setTradingUnit(Long.parseLong(fTradingUnitTextField.getText()));
          fInstitution.setTradingUnit(Long.parseLong(fTradingUnitTextField.
              getText()));
          fParam.setInterest(Double.parseDouble(fInterestTextField.getText()));
          fInstitution.setInterest(Double.parseDouble(fInterestTextField.
              getText()));
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(UNewInstitutionSettingDialog.this,
                                        "A valid numerical value is needed.");
          return;
        }
        setVisible(false);
        dispose();
      }
    });
    // Cancelボタンの設定
    fCancelButton.setText(fRb.getString("CANCEL"));
    fCancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fCancelButton.setBounds(new Rectangle(169, 198, 79, 27));
    fCancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
      }
    });
    // Defaultボタンの設定
    fDefaultButton.setBounds(new Rectangle(287, 198, 79, 27));
    fDefaultButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fDefaultButton.setText(fRb.getString("DEFAULT"));
    fDefaultButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setDefault();
      }
    });
    // ベースのPanelの設定
    fInstitutionPanel.setLayout(null);
    fInstitutionPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    fInstitutionPanel.setPreferredSize(new Dimension(400, 280));
    fInstitutionPanel.add(fMinCashUnitLabel, null);
    fInstitutionPanel.add(fInitCashLabel, null);
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
    fInstitutionPanel.add(fInitialCashTextField, null);
    fInstitutionPanel.add(fFileButton, null);
    // Dialogウィンドウの設定
    this.setResizable(false);
    this.getContentPane().add(fInstitutionPanel, BorderLayout.CENTER);
  }

  public void setDefault() {
    fInstitution = fParam.getInstitutionLog();
    try {
      URL institutionURL = getClass().getResource(UParameters.INSTITUTION);
      BufferedReader br = new BufferedReader(new InputStreamReader(
          institutionURL.openStream()));
      fInstitution.readFrom(br);
      br.close();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(5);
    }
    setupTextFields();
  }

  private void setupTextFields() {
    fInitialCashTextField.setText(String.valueOf(fInstitution.getInitialCash() /
                                                 fMinimuxCashUnit));
    fFeeTextField.setText(String.valueOf(fInstitution.getFeePerUnit()));
    fMarginTextField.setText(String.valueOf(fInstitution.getMarginRate()));
    fMaxLoanTextField.setText(String.valueOf(fInstitution.getMaxLoan()));
    fTradingUnitTextField.setText(String.valueOf(fInstitution.getTradingUnit()));
    fInterestTextField.setText(String.valueOf(fInstitution.getInterest()));
  }

  public static void main(String[] args) {
    UNewInstitutionSettingDialog id = new UNewInstitutionSettingDialog();
    id.setVisible(true);
  }

  void fFileButton_actionPerformed(ActionEvent e) {

  }
}
