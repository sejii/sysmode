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

public class U17_2 extends UAgent {

	/** 短期の節数のデフォルト値 */
	public static final int DEFAULT_SHORT_TERM = 8;

	/** 中期の節数のデフォルト値 */
	public static final int DEFAULT_MEDIUM_TERM = 16;

	/** 注文量の最大値のデフォルト値 */
  public static final int DEFAULT_MAX_QUANT = 100;

  /** 注文量の最小値のデフォルト値 */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** ポジションの最大値のデフォルト値 */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** 取引所から得られる全ての価格情報が無効のときに使う価格のデフォルト値 */
  public static final int DEFAULT_NOMINAL_PRICE = 2200;

  /** 1節前からの現在の節価格の変化率が大幅下落の場合 */
  //private static final int CASE_PRICE_LARGE_FALL = 0;
  /** 1節前からの現在の節価格の変化率が下落の場合 */
  private static final int CASE_PRICE_FALL = 0;
  /** 1節前からの現在の節価格の変化率が無変化の場合 */
  private static final int CASE_PRICE_NOT_CHANGE = 1;
  /** 1節前からの現在の節価格の変化率が上昇の場合 */
  private static final int CASE_PRICE_RISE = 2;
  /** 1節前からの現在の節価格の変化率が大幅下落の場合 */
  //private static final int CASE_PRICE_LARGE_RISE = 4;

  /** 用意した場合の数 */
  private static final int CASE_NUMBER = 3;

  /** 価格変化が大幅下落の場合の変化率の最小値閾値 */
  //private static final double THRESHOLD_LARGE_FALL = -3.0;
  /** 価格変化が下落の場合の変化率の最小値閾値 */
  private static final double THRESHOLD_FALL = -0.3;
  /** 価格変化が無変化の場合の変化率の最小値閾値 */
  private static final double THRESHOLD_NOT_CHANGE = 0.3;
  /** 価格変化が上昇の場合の変化率の最小値閾値 */
  //private static final double THRESHOLD_RISE = 3.0;

  /** 乱数生成器 */
  private Random fRandom;

  /** 1節前の現物価格 */
  private int prevspotPrice = DEFAULT_NOMINAL_PRICE;

  /** 1節前の先物価格 */
  private int prevfuturePrice = DEFAULT_NOMINAL_PRICE;

  /** 短期の節数 */
	private int fShortTerm = DEFAULT_SHORT_TERM;

	/** 中期の節数 */
	private int fMediumTerm = DEFAULT_MEDIUM_TERM;

  /** 注文量の最大値 */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** 注文量の最小値 */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** ポジションの最大値 */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** 取引所から得られる全ての価格情報が向こうのときに使う価格 */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

  /** 1節前における短期移動平均値 */
	private double fPreviousShortTermMovingAverage = UOrderForm.INVALID_PRICE;

	/** 1節前における中期移動平均値 */
	private double fPreviousMediumTermMovingAverage = UOrderForm.INVALID_PRICE;

	/** 1節前における現物価格の変化率 */
	private double[] prevspotpricechange = new double[3];

	/** 1節前の価格の変化率と現在の現物価格の変化率の場合の数をカウント*/
	private int[][] pre1changecounter = new int[3][3];

	/** 2節前の価格の変化率と現在の現物価格の変化率の場合の数をカウント*/
	private int[][] pre2changecounter = new int[3][3];

