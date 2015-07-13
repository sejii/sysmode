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

import cmdCore.*;

/**
 * GUIのメインウィンドクラス
 */
public class UServerManagerMainWindow extends JFrame {

  JPanel contentPane;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  JMenuItem jMenuHelpTutorial = new JMenuItem();
  JMenuItem jMenuHelpDownload = new JMenuItem();
  JMenuItem jMenuHelpReport = new JMenuItem();
  JTabbedPane fMainTabbedPane = new JTabbedPane();
  UServerManagerTopPanel fServerManagerTopPanel = new UServerManagerTopPanel();
  UServerManagerMembersInfoTablePanel fServerManagerMembersInfoTablePanel = new
      UServerManagerMembersInfoTablePanel();
  UServerSystemOutPanel fServerSystemOutPanel = new UServerSystemOutPanel();
  UServerManagerBoardPanel fServerManagerBoardPanel = new
      UServerManagerBoardPanel();
  UServerManagerChartPanel fChartPanel = new UServerManagerChartPanel();
  UServerManagerOrderResultPanel fOrderResultPanel = new
      UServerManagerOrderResultPanel();
  UServerManagerPositionPanel fPositionPanel = new UServerManagerPositionPanel();
  UServerManagerProfitPanel fProfitPanel = new UServerManagerProfitPanel();
  UServerManagerExchangeInfoPanel fExchangeInfoPanel = new
      UServerManagerExchangeInfoPanel();
  UServerManagerMessagePanel fMessagePanel = new UServerManagerMessagePanel();
  IGUI fUGUI;
  ResourceBundle fRb = UParameters.fRb;
  protected UProtocolCore fCProtocol;
  protected javax.swing.Timer fUpdateTimer;
  int fLogoWidth = 100;
  int fLogoHeight = 100;
  JPanel fLogoPanel = new JPanel();
  URL fImgURL = getClass().getResource("/resources/images/u-mart-logo.jpg");
  ImageIcon fIcon = new ImageIcon(fImgURL);
  ImageIcon fScaledIcon = new ImageIcon(fIcon.getImage().getScaledInstance(
      fLogoWidth, fLogoHeight, Image.SCALE_DEFAULT));
  JLabel fLogo = new JLabel(fScaledIcon);

  public UServerManagerMainWindow() {
  }

