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

import java.io.*;

/**
 * STDOUT 変換用．UParameters の PrintStream 初期化で使用．
 * コンソールの代わりに fObserver に標準出力がいく．
 */
public class UByteArrayOutputStream extends ByteArrayOutputStream {

  protected IOutputObserver fObserver = null; //本当は ArrayList にすべき

  protected String fName = "";

  public UByteArrayOutputStream() {
  }

  public UByteArrayOutputStream(IOutputObserver iob, String name) {
    fObserver = iob;
    fName = name;
  }

  public void setOutputObserver(IOutputObserver iob) {
    fObserver = iob;
  }

  public void setName(String name) {
    fName = name;
  }

  public String getName() {
    return fName;
  }

  public void flush() {
    try {
      super.flush();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    if (fObserver != null) {
      fObserver.update(this);
      reset();
    }
  }

  public static void main(String[] args) {
    UByteArrayOutputStream UByteArrayOutputStream1 = new UByteArrayOutputStream();
  }
}
