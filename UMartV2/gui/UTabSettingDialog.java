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
public class UTabSettingDialog extends JDialog {

  JPanel fPanel = new JPanel();
  protected UParameters fParam = UParameters.getInstance();
  JButton fOKButton = new JButton();
  JCheckBox fChartCheckBox = new JCheckBox();
  JCheckBox fProfitCheckBox = new JCheckBox();
  JCheckBox fOrderCheckBox = new JCheckBox();
  JCheckBox fOrderResultCheckBox = new JCheckBox();
  JCheckBox fQuotationCheckBox = new JCheckBox();
  JCheckBox fPositionCheckBox = new JCheckBox();
  JCheckBox fStdoutCheckBox = new JCheckBox();
  BorderLayout borderLayout1 = new BorderLayout();
  ResourceBundle fRb = UParameters.fRb;

  public UTabSettingDialog(Frame frame, boolean modal) {
    super(frame, "Tab Setting", modal);
    try {
      jbInit();
      pack();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public UTabSettingDialog() {
    this(null, false);
  }

  void jbInit() throws Exception {
    // チャートCheckBoxの設定
    fChartCheckBox.setSelected(isTabAvailable(UPanelFactory.CHART,
                                              fParam.getTabs()));
    fChartCheckBox.setText(fRb.getString("CHART"));
    fChartCheckBox.setBounds(new Rectangle(41, 22, 90, 25));
    // 板情報CheckBoxの設定
    fProfitCheckBox.setSelected(isTabAvailable(UPanelFactory.BOARD,
                                               fParam.getTabs()));
    fProfitCheckBox.setText(fRb.getString("ORDER_BOOK"));
    fProfitCheckBox.setBounds(new Rectangle(41, 44, 90, 25));
    // 現在の注文CheckBoxの設定
    fOrderCheckBox.setSelected(isTabAvailable(UPanelFactory.ORDER,
                                              fParam.getTabs()));
    fOrderCheckBox.setText(fRb.getString("ORDER"));
    fOrderCheckBox.setBounds(new Rectangle(41, 67, 90, 25));
    // 注文結果CheckBoxの設定
    fOrderResultCheckBox.setSelected(isTabAvailable(UPanelFactory.ORDER_RESULT,
        fParam.getTabs()));
    fOrderResultCheckBox.setText(fRb.getString("CONTRACTS"));
    fOrderResultCheckBox.setBounds(new Rectangle(41, 89, 90, 25));
    // 利益CheckBoxの設定
    fQuotationCheckBox.setSelected(isTabAvailable(UPanelFactory.PROFIT,
                                                  fParam.getTabs()));
    fQuotationCheckBox.setText(fRb.getString("PROFIT"));
    fQuotationCheckBox.setBounds(new Rectangle(41, 112, 90, 25));
    // ポジションCheckBoxの設定
    fPositionCheckBox.setSelected(isTabAvailable(UPanelFactory.POSITION,
                                                 fParam.getTabs()));
    fPositionCheckBox.setText(fRb.getString("POSITION"));
    fPositionCheckBox.setBounds(new Rectangle(41, 134, 90, 25));
    // 標準出力CheckBoxの設定
    fStdoutCheckBox.setSelected(isTabAvailable(UPanelFactory.STDOUT,
                                               fParam.getTabs()));
    fStdoutCheckBox.setText(fRb.getString("STDOUT"));
    fStdoutCheckBox.setBounds(new Rectangle(41, 157, 90, 25));
    // OKボタンの設定
    fOKButton.setBounds(new Rectangle(46, 198, 79, 27));
    fOKButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fOKButton.setText("OK");
    fOKButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fParam.getTabs().clear();
        if (fChartCheckBox.isSelected()) {
          fParam.getTabs().add(UPanelFactory.CHART);
        }
        if (fQuotationCheckBox.isSelected()) {
            fParam.getTabs().add(UPanelFactory.BOARD);
        }
        if (fOrderCheckBox.isSelected()) {
          fParam.getTabs().add(UPanelFactory.ORDER);
        }
        if (fOrderResultCheckBox.isSelected()) {
          fParam.getTabs().add(UPanelFactory.ORDER_RESULT);
        }
        if (fProfitCheckBox.isSelected()) {
            fParam.getTabs().add(UPanelFactory.PROFIT);
        }
        if (fPositionCheckBox.isSelected()) {
          fParam.getTabs().add(UPanelFactory.POSITION);
        }
        if (fStdoutCheckBox.isSelected()) {
          fParam.getTabs().add(UPanelFactory.STDOUT);
        }
        setVisible(false);
        dispose();
      }
    });
    // ベースのパネルの設定
    fPanel.setLayout(null);
    fPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    fPanel.setPreferredSize(new Dimension(180, 250));
    fPanel.add(fQuotationCheckBox, null);
    fPanel.add(fChartCheckBox, null);
    fPanel.add(fStdoutCheckBox, null);
    fPanel.add(fPositionCheckBox, null);
    fPanel.add(fOrderResultCheckBox, null);
    fPanel.add(fOrderCheckBox, null);
    fPanel.add(fProfitCheckBox, null);
    fPanel.add(fOKButton, null);
    // ダイアログの設定
    this.setResizable(false);
    this.getContentPane().add(fPanel, BorderLayout.CENTER);
  }

  private boolean isTabAvailable(String tabName, ArrayList access) {
    Iterator itr = access.iterator();
    while (itr.hasNext()) {
      String name = (String) itr.next();
      if (name.equals(tabName)) {
        return true;
      }
    }
    return false;
  }

}
