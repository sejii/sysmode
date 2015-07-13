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

import serverCore.*;
import strategyCore.UBaseAgent;

/**
 * パラメータ入力用ダイアログ.
 */
public class UParamDialog extends JDialog {

  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel fBasePanel = new JPanel();
  TitledBorder titledBorder1;
  JButton fAgentButton = new JButton();
  JButton fOKButton = new JButton();
  JButton fQuitButton = new JButton();
  TitledBorder titledBorder2;
  protected UParameters fParam = UParameters.getInstance();
  JButton fTabSettingButton = new JButton();
  ResourceBundle fRb = UParameters.fRb;
  JCheckBox fLogCheckBox = new JCheckBox();
  JButton fInstitutionSettingButton = new JButton();
  JButton fDefaultButton = new JButton();
  UInstitutionSetting fInstitution;
  boolean fStatus = false;
  UExperimentalSettingPanel fExperimentalSettingPanel = new
      UExperimentalSettingPanel();
  URandomSeedPanel fRandomSeedPanel = new URandomSeedPanel();
  JCheckBox fAutoRunCheckBox = new JCheckBox();

  public UParamDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
  }

  public UParamDialog() {
    this(null, UParameters.fRb.getString("SETTING"), true);
    setupDefaultInstitution();
    try {
      jbInit();
      pack();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  //コンポーネントの初期化
  private void jbInit() throws Exception {

    contentPane = (JPanel)this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setModal(true);
    this.setResizable(false);
    this.setTitle(fRb.getString("SETTING"));

    // 実験設定パネルの設定
    fExperimentalSettingPanel.setBounds(new Rectangle(32, 2, 278, 187));

    // 乱数パネル
    fRandomSeedPanel.setBounds(new Rectangle(54, 189, 222, 110));

    // エージェント設定用ボタン
    fAgentButton.setBounds(new Rectangle(33, 300, 124, 27));
    fAgentButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fAgentButton.setText(fRb.getString("AGENT_SETTING"));
    fAgentButton.setForeground(Color.black);
    fAgentButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        UAgentDialog jd = new UAgentDialog();
        jd.setLocationRelativeTo(UParamDialog.this);
        jd.pack();
        jd.setVisible(true);
      }
    });

    // 制度設定用ボタン
    fInstitutionSettingButton.setBounds(new Rectangle(171, 300, 124, 27));
    fInstitutionSettingButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fInstitutionSettingButton.setForeground(Color.black);
    fInstitutionSettingButton.setText(fRb.getString("PLAYERS_SETTING"));
    fInstitutionSettingButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        UNewInstitutionSettingDialog jd = new UNewInstitutionSettingDialog();
        jd.setLocationRelativeTo(UParamDialog.this);
        jd.pack();
        jd.setVisible(true);
      }
    });

    // 表示タブ設定用ボタン
    fTabSettingButton.setText(fRb.getString("TAB_SETTING"));
    fTabSettingButton.setForeground(Color.black);
    fTabSettingButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fTabSettingButton.setBounds(new Rectangle(172, 336, 124, 27));
    fTabSettingButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        UTabSettingDialog jd = new UTabSettingDialog( (Frame)null, true);
        jd.setLocationRelativeTo(UParamDialog.this);
        jd.pack();
        jd.setVisible(true);
      }
    });

    // ログ作成チェックボックス
    fLogCheckBox.setText(fRb.getString("CREATE_LOGS"));
    fLogCheckBox.setBounds(new Rectangle(40, 331, 113, 25));
    // 自動起動チェックボックス
    fAutoRunCheckBox.setSelected(fParam.isAutoRun());
    fAutoRunCheckBox.setText(fRb.getString("AUTO_RUN"));
    fAutoRunCheckBox.setBounds(new Rectangle(40, 356, 83, 23));

    // OKボタン
    fOKButton.setBounds(new Rectangle(29, 385, 79, 27));
    fOKButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fOKButton.setText("OK");
    fOKButton.setForeground(Color.black);
    fOKButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!setResults()) {
          return;
        }
        fStatus = true;
        setVisible(false);
        dispose();
      }
    });

    //　中止ボタン
    fQuitButton.setBounds(new Rectangle(130, 385, 79, 27));
    fQuitButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fQuitButton.setText(fRb.getString("QUIT"));
    fQuitButton.setForeground(Color.black);
    fQuitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fStatus = false;
        setVisible(false);
        dispose();
      }
    });

    // 初期値ボタン
    fDefaultButton.setForeground(Color.black);
    fDefaultButton.setText(fRb.getString("DEFAULT"));
    fDefaultButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fDefaultButton.setBounds(new Rectangle(231, 386, 79, 27));
    fDefaultButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setDefault();
      }
    });

    // ベースパネル
    fBasePanel.setBorder(BorderFactory.createEtchedBorder());
    fBasePanel.setLayout(null);
    fBasePanel.add(fQuitButton, null);
    fBasePanel.add(fOKButton, null);
    fBasePanel.add(fDefaultButton, null);
    fBasePanel.add(fRandomSeedPanel, null);
    fBasePanel.add(fExperimentalSettingPanel, null);
    fBasePanel.add(fInstitutionSettingButton, null);
    fBasePanel.add(fAgentButton, null);
    fBasePanel.add(fTabSettingButton, null);
    fBasePanel.add(fLogCheckBox, null);
    fBasePanel.add(fAutoRunCheckBox, null);

    contentPane.setPreferredSize(new Dimension(340, 430));
    contentPane.add(fBasePanel, BorderLayout.CENTER);
    setDefault();
  }

  /**
   * ServerManagerParamDialog内で選択された内容をUParametersに反映させる．
   * @return エラーが起こった場合：true, 起こらなかった場合:false
   */
  private boolean setResults() {
    String experimentName = fExperimentalSettingPanel.getSelectedExperimentName();
    UTimeSeriesDefinitions timeSeriesDefinitions = fExperimentalSettingPanel.
        getTimeSeriesDefinition();
    HashMap timeSeriesDef = timeSeriesDefinitions.getDefinition(experimentName);
    fParam.setBrand( (String) timeSeriesDef.get(UTimeSeriesDefinitions.
                                                STRING_BRAND_NAME));
    fParam.setPriceFile( (String) timeSeriesDef.get(UTimeSeriesDefinitions.
        STRING_FILENAME));
    if (!readPriceInfoDB(fParam.getPriceFile(), fParam.getPriceInfoDB())) {
      return false;
    }
    fParam.setDays(fExperimentalSettingPanel.getDays());
    fParam.setStartPoint(fExperimentalSettingPanel.getStartPoint());
    fParam.setBoardPerDay(fExperimentalSettingPanel.getBoardPerDay());
    fParam.setLogCreate(fLogCheckBox.isSelected());
    fParam.setAutoRun(fAutoRunCheckBox.isSelected());
    fParam.setSeed(fRandomSeedPanel.getSeed());
    fParam.setAutoRun(fAutoRunCheckBox.isSelected());
    if (!setupMachineAgentArray(fParam.getMemberLog(),
                                fParam.getMachineAgentArray())) {
      return false;
    }
    ArrayList access = fParam.getTabs();
    long initialCash = fParam.getInitCash();
    long tradingUnit = fParam.getTradingUnit();
    long feePerUnit = fParam.getFeePerUnit();
    long marginRate = fParam.getMarginRate();
    long maxLoan = fParam.getMaxLoan();
    double interest = fParam.getInterest();
    fParam.getMemberLog().appendMemberAtHead("You", "passwd1", "Human", "Local",
                                             access, "You", new ArrayList(),
                                             0, initialCash, tradingUnit,
                                             feePerUnit,
                                             marginRate, maxLoan, interest);
    return true;
  }

  /**
   * 与えられたmembersに登録されているマシンエージェント情報からマシンエージェントを生成し，machineAgentArrayに登録する．
   * @param members 会員情報
   * @param machineAgentArray マシンエージェントの登録先
   * @return 全てのマシンエージェントの生成に成功：true, 失敗：false
   */
  private boolean setupMachineAgentArray(UMemberList members,
                                         ArrayList machineAgentArray) {
    machineAgentArray.clear();
    Iterator iter = members.getMembers();
    while (iter.hasNext()) {
      HashMap memberInfo = (HashMap) iter.next();
      String loginName = (String) memberInfo.get(UMemberList.STRING_LOGIN_NAME);
      String passwd = (String) memberInfo.get(UMemberList.STRING_PASSWORD);
      String attribute = (String) memberInfo.get(UMemberList.STRING_ATTRIBUTE);
      String connection = (String) memberInfo.get(UMemberList.STRING_CONNECTION);
      String accessString = UMemberList.arrayListToString( (ArrayList)
          memberInfo.get(UMemberList.ARRAY_LIST_ACCESS));
      String realName = (String) memberInfo.get(UMemberList.STRING_REAL_NAME);
      String paramString = UMemberList.arrayListToString( (ArrayList) memberInfo.
          get(UMemberList.ARRAY_LIST_SYSTEM_PARAMETERS));
      int seed = ( (Integer) memberInfo.get(UMemberList.INT_SEED)).intValue();
      if (attribute.equals("Machine") && connection.equals("Local")) {
        try {
          UByteArrayOutputStream os = new UByteArrayOutputStream();
          os.setName(loginName);
          UBaseAgent strategy = UAgentFactory.makeAgent(loginName, passwd,
              realName, paramString, seed, os);
          machineAgentArray.add(strategy);
        } catch (IllegalArgumentException iae) {
          JOptionPane.showMessageDialog(null,
                                        fRb.getString("ERROR_CANNOT_INITIALIZE_AGENT") +
                                        loginName,
                                        fRb.getString("ERROR_DIALOG_TITLE"),
                                        JOptionPane.ERROR_MESSAGE);
          return false;
        }
      }
    }
    return true;
  }

  /**
   * 価格情報ファイルを読み込む
   * @param filename 価格情報ファイル名
   * @throws java.lang.Exception
   */
  private boolean readPriceInfoDB(String filename, UPriceInfoDB priceInfoDB) {
    try {
      StringTokenizer st = new StringTokenizer(filename, ":");
      String type = st.nextToken();
      String name = st.nextToken();
      if (type.equals("resource")) {
        URL dataURL = getClass().getResource("/resources/csv/" + name);
        BufferedReader br = new BufferedReader(new InputStreamReader(dataURL.
            openStream()));
        priceInfoDB.readFrom(br);
        br.close();
      } else {
        BufferedReader br = new BufferedReader(new FileReader(name));
        priceInfoDB.readFrom(br);
        br.close();
      }
    } catch (NoSuchElementException nse) {
      JOptionPane.showMessageDialog(null,
                                    fRb.getString(
          "ERROR_INVALID_PRICE_FILE_NAME") + filename,
                                    fRb.getString("ERROR_DIALOG_TITLE"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    } catch (FileNotFoundException fnfe) {
      JOptionPane.showMessageDialog(null,
                                    fRb.getString("ERROR_FILE_NOT_FOUND") +
                                    fnfe.getMessage(),
                                    fRb.getString("ERROR_DIALOG_TITLE"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    } catch (IOException ioe) {
      JOptionPane.showMessageDialog(null, fRb.getString("ERROR_IO"),
                                    fRb.getString("ERROR_DIALOG_TITLE"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    } catch (ParseException pe) {
      JOptionPane.showMessageDialog(null,
                                    fRb.getString("ERROR_FILE_FORMAT") +
                                    pe.getErrorOffset(),
                                    fRb.getString("ERROR_DIALOG_TITLE"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  public boolean getStatus() {
    return fStatus;
  }

  public void setDefault() {
    fExperimentalSettingPanel.setDefault();
    fRandomSeedPanel.setDefault();
    try {
      URL membersURL = getClass().getResource(UParameters.MEMBERS_SA);
      BufferedReader br = new BufferedReader(new InputStreamReader(membersURL.
          openStream()));
      fParam.getMemberLog().readFrom(br);
      br.close();
    } catch (Exception ex) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      ex.printStackTrace(pw);
      JOptionPane.showMessageDialog(null,
                                    fRb.getString("ERROR_FATAL") + "\n" +
                                    sw.toString(),
                                    fRb.getString("ERROR_DIALOG_TITLE"),
                                    JOptionPane.ERROR_MESSAGE);
      System.exit(5);
    }
    fLogCheckBox.setSelected(UParameters.DEFAULT_IS_LOG_CREATE);
  }

  private void setupDefaultInstitution() {
    fInstitution = fParam.getInstitutionLog();
    try {
      URL institutionURL = getClass().getResource(UParameters.INSTITUTION);
      BufferedReader br = new BufferedReader(new InputStreamReader(
          institutionURL.openStream()));
      fInstitution.readFrom(br);
      br.close();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(5);
    }
  }

  //ウィンドウが閉じられたときに終了するようにオーバーライド
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      setVisible(false);
      dispose();
    }
  }

  public static void main(String[] args) {
    new UParamDialog();
  }
}
