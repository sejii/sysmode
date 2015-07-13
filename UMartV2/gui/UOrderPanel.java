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
import javax.swing.event.*;
import javax.swing.table.*;

import cmdCore.*;

/**
 * 当日分の注文表示用タブ. 
 */
public class UOrderPanel extends UPanel implements IOrderObserver {
  protected Object[] fOrderHeaderData = {fRb.getString("ORDER_ID"),
      fRb.getString("ORDER_TIME"), fRb.getString("MARKET_LIMIT"),
      fRb.getString("SELL_BUY"), fRb.getString("PRICE"),
      fRb.getString("ORDER_VOLUME"), fRb.getString("CONTRACT_VOLUME"),
      fRb.getString("CANCEL")};

  //キャンセル時の約定判定用．TODO Magic Number ではなく文字列検索で動的に決めるべき
  protected final int ORDER_VOL_COLUMN = 5;

  protected final int CONTRACT_VOL_COLUMN = 6;

  // 上の CANCEL だけは JCheckBox なので isCellEditable のために特別列番号を控える．
  protected final int CANCEL_COLUMN = 7;

  protected Vector fOrderHeader = new Vector();

  protected Vector fOrderData = new Vector();

  protected JScrollPane fOrderScrollPane = new JScrollPane();

  UTable fOrderTable;

  //protected NumberFormat numFormat = NumberFormat.getNumberInstance();

