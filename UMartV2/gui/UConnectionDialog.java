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

public class UConnectionDialog extends JDialog {
  JPanel fRootPanel = new JPanel();
  JTextField fHostTextField = new JTextField();
  JTextField fPortTextField = new JTextField();
  JLabel fHostLabel = new JLabel();
  JLabel fPortLabel = new JLabel();
  JButton fOKButton = new JButton();
  JButton fQuitButton = new JButton();
  UParametersNet fParamNet = UParametersNet.getInstance();
  protected ResourceBundle fRb = UParameters.fRb;
  protected UProtocolForNetClient fCProtocol;
  protected UProtocolForNetClient fSuperUserProtocol;
  protected String fDefaultServerHostname = "localhost";

  public UConnectionDialog(UProtocolForNetClient cp, UProtocolForNetClient sucp, String hostname) {
    super( (Frame)null, "Connection", true);
    fDefaultServerHostname = hostname;
    fCProtocol = cp;
    fSuperUserProtocol = sucp;
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public UConnectionDialog(UProtocolForNetClient cp, UProtocolForNetClient sucp) {
    super( (Frame)null, "Connection", true);
    fCProtocol = cp;
    fSuperUserProtocol = sucp;
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    fRootPanel.setLayout(null);
    this.setSize(new Dimension(251, 154));
    fHostTextField.setText(fDefaultServerHostname);
    fHostTextField.setBounds(new Rectangle(115, 12, 109, 21));
    fPortTextField.setText(String.valueOf(fParamNet.getPort()));
    fPortTextField.setBounds(new Rectangle(115, 46, 109, 21));
    fHostLabel.setText("hostname:");
    fHostLabel.setBounds(new Rectangle(28, 15, 60, 15));
    fPortLabel.setText("port:");
    fPortLabel.setBounds(new Rectangle(28, 49, 60, 15));
    fOKButton.setBounds(new Rectangle(39, 91, 79, 27));
    fOKButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fOKButton.setText("OK");
    fOKButton.setForeground(Color.black);
    fOKButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (doConnect()) {
          setVisible(false);
          dispose();
        }
      }
    });
    fOKButton.addKeyListener(new KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        if (doConnect()) {
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
        if (doConnect()) {
          setVisible(false);
          dispose();
          System.exit(0);
        }
      }
    });
    this.getContentPane().add(fRootPanel, BorderLayout.CENTER);
    fRootPanel.add(fHostTextField, null);
    fRootPanel.add(fPortTextField, null);
    fRootPanel.add(fPortLabel, null);
    fRootPanel.add(fHostLabel, null);
    fRootPanel.add(fQuitButton, null);
    fRootPanel.add(fOKButton, null);
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

  private boolean doConnect() {
    try {
      fParamNet.setHostName(fHostTextField.getText());
      fParamNet.setPort(Integer.parseInt(fPortTextField.getText()));
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(UConnectionDialog.this,
                                    "A valid value is needed.");
      return false;
    }
    boolean isConnect = fCProtocol.setConnection(fParamNet.getHostName(),
                                                 fParamNet.getPort());
    boolean isSuConnect = fSuperUserProtocol.setConnection(fParamNet.
        getHostName(), fParamNet.getPort());
    if (isConnect && isSuConnect) {
      return true;
    } else {
      JOptionPane.showMessageDialog(UConnectionDialog.this,
                                    "Can not connect the server. Try again.");
      return false;
    }
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

  public String getServerName() {
    return fHostTextField.getText();
  }

  public int getServerPort() {
    return Integer.parseInt(fPortTextField.getText());
  }
}