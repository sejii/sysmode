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
package util;

import java.io.*;
import java.util.*;

/**
 * このクラスは，コマンドの定義のためのクラスである．
 */
public class UCmdDefinition {

  /** コマンド名 */
  private String fCmdName;

  /** コマンドへの引数のリスト */
  private ArrayList fArgList;

  /** コマンドの戻り値情報 */
  private HashMap fReturnData;

  /**
   * コンストラクタ
   */
  public UCmdDefinition() {
    fCmdName = null;
    fArgList = new ArrayList();
    fReturnData = new HashMap();
  }

  /**
   * ArrayList型を読み込む
   * @param br 入力ストリーム
   * @param st ArrayList型の変数名
   * @param hash 読込先のHashMap
   * @throws IOException
   */
  public void readArrayList(BufferedReader br, StringTokenizer st, HashMap hash) throws
      IOException {
    String type = "ArrayList";
    String name = st.nextToken();
    String comment = st.nextToken();
    hash.put("TYPE", type);
    hash.put("NAME", name);
    hash.put("COMMENT", comment);
    st.nextToken(); // "{"
    String line = br.readLine();
    StringTokenizer st2 = new StringTokenizer(line);
    HashMap contents = new HashMap();
    hash.put("CONTENTS", contents);
    String type2 = st2.nextToken();
    if (type2.equals("HashMap")) {
      readHashMap(br, st2, contents);
    } else if (type2.equals("ArrayList")) {
      System.err.println("Error: Nest of ArrayList is detected!!");
      System.exit(5);
//			readArrayList(br, st2, contents);
    } else {
      String name2 = st2.nextToken();
      String comment2 = st2.nextToken();
      contents.put("TYPE", type2);
      contents.put("NAME", name2);
      contents.put("COMMENT", comment2);
    }
    line = br.readLine(); // "}"
  }

  /**
   * HashMap型を読み込む
   * @param br 入力ストリーム
   * @param st HashMap型の変数名
   * @param hash 読込先のHashMap
   * @throws IOException
   */
  public void readHashMap(BufferedReader br, StringTokenizer st, HashMap hash) throws
      IOException {
    String type = "HashMap";
    String name = st.nextToken();
    String comment = st.nextToken();
    hash.put("TYPE", type);
    hash.put("NAME", name);
    hash.put("COMMENT", comment);
    String token = st.nextToken(); // "{"
    ArrayList contents = new ArrayList();
    hash.put("CONTENTS", contents);
    boolean readArrayListFlag = false;
    String line = null;
    while ( (line = br.readLine()) != null) {
      StringTokenizer st2 = new StringTokenizer(line);
      if (st2.hasMoreTokens()) {
        String type2 = st2.nextToken();
        if (type2.equals("}")) {
          return;
        }
        if (readArrayListFlag) {
          System.err.println(
              "Error: A data is detected after reading ArrayList!!");
          System.exit(5);
        }
        if (type2.equals("HashMap")) {
          HashMap hash2 = new HashMap();
          contents.add(hash2);
          readHashMap(br, st2, hash2);
        } else if (type2.equals("ArrayList")) {
          HashMap hash2 = new HashMap();
          contents.add(hash2);
          readArrayList(br, st2, hash2);
          readArrayListFlag = true;
        } else {
          String name2 = st2.nextToken();
          String comment2 = st2.nextToken();
          HashMap hash2 = new HashMap();
          hash2.put("TYPE", type2);
          hash2.put("NAME", name2);
          hash2.put("COMMENT", comment2);
          contents.add(hash2);
        }
      }
    }
  }

  /**
   * ファイルからコマンド定義を読み込む
   * @param filename コマンド定義のファイル名
   */
  public void readFrom(String filename) {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new
          FileInputStream(filename)));
      String line = null;
      System.out.println("Reading " + filename + " ...");
      while ( (line = br.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line);
        if (st.hasMoreTokens()) {
          String what = st.nextToken();
          if (what.equals("CommandName")) {
            fCmdName = st.nextToken();
          } else if (what.equals("param")) {
            String type = st.nextToken();
            String name = st.nextToken();
            String comment = st.nextToken();
            HashMap arg = new HashMap();
            arg.put("TYPE", type);
            arg.put("NAME", name);
            arg.put("COMMENT", comment);
            fArgList.add(arg);
          } else if (what.equals("return")) {
            String type = st.nextToken();
            if (type.equals("HashMap")) {
              readHashMap(br, st, fReturnData);
            } else if (type.equals("ArrayList")) {
              readArrayList(br, st, fReturnData);
            } else {
              System.err.println("Invalid type: " + type +
                                 " in UCmdDefinition.readFrom");
              System.exit(5);
            }
          } else {
            System.err.println("Unknown token:" + what);
            System.exit(5);
          }
        }
      }
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }

  public void printOn() {
    System.out.println(fCmdName);
    System.out.println(fArgList.toString());
    System.out.println(fReturnData);
  }

  /**
   * コマンドへの引数のリストを返す
   * @return コマンドへの引数リスト
   */
  public ArrayList getArgList() {
    return fArgList;
  }

  /**
   * コマンド名を返す
   * @return コマンド名
   */
  public String getCmdName() {
    return fCmdName;
  }

  /**
   * コマンドのリターンデータを返す
   * @return リターンデータ
   */
  public HashMap getReturnData() {
    return fReturnData;
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: java UCmdDefinition cmdFile");
      System.exit(0);
    }
    String cmdFile = args[0];
    UCmdDefinition cmdDef = new UCmdDefinition();
    cmdDef.readFrom(cmdFile);
    cmdDef.printOn();
  }
}
