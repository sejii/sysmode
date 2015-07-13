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

public class UCmdCoreGenerator {

  /** コマンド定義 */
  private UCmdDefinition fCmdDef;

  public UCmdCoreGenerator(UCmdDefinition cmdDef) {
    fCmdDef = cmdDef;
  }

  private void writeKey(PrintWriter pw, HashMap key) {
    String type = (String) key.get("TYPE");
    String name = (String) key.get("NAME");
    String comment = (String) key.get("COMMENT");
    StringTokenizer st = new StringTokenizer(name, "_");
    String keyStr = Utility.makeKeyString(type, name);
    pw.println("  /** " + comment + "を引くためのキー */");
    pw.println("  public static final String " + keyStr + " = \"" + keyStr +
               "\";");
  }

  private void writeArg(PrintWriter pw, HashMap key) {
    String type = (String) key.get("TYPE");
    String name = (String) key.get("NAME");
    String comment = (String) key.get("COMMENT");
    String variableName = Utility.makeFieldString(name);
    pw.println("  /** コマンドへの引数：" + comment + " */");
    pw.println("  protected " + type + " " + variableName + ";");
  }

  private void writeHashKey(PrintWriter pw, HashMap item) {
    writeKey(pw, item);
    pw.println();
    ArrayList contents = (ArrayList) item.get("CONTENTS");
    Iterator itr = contents.iterator();
    while (itr.hasNext()) {
      HashMap item2 = (HashMap) itr.next();
      String type = (String) item2.get("TYPE");
      if (type.equals("HashMap")) {
        writeHashKey(pw, item2);
      } else {
        writeKey(pw, item2);
        pw.println();
      }
    }
  }

  private void writeArrayListKey(PrintWriter pw, HashMap item) {
    writeKey(pw, item);
    HashMap contents = (HashMap) item.get("CONTENTS");
    String type = (String) contents.get("TYPE");
    if (type.equals("HashMap")) {
      writeHashKey(pw, contents);
    } else if (type.equals("ArrayList")) {
      writeArrayListKey(pw, contents);
    } else {
    }
  }

  private void writeAllKeys(PrintWriter pw) {
    String type = (String) fCmdDef.getReturnData().get("TYPE");
    if (type.equals("HashMap")) {
      ArrayList contents = (ArrayList) fCmdDef.getReturnData().get("CONTENTS");
      Iterator itr = contents.iterator();
      while (itr.hasNext()) {
        HashMap item = (HashMap) itr.next();
        String type2 = (String) item.get("TYPE");
        if (type2.equals("HashMap")) {
          writeHashKey(pw, item);
        } else {
          writeKey(pw, item);
          pw.println();
        }
      }
    } else if (type.equals("ArrayList")) {
      HashMap item = (HashMap) fCmdDef.getReturnData().get("CONTENTS");
      String type2 = (String) item.get("TYPE");
      if (type2.equals("HashMap")) {
        writeHashKey(pw, item);
      }
    }
  }

  private void writeAllArgs(PrintWriter pw) {
    Iterator itr = fCmdDef.getArgList().iterator();
    while (itr.hasNext()) {
      HashMap arg = (HashMap) itr.next();
      writeArg(pw, arg);
      pw.println();
    }
  }

  private void writeConstructer(PrintWriter pw, String className) {
    String type = (String) fCmdDef.getReturnData().get("TYPE");
    String name = (String) fCmdDef.getReturnData().get("NAME");
    String comment = (String) fCmdDef.getReturnData().get("COMMENT");
    pw.println("  /**　コンストラクタ */");
    pw.println("  public " + className + "() {");
    pw.println("    super();");
    pw.println("    fCommandStatus = new UCommandStatus();");
    pw.println("    " + Utility.makeFieldString(name) + " = new " + type +
               "();");
    pw.println("  }");
  }

  private void writeIsNameEqualTo(PrintWriter pw) {
    pw.println("  public boolean isNameEqualTo(String name) {");
    pw.println("    if ( name.equalsIgnoreCase(CMD_NAME) )");
    pw.println("      return true;");
    pw.println("    else");
    pw.println("      return false;");
    pw.println("  }");
  }

  private void writeGetName(PrintWriter pw) {
    pw.println("  public String getName() {");
    pw.println("    return CMD_NAME;");
    pw.println("  }");
  }

