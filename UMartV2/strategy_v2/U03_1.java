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
 * 独自エージェント用のテンプレートです．
 * このエージェントのコピーを作成して，名前をU<学籍番号>に修正して下さい．
 */
public class U03_1 extends UAgent {

	//ここにフィールド変数を定義してください．
	private static final int noOfMovingAverage = 3;	//状態として利用する先物価格や現物価格の移動平均値の数．*2+1で状態xの数になる．
	private static final int noOfTerms = 4;	//移動平均の計算に用いる節数
	private static final int noOfMiddleNode = 8;	//中間層のノード数．
	private static final double DEFAULT_ALPHA = 0.1;	//Qの学習率のデフォルト値
	private static final double DEFAULT_GAMMA = 0.8;	//Qの割引率のデフォルト値
	private static final double DEFAULT_ETA = 0.05;	//重みwの学習率のデフォルト値
	private static final double DEFAULT_GAIN_A = 0.001; //シグモイド関数のゲインのデフォルト値
	private static final double DEFAULT_GAIN_B = 10.0;
	private static final double DEFAULT_REWARDGAIN = 100.0;	//報酬の倍率のデフォルト値
	private static final int DEFAULT_MAX_QUANT = 100;	//最大注文数量のデフォルト値
	private static final int DEFAULT_PRICE_WIDTH = 50;	//価格幅のデフォルト値
	
	private Random fRandom;
	private double[] fX;	//状態（position,pf[t],pf[t-1],pf[t-2],pr[t],pr[t-1],pr[t-2]）．noOfMovingAverage*2+1個の要素数になる．
	private double[] fMiddleNodeIn;	//中間層に入る線形和
	private double[] fMiddleNodeOut; //中間層の出力（シグモイド関数を通した値）
	private double[] fOutputNode;	//出力層．
	private double[] fQ;	//状態xでの各行動の評価値．出力層にシグモイド関数を通した値になる．
	private double[][] fWa;	//fWa[i][j]:中間層iへの入力jからの重み
	private double[][] fWb;	//fWb[i][j]:出力i(買い，売り，注文なしの3つ)への中間層jからの重み．
	private int fActionOld;	//1回前に選択した行動．Qの更新に用いる．
	private long fMoneyOld;	//1回前の現金残高．
	private double fAlpha;	//Q'の学習率．
	private double fGamma;	//Q'の割引率．
	private double fEta;	//重みの学習率．
	private double fGainA;	//シグモイド関数のゲイン．中間層の出力用．
	private double fGainB;	//シグモイド関数のゲイン．出力層の出力用．
	private double fRewardGain;	//報酬のゲイン．
	private int fMaxQuant;	//最大注文数量．
	private int fPriceWidth;	//価格幅．
	
	private static double calculateMovingAverage(int[] prices, int term, int old) {
		int noOfSum = 0;
		double sum = 0.0;
		for(int i = 0; i < term; i++) {
			if(prices[prices.length - 1 - old*term - i] >= 0) {
				noOfSum++;
				sum += (double)prices[prices.length - 1 - old*term -  i];
			}
		}
		if(noOfSum == 0) {
			return UOrderForm.INVALID_PRICE;
		}
		return sum / (double)noOfSum;
	}
	
	private double sigmoid(double x, double gain) {
		return 1.0 / (1.0 + Math.exp(- gain * x));
	}
	
	/**
	 * 出力層や中間層への線形和を計算する．
	 * @param w 重み
	 * @param input 入力
	 * @return
	 */
	private static double calculateLinearSum(double[] w, double[] input) {
		double sum = 0;
		for(int i = 0; i < input.length; i++) {
			sum += w[i] * input[i];
		}
		return sum;
	}
	
	/**
	 * 状態fXに基づいて，中間層，出力層，Q値を計算する．
	 */
	private void calculateNeuralNetwork() {
		for(int i = 0; i < fMiddleNodeIn.length; i++) {
			fMiddleNodeIn[i] = calculateLinearSum(fWa[i], fX);
		}
		for(int i = 0; i < fMiddleNodeOut.length; i++) {
			fMiddleNodeOut[i] = sigmoid(fMiddleNodeIn[i], fGainA);
		}
		for(int i = 0; i < fOutputNode.length; i++) {
			fOutputNode[i] = calculateLinearSum(fWb[i], fMiddleNodeOut);
		}
		for(int i = 0; i < fQ.length; i++) {
			fQ[i] = sigmoid(fOutputNode[i], fGainB);
		}
	}
	
	/**
	 * Q値に基づいて行動をルーレット選択する．
	 * @return 行動番号 UorderForm.NONE,SELL,BUY
	 */
	private int chooseAction() {
		int action = UOrderForm.NONE;
		//ルーレット選択
		double sumOfQ = 0.0;
		for(int i = 0; i < fQ.length; i++) {
			sumOfQ += fQ[i];
		}
		double randomDouble = fRandom.nextDouble() * sumOfQ;
		double tmpDouble = 0.0;
		for(int i = 0; i < fQ.length; i++) {
			tmpDouble += fQ[i];
			if(randomDouble < tmpDouble) {
				action = i;
				break;
			}
		}
		return action;
	}
	
	private void updateX(int position, int[] spotPrices, int[] futurePrices) {
		fX[0] = position;
		for(int i = 0; i < noOfMovingAverage; i++) {
			fX[i + 1] = calculateMovingAverage(futurePrices, noOfTerms, i);
			fX[noOfMovingAverage + i + 1] = calculateMovingAverage(spotPrices, noOfTerms, i);
		}
	}
	
