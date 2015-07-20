package strategy_v2;


/* N�ߑO��(N-M)�ߑO�̌������i���r���Ĉӎv���肷��G�[�W�F���g*/

public class U11_1 extends UAgent {

  public static final int DEFAULT_MAX_QUANT = 200;			// �������ʂ̍ő�l�̃f�t�H���g�l
  public static final int DEFAULT_MIN_QUANT = 10;			// �������ʂ̍ŏ��l�̃f�t�H���g�l
  public static final int DEFAULT_MAX_POSITION = 800;	// ��/���|�W�V�����̍ő�l�̃f�t�H���g�l
  public static final int DEFAULT_DELAY_N = 1;				// �uN�ߑO�̌������i���Q�Ƃ���v��N�̃f�t�H���g�l
  public static final int DEFAULT_DELAY_M = 3;				// �uN�ߑO�̌������i���Q�Ƃ���v��N�̃f�t�H���g�l

  private int fMaxQuant = DEFAULT_MAX_QUANT;
  private int fMinQuant = DEFAULT_MIN_QUANT;
  private int fMaxPosition = DEFAULT_MAX_POSITION;
  private int fDelayN = DEFAULT_DELAY_N;
  private int fDelayM = DEFAULT_DELAY_M;

  /**
   * �R���X�g���N�^�ł��D
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public U11_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
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

		// �����敪���u�������Ȃ��v�ɐݒ肵�������[���쐬���ĕԂ��D
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
    forms[0].setBuySell(UOrderForm.NONE);

    // ���݂̓��C���݂̐߁C���݂̌������i��\������D
    println("");
 		print("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]  + ", Position=" + position);

    int difference = spotPrices[spotPrices.length - 1 - fDelayN] - spotPrices[spotPrices.length - 1 - fDelayN - fDelayM];
    // �����敪�����肷��
    if(difference > 0){
    	forms[0].setBuySell(UOrderForm.BUY);
    }else if(difference < 0){
    	forms[0].setBuySell(UOrderForm.SELL);
    }else{
    	forms[0].setBuySell(UOrderForm.NONE);
    	return forms;
    }

    //�|�W�V�����Ǘ�
    if (forms[0].getBuySell() == UOrderForm.BUY) {
      if (position > fMaxPosition) {
        forms[0].setBuySell(UOrderForm.NONE);
        return forms;
      }
    } else if (forms[0].getBuySell() == UOrderForm.SELL) {
      if (position < -fMaxPosition) {
        forms[0].setBuySell(UOrderForm.NONE);
        return forms;
      }
    }

    // �������i�����݂̐敨���i��+-20%�Ɏ��܂邩���l�����C�������i������
    if(Math.abs(spotPrices[spotPrices.length - 1] - futurePrices[futurePrices.length - 1]) < futurePrices[futurePrices.length - 1] * 0.2){
    	forms[0].setPrice(spotPrices[spotPrices.length - 1]);
    }else{
    	if(forms[0].getBuySell() == UOrderForm.BUY){
    		forms[0].setPrice((int)(futurePrices[futurePrices.length - 1] * 1.199));
    	}else if(forms[0].getBuySell() == UOrderForm.SELL){
    		forms[0].setPrice((int)(futurePrices[futurePrices.length - 1] * 0.801));
    	}
    }

    // �������ʂ����肷��
    // 80�~�㉺�������C���ʂ�fMaxQuant�܂Ŏw���֐��I�ɑ���
    double index = Math.pow((double)(fMaxQuant-fMinQuant), 1.0/80.0);
    if(fMinQuant + Math.pow(index, Math.abs(difference)) < fMaxQuant){
    	forms[0].setQuantity((int)(fMinQuant + Math.pow(index, Math.abs(difference))));
    }else{
    	forms[0].setQuantity(fMaxQuant);
    }

		// �����敪�C���i�C���ʂ��o�͂���
		print(" => Action=" + forms[0].getBuySellByString() + ", Price=" + forms[0].getPrice() + ", Quantity=" + forms[0].getQuantity());

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
