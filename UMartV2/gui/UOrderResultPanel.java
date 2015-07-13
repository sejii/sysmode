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
import javax.swing.*;
import javax.swing.table.*;

import cmdCore.*;

/**
 * 過去の注文結果表示用タブ．
 */
public class UOrderResultPanel extends UPanel implements IOrderObserver {
  protected Object[] fOrderResultHeaderData = {
      fRb.getString("ORDER_ID"),
      fRb.getString("DAY"),
      fRb.getString("SESSION"),
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

  /** 表示したいユーザーのID */
  protected int fTargetUserID = -1;

  public UOrderResultPanel() {
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
    UGUIUpdateManager.addStepObserver(this);
  }

  private void jbInit() throws Exception {
    /**
     * このクラスは2種類のオブザーバになっている．ひとつはUpdateManager,もうひとつは
     * UOrderButtonPanel．
     */
    // ISAGA バグ対応 いずれ修正必要。
    UOrderButtonPanel.addObserver(this);
    this.setLayout(null);
    for (int i = 0; i < fOrderResultHeaderData.length; i++) {
      fOrderResultHeader.add(fOrderResultHeaderData[i]);
    }
    fOrderResultTable = new UTable(new Vector(), fOrderResultHeader);
    fOrderResultScrollPane = new JScrollPane(fOrderResultTable);
    fOrderResultScrollPane.setBorder(BorderFactory.createEtchedBorder());
    fOrderResultScrollPane.setBounds(new Rectangle(7, 15, 739, 236));
    fAbbrLabel.setText(fRb.getString("ORDER_RESULT_ABBR"));
    fAbbrLabel.setForeground(Color.black);
    fAbbrLabel.setBounds(new Rectangle(10, 253, 698, 17));
    this.add(fOrderResultScrollPane, null);
    this.add(fAbbrLabel, null);
    fOrderResultScrollPane.getViewport().add(fOrderResultTable, null);
    //TODO デフォルトの昇順降順を決める。
//    boolean ascend[]={true,true,true,true,false,true,false,false,true,true,true,false,false};
//    fOrderResultTable.setDefaultAscending(ascend);
  }

  public int getTargetUserID() {
    return fTargetUserID;
  }

  public void setTargetUserID(int id) {
    fTargetUserID = id;
  }

  public void dataUpdate() {
    int step = UGUIUpdateManager.getStep();
    UCOrderCheckCore cOrderCheck = (UCOrderCheckCore) fCProtocol.getCommand(
        UCOrderCheckCore.CMD_NAME);
    UCOrderIDHistoryCore cOrderIDHistory = (UCOrderIDHistoryCore) fCProtocol.
        getCommand(UCOrderIDHistoryCore.CMD_NAME);
    cOrderIDHistory.setArguments(fTargetUserID, step); // まずは全部とってくる
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
      currentOrderResult.add(new UFormatLong( (Long) hm.get("LONG_ORDER_ID")));
      currentOrderResult.add( (Integer) hm.get("INT_ORDER_DATE"));
      currentOrderResult.add( (Integer) hm.get("INT_ORDER_BOARD_NO"));
      if ( ( (Integer) hm.get("INT_SELL_BUY")).intValue() == 1) {
        currentOrderResult.add(fRb.getString("SELL"));
      } else {
        currentOrderResult.add(fRb.getString("BUY"));
      }
      currentOrderResult.add(new UFormatLong( (Long) hm.get("LONG_ORDER_VOLUME")));
      if ( ( (Integer) hm.get("INT_MARKET_LIMIT")).intValue() == 1) {
        currentOrderResult.add(fRb.getString("MARKET"));
      } else {
        currentOrderResult.add(fRb.getString("LIMIT"));
      }
      currentOrderResult.add(new UFormatLong( (Long) hm.get("LONG_ORDER_PRICE")));
      currentOrderResult.add(new UFormatLong( (Long) hm.get(
          "LONG_CANCEL_VOLUME")));
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
        currentContractResult.add(new UFormatLong( (Long) tmpContract.get(
            "LONG_CONTRACT_ID")));
        currentContractResult.add( (Integer) tmpContract.get(
            "INT_CONTRACT_DATE"));
        currentContractResult.add( (Integer) tmpContract.get(
            "INT_CONTRACT_BOARD_NO"));
        currentContractResult.add(new UFormatLong( (Long) tmpContract.get(
            "LONG_CONTRACT_PRICE")));
        currentContractResult.add(new UFormatLong( (Long) tmpContract.get(
            "LONG_CONTRACT_VOLUME")));
        currentResult.addAll(currentContractResult);
        fOrderResultData.add(currentResult);
        //TODO デフォルトの昇順、降順、例えばの例。あまり使いやすくないけど、滅多に使わないのでどうしても必要があればということで。
      }
    }
//    Iterator it = fOrderResultData.iterator();
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

