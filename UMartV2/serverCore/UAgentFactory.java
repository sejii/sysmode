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

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import strategyCore.UBaseAgent;

/**
 * UBaseAgentの派生クラスを生成するためのファクトリクラスです．
 * U-Mart Version 1のStrategyの派生クラスも自動的に判別して生成します．
 */
public class UAgentFactory {
  /**
   * U-Mart Version 1におけるStrategyの派生クラスを生成します．
   * @param userName ログイン名
   * @param passwd パスワード
   * @param className クラス名
   * @param paramsStr エージェントのシステムパラメータを表す文字列
   * @param seed 乱数の種
   * @return UBaseStrategyの派生クラスのオブジェクト
   */
  private static UBaseAgent makeOldTypeStrategy(String loginName,
      String passwd,
      String className, String paramsStr, int seed,
      OutputStream os) {
    UBaseAgent result = null;
    try {
      Class targetClass = Class.forName(className);
      Class[] paramTypes = new Class[1];
      paramTypes[0] = Integer.TYPE;
      Constructor constructor = targetClass.getConstructor(paramTypes);
      Object[] args = new Object[1];
      args[0] = new Integer(seed);
      result = (UBaseAgent) constructor.newInstance(args);
      result.setLoginName(loginName);
      result.setPasswd(passwd);
      result.setOutputStream(os);
      String[] params = new String[0];
      if (paramsStr != null) {
        StringTokenizer st = new StringTokenizer(paramsStr, ":");
        params = new String[st.countTokens()];
        for (int i = 0; i < params.length; ++i) {
          params[i] = st.nextToken();
        }
      }
      result.setParameters(params);
      result.setRealName(className);
    } catch (NoSuchMethodException nme) {
      result = null;
    } finally {
      return result;
    }
  }

  /**
   * UBaseAgentの派生クラスを生成します．
   * @param userName ユーザー名
   * @param passwd パスワード
   * @param className クラス名
   * @param paramsStr エージェントのシステムパラメータを表す文字列
   * @param seed 乱数の種
   * @return UBaseAgentの派生クラスのオブジェクト
   */
  private static UBaseAgent makeNewTypeAgent(String userName, String passwd, String className, String paramsStr, int seed, OutputStream os) {
    UBaseAgent result = null;
    try {
      Class targetClass = Class.forName(className);
      Class[] paramTypes = new Class[4];
      paramTypes[0] = userName.getClass();
      paramTypes[1] = passwd.getClass();
      paramTypes[2] = className.getClass();
      paramTypes[3] = Integer.TYPE;
      Constructor constructor = targetClass.getConstructor(paramTypes);
      Object[] args = new Object[4];
      args[0] = userName;
      args[1] = passwd;
      args[2] = className;
      args[3] = new Integer(seed);
      result = (UBaseAgent) constructor.newInstance(args);
      result.setOutputStream(os);
      String[] params = new String[0];
      if (paramsStr != null) {
        StringTokenizer st = new StringTokenizer(paramsStr, ":");
        params = new String[st.countTokens()];
        for (int i = 0; i < params.length; ++i) {
          params[i] = st.nextToken();
        }
      }
      result.setParameters(params);
    } catch (NoSuchMethodException nme) {
      result = null;
    } finally {
      return result;
    }
  }

  /**
   * UBaseAgentの派生クラスを生成します．
   * このメソッドは旧バージョンにおけるStrategyクラスの派生クラスのオブジェクトも生成可能です．
   * @param userName ユーザー名
   * @param passwd パスワード
   * @param className クラス名
   * @param paramsStr エージェントのシステムパラメータを表す文字列
   * @param seed 乱数の種
   * @param os 出力ストリーム
   * @return UBaseAgentの派生クラスのオブジェクト
   */
  public static UBaseAgent makeAgent(String userName, String passwd,
                                       String className, String paramsStr,
                                       int seed, OutputStream os) throws IllegalArgumentException {
    UBaseAgent result = makeNewTypeAgent(userName, passwd, className,
                                         paramsStr, seed, os);
    if (result == null) {
      result = makeOldTypeStrategy(userName, passwd, className, paramsStr, seed, os);
      if (result == null) {
        throw new IllegalArgumentException();
      }
    }
    return result;
  }

  /**
   * UBaseAgentの派生クラスを生成します．
   * @param userName ユーザ名
   * @param passwd パスワード
   * @param className クラス名
   * @param paramsStr エージェントのシステムパラメータを表す文字列
   * @param seed 乱数の種
   * @return UBaseAgentの派生クラスのオブジェクト
   * @throws IllegalArgumentException
   */
  public static UBaseAgent makeAgent(String userName, String passwd,
                                       String className, String paramsStr,
                                       int seed) throws IllegalArgumentException {
    return makeAgent(userName, passwd, className, paramsStr, seed, System.out);
  }

}