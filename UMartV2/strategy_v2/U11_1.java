package strategy_v2;


/* N節前と(N-M)節前の現物価格を比較して意思決定するエージェント*/

public class U11_1 extends UAgent {

  public static final int DEFAULT_MAX_QUANT = 200;			// 注文数量の最大値のデフォルト値
  public static final int DEFAULT_MIN_QUANT = 10;			// 注文数量の最小値のデフォルト値
  public static final int DEFAULT_MAX_POSITION = 800;	// 売/買ポジションの最大値のデフォルト値
  public static final int DEFAULT_DELAY_N = 1;				// 「N節前の現物価格を参照する」のNのデフォルト値
  public static final int DEFAULT_DELAY_M = 3;				// 「N節前の現物価格を参照する」のNのデフォルト値

  private int fMaxQuant = DEFAULT_MAX_QUANT;
  private int fMinQuant = DEFAULT_MIN_QUANT;
  private int fMaxPosition = DEFAULT_MAX_POSITION;
  private int fDelayN = DEFAULT_DELAY_N;
  private int fDelayM = DEFAULT_DELAY_M;

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U11_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

  /**
   * 注文票を作成します．
   * @param day 日
   * @param session 節
   * @param maxDays 取引日数
   * @param noOfSessionsPerDay 1日の節数
   * @param spotPrices 現物価格系列．spotPrices[0]からspotPrices[119]までの120節分のデータが格納されている．spotPrices[119]が直近の価格です．ただし，価格が成立していない場合，-1が入っているので注意してください．
   * @param futurePrices 先物価格系列．futurePrices[0]からfuturePrices[59]までの60節分のデータが格納されています．futurePrices[59]が直近の価格です．ただし，価格が成立していない場合，-1が入っているので注意してください．また，取引開始節より前は現物価格が格納されています．
   * @param position ポジション．正ならば買い越し(ロング・ポジション)，負ならば売り越し（ショート・ポジション）を表します．
   * @param money 現金残高．型がlongであることに注意してください．
   * @return UOrderForm[] 注文票の配列
   */
	public UOrderForm[] makeOrderForms(int day, int session,
                                      int maxDays, int noOfSessionsPerDay,
                                      int[] spotPrices, int[] futurePrices,
                                      int position, long money) {

		// 注文区分を「何もしない」に設定した注文票を作成して返す．
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
    forms[0].setBuySell(UOrderForm.NONE);

    // 現在の日，現在の節，現在の現物価格を表示する．
    println("");
 		print("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]  + ", Position=" + position);

    int difference = spotPrices[spotPrices.length - 1 - fDelayN] - spotPrices[spotPrices.length - 1 - fDelayN - fDelayM];
    // 注文区分を決定する
    if(difference > 0){
    	forms[0].setBuySell(UOrderForm.BUY);
    }else if(difference < 0){
    	forms[0].setBuySell(UOrderForm.SELL);
    }else{
    	forms[0].setBuySell(UOrderForm.NONE);
    	return forms;
    }

    //ポジション管理
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

    // 注文価格が現在の先物価格の+-20%に収まるかを考慮し，注文価格を決定
    if(Math.abs(spotPrices[spotPrices.length - 1] - futurePrices[futurePrices.length - 1]) < futurePrices[futurePrices.length - 1] * 0.2){
    	forms[0].setPrice(spotPrices[spotPrices.length - 1]);
    }else{
    	if(forms[0].getBuySell() == UOrderForm.BUY){
    		forms[0].setPrice((int)(futurePrices[futurePrices.length - 1] * 1.199));
    	}else if(forms[0].getBuySell() == UOrderForm.SELL){
    		forms[0].setPrice((int)(futurePrices[futurePrices.length - 1] * 0.801));
    	}
    }

    // 注文数量を決定する
    // 80円上下した時，数量はfMaxQuantまで指数関数的に増加
    double index = Math.pow((double)(fMaxQuant-fMinQuant), 1.0/80.0);
    if(fMinQuant + Math.pow(index, Math.abs(difference)) < fMaxQuant){
    	forms[0].setQuantity((int)(fMinQuant + Math.pow(index, Math.abs(difference))));
    }else{
    	forms[0].setQuantity(fMaxQuant);
    }

		// 注文区分，価格，数量を出力する
		print(" => Action=" + forms[0].getBuySellByString() + ", Price=" + forms[0].getPrice() + ", Quantity=" + forms[0].getQuantity());

    return forms;
  }

  /**
   * エージェントのシステムパラメータを設定します．
   * @param args システムパラメータ
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //このメソッドをオーバーライドする必要はありません．
  }
}
