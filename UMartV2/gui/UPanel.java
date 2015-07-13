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
import java.util.*;

import javax.swing.*;

import cmdCore.*;

/**
 * メインウインドウのパネルの親クラス．パラメータやサーバと通信する fCommandHashMap 等を持つ．
 * 継承先では dataUpdate() と必要ならば gUpdate() をオーバライドする．
 * dataUpdate はモデルを更新する．gUpdate() は可視の場合のみモデルを更新し，repaint する．
 * メインウインドウでは必要に応じて dataUpdate や gUpdate をする．
 * なお，メインウインドウでは UPanelFactory を通して Upanel を継承したクラスを得る．
 * サブクラス： すべてのタブ，UTopPanel, UOrderButtonPanel, USubPanel
 */
public class UPanel extends JPanel implements IGUIObserver {

//    protected static HashMap fCommandHashMap;
  protected static UProtocolCore fCProtocol;
  protected static ResourceBundle fRb = UParameters.fRb;
  protected static UParameters fParam = UParameters.getInstance();
  protected String fName = "UPanel";

  public UPanel() {
    // Update List に追加．呼ばないとUpdateされない．
    addUpdateObserverList();
    setBorder(BorderFactory.createRaisedBevelBorder());
    setLayout(null);
  }

  public static void setCProtocol(UProtocolCore cp) {
    fCProtocol = cp;
  }

  /** IGUIObserver を継承しているので，必要な場合には継承先で自分を addUpdateObserverList から
   * UpdateManager に登録することを忘れないこと．
   * UPanel を作った後で IGUIObserver を導入したためこの辺の設計はうまくないと思う．
   * 問題点は継承先でaddUpdateObserverList の作成を強要できないこと．abstract にすればいいんだけど
   * なぜか継承先でJBuilderのGUI作成画面が真っ赤になってしまうため断念．回避法を調べる気力は現在なし．
   * 余力があれば直す．
   */
  public void addUpdateObserverList() {};

  public static void setParameters(UParameters p) {
    fParam = p;
  }

  public static UParameters getParameters() {
    return fParam;
  }

  /** Update をするかどうか。面倒だが負荷軽減のため。
   * 基本的にはisVisivilでdataUpdateするかを判断したいが、タブクリック時の
   * 処理のためや、定期的なUpdateのために強制的にUpdateするフラグを利用する。
   * isVisivleでない場合にUpdateしたいときはsetUpdateをしてから呼ぶ。
   * 現在は主にUGUIMainWindowのChangeListener内で利用している。
   * */
  public void dataUpdate() {
    /* モデルの更新 */
  }

  public void gUpdate() {
    IGUIEvent iev = new IGUIEvent() {
      public void execute() {
        dataUpdate();
      }

      public Runnable getRunnableForInvokeLater() {
        return new Runnable() {
          public void run() {
            repaint();
          }
        };
      }
    };
    UGUIEventQueue.getInstance().addIGUIEvent(iev);
  }

  public String getName() {
    return fName;
  }

  public void writeLog(String filename) throws IOException {
  }
}