  private void writeArrayListInGetResultString(PrintWriter pw, String arrayName,
                                               HashMap contents, String indent) {
    String className = "UC" + fCmdDef.getCmdName() + "Core";
    String type = (String) contents.get("TYPE");
    String name = (String) contents.get("NAME");
    String itrStr = arrayName + "Itr";
    pw.println(indent + "Iterator " + itrStr + " = " + arrayName +
               ".iterator();");
    pw.println(indent + "while ( " + itrStr + ".hasNext() ) {");
    String indent2 = indent + "  ";
    String localName = Utility.makeLocalVariableString(name);
    if (type.equals("HashMap")) {
      pw.println(indent2 + "HashMap " + localName + " = (HashMap)" + itrStr +
                 ".next();");
      ArrayList contents2 = (ArrayList) contents.get("CONTENTS");
      writeHashInGetResultString(pw, localName, contents2, indent2);
    } else if (type.equals("ArrayList")) {
      pw.println(indent2 + "ArrayList " + localName + " = (ArrayList)" + itrStr +
                 ".next();");
      HashMap contents2 = (HashMap) contents.get("CONTENTS");
      writeArrayListInGetResultString(pw, localName, contents2, indent2);
    } else {
      pw.println(indent2 + "returnStr += " + itrStr + ".next().toString();");
    }
    pw.println(indent + "}");
  }

  private void writeHashInGetResultString(PrintWriter pw, String hashName,
                                          ArrayList contents, String indent) {
    String className = "UC" + fCmdDef.getCmdName() + "Core";
    Iterator itr = contents.iterator();
    while (itr.hasNext()) {
      HashMap elem = (HashMap) itr.next();
      String type = (String) elem.get("TYPE");
      String name = (String) elem.get("NAME");
      String localName = Utility.makeLocalVariableString(name);
      String keyStr = className + "." + Utility.makeKeyString(type, name);
      if (type.equals("HashMap")) {
        pw.println(indent + "HashMap " + localName + " = (HashMap)" + hashName +
                   ".get(" + keyStr + ");");
        ArrayList contents2 = (ArrayList) elem.get("CONTENTS");
        writeHashInGetResultString(pw, localName, contents2, indent);
      } else if (type.equals("ArrayList")) {
        pw.println(indent + "ArrayList " + localName + " = (ArrayList)" +
                   hashName + ".get(" + keyStr + ");");
        HashMap contents2 = (HashMap) elem.get("CONTENTS");
        writeArrayListInGetResultString(pw, localName, contents2, indent);
      } else {
        pw.println(indent + "returnStr += " + hashName + ".get(" + keyStr +
                   ").toString() + \"\\n\";");
      }
    }
  }

  private void writeGetResultString(PrintWriter pw, String cmdName) {
    String className = "UC" + cmdName + "Core";
    pw.println("  public String getResultString() {");
    pw.println("    String returnStr = \"\";");
    String type = (String) fCmdDef.getReturnData().get("TYPE");
    String name = (String) fCmdDef.getReturnData().get("NAME");
    String initialIndent = "    ";
    if (type.equals("HashMap")) {
      String hashName = Utility.makeFieldString(name);
      ArrayList contents = (ArrayList) fCmdDef.getReturnData().get("CONTENTS");
      writeHashInGetResultString(pw, hashName, contents, initialIndent);
    } else if (type.equals("ArrayList")) {
      String arrayListName = Utility.makeFieldString(name);
      HashMap contents = (HashMap) fCmdDef.getReturnData().get("CONTENTS");
      writeArrayListInGetResultString(pw, arrayListName, contents,
                                      initialIndent);
    } else {
      System.err.println("Unknown type in UCmdGen.writeGetResultString: " +
                         type);
      System.exit(5);
    }

    pw.println("    return returnStr;");
    pw.println("  }");

  }

  private void writeSetArguments(PrintWriter pw) {
    pw.println("  public boolean setArguments(StringTokenizer st) {");
    pw.println("    try {");
    Iterator itr = fCmdDef.getArgList().iterator();
    while (itr.hasNext()) {
      HashMap argInfo = (HashMap) itr.next();
      String type = (String) argInfo.get("TYPE");
      String name = (String) argInfo.get("NAME");
      if (type.equals("int")) {
        pw.println("      " + Utility.makeFieldString(name) +
                   " = Integer.parseInt(st.nextToken());");
      } else if (type.equals("long")) {
        pw.println("      " + Utility.makeFieldString(name) +
                   " = Long.parseLong(st.nextToken());");
      } else if (type.equals("double")) {
        pw.println("      " + Utility.makeFieldString(name) +
                   " = Double.parseDouble(st.nextToken());");
      } else if (type.equals("String")) {
        pw.println("      " + Utility.makeFieldString(name) +
                   " = st.nextToken();");
      } else {
        System.err.println("Unknown type in UCmdGen.writeSetArguments: " + type);
        System.exit(5);
      }
    }
    pw.println("      return true;");
    pw.println("    } catch ( Exception e ) {");
    pw.println("      e.printStackTrace();");
    pw.println("      return false;");
    pw.println("    }");
    pw.println("  }");
  }

