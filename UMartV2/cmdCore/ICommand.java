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
package cmdCore;

import java.util.*;

/**
 * コマンドを定義するためのインタフェースです．
 * システム中の全てのコマンドはこのインタフェースを実装しなければなりません．
 */
public interface ICommand {

  /** 引数が間違っていることを表す定数 */
  public static final int INVALID_ARGUMENTS = 1;

  /** 受付られないコマンドであることを表す定数 */
  public static final int UNACCEPTABLE_COMMAND = 2;

  /** 存在しないコマンドであることを表す定数 */
  public static final int INVALID_COMMAND = 3;

  /**
   * コマンド名を返します．
   * @return コマンド名
   */
  public String getName();

  /**
   * このコマンドの名前がnameと等しいかを返します．
   * @param name 比較するコマンド名
   * @return true:等しい，false:等しくない
   */
  public boolean isNameEqualTo(String name);

  /**
   * このコマンドに必要な引数を設定します．
   * @param st 引数群（StringTokenizer.nextTokenで取り出せる）
   * return true：引数を正しく設定できた場合，false:失敗した場合
   */
  public boolean setArguments(StringTokenizer st);

  /**
   * コマンドを実行します．
   * @return 実行結果
   */
  public UCommandStatus doIt();

  /**
   * コマンドの実行結果を標準出力に表示します．
   */
  public void printOn();

  /**
   * コマンドの実行結果をスペース区切りの文字列として返します．
   * @return 実行結果の文字列
   */
  public String getResultString();

}
