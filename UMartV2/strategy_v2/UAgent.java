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
package strategy_v2;

import java.util.ArrayList;
import java.util.HashMap;

import serverCore.UOrder;
import serverCore.UServerStatus;
import strategyCore.UBaseAgent;
import cmdCore.UCBalancesCore;
import cmdCore.UCFuturePriceCore;
import cmdCore.UCOrderRequestCore;
import cmdCore.UCPositionCore;
import cmdCore.UCSpotPriceCore;
import cmdCore.UCommandStatus;

/**
 * �S�Ă̐헪�N���X�̐e�N���X�ł��D
 */
public class UAgent extends UBaseAgent {

	/** �������i�n��̒��� */
  public static final int NO_OF_SPOT_PRICES = 120;

  /** �敨���i�n��̒��� */
  public static final int NO_OF_FUTURE_PRICES = 60;

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
  public UAgent(String loginName, String passwd, String realName, int seed) {
	  super(loginName, passwd, realName, seed);
	}

  /**
   * �������i�n���Ԃ��܂��D
   * @return �������i�n��
   */
  private int[] getSpotPrices() {
    UCSpotPriceCore cmd = (UCSpotPriceCore)fUmcp.getCommand(UCSpotPriceCore.CMD_NAME);
    cmd.setArguments("j30", UAgent.NO_OF_SPOT_PRICES);
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() + " in UAgent.getSpotPrices");
      System.exit(5);
    }
    ArrayList spotList = cmd.getResults();
    if (spotList.size() != UAgent.NO_OF_SPOT_PRICES) {
      System.err.println("spotList.size() != this.NO_OF_SPOT_PRICES in UAgent.doActions");
      System.exit(5);
    }
    int[] spotPrices = new int[UAgent.NO_OF_SPOT_PRICES];
    for (int i = 0; i < UAgent.NO_OF_SPOT_PRICES; ++i) {
      HashMap elem = (HashMap) spotList.get(i);
      spotPrices[UAgent.NO_OF_SPOT_PRICES - i - 1] = (int)((Long)elem.get(UCSpotPriceCore.LONG_PRICE)).longValue();
    }
    return spotPrices;
  }

  /**
   * �敨���i�n���Ԃ��܂��D
   * @return �敨���i
   */
  private int[] getFuturePrices() {
    UCFuturePriceCore cmd = (UCFuturePriceCore)fUmcp.getCommand(UCFuturePriceCore.CMD_NAME);
    cmd.setArguments("j30", UAgent.NO_OF_FUTURE_PRICES);
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() +
                         " in UAgent.getFuturePrices");
      System.exit(5);
    }
    ArrayList futureList = cmd.getResults();
    if (futureList.size() != UAgent.NO_OF_FUTURE_PRICES) {
      System.err.println("futureList.size():" + futureList.size() +
                         " != this.NO_OF_FUTURE_PRICES" + NO_OF_FUTURE_PRICES +
                         " in UAgent.getFuturePrices");
      System.exit(5);
    }
    int[] futurePrices = new int[UAgent.NO_OF_FUTURE_PRICES];
    for (int i = 0; i < UAgent.NO_OF_FUTURE_PRICES; ++i) {
      HashMap elem = (HashMap)futureList.get(i);
      futurePrices[UAgent.NO_OF_FUTURE_PRICES - i - 1] = (int)((Long)elem.get(UCFuturePriceCore.LONG_PRICE)).longValue();
    }
    return futurePrices;
  }

  /**
   * �|�W�V������Ԃ��܂��D
   * @return �|�W�V����
   */
  private int getPosition() {
    UCPositionCore cmd = (UCPositionCore)fUmcp.getCommand(UCPositionCore.CMD_NAME);
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID +
                         " in UAgent.getPosition");
      System.err.println(status.getErrorMessage() + " in UAgent.getPosition");
      System.exit(5);
    }
    HashMap result = cmd.getResults();
    long todayBuy = ( (Long) result.get(UCPositionCore.LONG_TODAY_BUY)).
        longValue();
    long todaySell = ( (Long) result.get(UCPositionCore.LONG_TODAY_SELL)).
        longValue();
    long yesterdayBuy = ( (Long) result.get(UCPositionCore.LONG_YESTERDAY_BUY)).
        longValue();
    long yesterdaySell = ( (Long) result.get(UCPositionCore.LONG_YESTERDAY_SELL)).
        longValue();
    long buy = todayBuy + yesterdayBuy;
    long sell = todaySell + yesterdaySell;
    return (int) (buy - sell);
  }

  /**
   * �����c����Ԃ��܂��D
   * @return �����c��
   */
  private long getMoney() {
    UCBalancesCore cmd = (UCBalancesCore)fUmcp.getCommand(UCBalancesCore.CMD_NAME);
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() + " in UAgent.getMoney");
      System.exit(5);
    }
    HashMap bal = cmd.getTodayResults();
    return ( (Long) bal.get(UCBalancesCore.LONG_SURPLUS)).longValue();
  }

  /**
   * @see strategyCore.UBaseAgent#doActions(int, int, int, int, int)
   */
  public void doActions(int day, int session, int serverState,
                         int maxDays, int noOfSessionsPerDay) {
    if (serverState != UServerStatus.ACCEPT_ORDERS) {
      return;
    }
    int[] spotPrices = getSpotPrices();
    int[] futurePrices = getFuturePrices();
    int position = getPosition();
    long money = getMoney();
    UOrderForm[] forms = makeOrderForms(day, session, maxDays, noOfSessionsPerDay,
                                        spotPrices, futurePrices, position, money);
    for (int i = 0; i < forms.length; ++i) {
    	orderRequest(forms[i]);
    }
  }

  /**
   * ������T�[�o�֒����𑗐M���܂��D
   * @param form �����[
   */
  protected void orderRequest(UOrderForm form) {
    UCOrderRequestCore cmd = (UCOrderRequestCore)fUmcp.getCommand(UCOrderRequestCore.CMD_NAME);
    int sellBuy;
    if (form.getBuySell() == UOrderForm.BUY) {
      sellBuy = UOrder.BUY;
    } else if (form.getBuySell() == UOrderForm.SELL) {
      sellBuy = UOrder.SELL;
    } else {
      return;
    }
    if (form.getPrice() <= 0 || form.getQuantity() <= 0) {
    	return;
    }
    cmd.setArguments("j30", UOrder.NEW, sellBuy, UOrder.LIMIT, form.getPrice(), form.getQuantity());
    UCommandStatus status = cmd.doIt();
    if (status.getStatus() == false) {
      System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
      System.err.println(status.getErrorMessage() + " in UAgent.orderRequest");
    }
  }

  /**
   * ���Z���܂ł̐ߐ����v�Z���܂��D
   * @param day ��
   * @param session ��
   * @param maxDays �������
   * @param noOfSessionsPerDay 1���̐ߐ�
   * @return�@���Z���܂ł̐ߐ�
   */
  public int calculateRestSessions(int day, int session,
                               int maxDays, int noOfSessionsPerDay) {
    return (maxDays - day) * noOfSessionsPerDay + noOfSessionsPerDay - session + 1;
  }

  /**
   * �����������߂̉��i��Ԃ��܂��D
   * @param prices ���i�n��Dprices[prices.length - 1]�����߁D
   * @return �����������߂̉��i
   */
  public int getLatestPrice(int[] prices) {
    for (int i = prices.length - 1; i >= 0; --i) {
      if (prices[i] >= 0) {
        return prices[i];
      }
    }
    return UOrderForm.INVALID_PRICE;
  }

  /**
   * �����[���쐬���܂��D
   * �f�t�H���g�ł́u�������Ȃ��v�����[��Ԃ������Ȃ̂ŁC�q�N���X�ŕK���I�[�o�[���C�h���Ă��������D
   * @param day ��
   * @param session ��
   * @param maxDays�@�������
   * @param noOfSessionsPerDay 1���̐ߐ�
   * @param spotPrices �������i�n��DspotPrices[0]����spotPrices[119]�܂ł�120�ߕ��̃f�[�^���i�[����Ă��܂��DspotPrices[119]�����߂̉��i�ł��D�������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��Ă��������D
   * @param futurePrices �敨���i�n��DfuturePrices[0]����futurePrices[59]�܂ł�60�ߕ��̃f�[�^���i�[����Ă��܂��DfuturePrices[59]�����߂̉��i�ł��D�������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��Ă��������D�܂��C����J�n�߂��O�͌������i���i�[����Ă��܂��D
   * @param position �|�W�V�����D���Ȃ�Δ����z��(�����O�E�|�W�V����)�C���Ȃ�Δ���z���i�V���[�g�E�|�W�V�����j��\���܂��D
   * @param money �����c���D�^��long�ł��邱�Ƃɒ��ӂ��Ă��������D
   * @return UOrderForm[] �����[�̔z��
   */
  public UOrderForm[] makeOrderForms(int day, int session,
  	                                  int maxDays, int noOfSessionsPerDay,
  		                                int[] spotPrices, int[] futurePrices,
  		                                int position, long money) {
  	UOrderForm[] forms = new UOrderForm [1];
  	forms[0] = new UOrderForm();
  	return forms;
  }

}
