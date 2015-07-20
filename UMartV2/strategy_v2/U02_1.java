/**
 * Copyright (c) 2001-2008 U-Mart Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ---------------------------------------------------------------------
 */
package strategy_v2;
import java.util.*;

/**
 * 独自エージェント用のテンプレートです． このエージェントのコピーを作成して，名前をU<学籍番号>に修正して下さい．
 */
public class U02_1 extends UAgent {

	//ここにフィールド変数を定義してください．
    private int fShortTerm;
    private int fMediumTerm;
    private int fMaxPosition;
    private int fMinQuant;
    private int fMaxQuant;
    
    private static final int DEFAULT_SHORT_TERM = 10;
    private static final int DEFAULT_MEDIUM_TERM = 30;
    private static final int DEFAULT_MAX_POSITION = 300;
    private static final int DEFAULT_MIN_QUANT = 10;
    private static final int DEFAULT_MAX_QUANT = 50;

    private static final String SHORT_TERM_KEY = "ShortTerm";
    private static final String MEDIUM_TERM_KEY = "MediumTerm";
    private static final String MAX_POSITION_KEY = "MaxPosition";
    private static final String MIN_QUANT_KEY = "MinQuant";
    private static final String MAX_QUANT_KEY = "MaxQuant";
    /**
     * コンストラクタです．
     *
     * @param loginName ログイン名
     * @param passwd パスワード
     * @param realName 実名
     * @param seed 乱数の種
     */
    public U02_1(String loginName, String passwd, String realName, int seed) {
        super(loginName, passwd, realName, seed);

		//ここでフィールド変数の値を初期化してください．
        this.fShortTerm = DEFAULT_SHORT_TERM;
        this.fMediumTerm = DEFAULT_MEDIUM_TERM;
        this.fMaxPosition = DEFAULT_MAX_POSITION;
        this.fMinQuant = DEFAULT_MIN_QUANT;
        this.fMaxQuant = DEFAULT_MAX_QUANT;
    }
    public int getShortTerm() {
        return this.fShortTerm;
    }

    public int getMediumTerm() {
        return this.fMediumTerm;
    }

    public int getMaxPosition() {
        return this.fMaxPosition;
    }

    public int getMinQuant() {
        return this.fMinQuant;
    }

    public int getMaxQuant() {
        return this.fMaxQuant;
    }


    public static double calculateAutoCorr( int[] prices , int term ) {
        int n = prices.length;
           
        double sum = 0.0;
        for(int i = 0; i < term; ++i) {
            if( prices[ (n-1) - i] < 0) {
                continue;
            }
            sum +=( double ) prices[ (n-1) - i];
        }
        
        double mean = sum/(double)term;
        
        sum = 0.0;
        for(int i = 0; i < term; ++i) {
            if( prices[ (n-1) - i] < 0) {
                continue;
            }
            sum += ((double)prices[(n-1)-i]-mean) * ((double)prices[n-1]-mean);
        }
        
        return sum/(double)term;
    }

    private int chooseAction(int[] prices) {
        double shortTermAutoCorr = this.calculateAutoCorr(prices, this.fShortTerm);
        double mediumTermAutoCorr = this.calculateAutoCorr(prices, this.fMediumTerm);
        
        println("Short: "+ shortTermAutoCorr);
        println("Medium: " + mediumTermAutoCorr);

        if (shortTermAutoCorr<0 && mediumTermAutoCorr<0) {
            return UOrderForm.BUY;
        } else if (shortTermAutoCorr>0 && mediumTermAutoCorr>0) {
            return UOrderForm.SELL;
        } else {
            return UOrderForm.NONE;
        }
    }

    private int calculateQuantity() {
        return (int) ((this.fMaxQuant - this.fMinQuant) 
                * super.getRandom().nextDouble() + this.fMinQuant);
    }

    private double getPriceRandomDoubleVal(int[] futurePrices, int session) {
        double priceDiff = futurePrices[session - 1] - futurePrices[session - 2];
        return (priceDiff / 4.0) * super.getRandom().nextGaussian() + priceDiff;
    }

    private int determinePrice(int action, int[] prices) {
        int orderPrice = UOrderForm.INVALID_PRICE;
        int currnetPrice = prices[prices.length - 1];

        switch (action) {
            case UOrderForm.BUY:
                orderPrice = (int) (currnetPrice + getPriceRandomDoubleVal(prices, prices.length));
                break;
            case UOrderForm.SELL:
                orderPrice = (int) (currnetPrice - getPriceRandomDoubleVal(prices, prices.length));
                if (orderPrice <= 0) {
                    orderPrice = 1;
                }
                break;
            default:
                break;
        }

        return orderPrice;
    }
    /**
     * 注文票を作成します．
     *
     * @param day 日
     * @param session 節
     * @param maxDays 取引日数
     * @param noOfSessionsPerDay 1日の節数
     * @param spotPrices
     * 現物価格系列．spotPrices[0]からspotPrices[119]までの120節分のデータが格納されている．spotPrices[119]が直近の価格です．ただし，価格が成立していない場合，-1が入っているので注意してください．
     * @param futurePrices
     * 先物価格系列．futurePrices[0]からfuturePrices[59]までの60節分のデータが格納されています．futurePrices[59]が直近の価格です．ただし，価格が成立していない場合，-1が入っているので注意してください．また，取引開始節より前は現物価格が格納されています．
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

        forms[0].setBuySell(UOrderForm.NONE);

        int action = this.chooseAction(futurePrices);

        if (action == UOrderForm.NONE) {
            // 注文なし
        } else {
                // 注文を注文票へ                
                forms[0].setBuySell(action);
                forms[0].setQuantity(this.calculateQuantity());
                forms[0].setPrice(this.determinePrice(action, futurePrices));
        }
        println("day ="+ day +", session ="+ session +", latestPrice ="+ futurePrices[futurePrices.length-1]
                +", "+ forms[0].getBuySellByString() +", price ="+ forms[0].getPrice()
                +", quantity ="+ forms[0].getQuantity() + ", position ="+position);
        
        return forms;
    }

    /**
     * エージェントのシステムパラメータを設定します．
     *
     * @param args システムパラメータ
     */
    public void setParameters(String[] args) {
        super.setParameters(args);
        //このメソッドをオーバーライドする必要はありません．
    }
}
