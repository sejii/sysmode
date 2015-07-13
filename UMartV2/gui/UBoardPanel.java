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
 * 板情報表示用タブ．左が現在の板，右が一つ前の板なので注意が必要．
 */
public class UBoardPanel extends UPanel {
  //左テーブル部分
  UBoardTablePanel fBoardTablePanel = new UBoardTablePanel();
  // 右グラフ部分
  UBoardGraphPanel fBoardGraphPanel = new UBoardGraphPanel();

  public UBoardPanel() {
    fName = fRb.getString("ORDER_BOOK");
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    fBoardGraphPanel.setBounds(335, 6, 397, 283);
    fBoardTablePanel.setBounds(28, 6, 288, 283);
    this.add(fBoardTablePanel, null);
    this.add(fBoardGraphPanel, null);
  }
}