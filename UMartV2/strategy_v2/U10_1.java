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

import java.util.Random;


/**
 * �Ǝ��G�[�W�F���g�p�̃e���v���[�g�ł��D
 * ���̃G�[�W�F���g�̃R�s�[���쐬���āC���O��U<�w�Дԍ�>�ɏC�����ĉ������D
 */
public class U10_1 extends UAgent {

	//�����Ƀt�B�[���h�ϐ����`���Ă��������D

	 public static final int DEFAULT_WIDTH_OF_PRICE = 50;//20;
	 public static final int DEFAULT_MAX_QUANT = 10;//50;
	 public static final int DEFAULT_MIN_QUANT = 1;//10;
	 public static final int DEFAULT_MAX_POSITION = 100;//300;
	 public static final int DEFAULT_NOMINAL_PRICE = 2000;//3000;

	 private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE ;
	 private int fMaxQuant = DEFAULT_MAX_QUANT ;
	 private int fMinQuant = DEFAULT_MIN_QUANT ;
	 private int fMaxPosition = DEFAULT_MAX_POSITION ;
	 private int fNominalPrice = DEFAULT_NOMINAL_PRICE ;

	 public static final String WIDTH_OF_PRICE_KEY = " WidthOfPrice " ;
	 public static final String MAX_QUANT_KEY = " MaxQuant ";
	 public static final String MIN_QUANT_KEY = " MinQuant ";
	 public static final String MAX_POSITION_KEY = " MaxPosition " ;
	 public static final String NOMINAL_PRICE_KEY = " NominalPrice ";


  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U10_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//�����Ńt�B�[���h�ϐ��̒l�����������Ă��������D
		/*public static final String [] PARAMETERS = { " WidthOfPrice =50" ,
			 " MinQuant =1" ,
			 " MaxQuant =10" ,
			 " MaxPosition =100" ,
			 " NominalPrice =2000" };*/

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
		// println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]);

		 Random rand = getRandom ();
		 UOrderForm [] forms = new UOrderForm [1];
		 forms [0] = new UOrderForm ();
		 forms [0]. setBuySell ( rand . nextInt (2) + 1);
		 if ( forms [0]. getBuySell () == UOrderForm . BUY ) {
		 if ( position > fMaxPosition ) {
		 forms [0]. setBuySell ( UOrderForm . NONE );
		 return forms ;
		 }
		 } else if ( forms [0]. getBuySell () == UOrderForm . SELL ) {
		 if ( position < - fMaxPosition ) {
		 forms [0]. setBuySell ( UOrderForm . NONE );
		 return forms ;
		 }
		 }
		 int latestPrice = getLatestPrice ( futurePrices );
		 if ( latestPrice == UOrderForm . INVALID_PRICE ) {
		 latestPrice = getLatestPrice ( spotPrices );
		 }
		 if ( latestPrice == UOrderForm . INVALID_PRICE ) {
		 latestPrice = fNominalPrice ;
		 }
		 int price = latestPrice + ( int )(( double ) fWidthOfPrice * rand . nextGaussian ());
		 if ( price <= 0) {
		 price = 1;
		 }
		 forms [0]. setPrice ( price );
		 forms [0]. setQuantity ( fMinQuant + rand . nextInt ( fMaxQuant - fMinQuant + 1));
		 println ( "day =" + day + ", session =" + session  + ", spot=" +  spotPrices[spotPrices.length - 1]
		 /*+ " , latestPrice =" + latestPrice + " , " + forms [0]. getBuySellByString ()*/
		 + ", futures =" + forms [0]. getPrice ()
		 /*+ " , quantity =" + forms [0]. getQuantity ()*/);
		 return forms ;


		// �����敪���u�������Ȃ��v�ɐݒ肵�������[���쐬���ĕԂ��D
		/*UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
    forms[0].setBuySell(UOrderForm.NONE);
    return forms;*/
  }

  /**
   * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肵�܂��D
   * @param args �V�X�e���p�����[�^
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //���̃��\�b�h���I�[�o�[���C�h����K�v�͂���܂���D
    for ( int i = 0; i < args . length ; ++ i ) {
    	 String [] strArray = args [i ]. split ( "=" );
    	 String key = strArray [0];
    	 String value = strArray [1];
    	 if ( key . equals ( URandomAgent . WIDTH_OF_PRICE_KEY )) {
    	 fWidthOfPrice = Integer . parseInt ( value );
    	 println (" WidthOfPrice has been changed to " + fWidthOfPrice );
    	 } else if ( key . equals ( URandomAgent . MIN_QUANT_KEY )) {
    	 fMinQuant = Integer . parseInt ( value );
    	 println (" MinQuant has been changed to " + fMinQuant );
    	 } else if ( key . equals ( URandomAgent . MAX_QUANT_KEY )) {
    	 fMaxQuant = Integer . parseInt ( value );
    	 println (" MaxQuant has been changed to " + fMaxQuant );
    	 } else if ( key . equals ( URandomAgent . MAX_POSITION_KEY )) {
    	 fMaxPosition = Integer . parseInt ( value );
    	 println (" MaxPosition has been changed to " + fMaxPosition );
    	 } else if ( key . equals ( URandomAgent . NOMINAL_PRICE_KEY )) {
    	 fNominalPrice = Integer . parseInt ( value );
    	 println (" NominalPrice has been changed to " + fNominalPrice );
    	 } else {
    	 println (" Unknown parameter :" + key + " in URandomAgent . setParameters " );
    	 }
    	}
  }
}
