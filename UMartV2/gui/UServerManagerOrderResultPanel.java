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
import javax.swing.event.*;
import javax.swing.table.*;

import cmdCore.*;

/**
 * 過去の注文結果表示用タブ．
 */
public class UServerManagerOrderResultPanel extends UPanel {
  protected Object[] fOrderResultHeaderData = {
      fRb.getString("ORDER_ID"),
      fRb.getString("ORDER_DAY"),
      fRb.getString("ORDER_SESSION"),
      fRb.getString("SELL_BUY"),
      fRb.getString("ORDER_VOLUME"),
      fRb.getString("MARKET_LIMIT"),
      fRb.getString("PRICE"),
      fRb.getString("CANCEL_VOLUME"),
      fRb.getString("CONTRACT_ID"),
      fRb.getString("CONTRACT_DAY"),
      fRb.getString("CONTRACT_SESSION"),
      fRb.getString("CONTRACT_PRICE"),
      fRb.getString("CONTRACT_VOLUME")};
  protected Vector fOrderResultHeader = new Vector();
  protected Vector fOrderResultData = new Vector();
  protected ArrayList fOrderResultHistory = new ArrayList();
  protected JScrollPane fOrderResultScrollPane;
  protected int fCurrentIndex = 0;
  UTable fOrderResultTable;
  JLabel fAbbrLabel = new JLabel();
  private boolean fIsUpdating = false;
  protected UUserChooser fUserChooser = new UUserChooser();

  public UServerManagerOrderResultPanel() {
    fName = fRb.getString("CONTRACTS");
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * interface IGUIObserver を持つものは UGUIUpdateManager の
   * addDayObserver か addStepObserver を呼ぶ必要あり．すべてのUpanelで必要．
   */
  public void addUpdateObserverList() {
    UGUIUpdateManager.addAlwaysObserver(this);
  }

  private void jbInit() throws Exception {
    this.setLayout(null);
    for (int i = 0; i < fOrderResultHeaderData.length; i++) {
      fOrderResultHeader.add(fOrderResultHeaderData[i]);
    }
    fOrderResultTable = new UTable(new Vector(), fOrderResultHeader);
    fOrderResultScrollPane = new JScrollPane(fOrderResultTable);
    fOrderResultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.
        HORIZONTAL_SCROLLBAR_AS_NEEDED);
    fOrderResultScrollPane.setBorder(BorderFactory.createEtchedBorder());
    fOrderResultScrollPane.setBounds(new Rectangle(7, 105, 672, 376));
    this.add(fOrderResultScrollPane, null);
    fOrderResultScrollPane.getViewport().add(fOrderResultTable, null);
    if (!System.getProperties().getProperty("user.language").equals("ja")) {
      fAbbrLabel.setText(
          "S/B: Sell/Buy   Vol: Volume   M/L: Market/Limit   Ses.: Session"
          + "   O-: Order   C-: Contract   Can-: Cancel");
    }
    fAbbrLabel.setForeground(Color.black);
    fAbbrLabel.setBounds(new Rectangle(13, 487, 606, 17));
    this.add(fAbbrLabel, null);
    // UUserChooserの設定
    fUserChooser.setBounds(new Rectangle(8, 8, 423, 90));
    this.add(fUserChooser, null);
    fUserChooser.addListSelectionListener(new javax.swing.event.
                                          ListSelectionListener() {
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        gUpdate();
      }
    });
    addComponentListener(new ComponentAdapter() {
      public void componentShown(ComponentEvent ce) {
        gUpdate();
      }
    });
  }

  public void dataUpdate() {
    fUserChooser.setupUserList(fCProtocol);
    int step = UGUIUpdateManager.getStep();
    UCOrderCheckCore cOrderCheck = (UCOrderCheckCore) fCProtocol.getCommand(
        UCOrderCheckCore.CMD_NAME);
    UCOrderIDHistoryCore cOrderIDHistory = (UCOrderIDHistoryCore) fCProtocol.
        getCommand(UCOrderIDHistoryCore.CMD_NAME);
    cOrderIDHistory.setArguments(fUserChooser.getTargetUserID(), step); // まずは全部とってくる
    cOrderIDHistory.doIt();
    ArrayList orderID = new ArrayList(cOrderIDHistory.getOrderIDHistory());
    Iterator iter = orderID.iterator();
    fOrderResultData = new Vector();
    while (iter.hasNext()) {
      long id = ( (Long) iter.next()).longValue();
      Vector currentOrderResult = new Vector();
      cOrderCheck.setArguments(id);
      cOrderCheck.doIt();
      HashMap hm = cOrderCheck.getData();
      ArrayList contractArray = (ArrayList) hm.get(
          "ARRAYLIST_CONTRACT_INFORMATION_ARRAY");
      currentOrderResult.add(hm.get("LONG_ORDER_ID"));
      currentOrderResult.add(hm.get("INT_ORDER_DATE"));
      currentOrderResult.add(hm.get("INT_ORDER_BOARD_NO"));
      if ( ( (Integer) hm.get("INT_SELL_BUY")).intValue() == 1) {
        currentOrderResult.add(fRb.getString("SELL"));
      } else {
        currentOrderResult.add(fRb.getString("BUY"));
      }
      currentOrderResult.add(hm.get("LONG_ORDER_VOLUME"));
      if ( ( (Integer) hm.get("INT_MARKET_LIMIT")).intValue() == 1) {
        currentOrderResult.add(fRb.getString("MARKET"));
      } else {
        currentOrderResult.add(fRb.getString("LIMIT"));
      }
      currentOrderResult.add(hm.get("LONG_ORDER_PRICE"));
      currentOrderResult.add(hm.get("LONG_CANCEL_VOLUME"));
      if (contractArray.size() == 0) {
        currentOrderResult.add(null);
        currentOrderResult.add(null);
        currentOrderResult.add(null);
        currentOrderResult.add(null);
        currentOrderResult.add(null);
        fOrderResultData.add(currentOrderResult);
        continue;
      }
      Iterator iter2 = contractArray.iterator();
      while (iter2.hasNext()) {
        HashMap tmpContract = (HashMap) iter2.next();
        Vector currentContractResult = new Vector();
        Vector currentResult = new Vector(currentOrderResult);
        currentContractResult.add(tmpContract.get("LONG_CONTRACT_ID"));
        currentContractResult.add(tmpContract.get("INT_CONTRACT_DATE"));
        currentContractResult.add(tmpContract.get("INT_CONTRACT_BOARD_NO"));
        currentContractResult.add(tmpContract.get("LONG_CONTRACT_PRICE"));
        currentContractResult.add(tmpContract.get("LONG_CONTRACT_VOLUME"));
        currentResult.addAll(currentContractResult);
        fOrderResultData.add(currentResult);
      }
    }
  }

  public boolean getUpdaing() {
    return fIsUpdating;
  }

  public void setUpdaing(boolean b) {
    fIsUpdating = b;
  }

  public void gUpdate() {
    if (!isVisible()) {
      return;
    }
    IGUIEvent iev = new IGUIEvent() {
      public void execute() {
        dataUpdate();
      }

      public Runnable getRunnableForInvokeLater() {
        return new Runnable() {
          public void run() {
            fOrderResultTable.setModel(new DefaultTableModel(fOrderResultData,
                fOrderResultHeader));
            repaint();
          }
        };
      }
    };
    UGUIEventQueue.getInstance().addIGUIEvent(iev);
  }

}