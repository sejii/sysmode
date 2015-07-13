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

public class UBugReportWindow extends JFrame {
  BorderLayout fBorderLayout = new BorderLayout();
  JLabel fPromptLabel = new JLabel();
  JTextArea fMessageArea = new JTextArea();
  JPanel fPanel = new JPanel();
  JButton fSendButton = new JButton();
  ResourceBundle fRb = UParameters.fRb;
  BufferedReader fIn;
  PrintWriter fOut;

  public UBugReportWindow() {
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    this.setSize(new Dimension(400, 200));
    fPromptLabel.setText(fRb.getString("REPORT_PROMPT"));
    this.getContentPane().setLayout(fBorderLayout);
    fMessageArea.setBorder(BorderFactory.createLineBorder(Color.black));
    fSendButton.setText(fRb.getString("REPORT_BUTTON"));
    fSendButton.addActionListener(new UBugReportWindow_sendButton_actionAdapter(this));
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setTitle("Bug Report");
    this.getContentPane().add(fPromptLabel, BorderLayout.NORTH);
    this.getContentPane().add(fMessageArea, BorderLayout.CENTER);
    this.getContentPane().add(fPanel, BorderLayout.SOUTH);
    fPanel.add(fSendButton, null);
  }

  void sendButton_actionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        sendMail();
      }
    });
  }

  public void sendMail() {
    try {
      Socket s = new Socket(UParameters.SMTP_SERVER, 25);
      fOut = new PrintWriter(new OutputStreamWriter(s.getOutputStream(),
          "ISO-2022-JP"));
      fIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
      String hostName = InetAddress.getLocalHost().getHostName();
      send(null);
      send("EHLO " + hostName);
      send("AUTH LOGIN");
      send("eWpfaHNhdG8=");
      send("bmRhc2F0bw==");
      send("MAIL FROM: " + UParameters.MAIL_FROM);
      send("RCPT TO: " + UParameters.MAIL_TO1);
      send("RCPT TO: " + UParameters.MAIL_TO2);
      send("RCPT TO: " + UParameters.MAIL_TO3);
      send("DATA");
      fOut.println("Subject: [UMartSystem] Bug Report");
      System.out.println(fMessageArea.getText());
      Properties systemProperties = System.getProperties();
      Enumeration e1 = systemProperties.propertyNames();
      while (e1.hasMoreElements()) {
        String key = (String) e1.nextElement();
        System.out.println(key + "=" + systemProperties.getProperty(key));
        fOut.println(key + "=" + systemProperties.getProperty(key));
      }
      fOut.println("---");
      fOut.println(fMessageArea.getText());
      send(".");
      s.close();
      dispose();
      JOptionPane.showMessageDialog(this, fRb.getString("REPORT_MESSAGE"));
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public void send(String s) throws IOException {
    if (s != null) {
      // System.out.println(s);
      fOut.println(s);
      fOut.flush();
    }
    String line;
    if ( (line = fIn.readLine()) != null) {
      // System.out.println(line);
    }
  }
}

class UBugReportWindow_sendButton_actionAdapter implements java.awt.event.
    ActionListener {
  UBugReportWindow adaptee;

  UBugReportWindow_sendButton_actionAdapter(UBugReportWindow adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.sendButton_actionPerformed(e);
  }
}
