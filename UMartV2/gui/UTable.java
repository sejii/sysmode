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
import javax.swing.table.*;

/**
 * ソート可能なテーブルクラス. 
 * Swing が Thread safe でないため Exception を吐くことあり．
 */
public class UTable extends JTable {

  protected USortDecorator fDecorator;

  //  protected boolean fIsAscending = true;
  //各列でデフォルトの昇順降順を決められるようにする。
  protected boolean[] fDefaultAscending;

  protected boolean[] fCurrentAscending;

  //各列がソート可能かどうか
  protected boolean[] fIsSortable;

  protected int fSortedColumn = -1;

  protected JTableHeader fTableHeader;

  protected MouseListener fSortMouseListener;

  //Table Header のマウスリスナー一時退避用
  protected MouseListener[] fMouseListenerArrayOfHeader;

  //クリック時の時間．
  protected long fClickTime = 0;

  //  クリック後, fClickDeltaTime 間は Mouse Event を受け付けない．
  protected long fClickDeltaTime = 500; //ミリ秒

  //setModelの時間．
  protected long fSetModelTime = 0;

  //  setModel 後, fSetModelDeltaTime 間は Table の更新をしない．
  //極端に早い更新は必ず Exception を吐くため．
  protected long fSetModelDeltaTime = 500; //ミリ秒

