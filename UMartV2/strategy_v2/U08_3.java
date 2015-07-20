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


/**
 * �Ǝ��G�[�W�F���g�p�̃e���v���[�g�ł��D
 * ���̃G�[�W�F���g�̃R�s�[���쐬���āC���O��U<�w�Дԍ�>�ɏC�����ĉ������D
 */
public class U08_3 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D
	private int fCalculateTerm;							// �ړ����ς��v�Z����ߐ�
	private double fPreviousMovingAverage;	// 1�ߑO�̈ړ�����
	private int fMaxPosition;								// �|�W�V�����̍ő�l

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U08_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//�����Ńt�B�[���h�ϐ��̒l�����������Ă��������D
		fCalculateTerm = 8;
		fPreviousMovingAverage = (double)UOrderForm.INVALID_PRICE;
		fMaxPosition = 300;
	}

  /**
   * �����[���쐬���܂��D
   * @param day ��
   * @param session ��
   * @param maxDays �������
   * @param noOfSessionsPerDay 1���̐ߐ�
   * @param spotPrices �������i�n��DspotPrices[0]����spotPrices[119]�܂ł�120�ߕ��̃f�[�^���i�[����Ă���DspotPrices[119]�����߂̉��i�ł��D�������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��Ă��������D
   * @param futurePrices �敨���i�n��DfuturePrices[0]����futurePrices[59]�܂ł�60�ߕ��̃f�[�^���i�[����Ă��܂��DfuturePrices[59]�����߂̉��i�ł��D�������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��Ă��������D�܂��C����J�n�߂��O�͌������i���i�[����Ă��܂��D
   * @param position �|�W�V�����D���Ȃ�Δ����z��(�����O�E�|�W�V����)�C���Ȃ�Δ���z���i�V���[�g�E�|�W�V�����j��\���܂��D
   * @param money �����c���D�^��long�ł��邱�Ƃɒ��ӂ��Ă��������D
   * @return UOrderForm[] �����[�̔z��
   */
	public UOrderForm[] makeOrderForms(int day, int session,
                                      int maxDays, int noOfSessionsPerDay,
                                      int[] spotPrices, int[] futurePrices,
                                      int position, long money) {

		// ���݂̓��C���݂̐߁C���݂̌������i��\������D
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]);

		// �����敪���u�������Ȃ��v�ɐݒ肵�������[���쐬���ĕԂ��D
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		forms[0].setBuySell(UOrderForm.NONE);

		// ���݂̈ړ����ϒl���v�Z
		double movingAverage;
		movingAverage = calculateMovingAverage(futurePrices, fCalculateTerm);

		// ���݂̈ړ����ϒl��1�ߑO�̈ړ����ϒl���璍���敪�����߂�
		if (fPreviousMovingAverage != UOrderForm.INVALID_PRICE
				&& movingAverage != UOrderForm.INVALID_PRICE) {

			// (���݂̈ړ����ϒl) > (1�ߑO�̈ړ����ϒl) -> BUY
			// (���݂̈ړ����ϒl) < (1�ߑO�̈ړ����ϒl) -> SELL
			if (fPreviousMovingAverage < movingAverage) {
				forms[0].setBuySell(UOrderForm.BUY);
			} else if (fPreviousMovingAverage > movingAverage) {
				forms[0].setBuySell(UOrderForm.SELL);
			} else {
				return forms;
			}
		} else {
			fPreviousMovingAverage = movingAverage;
			return forms;
		}

		// ���݂̃|�W�V�������|�W�V�����̍ő�l�𒴂��Ă���ꍇ
		// �����Ȃ��ɕύX���Ē����\��Ԃ�
		if (forms[0].getBuySell() == UOrderForm.BUY) {
			if (position > fMaxPosition) {
				forms[0].setBuySell(UOrderForm.NONE);
				fPreviousMovingAverage = movingAverage;
				return forms;
			}
		} else if (forms[0].getBuySell() == UOrderForm.SELL) {
			if (position < -fMaxPosition) {
				forms[0].setBuySell(UOrderForm.NONE);
				fPreviousMovingAverage = movingAverage;
				return forms;
			}
		}

		// �������i�̌���
		// BUY -> (���݂̐敨���i) + 5
		// SELL -> (���݂̐敨���i) - 5
		if (forms[0].getBuySell() == UOrderForm.BUY) {
			forms[0].setPrice(futurePrices[futurePrices.length - 1] + 5);
		} else if (forms[0].getBuySell() == UOrderForm.SELL) {
			forms[0].setPrice(futurePrices[futurePrices.length - 1] - 5);
		}
		if (forms[0].getPrice() <= 0) {
			forms[0].setPrice(1);
		}

		forms[0].setQuantity(10);

		fPreviousMovingAverage = movingAverage;
    return forms;
  }

  /**
   * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肵�܂��D
   * @param args �V�X�e���p�����[�^
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //���̃��\�b�h���I�[�o�[���C�h����K�v�͂���܂���D
  }

  /**
   * �ړ����ς��v�Z����
   * @param prices ���i���n��
   * @param term �ړ����ς��v�Z�������
   */
  private double calculateMovingAverage(int prices[], int term) {
  	double sum = 0.0;
  	for (int i = 0; i < term; i++) {
  		// �v�Z���Ԃ̉��i���n��ɖ����ȉ��i�������Ă����ꍇ�͖����ȉ��i��Ԃ�
  		if (prices[prices.length - 1 - i] == UOrderForm.INVALID_PRICE) {
  			return (double)UOrderForm.INVALID_PRICE;
  		}
  		sum += (double)prices[prices.length - 1 - i];
  	}
  	return sum / (double)term;
  }
}