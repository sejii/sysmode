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

public class UGUIMain {
  ULogoWindow fLogow;
  UGUITypeSetFrame fStartFrame;
  UParameters fParam = UParameters.getInstance();
  public UGUIMain() {
    try {
      fLogow = new ULogoWindow();
      fLogow.setVisible(true);
      Thread.sleep(2500);
      fLogow.dispose();
      fStartFrame = new UGUITypeSetFrame();
      fStartFrame.setVisible(true);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void startGUI() {
    if (fParam.getConnectionType() == UParameters.CONNECTION_TYPE_STAND_ALONE) {
      try {
        UStandAloneGUI ugui = new UStandAloneGUI();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (fParam.getConnectionType() ==
               UParameters.CONNECTION_TYPE_NETWORK) {
      try {
        UNetGUI ugui = new UNetGUI("localhost");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    if (args.length != 0) {
      System.err.println("usage:");
      System.err.println("Default Language GUI Client: java -jar UGUI.jar");
      System.err.println(
          "Japanese GUI Client: java -Duser.language=ja -jar UGUI.jar");
      System.err.println(
          "English GUI Client: java -Duser.language=en -jar UGUI.jar");
      System.err.println("ServerManager: java -cp UGUI.jar gui.UServerManager");
      System.err.println("Network server(CUI): java -cp UGUI.jar serverNet.UMartNetwork priceFile startPoint maxDate noOfBoardsPerDay noOfMembers timeForWaitingLogin interval");
      System.exit( -1);
    }
    UGUIMain guiMain = new UGUIMain();
    guiMain.startGUI();
  }

}