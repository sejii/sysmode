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
import javax.swing.border.*;

import serverCore.UTimeSeriesDefinitions;

public class UExperimentalSettingPanel extends JPanel {

  JComboBox fDataComboBox = new JComboBox();
  JLabel fDataPanel = new JLabel();
  JPanel fDatePanel = new JPanel();
  TitledBorder titledBorder1;
  JLabel fDaysLabel = new JLabel();
  JLabel fStartLabel = new JLabel();
  JTextField fStartTextField = new JTextField();
  JTextField fDaysTextField = new JTextField();
  JTextField fBoardPerDayTextField = new JTextField();
  JLabel fBoardPerDayLabel = new JLabel();
  protected UParameters fParam = UParameters.getInstance();
  ResourceBundle fRb = UParameters.fRb;
  UTimeSeriesDefinitions fTimeSeriesDefinitions;
  JButton fFileButton = new JButton();
  JFileChooser fFileChooser = new JFileChooser();
  JLabel fBrandNameLabel = new JLabel();
  JLabel fFileNameLabel = new JLabel();

  public UExperimentalSettingPanel() {
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {

    // 「決済ステップ」パネル
    titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.
        white, new Color(148, 145, 140)), fRb.getString("EXPERIMENTAL_SETTING"));
    titledBorder1.setTitleColor(Color.black);
    fDatePanel.setBorder(titledBorder1);
    fDatePanel.setToolTipText("");
    fDatePanel.setBounds(new Rectangle(4, 5, 268, 181));
    fDatePanel.setLayout(null);
    fDatePanel.add(fStartLabel, null);
    fDatePanel.add(fStartTextField, null);
    fDatePanel.add(fBoardPerDayLabel, null);
    fDatePanel.add(fBoardPerDayTextField, null);
    fDatePanel.add(fDaysLabel, null);
    fDatePanel.add(fDaysTextField, null);
    fDatePanel.add(fFileNameLabel, null);
    fDatePanel.add(fBrandNameLabel, null);
    fDatePanel.add(fDataPanel, null);
    fDatePanel.add(fDataComboBox, null);
    fDatePanel.add(fFileButton, null);
    fDatePanel.add(fFileNameLabel, null);
    // 「現物価格データ」と表示しているテキストボックス
    fDataPanel.setText(fRb.getString("SETTING"));
    fDataPanel.setForeground(Color.black);
    fDataPanel.setBounds(new Rectangle(16, 26, 71, 17));
    // 現物価格データを選択するためのコンボ・ボックス
    fDataComboBox.setRequestFocusEnabled(false);
    fDataComboBox.setBounds(new Rectangle(93, 24, 92, 21));
    fDataComboBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String nickname = (String) fDataComboBox.getSelectedItem();
        setupTimeSeriesInfo(nickname);
      }
    });
    fDataComboBox.setBackground(Color.white);
    // 現物価格データを読み込むためのファイル・ボタン
    fFileButton.setBounds(new Rectangle(192, 23, 67, 22));
    fFileButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fFileButton.setText(fRb.getString("FILE"));
    fFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fFileChooser = new JFileChooser();
        fFileChooser.setCurrentDirectory(new File("."));
        fFileChooser.setFileFilter(new UCsvFileFilter());
        int state = fFileChooser.showOpenDialog(null);
        if (state == JFileChooser.CANCEL_OPTION) {
          return;
        }
        File file = fFileChooser.getSelectedFile();
        UTimeSeriesDefinitions orgDef = fTimeSeriesDefinitions;
        try {
          fTimeSeriesDefinitions = new UTimeSeriesDefinitions();
          readTimeSeriesDefinitions(file);
        } catch (ParseException pe) {
          JOptionPane.showMessageDialog(UExperimentalSettingPanel.this,
                                        fRb.getString("ERROR_FILE_FORMAT") +
                                        pe.getErrorOffset(),
                                        fRb.getString("ERROR_DIALOG_TITLE"),
                                        JOptionPane.ERROR_MESSAGE);
          fTimeSeriesDefinitions = orgDef;
        } catch (IOException ioex) {
          JOptionPane.showMessageDialog(null, fRb.getString("ERROR_IO"),
                                        fRb.getString("ERROR_DIALOG_TITLE"),
                                        JOptionPane.ERROR_MESSAGE);
          fTimeSeriesDefinitions = orgDef;
        }
        setupDataComboBox();
      }
    });
    // 銘柄名
    fBrandNameLabel.setBounds(new Rectangle(16, 51, 248, 17));
    fBrandNameLabel.setForeground(Color.black);
    fBrandNameLabel.setText(fRb.getString("BRAND_NAME"));
    // ファイル名
    fFileNameLabel.setText(fRb.getString("BRAND_NAME"));
    fFileNameLabel.setForeground(Color.black);
    fFileNameLabel.setBounds(new Rectangle(17, 36, 248, 17));
    fFileNameLabel.setText(fRb.getString("FILENAME"));
    fFileNameLabel.setForeground(Color.black);
    fFileNameLabel.setBounds(new Rectangle(16, 76, 248, 17));
    // 取引日数ラベル
    fDaysLabel.setText(fRb.getString("TRADING_PERIOD"));
    fDaysLabel.setForeground(Color.black);
    fDaysLabel.setBounds(new Rectangle(16, 101, 146, 17));
    // 取引日数入力フィールド
    fDaysTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fDaysTextField.setBounds(new Rectangle(167, 98, 58, 21));
    // 1日の板寄せ回数ラベル
    fBoardPerDayLabel.setText(fRb.getString("SESSIONS_PER_DAY"));
    fBoardPerDayLabel.setForeground(Color.black);
    fBoardPerDayLabel.setBounds(new Rectangle(16, 126, 146, 17));
    // １日の板寄せ回数入力フィールド
    fBoardPerDayTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fBoardPerDayTextField.setBounds(new Rectangle(167, 124, 58, 21));
    // 開始ステップラベル
    fStartLabel.setText(fRb.getString("START_POINT"));
    fStartLabel.setForeground(Color.black);
    fStartLabel.setBounds(new Rectangle(16, 151, 146, 17));
    // 開始ステップ入力フィールド
    fStartTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fStartTextField.setBounds(new Rectangle(167, 151, 58, 21));

    setLayout(null);
    this.add(fDatePanel, null);
  }

  public void setDefault() {
    fTimeSeriesDefinitions = fParam.getTimeSeriesDefinitionLog();
    try {
      readTimeSeriesDefinitions(UParameters.TIME_SERIES_DEFINITIONS);
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
    setupDataComboBox();
  }

  /**
   * 選択された実験名を返す．
   * @return 選択された実験名
   */
  public String getSelectedExperimentName() {
    return (String) fDataComboBox.getSelectedItem();
  }

  public UTimeSeriesDefinitions getTimeSeriesDefinition() {
    return fTimeSeriesDefinitions;
  }

  public int getDays() {
    return Integer.parseInt(fDaysTextField.getText());
  }

  public int getStartPoint() {
    return Integer.parseInt(fStartTextField.getText());
  }

  public int getBoardPerDay() {
    return Integer.parseInt(fBoardPerDayTextField.getText());
  }

  private void setupDataComboBox() {
    int noOfItems = fDataComboBox.getItemCount();
    Iterator itr = fTimeSeriesDefinitions.getTimeSeriesDefinitions();
    while (itr.hasNext()) {
      HashMap info = (HashMap) itr.next();
      String nickName = info.get(UTimeSeriesDefinitions.STRING_NICKNAME).
          toString();
      fDataComboBox.addItem(nickName);
    }
    for (int i = 0; i < noOfItems; ++i) {
      fDataComboBox.removeItemAt(0);
    }
    String nickname = (String) fDataComboBox.getSelectedItem();
    setupTimeSeriesInfo(nickname);
  }

  private void readTimeSeriesDefinitions(String resourceName) throws
      IOException, ParseException {
    URL timeSeriesDefinitionsURL = getClass().getResource(resourceName);
    BufferedReader br = new BufferedReader(new InputStreamReader(
        timeSeriesDefinitionsURL.openStream()));
    fTimeSeriesDefinitions.readFrom(br);
    br.close();
  }

  private void readTimeSeriesDefinitions(File file) throws IOException,
      ParseException {
    BufferedReader br = new BufferedReader(new FileReader(file));
    fTimeSeriesDefinitions.readFrom(br);
    br.close();
  }

  private void setupTimeSeriesInfo(String nickname) {
    HashMap info = fTimeSeriesDefinitions.getDefinition(nickname);
    fBrandNameLabel.setText(fRb.getString("BRAND_NAME") + " : "
                            +
                            info.get(UTimeSeriesDefinitions.STRING_BRAND_NAME).
                            toString());
    fFileNameLabel.setText(fRb.getString("FILENAME") + " : "
                           +
                           info.get(UTimeSeriesDefinitions.STRING_FILENAME).toString());
    fStartTextField.setText(info.get(UTimeSeriesDefinitions.INT_START_STEP).
                            toString());
    fDaysTextField.setText(info.get(UTimeSeriesDefinitions.INT_MAX_DATE).
                           toString());
    fBoardPerDayTextField.setText(info.get(UTimeSeriesDefinitions.
                                           INT_NO_OF_SESSIONS_PER_DAY).toString());
  }

}