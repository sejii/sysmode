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
 * テーブルで表示とソート時の比較法を分離するためのクラス。
 * prefix と suffix の2つからなる表示を扱う。
 * 例えば、agent0, agent100 などを正確に表示したい場合には
 * "agent"+Integer とすれば良い。 比較は prefix が優先され、
 * prefix が一致した場合のみ suffix の結果が影響
 */
public class UTwoPartsElement implements Comparable {
  protected Object fPrefix_;
  protected Object fSuffix_;

  /**
   *
   */
  public UTwoPartsElement() {
    fPrefix_ = null;
    fSuffix_ = null;
  }

  public UTwoPartsElement(Object pre, Object suf) {
    fPrefix_ = pre;
    fSuffix_ = suf;
  }

  /**
   * 第二引数が数字の場合が多いと考えられるのでコンストラクタを作っておく。
   * @param pre
   * @param num
   */
  public UTwoPartsElement(Object pre, char num) {
    fPrefix_ = pre;
    fSuffix_ = new Long(num);
  }

  /**
   * 第二引数が数字の場合が多いと考えられるのでコンストラクタを作っておく。
   * @param pre
   * @param num
   */
  public UTwoPartsElement(Object pre, int num) {
    fPrefix_ = pre;
    fSuffix_ = new Long(num);
  }

  /**
   * 第二引数が数字の場合が多いと考えられるのでコンストラクタを作っておく。
   * @param pre
   * @param num
   */
  public UTwoPartsElement(Object pre, long num) {
    fPrefix_ = pre;
    fSuffix_ = new Long(num);
  }

  /*
   * 現在は UTwoPartsElement 以外の型との比較は考えていない。毎回クラスのチェックをすると
   * 重くなるため。キャストしそこなう場合は想定外。一応 try catch で落ちないようにする。
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object u2Parts) {
    int result = 0;
    Object pre = null, suf = null;
    try {
      pre = ( (UTwoPartsElement) u2Parts).getPrefix();
      suf = ( (UTwoPartsElement) u2Parts).getSuffix();
    } catch (Exception e) {
      System.err.println(
          "\"u2Parts\" of compareTo(Object u2Parts) in UTwoPartsElement.java must be an instance of UTwoPartsElement!");
      e.printStackTrace();
    }
    try {
      //			null は最小
      if (getPrefix() == null && pre == null) {
        return 0;
      } else if (getPrefix() != null && pre == null) {
        return 1;
      } else if (getPrefix() == null && pre != null) {
        return -1;
      }
      result = ( (Comparable) getPrefix()).compareTo( (Comparable) pre);
      if (result == 0) {
        if (getSuffix() == null && suf == null) {
          return 0;
        } else if (getSuffix() != null && suf == null) {
          return 1;
        } else if (getSuffix() == null && suf != null) {
          return -1;
        }
        result = ( (Comparable) getSuffix()).compareTo( (Comparable) suf);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  public String toString() {
    String p, s;
    if (fPrefix_ == null) {
      p = "";
    } else {
      p = fPrefix_.toString();
    }

    if (fSuffix_ == null) {
      s = "";
    } else {
      s = fSuffix_.toString();
    }
    return p.toString() + s.toString();
  }

  /*
      public static void main(String[] args) {
          UTwoPartsElement[] array = new UTwoPartsElement[10];
          String pre = "agent";
          String[] str = new String[10];
          Integer[] num = new Integer[10];
          for (int i = 0; i < 10; i++) {
              num[i] = new Integer(i + i % 2 * 100);
              str[i] = pre + num[i];
              array[i] = new UTwoPartsElement(pre, num[i]);
          }
          array[9] = new UTwoPartsElement();
                  array[9].setPrefix(pre);
                  array[9].setSuffix(null);
          Arrays.sort(str);
          for (int i = 0; i < str.length; i++) {
              System.out.println(str[i]);
          }
          System.err.println("array:");
          Arrays.sort(array);
          for (int i = 0; i < array.length; i++) {
              System.out.println(i + " " + array[i]);
          }
      }
   */
  /**
   * @return
   */
  public Object getPrefix() {
    return fPrefix_;
  }

  /**
   * @return
   */
  public Object getSuffix() {
    return fSuffix_;
  }

  /**
   * @param object
   */
  public void setPrefix(Object object) {
    fPrefix_ = object;
  }

  /**
   * @param object
   */
  public void setSuffix(Object object) {
    fSuffix_ = object;
  }

  /**
   * @param num
   */
  public void setSuffix(char num) {
    fSuffix_ = new Long(num);
  }

  /**
   * @param num
   */
  public void setSuffix(int num) {
    fSuffix_ = new Long(num);
  }

  /**
   * @param object
   */
  public void setSuffix(long num) {
    fSuffix_ = new Long(num);
  }
}