  public UServerManagerMainWindow(IGUI fUgui, Collection panels) {
    fUGUI = fUgui;
    fRb = UParameters.fRb;
    setTitle("U-Mart: " + fUGUI.getName());
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    setCProtocol(fUGUI.getCProtocol());
    //	UPanel.setParameters(fUGUI.getParameters());
    setTimer(fUGUI.getUpdateTimer());
    Iterator iter = panels.iterator();
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //コンポーネントの初期化
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    contentPane.setLayout(null);
    this.setResizable(false);
    this.setSize(new Dimension(710, 700));
    jMenuFile.setText(fRb.getString("FILE"));
    jMenuFileExit.setText(fRb.getString("QUIT_WINDOW"));
    jMenuFileExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileExit_actionPerformed(e);
      }
    });
    jMenuHelp.setText(fRb.getString("HELP"));
    jMenuHelpAbout.setText(fRb.getString("ABOUT"));
    jMenuHelpTutorial.setText(fRb.getString("TUTORIAL"));
    jMenuHelpTutorial.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpTutorial_actionPerformed(e);
      }
    });
    jMenuHelpDownload.setText(fRb.getString("DOWNLOAD"));
    jMenuHelpDownload.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpDownload_actionPerformed(e);
      }
    });
    jMenuHelpReport.setText(fRb.getString("REPORT"));
    jMenuHelpReport.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpReport_actionPerformed(e);
      }
    });
    jMenuHelpAbout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
      }
    });

    fMainTabbedPane.setBounds(new Rectangle(25, 224, 753, 320));
    fServerManagerTopPanel.setBorder(BorderFactory.createEtchedBorder());
    fServerManagerTopPanel.setBounds(new Rectangle(16, 7, 531, 81));
    fServerManagerTopPanel.setLayout(null);
    contentPane.setPreferredSize(new Dimension(1000, 720));
    fMainTabbedPane.setBounds(new Rectangle(6, 96, 691, 549));
    fLogoPanel.setBounds(new Rectangle(579, 7, fLogoWidth, fLogoHeight));
    jMenuFile.add(jMenuFileExit);
    jMenuHelp.add(jMenuHelpTutorial);
    jMenuHelp.addSeparator();
    jMenuHelp.add(jMenuHelpDownload);
    jMenuHelp.add(jMenuHelpReport);
    jMenuHelp.addSeparator();
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuHelp);
    fMainTabbedPane.add(fServerManagerMembersInfoTablePanel, "Info");
    fMainTabbedPane.add(fChartPanel, fChartPanel.getName());
    fMainTabbedPane.add(fServerManagerBoardPanel,
                        fServerManagerBoardPanel.getName());
    fMainTabbedPane.add(fOrderResultPanel, fOrderResultPanel.getName());
    fMainTabbedPane.add(fProfitPanel, fProfitPanel.getName());
    fMainTabbedPane.add(fPositionPanel, fPositionPanel.getName());
    fMainTabbedPane.add(fExchangeInfoPanel, fExchangeInfoPanel.getName());
    // fMainTabbedPane.add(fServerSystemOutPanel, fServerSystemOutPanel.getName());
    fMainTabbedPane.add(fMessagePanel, fMessagePanel.getName());
    contentPane.add(fLogoPanel, null);
    fLogoPanel.add(fLogo, null);
    contentPane.add(fServerManagerTopPanel, null);
    contentPane.add(fMainTabbedPane, null);
    this.setJMenuBar(jMenuBar1);
  } // end of jInit

  public void setCProtocol(UProtocolCore cp) {
    fCProtocol = cp;
    UPanel.setCProtocol(cp);
    UGUIUpdateManager.setCProtocol(cp);
  }

  public void setTimer(javax.swing.Timer timer) {
    fUpdateTimer = timer;
    fServerManagerTopPanel.setTimer(timer);
  }

  public void gUpdate() {
    UGUIUpdateManager.update();
    //SidePanel の gUpdate は板tableのみを更新。
    //table だけは頻繁な更新が必要なので。
    /*
       fUServerManagerTopPanel.gUpdate();
     */
  }

  //[ファイル|終了]
  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    confirmExit();
  }

  //[ヘルプ|バージョン情報]
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    UGUIMainWindow_AboutBox dlg = new UGUIMainWindow_AboutBox(this,
        UGUIMainWindow_AboutBox.MARKET_SERVER);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation(
        (frmSize.width - dlgSize.width) / 2 + loc.x,
        (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.show();
  }

  public void jMenuHelpTutorial_actionPerformed(ActionEvent e) {
    String urlString = null;
    if (System.getProperties().getProperty("user.language").equals("ja")) {
      urlString = getClass().getResource(UParameters.JAPANESE_TUTORIAL_FILE).
          toString();
    } else {
      urlString = getClass().getResource(UParameters.ENGLISH_TUTORIAL_FILE).
          toString();
    }
    UBrowserWindow browser = new UBrowserWindow(urlString);
    browser.setVisible(true);
  }

  public void jMenuHelpDownload_actionPerformed(ActionEvent e) {
    String osType = System.getProperty("os.name");
    if (osType.substring(0, 3).equalsIgnoreCase("Win")) {
      try {
        Runtime.getRuntime().exec("c:/Program Files/Internet Explorer/iexplore " + UParameters.DOWNLOAD_SITE);
      } catch (IOException e1) {
        JOptionPane.showMessageDialog(null,
                                      "Cannot invoke c:/Program Files/Internet Explorer/iexplore.",
                                      fRb.getString("ERROR_DIALOG_TITLE"),
                                      JOptionPane.ERROR_MESSAGE);
      }
    } else if (osType.substring(0, 3).equals("Lin")) {
      try {
        Runtime.getRuntime().exec("/usr/bin/mozilla " + UParameters.DOWNLOAD_SITE);
      } catch (IOException e1) {
        JOptionPane.showMessageDialog(null,
                                      "Cannot invoke /usr/bin/mozilla.",
                                      fRb.getString("ERROR_DIALOG_TITLE"),
                                      JOptionPane.ERROR_MESSAGE);
      }
    } else {
      try {
        Runtime.getRuntime().exec("open " + UParameters.DOWNLOAD_SITE);
      } catch (IOException e1) {
        JOptionPane.showMessageDialog(null,
                                      "Cannot invoke /Applications/Safari.app.",
                                      fRb.getString("ERROR_DIALOG_TITLE"),
                                      JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void jMenuHelpReport_actionPerformed(ActionEvent e) {
    // UBugReporter smtpServer = new UBugReporter();
    UBugReportWindow smtpServer = new UBugReportWindow();
    smtpServer.setVisible(true);
  }

  //ウィンドウが閉じられたときに終了するようにオーバーライド
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }

  public void mainImpl() {
    //validate() はサイズを調整する
    //pack() は有効なサイズ情報をレイアウトなどから取得する
    validate();
    //ウィンドウを中央に配置
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension mySize = getSize();
    if (mySize.height > screenSize.height) {
      mySize.height = screenSize.height;
    }
    if (mySize.width > screenSize.width) {
      mySize.width = screenSize.width;
    }
    setLocation(
        (screenSize.width - mySize.width) / 2,
        (screenSize.height - mySize.height) / 2);
    setVisible(true);
  }

  protected void confirmExit() {
    int ans =
        JOptionPane.showConfirmDialog(
        null, fRb.getString("QUIT_CONFIRM"),
        fRb.getString("QUIT_CONFIRM_WINDOW"), JOptionPane.OK_CANCEL_OPTION);
    if (ans == JOptionPane.OK_OPTION) {
      System.exit(0);
    }
    if (!this.isVisible()) {
      this.setVisible(true);
    }
  }

  /*
     void fMainTabbedPane1_stateChanged(ChangeEvent e) {

     }
   */
}
