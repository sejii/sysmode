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
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import cmdCore.*;

/**
 * GUIのメインウィンドクラス．
 */
public class UGUIMainWindow extends JFrame {
  JPanel contentPane;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileSave = new JMenuItem();
  JMenuItem jMenuFileQuit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  JMenuItem jMenuHelpTutorial = new JMenuItem();
  JMenuItem jMenuHelpDownload = new JMenuItem();
  JMenuItem jMenuHelpReport = new JMenuItem();
  JTabbedPane fMainTabbedPane = new JTabbedPane();
  UOrderButtonPanel fUOrderButtonPanel = new UOrderButtonPanel(this);
  UTopPanel fUTopPanel = new UTopPanel();
  IGUI fUGUI;
  ResourceBundle fRb = UParameters.fRb;
  USubPanel fUSubPanel = new USubPanel();
  UInfoPanel fInfoPanel = new UInfoPanel();
  protected UProtocolCore fCProtocol;
  protected javax.swing.Timer fUpdateTimer;
  protected ArrayList fUPanelArrayList = new ArrayList();

  //フレームの構築
  public UGUIMainWindow() {
  }

  public UGUIMainWindow(IGUI fUgui, Collection panels) {
    fUGUI = fUgui;
    fRb = UParameters.fRb;
    setTitle(fUGUI.getName());
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    setCProtocol(fUGUI.getCProtocol()); // fCProtocol がセットされる．
    setTimer(fUGUI.getUpdateTimer());
    Iterator iter = panels.iterator();
    while (iter.hasNext()) {
      String s = (String) iter.next();
      fUPanelArrayList.add(UPanelFactory.createUpanel(s));
    }
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
    this.setSize(new Dimension(800, 600));
    this.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        UGUIMainWindow.this.requestFocus();
      }

