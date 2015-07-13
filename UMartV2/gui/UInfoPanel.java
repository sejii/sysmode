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
import javax.swing.text.*;

import cmdCore.*;
import serverCore.*;

public class UInfoPanel extends UPanel {
  protected JTextArea fInfoTextArea = new JTextArea();
  JScrollPane jScrollPane1 = new JScrollPane();
  JLabel fInfoLabel = new JLabel();

  public UInfoPanel() {
    fInfoLabel.setFont(new java.awt.Font("Dialog", 0, 14));
    fInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    fInfoLabel.setText(fRb.getString("LOGS"));
    fInfoLabel.setForeground(Color.black);
    fInfoLabel.setBounds(new Rectangle(7, 5, 150, 17));
    jScrollPane1.setToolTipText("");
    jScrollPane1.setBounds(new Rectangle(3, 29, 158, 179));
    jScrollPane1.getViewport().add(fInfoTextArea, null);
    fInfoTextArea.setEditable(false);
    fInfoTextArea.setText(fRb.getString("DAY") + " " + fRb.getString("SESSION") +
                          " " + fRb.getString("CURRENT_SPOT_PRICE_FOR_LOGS") +
                          " " + fRb.getString("CURRENT_U-MART_PRICE_FOR_LOGS") +
                          "\n");

    add(jScrollPane1, null);
    add(fInfoLabel, null);
  }

  /**
   * interface IGUIObserver を持つものは UGUIUpdateManager の
   * addDayObserver か addStepObserver を呼ぶ必要あり，すべてのUpanelで必要．
   * ここではインフォメーションパネルのアップデートのために呼んでいる．
   */
  public void addUpdateObserverList() {
    UGUIUpdateManager.addStepObserver(this);
  }

  public void gUpdate() {
    IGUIEvent iev = new IGUIEvent() {
      public void execute() {
        int status = UGUIUpdateManager.getMarketStatus();
        if (status == UServerStatus.ACCEPT_ORDERS) {
          //appendInfo("Accept orders.");
          String spotPrice = "", futurePrice = "", infoLine = "";
          //現物，先物価格更新
          UCSpotPriceCore cSpotPrice = (UCSpotPriceCore) fCProtocol.getCommand(
              UCSpotPriceCore.CMD_NAME);
          cSpotPrice.setArguments(new StringTokenizer("j30 1"));
          cSpotPrice.doIt();
          Iterator iter = cSpotPrice.getResults().iterator();
          if (iter.hasNext()) {
            HashMap elem = (HashMap) iter.next();
            spotPrice = elem.get(UCSpotPriceCore.LONG_PRICE).toString();
          }
          UCFuturePriceCore cFuturePrice =
              (UCFuturePriceCore) fCProtocol.getCommand(UCFuturePriceCore.
              CMD_NAME);
          cFuturePrice.setArguments(new StringTokenizer("j30 1"));
          cFuturePrice.doIt();
          iter = cFuturePrice.getResults().iterator();
          if (iter.hasNext()) {
            HashMap elem = (HashMap) iter.next();
            futurePrice = elem.get(UCFuturePriceCore.LONG_PRICE).toString();
          }

          infoLine = String.valueOf(UGUIUpdateManager.getDate()) + " " +
              String.valueOf(UGUIUpdateManager.getBoard()) + " " + spotPrice +
              " " + futurePrice;
          appendInfo(infoLine);
        } else if (
            status == UServerStatus.AFTER_MARKING_TO_MARKET) {
          // appendInfo("Market is closed.");
        }
      }

      public Runnable getRunnableForInvokeLater() {
        return new Runnable() {
          public void run() {
            repaint();
          }
        };
      }
    };
    UGUIEventQueue.getInstance().addIGUIEvent(iev);
  }

  public void appendInfo(String s) {
    fInfoTextArea.append(s + "\n");
    Document doc = fInfoTextArea.getDocument();
    Position last = doc.getEndPosition(); // ドキュメントの末尾を表す位置を取得
    int pos = last.getOffset();
    fInfoTextArea.getCaret().setDot(pos); // Caretをドキュメントの末尾に設定
  }

  public static void main(String[] args) {
    UInfoPanel UInfoPanel1 = new UInfoPanel();
  }

}