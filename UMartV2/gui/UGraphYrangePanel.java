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

public class UGraphYrangePanel extends JPanel {

  protected JCheckBox fAutoYrangeCheckBox = new JCheckBox();
  protected JTextField fYrangeMax = new JTextField();
  protected JTextField fYrangeMin = new JTextField();
  protected JLabel fYrangeMaxLabel = new JLabel();
  protected JLabel fYrangeMinLabel = new JLabel();
  protected JButton fYrangeUpdate = new JButton();
  protected UGraph fUGraph = null;
  static protected ResourceBundle fRb = UParameters.fRb;

  public UGraphYrangePanel() {
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setGraph(UGraph graph) {
    fUGraph = graph;
    fYrangeMax.setText(String.valueOf( (int) fUGraph.getFixedMaxY()));
    fYrangeMin.setText(String.valueOf( (int) fUGraph.getFixedMinY()));
  }

  void jbInit() throws Exception {
    // fAutoYrangeCheckBoxÇÃê›íË
    fAutoYrangeCheckBox.setText(fRb.getString("AUTO_Y"));
    fAutoYrangeCheckBox.setForeground(Color.black);
    fAutoYrangeCheckBox.setBounds(new Rectangle(3, 5, 97, 25));
    fAutoYrangeCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fUGraph.setYAuto(fAutoYrangeCheckBox.isSelected());
      }
    });
    // fYrangeUpdateÇÃê›íË
    fYrangeUpdate.setBounds(new Rectangle(100, 7, 76, 30));
    fYrangeUpdate.setText(fRb.getString("UPDATE"));
    fYrangeUpdate.setForeground(Color.black);
    fYrangeUpdate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        double max;
        double min;
        try {
          max = Double.parseDouble(fYrangeMax.getText().trim());
          min = Double.parseDouble(fYrangeMin.getText().trim());
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(UParameters.getInstance().
                                        getMainComponet(),
                                        fRb.getString("NUMERICAL_VALUE_NEEDED"));
          return;
        }
        if (max <= min) {
          JOptionPane.showMessageDialog(UParameters.getInstance().
                                        getMainComponet(),
                                        fRb.getString("ILLEGAL_VALUES"));
          //          fYrangeMax.setText(String.valueOf((int)fInitMaxY));
          //          fYrangeMin.setText(String.valueOf((int)fInitMinY));
          fYrangeMax.setText(String.valueOf( (int) fUGraph.getFixedMaxY()));
          fYrangeMin.setText(String.valueOf( (int) fUGraph.getFixedMinY()));
          return;
        }
        fUGraph.setFixedMaxY(max);
        fUGraph.setFixedMinY(min);
        fUGraph.repaint();
      }
    });
    // fYrangeMaxÇÃê›íË
    fYrangeMax.setBounds(new Rectangle(82, 59, 93, 22));
    fYrangeMax.setHorizontalAlignment(JTextField.RIGHT);
    fYrangeMax.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fYrangeUpdate.requestFocus();
      }
    });
    // fYrangeMinÇÃê›íË
    fYrangeMin.setBounds(new Rectangle(82, 99, 93, 22));
    fYrangeMin.setHorizontalAlignment(JTextField.RIGHT);
    fYrangeMin.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fYrangeUpdate.requestFocus();
      }
    });
    // fYrangeMaxLabelÇÃê›íË
    fYrangeMaxLabel.setText(fRb.getString("MAXIMUM"));
    fYrangeMaxLabel.setForeground(Color.black);
    fYrangeMaxLabel.setHorizontalAlignment(SwingConstants.CENTER);
    fYrangeMaxLabel.setBounds(new Rectangle(5, 62, 72, 17));
    // fYrangeMinLabelÇÃê›íË
    fYrangeMinLabel.setText(fRb.getString("MINIMUM"));
    fYrangeMinLabel.setForeground(Color.black);
    fYrangeMinLabel.setHorizontalAlignment(SwingConstants.CENTER);
    fYrangeMinLabel.setBounds(new Rectangle(5, 102, 72, 17));
    // fYrangePanelÇÃê›íË
    setLayout(null);
    add(fAutoYrangeCheckBox, null);
    add(fYrangeUpdate, null);
    add(fYrangeMin, null);
    add(fYrangeMaxLabel, null);
    add(fYrangeMinLabel, null);
    add(fYrangeMax, null);
    setBorder(BorderFactory.createEtchedBorder());
  }

  public void setYAuto(boolean b) {
    fAutoYrangeCheckBox.setSelected(b);
  }
}