	private double calculateReward(long money) {
		if(fMoneyOld == UOrderForm.INVALID_PRICE || money == UOrderForm.INVALID_PRICE || fMoneyOld == 0) {
			return 0;
		}
		return fRewardGain*((double)money / fMoneyOld - 1.0);
	}
	
	/**
	 * qTargetとの誤差が小さくなるように，1回前に選択した行動に関わる重みを更新する．
	 * @param action 1回前に選択した行動
	 * @param qTarget Q値の目標値
	 */
	private void updateWOf(int action, double qTarget) {
		double gradient;
		final double gradC = 2.0 * (fQ[action] - qTarget) * fGainB * (1.0 - fQ[action]) * fQ[action]; 
		for(int i = 0; i < fWa.length; i++) {
			for(int j = 0; j < fWa[i].length; j++) {
				gradient = gradC * fGainA * (1.0 - fMiddleNodeOut[i])*fMiddleNodeOut[i] * fWb[action][i]*fX[j];
				fWa[i][j] = fWa[i][j] - fEta * gradient;
			}
		}
		for(int j = 0; j < fWb[action].length; j++) {
			gradient = gradC * fMiddleNodeOut[j];
			fWb[action][j] = fWb[action][j] - fEta * gradient;
		}
	}
	
	private int determinePrice(int[] futurePrices, int[] spotPrices, int action) {
		int price = UOrderForm.INVALID_PRICE;
		if(futurePrices[futurePrices.length - 1] != UOrderForm.INVALID_PRICE) {
			price = futurePrices[futurePrices.length - 1];
		} else if(spotPrices[spotPrices.length - 1] != UOrderForm.INVALID_PRICE) {
			price = spotPrices[spotPrices.length - 1];
		}
		return (int)Math.round(price + fPriceWidth * (fQ[action] - 0.5));
	}
	
	private int determineQuantity(int action) {
		return (int)Math.round(fMaxQuant * fQ[action]);
	}
	
	private double getMaxQ() {
		double tmp = Math.max(fQ[UOrderForm.NONE], fQ[UOrderForm.SELL]);
		return Math.max(tmp, fQ[UOrderForm.BUY]);
	}

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U03_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//ここでフィールド変数の値を初期化してください．
		fRandom = getRandom();
		fX = new double[noOfMovingAverage * 2 + 1];
		for(int i = 0; i < fX.length; i++) {
			fX[i] = UOrderForm.INVALID_PRICE;
		}
		fMiddleNodeIn = new double[noOfMiddleNode];
		fMiddleNodeOut = new double[noOfMiddleNode];
		fOutputNode = new double[3];
		fQ = new double[fOutputNode.length];
		fWa = new double[fMiddleNodeIn.length][fX.length];
		for(int i = 0; i < fWa.length; i++) {
			for(int j = 0; j < fWa[i].length; j++) {
				fWa[i][j] = fRandom.nextDouble() - 0.5;
			}
		}
		fWb = new double[fOutputNode.length][fMiddleNodeOut.length];
		for(int i = 0; i < fWb.length; i++) {
			for(int j = 0; j < fWb[i].length; j++) {
				fWb[i][j] = fRandom.nextDouble() - 0.5;
			}
		}
		fActionOld = UOrderForm.NONE;
		fMoneyOld = UOrderForm.INVALID_PRICE;
		fAlpha = DEFAULT_ALPHA;
		fGamma = DEFAULT_GAMMA;
		fEta = DEFAULT_ETA;
		fGainA = DEFAULT_GAIN_A;
		fGainB = DEFAULT_GAIN_B;
		fRewardGain = DEFAULT_REWARDGAIN;
		fMaxQuant = DEFAULT_MAX_QUANT;
		fPriceWidth = DEFAULT_PRICE_WIDTH;
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

		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		
		// 1回前に選んだ行動にもとづいてニューラルネットワークの重みを更新する．
		final double qOld = fQ[fActionOld];	//1回前に選択した行動のQ値．
		final double reward = calculateReward(money);	//現金残高の差を報酬とする．
		updateX(position, spotPrices, futurePrices);	//状態xの更新．
		calculateNeuralNetwork();	//現在の状態xと更新前の重みから中間層，出力層，Q値を計算する．
		final double qTarget = (1 - fAlpha)*qOld + fAlpha*(reward + fGamma*getMaxQ());// Q'値(Q値の目標値)の計算
		updateWOf(fActionOld, qTarget);	//1回前に選択した行動に関わる重みを更新する．
		
		// 注文区分の決定．
		calculateNeuralNetwork();	//現在の状態xと更新後の重みから中間層，出力層，Q値を計算する．
		int action = chooseAction();	//Q値にもとづいて注文区分を選択する．
		forms[0].setBuySell(action);	
		
		//注文価格の決定．
		forms[0].setPrice(determinePrice(futurePrices, spotPrices, action));
		
		//注文数量の決定．
		forms[0].setQuantity(determineQuantity(action));
		
		fActionOld = action;
		fMoneyOld = money;
		
		// 現在の日，現在の節，現在の現物価格を表示する．
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + ", future=" + futurePrices[futurePrices.length - 1]
					+ ", action=" + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice() + ", quantity=" + forms[0].getQuantity() + ", reward=" + reward + ", position=" + position);
		//println(arrayString("x=", fX));
		//println(arrayString("Mid=", fMiddleNodeOut));
		//println(arrayString("Out=", fOutputNode));
		//println(arrayString("Q=", fQ));
		//println(qOld + ", " + qTarget + "," + fQ[fActionOld]);
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
  
  public static String arrayString(String start, double[] src) {
  	String str = start;
  	str += src[0];
 		for(int i = 1; i < src.length; i++) {
  		str += "," + src[i];
  	}
  	return str;
  }
}
