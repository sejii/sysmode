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
public class U17_2 extends UAgent {
	
	/** 注文価格の幅のデフォルト値 */
  public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** 注文量の最大値のデフォルト値 */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** 注文量の最小値のデフォルト値 */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** ポジションの最大値のデフォルト値 */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** 取引所から得られる全ての価格情報が無効のときに使う価格のデフォルト値 */
  public static final int DEFAULT_NOMINAL_PRICE = 2200;

  /** 乱数生成器 */
  private Random fRandom;
  
  /** 1節前の先物価格 */
  private int prevPrice = DEFAULT_NOMINAL_PRICE;

  /** 注文価格の幅 */
  private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE;

  /** 注文量の最大値 */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** 注文量の最小値 */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** ポジションの最大値 */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** 取引所から得られる全ての価格情報が向こうのときに使う価格 */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

  /**
   * コンストラクタです．
   * @param loginName ログイン名
   * @param passwd パスワード
   * @param realName 実名
   * @param seed 乱数の種
   */
	public U17_2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		//ここでフィールド変数の値を初期化してください．

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
		int latestPrice = getLatestPrice(futurePrices);
    if (latestPrice == UOrderForm.INVALID_PRICE) {
      latestPrice = getLatestPrice(spotPrices);
    }
    if (latestPrice == UOrderForm.INVALID_PRICE) {
      latestPrice = fNominalPrice;
    }
    println("latestPrice=" + latestPrice);
		println("prePrice=" + prevPrice);
		println("増加率=" +  (spotPrices[spotPrices.length - 1]-prevPrice)*100.0/prevPrice);
		println("spotlen" + spotPrices.length);
		println("futurelen" + futurePrices.length);
		// 1節前の先物価格を取得する
    prevPrice = getLatestPrice(spotPrices);
    if (prevPrice == -1) {
      prevPrice = fNominalPrice;
    }
		// 注文区分を「何もしない」に設定した注文票を作成して返す．
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
    forms[0].setBuySell(UOrderForm.NONE);
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
