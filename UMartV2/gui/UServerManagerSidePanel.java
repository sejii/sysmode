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

import java.awt.*;
import javax.swing.*;

public class UServerManagerSidePanel extends JPanel {
  UServerManagerSideChartPanel fServerChartPanel = new
      UServerManagerSideChartPanel();
  // UServerManagerBoardTablePanel fBoardTablePanel = new UServerManagerBoardTablePanel();

  public UServerManagerSidePanel() {
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(null);
    fServerChartPanel.setBackground(Color.white);
    fServerChartPanel.setBounds(new Rectangle(9, 4, 256, 177));
    this.setBackground(Color.lightGray);
    // fBoardTablePanel.setBackground(Color.lightGray);
    // fBoardTablePanel.setBounds(new Rectangle(9, 190, 260, 375));
    this.add(fServerChartPanel, null);
    // this.add(fBoardTablePanel, null);
  }

  public void gUpdate() {
    //この板だけはステップの切れ目以外でも更新が必要。
    // fBoardTablePanel.gUpdate();
  }
}