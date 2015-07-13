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
import java.text.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import serverCore.UMemberList;

/**
 * エージェントパラメータ設定用ダイアログ．
 */
public class UAgentDialog extends JDialog {
  JPanel fAgentSettingMainPanel = new JPanel();
  JButton fOKButton = new JButton();
  JButton fCancelButton = new JButton();
  ResourceBundle fRb = UParameters.fRb;
  JScrollPane fAgentSettingScrollPane = new JScrollPane();
  //protected JTable fAgentSettingTable;
  protected UTable fAgentSettingTable;
  protected DefaultTableModel fAgentSettingTableModel;
  protected UParameters fParam = UParameters.getInstance();
  protected Vector fHeader = new Vector();
  JButton fDefaultButton = new JButton();
  JButton fFileButton = new JButton();
  JFileChooser fFileChooser = new JFileChooser();
  ButtonGroup buttonGroup1 = new ButtonGroup();

  public UAgentDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
  }

  public UAgentDialog() {
    this(null, "Agent Setting", true);
    try {
      jbInit();
      pack();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    // Cancelボタンの設定
    fCancelButton.setBounds(new Rectangle(137, 355, 79, 27));
    fCancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fCancelButton.setText(fRb.getString("CANCEL"));
    fCancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
      }
    });
    // OKボタンの設定
    fOKButton.setBounds(new Rectangle(46, 355, 79, 27));
    fOKButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fOKButton.setText("OK");
    fOKButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setResults();
        setVisible(false);
        dispose();
      }
    });
    // Defaultボタンの設定
    fDefaultButton.setBounds(new Rectangle(228, 356, 79, 27));
    fDefaultButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fDefaultButton.setText(fRb.getString("DEFAULT"));
    fDefaultButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setDefault();
      }
    });
    // Fileボタンの設定
    fFileButton.setBounds(new Rectangle(46, 314, 67, 22));
    fFileButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fFileButton.setText(fRb.getString("FILE"));
    fFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fFileChooser = new JFileChooser();
        fFileChooser.setCurrentDirectory(new File("."));
        fFileChooser.setFileFilter(new UCsvFileFilter());
        if (fFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
          File file = fFileChooser.getSelectedFile();
          readMembersFromFile(file);
        }
      }
    });
    // エージェント設定確認用テーブルの設定
    fAgentSettingTable = new UTable();
    fAgentSettingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    initTable(fParam.getMemberLog());
    // エージェント設定確認用テーブルのスクロールペインの設定
    fAgentSettingScrollPane.setPreferredSize(new Dimension(600, 404));
    fAgentSettingScrollPane.setBounds(new Rectangle(22, 10, 662, 290));
    fAgentSettingScrollPane.getViewport().add(fAgentSettingTable, null);
    // メインパネルの設定
    fAgentSettingMainPanel.setLayout(null);
    fAgentSettingMainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    fAgentSettingMainPanel.setPreferredSize(new Dimension(700, 400));
    fAgentSettingMainPanel.add(fAgentSettingScrollPane, null);
    fAgentSettingMainPanel.add(fFileButton, null);
    fAgentSettingMainPanel.add(fCancelButton, null);
    fAgentSettingMainPanel.add(fDefaultButton, null);
    fAgentSettingMainPanel.add(fOKButton, null);
    getContentPane().add(fAgentSettingMainPanel, BorderLayout.SOUTH);
    setModal(true);
    setResizable(false);
  }

  /**
   * ファイルからメンバーファイルを読み込む．
   * @param file ファイル
   * @return 読み込み成功：true, 失敗:false
   */
  protected boolean readMembersFromFile(File file) {
    try {
      UMemberList members = new UMemberList();
      BufferedReader br = new BufferedReader(new FileReader(file));
      members.readFrom(br);
      br.close();
      initTable(members);
    } catch (FileNotFoundException fnfe) {
      JOptionPane.showMessageDialog(this,
                                    fRb.getString("ERROR_FILE_NOT_FOUND") + file,
                                    fRb.getString("ERROR_DIALOG_TITLE"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    } catch (ParseException pe) {
      JOptionPane.showMessageDialog(this,
                                    fRb.getString("ERROR_FILE_FORMAT") +
                                    pe.getErrorOffset(),
                                    fRb.getString("ERROR_DIALOG_TITLE"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    } catch (IOException ioex) {
      JOptionPane.showMessageDialog(this, fRb.getString("ERROR_IO"),
                                    fRb.getString("ERROR_DIALOG_TITLE"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  /**
   * デフォルトに設定する．
   */
  private void setDefault() {
    try {
      String path = null;
      if (fParam.getConnectionType() == UParameters.GUI_TYPE_NETWORK) {
        path = UParameters.MEMBERS_NETWORK;
      } else {
        path = UParameters.MEMBERS_SA;
      }
      URL membersURL = getClass().getResource(path);
      BufferedReader br = new BufferedReader(new InputStreamReader(membersURL.
          openStream()));
      fParam.getMemberLog().readFrom(br);
      br.close();
      initTable(fParam.getMemberLog());
    } catch (Exception ex) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      ex.printStackTrace(pw);
      JOptionPane.showMessageDialog(this,
                                    fRb.getString("ERROR_FATAL") + "\n" +
                                    sw.toString(),
                                    fRb.getString("ERROR_DIALOG_TITLE"),
                                    JOptionPane.ERROR_MESSAGE);
      System.exit(5);
    }
  }

  /**
   * 結果をfParam.fMemberLogに設定する．
   */
  protected void setResults() {
    fParam.getMemberLog().clear();
    Iterator iter = fAgentSettingTableModel.getDataVector().iterator();
    while (iter.hasNext()) {
      Vector line = (Vector) iter.next();
      if ( (String) line.get(1) == "") {
        continue;
      } else {
        String loginName = line.get(0).toString();
        String password = line.get(1).toString();
        String attribute = line.get(2).toString();
        String connection = line.get(3).toString();
        ArrayList access = UMemberList.stringToArrayList(line.get(4).toString());
        String realName = line.get(5).toString();
        ArrayList systemParameters = UMemberList.stringToArrayList(line.get(6).
            toString());
        /*
                 int seed = Integer.parseInt(line.get(7).toString());
                 long initialCash = Long.parseLong(line.get(8).toString());
                 long tradingUnit = Long.parseLong(line.get(9).toString());
                 long feePerUnit = Long.parseLong(line.get(10).toString());
                 long marginRate = Long.parseLong(line.get(11).toString());
                 long maxLoan = Long.parseLong(line.get(12).toString());
                 double interest = Double.parseDouble(line.get(13).toString());
         */
        //テーブル上での数値はフォーマットの問題で UFormatLong or UFormatDouble
        //が使われることになっている．
        int seed = (int) ( ( (UFormatLong) line.get(7)).longValue()); // 過去との互換を保つため
        long initialCash = ( (UFormatLong) line.get(8)).longValue();
        long tradingUnit = ( (UFormatLong) line.get(9)).longValue();
        long feePerUnit = ( (UFormatLong) line.get(10)).longValue();
        long marginRate = ( (UFormatLong) line.get(11)).longValue();
        long maxLoan = ( (UFormatLong) line.get(12)).longValue();
        double interest = ( (UFormatDouble) line.get(13)).doubleValue();

        fParam.getMemberLog().appendMember(loginName, password, attribute,
                                           connection, access, realName,
                                           systemParameters, seed,
                                           initialCash, tradingUnit,
                                           feePerUnit, marginRate,
                                           maxLoan, interest);
      }
    }
  }

  /**
   * テーブルヘッダーを設定する．
   */
  private void setupHeader() {
    fHeader.clear();
    for (int i = 0; i < UMemberList.HEADER_ARRAY.length; ++i) {
      fHeader.add(UMemberList.HEADER_ARRAY[i]);
    }
  }

  /**
   * テーブルを設定する．
   * @param members テーブルに表示するメンバー情報
   */
  protected void initTable(UMemberList members) {
    setupHeader();
    fAgentSettingTableModel = new DefaultTableModel(new Vector(), fHeader);
    Iterator iter = members.getMembers();
    while (iter.hasNext()) {
      HashMap hm = (HashMap) iter.next();
      Vector line = new Vector();
      line.add(hm.get(UMemberList.STRING_LOGIN_NAME));
      line.add(hm.get(UMemberList.STRING_PASSWORD));
      line.add(hm.get(UMemberList.STRING_ATTRIBUTE));
      line.add(hm.get(UMemberList.STRING_CONNECTION));
      line.add(UMemberList.arrayListToString( (ArrayList) hm.get(UMemberList.
          ARRAY_LIST_ACCESS)));
      line.add(hm.get(UMemberList.STRING_REAL_NAME));
      line.add(UMemberList.arrayListToString( (ArrayList) hm.get(UMemberList.
          ARRAY_LIST_SYSTEM_PARAMETERS)));
      line.add(new UFormatLong( (Integer) hm.get(UMemberList.INT_SEED)));
      line.add(new UFormatLong( (Long) hm.get(UMemberList.LONG_INITIAL_CASH)));
      line.add(new UFormatLong( (Long) hm.get(UMemberList.LONG_TRADING_UNIT)));
      line.add(new UFormatLong( (Long) hm.get(UMemberList.LONG_FEE_PER_UNIT)));
      line.add(new UFormatLong( (Long) hm.get(UMemberList.LONG_MARGIN_RATE)));
      line.add(new UFormatLong( (Long) hm.get(UMemberList.LONG_MAX_LOAN)));
      line.add(new UFormatDouble( (Double) hm.get(UMemberList.DOUBLE_INTEREST)));
      fAgentSettingTableModel.addRow(line); //Table に行追加
    }
    fAgentSettingTable.getTableHeader().setReorderingAllowed(false);
    fAgentSettingTable.setModel(fAgentSettingTableModel);
  }

}
