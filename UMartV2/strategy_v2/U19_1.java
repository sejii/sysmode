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
import java.util.Random;

/**
 * �Ǝ��G�[�W�F���g�p�̃e���v���[�g�ł��D
 * ���̃G�[�W�F���g�̃R�s�[���쐬���āC���O��U<�w�Дԍ�>�ɏC�����ĉ������D
 */
public class U19_1 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D

	
	private static final double SELL_PARAM = 2;
	private static final double BUY_PARAM = 2;
	
  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	
	public U19_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//�����Ńt�B�[���h�ϐ��̒l�����������Ă��������D
		loginName = "h19_1";
		passwd = "";
		realName = "Daiki Sato";
		seed = 1;
		
	}


	private ArrayList<Integer> getValidPrices(int[] prices){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(int i = prices.length - 1; i >= 0; --i){
			if(prices[i] >= 0){
				ret.add(prices[i]);
			}
		}
		return ret;
	}


	private int determinePrice(int action, int[] prices){
		Random rand = getRandom();
		int differenceOfPrice = Math.abs(prices[prices.length -1] - prices[prices.length -2]);
		int price = 0;
		if(action == UOrderForm.BUY){
			price = prices[prices.length - 1]
						+ (differenceOfPrice + (int) (((double)differenceOfPrice) / 4.0 * rand.nextGaussian()));
			if(price <= 0){
				price = 1;
			}
		} else if(action == UOrderForm.SELL){
			price = prices[prices.length - 1]
						- (differenceOfPrice + (int) (((double)differenceOfPrice) / 4.0 * rand.nextGaussian()));
			if(price <= 0){
				price = 1;
			}
		}
		return price;
	}
	
	private int chooseAction(ArrayList<Integer> validPrices, int[] acc){
		if (validPrices.size() < 3) { return UOrderForm.NONE; }
		else{
			
			int fp = validPrices.get(0);
			int sp = validPrices.get(1);
			int tp = validPrices.get(2);
			
			acc[0] = (fp - sp) - (sp - tp);
			System.out.println(acc[0]);
			if(acc[0] > 0){
				System.out.println("buy");
				return UOrderForm.BUY;
			}

			else if(acc[0] < 0){
				System.out.println("sell");
				return UOrderForm.SELL;
			}
			else{
				return UOrderForm.NONE;
			}
		}
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
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + ", future=" + futurePrices[futurePrices.length-1]);

		// �����敪���u�������Ȃ��v�ɐݒ肵�������[���쐬���ĕԂ��D
		UOrderForm[] forms = new UOrderForm[1];
		//forms[0] = new UOrderForm();
		UOrderForm f = new UOrderForm();
		f.setBuySell(UOrderForm.NONE);

		ArrayList<Integer>vp = new ArrayList<Integer>();
		vp = getValidPrices(futurePrices);
		int[] acc = new int[1];
		int status = UOrderForm.NONE;
		status = chooseAction(vp, acc);
				
		if(status == UOrderForm.NONE){ ; }
		else if(status == UOrderForm.BUY) {
			f.setBuySell(UOrderForm.BUY);
			//int p = 2*vp.get(0) - vp.get(1)/4;
			//if(p < 1){ p = 1;}
			//if(p < vp.get(0)*0.8){ p =(int)(vp.get(0)*0.9); }
			//if(p > vp.get(0)*1.2){ p = (int)(vp.get(0)*1.1); }
			f.setPrice(determinePrice(f.getBuySell(), futurePrices));
			f.setQuantity((int)(BUY_PARAM * acc[0]));
		}
		else{
			f.setBuySell(UOrderForm.SELL);
			//int p = 2*vp.get(0) - vp.get(1)/4;
			//if(p < 1){ p = 1;}
			//if(p < vp.get(0)*0.8){ p =(int)(vp.get(0)*0.9); }
			//if(p > vp.get(0)*1.2){ p = (int)(vp.get(0)*1.1); }
			f.setPrice(determinePrice(f.getBuySell(), futurePrices));
			f.setQuantity((int)(SELL_PARAM * (-acc[0])));
		}
		
		forms[0] = f;
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
}
