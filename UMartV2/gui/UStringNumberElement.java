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

/**
 * 文字列＋数字に特化したクラス．0が数値の先頭にある場合の
 * 処理を考えている．
 */
public class UStringNumberElement extends UTwoPartsElement {
  /**
   * p0342001 が p342001 と表示されてしまう．ソートの性質を保つために，先頭の0は文字列の方に 含める．
   */
  public UStringNumberElement(String s) {
    //前部分 null はややこしいので入れない．何もなくても空文字列を入れる．
    fPrefix_ = s.replaceAll("[1-9]+[0-9]*\\b", ""); // 最後の数字部分を消す．先頭は0以外
    //後部分
    String suf = s.replaceAll("^.*\\D0*", ""); // 最初の数字以外部分を消す．末尾の0*は含む．

    if (suf.equals("")) {
      //数字部分がない場合は第二部分はなし
      fSuffix_ = null;
    } else {
      fSuffix_ = Long.valueOf(suf);
    }
  }

  /**
   * 親クラスをオーバーライド．末尾の0を削ったものを返す．
   * そうしないと，a01 > a10 になってしまう．
   *
   * @return
   */
  public Object getPrefix() {
    String s = ( (String) fPrefix_).replaceAll("0+\\b", "");
    return s;
  }

  public static void main(String[] args) {
    UStringNumberElement s1 = new UStringNumberElement("art001");
    UStringNumberElement s2 = new UStringNumberElement("art0001");
    UStringNumberElement s3 = new UStringNumberElement("art100");
    System.out.println(s1);
    System.out.println(s1.getPrefix());
    System.out.println(s1.getSuffix());
    System.out.println(s2);
    System.out.println(s2.getPrefix());
    System.out.println(s2.getSuffix());
    System.out.println(s3);
    System.out.println(s3.getPrefix());
    System.out.println(s3.getSuffix());
    Object[] array = new Object[3];
    array[0] = s3;
    array[1] = s1;
    array[2] = s2;
    Arrays.sort(array);
    for (int i = 0; i < array.length; i++) {
      System.out.println(array[i]);
    }

  }
}