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

public class UCmdClientSAGenerator {

  /** コマンド定義 */
  private UCmdDefinition fCmdDef;

  /**
   * コンストラクタ
   *
   */
  public UCmdClientSAGenerator(UCmdDefinition cmdDef) {
    fCmdDef = cmdDef;
  }

  private void writeDataClear(PrintWriter pw, String indent) {
    String str = indent;
    str += Utility.makeFieldString( (String) fCmdDef.getReturnData().get("NAME"));
    str += ".clear();";
    pw.println(str);
  }

  private void writeDoCommand(PrintWriter pw, String indent) {
    String str = indent + "fCommandStatus = fUMart.do" + fCmdDef.getCmdName() +
        "(";
    str += Utility.makeFieldString( (String) fCmdDef.getReturnData().get("NAME"));
    str += ", fUserID";
    Iterator itr = fCmdDef.getArgList().iterator();
    while (itr.hasNext()) {
      HashMap argInfo = (HashMap) itr.next();
      String argName = Utility.makeFieldString( (String) argInfo.get("NAME"));
      str += ", " + argName;
    }
    str += ");";
    pw.println(str);
  }

  public void writeCommandClientSA() throws FileNotFoundException {
    String className = "UCC" + fCmdDef.getCmdName() + "SA";
    PrintWriter pw = new PrintWriter(new FileOutputStream(className + ".java"));
    System.out.println("Writing " + className + ".java ...");
    pw.println("package cmdClientSA;");
    pw.println();
    pw.println("import serverCore.UMart;");
    String coreClassName = "UC" + fCmdDef.getCmdName() + "Core";
    pw.println("import cmdCore." + coreClassName + ";");
    pw.println("import cmdCore.UCommandStatus;");
    pw.println();
    pw.println("public class " + className + " extends UC"
               + fCmdDef.getCmdName() + "Core implements IClientCmdSA {");
    pw.println();
    pw.println("  /** U-Martオブジェクトへの参照 */");
    pw.println("  private UMart fUMart;");
    pw.println();
    pw.println("  /** ユーザID */");
    pw.println("  private int fUserID;");
    pw.println();
    pw.println("  /** コンストラクタ */");
    pw.println("  " + className + "() {");
    pw.println("    super();");
    pw.println("    fUMart = null;");
    pw.println("    fUserID = -1;");
    pw.println("  }");
    pw.println();
    pw.println("  public void setConnection(UMart umart, int userID) {");
    pw.println("    fUMart = umart;");
    pw.println("    fUserID = userID;");
    pw.println("  }");
    pw.println();
    pw.println("  public UCommandStatus doIt() {");
    writeDataClear(pw, "    ");
    writeDoCommand(pw, "    ");
    pw.println("    return fCommandStatus;");
    pw.println("  }");
    pw.println();
    pw.println("}");
    pw.close();
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: java UCmdClientSAGenerator cmdfile");
      System.exit(0);
    }
    String cmdFile = args[0];
    UCmdDefinition cmdDef = new UCmdDefinition();
    cmdDef.readFrom(cmdFile);
    UCmdClientSAGenerator gen = new UCmdClientSAGenerator(cmdDef);
    try {
      gen.writeCommandClientSA();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }

  }
}
