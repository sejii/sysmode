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

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

public class UServerSystemOutPanel extends UPanel implements IOutputObserver {

  protected JScrollPane fScrollPane = new JScrollPane();
  protected JTextArea fSystemOutTextArea = new JTextArea();

  public UServerSystemOutPanel() {
    System.setOut(UParameters.fPrintStream);
    fName = fRb.getString("STDOUT");
    fSystemOutTextArea.setRows(30000);
    UParameters.fByteArrayOutputStream.setOutputObserver(this);
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    fScrollPane.setBounds(new Rectangle(7, 6, 677, 500));
    fSystemOutTextArea.setEditable(false);
    this.add(fScrollPane, null);
    fScrollPane.getViewport().add(fSystemOutTextArea, null);
  }

  /**
   * これは，IOutputObserver の効果によりflash時に更新されるのでUpdateManager による更新は不要．
   */
  public void addUpdateObserverList() {};

  public void update(UByteArrayOutputStream bos) {
    String s = bos.toString();
    System.err.print(">>" + s);
    fSystemOutTextArea.append(s);
    Document doc = fSystemOutTextArea.getDocument();
    Position last = doc.getEndPosition(); // ドキュメントの末尾を表す位置を取得
    int pos = last.getOffset();
    fSystemOutTextArea.getCaret().setDot(pos); // Caretをドキュメントの末尾に設定
  }

  public static void main(String[] args) {
    USystemOutPanel USystemOutPanel1 = new USystemOutPanel();
  }

}