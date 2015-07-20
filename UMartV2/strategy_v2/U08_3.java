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
 * 独自エージェント用のテンプレートです．
 * このエージェントのコピーを作成して，名前をU<学籍番号>に修正して下さい．
 */
public class U08_3 extends UAgent {

	//ここにフィールド変数を定義してください．
	private int fCalculateTerm;							// 移動平均を計算する節数
	private double fPreviousMovingAverage;	// 1節前の移動平均
	private int fMaxPosition;								// ポジションの最大値

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U08_3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//ここでフィールド変数の値を初期化してください．
		fCalculateTerm = 8;
		fPreviousMovingAverage = (double)UOrderForm.INVALID_PRICE;
		fMaxPosition = 300;
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

		// 現在の日，現在の節，現在の現物価格を表示する．
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]);

		// 注文区分を「何もしない」に設定した注文票を作成して返す．
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		forms[0].setBuySell(UOrderForm.NONE);

		// 現在の移動平均値を計算
		double movingAverage;
		movingAverage = calculateMovingAverage(futurePrices, fCalculateTerm);

		// 現在の移動平均値と1節前の移動平均値から注文区分を決める
		if (fPreviousMovingAverage != UOrderForm.INVALID_PRICE
				&& movingAverage != UOrderForm.INVALID_PRICE) {

			// (現在の移動平均値) > (1節前の移動平均値) -> BUY
			// (現在の移動平均値) < (1節前の移動平均値) -> SELL
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

		// 現在のポジションがポジションの最大値を超えている場合
		// 注文なしに変更して注文表を返す
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

		// 注文価格の決定
		// BUY -> (現在の先物価格) + 5
		// SELL -> (現在の先物価格) - 5
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
   * エージェントのシステムパラメータを設定します．
   * @param args システムパラメータ
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //このメソッドをオーバーライドする必要はありません．
  }

  /**
   * 移動平均を計算する
   * @param prices 価格時系列
   * @param term 移動平均を計算する期間
   */
  private double calculateMovingAverage(int prices[], int term) {
  	double sum = 0.0;
  	for (int i = 0; i < term; i++) {
  		// 計算期間の価格時系列に無効な価格が入っていた場合は無効な価格を返す
  		if (prices[prices.length - 1 - i] == UOrderForm.INVALID_PRICE) {
  			return (double)UOrderForm.INVALID_PRICE;
  		}
  		sum += (double)prices[prices.length - 1 - i];
  	}
  	return sum / (double)term;
  }
}