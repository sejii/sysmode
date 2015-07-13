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

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import cmdCore.*;
import serverCore.*;

public class UServerManagerMembersInfoTablePanel extends UPanel {
  /** テーブルのヘッダ */
  Object[] fMemberTableHeaderData = {" ", " ", "MemberID", "Sell", "Buy",
      "TotalSell", "TotalBuy", "Cash", "UnrealizedProfit", "Margin",
      "SumOfFee", "Loan", "SumOfInterest", "RealizedProfit",
      "InitialCash", "TradingUnit", "FeePerUnit", "MarginRate",
      "MaxLoan", "Interest"};

  Vector fMemberTableHeader = new Vector();

  Vector fMemberInformation = new Vector();

  UTable fMemberTable = new UTable();

  JScrollPane fMemberTablePane = new JScrollPane();

  ImageIcon fGreenBallImg = new ImageIcon(getClass().getResource(
      "/resources/images/GreenBall.gif"));

  ImageIcon fRedBallImg = new ImageIcon(getClass().getResource(
      "/resources/images/RedBall.gif"));

  ImageIcon fWhiteBallImg = new ImageIcon(getClass().getResource(
      "/resources/images/WhiteBall.gif"));

  public UServerManagerMembersInfoTablePanel() {
    fName = "SERVER_BOARD_TABLE";
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * interface IGUIObserver を持つものは UGUIUpdateManager の addDayObserver か
   * addStepObserver を呼ぶ必要あり．すべてのUpanelで必要．
   */
  public void addUpdateObserverList() {
    UGUIUpdateManager.addAlwaysObserver(this);
  }

  private void jbInit() throws Exception {
    this.setLayout(null);
    fMemberTablePane.getViewport().setBackground(Color.white);
    fMemberTablePane.setBounds(new Rectangle(6, 11, 661, 504));
    fMemberTablePane.setBorder(BorderFactory.createRaisedBevelBorder());
    fMemberTablePane.getViewport().setBackground(Color.white);
    this.add(fMemberTablePane, null);
    for (int i = 0; i < fMemberTableHeaderData.length; i++) {
      fMemberTableHeader.add(fMemberTableHeaderData[i]);
    }
    fMemberTable = new UTable(new Vector(), fMemberTableHeader);
    fMemberTablePane.getViewport().add(fMemberTable, null);
    // 以下，表の各列の幅の設定
    fMemberTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    fMemberTable.getColumnModel().getColumn(0).setPreferredWidth(15); // Status
    fMemberTable.getColumnModel().getColumn(1).setPreferredWidth(15); // Attribute
    fMemberTable.getColumnModel().getColumn(2).setPreferredWidth(150); // MemberID
    fMemberTable.getColumnModel().getColumn(3).setPreferredWidth(35); // Sell
    fMemberTable.getColumnModel().getColumn(4).setPreferredWidth(35); // Buy
    fMemberTable.getColumnModel().getColumn(5).setPreferredWidth(55); // Total
    // Sell
    fMemberTable.getColumnModel().getColumn(6).setPreferredWidth(55); // Total
    // Buy
    fMemberTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Cash
    fMemberTable.getColumnModel().getColumn(8).setPreferredWidth(90); // Unrealized
    // profit
    fMemberTable.getColumnModel().getColumn(9).setPreferredWidth(90); // Margin
    fMemberTable.getColumnModel().getColumn(10).setPreferredWidth(80); // Sum
    // of
    // fee
    fMemberTable.getColumnModel().getColumn(11).setPreferredWidth(80); // Loan
    fMemberTable.getColumnModel().getColumn(12).setPreferredWidth(80); // Sum
    // of
    // Interest
    fMemberTable.getColumnModel().getColumn(13).setPreferredWidth(90); // Profit
    fMemberTable.getColumnModel().getColumn(14).setPreferredWidth(90); // Initial
    // Cash
    fMemberTable.getColumnModel().getColumn(15).setPreferredWidth(80); // TradingUnit
    fMemberTable.getColumnModel().getColumn(16).setPreferredWidth(80); // FeePerUnit
    fMemberTable.getColumnModel().getColumn(17).setPreferredWidth(80); // MarginRate
    fMemberTable.getColumnModel().getColumn(18).setPreferredWidth(80); // MaxLoan
    fMemberTable.getColumnModel().getColumn(19).setPreferredWidth(80); // Interest
  }

  public void dataUpdate() {
    UCExchangeProfileCore cExchangeProfile = (UCExchangeProfileCore) fCProtocol
        .getCommand(UCExchangeProfileCore.CMD_NAME);
    cExchangeProfile.doIt();
    HashMap hmep = cExchangeProfile.getData();
    if (hmep.isEmpty()) {
      return;
    }
    int id, maxMember;
    maxMember = ( (Integer) hmep
                 .get(UCExchangeProfileCore.INT_NO_OF_MEMBERS)).intValue();
    fMemberInformation.clear();
    for (id = 1; id <= maxMember; id++) {
      UCMemberProfileCore cMemberProfile = (UCMemberProfileCore) fCProtocol
          .getCommand(UCMemberProfileCore.CMD_NAME);
      cMemberProfile.setArguments(id);
      cMemberProfile.doIt();
      HashMap hmmp = cMemberProfile.getData();
      HashMap hmToday = (HashMap) hmmp
          .get(UCMemberProfileCore.HASHMAP_TODAY_BALANCE);
      HashMap hmYesterday = (HashMap) hmmp
          .get(UCMemberProfileCore.HASHMAP_YESTERDAY_BALANCE);
      HashMap hmPosition = (HashMap) hmmp
          .get(UCMemberProfileCore.HASHMAP_POSITION);
      Vector tmpData = new Vector();
      int status = ( (Integer) hmmp.get(UCMemberProfileCore.INT_STATUS))
          .intValue();
      int noOfLoginAgents = ( (Integer) hmmp
                             .get(UCMemberProfileCore.INT_NO_OF_LOGIN_AGENTS))
          .intValue();
      if (status == UAccount.UNAVAILABLE) {
        tmpData.add(fRedBallImg);
      } else if (noOfLoginAgents > 0) {
        tmpData.add(fGreenBallImg);
      } else {
        tmpData.add(fWhiteBallImg);
      }
      String attribute = (String) hmmp
          .get(UCMemberProfileCore.STRING_ATTRIBUTE);
      if (attribute.equals("Human")) {
        tmpData.add("H");
      } else if (attribute.equals("Machine")) {
        tmpData.add("M");
      } else {
        System.err.println(attribute);
        System.exit(5);
      }
      //tmpData.add((String)hmmp.get(UCMemberProfileCore.STRING_LOGIN_NAME));
      tmpData.add(new UStringNumberElement( (String) hmmp
                                           .get(UCMemberProfileCore.
                                                STRING_LOGIN_NAME)));
      // 今まで Long だったのを UFormatLong に変更。by mori
      tmpData.add(new UFormatLong( (Long) hmPosition
                                  .get(UCMemberProfileCore.
                                       LONG_TODAY_SELL_POSITIONS)));
      tmpData.add(new UFormatLong( (Long) hmPosition
                                  .get(UCMemberProfileCore.
                                       LONG_TODAY_BUY_POSITIONS)));
      tmpData
          .add(new UFormatLong(
          (Long) hmPosition
          .get(UCMemberProfileCore.LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY)));
      tmpData
          .add(new UFormatLong(
          (Long) hmPosition
          .get(UCMemberProfileCore.LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY)));
      tmpData.add(new UFormatLong( (Long) hmToday
                                  .get(UCMemberProfileCore.LONG_CASH)));
      tmpData.add(new UFormatLong( (Long) hmToday
                                  .get(UCMemberProfileCore.
                                       LONG_UNREALIZED_PROFIT)));
      tmpData.add(new UFormatLong( (Long) hmToday
                                  .get(UCMemberProfileCore.LONG_MARGIN)));
      tmpData.add(new UFormatLong( (Long) hmToday
                                  .get(UCMemberProfileCore.LONG_SUM_OF_FEE)));
      tmpData.add(new UFormatLong( (Long) hmToday
                                  .get(UCMemberProfileCore.LONG_LOAN)));
      tmpData.add(new UFormatLong( (Long) hmToday
                                  .get(UCMemberProfileCore.LONG_SUM_OF_INTEREST)));
      tmpData.add(new UFormatLong( (Long) hmToday
                                  .get(UCMemberProfileCore.LONG_PROFIT)));
      tmpData.add(new UFormatLong( (Long) hmmp
                                  .get(UCMemberProfileCore.LONG_INITIAL_CASH)));
      tmpData.add(new UFormatLong( (Long) hmmp
                                  .get(UCMemberProfileCore.LONG_TRADING_UNIT)));
      tmpData.add(new UFormatLong( (Long) hmmp
                                  .get(UCMemberProfileCore.LONG_FEE_PER_UNIT)));
      tmpData.add(new UFormatLong( (Long) hmmp
                                  .get(UCMemberProfileCore.LONG_MARGIN_RATE)));
      tmpData.add(new UFormatLong( (Long) hmmp
                                  .get(UCMemberProfileCore.LONG_MAX_LOAN)));
      tmpData.add( (Double) hmmp.get(UCMemberProfileCore.DOUBLE_INTEREST));
      fMemberInformation.add(tmpData);
      // セルの横幅がもとに戻らないようにする
      fMemberTable.setAutoCreateColumnsFromModel(false);

      //以下はお好みで変更よろしく。by mori
      //TODO デフォルトの昇順、降順、例えばの例。あまり使いやすくないけど、滅多に使わないのでどうしても必要があればということで。
      //ちなみになにもしないと全部true(昇順). この例は数値関係のみ降順。でも最後は昇順。
      boolean ascend[] = {true, true, true, false, false, false, false,
          false, false, false, false, false, false, false, false,
          false, false, false, false, true};
      fMemberTable.setDefaultAscending(ascend);
      //左端をソートできないようにする。
      fMemberTable.setSortableAt(0, false);
    }
  }

  /* UStringNumberElement に移動
      protected UTwoPartsElement stringToUTwoPartsElement(String s) {
          try {
              //p0342001 が p342001 と表示されてしまう．ソートの性質を保つために，先頭の0は文字列の方に
              //含める．
   String pre = s.replaceAll("[1-9]+[0-9]*\\b", ""); // 最後の数字部分を消す．先頭は0以外
   String post = s.replaceAll("^.*\\D0*", ""); // 最初の数字以外部分を消す．先頭の0は含む．
              if (post.equals("")) {
                  //数字部分がない場合は第二部分はなし
                  return new UTwoPartsElement(pre, "");
              } else {
                  long num = Long.valueOf(post).longValue();
                  return new UTwoPartsElement(pre, num);
              }
          } catch (Exception ex) {
              ex.printStackTrace();
              return new UTwoPartsElement(s, "");
          }
      }
   */
  public void gUpdate() {
    if (!isVisible()) {
      return;
    }
    IGUIEvent iev = new IGUIEvent() {
      public void execute() {
        dataUpdate();
      }

      public Runnable getRunnableForInvokeLater() {
        return new Runnable() {
          public void run() {
            fMemberTable.setModel(new DefaultTableModel(
                fMemberInformation, fMemberTableHeader));
            repaint();
          }
        };
      }
    };
    UGUIEventQueue.getInstance().addIGUIEvent(iev);
  }

}