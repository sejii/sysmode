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

public class UCmdGen {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: java UCmdClientNetGenerator cmdDefFile");
      System.exit(0);
    }
    String cmdFile = args[0];
    UCmdDefinition cmdDef = new UCmdDefinition();
    cmdDef.readFrom(cmdFile);
    UCmdClientNetGenerator cmdClientNetGen = new UCmdClientNetGenerator(cmdDef);
    UCmdClientSAGenerator cmdClientSAGen = new UCmdClientSAGenerator(cmdDef);
    UCmdCoreGenerator cmdCoreGen = new UCmdCoreGenerator(cmdDef);
    UCmdServerGenerator cmdServerGen = new UCmdServerGenerator(cmdDef);
    UServerMethodGenerator serverMethodGen = new UServerMethodGenerator(cmdDef);
    try {
      cmdClientNetGen.writeCommandClientNet();
      cmdClientSAGen.writeCommandClientSA();
      cmdCoreGen.writeCommandCore();
      cmdServerGen.writeCommandServer();
      serverMethodGen.writeServerMethod();
      System.out.println("Done.");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }
}
