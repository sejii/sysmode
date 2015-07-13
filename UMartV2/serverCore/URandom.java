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
package serverCore;

import java.util.*;

/**
 * 乱数を扱うクラスです．
 */
public class URandom {

  /** 乱数の種 */
  private static long fSeed;

  /** 乱数生成器 */
  private static Random fRandom = null;

  /**
   * 乱数の種をシステム時刻で初期化した乱数オブジェクトを返します．
   * @return 乱数オブジェクト
   */
  public static Random getInstance() {
    if (fRandom == null) {
      fSeed = System.currentTimeMillis();
      fRandom = new Random(fSeed);
    }
    return fRandom;
  }

  /**
   * 乱数の種を設定します．
   * @param seed 乱数の種
   */
  public static void setSeed(long seed) {
    fSeed = seed;
    fRandom = new Random(fSeed);
  }

  /**
   * 乱数の種を返します．
   * @return 乱数の種
   */
  public static long getSeed() {
    return fSeed;
  }

}
