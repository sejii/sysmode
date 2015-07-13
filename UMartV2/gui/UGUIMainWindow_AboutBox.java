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
 * UGUIMainWindow の AboutBox クラス．ヘルプ->バージョン情報で呼ばれる．
 */
public class UGUIMainWindow_AboutBox extends JDialog implements ActionListener {

  public static final int MARKET_SERVER = 0;
  public static final int MARKET_SIMULATOR = 1;
  public static final int MACHINE_AGENT_VIEWER = 2;
  public static final int TRADING_TERMINAL = 3;
  private static final String[] ABOUT_STRINGS = {"ABOUT_MARKET_SERVER",
      "ABOUT_MARKET_SIMULATOR",
      "ABOUT_MACHINE_AGENT_VIEWER",
      "ABOUT_TRADING_TERMINAL"};
  private static final String[] PRODUCT_STRINGS = {"MARKET_SERVER_PRODUCT",
      "MARKET_SIMULATOR_PRODUCT",
      "MACHINE_AGENT_VIEWER_PRODUCT",
      "TRADING_TERMINAL_PRODUCT"};
  private static final String[] VERSION_STRINGS = {"MARKET_SERVER_VERSION",
      "MARKET_SIMULATOR_VERSION",
      "MACHINE_AGENT_VIEWER_VERSION",
      "TRADING_TERMINAL_VERSION"};
  private static final String[] COPYRIGHT_STRINGS = {"MARKET_SERVER_COPYRIGHT",
      "MARKET_SIMULATOR_COPYRIGHT",
      "MACHINE_AGENT_VIEWER_COPYRIGHT",
      "TRADING_TERMINAL_COPYRIGHT"};
  private static final String[] COMMENTS_STRINGS = {"MARKET_SERVER_COMMENTS",
      "MARKET_SIMULATOR_COMMENTS",
      "MACHINE_AGENT_VIEWER_COMMENTS",
      "TRADING_TERMINAL_COMMENTS"};
  private int fType;
  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JPanel insetsPanel2 = new JPanel();
  JPanel insetsPanel3 = new JPanel();
  JButton button1 = new JButton();
  JLabel imageLabel = new JLabel();
  JLabel label1 = new JLabel();
  JLabel label2 = new JLabel();
  JLabel label3 = new JLabel();
  JLabel label4 = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  GridLayout gridLayout1 = new GridLayout();
  String product = "UMart GUI";
  String version = "Version 0.55";
  String copyright = "Copyright (c) 2003";
  String comments = "Naoki Mori & U-Mart Project";
  ResourceBundle fRb;

  public UGUIMainWindow_AboutBox(Frame parent, int type) {
    super(parent);
    fType = type;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
    pack();
  }

  //コンポーネントの初期化
  private void jbInit() throws Exception {
    fRb = UParameters.fRb;
    this.setTitle(fRb.getString(UGUIMainWindow_AboutBox.ABOUT_STRINGS[fType]));
    setResizable(false);
    panel1.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    insetsPanel1.setLayout(flowLayout1);
    insetsPanel2.setLayout(flowLayout1);
    insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    gridLayout1.setRows(4);
    gridLayout1.setColumns(1);
    label1.setText(fRb.getString(UGUIMainWindow_AboutBox.PRODUCT_STRINGS[fType]));
    label2.setText(fRb.getString(UGUIMainWindow_AboutBox.VERSION_STRINGS[fType]));
    label3.setText(fRb.getString(UGUIMainWindow_AboutBox.COPYRIGHT_STRINGS[
                                 fType]));
    label4.setText(fRb.getString(UGUIMainWindow_AboutBox.COMMENTS_STRINGS[fType]));
    insetsPanel3.setLayout(gridLayout1);
    insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
    button1.setText("Ok");
    button1.addActionListener(this);
    insetsPanel2.add(imageLabel, null);
    panel2.add(insetsPanel2, BorderLayout.WEST);
    this.getContentPane().add(panel1, null);
    insetsPanel3.add(label1, null);
    insetsPanel3.add(label2, null);
    insetsPanel3.add(label3, null);
    insetsPanel3.add(label4, null);
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2, BorderLayout.NORTH);
  }

  //ウィンドウが閉じられたときに終了するようにオーバーライド
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  //ダイアログを閉じる
  void cancel() {
    dispose();
  }

  //ボタンイベントでダイアログを閉じる
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == button1) {
      cancel();
    }
  }
}
