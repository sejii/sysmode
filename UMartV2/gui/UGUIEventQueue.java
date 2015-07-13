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

import javax.swing.*;

public class UGUIEventQueue {
  private LinkedList fQueue;
  private Thread fWorker;
  private int fMaxSize = 50;
  private static UGUIEventQueue fInstance = new UGUIEventQueue(); // Singlegon

  private UGUIEventQueue() {
    fQueue = new LinkedList();
    fWorker = new Thread() {
      public void run() {
        while (true) {
          IGUIEvent iev = getIGUIEvent();
          iev.execute();
          SwingUtilities.invokeLater(iev.getRunnableForInvokeLater());
        }
      }
    };
    fWorker.start();
  }

  public synchronized IGUIEvent getIGUIEvent() {
    while (fQueue.size() <= 0) {
      try {
        wait();
      } catch (InterruptedException ie) {
        System.err.println("Interrupted in KidQueue.get");
        return (IGUIEvent)null;
      } catch (Exception e) {
        System.err.println("Error: " + e + " in getIGUIEvent");
      }
    }
    IGUIEvent iev = (IGUIEvent) fQueue.removeFirst();
    notifyAll();
    return iev;
  }

  /** 必ずキューに入れたい場合. 注文関係など*/
  public synchronized void addIGUIEvent(IGUIEvent iev) {
    while (fQueue.size() >= fMaxSize) {
      try {
        wait();
      } catch (Exception e) {
        System.err.println("Error: " + e + " in addIGUIEvent");
      }
    }
    fQueue.add(iev);
    notifyAll();
  }

  /** 満杯の場合はキューに入れなくても良い場合. 画面更新とか
   * 現在は使っていないが将来的に負荷が大きな問題になった場合には
   * 対応が必要。
   */
  public synchronized void addIGUIEventOfNoWait(IGUIEvent iev) {
    if (fQueue.size() >= fMaxSize) {
      return;
    }
    fQueue.add(iev);
    notifyAll();
  }

  /** 特急イベント用. UGUIUpdateManager の更新関係用*/
  public synchronized void addIGUIEventToTop(IGUIEvent iev) {
    if (fQueue.size() >= fMaxSize) {
      fQueue.removeLast();
    }
    fQueue.addFirst(iev);
    notifyAll();
  }

  public synchronized static UGUIEventQueue getInstance() {
    return fInstance;
  }

  /**
   * @return
   */
  public int getMaxSize() {
    return fMaxSize;
  }

  /**
   * @param i
   */
  public void setMaxSize(int i) {
    fMaxSize = i;
  }

  public int getSize() {return fQueue.size();
  }

  public static void main(String[] args) {
    System.out.println("Start!");
    final UGUIEventQueue q = UGUIEventQueue.getInstance();
    Thread t = new Thread() {
      public void run() {
        while (true) {
          IGUIEvent iev = new IGUIEvent() {
            public void execute() {
              System.out.println("Hello World!");
              try {
                Thread.sleep(2000);
              } catch (Exception e) {
              }
            }

            public Runnable getRunnableForInvokeLater() {
              return new Runnable() {
                public void run() {
                  System.out.println("Runnable start!" + Math.random());
                }
              };
            }
          };
          q.addIGUIEvent(iev);
          try {
            Thread.sleep(500);
          } catch (Exception e) {
          }
        }
      }
    };
    t.start();
  }
}