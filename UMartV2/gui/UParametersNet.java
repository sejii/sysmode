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

public class UParametersNet {
  static private UParametersNet fParametersNet = new UParametersNet(); //Singlton

  // àÍìxÇæÇØèâä˙âª

  protected String fHostName = "localhost";

  protected String fAccountName = "member";

  protected int fPort = 5010;

  protected ResourceBundle fRb = UParameters.fRb;

  private UParametersNet() {
  }

  public static UParametersNet getInstance() {
    return fParametersNet;
  }

  public static void main(String[] args) {
    UParametersNet UParametersNet1 = new UParametersNet();
  }

  /**
   * @return
   */
  public String getAccountName() {
    return fAccountName;
  }

  /**
   * @return
   */
  public String getHostName() {
    return fHostName;
  }

  /**
   * @return
   */
  public int getPort() {
    return fPort;
  }

  /**
   * @param string
   */
  public void setAccountName(String string) {
    fAccountName = string;
  }

  /**
   * @param string
   */
  public void setHostName(String string) {
    fHostName = string;
  }

  /**
   * @param i
   */
  public void setPort(int i) {
    fPort = i;
  }

}