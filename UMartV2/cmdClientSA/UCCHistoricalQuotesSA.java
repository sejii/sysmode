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
package cmdClientSA;

import cmdCore.*;
import serverCore.*;

/**
 * ���[�J���œ��삷��N���C�A���g�����p����C�ߋ��̓����ł̉��i���ڂ��擾���邽�߂�SVMP�R�}���h�N���X�ł��D
 */
public class UCCHistoricalQuotesSA extends UCHistoricalQuotesCore implements
    IClientCmdSA {

  /** �T�[�o�[�ւ̎Q�� */
  private UMart fUMart;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * �R���X�g���N�^�ł��D
   */
  public UCCHistoricalQuotesSA() {
    super();
    fUMart = null;
    fUserID = -1;
  }

  /**
   * @see cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fQuotesArray.clear();
    fStatus = fUMart.doHistoricalQuotes(fQuotesArray, fBrandName, fNoOfDays);
    return fStatus;
  }

  /**
   * @see cmdClientSA.IClientCmdSA#setConnection(UMart, int)
   */
  public void setConnection(UMart umart, int userID) {
    fUMart = umart;
    fUserID = userID;
  }
}