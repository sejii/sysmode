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

import java.text.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * UGraphData の内容をPanelに描画する簡単な汎用グラフクラス．
 */
public class UGraph extends JPanel {

  static protected final double DIGIT = 10;

  /** 上部マージン */
  protected double fTopMargin;

  /** 下部マージン */
  protected double fBottomMargin;

  /** 右部マージン */
  protected double fRightMargin;

  /** 左部マージン */
  protected double fLeftMargin;

  /** グラフの高さ */
  protected double fHeight;

  /** グラフの幅 */
  protected double fWidth;

  /** グラフの横数 */
  protected int fNumOfHorizontalLine;

  /** グラフの縦線の数 */
  protected int fNumOfVerticalLine;
  protected double fMaxX;
  protected double fFixedMaxX;
  protected double fMinX;
  protected double fFixedMinX;
  protected double fMaxY;
  protected double fFixedMaxY;
  protected double fMinY;
  protected double fFixedMinY;
  protected boolean fIsXAuto;
  protected boolean fIsYAuto;
  protected int fNumOfPlotData;
  protected int fNumOfNowData;

  /** UGraphData を保存*/
  protected ArrayList fGraph = new ArrayList();

  //  Buffered Image
  protected BufferedImage f2img;
  protected Graphics2D f2ig;
  protected double fXMarginFactor = 0.1;

  //  protected double YFACTOR = 1.0;
  protected FontMetrics fFm = getFontMetrics(getFont());

  protected String fXLableName = null;
  protected String fYLableName = null;

