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

public class UServerMethodGenerator {

  /** コマンド定義 */
  private UCmdDefinition fCmdDef;

  public UServerMethodGenerator(UCmdDefinition cmdDef) {
    fCmdDef = cmdDef;
  }

  private void writePuttingDataToHashMap(PrintWriter pw, String indent,
                                         HashMap item, String hashName) {
    String coreClassName = "UC" + fCmdDef.getCmdName() + "Core";
    String type = (String) item.get("TYPE");
    ArrayList contents = (ArrayList) item.get("CONTENTS");
    Iterator itr = contents.iterator();
    while (itr.hasNext()) {
      HashMap item2 = (HashMap) itr.next();
      String type2 = (String) item2.get("TYPE");
      String name2 = (String) item2.get("NAME");
      String localVariableName = Utility.makeLocalVariable(name2);
      String comment2 = (String) item2.get("COMMENT");
      if (type2.equals("HashMap")) {
        pw.println(indent + type2 + " " + localVariableName +
                   " = new HashMap();");
        writePuttingDataToHashMap(pw, indent, item2, localVariableName);
        String key = Utility.makeKeyString(type2, (String) item2.get("NAME"));
        pw.print(indent + hashName + ".put(" + coreClassName + "." + key + ", " +
                 localVariableName + ");");
        pw.println(" // " + comment2);
      } else if (type2.equals("ArrayList")) {
        pw.println(indent + type2 + " " + localVariableName +
                   " = new ArrayList();");
        writePuttingDataToArray(pw, indent, item2, localVariableName);
        String key = Utility.makeKeyString(type2, (String) item2.get("NAME"));
        pw.print(indent + hashName + ".put(" + coreClassName + "." + key + ", " +
                 localVariableName + ");");
        pw.println(" // " + comment2);
      } else {
        String key = Utility.makeKeyString(type2, name2);
        pw.println(indent + "// TODO " + comment2 + ": " + localVariableName +
                   " の設定");
        pw.print(indent +
                 Utility.makeStringForPuttingDataToHash(hashName, coreClassName,
            item2));
        pw.println(" // " + comment2);
      }
    }
  }

  private void writePuttingDataToArray(PrintWriter pw, String indent,
                                       HashMap item, String arrayName) {
    String coreClassName = "UC" + fCmdDef.getCmdName() + "Core";
    String type = (String) item.get("TYPE");
    HashMap contents = (HashMap) item.get("CONTENTS");
    String name = Utility.makeLocalVariable( (String) item.get("NAME"));
    String type2 = (String) contents.get("TYPE");
    String name2 = Utility.makeLocalVariable( (String) contents.get("NAME"));
    String comment2 = (String) contents.get("COMMENT");
    pw.println(indent + "{");
    if (type2.equals("HashMap")) {
      pw.println(indent + "  " + type2 + " " + name2 + " = new HashMap();");
      writePuttingDataToHashMap(pw, indent + "  ", contents, name2);
      String key = Utility.makeKeyString(type2, (String) contents.get("NAME"));
      pw.print(indent + "  " + arrayName + ".add(" + name2 + ");");
      pw.println(" // " + comment2);
    } else if (type2.equals("ArrayList")) {
      pw.println(indent + type2 + " " + name2 + " = new ArrayList();");
      writePuttingDataToArray(pw, indent, contents, name2);
      String key = Utility.makeKeyString(type2, (String) contents.get("NAME"));
      pw.print(indent + arrayName + ".add(" + name2 + ");");
      pw.println(" // " + comment2);
    } else {
      pw.println(indent + "// TODO " + comment2 + ": " + name2 + " の設定");
      pw.print(indent + "  " +
               Utility.makeStringForPuttingDataToArrayList(arrayName, contents));
      pw.println(" // " + comment2);
    }
    pw.println(indent + "}");
  }

  public void writeServerMethod() throws FileNotFoundException {
    String filename = "UC" + fCmdDef.getCmdName() + "ServerMethod.java";
    PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
    System.out.println("Writing " + filename + "...");
    String funcDef = "  public UCommandStatus do" + fCmdDef.getCmdName() + "(";
    funcDef += (String) fCmdDef.getReturnData().get("TYPE")
        + " " +
        Utility.makeLocalVariable( (String) fCmdDef.getReturnData().get("NAME"));
    funcDef += ", int userID";
    Iterator itr = fCmdDef.getArgList().iterator();
    while (itr.hasNext()) {
      HashMap argInfo = (HashMap) itr.next();
      String argType = (String) argInfo.get("TYPE");
      String argName = Utility.makeLocalVariable( (String) argInfo.get("NAME"));
      funcDef += ", " + argType + " " + argName;
    }
    funcDef += ") {";
    pw.println(funcDef);
    pw.println("    UCommandStatus ucs = new UCommandStatus();");
    pw.println("    try {");
    pw.println("      fStateLock.readLock();");
    pw.println("      if (!fCmdExecutableChecker.isExecutable(UC" +
               fCmdDef.getCmdName() + "Core.CMD_NAME, fStatus, userID)) {");
    pw.println("        ucs.setStatus(false);");
    pw.println("        ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);");
    pw.println(
        "        ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());");
    pw.println("        return ucs;");
    pw.println("      }");
    String type = (String) fCmdDef.getReturnData().get("TYPE");
    String fieldName = Utility.makeLocalVariable( (String) fCmdDef.
                                                 getReturnData().get("NAME"));
    if (type.equals("HashMap")) {
      writePuttingDataToHashMap(pw, "      ", fCmdDef.getReturnData(),
                                fieldName);
    } else if (type.equals("ArrayList")) {
      writePuttingDataToArray(pw, "      ", fCmdDef.getReturnData(), fieldName);
    }
    pw.println("      ucs.setStatus(true);");
    pw.println("    } catch (Exception e) {");
    pw.println("      e.printStackTrace();");
    pw.println("      System.exit(5);");
    pw.println("    } finally {");
    pw.println("      fStateLock.readUnlock();");
    pw.println("      return ucs;");
    pw.println("    }");
    pw.println("  }");
    pw.close();
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: java UServerMethodGenerator cmdfile");
      System.exit(0);
    }
    String cmdFile = args[0];
    UCmdDefinition cmdDef = new UCmdDefinition();
    cmdDef.readFrom(cmdFile);
    UServerMethodGenerator cmdGen = new UServerMethodGenerator(cmdDef);
    try {
      cmdGen.writeServerMethod();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }
}
