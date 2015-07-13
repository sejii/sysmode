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

import cmdClientNet.*;
import cmdCore.*;

public class ULoginDialog extends JDialog {
  JPanel fRootPanel = new JPanel();
  JTextField fAccountTextField = new JTextField();
  JPasswordField fPasswordField = new JPasswordField();
  JLabel fAccountLabel = new JLabel();
  JLabel fPasswordLabel = new JLabel();
  JButton fOKButton = new JButton();
  JButton fQuitButton = new JButton();
  UParametersNet fParamNet = UParametersNet.getInstance();
  protected ResourceBundle fRb = UParameters.fRb;
  protected UProtocolForNetClient fCProtocol;

  public ULoginDialog(UProtocolForNetClient cp, String serverName, int serverPort) {
    super( (Frame)null, "Welcome to " + serverName + ":" + serverPort, true);
    fCProtocol = cp;
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    fRootPanel.setLayout(null);
    this.setSize(new Dimension(251, 154));
    // fAccountTextField.setText(fParamNet.getAccountName());
    fAccountTextField.setBounds(new Rectangle(115, 12, 109, 21));
    fPasswordField.setBounds(new Rectangle(115, 46, 109, 21));
    fAccountLabel.setText("Member ID:");
    fAccountLabel.setBounds(new Rectangle(18, 15, 83, 15));
    fPasswordLabel.setText("Password:");
    fPasswordLabel.setBounds(new Rectangle(18, 49, 83, 15));
    fOKButton.setBounds(new Rectangle(39, 91, 79, 27));
    fOKButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fOKButton.setText("OK");
    fOKButton.setForeground(Color.black);
    fOKButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (doLogin()) {
          setVisible(false);
          dispose();
        }
      }
    });
    fOKButton.addKeyListener(new KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        if (doLogin()) {
          setVisible(false);
          dispose();
        }
      }
    });
    fQuitButton.setBounds(new Rectangle(140, 91, 79, 27));
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
    fQuitButton.addKeyListener(new KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        setVisible(false);
        dispose();
        System.exit(0);
      }
    });
    this.getContentPane().add(fRootPanel, BorderLayout.CENTER);
    fRootPanel.add(fAccountTextField, null);
    fRootPanel.add(fPasswordField, null);
    fRootPanel.add(fAccountLabel, null);
    fRootPanel.add(fQuitButton, null);
    fRootPanel.add(fOKButton, null);
    fRootPanel.add(fPasswordLabel, null);
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

  private boolean doLogin() {
    String passwd;
    try {
      fParamNet.setAccountName(fAccountTextField.getText());
      passwd = new String(fPasswordField.getPassword());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(ULoginDialog.this,
                                    "A valid value is needed.");
      return false;
    }
    UCLoginCore loginCmd = (UCLoginCore) fCProtocol.getCommand(UCLoginCore.
        CMD_NAME);
    loginCmd.setArguments(fParamNet.getAccountName(), passwd);
    UCommandStatus cmdStatus = loginCmd.doIt();
    if (!cmdStatus.getStatus()) {
      JOptionPane.showMessageDialog(ULoginDialog.this,
                                    "Cannot login. Try again.");
      return false;
    } else {
      return true;
    }
  }

  public static void main(String[] args) {
    ULoginDialog ucd = new ULoginDialog(new UProtocolForNetClient(), "localhost", 5010);
    ucd.setVisible(true);
  }

  //ウィンドウが閉じられたときに終了するようにオーバーライド
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      setVisible(false);
      dispose();
      System.exit( -1);
    }
  }
}