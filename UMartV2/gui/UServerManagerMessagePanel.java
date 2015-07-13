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
import javax.swing.event.*;
import javax.swing.text.*;

import strategyCore.UBaseAgent;

public class UServerManagerMessagePanel extends UPanel implements
    IOutputObserver {

  UUserChooser fUserChooser = new UUserChooser();

  JScrollPane fScrollPane = new JScrollPane();

  HashMap fTextAreas = new HashMap();

  public UServerManagerMessagePanel() {
    super();
    fName = fRb.getString("STDOUT");
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    UParameters.fByteArrayOutputStream.setOutputObserver(this);
    UParameters.fByteArrayOutputStream.setName("STDOUT");
    System.setOut(UParameters.fPrintStream);
    fUserChooser.setBounds(10, 5, 670, 95);
    fUserChooser.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent event) {
        fScrollPane.getViewport().add( (JTextArea) fTextAreas.get(fUserChooser.
            getTargetUserName()), null);
      }
    });
    add(fUserChooser);
    fScrollPane.setBounds(7, 100, 670, 400);
    add(fScrollPane, null);
    JTextArea textArea = new JTextArea();
    textArea.setEditable(false);
    fTextAreas.put("STDOUT", textArea);
    fUserChooser.appendUser("STDOUT");
    ArrayList agentArray = UParameters.getInstance().getMachineAgentArray();
    Iterator itr = agentArray.iterator();
    while (itr.hasNext()) {
      UBaseAgent agent = (UBaseAgent) itr.next();
      String name = agent.getLoginName();
      UByteArrayOutputStream os = (UByteArrayOutputStream) agent.
          getOutputStream();
      os.setOutputObserver(this);
      textArea = new JTextArea();
      textArea.setEditable(false);
      fTextAreas.put(name, textArea);
      fUserChooser.appendUser(name);
    }
    fScrollPane.getViewport().add( (JTextArea) fTextAreas.get(fUserChooser.
        getTargetUserName()), null);
  }

  public void update(UByteArrayOutputStream bos) {
    JTextArea textArea = (JTextArea) fTextAreas.get(bos.getName());
    String s = bos.toString();
    textArea.append(s);
    Document doc = textArea.getDocument();
    Position last = doc.getEndPosition();
    int pos = last.getOffset();
    textArea.getCaret().setDot(pos);
  }

}