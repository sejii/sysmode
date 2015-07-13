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


public class UGUITypeSetFrame extends JDialog {
  JPanel contentPane;
  JPanel fGUIRootPanel = new JPanel();
  ButtonGroup buttonGroup1 = new ButtonGroup();
  JRadioButton fSARadioButton = new JRadioButton();
  JRadioButton fNetRadioButton = new JRadioButton();
  JButton fOKButton = new JButton();
  JButton fQuitButton = new JButton();
  protected UParameters fParam = UParameters.getInstance();
  protected ResourceBundle fRb = UParameters.fRb;
  JLabel jLabel1 = new JLabel();
  public UGUITypeSetFrame() {
    super( (Frame)null, "GUI Type Setting", true);
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    this.setResizable(false);
    setSize(new Dimension(250, 200));
    fGUIRootPanel.setLayout(null);
    fSARadioButton.setText("Stand Alone");
    fSARadioButton.setBounds(new Rectangle(61, 49, 134, 23));
    fSARadioButton.setSelected(true);
    fNetRadioButton.setText("Network");
    fNetRadioButton.setBounds(new Rectangle(61, 83, 134, 23));
    fOKButton.setBounds(new Rectangle(41, 128, 79, 27));
    fOKButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fOKButton.setText("OK");
    fOKButton.setForeground(Color.black);
    fOKButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (fSARadioButton.isSelected()) {
          fParam.setConnectionType(UParameters.CONNECTION_TYPE_STAND_ALONE);
        } else if (fNetRadioButton.isSelected()) {
          fParam.setConnectionType(UParameters.CONNECTION_TYPE_NETWORK);
        }
        setVisible(false);
        dispose();
      }
    });
    fQuitButton.setBounds(new Rectangle(142, 128, 79, 27));
    fQuitButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fQuitButton.setText(fRb.getString("QUIT"));
    fQuitButton.setForeground(Color.black);
    fQuitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
        System.exit(0);
      }
    });
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setText("Welcome to U-Mart");
    jLabel1.setBounds(new Rectangle(34, 8, 168, 22));
    contentPane.add(fGUIRootPanel, BorderLayout.CENTER);
    fGUIRootPanel.add(fQuitButton, null);
    fGUIRootPanel.add(fOKButton, null);
    fGUIRootPanel.add(jLabel1, null);
    fGUIRootPanel.add(fSARadioButton, null);
    fGUIRootPanel.add(fNetRadioButton, null);
    buttonGroup1.add(fSARadioButton);
    buttonGroup1.add(fNetRadioButton);
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        setVisible(false);
        dispose();
      }
    });
    //ウィンドウを中央に配置
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension mySize = this.getSize();
    if (mySize.height > screenSize.height) {
      mySize.height = screenSize.height;
    }
    if (mySize.width > screenSize.width) {
      mySize.width = screenSize.width;
    }
    setLocation( (screenSize.width - mySize.width) / 2,
                (screenSize.height - mySize.height) / 2);
  }

  public static void main(String[] args) {
    UGUITypeSetFrame UGUIRootDialog1 = new UGUITypeSetFrame();
    UGUIRootDialog1.setVisible(true);
  }
}