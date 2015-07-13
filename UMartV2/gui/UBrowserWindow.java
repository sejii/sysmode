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
import javax.swing.event.*;

public class UBrowserWindow extends JFrame {
  BorderLayout fBorderLayout = new BorderLayout();
  JPanel fPanel = new JPanel();
  JLabel fUrlLabel = new JLabel();
  JTextField fUrlField = new JTextField(30);
  JButton fLoadButton = new JButton();
  JButton fBackButton = new JButton();
  JScrollPane fScrollPane = new JScrollPane();
  JEditorPane fEditorPane = new JEditorPane();
  ResourceBundle fRb = UParameters.fRb;
  String fFirstPage;
  Stack fUrlStack = new Stack();

  public UBrowserWindow() {
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public UBrowserWindow(String s) {
    fFirstPage = s;
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    this.getContentPane().setLayout(fBorderLayout);
    this.setSize(new Dimension(600, 600));
    this.setTitle("Simple Browser");
    fUrlLabel.setText("URL");
    fLoadButton.setText(fRb.getString("BROWSER_LOAD"));
    fLoadButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadButton_actionPerformed(e);
      }
    });
    fBackButton.setText(fRb.getString("BROWSER_BACK"));
    fBackButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        backButton_actionPerformed(e);
      }
    });
    fEditorPane.setEditable(false);
    fEditorPane.setText("");
    fEditorPane.addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        editorPane_actionPerformed(e);
      }
    });
    if (fFirstPage != null) {
      try {
        fUrlStack.push(fFirstPage);
        fUrlField.setText(fFirstPage);
        fEditorPane.setPage(new URL(fFirstPage));
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    this.getContentPane().add(fPanel, BorderLayout.NORTH);
    fPanel.add(fUrlLabel, null);
    fPanel.add(fUrlField, null);
    fPanel.add(fLoadButton, null);
    fPanel.add(fBackButton, null);
    this.getContentPane().add(fScrollPane, BorderLayout.CENTER);
    fScrollPane.getViewport().add(fEditorPane, null);
  }

  protected void editorPane_actionPerformed(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      try {
        fUrlStack.push(e.getURL().toString());
        fUrlField.setText(e.getURL().toString());
        fEditorPane.setPage(e.getURL());
      } catch (IOException ex) {
        fEditorPane.setText("Error: " + ex);
      }
    }
  }

  protected void loadButton_actionPerformed(ActionEvent e) {
    try {
      fUrlStack.push(fUrlField.getText());
      fEditorPane.setPage(fUrlField.getText());
    } catch (IOException ex) {
      fEditorPane.setText("Error: " + ex);
    }
  }

  protected void backButton_actionPerformed(ActionEvent e) {
    if (fUrlStack.size() <= 1) {
      return;
    }
    try {
      fUrlStack.pop();
      String urlString = (String) fUrlStack.peek();
      fUrlField.setText(urlString);
      fEditorPane.setPage(urlString);
    } catch (IOException ex) {
      fEditorPane.setText("Error: " + ex);
    }
  }
}
