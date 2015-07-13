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
import java.awt.geom.*;
import javax.swing.*;

import cmdCore.*;

public class UBoardGraphPanel extends UPanel {
  protected String fSellName = fRb.getString("SELL");
  protected String fBuyName = fRb.getString("BUY");
  protected Color fSellColor = Color.blue;
  protected Color fBuyColor = Color.red;

  UGraph fBoardGraph = new UGraph();
  Vector fBoardData = new Vector();
  JTextField fContractPriceTextField = new JTextField();
  JTextField fContractVolumeTextField = new JTextField();
  JLabel fContractVolumeLabel = new JLabel();
  JLabel fContractPriceLabel = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  protected int fCurrentStep = 0;
  protected int fCurrentStatus = -1; // need to modify
  UValueLabel fDay = new UValueLabel();
  JLabel fDayLabel = new JLabel();
  JLabel fBoardLabel = new JLabel();
  UValueLabel fBoard = new UValueLabel();
  JLabel fPreviousBoardLabel = new JLabel();
  JToggleButton fXYToggleButton = new JToggleButton();
  protected boolean fIsExchangeXY = false;
  protected boolean fIsUpdated = false;

  public void addUpdateObserverList() {
    UGUIUpdateManager.addAlwaysObserver(this);
  }

  public UBoardGraphPanel() {
    fName = fRb.getString("ORDER_BOOK");
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void jbInit() throws Exception {
    this.setLayout(null);
    fContractPriceTextField.setFont(new java.awt.Font("Dialog", 1, 11));
    fContractPriceTextField.setDisabledTextColor(Color.black);
    fContractPriceTextField.setEditable(false);
    fContractPriceTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fContractPriceTextField.setBounds(new java.awt.Rectangle(285, 182, 55, 21));
    fContractVolumeTextField.setFont(new java.awt.Font("Dialog", 1, 11));
    fContractVolumeTextField.setDisabledTextColor(Color.black);
    fContractVolumeTextField.setEditable(false);
    fContractVolumeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    fContractVolumeTextField.setBounds(new java.awt.Rectangle(285, 238, 55, 21));
    fContractVolumeLabel.setText(fRb.getString("CONTRACT_VOLUME"));
    fContractVolumeLabel.setForeground(Color.black);
    fContractVolumeLabel.setBounds(new java.awt.Rectangle(272, 218, 99, 17));
    fContractPriceLabel.setBounds(new java.awt.Rectangle(272, 160, 92, 17));
    fContractPriceLabel.setText(fRb.getString("CONTRACT_PRICE"));
    fContractPriceLabel.setForeground(Color.black);

    fBoardGraph.setLayout(borderLayout1);
    fDay.setBounds(new java.awt.Rectangle(337, 87, 42, 21));
    fDayLabel.setText(fRb.getString("DAY") + ":");
    fDayLabel.setForeground(Color.black);
    fDayLabel.setBounds(new java.awt.Rectangle(267, 89, 67, 17));
    fBoardLabel.setText(fRb.getString("SESSION") + ":");
    fBoardLabel.setForeground(Color.black);
    fBoardLabel.setBounds(new java.awt.Rectangle(267, 121, 68, 17));
    fBoard.setBounds(new java.awt.Rectangle(337, 119, 42, 22));
    fPreviousBoardLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    fPreviousBoardLabel.setText(fRb.getString("PREVIOUS_PRICING"));
    fPreviousBoardLabel.setForeground(Color.black);
    fPreviousBoardLabel.setBounds(new java.awt.Rectangle(85, 4, 99, 17));
    fXYToggleButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fXYToggleButton.setText("Exchange X-Y");
    fXYToggleButton.setBounds(new java.awt.Rectangle(268, 43, 114, 25));
    fXYToggleButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fIsExchangeXY = fXYToggleButton.isSelected();
        fIsUpdated = true;
        gUpdate();
      }
    });

    this.setBorder(BorderFactory.createEtchedBorder());
    this.setBounds(new java.awt.Rectangle(325, 7, 387, 283));
    this.setLayout(null);
    fBoardGraph.setBorder(BorderFactory.createEtchedBorder());
    fBoardGraph.setBounds(new java.awt.Rectangle(5, 26, 257, 243));
    fBoardGraph.getGraph().add(new UGraphData(fSellName, fSellColor));
    fBoardGraph.getGraph().add(new UGraphData(fBuyName, fBuyColor));
    fBoardGraph.setLeftMargin(60);
    this.add(fBoardGraph, null);
    this.add(fContractVolumeTextField, null);
    this.add(fContractVolumeLabel, null);
    this.add(fContractPriceTextField, null);
    this.add(fContractPriceLabel, null);
    this.add(fBoardLabel, null);
    this.add(fDayLabel, null);
    this.add(fXYToggleButton, null);
    this.add(fPreviousBoardLabel, null);
    this.add(fDay, null);
    this.add(fBoard, null);
    fBoardGraph.setBackground(Color.white);
    fBoardGraph.setNumOfHorizontalLine(0);
    fBoardGraph.setNumOfVerticalLine(0);
    fBoardGraph.setFixedMaxX(1);
    fBoardGraph.setFixedMinX(0);
    fBoardGraph.setFixedMaxY(1);
    fBoardGraph.setFixedMinY(0);
    addComponentListener(new ComponentAdapter() {
      public void componentShown(ComponentEvent ce) {
        gUpdate();
      }
    });
  }

  public void dataUpdate() {
    //日付関係はUGUIUpdateManager経由で取得するように変更．
    int date = UGUIUpdateManager.getDate();
    int boardNo = UGUIUpdateManager.getBoard();
    int step = UGUIUpdateManager.getStep();
    int now = UGUIUpdateManager.getMarketStatus();

    if (now != fCurrentStatus) {
      fIsUpdated = true;
    }

    if (step <= fCurrentStep) {
      // Exchange X-Y がセットされている場合の処理．フラグ処理はあまりきれいではないが
      // X-Y の入れ換え自体が???なので勘弁してもらおう．
      if (fIsUpdated == false) {
        return;
      } else {
        fIsUpdated = false;
      }
    }

    if (boardNo == 1) {
      fDay.setText(String.valueOf(date - 1));
      fBoard.setText(String.valueOf(fParam.getBoardPerDay()));
    } else {
      fDay.setText(String.valueOf(date));
      if (now == fCurrentStatus) {
        fBoard.setText(String.valueOf(boardNo - 1));
      } else {
        fBoard.setText(String.valueOf(boardNo));
      }
    }
    fCurrentStep = step;
    fCurrentStatus = now;

    UCBoardDataCore cBoardData = (UCBoardDataCore) fCProtocol
        .getCommand(UCBoardDataCore.CMD_NAME);
    UCommandStatus status = cBoardData.doIt();
    ArrayList array;
    HashMap boardDataInfo;
    long maxPrice, minPrice, totalBuyVolume;
    Vector printData = new Vector();
    if (status.getStatus()) {
      array = cBoardData.getBoardDataArray();
      boardDataInfo = cBoardData.getBoardDataInfo();
      maxPrice = ( (Long) boardDataInfo
                  .get(UCBoardDataCore.LONG_MAX_PRICE)).longValue();
      minPrice = ( (Long) boardDataInfo
                  .get(UCBoardDataCore.LONG_MIN_PRICE)).longValue();
      totalBuyVolume = ( (Long) boardDataInfo
                        .get(UCBoardDataCore.LONG_TOTAL_BUY_VOLUME)).longValue();
      if (fIsExchangeXY == false) {
        fBoardGraph.setFixedMaxX(maxPrice);
        fBoardGraph.setFixedMinX(minPrice);
        fBoardGraph.setFixedMaxY(totalBuyVolume);
        fBoardGraph.setFixedMinY(0);
      } else {
        fBoardGraph.setFixedMaxX(totalBuyVolume);
        fBoardGraph.setFixedMinX(0);
        fBoardGraph.setFixedMaxY(maxPrice);
        fBoardGraph.setFixedMinY(minPrice);
      }
    } else {
      return;
    }
    long contractPrice = ( (Long) boardDataInfo
                          .get(UCBoardDataCore.LONG_CONTRACT_PRICE)).longValue();
    long contractVolume = ( (Long) boardDataInfo
                           .get(UCBoardDataCore.LONG_CONTRACT_VOLUME)).
        longValue();
    fContractPriceTextField.setText(String.valueOf(contractPrice));
    fContractVolumeTextField.setText(String.valueOf(contractVolume));
    ArrayList tmpSellData = new ArrayList();
    ArrayList tmpBuyData = new ArrayList();
    ArrayList tmpContractData = new ArrayList();
    if (fIsExchangeXY == false) {
      tmpContractData.add(new Point2D.Double(minPrice, contractVolume));
      tmpContractData.add(new Point2D.Double(contractPrice,
                                             contractVolume));
      tmpContractData.add(new Point2D.Double(contractPrice, 0));
      fBoardGraph.setXLableName("price");
      fBoardGraph.setYLableName("volume");
    } else {
      tmpContractData.add(new Point2D.Double(contractVolume, minPrice));
      tmpContractData.add(new Point2D.Double(contractVolume,
                                             contractPrice));
      tmpContractData.add(new Point2D.Double(0, contractPrice));
      fBoardGraph.setXLableName("volume");
      fBoardGraph.setYLableName("price");
    }
    Iterator iter = array.iterator();
    double tmpPrice, tmpVolume, currentSellVolume = 0,
        currentBuyVolume = totalBuyVolume;
    while (iter.hasNext()) {
      HashMap os = (HashMap) iter.next();
      String s = (String) os.get(UCBoardDataCore.STRING_SELL_BUY);
      if (s.equals("sell")) {
        tmpPrice = ( (Long) os.get(UCBoardDataCore.LONG_PRICE))
            .doubleValue();
        tmpVolume = ( (Long) os.get(UCBoardDataCore.LONG_VOLUME))
            .doubleValue();
        if (fIsExchangeXY == false) {
          tmpSellData.add(new Point2D.Double(tmpPrice,
                                             currentSellVolume));
          tmpSellData.add(new Point2D.Double(tmpPrice,
                                             currentSellVolume + tmpVolume));
        } else {
          tmpSellData.add(new Point2D.Double(currentSellVolume,
                                             tmpPrice));
          tmpSellData.add(new Point2D.Double(currentSellVolume
                                             + tmpVolume, tmpPrice));
        }
        currentSellVolume += tmpVolume;
      } else if (s.equals("buy")) {
        tmpPrice = ( (Long) os.get(UCBoardDataCore.LONG_PRICE))
            .doubleValue();
        tmpVolume = ( (Long) os.get(UCBoardDataCore.LONG_VOLUME))
            .doubleValue();
        if (fIsExchangeXY == false) {
          tmpBuyData.add(new Point2D.Double(tmpPrice,
                                            currentBuyVolume));
          tmpBuyData.add(new Point2D.Double(tmpPrice,
                                            currentBuyVolume - tmpVolume));
        } else {
          tmpBuyData.add(new Point2D.Double(currentBuyVolume,
                                            tmpPrice));
          tmpBuyData.add(new Point2D.Double(currentBuyVolume
                                            - tmpVolume, tmpPrice));
        }
        currentBuyVolume -= tmpVolume;
      }
    }
    ( (UGraphData) fBoardGraph.getGraph().get(0)).setData(tmpSellData);
    ( (UGraphData) fBoardGraph.getGraph().get(1)).setData(tmpBuyData);
  }

  public static void main(String[] args) {
  }
} //  @jve:decl-index=0:visual-constraint="10,10"
