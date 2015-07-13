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

/**
 * UPanel の Factory．新しい UPanel を作ったらこのクラスと UTabSettingDialog を変更する．
 * UTopPanel, UOrderButtonPanel, USubPanel はタブではないため特例でこれで生成されない．
 */
public class UPanelFactory {

  public static final String CHART = "CHART";
  public static final String PROFIT = "PROFIT";
  public static final String ORDER = "ORDER";
  public static final String ORDER_RESULT = "ORDER_RESULT";
  public static final String BOARD = "BOARD";
  public static final String POSITION = "POSITION";
  public static final String STDOUT = "STDOUT";

// for Server
  public static final String SERVER_ALL_AGENTS = "SERVER_ALL_AGENTS";

  public static final String[] PANEL_TYPES = {CHART,
      BOARD,
      ORDER,
      ORDER_RESULT,
      PROFIT,
      POSITION,
      STDOUT};

  private UPanelFactory() {
  }

  public static UPanel createUpanel(String s) {
    if (s == CHART) {
      return new UChartPanel();
    } else if (s == PROFIT) {
      return new UProfitPanel();
    } else if (s == ORDER) {
      return new UOrderPanel();
    } else if (s == ORDER_RESULT) {
      return new UOrderResultPanel();
    } else if (s == BOARD) {
      return new UBoardPanel();
    } else if (s == POSITION) {
      return new UPositionPanel();
    } else if (s == STDOUT) {
      return new UMessagePanel();
    }
    //return new UPanel();
    //TODO ちょっと怪しい．
    return (UPanel)null;
  }

  public static void main(String[] args) {
    UPanelFactory UPanelFactory1 = new UPanelFactory();
  }
}
