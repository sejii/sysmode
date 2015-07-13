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
import javax.swing.table.*;

import cmdCore.*;

/**
 * エージェントパラメータ設定用ダイアログ．
 */
public class UFinalResultDialog extends JDialog {
  JPanel fFinalResultMainpanel = new JPanel();
  JButton fOKButton = new JButton();
  ResourceBundle fRb = UParameters.fRb;
  JScrollPane fFinalResultScrollPane = new JScrollPane();
  protected JTable fFinalResultTable;
  protected DefaultTableModel fFinalResultTableModel;
  protected UParameters fParam = UParameters.getInstance();
  Object[] fMemberTableHeaderData = {
      "memberID", "Property", "status"};
  protected Vector fMemberTableHeader = new Vector();
  protected Vector fMemberInformation = new Vector();
  protected UTable fMemberTable;
  protected UProtocolCore fCProtocol;
  public UFinalResultDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
  }

  public UFinalResultDialog(UProtocolCore cp) {
    this(null, "Final Result", true);
    fCProtocol = cp;
    try {
      jbInit();
      pack();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    setLocationRelativeTo( (Frame)null);
    setVisible(true);
  }

  public UFinalResultDialog() {
    this(null, "Final Result", true);
    try {
      jbInit();
      pack();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    setLocationRelativeTo( (Frame)null);
    setVisible(true);
  }

  /*
      public UFinalResultDialog(UParameters pm) {
          this(null, "Agent Setting", true);
          fParam = pm;
          try {
              jbInit();
              pack();
          }
          catch(Exception ex) {
              ex.printStackTrace();
          }
      }
   */
  void jbInit() throws Exception {
    this.setModal(false);
    this.setResizable(false);
    for (int i = 0; i < fMemberTableHeaderData.length; i++) {
      fMemberTableHeader.add(fMemberTableHeaderData[i]);
    }
    fFinalResultScrollPane.getViewport().setBackground(Color.white);
    initMemberInformation();
    fMemberTable = new UTable(fMemberInformation, fMemberTableHeader);
    fFinalResultScrollPane.setBounds(new Rectangle(6, 11, 661, 504));
    fFinalResultScrollPane.setBorder(BorderFactory.createRaisedBevelBorder());
    fFinalResultScrollPane.getViewport().setBackground(Color.white);
    this.getContentPane().add(fFinalResultScrollPane, null);
    fFinalResultScrollPane.getViewport().add(fMemberTable, null);
    fFinalResultMainpanel.setLayout(null);
    fFinalResultMainpanel.setBorder(BorderFactory.createRaisedBevelBorder());
    fFinalResultMainpanel.setMaximumSize(new Dimension(450, 550));
    fFinalResultMainpanel.setMinimumSize(new Dimension(450, 550));
    fFinalResultMainpanel.setPreferredSize(new Dimension(450, 550));
    fOKButton.setBounds(new Rectangle(186, 499, 79, 27));
    fOKButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fOKButton.setText("OK");
    fOKButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
        //JOptionPane.showMessageDialog(UFinalResultDialog.this, "Finished!");
      }
    });
    fFinalResultScrollPane.setBounds(new Rectangle(25, 11, 407, 476));
    fFinalResultMainpanel.add(fOKButton, null);
    fFinalResultMainpanel.add(fFinalResultScrollPane, null);
    this.getContentPane().add(fFinalResultMainpanel, BorderLayout.SOUTH);
  }

  public void initMemberInformation() {
    UCExchangeProfileCore cExchangeProfile = (UCExchangeProfileCore)fCProtocol.getCommand(UCExchangeProfileCore.CMD_NAME);
    UCommandStatus status = cExchangeProfile.doIt();
    if (status.getStatus() == false) {
      System.err.println("Error in UFileResultDialog.initTable()");
      System.err.println(status.getErrorMessage());
    }
    HashMap hmep = cExchangeProfile.getData();
    int id, maxMember;
    maxMember = ((Integer)hmep.get(UCExchangeProfileCore.INT_NO_OF_MEMBERS)).intValue();
    fMemberInformation.clear();
    for (id = 1; id <= maxMember; id++) {
      UCMemberProfileCore cMemberProfile = (UCMemberProfileCore)fCProtocol.getCommand(UCMemberProfileCore.CMD_NAME);
      cMemberProfile.setArguments(id);
      cMemberProfile.doIt();
      HashMap hmmp = cMemberProfile.getData();
      HashMap hmToday = (HashMap) hmmp.get(UCMemberProfileCore.
                                           HASHMAP_TODAY_BALANCE);
      HashMap hmYesterday = (HashMap) hmmp.get(UCMemberProfileCore.
                                               HASHMAP_YESTERDAY_BALANCE);
      HashMap hmPosition = (HashMap) hmmp.get(UCMemberProfileCore.
                                              HASHMAP_POSITION);
      Vector tmpData = new Vector();
      tmpData.add( (String) hmmp.get(UCMemberProfileCore.STRING_LOGIN_NAME));
      // tmpData.add( (Long) hmPosition.get(UCMemberProfileCore.LONG_TODAY_SELL_POSITIONS));
      // tmpData.add( (Long) hmPosition.get(UCMemberProfileCore.LONG_TODAY_BUY_POSITIONS));
      // tmpData.add( (Long) hmPosition.get(UCMemberProfileCore.LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY));
      // tmpData.add( (Long) hmPosition.get(UCMemberProfileCore.LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY));
      tmpData.add(new UFormatLong((Long)hmToday.get(UCMemberProfileCore.LONG_CASH)));
      tmpData.add( (Integer) hmmp.get(UCMemberProfileCore.INT_STATUS));
      fMemberInformation.add(tmpData);
    }
  }

  public static void main(String[] argv) {
    new UFinalResultDialog();
  }
}
