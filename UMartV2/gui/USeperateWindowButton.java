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

public class USeperateWindowButton extends JButton {

  protected JFrame fSubWindow;
  protected UGraph fSubUGraph;
  protected UGraph fUGraph;
  protected String fSubWindowTitle = "Graph";
  static protected ResourceBundle fRb = UParameters.fRb;

  public USeperateWindowButton() {
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setGraph(UGraph graph) {
    fUGraph = graph;
  }

  public boolean isSubWindowAvailable() {
    if (fSubWindow != null) {
      return true;
    } else {
      return false;
    }
  }

  public void setSubWindowTitle(String title) {
    fSubWindowTitle = title;
    if (fSubWindow != null) {
      fSubWindow.setTitle(fSubWindowTitle);
    }
  }

  public void repaintSubWindow() {
    if (isSubWindowAvailable()) {
      fSubWindow.repaint();
    }
  }

  void jbInit() throws Exception {
    setBorder(BorderFactory.createRaisedBevelBorder());
    setText(fRb.getString("SEPARATE_WINDOW"));
    setForeground(Color.black);
    addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // ‚Í‚¶‚ß‚Í null ‚¾‚ª dispose ‚µ‚Ä‚à‚·‚® null ‚É‚È‚é‚Æ‚ÍŒÀ‚ç‚È‚¢‚Ì‚Å isDisplayable() •¹—p
        if (fSubWindow == null || fSubWindow.isDisplayable() == false) {
          fSubWindow = new JFrame(fSubWindowTitle);
          fSubWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
              fSubWindow.setVisible(false);
              fSubWindow.dispose();
              fSubWindow = null;
            }
          });
          fSubUGraph = fUGraph.copyGraph();
          int noOfVerticalLines = fSubUGraph.getNumOfVerticalLine();
          fSubWindow.getContentPane().add(fSubUGraph);
          fSubWindow.setBounds(new Rectangle(100, 100, 240, 180));
          fSubWindow.pack();
          fSubWindow.setVisible(true);
        }
      }
    });
  }
}
