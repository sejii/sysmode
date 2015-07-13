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
import java.awt.geom.*;

/**
 * グラフのデータ保存用クラス．
 * @author akimoto
 *
 */
public class UGraphData {

  /** グラフの名前 */
  protected String fDataName;

  /** Point2D によるデータ*/
  protected ArrayList fData;

  /** グラフの色 */
  protected Color fC;

  public UGraphData() {
    fDataName = "";
    fData = new ArrayList();
    fC = Color.blue;
  }

  public UGraphData(String s) {
    fDataName = s;
    fData = new ArrayList();
    fC = Color.blue;
  }

  public UGraphData(String s, Color c) {
    fDataName = s;
    fData = new ArrayList();
    fC = c;
  }

  public String getDataName() {
    return fDataName;
  }

  public void setDataName(String s) {
    fDataName = s;
  }

  public ArrayList getData() {
    return fData;
  }

  public void setData(ArrayList v) {
    if (v.size() == 0) {
      return;
    }
    fData.clear();
    Class type = v.get(0).getClass();
    if (type.getSuperclass() == java.awt.geom.Point2D.class) {
      fData.addAll(v);
    } else if (type.getSuperclass() == java.lang.Number.class) {
      Iterator iter = v.iterator();
      int index = 0;
      while (iter.hasNext()) {
        Number tmpN = (Number) iter.next();
        double tmpD = tmpN.doubleValue();
        Point2D tmpPoint = new Point2D.Double(index, tmpD);
        fData.add(tmpPoint);
        index++;
      }
    }
  }

  public void setData(char[] data) {
    fData.clear();
    for (int i = 0; i < data.length; i++) {
      Point2D tmpPoint = new Point2D.Double(i, data[i]);
      fData.add(tmpPoint);
    }
  }

  public void setData(int[] data) {
    fData.clear();
    for (int i = 0; i < data.length; i++) {
      Point2D tmpPoint = new Point2D.Double(i, data[i]);
      fData.add(tmpPoint);
    }
  }

  public void setData(long[] data) {
    fData.clear();
    for (int i = 0; i < data.length; i++) {
      Point2D tmpPoint = new Point2D.Double(i, data[i]);
      fData.add(tmpPoint);
    }
  }

  public void setData(float[] data) {
    fData.clear();
    for (int i = 0; i < data.length; i++) {
      Point2D tmpPoint = new Point2D.Double(i, data[i]);
      fData.add(tmpPoint);
    }
  }

  public void setData(double[] data) {
    fData.clear();
    for (int i = 0; i < data.length; i++) {
      Point2D tmpPoint = new Point2D.Double(i, data[i]);
      fData.add(tmpPoint);
    }
  }

  public Color getColor() {
    return fC;
  }

  public void setColor(Color c) {
    fC = c;
  }

  public double maxX() {
    Iterator iter = fData.iterator();
    Point2D tmpPoint = new Point2D.Double();
    double max = Double.MIN_VALUE;
    while (iter.hasNext()) {
      tmpPoint = (Point2D) iter.next();
      if (tmpPoint.getX() > max) {
        max = tmpPoint.getX();
      }
    }
    return max;
  }

  public double maxX(int start, int end) {
    Iterator iter = fData.subList(start, end).iterator();
    Point2D tmpPoint = new Point2D.Double();
    double max = Double.MIN_VALUE;
    while (iter.hasNext()) {
      tmpPoint = (Point2D) iter.next();
      if (tmpPoint.getX() > max) {
        max = tmpPoint.getX();
      }
    }
    return max;
  }

  public double minX() {
    Iterator iter = fData.iterator();
    Point2D tmpPoint = new Point2D.Double();
    double min = Double.MAX_VALUE;
    while (iter.hasNext()) {
      tmpPoint = (Point2D) iter.next();
      if (tmpPoint.getX() < min) {
        min = tmpPoint.getX();
      }
    }
    return min;
  }

  public double minX(int start, int end) {
    Iterator iter = fData.subList(start, end).iterator();
    Point2D tmpPoint = new Point2D.Double();
    double min = Double.MAX_VALUE;
    while (iter.hasNext()) {
      tmpPoint = (Point2D) iter.next();
      if (tmpPoint.getX() < min) {
        min = tmpPoint.getX();
      }
    }
    return min;
  }

  public double maxY() {
    Iterator iter = fData.iterator();
    Point2D tmpPoint = new Point2D.Double();
    double max = Double.MIN_VALUE;
    while (iter.hasNext()) {
      tmpPoint = (Point2D) iter.next();
      if (tmpPoint.getY() > max) {
        max = tmpPoint.getY();
      }
    }
    return max;
  }

  public double maxY(int start, int end) {
    Iterator iter = fData.subList(start, end).iterator();
    Point2D tmpPoint = new Point2D.Double();
    double max = Double.MIN_VALUE;
    while (iter.hasNext()) {
      tmpPoint = (Point2D) iter.next();
      if (tmpPoint.getY() > max) {
        max = tmpPoint.getY();
      }
    }
    return max;
  }

  public double minY() {
    Iterator iter = fData.iterator();
    Point2D tmpPoint = new Point2D.Double();
    double min = Double.MAX_VALUE;
    while (iter.hasNext()) {
      tmpPoint = (Point2D) iter.next();
      if (tmpPoint.getY() < min) {
        min = tmpPoint.getY();
      }
    }
    return min;
  }

  public double minY(int start, int end) {
    Iterator iter = fData.subList(start, end).iterator();
    Point2D tmpPoint = new Point2D.Double();
    double min = Double.MAX_VALUE;
    while (iter.hasNext()) {
      tmpPoint = (Point2D) iter.next();
      if (tmpPoint.getY() < min) {
        min = tmpPoint.getY();
      }
    }
    return min;
  }

  public Point2D pointAt(int i) {
    return (Point2D) fData.get(i);
  }

  public Point2D[] pointArray() {
    int dataSize = fData.size();
    Point2D[] p = null;
    try {
      p = new Point2D[fData.size()];
      for (int i = 0; i < fData.size(); i++) {
        p[i] = new Point2D.Double( ( (Point2D) fData.get(i)).getX(),
                                  ( (Point2D) fData.get(i)).getY());
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Original:" + dataSize + ", Current:" + fData.size());
    }
    return p;
  }

  public static void main(String[] args) {
    UGraphData test = new UGraphData("test");
    Point2D a, b, c;
    a = new Point2D.Double(1, -1);
    b = new Point2D.Double(3, 4);
    c = new Point2D.Double(0.4, 3.4);
    test.getData().add(a);
    test.getData().add(b);
    test.getData().add(c);
    System.out.println("name:" + test.getDataName() + " maxX:" + test.maxX() +
                       " minX:" + test.minX() + " maxY:" + test.maxY() +
                       " minY:" + test.minY());
  }
}
