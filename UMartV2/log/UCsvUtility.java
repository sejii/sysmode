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
package log;

import java.util.*;

/**
 * CSV形式データを扱うためのユーティリティクラスです．
 */
public class UCsvUtility {

  /**
   * CSV形式に従う一行分の文字列を要素に分解し，それらの要素をArrayListに格納して返します．
   * @param line　文字列
   * @return トークンの配列
   */
  public static ArrayList split(String line) {
    ArrayList result = new ArrayList();
    StringTokenizer st = new StringTokenizer(line, ",", true);
    String latestToken = "";
    while (true) {
      try {
        String token = st.nextToken();
        latestToken = token;
        if (token.equals(",")) {
          result.add("");
        } else {
          result.add(token);
          token = st.nextToken();
          latestToken = token;
          if (!token.equals(",")) {
            System.err.println("Error: Wrong format in UMemberLog.readLineIntoArray");
            System.exit(5);
          }
        }
      } catch (NoSuchElementException nsee) {
        if (latestToken.equals(",")) {
          result.add("");
        }
        break;
      }
    }
    return result;
  }

}