  private void writeSetArguments2(PrintWriter pw) {
    {
      String argStr = "";
      Iterator itr = fCmdDef.getArgList().iterator();
      while (itr.hasNext()) {
        HashMap argInfo = (HashMap) itr.next();
        String type = (String) argInfo.get("TYPE");
        String name = (String) argInfo.get("NAME");
        String variableName = Utility.makeLocalVariableString(name);
        argStr += type + " " + variableName;
        if (itr.hasNext()) {
          argStr += ", ";
        }
      }
      pw.println("  public void setArguments(" + argStr + ") {");
    }
    {
      Iterator itr = fCmdDef.getArgList().iterator();
      while (itr.hasNext()) {
        HashMap argInfo = (HashMap) itr.next();
        String type = (String) argInfo.get("TYPE");
        String name = (String) argInfo.get("NAME");
        String variableName = Utility.makeLocalVariableString(name);
        String fieldName = Utility.makeFieldString(name);
        pw.println("    " + fieldName + " = " + variableName + ";");
      }
    }
    pw.println("  }");
  }

  private void writeGetStatus(PrintWriter pw) {
    pw.println("  public UCommandStatus getStatus() {");
    pw.println("  	return fCommandStatus;");
    pw.println("  }");
  }

  private void writePrintOn(PrintWriter pw) {
    pw.println("  public void printOn() {");
    pw.println("    System.out.println(" +
               Utility.makeFieldString( (String) fCmdDef.getReturnData().get(
        "NAME"))
               + ".toString());");
    pw.println("  }");
  }

  private void writeGetData(PrintWriter pw) {
    String type = (String) fCmdDef.getReturnData().get("TYPE");
    pw.println("  public " + type + " getData() {");
    pw.println("    return " +
               Utility.makeFieldString( (String) fCmdDef.
                                       getReturnData().get("NAME")) + ";");
    pw.println("  }");
  }

  private void writeReturn(PrintWriter pw) {
    String type = (String) fCmdDef.getReturnData().get("TYPE");
    String name = (String) fCmdDef.getReturnData().get("NAME");
    String comment = (String) fCmdDef.getReturnData().get("COMMENT");
    pw.println("  /** " + comment + " */");
    pw.println("  protected " + type + " " + Utility.makeFieldString(name) +
               ";");

  }

  public void writeCommandCore() throws FileNotFoundException {
    String className = "UC" + fCmdDef.getCmdName() + "Core";
    PrintWriter pw = new PrintWriter(new FileOutputStream(className + ".java"));
    System.out.println("Wrting " + className + ".java ...");
    pw.println("package cmdCore;");
    pw.println();
    pw.println("import java.util.ArrayList;");
    pw.println("import java.util.HashMap;");
    pw.println("import java.util.Iterator;");
    pw.println("import java.util.StringTokenizer;");
    pw.println();
    pw.println("public abstract class " + className + " implements ICommand {");
    pw.println();
    pw.println("  /** コマンド名 */");
    pw.println("  public static final String CMD_NAME = \"" +
               fCmdDef.getCmdName() + "\";");
    pw.println();
    writeAllArgs(pw); // コマンドへの引数を出力
    pw.println(" /** コマンドの実行結果の状態 */");
    pw.println("  protected UCommandStatus fCommandStatus;");
    pw.println();
    writeReturn(pw);
    pw.println();
    writeAllKeys(pw);
    pw.println();
    writeConstructer(pw, className);
    pw.println();
    writeIsNameEqualTo(pw);
    pw.println();
    writeGetName(pw);
    pw.println();
    writeSetArguments(pw);
    pw.println();
    writeSetArguments2(pw);
    pw.println();
    writeGetStatus(pw);
    pw.println();
    writePrintOn(pw);
    pw.println();
    writeGetData(pw);
    pw.println();
    writeGetResultString(pw, className);
    pw.println();
    pw.println("}");
    pw.close();
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: java UCmdGen cmdfile");
      System.exit(0);
    }
    String cmdFile = args[0];
    UCmdDefinition cmdDef = new UCmdDefinition();
    cmdDef.readFrom(cmdFile);
    UCmdCoreGenerator gen = new UCmdCoreGenerator(cmdDef);
    try {
      gen.writeCommandCore();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }

}