  /**
   * クリックによるソートを実現する MouseListener 昔は無名クラスを使っていたがいろいろ変更の必要があって内部クラス化。
   * でも、今はその必要がなくなったがそのままにしておく。
   */
  protected class UMouseListener extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      //クリックされた時間を記録
      long currentTime = System.currentTimeMillis();
      //クリック直後は再ソートしない。
      if (currentTime - fClickTime < fClickDeltaTime) {
        fClickTime = currentTime;
        return;
      }
      fClickTime = currentTime;
      //現在ソートを解消する方法がないのでCtrl+Click でソートを解消。
      int ctrlPressed = e.getModifiers() & InputEvent.CTRL_MASK;
      if (ctrlPressed != 0) {
        fSortedColumn = -1;
        return;
      }
      if (getRowCount() < 2) { //0,1 はソートの必要なし
        return;
      }
      TableColumnModel tcm = getColumnModel();
      int columnNum = tcm.getColumnIndexAtX(e.getX());
      //Sortable でない列は終了。
      if (!fIsSortable[columnNum]) {
        return;
      }
      //JTableHeader は default で2つの MouseListener を持っているので Click Event を
      //無視するために全部削除．連続クリック対策．
      //removeAllMouseListenerFromHeader();
      int mc = convertColumnIndexToModel(columnNum);
      int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
      if (shiftPressed == 0) {
        fCurrentAscending[columnNum] = fDefaultAscending[columnNum];
      } else {
        fCurrentAscending[columnNum] = !fDefaultAscending[columnNum];
      }
      fDecorator.sort(mc, fCurrentAscending[columnNum]);
      fSortedColumn = mc;
      //上で削除した MouseListener を戻す．fClickDeltaTime 経たないとだめ．
      //TODO MouseEvent を受けないようにするいい方法ないの！？
      //returnAllMouseListenerToHeader();
    }
  };

  public UTable() {
    super();
  }

  public UTable(Object[][] data, Object[] header) {
    super(data, header);
    fDefaultAscending = new boolean[getColumnCount()];
    fCurrentAscending = new boolean[getColumnCount()];
    fIsSortable = new boolean[getColumnCount()];
    Arrays.fill(fDefaultAscending, true);
    Arrays.fill(fCurrentAscending, true);
    Arrays.fill(fIsSortable, true);
    fTableHeader = getTableHeader();
    //      setModel(getModel());
    addMouseListenerToHeader();
  }

  public UTable(Vector data, Vector header) {
    super(data, header);
    fDefaultAscending = new boolean[getColumnCount()];
    fCurrentAscending = new boolean[getColumnCount()];
    fIsSortable = new boolean[getColumnCount()];
    Arrays.fill(fDefaultAscending, true);
    Arrays.fill(fCurrentAscending, true);
    Arrays.fill(fIsSortable, true);
    fTableHeader = getTableHeader();
    //      setModel(getModel());
    addMouseListenerToHeader();
  }

  /*
   * TODO なぜかはわからないが，constracta よりも先に setModel が一回呼ばれている． super(data,header)
   * で呼ばれている？ 何でだろう??
   */
  public void setModel(TableModel dataModel) {
    long currentTime = System.currentTimeMillis();
    //setModel直後は再setModelしない．
    if (currentTime - fSetModelTime < fSetModelDeltaTime) {
      return;
    }
    fSetModelTime = currentTime;
    // ドラッグ中は何もせずに終了。
    if (fTableHeader != null && fTableHeader.getDraggedDistance() != 0) {
      return;
    }

    //変更後も列の変化を保存するため現在の配列を覚えておいて，モデルをセットしたあと
    //並び替えている．エレガントさに欠けるがこれ以外どうすればいいか分からなかった．
    //これで2003年のゴールデンウィークがつぶれた...
    int column = getColumnCount();
    int[] order = new int[column];
    for (int i = 0; i < column; i++) {
      order[i] = convertColumnIndexToModel(i);
    }
    fDecorator = new USortDecorator(dataModel);
    //過去のソート情報を受け継ぐ．!= null の処理はよろしくないなぁ．
    //if (fSortedColumn >= 0 && fCurrentAscending != null) {
    if (fSortedColumn >= 0 && fCurrentAscending != null) {
      fDecorator.sort(fSortedColumn, fCurrentAscending[fSortedColumn]);
    }
    super.setModel(fDecorator);
    //過去の列情報を新しいテーブルに反映．
    for (int i = 0; i < column; i++) {
      super.moveColumn(convertColumnIndexToView(order[i]), i);
    }
  }

  public Class getColumnClass(int column) {
    Object target = null;
    //テーブルにはnullが多いので、初めてnull以外がでるまで調べる。
    //ただし、複数のオブジェクトがある場合は想定しない。
    for (int i = 0; i < fDecorator.getRowCount(); i++) {
      if (fDecorator.getValueAt(i, column) != null) {
        target = fDecorator.getValueAt(i, column);
        break;
      }
    }
    if (target == null) {
      return java.lang.Object.class;
    }
    Class c = target.getClass();
    if (c == java.lang.String.class) {
      return java.lang.Number.class; // String も右寄せ
    } else if (c == java.lang.Number.class) {
      return java.lang.Number.class;
    } else if (c == gui.UFormatLong.class) {
      return java.lang.Number.class;
    } else if (c == gui.UFormatDouble.class) {
      return java.lang.Number.class;
    } else if (c == UTwoPartsElement.class) {
      return java.lang.Number.class;
    } else {
      return c;
    }
  }

  public boolean isCellEditable(int r, int c) {
    return false;
  }

  protected void addMouseListenerToHeader() {
    if (fTableHeader == null) {
      return;
    }
    fSortMouseListener = new UMouseListener();
    fTableHeader.addMouseListener(fSortMouseListener);
  }

  /**
   * 列i のデフォルト昇順降順を得る。
   *
   * @param i
   * @return true: 昇順 false: 降順
   */
  protected boolean getDefaultAscendingAt(int i) {
    return fDefaultAscending[i];
  }

  /**
   * 列のデフォルト昇順降順をセット
   *
   * @param b
   *            列全部の情報を記した配列 true: 昇順 false: 降順
   */
  protected void setDefaultAscending(boolean ba[]) {
    fDefaultAscending = ba;
  }

  /**
   * 列の i のデフォルト昇順降順をセット
   *
   * @param i
   *            セットする列
   * @param b
   *            昇順降順内容 true: 昇順 false: 降順
   */
  protected void setDefaultAscendingAt(int i, boolean b) {
    fDefaultAscending[i] = b;
  }

  /**
   * 列 i がソート可能かどうか
   *
   * @param i
   *            列
   * @return
   */
  protected boolean getSortableAt(int i) {
    return fIsSortable[i];
  }

  /**
   * 列 i がソート可能かどうかセットする。
   *
   * @param i
   *            列
   * @param b
   *            true: ソート可 false: ソート不可
   * @return
   */
  protected void setSortableAt(int i, boolean b) {
    fIsSortable[i] = b;
  }

  public void removeAllMouseListenerFromHeader() {
    if (fTableHeader == null) {
      return;
    }
    //MouseListener 一時退避．
    fMouseListenerArrayOfHeader = fTableHeader.getMouseListeners();
    for (int i = 0; i < fMouseListenerArrayOfHeader.length; i++) {
      fTableHeader.removeMouseListener(fMouseListenerArrayOfHeader[i]);
    }
  }

  public void returnAllMouseListenerToHeader() {
    if (fTableHeader == null) {
      return;
    }
    for (int i = 0; i < fMouseListenerArrayOfHeader.length; i++) {
      fTableHeader.addMouseListener(fMouseListenerArrayOfHeader[i]);
    }
  }

  public static void main(String args[]) {
    int[][] priceData = { {1960, 20, 15}
        , {2000, 22, 21}
        , {2004, 21, 11}
        , {2010, 12, 1}
        , {2002, 32, 4}
        , {2050, -2, 7}
        , {1, 2, 3}
    }; // 6x3
    String[] header = {"Price", "Sell", "Buy", "Name"};
    Object[][] data = new Object[priceData.length][priceData[0].length + 1];
    Object[] toph = new Object[4];
    int i, j;
    for (i = 0; i < priceData.length; i++) {
      for (j = 0; j < priceData[0].length; j++) {
        data[i][j] = new Integer(priceData[i][j]);
      }
    }
    data[0][0] = new JButton();
    for (i = 0; i < priceData.length; i++) {
      Integer itmp = new Integer(i * 2 + 1);
      data[i][3] = "member" + itmp.toString();
    }
    for (j = 0; j < header.length; j++) {
      toph[j] = header[j];
    }
    UTable t = new UTable(data, toph);
    JFrame f = new JFrame();
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    f.getContentPane().add(new JScrollPane(t));
    f.setBounds(new Rectangle(300, 300, 450, 300));
    f.setVisible(true);
  }

}