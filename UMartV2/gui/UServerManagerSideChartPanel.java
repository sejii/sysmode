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

import cmdCore.*;

public class UServerManagerSideChartPanel extends UPanel {
  protected UGraph fUGraph = new UGraph();
  protected double fInitMaxY = 3000;
  protected double fInitMinY = 0;
  protected double fInitInterval = 20;
  protected String fSpotLineName = fRb.getString("CURRENT_SPOT_PRICE");
  protected String fFutureLineName = fRb.getString("CURRENT_U-MART_PRICE");
  protected Color fSpotLineColor = Color.blue;
  protected Color fFutureLineColor = Color.red;
  protected int fCurrentStep = 0;
  protected ArrayList fSpotPriceData = new ArrayList();
  protected ArrayList fFuturePriceData = new ArrayList();
  BorderLayout borderLayout1 = new BorderLayout();

  public UServerManagerSideChartPanel() {
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * interface IGUIObserver を持つものは UGUIUpdateManager の
   * addDayObserver か addStepObserver を呼ぶ必要あり．すべてのUpanelで必要．
   */
  public void addUpdateObserverList() {
    UGUIUpdateManager.addStepObserver(this);
  }

  public void dataUpdate() {
    //日付関係はUGUIUpdateManager経由で取得するように変更．
//            UCServerDateCore cServerDate =
//                (UCServerDateCore) fCProtocol.getCommand(UCServerDateCore.CMD_NAME);
//            cServerDate.doIt();
//            HashMap result = cServerDate.getResults();
//            int date = ( (Integer) result.get(UCServerDateCore.INT_DAY)).intValue();
//            int boardNo = ( (Integer) result.get(UCServerDateCore.INT_BOARD_NO)).
//                intValue();
//            int step = (date - 1) * fParam.getBoardPerDay() + boardNo;
    int step = UGUIUpdateManager.getStep();

    if (step <= fCurrentStep) {
      return;
    }
    Iterator iter;
    UCSpotPriceCore cSpotPrice = (UCSpotPriceCore) fCProtocol.getCommand(
        UCSpotPriceCore.CMD_NAME);
    // step-fCurrentStep 分だけSpotPriceデータを要求
    cSpotPrice.setArguments(
        new StringTokenizer(
        "j30" + " " + String.valueOf(step - fCurrentStep)));
    cSpotPrice.doIt();
    iter = cSpotPrice.getResults().iterator();
    while (iter.hasNext()) {
      // StringTokenizer st = new StringTokenizer((String) iter.next());
      // st.nextToken();
      // st.nextToken();
      // double d = Double.parseDouble(st.nextToken());
      HashMap elem = (HashMap) iter.next();
      double d = (double) ( (Long) elem.get(UCSpotPriceCore.LONG_PRICE)).
          longValue();
      //注意！！！！変更必要．現在は -1 に特殊な意味あり．
      if (d > 0.0) {
        fSpotPriceData.add(new Double(d));
      } else {
        fSpotPriceData.add(new Double(Double.NaN));
      }
    }
    ( (UGraphData) fUGraph.getGraph().get(0)).setData(fSpotPriceData);
    UCFuturePriceCore cFuturePrice = (UCFuturePriceCore) fCProtocol.
        getCommand("FuturePrice");
    // step-fCurrentStep 分だけ FuturePriceデータを要求
    cFuturePrice.setArguments(new StringTokenizer("j30" + " " +
                                                  String.valueOf(step -
        fCurrentStep)));
    cFuturePrice.doIt();
    iter = cFuturePrice.getResults().iterator();
    while (iter.hasNext()) {
//			StringTokenizer st = new StringTokenizer((String) iter.next());
//			st.nextToken();
//			st.nextToken();
//			double d = Double.parseDouble(st.nextToken());
      HashMap elem = (HashMap) iter.next();
      double d = ( (Long) elem.get(UCFuturePriceCore.LONG_PRICE)).doubleValue();
      //注意！！！！変更必要．無効の場合の定義が未定．
      if (d > 0) {
        fFuturePriceData.add(new Double(d));
      } else {
        fFuturePriceData.add(new Double(Double.NaN));
      }
    }
    ( (UGraphData) fUGraph.getGraph().get(1)).setData(fFuturePriceData);
    fCurrentStep = step; // 更新は最後じゃないとグラフがアップデートされない
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    fUGraph.setLayout(null);
    fUGraph.getGraph().add(new UGraphData(fSpotLineName, fSpotLineColor));
    fUGraph.getGraph().add(new UGraphData(fFutureLineName, fFutureLineColor));
    fUGraph.setBackground(Color.white);
    fUGraph.setFixedMaxX(fParam.getDays() * fParam.getBoardPerDay());
    fUGraph.setYAuto(true);
    fUGraph.setNumOfVerticalLine(5);
    fUGraph.setNumOfHorizontalLine(5);
    this.add(fUGraph, BorderLayout.CENTER);
  }
}