  public UOrderPanel() {
    fName = fRb.getString("ORDER");
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * interface IGUIObserver を持つものは UGUIUpdateManager の addDayObserver か
   * addStepObserver を呼ぶ必要あり．すべてのUPanelで必要．
   */
  public void addUpdateObserverList() {
    UGUIUpdateManager.addStepObserver(this);
  }

  private void jbInit() throws Exception {
    /**
     * このクラスは2種類のオブザーバになっている．ひとつはUpdateManager,もうひとつは UOrderButtonPanel．
     */
    UOrderButtonPanel.addObserver(this);
    this.setLayout(null);
    for (int i = 0; i < fOrderHeaderData.length; i++) {
      fOrderHeader.add(fOrderHeaderData[i]);
    }
    fOrderTable = new UTable(new Vector(), fOrderHeader) {
      public boolean isCellEditable(int r, int c) {
        if (c == CANCEL_COLUMN) {
          int orderVol = Integer.parseInt(fOrderTable.getValueAt(r,
              ORDER_VOL_COLUMN).toString());
          int contractVol = Integer.parseInt(fOrderTable.getValueAt(
              r, CONTRACT_VOL_COLUMN).toString());
          if (orderVol == contractVol) { // このときは約定している．
            JOptionPane
                .showMessageDialog(UParameters.getInstance()
                                   .getMainComponet(), fRb
                                   .getString("ALREADY_CONTRACTED"));
            return false;
          }
          return true;
        }
        return false;
      }
    };
    for (int i = 0; i < fOrderTable.getColumnCount(); i++) {
      //ソートとキャンセルボタンは両立しないのですべての列でソート不可とする．
      fOrderTable.setSortableAt(i, false);
    }
    fOrderTable.getTableHeader().setReorderingAllowed(false);
    fOrderScrollPane.getViewport().setBackground(Color.white);
    fOrderScrollPane.setBorder(BorderFactory.createRaisedBevelBorder());
    fOrderScrollPane.setBounds(new Rectangle(10, 15, 728, 244));
    fOrderScrollPane.getViewport().add(fOrderTable, null);
    this.add(fOrderScrollPane, null);
    //TODO デフォルトの昇順降順を決める。
    //    boolean ascend[]={true,true,true,true,false,false,false};
    //    fOrderTable.setDefaultAscending(ascend);
  }

  public void dataUpdate() {
    UCOrderStatusCore cOrderStatus = (UCOrderStatusCore) fCProtocol
        .getCommand(UCOrderStatusCore.CMD_NAME);
    cOrderStatus.doIt();
    ArrayList array = cOrderStatus.getResults();
    fOrderData = new Vector();
    Iterator iter = array.iterator();
    while (iter.hasNext()) {
      HashMap orderMap = (HashMap) iter.next();
      Vector currentOrder = new Vector();
      //	    currentOrder.add( new Long(
      // (String)os.get(UCOrderStatusCore.LONG_ORDER_ID) ));
      currentOrder.add(orderMap.get(UCOrderStatusCore.LONG_ORDER_ID));
      currentOrder.add( (String) orderMap
                       .get(UCOrderStatusCore.STRING_ORDER_TIME));
      //	    if( Integer.parseInt(
      // (String)os.get(UCOrderStatusCore.INT_MARKET_LIMIT)) == 1){
      //            if(
      // ((Integer)os.get(UCOrderStatusCore.INT_MARKET_LIMIT)).intValue()
      // == 1){
      if (Integer.parseInt( ( (orderMap
                               .get(UCOrderStatusCore.INT_MARKET_LIMIT)).
                             toString())) == 1) {
        currentOrder.add(fRb.getString("MARKET"));
      } else {
        currentOrder.add(fRb.getString("LIMIT"));
      }
      if (Integer.parseInt( (orderMap.get(UCOrderStatusCore.INT_SELL_BUY))
                           .toString()) == 1) {
        currentOrder.add(fRb.getString("SELL"));
      } else {
        currentOrder.add(fRb.getString("BUY"));
      }
      currentOrder.add(new UFormatLong(new Long(orderMap.get(
          UCOrderStatusCore.LONG_PRICE).toString())));
      currentOrder.add(new UFormatLong(new Long(orderMap.get(
          UCOrderStatusCore.LONG_VOLUME).toString())));
      currentOrder.add(new UFormatLong(new Long(orderMap.get(
          UCOrderStatusCore.LONG_CONTRACTED_VOLUME).toString())));
      currentOrder.add(new Boolean(false)); // Cancel用 JCheckBox
      // でキャンセルできるようにする．05/03/08
      fOrderData.add(currentOrder);
    }
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
            fOrderTable.setModel(new CancelAcceptTableModel(
                fOrderData, fOrderHeader));
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
            fOrderTable.setModel(new CancelAcceptTableModel(
                fOrderData, fOrderHeader));
            repaint();
          }
        };
      }
    };
    UGUIEventQueue.getInstance().addIGUIEvent(iev);
  }

  /**
   * @author mori テーブルでキャンセルを実行せざるをえなくなって作ったTableModel
   *         キャンセル処理がここのTableとボタンの2箇所に分岐している．
   *         static な notifyOrderObservers を呼ばなくてはならないので，UOrderButtonPanel と
   *         密になることは避けられない．
   * @see UOrderButtonPanel
   */
  class CancelAcceptTableModel extends DefaultTableModel {
    public CancelAcceptTableModel(Vector data, Vector header) {
      super(data, header);
      addTableModelListener(new TableModelListener() {
        public void tableChanged(TableModelEvent e) {
          final TableModelEvent finalTEvent = e;
          IGUIEvent iev = new IGUIEvent() {
            public void execute() {
              //なし
            }

            public Runnable getRunnableForInvokeLater() {
              return new Runnable() {
                public void run() {
                  int row = finalTEvent.getLastRow();
                  int column = finalTEvent.getColumn();
                  Object num = fOrderTable.getValueAt(row, 0);
                  long orderID = 0;
                  try {
                    orderID = Long
                        .parseLong(num.toString());
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                  UCOrderCancelCore uoc = (UCOrderCancelCore) fCProtocol
                      .getCommand(UCOrderCancelCore.CMD_NAME);
                  uoc.setArguments(orderID);
                  UCommandStatus cmdStatus = uoc.doIt();
                  UOrderButtonPanel.notifyOrderObservers();
                }
              };
            }
          };
          //Cancel は特急イベント
          UGUIEventQueue.getInstance().addIGUIEventToTop(iev);
        }
      });
    }
  }
}