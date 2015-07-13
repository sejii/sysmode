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

public class UGraphXrangePanel extends JPanel {

  protected JCheckBox fAutoXrangeCheckBox = new JCheckBox();
  protected JTextField fXrangeInterval = new JTextField();
  protected JLabel fXrangeIntervalLabel = new JLabel();
  protected JButton fXrangeUpdate = new JButton();
  protected double fInitInterval = 20;
  protected UGraph fUGraph = null;
  static protected ResourceBundle fRb = UParameters.fRb;

  public UGraphXrangePanel() {
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setGraph(UGraph graph) {
    fUGraph = graph;
  }

  void jbInit() throws Exception {
    // fAutoXrangeCheckBoxÇÃê›íË
    fAutoXrangeCheckBox.setBounds(new Rectangle(3, 5, 94, 25));
    fAutoXrangeCheckBox.setText(fRb.getString("AUTO_X"));
    fAutoXrangeCheckBox.setForeground(Color.black);
    fAutoXrangeCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fUGraph.setXAuto(fAutoXrangeCheckBox.isSelected());
      }
    });

    // fXrangeIntervalÇÃê›íË
    fXrangeInterval.setBounds(new Rectangle(82, 48, 93, 22));
    fXrangeInterval.setHorizontalAlignment(JTextField.RIGHT);
    fXrangeInterval.setText(String.valueOf( (int) fInitInterval));
    fXrangeInterval.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fXrangeUpdate.requestFocus();
      }
    });
    // fXrangeIntervalLabelÇÃê›íË
    fXrangeIntervalLabel.setText(fRb.getString("PERIOD"));
    fXrangeIntervalLabel.setForeground(Color.black);
    fXrangeIntervalLabel.setHorizontalAlignment(SwingConstants.CENTER);
    fXrangeIntervalLabel.setBounds(new Rectangle(8, 51, 68, 17));
    fXrangeUpdate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int num;
        try {
          num = Integer.parseInt(fXrangeInterval.getText().trim());
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(UParameters.getInstance().
                                        getMainComponet(),
                                        fRb.getString("NUMERICAL_VALUE_NEEDED"));
          return;
        }
        if (num <= 0) {
          JOptionPane.showMessageDialog(UParameters.getInstance().
                                        getMainComponet(),
                                        fRb.getString("POSITIVE_VALUE_NEEDED"));
          return;
        } else {
          fUGraph.setNumOfPlotData(num);
        }
        fUGraph.repaint();
      }
    });
    // fXrangeUpdateÇÃê›íË
    fXrangeUpdate.setText(fRb.getString("UPDATE"));
    fXrangeUpdate.setForeground(Color.black);
    fXrangeUpdate.setBounds(new Rectangle(98, 7, 76, 30));
    // fXrangePanelÇÃê›íË
    setLayout(null);
    add(fAutoXrangeCheckBox, null);
    add(fXrangeInterval, null);
    add(fXrangeIntervalLabel, null);
    add(fXrangeUpdate, null);
    setBorder(BorderFactory.createEtchedBorder());
    setBounds(new Rectangle(244, 362, 185, 79));
  }

  public void setXAuto(boolean b) {
    fAutoXrangeCheckBox.setSelected(b);
  }
}