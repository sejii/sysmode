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

import cmdCore.*;
import serverCore.*;

/**
 * メインウィンドウ中央部の注文用のボタンをまとめたクラス．
 * キーボードショートカットに対応したため外部からも注文関数が呼べる．
 * (MarketSell(),MarketBuy(),LimitSell(),LimitBuy())
 */
public class UOrderButtonPanel extends UPanel {
  JButton fLimitSellButton = new JButton();

  JButton fLimitBuyButton = new JButton();

  JButton fMarketBuyButton = new JButton();

  JPanel fPriceVolumePanel = new JPanel();

  JButton fMarketSellButton = new JButton();

  JTextField fLimitPriceTextField = new JTextField();

  JTextField fVolumeTextField = new JTextField();

  JLabel fLimitPriceLabel = new JLabel();

  JLabel fVolumeLabel = new JLabel();

  JCheckBox fConfirmCheckBox = new JCheckBox();

  protected UGUIMainWindow fMainWindow;

  JButton fPriceUpButton = new JButton();

  JButton fPriceDownButton = new JButton();

  JButton fVolumeUpButton = new JButton();

  JButton fVolumeDownButton = new JButton();

  boolean fIsPressed = true;

  JButton fCancelButton = new JButton();

  private static Vector fObserverList = new Vector(); // 注文時に変更すべきパネルのリスト

  public UOrderButtonPanel() {
    fName = "Order Button";
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * 本当は，addUpdateObserverList はこのクラスではいらない．ちょっと設計ヘボいかも． UCommand 系を使いたいたかったので
   * UPanel を継承した結果，以下の実装が必要になった． しかし，このパネルは受動的な更新はしないので登録は不必要．
   */
  public void addUpdateObserverList() {
  }

  public UOrderButtonPanel(UGUIMainWindow mw) {
    fMainWindow = mw;
    fName = "Order Button";
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setMainWindow(UGUIMainWindow mw) {
    fMainWindow = mw;
  }

  void jbInit() throws Exception {
    this.setLayout(null);
    //指値
    fLimitSellButton.setText(fRb.getString("LIMIT_SELL"));
    fLimitSellButton.setBackground(new Color(235, 235, 235));
    fLimitSellButton.setBounds(new Rectangle(324, 13, 107, 31));
    fLimitSellButton.setFont(new java.awt.Font("Dialog", 1, 12));
    fLimitSellButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fLimitSellButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        LimitSell();
      }
    });

