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

import cmdCore.*;

/**
 * チャート表示タブ．
 */
public class UChartPanel extends UPanel {

  protected String fSpotLineName = fRb.getString("CURRENT_SPOT_PRICE");
  protected String fFutureLineName = fRb.getString("CURRENT_U-MART_PRICE");
  protected Color fSpotLineColor = Color.blue;
  protected Color fFutureLineColor = Color.red;
  protected int fCurrentStep = 0;
  protected ArrayList fSpotPriceData = new ArrayList();
  protected ArrayList fFuturePriceData = new ArrayList();
  protected UGraph fGraph = new UGraph();
  protected UGraphYrangePanel fGraphYrangePanel = new UGraphYrangePanel();
  protected UGraphXrangePanel fGraphXrangePanel = new UGraphXrangePanel();
  protected USeperateWindowButton fSeperateWindowButton = new
      USeperateWindowButton();
  protected ArrayList fSpotPriceDataLog = new ArrayList();
  protected ArrayList fFuturePriceDataLog = new ArrayList();

  public UChartPanel() {
    fName = fRb.getString("CHART");
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    /*
         fName = fRb.getString("CHART");
         UGraph graph = new UGraph();
         graph.setLayout(null);
         graph.getGraph().add(new UGraphData(fSpotLineName, fSpotLineColor));
     graph.getGraph().add(new UGraphData(fFutureLineName, fFutureLineColor));
         graph.setBackground(Color.white);
         graph.setBounds(new Rectangle(10, 15, 520, 253));
         graph.setFixedMaxX(fParam.getDays() * fParam.getBoardPerDay());
         setUGraph(graph);
         try {
      jbInit();
         } catch (Exception ex) {
      ex.printStackTrace();
         }
     */
  }

  private void jbInit() throws Exception {
    // UGraphの設定
    fGraph.getGraph().add(new UGraphData(fSpotLineName, fSpotLineColor));
    fGraph.getGraph().add(new UGraphData(fFutureLineName, fFutureLineColor));
    fGraph.setBackground(Color.white);
    fGraph.setBounds(new Rectangle(10, 15, 520, 253));
    fGraph.setFixedMaxX(fParam.getDays() * fParam.getBoardPerDay());
    this.add(fGraph, null);
    // UGraphYrangePanelの設定
    fGraphYrangePanel.setGraph(fGraph);
    fGraphYrangePanel.setBounds(new Rectangle(537, 11, 185, 134));
    this.add(fGraphYrangePanel, null);
    // UGraphXrangePanelの設定
    fGraphXrangePanel.setGraph(fGraph);
    fGraphXrangePanel.setBounds(new Rectangle(537, 151, 185, 79));
    this.add(fGraphXrangePanel, null);
    // USeperateWindowButtonの設定
    fSeperateWindowButton.setGraph(fGraph);
    fSeperateWindowButton.setBounds(new Rectangle(544, 236, 171, 30));
    fSeperateWindowButton.setSubWindowTitle(fName);
    this.add(fSeperateWindowButton, null);
    addComponentListener(new ComponentAdapter() {
      public void componentShown(ComponentEvent ce) {
        gUpdate();
      }
    });
  }

  /**
   * interface IGUIObserver を持つものは UGUIUpdateManager の
   * addDayObserver か addStepObserver を呼ぶ必要あり．すべてのUpanelで必要．
   */
  public void addUpdateObserverList() {
    UGUIUpdateManager.addStepObserver(this);
  }