  public UGraph() {
    fGraph = new ArrayList();
    fIsXAuto = false;
    fIsYAuto = false;
    fFixedMaxX = 240;
    fFixedMinX = 0;
    fFixedMaxY = 3000;
    fFixedMinY = 0;
    fNumOfPlotData = 20;
    fTopMargin = 15;
    fRightMargin = 25;
    fBottomMargin = 50;
    fLeftMargin = 90;
    fNumOfVerticalLine = 11;
    fNumOfHorizontalLine = 9;
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  protected void jbInit() throws Exception {
    setBackground(Color.white);
  }

  public UGraph(double maxx, double minx, double maxy, double miny, int vlnum,
                int hlnum) {
    fIsXAuto = false;
    fIsYAuto = false;
    fFixedMaxX = maxx;
    fFixedMinX = minx;
    fFixedMaxY = maxy;
    fFixedMinY = miny;
    fNumOfVerticalLine = vlnum;
    fNumOfHorizontalLine = hlnum;
    fNumOfPlotData = 20;
    fTopMargin = fRightMargin = 25;
    fBottomMargin = 40;
    fLeftMargin = 90;
  }

  /** ユーザのデータを画面上の座標に変換 */
  protected Point2D[] transPoint(Point2D point[]) {
    double maxx, minx, maxy, miny;
    if (fIsXAuto) {
      maxx = fMaxX + fNumOfPlotData * fXMarginFactor;
      minx = fMinX;
    } else {
      maxx = fFixedMaxX;
      minx = fFixedMinX;
    }
    if (fIsYAuto) {
      if (fMaxY == fMinY) {
        if (fMaxY < 10) {
          maxy = fMinY + 10;
          miny = fMinY;
        } else {
          maxy = fMaxY * 1.1;
          miny = fMaxY * 0.9;
        }
      } else {
        maxy = fMaxY;
        miny = fMinY;
      }
    } else {
      maxy = fFixedMaxY;
      miny = fFixedMinY;
    }
    double xrange, yrange;
    Point2D[] tPoint = new Point2D[point.length];
    xrange = maxx - minx;
    yrange = maxy - miny;
    if (xrange == 0) {
      xrange = 1;
    }
    if (yrange == 0) {
      yrange = 1;

    }
    double xfact;
    if (fIsXAuto && fNumOfNowData <= fNumOfPlotData) {
      xfact = fWidth / ( (1 + fXMarginFactor) * fNumOfPlotData);
    } else {
      xfact = fWidth / xrange;
    }
    double yfact = (fHeight) / yrange;

    for (int i = 0; i < point.length; i++) {
      // 座標変換
      double newX = (point[i].getX() - minx) * xfact;
      double newY = fHeight - (point[i].getY() - miny) * yfact;
      tPoint[i] = new Point2D.Double(newX, newY);
    }
    return tPoint;
  }

  /**
     UGraphData からグラフを描画
     @param g2d 描画用Graphics2D
     @param point グラフデータ
     @param c 色
   */
  protected void drawGraph(Graphics2D g2d, Point2D[] point, Color c) {
    //データが一つ以下の場合は終わり
    if (point.length < 2) {
      return;
    }
    // 色設定
    g2d.setColor(c);
    int start = 0;
    if (fIsXAuto) {
      if (point.length > fNumOfPlotData) {
        start = point.length - fNumOfPlotData;
      } else {
        start = 0;
      }
    }
    for (int i = start; i < point.length - 1; i++) {
      if (Double.isNaN(point[i].getY()) || Double.isNaN(point[i + 1].getY())) {
        continue;
      }
      Line2D biLine = new Line2D.Double(point[i], point[i + 1]);
      g2d.draw(biLine);
    }
  }

  protected void drawBaseLine(Graphics2D g2d) {
    Rectangle2D.Double rect = new Rectangle2D.Double(fLeftMargin, fTopMargin,
        fWidth, fHeight);
    g2d.draw(rect);
    Line2D biLine = new Line2D.Double();
    double dx = fWidth / (fNumOfVerticalLine + 1);
    double dy = fHeight / (fNumOfHorizontalLine + 1);
    double maxx, minx, maxy, miny;
    if (fIsXAuto) {
      maxx = fMaxX + fNumOfPlotData * fXMarginFactor;
      minx = fMinX;
    } else {
      maxx = fFixedMaxX;
      minx = fFixedMinX;
    }
    if (fIsYAuto) {
      maxy = fMaxY;
      miny = fMinY;
    } else {
      maxy = fFixedMaxY;
      miny = fFixedMinY;
    }
    double xrange = maxx - minx;
    double yrange = maxy - miny;
    if (xrange == 0) {
      xrange = 1;
    }
    if (yrange == 0) {
      yrange = 1;

    }
    double xfact;
    if (fIsXAuto && fNumOfNowData <= fNumOfPlotData) {
      xfact = fWidth / ( (1 + fXMarginFactor) * fNumOfPlotData);
    } else {
      xfact = fWidth / xrange;
    }
    double yfact = fHeight / yrange;
    //	System.out.println("w:"+fWidth+" h:"+fHeight+" fact:"+xfact+" range:"+xrange);
    //垂直線
    for (int i = 0; i < fNumOfVerticalLine + 2; i++) {
      biLine.setLine(fLeftMargin + i * dx, fTopMargin, fLeftMargin + i * dx,
                     fTopMargin + fHeight);
      g2d.setColor(Color.lightGray);
      g2d.draw(biLine);
      Long xNum = new Long( (long) (minx + (dx * i) / xfact));
      String xs;
      double dxNum = Math.abs(xNum.doubleValue());
      // DIGIT桁以上，小数点以下DIGIT桁以下で表記が指数に変わる．現在はDIGIT=10
      if ( (Math.pow(10, -DIGIT) < dxNum && dxNum < Math.pow(10, DIGIT)) ||
          (dxNum == 0)) {
        xs = NumberFormat.getNumberInstance().format(xNum);
      } else {
        xs = new DecimalFormat("0.00E0").format(xNum);
      }
      g2d.setColor(Color.black);
      g2d.drawString(xs,
                     (long) (fLeftMargin + i * dx) - fFm.stringWidth(xs) / 2,
                     (long) (fTopMargin + fHeight + fFm.getHeight()));
    }
    //水平線
    for (int i = 0; i < fNumOfHorizontalLine + 2; i++) {
      biLine.setLine(fLeftMargin, fTopMargin + i * dy, fLeftMargin + fWidth,
                     fTopMargin + i * dy);
      g2d.setColor(Color.lightGray);
      g2d.draw(biLine);
      Long yNum = new Long( (long) (miny + (dy * i) / yfact));
      String ys;
      double dyNum = Math.abs(yNum.doubleValue());
      if ( (Math.pow(10, -DIGIT) < dyNum && dyNum < Math.pow(10, DIGIT)) ||
          (dyNum == 0)) {
        ys = NumberFormat.getNumberInstance().format(yNum);
      } else {
        ys = new DecimalFormat("0.00E0").format(yNum);
      }
      g2d.setColor(Color.black);
      g2d.drawString(ys, (long) (fLeftMargin - fFm.stringWidth(ys) - 5),
                     (long) (fHeight + fTopMargin - i * dy +
                             fFm.getHeight() / 3));
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Dimension s = getSize();
    fWidth = s.width - fRightMargin - fLeftMargin;
    fHeight = s.height - fTopMargin - fBottomMargin;
    Graphics2D g2d = (Graphics2D) g;
    //        f2img = (BufferedImage) createImage((int)fWidth, (int)fHeight);
    f2img = new BufferedImage( (int) fWidth, (int) fHeight,
                              BufferedImage.TYPE_4BYTE_ABGR);
    f2ig = f2img.createGraphics();
    //        g.drawImage(f2img,0,0,this);
    Iterator iter = fGraph.iterator();
    // 現在のデータの長さを求める．
    fNumOfNowData = Integer.MIN_VALUE;
    int step = 0;
    while (iter.hasNext()) {
      UGraphData next = (UGraphData) iter.next();
      if (next.getData().size() > fNumOfNowData) {
        fNumOfNowData = next.getData().size();
      }
      g2d.setColor(next.getColor());
      g2d.drawLine(step + 10, (int) s.height - 15,
                   step + 30, (int) s.height - 15);
      step += 25;
      g2d.setColor(Color.black);
      String gName = next.getDataName();
      //g2d.drawString(gName, (int) fLeftMargin + step + 10, (int) s.height - 10);
      g2d.drawString(gName, step + 10, (int) s.height - 10);
      step += fFm.stringWidth(gName) + 15;
    }
    //x軸，y軸のラベル設定
    if (fXLableName != null) {
      String xlabel = "X: " + getXLableName();
      g2d.drawString(xlabel, step + 10, (int) s.height - 10);
      step += fFm.stringWidth(xlabel) + 5;
    }

    if (fYLableName != null) {
      String ylabel = "Y: " + getYLableName();
      g2d.drawString(ylabel, step + 10, (int) s.height - 10);
    }

    // データの最大最小を求める
    fMaxX = fMaxY = Double.MIN_VALUE;
    fMinX = fMinY = Double.MAX_VALUE;
    iter = fGraph.iterator();
    if (fIsXAuto) {
      while (iter.hasNext()) {
        UGraphData next = (UGraphData) iter.next();
        if (next.maxX() > fMaxX) {
          fMaxX = next.maxX();
        }
        if (next.minX() < fMinX) {
          fMinX = next.minX();
        }
      }
      //	    System.out.println("maxX:"+fMaxX+" minX:"+fMinX);
      // 横幅を動的に決めるための処理
      if (fNumOfNowData > fNumOfPlotData) { //現在のデータ数が幅より大きい必要あり
        fMinX = fMaxX - ( (fMaxX - fMinX) / fNumOfNowData) * fNumOfPlotData;
      }
    }

    if (fIsYAuto) {
      iter = fGraph.iterator();
      while (iter.hasNext()) { // グラフの最大最小を決めるためのループ．
        UGraphData next = (UGraphData) iter.next();
        int len = next.getData().size();
        if (fIsXAuto && len > fNumOfPlotData) {
          if (next.maxY() > fMaxY) {
            fMaxY = next.maxY(len - fNumOfPlotData, len);
          }
          if (next.minY() < fMinY) {
            fMinY = next.minY(len - fNumOfPlotData, len);
          }
        } else {
          if (next.maxY() > fMaxY) {
            fMaxY = next.maxY();
          }
          if (next.minY() < fMinY) {
            fMinY = next.minY();
          }
        }
      }
    }

    drawBaseLine(g2d);
    iter = fGraph.iterator();
    while (iter.hasNext()) { // 各 UGraphData に対するグラフ描画のループ．
      Point2D[] point, tPoint;
      UGraphData next = (UGraphData) iter.next();
      point = next.pointArray();
      tPoint = transPoint(point);
      drawGraph(f2ig, tPoint, next.getColor());
    }
    g.drawImage(f2img, (int) fLeftMargin, (int) fTopMargin, this);
  }

  public ArrayList getGraph() {
    return fGraph;
  }

  // fGraph の shallow copy. v が変われば fGraph も変わる．
  public void setGraph(ArrayList v) {
    Iterator iter = v.iterator();
    fGraph = new ArrayList();
    while (iter.hasNext()) {
      UGraphData og = (UGraphData) iter.next();
      fGraph.add(og);
    }
  }

  public void setXAuto(boolean b) {
    fIsXAuto = b;
  }

  public void setYAuto(boolean b) {
    fIsYAuto = b;
  }

  public void setFixedMaxX(double d) {
    fFixedMaxX = d;
  }

  public double getFixedMaxX() {
    return fFixedMaxX;
  }

  public void setFixedMinX(double d) {
    fFixedMinX = d;
  }

  public double getFixedMinX() {
    return fFixedMinX;
  }

  public void setFixedMaxY(double d) {
    fFixedMaxY = d;
  }

  public double getFixedMaxY() {
    return fFixedMaxY;
  }

  public void setFixedMinY(double d) {
    fFixedMinY = d;
  }

  public double getFixedMinY() {
    return fFixedMinY;
  }

  public void setNumOfPlotData(int i) {
    fNumOfPlotData = i;
  }

  public Dimension getPreferredSize() {
    return new Dimension(330, 220);
  }

  public double getTopMargin() {
    return fTopMargin;
  }

  public void setTopMargin(double d) {
    fTopMargin = d;
  }

  public double getBottomMargin() {
    return fBottomMargin;
  }

  public void setBottomMargin(double d) {
    fBottomMargin = d;
  }

  public double getRightMargin() {
    return fRightMargin;
  }

  public void setRightMargin(double d) {
    fRightMargin = d;
  }

  public double getLeftMargin() {
    return fLeftMargin;
  }

  public void setLeftMargin(double d) {
    fLeftMargin = d;
  }

  public int getNumOfHorizontalLine() {
    return fNumOfHorizontalLine;
  }

  public void setNumOfHorizontalLine(int i) {
    fNumOfHorizontalLine = i;
  }

  public int getNumOfVerticalLine() {
    return fNumOfVerticalLine;
  }

  public void setNumOfVerticalLine(int i) {
    fNumOfVerticalLine = i;
  }

  // Shallow Copy で fGraph を共有し，その他の変数が同一の UGraph を作って返す．
  public UGraph copyGraph() {
    UGraph tmpG = new UGraph();
    tmpG.setLayout(null);
    // fGraph はシャローコピー．複数の画面に同一のグラフを表示するため
    tmpG.setGraph(fGraph);
    tmpG.setBackground(this.getBackground());
    tmpG.setXAuto(fIsXAuto);
    tmpG.setYAuto(fIsYAuto);
    tmpG.setFixedMaxX(fFixedMaxX);
    tmpG.setFixedMinX(fFixedMinX);
    tmpG.setFixedMaxY(fFixedMaxY);
    tmpG.setFixedMinY(fFixedMinY);
    tmpG.setNumOfPlotData(fNumOfPlotData);
    tmpG.setTopMargin(fTopMargin);
    tmpG.setBottomMargin(fBottomMargin);
    tmpG.setRightMargin(fRightMargin);
    tmpG.setLeftMargin(fLeftMargin);
    return tmpG;
  }

  public static void main(String[] rgs) {
    final JFrame f = new JFrame("UGraph");
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        f.setVisible(false);
        f.dispose();
        System.exit(0);
      }
    });
    UGraph g = new UGraph();
    g.setFixedMaxX(10000);
    g.setFixedMaxY(1000);
    f.setContentPane(g);
    f.setLocation(100, 100);
    f.pack();
    f.setVisible(true);
  }

  /**
   * @return Returns the xLableName.
   */
  public String getXLableName() {
    return fXLableName;
  }

  /**
   * @param lableName The xLableName to set.
   */
  public void setXLableName(String lableName) {
    fXLableName = lableName;
  }

  /**
   * @return Returns the yLableName.
   */
  public String getYLableName() {
    return fYLableName;
  }

  /**
   * @param lableName The yLableName to set.
   */
  public void setYLableName(String lableName) {
    fYLableName = lableName;
  }
}
