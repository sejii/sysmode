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
import javax.swing.border.*;

public class URandomSeedPanel extends JPanel {

  protected UParameters fParam = UParameters.getInstance();
  ResourceBundle fRb = UParameters.fRb;
  JPanel fSeedPanel = new JPanel();
  JRadioButton fAutoSeedButton = new JRadioButton();
  JTextField fSeedTextField = new JTextField();
  JRadioButton fSetSeedButton = new JRadioButton();
  ButtonGroup fSeedButtonGroup = new ButtonGroup();
  TitledBorder titledBorder2;

  public URandomSeedPanel() {
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    // システム時刻使用設定ボタンの設定
    fAutoSeedButton.setToolTipText("");
    fAutoSeedButton.setText(fRb.getString("SYSTEM_CLOCK"));
    fAutoSeedButton.setBounds(new Rectangle(25, 20, 158, 25));
    fAutoSeedButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fSeedTextField.setEditable(false);
        fParam.setIsSeed(false);
      }
    });
    fSeedButtonGroup.add(fAutoSeedButton);
    // シード指定ボタンの設定
    fSetSeedButton.setText(fRb.getString("USER"));
    fSetSeedButton.setBounds(new Rectangle(25, 45, 144, 25));
    fSetSeedButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fSeedTextField.setEditable(true);
        fParam.setIsSeed(true);
        long seed;
        try {
          seed = Long.parseLong(fSeedTextField.getText());
        } catch (Exception ex) {
          fSeedTextField.setText(String.valueOf(UParameters.DEFAULT_SEED));
          seed = UParameters.DEFAULT_SEED;
        }
        fParam.setSeed(seed);
      }
    });
    // シード指定TextFieldの設定
    fSeedTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fSeedTextField.setBounds(new Rectangle(50, 74, 63, 21));
    fSeedTextField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        long seed;
        try {
          seed = Long.parseLong(fSeedTextField.getText().trim());
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, "A numerical value is needed.");
          return;
        }
        fParam.setSeed(seed);
        fSetSeedButton.requestFocus();
      }
    });
    fSeedButtonGroup.add(fSetSeedButton);
    // 乱数シードパネルの設定
    fSeedPanel.setBounds(new Rectangle(2, 2, 216, 106));
    fSeedPanel.setLayout(null);
    titledBorder2 = new TitledBorder(BorderFactory.createEtchedBorder(Color.
        white, new Color(148, 145, 140)), fRb.getString("RANDOM_SEED"));
    titledBorder2.setTitleColor(Color.black);
    fSeedPanel.setBorder(titledBorder2);
    fSeedPanel.add(fAutoSeedButton, null);
    fSeedPanel.add(fSetSeedButton, null);
    fSeedPanel.add(fSeedTextField, null);

    setLayout(null);
    this.add(fSeedPanel, null);
  }

  public void setDefault() {
    fSeedTextField.setEditable(UParameters.DEFAULT_IS_SEED);
    fSeedTextField.setText(String.valueOf(UParameters.DEFAULT_SEED));
    fAutoSeedButton.setSelected(!UParameters.DEFAULT_IS_SEED);
    fSetSeedButton.setSelected(UParameters.DEFAULT_IS_SEED);
  }

  public long getSeed() {
    if (fAutoSeedButton.isSelected()) {
      return System.currentTimeMillis();
    } else {
      return Long.parseLong(fSeedTextField.getText());
    }
  }

}