      public void mouseClicked(MouseEvent e) {
        this_mouseClicked(e);
      }
    });
    //キーボードショートカットの設定
    this.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        int keycode = e.getKeyCode();
        if (e.isControlDown() && e.isAltDown() == false) {
          if (keycode == KeyEvent.VK_S) {
            fUOrderButtonPanel.LimitSell();
          } else if (keycode == KeyEvent.VK_B) {
            fUOrderButtonPanel.LimitBuy();
          }
        } else if (e.isAltDown() && e.isControlDown() == false) {
          if (keycode == KeyEvent.VK_S) {
            fUOrderButtonPanel.MarketSell();
          } else if (keycode == KeyEvent.VK_B) {
            fUOrderButtonPanel.MarketBuy();
          }
        }
      }
    });
    jMenuFile.setText(fRb.getString("FILE"));
    jMenuFileSave.setText(fRb.getString("SAVE"));
    jMenuFileSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileSave_actionPerformed(e);
      }
    });
    jMenuFileQuit.setText(fRb.getString("QUIT_WINDOW"));
    jMenuFileQuit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        confirmExit();
      }
    });
    jMenuHelp.setText(fRb.getString("HELP"));
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
    jMenuHelpAbout.setText(fRb.getString("ABOUT"));
    jMenuHelpAbout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
      }
    });

    fMainTabbedPane.setBounds(new Rectangle(25, 224, 753, 320));
    fUTopPanel.setBorder(BorderFactory.createEtchedBorder());
    fUTopPanel.setBounds(new Rectangle(25, 11, 597, 112));
    fUTopPanel.setLayout(null);
    fUOrderButtonPanel.setToolTipText("");
    fUOrderButtonPanel.setBounds(new Rectangle(25, 126, 440, 97));
    fUOrderButtonPanel.setLayout(null);
    fUSubPanel.setBorder(BorderFactory.createEtchedBorder());
    fUSubPanel.setBounds(new Rectangle(467, 127, 154, 93));
    fInfoPanel.setBounds(new Rectangle(621, 10, 165, 212));
    fInfoPanel.setLayout(null);
    contentPane.setPreferredSize(new Dimension(800, 600));
    jMenuFile.add(jMenuFileSave);
    jMenuFile.add(jMenuFileQuit);
    jMenuHelp.add(jMenuHelpTutorial);
    jMenuHelp.addSeparator();
    jMenuHelp.add(jMenuHelpDownload);
    jMenuHelp.add(jMenuHelpReport);
    jMenuHelp.addSeparator();
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuHelp);
    Iterator iter = fUPanelArrayList.iterator();
    while (iter.hasNext()) {
      UPanel upanel = (UPanel) iter.next();
      fMainTabbedPane.add(upanel, upanel.getName());
    }
    fMainTabbedPane.getModel().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        SingleSelectionModel model = (SingleSelectionModel) e.getSource();
        UPanel panel = (UPanel) fUPanelArrayList.get(model.getSelectedIndex());
        panel.setVisible(true); //これをしないとpanelが見えていないことになり，gUpdate が実行されない．設計ミス(;_;)
        panel.gUpdate();
      }
    });
    contentPane.add(fMainTabbedPane, null);
    contentPane.add(fUSubPanel, null);
    contentPane.add(fUTopPanel, null);
    contentPane.add(fUOrderButtonPanel, null);
    contentPane.add(fInfoPanel, null);
    this.setJMenuBar(jMenuBar1);
  } // end of jInit

  public void setCProtocol(UProtocolCore cp) {
    fCProtocol = cp;
    UPanel.setCProtocol(fCProtocol);
    UGUIUpdateManager.setCProtocol(fCProtocol);
    UGUIUpdateManager.addFinalObserver(new IGUIObserver() {
      public void gUpdate() {
        fUpdateTimer.stop();
        new UFinalResultDialog(fUGUI.getSuperUserCProtocol()); // TODO 最終結果の表示
      }

      public void addUpdateObserverList() {
      }
    });
  }

  public void setTimer(javax.swing.Timer timer) {
    fUpdateTimer = timer;
    fUTopPanel.setTimer(timer);
  }

  public void gUpdate() {
    /** update 方針変更．UGUIUpdateManager に全部任せる．
     * その代り，UPanel 系は初期化時に登録が必要．
     */
    UGUIUpdateManager.update();
    /*
       Iterator iter = fUPanelArrayList.iterator();
       while(iter.hasNext()){
            ((UPanel)iter.next()).gUpdate();
       }
       //Top と Sub でけ別であまりよろしくない．
            fUTopPanel.gUpdate();
            fUSubPanel.gUpdate();
     */
  }

  //[ファイル[保存]
  public void jMenuFileSave_actionPerformed(ActionEvent e) {
    JFileChooser saveDialog = new JFileChooser();
    //起動フォルダ
    saveDialog.setCurrentDirectory(new File("."));
    //CSV のみを対象
    saveDialog.setFileFilter(new UCsvFileFilter());
    if (saveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = saveDialog.getSelectedFile();
      String baseName = file.getAbsolutePath();
      int noOfTabs = fMainTabbedPane.getComponentCount();
      for (int i = 0; i < noOfTabs; ++i) {
        try {
          ( (UPanel) fMainTabbedPane.getComponent(i)).writeLog(baseName);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  //[ヘルプ|バージョン情報]
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    int type;
    if (fUGUI.getName().equals("Network")) {
      type = UGUIMainWindow_AboutBox.TRADING_TERMINAL;
    } else {
      type = UGUIMainWindow_AboutBox.MARKET_SIMULATOR;
    }
    UGUIMainWindow_AboutBox dlg = new UGUIMainWindow_AboutBox(this, type);
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
      urlString = getClass().getResource(UParameters.JAPANESE_TUTORIAL_FILE).toString();
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
      confirmExit();
    }
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
       public void appendInfo(String s){
           fInfoTextArea.append(s+"\n");
           Document doc    = fInfoTextArea.getDocument();
           Position last = doc.getEndPosition();  // ドキュメントの末尾を表す位置を取得
           int pos = last.getOffset();
       fInfoTextArea.getCaret().setDot(pos);             // Caretをドキュメントの末尾に設定
       }
   */
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

  void this_mouseClicked(MouseEvent e) {
    this.requestFocus();
  }
}
