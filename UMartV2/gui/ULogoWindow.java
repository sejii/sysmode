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

import java.net.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * スタート時のロゴ表示クラス．
 */
public class ULogoWindow extends JWindow {
  Toolkit fToolkit = Toolkit.getDefaultToolkit();
  JLabel fLogo;

  public ULogoWindow() {
    URL img = getClass().getResource("/resources/images/u-mart-logo.jpg");
    fLogo = new JLabel(new ImageIcon(img));
    fLogo.setBorder(BorderFactory.createRaisedBevelBorder());
    //ウィンドウを中央に配置
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension mySize = fLogo.getPreferredSize();
    if (mySize.height > screenSize.height) {
      mySize.height = screenSize.height;
    }
    if (mySize.width > screenSize.width) {
      mySize.width = screenSize.width;
    }
    setLocation( (screenSize.width - mySize.width) / 2,
                (screenSize.height - mySize.height) / 2);
    getContentPane().add(fLogo, "Center");
    pack();
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        dispose();
      }
    });
  }
}