  public void dataUpdate() {
    if (!isVisible() && !fSeperateWindowButton.isSubWindowAvailable()) {
      return;
    }
    int step = UGUIUpdateManager.getStep();
    if (step <= fCurrentStep) {
      return;
    }
    UCSpotPriceCore cSpotPrice = (UCSpotPriceCore) fCProtocol.getCommand(
        UCSpotPriceCore.CMD_NAME);
    // step-fCurrentStep 分だけSpotPriceデータを要求
    cSpotPrice.setArguments(new StringTokenizer("j30" + " " +
                                                String.valueOf(step -
        fCurrentStep)));
    cSpotPrice.doIt();
    ArrayList spotSeries = cSpotPrice.getResults();
    for (int i = spotSeries.size() - 1; i >= 0; --i) {
      HashMap elem = (HashMap) spotSeries.get(i);
      fSpotPriceDataLog.add(elem);
      double d = (double) ( (Long) elem.get(UCSpotPriceCore.LONG_PRICE)).
          longValue();
      //注意！！！！変更必要．現在は -1 に特殊な意味あり．
      if (d > 0.0) {
        fSpotPriceData.add(new Double(d));
      } else {
        fSpotPriceData.add(new Double(Double.NaN));
      }
    }
    ( (UGraphData) fGraph.getGraph().get(0)).setData(fSpotPriceData);
    UCFuturePriceCore cFuturePrice = (UCFuturePriceCore) fCProtocol.getCommand(
        "FuturePrice");
    // step-fCurrentStep 分だけ FuturePriceデータを要求
    cFuturePrice.setArguments(new StringTokenizer("j30" + " " +
                                                  String.valueOf(step -
        fCurrentStep)));
    cFuturePrice.doIt();
    ArrayList futuresSeries = cFuturePrice.getResults();
    for (int i = futuresSeries.size() - 1; i >= 0; --i) {
      HashMap elem = (HashMap) futuresSeries.get(i);
      fFuturePriceDataLog.add(elem);
      double d = ( (Long) elem.get(UCFuturePriceCore.LONG_PRICE)).doubleValue();
      //注意！！！！変更必要．無効の場合の定義が未定．
      if (d > 0) {
        fFuturePriceData.add(new Double(d));
      } else {
        fFuturePriceData.add(new Double(Double.NaN));
      }
    }
    ( (UGraphData) fGraph.getGraph().get(1)).setData(fFuturePriceData);
    fCurrentStep = step; // 更新は最後じゃないとグラフがアップデートされない
  }

  public void writeLog(String basename) throws IOException {
    int noOfSteps = fParam.getDays() * fParam.getBoardPerDay() + 2;
    PrintWriter pw = new PrintWriter(new FileWriter(basename + "_price.csv"));
    pw.println("step,date,session,spot,future");
    UCFuturePriceCore cFuturePrice = (UCFuturePriceCore) fCProtocol.getCommand(
        UCFuturePriceCore.CMD_NAME);
    cFuturePrice.setArguments("j30", noOfSteps);
    cFuturePrice.doIt();
    UCSpotPriceCore cSpotPrice = (UCSpotPriceCore) fCProtocol.getCommand(
        UCSpotPriceCore.CMD_NAME);
    cSpotPrice.setArguments("j30", noOfSteps);
    cSpotPrice.doIt();
    for (int i = noOfSteps - 1; i >= 0; --i) {
      HashMap hmFuture = (HashMap) cFuturePrice.getResults().get(i);
      HashMap hmSpot = (HashMap) cSpotPrice.getResults().get(i);
      int stepFuture = ( (Integer) hmFuture.get(UCSpotPriceCore.INT_STEP)).
          intValue();
      int stepSpot = ( (Integer) hmSpot.get(UCSpotPriceCore.INT_STEP)).intValue();
      if (stepFuture != stepSpot) {
        System.err.println("stepFuture != stepSpot in UChartPanel.writeLog");
      }
      int dateFuture = ( (Integer) hmFuture.get(UCSpotPriceCore.INT_DAY)).
          intValue();
      int dateSpot = ( (Integer) hmSpot.get(UCSpotPriceCore.INT_DAY)).intValue();
      if (dateFuture != dateSpot) {
        System.err.println("dateFuture != dateSpot in UChartPanel.writeLog");
      }
      int boardFuture = ( (Integer) hmFuture.get(UCSpotPriceCore.INT_BOARD_NO)).
          intValue();
      int boardSpot = ( (Integer) hmSpot.get(UCSpotPriceCore.INT_BOARD_NO)).
          intValue();
      if (boardFuture != boardSpot) {
        System.err.println("boardFuture != boardSpot in UChartPanel.writeLog");
      }
      long priceFuture = ( (Long) hmFuture.get(UCSpotPriceCore.LONG_PRICE)).
          intValue();
      long priceSpot = ( (Long) hmSpot.get(UCSpotPriceCore.LONG_PRICE)).
          intValue();
      pw.println(stepSpot + "," + dateSpot + "," + boardSpot + "," + priceSpot +
                 "," + priceFuture);
    }
    pw.close();
  }

  public void repaint() {
    if (super.isVisible()) {
      super.repaint();
    }
    if (fSeperateWindowButton != null) {
      fSeperateWindowButton.repaintSubWindow();
    }
  }

}