    fLimitBuyButton.setText(fRb.getString("LIMIT_BUY"));
    fLimitBuyButton.setBackground(Color.lightGray);
    fLimitBuyButton.setBounds(new Rectangle(324, 55, 107, 31));
    fLimitBuyButton.setFont(new java.awt.Font("Dialog", 1, 12));
    fLimitBuyButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fLimitBuyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        LimitBuy();
      }
    });
    fLimitPriceLabel.setBounds(new Rectangle(13, 34, 74, 17));
    fLimitPriceLabel.setText(fRb.getString("LIMIT_PRICE"));
    fLimitPriceLabel.setForeground(Color.black);
    fLimitPriceTextField.setBounds(new Rectangle(95, 31, 67, 21));
    fLimitPriceTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fLimitPriceTextField.setText("2500");
    fLimitPriceTextField.setFont(new java.awt.Font("SansSerif", 1, 12));
    fLimitPriceTextField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        UOrderButtonPanel.this.requestFocus();
      }
    });
    fVolumeLabel.setBounds(new Rectangle(11, 61, 93, 17));
    fVolumeLabel.setText(fRb.getString("ORDER_VOLUME"));
    fVolumeLabel.setForeground(Color.black);
    fVolumeLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    fVolumeTextField.setBounds(new Rectangle(95, 60, 67, 21));
    fVolumeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fVolumeTextField.setText("10");
    fVolumeTextField.setFont(new java.awt.Font("SansSerif", 1, 12));
    fVolumeTextField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        UOrderButtonPanel.this.requestFocus();
      }
    });
    fPriceVolumePanel.setBorder(BorderFactory.createEtchedBorder());
    fPriceVolumePanel.setBounds(new Rectangle(119, 7, 200, 85));
    fPriceVolumePanel.setLayout(null);
    this.setEnabled(true);
    this.setSize(new Dimension(603, 98));
    fPriceUpButton.setBounds(new Rectangle(164, 32, 9, 9));
    fPriceUpButton.setBounds(new Rectangle(164, 32, 9, 9));
    fPriceUpButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fPriceUpButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        long price = Long.parseLong(fLimitPriceTextField.getText());
        fLimitPriceTextField.setText(String.valueOf(price + 1));
      }
    });
    fPriceDownButton.setBounds(new Rectangle(164, 41, 9, 9));
    fPriceDownButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fPriceDownButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        long price = Long.parseLong(fLimitPriceTextField.getText());
        if (price < 1) {
          price = 1;
        }
        fLimitPriceTextField.setText(String.valueOf(price - 1));
      }
    });
    fVolumeUpButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fVolumeUpButton.setBounds(new Rectangle(164, 61, 9, 9));
    fVolumeUpButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        long vol = Long.parseLong(fVolumeTextField.getText());
        fVolumeTextField.setText(String.valueOf(vol + 1));
      }
    });
    fVolumeDownButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fVolumeDownButton.setBounds(new Rectangle(164, 70, 9, 9));
    fVolumeDownButton
        .addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        long vol = Long.parseLong(fVolumeTextField.getText());
        if (vol < 1) {
          vol = 1;
        }
        fVolumeTextField.setText(String.valueOf(vol - 1));
      }
    });
    fCancelButton.setBackground(Color.gray);
    fCancelButton.setBounds(new Rectangle(106, 4, 85, 22));
    fCancelButton.setForeground(Color.white);
    fCancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fCancelButton.setText(fRb.getString("CANCEL"));
    fCancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelOrder();
      }
    });
    fPriceVolumePanel.add(fLimitPriceTextField, null);
    fPriceVolumePanel.add(fLimitPriceLabel, null);
    fPriceVolumePanel.add(fVolumeLabel, null);
    fPriceVolumePanel.add(fVolumeTextField, null);
    fPriceVolumePanel.add(fPriceDownButton, null);
    fPriceVolumePanel.add(fPriceUpButton, null);
    fPriceVolumePanel.add(fVolumeUpButton, null);
    fPriceVolumePanel.add(fVolumeDownButton, null);
    fPriceVolumePanel.add(fCancelButton, null);
    fPriceVolumePanel.add(fConfirmCheckBox, null);
    this.add(fLimitBuyButton, null);
    this.add(fLimitSellButton, null);
    this.add(fMarketBuyButton, null);

    this.add(fMarketSellButton, null);

    this.add(fPriceVolumePanel, null);

    fMarketBuyButton.setText(fRb.getString("MARKET_BUY"));
    fMarketBuyButton.setBackground(Color.lightGray);
    fMarketBuyButton.setBounds(new Rectangle(7, 55, 107, 31));
    fMarketBuyButton.setFont(new java.awt.Font("Dialog", 1, 12));
    fMarketBuyButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fMarketBuyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MarketBuy();
      }
    });

    fMarketSellButton.setBackground(new Color(235, 235, 235));
    fMarketSellButton.setBounds(new Rectangle(7, 13, 107, 31));
    fMarketSellButton.setFont(new java.awt.Font("Dialog", 1, 12));
    fMarketSellButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fMarketSellButton.setText(fRb.getString("MARKET_SELL"));
    fMarketSellButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MarketSell();
      }
    });
    fConfirmCheckBox.setSelected(true);
    fConfirmCheckBox.setText(fRb.getString("CONFIRMATION"));
    fConfirmCheckBox.setBounds(new Rectangle(2, 3, 102, 25));
  }

  public void MarketSell() {
    disnableAllButton();
    long volume;
    boolean isOrder = false; // 注文を出すかどうか
    try {
      volume = Long.parseLong(fVolumeTextField.getText());
    } catch (Exception ex) {
      JOptionPane
          .showMessageDialog(UParameters.getInstance()
                             .getMainComponet(), fRb
                             .getString("NUMERICAL_VALUE_NEEDED"));
      enableAllButton();
      return;
    }
    if (volume <= 0) {
      JOptionPane.showMessageDialog(UParameters.getInstance()
                                    .getMainComponet(),
                                    fRb.getString("POSITIVE_VALUE_NEEDED"));
      enableAllButton();
      return;
    }

    if (fConfirmCheckBox.isSelected()) {
      int ans = JOptionPane.showConfirmDialog(UParameters.getInstance()
                                              .getMainComponet(),
                                              fRb.getString("ORDER_VOLUME") +
                                              ": "
                                              + fVolumeTextField.getText(),
                                              fRb.getString("CONFIRMATION")
                                              + ": " +
                                              fRb.getString("MARKET_SELL"),
                                              JOptionPane.OK_CANCEL_OPTION);
      if (ans == JOptionPane.OK_OPTION) {
        isOrder = true;
      }
    } else {
      isOrder = true;
    }
    //無名クラスにはfinal変数しか渡せない
    final long finalVolume = volume;
    final boolean finalIsOrder = isOrder;
    IGUIEvent iev = new IGUIEvent() {
      public void execute() {
        if (finalIsOrder) {
          UCOrderRequestCore uor = (UCOrderRequestCore) fCProtocol
              .getCommand(UCOrderRequestCore.CMD_NAME);
          uor.setArguments("j30", UOrder.NEW, UOrder.SELL,
                           UOrder.MARKET, 0, finalVolume);
          uor.doIt();
        }
      }

      public Runnable getRunnableForInvokeLater() {
        return new Runnable() {
          public void run() {
            notifyOrderObservers();
            enableAllButton();
          }
        };
      }
    };
    UGUIEventQueue.getInstance().addIGUIEvent(iev);
  }

  public void MarketBuy() {
    disnableAllButton();
    long volume;
    boolean isOrder = false; // 注文を出すかどうか
    try {
      volume = Long.parseLong(fVolumeTextField.getText());
    } catch (Exception ex) {
      JOptionPane
          .showMessageDialog(UParameters.getInstance()
                             .getMainComponet(), fRb
                             .getString("NUMERICAL_VALUE_NEEDED"));
      enableAllButton();
      return;
    }
    if (volume <= 0) {
      JOptionPane.showMessageDialog(UParameters.getInstance()
                                    .getMainComponet(),
                                    fRb.getString("POSITIVE_VALUE_NEEDED"));
      enableAllButton();
      return;
    }

    if (fConfirmCheckBox.isSelected()) {
      int ans = JOptionPane.showConfirmDialog(UParameters.getInstance()
                                              .getMainComponet(),
                                              fRb.getString("ORDER_VOLUME") +
                                              ": "
                                              + fVolumeTextField.getText(),
                                              fRb.getString("CONFIRMATION")
                                              + ": " +
                                              fRb.getString("MARKET_BUY"),
                                              JOptionPane.OK_CANCEL_OPTION);
      if (ans == JOptionPane.OK_OPTION) {
        isOrder = true;
      }
    } else {
      isOrder = true;
    }
    //無名クラスにはfinal変数しか渡せない
    final long finalVolume = volume;
    final boolean finalIsOrder = isOrder;
    IGUIEvent iev = new IGUIEvent() {
      public void execute() {
        if (finalIsOrder) {
          UCOrderRequestCore uor = (UCOrderRequestCore) fCProtocol
              .getCommand(UCOrderRequestCore.CMD_NAME);
          uor.setArguments("j30", UOrder.NEW, UOrder.BUY,
                           UOrder.MARKET, 0, finalVolume);
          uor.doIt();
        }
      }

      public Runnable getRunnableForInvokeLater() {
        return new Runnable() {
          public void run() {
            notifyOrderObservers();
            enableAllButton();
          }
        };
      }
    };
    UGUIEventQueue.getInstance().addIGUIEvent(iev);
  }

  public void LimitSell() {
    disnableAllButton();
    long price, volume;
    boolean isOrder = false;
    try {
      price = Long.parseLong(fLimitPriceTextField.getText());
      volume = Long.parseLong(fVolumeTextField.getText());
    } catch (Exception ex) {
      JOptionPane
          .showMessageDialog(UParameters.getInstance()
                             .getMainComponet(), fRb
                             .getString("NUMERICAL_VALUE_NEEDED"));
      enableAllButton();
      return;
    }
    if (price <= 0 || volume <= 0) {
      JOptionPane.showMessageDialog(UParameters.getInstance()
                                    .getMainComponet(),
                                    fRb.getString("POSITIVE_VALUE_NEEDED"));
      enableAllButton();
      return;
    }
    if (fConfirmCheckBox.isSelected()) {
      int ans = JOptionPane.showConfirmDialog(UParameters.getInstance()
                                              .getMainComponet(),
                                              fRb.getString("PRICE") + ": "
                                              + fLimitPriceTextField.getText() +
                                              "  "
                                              + fRb.getString("ORDER_VOLUME") +
                                              ": "
                                              + fVolumeTextField.getText(),
                                              fRb.getString("CONFIRMATION")
                                              + ": " +
                                              fRb.getString("LIMIT_SELL"),
                                              JOptionPane.OK_CANCEL_OPTION);
      if (ans == JOptionPane.OK_OPTION) {
        isOrder = true;
      }
    } else {
      isOrder = true;
    }
    //無名クラスにはfinal変数しか渡せない
    final long finalVolume = volume;
    final long finalPrice = price;
    final boolean finalIsOrder = isOrder;
    IGUIEvent iev = new IGUIEvent() {
      public void execute() {
        if (finalIsOrder) {
          UCOrderRequestCore uor = (UCOrderRequestCore) fCProtocol
              .getCommand(UCOrderRequestCore.CMD_NAME);
          uor.setArguments("j30", UOrder.NEW, UOrder.SELL,
                           UOrder.LIMIT, finalPrice, finalVolume);
          uor.doIt();
        }
      }

      public Runnable getRunnableForInvokeLater() {
        return new Runnable() {
          public void run() {
            notifyOrderObservers();
            enableAllButton();
          }
        };
      }
    };
    UGUIEventQueue.getInstance().addIGUIEvent(iev);
  }

  public void LimitBuy() {
    disnableAllButton();
    long price, volume;
    boolean isOrder = false;
    try {
      price = Long.parseLong(fLimitPriceTextField.getText());
      volume = Long.parseLong(fVolumeTextField.getText());
    } catch (Exception ex) {
      JOptionPane
          .showMessageDialog(UParameters.getInstance()
                             .getMainComponet(), fRb
                             .getString("NUMERICAL_VALUE_NEEDED"));
      enableAllButton();
      return;
    }
    if (price <= 0 || volume <= 0) {
      JOptionPane.showMessageDialog(UParameters.getInstance()
                                    .getMainComponet(),
                                    fRb.getString("POSITIVE_VALUE_NEEDED"));
      enableAllButton();
      return;
    }
    if (fConfirmCheckBox.isSelected()) {
      int ans = JOptionPane.showConfirmDialog(UParameters.getInstance()
                                              .getMainComponet(),
                                              fRb.getString("PRICE") + ": "
                                              + fLimitPriceTextField.getText() +
                                              "  "
                                              + fRb.getString("ORDER_VOLUME") +
                                              ": "
                                              + fVolumeTextField.getText(),
                                              fRb.getString("CONFIRMATION")
                                              + ": " +
                                              fRb.getString("LIMIT_BUY"),
                                              JOptionPane.OK_CANCEL_OPTION);
      if (ans == JOptionPane.OK_OPTION) {
        isOrder = true;
      }
    } else {
      isOrder = true;
    }
    //無名クラスにはfinal変数しか渡せない
    final long finalVolume = volume;
    final long finalPrice = price;
    final boolean finalIsOrder = isOrder;
    IGUIEvent iev = new IGUIEvent() {
      public void execute() {
        if (finalIsOrder) {
          UCOrderRequestCore uor = (UCOrderRequestCore) fCProtocol
              .getCommand(UCOrderRequestCore.CMD_NAME);
          uor.setArguments("j30", UOrder.NEW, UOrder.BUY,
                           UOrder.LIMIT, finalPrice, finalVolume);
          uor.doIt();
        }
      }

      public Runnable getRunnableForInvokeLater() {
        return new Runnable() {
          public void run() {
            notifyOrderObservers();
            enableAllButton();
          }
        };
      }
    };
    UGUIEventQueue.getInstance().addIGUIEvent(iev);
  }

  protected synchronized void disnableAllButton() {
    fMarketSellButton.setEnabled(false);
    fMarketBuyButton.setEnabled(false);
    fLimitSellButton.setEnabled(false);
    fLimitBuyButton.setEnabled(false);
  }

  protected synchronized void enableAllButton() {
    //TODO 連打禁止．もう少しエレガントな方法をいずれ探す．
    if (!fConfirmCheckBox.isSelected()) {
      try {
        Thread.sleep(500);
      } catch (Exception ex) {
      }
    }
    fMarketSellButton.setEnabled(true);
    fMarketBuyButton.setEnabled(true);
    fLimitSellButton.setEnabled(true);
    fLimitBuyButton.setEnabled(true);
  }

  public static synchronized void addObserver(IOrderObserver o) {
    fObserverList.add(o);
  }

  /**
   * 注文時に更新するパネルを限定して負荷を軽減するのが目的 注文後(LimitSell, LimitBuy, MarketSell,
   * MarketBuy)の InvokeLater 用 Runnable で設定される。
   */
  public static synchronized void notifyOrderObservers() {
    Iterator it = fObserverList.iterator();
    while (it.hasNext()) {
      IOrderObserver o = ( (IOrderObserver) it.next());
      synchronized (o) {
        o.updateOrderObserver();
      }
    }
  }

  /**
   * テーブルでキャンセル可能にせよとの命令から作ったメソッド．このクラスはキーボードショートカットも 実現するために無理をしている．
   */
  public static void cancelOrder() {
    String orderIDString = JOptionPane.showInputDialog(UParameters
        .getInstance().getMainComponet(), fRb.getString("ORDER_ID"));
    long orderID;
    if (orderIDString == null || orderIDString.length() == 0) {
      return;
    }
    try {
      orderID = Long.parseLong(orderIDString);
    } catch (Exception ex) {
      JOptionPane
          .showMessageDialog(UParameters.getInstance()
                             .getMainComponet(), fRb
                             .getString("NUMERICAL_VALUE_NEEDED"));
      return;
    }
    final long finalOrderID = orderID;
    IGUIEvent iev = new IGUIEvent() {
      public void execute() {
        UCOrderCancelCore uoc = (UCOrderCancelCore) fCProtocol
            .getCommand(UCOrderCancelCore.CMD_NAME);

        uoc.setArguments(finalOrderID);
        UCommandStatus cmdStatus = uoc.doIt();
      }

      public Runnable getRunnableForInvokeLater() {
        return new Runnable() {
          public void run() {
            notifyOrderObservers();
          }
        };
      }
    };
    //キャンセルは特急イベント
    UGUIEventQueue.getInstance().addIGUIEventToTop(iev);
  }
}