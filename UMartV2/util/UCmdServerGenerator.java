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

public class UCmdServerGenerator {

  /** コマンド定義 */
  private UCmdDefinition fCmdDef;

  /**
   * コンストラクタ
   *
   */
  public UCmdServerGenerator(UCmdDefinition cmdDef) {
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
    str += ", userID";
    Iterator itr = fCmdDef.getArgList().iterator();
    while (itr.hasNext()) {
      HashMap argInfo = (HashMap) itr.next();
      String argName = Utility.makeFieldString( (String) argInfo.get("NAME"));
      str += ", " + argName;
    }
    str += ");";
    pw.println(str);
  }

  private void writeSendingHashMap(PrintWriter pw, String indent, HashMap item,
                                   String hashName) {
    String coreClassName = "UC" + fCmdDef.getCmdName() + "Core";
    String type = (String) item.get("TYPE");
    ArrayList contents = (ArrayList) item.get("CONTENTS");
    Iterator itr = contents.iterator();
    while (itr.hasNext()) {
      HashMap item2 = (HashMap) itr.next();
      String type2 = (String) item2.get("TYPE");
      String name2 = (String) item2.get("NAME");
      String key2 = Utility.makeKeyString(type2, name2);
      String comment2 = (String) item2.get("COMMENT");
      String localVariableName = Utility.makeLocalVariable(name2);
      if (type2.equals("HashMap")) {
        pw.print(indent + "HashMap " + localVariableName
                 + " = (HashMap)" + hashName + ".get("
                 + coreClassName + "." + key2 + ");");
        pw.println(" // " + comment2);
        writeSendingHashMap(pw, indent, item2, localVariableName);
      } else if (type2.equals("ArrayList")) {
        pw.print(indent + "ArrayList " + localVariableName
                 + " = (ArrayList)" + hashName + ".get("
                 + coreClassName + "." + key2 + ");");
        pw.println(" // " + comment2);
        writeSendingArrayList(pw, indent, item2, localVariableName);
      } else {
        String str = indent + "fAgent.sendMessage(" + hashName + ".get(";
        str += "UC" + fCmdDef.getCmdName() + "Core." + key2 + ").toString());";
        pw.print(str);
        pw.println(" // " + comment2);
      }
    }
  }

  private void writeSendingArrayList(PrintWriter pw, String indent,
                                     HashMap item, String arrayName) {
    String type = (String) item.get("TYPE");
    HashMap contents = (HashMap) item.get("CONTENTS");
    String name = Utility.makeLocalVariable( (String) item.get("NAME"));
    String type2 = (String) contents.get("TYPE");
    String name2 = Utility.makeLocalVariable( (String) contents.get("NAME"));
    String comment2 = (String) contents.get("COMMENT");
    pw.println(indent + "Iterator itr = " + arrayName + ".iterator();");
    pw.println(indent + "while ( itr.hasNext() ) {");
    if (type2.equals("HashMap")) {
      pw.println(indent + "  HashMap " + name2 + " = " + "(HashMap)itr.next();");
      writeSendingHashMap(pw, indent, contents, name2);
    } else if (type2.equals("ArrayList")) {
      pw.print(indent + "  ArrayList " + name2 + " = (ArrayList)itr.next();");
      writeSendingArrayList(pw, indent, contents, name2);
    } else {
      pw.print(indent + "  fAgent.sendMassage(" + name2 + ".toString());");
      pw.println(" // " + comment2);
    }
    pw.println(indent + "}");
  }

  public void writeCommandServer() throws FileNotFoundException {
    String className = "USC" + fCmdDef.getCmdName();
    PrintWriter pw = new PrintWriter(new FileOutputStream(className + ".java"));
    System.out.println("Writing " + className + ".java ...");
    pw.println("package cmdServer;");
    pw.println();
    pw.println("import java.util.ArrayList;");
    pw.println("import java.util.HashMap;");
    pw.println("import java.util.Iterator;");
    pw.println("import serverNet.UAgent;");
    pw.println("import serverNet.UMartNetwork;");
    pw.println("import cmdCore.ICommand;");
    String coreClassName = "UC" + fCmdDef.getCmdName() + "Core";
    pw.println("import cmdCore." + coreClassName + ";");
    pw.println("import cmdCore.UCommandStatus;");
    pw.println();
    pw.println("public class " + className + " extends UC"
               + fCmdDef.getCmdName() + "Core implements IServerCmd {");
    pw.println();
    pw.println("  /** このコマンドを実行したエージェント */");
    pw.println("  private UAgent fAgent;");
    pw.println();
    pw.println("  /** サーバーへの参照 */");
    pw.println("  UMartNetwork fUMart;");
    pw.println();
    pw.println("  /** コンストラクタ */");
    pw.println("  public " + className + "() {");
    pw.println("    super();");
    pw.println("    fAgent = null;");
    pw.println("    fUMart = null;");
    pw.println("  }");
    pw.println();
    pw.println(
        "  public void setConnection(UAgent agent, UMartNetwork umart) {");
    pw.println("    fAgent = agent;");
    pw.println("    fUMart = umart;");
    pw.println("  }");
    pw.println();
    pw.println("  public UCommandStatus doIt() {");
    pw.println("    try {");
    pw.println("      int userID = fAgent.getOffice().getUserID();");
    writeDataClear(pw, "      ");
    writeDoCommand(pw, "      ");
    pw.println("      if ( fCommandStatus.getStatus() ) {");
    pw.println("        fAgent.sendMessage(\"+ACCEPT\");");
    String type = (String) fCmdDef.getReturnData().get("TYPE");
    String fieldName = Utility.makeFieldString( (String) fCmdDef.getReturnData().
                                               get("NAME"));
    if (type.equals("HashMap")) {
      writeSendingHashMap(pw, "        ", fCmdDef.getReturnData(), fieldName);
    } else if (type.equals("ArrayList")) {
      writeSendingArrayList(pw, "        ", fCmdDef.getReturnData(), fieldName);
    }
    pw.println("      } else {");
    pw.println(
        "        fAgent.sendMessage(\"+ERROR \" + fCommandStatus.getErrorCode());");
    pw.println("        fAgent.sendMessage(fCommandStatus.getErrorMessage());");
    pw.println("      }");

    pw.println("    } catch (Exception e) {");
    pw.println(
        "      fAgent.sendMessage(\"+ERROR \" + ICommand.INVALID_ARGUMENTS);");
    pw.println("      fAgent.sendMessage(\"USAGE: " +
               fCmdDef.getCmdName().toUpperCase() + "\");");
    pw.println("    }");
    pw.println("    fAgent.flushMessage();");
    pw.println("    return fCommandStatus;");
    pw.println("  }");
    pw.println();
    pw.println("}");
    pw.close();
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: java UCmdServerGenerator cmdfile");
      System.exit(0);
    }
    String cmdFile = args[0];
    UCmdDefinition cmdDef = new UCmdDefinition();
    cmdDef.readFrom(cmdFile);
    UCmdServerGenerator gen = new UCmdServerGenerator(cmdDef);
    try {
      gen.writeCommandServer();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }
}
