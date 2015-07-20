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

import java.util.Arrays;
import java.util.Random;

/**
 * This is a template class for your original agent class.
 * Make a copy of this file and modify the class name to "U<student_number>".
 */
public class U10_4 extends UAgent {

	//The definitions of field variables are here.
	
	/** the default value of the number of sessions in the short term */
	public static final int DEFAULT_SHORT_TERM = 30;
	
	/** the default value of the number of sessions in the medium term */
	public static final int DEFAULT_MEDIUM_TERM = 59;
  
  /** the default value of the maximum order volume */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** the default value of the minimum order volume */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** the default value of the maximum position */
  public static final int DEFAULT_MAX_POSITION = 300;

	/** the number of sessions in the short term */
	private int fShortTerm;
	
	/** the number of sessions in the medium term */
	private int fMediumTerm;

  /** the maximum order volume */
  private int fMaxQuant;

  /** the minimum order volume */
  private int fMinQuant;

  /** the maximum position */
  private int fMaxPosition;
  
  /** the parameter name of the number of sessions in the short term */
  public static final String SHORT_TERM_KEY = "ShortTerm";

  /** the parameter name of the number of sessions in the medium term */
  public static final String MEDIUM_TERM_KEY = "MediumTerm";
  
  /** the parameter name of the maximum order volume */
	public static final String MAX_QUANT_KEY = "MaxQuant";

	/** the parameter name of the minimum order volume */
	public static final String MIN_QUANT_KEY = "MinQuant";
	
	/** the parameter name of the maximum position */
	public static final String MAX_POSITION_KEY = "MaxPosition";
  
  /** the previous short-term moving average */
	private double fPreviousShortEMA = UOrderForm.INVALID_PRICE;
	
	/** the previous medium-term moving average */
	private double fPreviousMediumEMA = UOrderForm.INVALID_PRICE;
	
  /**
   * Constructor.
   * @param loginName the login name
   * @param passwd the password
   * @param realName the real name
   * @param seed the random seed
   */
	public U10_4(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		// Initialize the field variables here.
		fShortTerm = DEFAULT_SHORT_TERM;
		fMediumTerm = DEFAULT_MEDIUM_TERM;
		fMaxPosition =  DEFAULT_MAX_POSITION;
		fMinQuant = DEFAULT_MIN_QUANT;
		fMaxQuant = DEFAULT_MAX_QUANT;
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

		Random rand = getRandom();
  	UOrderForm[] forms = new UOrderForm[1];
  	
  	
  	forms[0] = new UOrderForm();
  	int[] prices = futurePrices;
    forms[0].setBuySell(chooseAction(prices));
    println("");
    println("day=" + day + ", session=" + session+ ", futures=" + prices[prices.length - 1] + ", shortTerm=" + fPreviousShortEMA+ ", mediumTerm=" + fPreviousMediumEMA);
    println("Action="+forms[0].getBuySellByString());
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
	
	private int chooseAction(int[] prices){
		int action = UOrderForm.NONE;
  	double shortTermEMA = calculateEMA(prices, fShortTerm);
  	double mediumTermEMA = calculateEMA(prices, fMediumTerm);
  	if (fPreviousShortEMA < fPreviousMediumEMA && mediumTermEMA < shortTermEMA) {
  		action = UOrderForm.BUY;
  	} else if (fPreviousMediumEMA < fPreviousShortEMA && shortTermEMA < mediumTermEMA) {
  		action = UOrderForm.SELL;
  	}
  	fPreviousShortEMA = shortTermEMA;
  	fPreviousMediumEMA = mediumTermEMA;
  	
  	return action;
	}
	
	public double calculateEMA(int[] prices, int term) {
  	double result = 0.0;
  	if(prices.length>term){
  		double prevEMA = calculateEMA(Arrays.copyOf(prices, prices.length-1), term);
  		double factor = 2/(term+1);
  		result = (prices[prices.length-1]-prevEMA)*factor+prevEMA;
  	}else{
  		int sum = 0;
  		for(int i=0;i<prices.length;i++){
  			sum+=prices[i];
  		}
  		result = (double)sum/term;
  	}
  	return result;
  }
	
  /**
   * This method sets up the system parameters.
   * @param args system parameters
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    // This method does not need to be overridden.
  }
}