  public void updateOrderObserver() {
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

  private void setupOrderResultData(ArrayList array) {
    int step = UGUIUpdateManager.getStep();
    UCOrderCheckCore cOrderCheck = (UCOrderCheckCore) fCProtocol.getCommand(
        UCOrderCheckCore.CMD_NAME);
    UCOrderIDHistoryCore cOrderIDHistory = (UCOrderIDHistoryCore) fCProtocol.
        getCommand(UCOrderIDHistoryCore.CMD_NAME);
    cOrderIDHistory.setArguments(fTargetUserID, step); // まずは全部とってくる
    cOrderIDHistory.doIt();
    ArrayList orderID = new ArrayList(cOrderIDHistory.getOrderIDHistory());
    Iterator iter = orderID.iterator();
    while (iter.hasNext()) {
      long id = ( (Long) iter.next()).longValue();
      Vector currentOrderResult = new Vector();
      cOrderCheck.setArguments(id);
      cOrderCheck.doIt();
      HashMap hm = cOrderCheck.getData();
      ArrayList contractArray = (ArrayList) hm.get(
          "ARRAYLIST_CONTRACT_INFORMATION_ARRAY");
      currentOrderResult.add( (Long) hm.get("LONG_ORDER_ID"));
      currentOrderResult.add( (Integer) hm.get("INT_ORDER_DATE"));
      currentOrderResult.add( (Integer) hm.get("INT_ORDER_BOARD_NO"));
      if ( ( (Integer) hm.get("INT_SELL_BUY")).intValue() == 1) {
        currentOrderResult.add(fRb.getString("SELL"));
      } else {
        currentOrderResult.add(fRb.getString("BUY"));
      }
      currentOrderResult.add( (Long) hm.get("LONG_ORDER_VOLUME"));
      if ( ( (Integer) hm.get("INT_MARKET_LIMIT")).intValue() == 1) {
        currentOrderResult.add(fRb.getString("MARKET"));
      } else {
        currentOrderResult.add(fRb.getString("LIMIT"));
      }
      currentOrderResult.add( (Long) hm.get("LONG_ORDER_PRICE"));
      currentOrderResult.add( (Long) hm.get("LONG_CANCEL_VOLUME"));
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
        currentContractResult.add( (Long) tmpContract.get("LONG_CONTRACT_ID"));
        currentContractResult.add( (Integer) tmpContract.get(
            "INT_CONTRACT_DATE"));
        currentContractResult.add( (Integer) tmpContract.get(
            "INT_CONTRACT_BOARD_NO"));
        currentContractResult.add( (Long) tmpContract.get("LONG_CONTRACT_PRICE"));
        currentContractResult.add( (Long) tmpContract.get(
            "LONG_CONTRACT_VOLUME"));
        currentResult.addAll(currentContractResult);
        array.add(currentResult);
      }
    }
  }

  private void writeOrderInfoToLog(PrintWriter pw, Vector info) throws
      IOException {
    Iterator itr = info.iterator();
    while (itr.hasNext()) {
      pw.print(itr.next());
      if (!itr.hasNext()) {
        pw.println();
      } else {
        pw.print(",");
      }
    }
  }

  public void writeLog(String basename) throws IOException {
    ArrayList orderResultData = new ArrayList();
    setupOrderResultData(orderResultData);
    String filename = basename + "_order.csv";
    PrintWriter pw = new PrintWriter(new FileWriter(filename));
    pw.print("OrderID,Date,Session,Sell/Buy,OrderVolume,Market/Limit,OrderPrice,CancelVolume,");
    pw.print(
        "ContractID,ContractDate,ContractBoardNo,ContractPrice,ContractVolume");
    pw.println();
    Iterator itr = orderResultData.iterator();
    while (itr.hasNext()) {
      Vector info = (Vector) itr.next();
      System.err.println(info.toString());
      writeOrderInfoToLog(pw, info);
    }
    pw.close();
  }

}
