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
import javax.swing.*;

import cmdCore.*;

public class UUserChooser extends JPanel {

  JScrollPane fUserListScrollPane = new JScrollPane();
  JList fUserList = new JList();
  DefaultListModel fUserListModel = null;
  JLabel fSelectUserLabel = new JLabel();

  public UUserChooser() {
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    setLayout(null);
    fSelectUserLabel.setText("Select the user : ");
    add(fUserListScrollPane, null);
    fUserListScrollPane.getViewport().add(fUserList, null);
    this.add(fSelectUserLabel, null);
    fUserListScrollPane.setBounds(new Rectangle(180, 3, 233, 80));
    fSelectUserLabel.setBounds(new Rectangle(3, 3, 156, 29));
  }

  public int getTargetUserID() {
    return fUserList.getSelectedIndex() + 1;
  }

  public void setTargetUserID(int id) {
    fUserList.setSelectedIndex(id - 1);
  }

  public String getTargetUserName() {
    return (String) fUserList.getSelectedValue();
  }

  public void setupUserList(UProtocolCore cprotocol) {
    if (fUserListModel != null) {
      return;
    }
    UCExchangeProfileCore cExchangeProfile = (UCExchangeProfileCore) cprotocol.
        getCommand(UCExchangeProfileCore.CMD_NAME);
    cExchangeProfile.doIt();
    HashMap exProf = cExchangeProfile.getData();
    int noOfMembers = ( (Integer) exProf.get(UCExchangeProfileCore.
                                             INT_NO_OF_MEMBERS)).intValue();
    fUserListModel = new DefaultListModel();
    UCMemberProfileCore cMemberProfile = (UCMemberProfileCore) cprotocol.
        getCommand(UCMemberProfileCore.CMD_NAME);
    for (int i = 0; i < noOfMembers; ++i) {
      cMemberProfile.setArguments(i + 1);
      cMemberProfile.doIt();
      HashMap memProf = cMemberProfile.getData();
      fUserListModel.addElement( (String) memProf.get(UCMemberProfileCore.
          STRING_LOGIN_NAME));
    }
    fUserList.setModel(fUserListModel);
    setTargetUserID(1);
  }

  public void appendUser(String username) {
    if (fUserListModel == null) {
      fUserListModel = new DefaultListModel();
    }
    fUserListModel.addElement(username);
    fUserList.setModel(fUserListModel);
    fUserList.setSelectedIndex(0);
  }

  public void addListSelectionListener(javax.swing.event.ListSelectionListener
                                       listener) {
    fUserList.addListSelectionListener(listener);
  }

}