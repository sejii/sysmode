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
 * This is a template class for your original agent class.
 * Make a copy of this file and modify the class name to "U<student_number>".
 */
public class U15_4 extends UAgent {

	//The definitions of field variables are here.
	public static final int DEFAULT_SHORT_TERM = 12;
	public static final int DEFAULT_LONG_TERM = 26;
	public static final int DEFAULT_MIN_QUANT = 10;
	public static final int DEFAULT_MAX_QUANT = 50;
	public static final int DEFAULT_MAX_POSITION = 300;

	public static final String SHORT_TERM_KEY ="ShortTerm";
	public static final String LONG_TERM_KEY = "LongTerm";
	public static final String MIN_QUANT_KEY = "MinQuant";
	public static final String MAX_QUANT_KEY = "MaxQuant";
	public static final String MAX_POSITION_KEY = "MaxPosition";

	private int fShortTerm = DEFAULT_SHORT_TERM;
	private int fLongTerm = DEFAULT_LONG_TERM;
	private int fMinQuant = DEFAULT_MIN_QUANT;
	private int fMaxQuant = DEFAULT_MAX_QUANT;
	private int fMaxPosition = DEFAULT_MAX_POSITION;
	private double fPreviousEMAShort = UOrderForm.INVALID_PRICE;
	private double fPreviousEMALong = UOrderForm.INVALID_PRICE;
	private double fPreviousDEA = UOrderForm.INVALID_PRICE;
	private double fPreviousDIF = UOrderForm.INVALID_PRICE;
  /**
   * Constructor.
   * @param loginName the login name
   * @param passwd the password
   * @param realName the real name
   * @param seed the random seed
   */
	public U15_4(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		// Initialize the field variables here.

	}

  /**
   * This method makes and returns order forms.
   * @param day the current date
   * @param session the current session
   * @param maxDays the maximum dates
   * @param noOfSessionsPerDay the number of sessions per day
   * @param spotPrices the spot price sequence. It consists of the past 120 spot prices. The latest price is spotPrices[119]. Note that spotPrices[i] is set to -1 if the price at time i was not determined.
   * @param futurePrices the futures price sequence. It consists of the past 60 futures prices. The latest price is futurePrices[59]. Note that spotPrices[i] is set to -1 if the price at time i was not determined. The futures prices before the trading start time are set to the corresponding spot prices.
   * @param position the position. If it is positive, it means "a long position". If it is negative, it means "a short position".
   * @param money the current cash balance. Note that its type is "long".
   * @return UOrderForm[] an array of order forms
   */
	public UOrderForm[] makeOrderForms(int day, int session,
                                      int maxDays, int noOfSessionsPerDay,
                                      int[] spotPrices, int[] futurePrices,
                                      int position, long money) {

		// The current date, session, and spot price are displayed.
		println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1]);

		// An order form with an order division of "NONE" is made and returned.
		Random rand = getRandom();
		UOrderForm[] forms = new UOrderForm[1];
  	forms[0] = new UOrderForm();
  	int[] prices = futurePrices;
    forms[0].setBuySell(chooseAction(prices));
    println("");
    print("day=" + day + ", session=" + session
          + ", futures=" + prices[prices.length - 1] 
          + ", DIF=" + fPreviousDIF
          + ", DEA=" + fPreviousDEA);
    if (forms[0].getBuySell() == UOrderForm.NONE) {
    	return forms;
    }
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
    forms[0].setPrice(determinePrice(forms[0].getBuySell(), prices));
    forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));
    print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
           + ", quantity=" + forms[0].getQuantity()); 
    return forms;
  }

  /**
   * This method sets up the system parameters.
   * @param args system parameters
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    // This method does not need to be overridden.
  }
  
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

	private double calculateEMAShort(int[] prices) {
		if(fPreviousEMAShort == UOrderForm.INVALID_PRICE) {
			fPreviousEMAShort = calculateMovingAverage(prices, fShortTerm);
		}
		
		if(fPreviousEMAShort == UOrderForm.INVALID_PRICE)
			return UOrderForm.INVALID_PRICE;
		
		double EMA = fPreviousEMAShort + 2.0 /(double)(fShortTerm + 1) * (prices[prices.length - 1] - fPreviousEMAShort); 
		return EMA;
	}
	
	private double calculateEMALong(int[] prices) {
		if(fPreviousEMALong == UOrderForm.INVALID_PRICE) {
			fPreviousEMALong = calculateMovingAverage(prices, fLongTerm);
		}
		
		if(fPreviousEMALong == UOrderForm.INVALID_PRICE)
			return UOrderForm.INVALID_PRICE;
		
		double EMA = fPreviousEMALong + 2.0 /(double)(fLongTerm + 1) * (prices[prices.length - 1] - fPreviousEMALong); 
		return EMA;
	}
	
	
	
	int chooseAction(int[] prices) {
		
		int action = UOrderForm.NONE;
		
		double fShortTermEMA = calculateEMAShort(prices);
		double fLongTermEMA = calculateEMALong(prices);
		
		double fDIF = fShortTermEMA - fLongTermEMA;
		
		double fDEA = fPreviousDEA == UOrderForm.INVALID_PRICE ? fDIF : (fPreviousDEA * 0.8 + fDIF * 0.2);
		
		
		if (fShortTermEMA != UOrderForm.INVALID_PRICE && fLongTermEMA != UOrderForm.INVALID_PRICE
				&& fPreviousDIF != UOrderForm.INVALID_PRICE) {
			if(fDEA > 0 && fDIF > 0 && fDEA > fPreviousDEA && fDIF > fPreviousDIF || 
					fDEA < 0 && fDIF < 0 && fDEA > fPreviousDEA && fDIF > fPreviousDIF){
				action = UOrderForm.BUY;
			}
			else if (fDEA > 0 && fDIF > 0 && fDEA < fPreviousDEA && fDIF < fPreviousDIF || 
					fDEA < 0 && fDIF < 0 && fDEA < fPreviousDEA && fDIF < fPreviousDIF) {
				action =  UOrderForm.SELL;
			}
		}
		
		fPreviousEMAShort = fShortTermEMA;
		fPreviousEMALong = fLongTermEMA;
		fPreviousDEA = fDEA;
		fPreviousDIF = fDIF;
		
		return action;
	}
	
	int determinePrice(int action, int[] prices) {
		
	
		int difference = prices[prices.length - 1] - prices[prices.length - 2];
		int currentPrice = prices[prices.length - 1];
		int price = currentPrice;
				
		switch (action) {
		case UOrderForm.BUY:
			price = currentPrice + (int) (difference + getRandom().nextGaussian() * Math.sqrt((double)difference/4.0));
			break;
		case UOrderForm.SELL:
			price = currentPrice - (int) (difference + getRandom().nextGaussian() * Math.sqrt((double)difference/4.0)); 
			break;
		default:
			break;
		}
		return price < 0 ? 1 : price;
	}
}