	/** 2節前の価格の変化率と現在の現物価格の変化率の場合の数をカウント*/
	private int[][] pre3changecounter = new int[3][3];

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U17_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		for (int i = 0;i < CASE_NUMBER; i++){
			for (int j = 0;j < CASE_NUMBER; j++){
				pre1changecounter[i][j] = 0;
				pre2changecounter[i][j] = 0;
				pre3changecounter[i][j] = 0;
			}
		}
		for (int i = 0;i < prevspotpricechange.length; i++){
			prevspotpricechange[i] = 0;
		}
	}

	/**
	 * 短期の節数を返します．
	 * @return 短期の節数
	 */
	public int getShortTerm() {
		return fShortTerm;
	}

	/**
	 * 中期の節数を返します．
	 * @return 中期の節数
	 */
	public int getMediumTerm() {
		return fMediumTerm;
	}

	/**
	 * 最小注文数量を返します．
	 * @return 最小注文数量
	 */
	public int getMinQuant() {
		return fMinQuant;
	}

	/**
	 * 最大注文数量を返します．
	 * @return 最大注文数量
	 */
	public int getMaxQuant() {
		return fMaxQuant;
	}

	/**
	 * 最大ポジションを返します．
	 * @return 最大ポジション
	 */
	public int getMaxPosition() {
		return fMaxPosition;
	}

	/**
	 * 市場価格が未定のときの注文価格を返します．
	 * @return 市場価格が未定のときの注文価格
	 */
	public int getNominalPrice() {
		return fNominalPrice;
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
		double spotpricechange = (getLatestPrice(spotPrices)-prevspotPrice)*100.0/prevspotPrice;
		// 5節以降であれば変化率のカウントを開始
		if((day-1)*noOfSessionsPerDay + session > 4){
			int state = determinChangeCase(spotpricechange);
			int pre1state =determinChangeCase(prevspotpricechange[0]);
			int pre2state =determinChangeCase(prevspotpricechange[1]);
			int pre3state =determinChangeCase(prevspotpricechange[2]);
			pre1changecounter[pre1state][state] += 1;
			pre2changecounter[pre2state][state] += 1;
			pre3changecounter[pre3state][state] += 1;

			double[] predictnextstate= {1.0,1.0,1.0};
			//3節前からの変化率を用いて次の変化を予想
			for( int i = 0; i < CASE_NUMBER; i++){
				predictnextstate[i] *= calcurateProbability(pre1changecounter,i,state);
			}
			for( int i = 0; i < CASE_NUMBER; i++){
				predictnextstate[i] *= calcurateProbability(pre2changecounter,i,pre1state);
			}
			for( int i = 0; i < CASE_NUMBER; i++){
				predictnextstate[i] *= calcurateProbability(pre3changecounter,i,pre1state);
			}
			println("変化率" + spotpricechange + "%");
			println("下落" + predictnextstate[0] + "変わらない" + predictnextstate[1] + "上昇" + predictnextstate[2]);
		}

		// 現在の日，現在の節，現在の現物価格を表示する．
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + "future=" + futurePrices[futurePrices.length -1]);
    // 1節前の現物価格を取得する
    prevspotPrice = getLatestPrice(spotPrices);
    // 1節前の先物価格を取得する
    prevfuturePrice = getLatestPrice(futurePrices);
    if (prevfuturePrice == -1) {
      prevfuturePrice = UOrderForm.INVALID_PRICE;
    }
    double tmp = spotpricechange;
    for (int i=0; i<3; i++){
    	prevspotpricechange[i] = tmp;
    	tmp = prevspotpricechange[i];
    }
    // 注文区分を「何もしない」に設定した注文票を作成して返す．
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
    forms[0].setBuySell(UOrderForm.NONE);
    return forms;
  }

	private double calcurateProbability(int[][] counter,int state, int prestate){
		int sum = 0;
		for (int i=0;i<CASE_NUMBER; i++){
			sum += counter[prestate][i];
		}
		if(sum == 0){
			return 1.0;
		}
		return (double)counter[prestate][state]/sum;
	}

	/**
	 * 価格変化率から「下落」か「変化なし」か「上昇」か場合分けする
	 * @param pricechange
	 * @return 場合
	 */
	private int determinChangeCase(double pricechange){
		int state = CASE_PRICE_NOT_CHANGE;
		if (pricechange < THRESHOLD_FALL){
			state = CASE_PRICE_FALL;
		}else if (pricechange < THRESHOLD_NOT_CHANGE){
			state = CASE_PRICE_NOT_CHANGE;
		}else{
			state = CASE_PRICE_RISE;
		}
		return state;
	}

	 /**
   * 注文価格を決定して返します．
   * @param action 売買区分
   * @param prices 価格系列．ただし，prices[prices.length]を直近としてください．
   * @return 注文価格
   */
  private int determinePrice(int action, int[] prices) {
  	int price = UOrderForm.INVALID_PRICE;
  	int widthOfPrice = Math.abs(prices[prices.length - 1] - prices[prices.length - 2]);
  	if (action == UOrderForm.BUY) {
      price = prices[prices.length - 1] + widthOfPrice + (int)((double)widthOfPrice / 4.0 * getRandom().nextGaussian());
    } else if (action == UOrderForm.SELL) {
      price = prices[prices.length - 1] - widthOfPrice + (int)((double)widthOfPrice / 4.0 * getRandom().nextGaussian());
    }
  	if (price < 0) {
  		price = 1;
  	}
  	return price;
  }

  /**
   * 売買区分を選んで返します．
   * @param prices 価格系列．ただし，prices[prices.length]を直近としてください．
   * @return 売買区分
   */
  private int chooseAction(int[] prices) {
  	int action = UOrderForm.NONE;
  	double shortTermMovingAverage = calculateMovingAverage(prices, fShortTerm);
  	double mediumTermMovingAverage = calculateMovingAverage(prices, fMediumTerm);
  	if (fPreviousShortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& fPreviousMediumTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& shortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& mediumTermMovingAverage != UOrderForm.INVALID_PRICE) {
  		if (fPreviousShortTermMovingAverage < fPreviousMediumTermMovingAverage
  				&& mediumTermMovingAverage < shortTermMovingAverage) {
  			action = UOrderForm.BUY;
  		} else if (fPreviousMediumTermMovingAverage < fPreviousShortTermMovingAverage
  				        && shortTermMovingAverage < mediumTermMovingAverage) {
  			action = UOrderForm.SELL;
  		}
  	}
  	fPreviousShortTermMovingAverage = shortTermMovingAverage;
  	fPreviousMediumTermMovingAverage = mediumTermMovingAverage;
  	return action;
  }

  /**
   * 直近からterm節分の価格系列の移動平均値を計算して返します．
   * ただし，価格が成立していない場合は，UOrderForm.INVALID_PRICE (=-1)を返します．
   * @param prices 価格系列．ただし，prices[prices.length - 1]を直近としてください．
   * @param term 移動平均をとる期間
   * @return 直近からterm節分の価格系列の移動平均値
   */
  private double calculateMovingAverage(int[] prices, int term) {
  	double sum = 0.0;
  	for (int i = 0; i < term; ++i) {
  		if (prices[prices.length - 1 - i] < 0) {
  			return (double)UOrderForm.INVALID_PRICE;
  		}
  		sum += (double)prices[prices.length - 1 - i];
  	}
  	return sum / (double)term;
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
