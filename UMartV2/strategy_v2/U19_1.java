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
 * 独自エージェント用のテンプレートです．
 * このエージェントのコピーを作成して，名前をU<学籍番号>に修正して下さい．
 */
public class U19_1 extends UAgent {

	//ここにフィールド変数を定義してください．

	
	private static final double SELL_PARAM = 2;
	private static final double BUY_PARAM = 2;
	
  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	
	public U19_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//ここでフィールド変数の値を初期化してください．
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
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + ", future=" + futurePrices[futurePrices.length-1]);

		// 注文区分を「何もしない」に設定した注文票を作成して返す．
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
   * エージェントのシステムパラメータを設定します．
   * @param args システムパラメータ
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    //このメソッドをオーバーライドする必要はありません．
  }
}
