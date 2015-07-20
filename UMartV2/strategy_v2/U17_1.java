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
public class U17_1 extends UAgent {

	//The definitions of field variables are here.
	
	/** the default value of the width of order price. */
  public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** the default value of the maximum order volume. */
  public static final int DEFAULT_MAX_QUANT = 150;

  /** the default value of the minimum order volume. */
  public static final int DEFAULT_MIN_QUANT = 30;

  /** the default value of the maximum position. */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** the default value of the nominal price. */
  public static final int DEFAULT_NOMINAL_PRICE = 3000;

  /** the width of order price. */
  private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE;

  /** the maximum order volume. */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** the minimum order volume. */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** the maximum position. */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** the nominal order price. */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

	/** the parameter name of the width of order price. */
	public static final String WIDTH_OF_PRICE_KEY = "WidthOfPrice";

	/** the parameter name of the maximum order price. */
	public static final String MAX_QUANT_KEY = "MaxQuant";

	/** the parameter name of the minimum order price. */
	public static final String MIN_QUANT_KEY = "MinQuant";
	
	/** the parameter name of the maximum position. */
	public static final String MAX_POSITION_KEY = "MaxPosition";
	
	/** the parameter name of the nominal order price. */
	public static final String NOMINAL_PRICE_KEY = "NominalPrice";

  /**
   * Constructor.
   * @param loginName the login name
   * @param passwd the password
   * @param realName the real name
   * @param seed the random seed
   */
	public U17_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		// Initialize the field variables here.

	}
	
	/**
	 * This method returns the width of order price.
	 * @return the width of order price.
	 */
	public int getWidthOfPrice() {
		return fWidthOfPrice;
	}

	/**
	 * This method returns the minimum order volume.
	 * @return the minimum order volume.
	 */
	public int getMinQuant() {
		return fMinQuant;
	}

	/**
	 * This method returns the maximum order volume.
	 * @return the maximum order volume.
	 */
	public int getMaxQuant() {
		return fMaxQuant;
	}

	/**
	 * This method returns the maximum position.
	 * @return the maximum position.
	 */
	public int getMaxPosition() {
		return fMaxPosition;
	}

	/**
	 * This method returns the nominal order price.
	 * @return the nominal order price.
	 */
	public int getNominalPrice() {
		return fNominalPrice;
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
    forms[0].setBuySell(rand.nextInt(2) + 1);
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
    int latestPrice = getLatestPrice(futurePrices);
    if (latestPrice == UOrderForm.INVALID_PRICE) {
      latestPrice = getLatestPrice(spotPrices);
    }
    if (latestPrice == UOrderForm.INVALID_PRICE) {
      latestPrice = fNominalPrice;
    }
    int price = latestPrice + (int)((double)fWidthOfPrice * rand.nextGaussian()/2);
    if (price <= 0) {
    	price = 1;
    }
    forms[0].setPrice(price);
    forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));
    println("day=" + day + ", session=" + session + ", latestPrice="  + latestPrice
    		    + ", " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
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
}
