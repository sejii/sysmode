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
 */
public class U09_4 extends UAgent {
	
	public static final int DEFAULT_MAX_POSITION = 300;
	
	public static final int DEFAULT_QUANTITY = 40;
	
	public static final int MAX_PRICE_OFFSET = 100;
	
	private int fMaxPosition = DEFAULT_MAX_POSITION;
	
	private int fMaxPriceOffset = MAX_PRICE_OFFSET;
	
	private int fQuantity = DEFAULT_QUANTITY;
	
  /** the parameter name of the maximum price offset */
	public static final String MAX_PRICEOFF_KEY = "MaxPriceOffset";

	/** the parameter name of the default volume */
	public static final String DEFAULT_QUANT_KEY = "DefQuant";
	
	/** the parameter name of the maximum position */
	public static final String MAX_POSITION_KEY = "MaxPosition";
	
	private int buyCounter;
	
	private int sellCounter;

	//The definitions of field variables are here.

  /**
   * Constructor.
   * @param loginName the login name
   * @param passwd the password
   * @param realName the real name
   * @param seed the random seed
   */
	public U09_4(String loginName, String passwd, String realName, int seed) {
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
		
		// An order form with an order division of "NONE" is made and returned.
		UOrderForm[] forms = new UOrderForm[1];
		int[] prices = futurePrices;
		
		forms[0] = new UOrderForm();
    forms[0].setBuySell(chooseAction(prices));
    
    if (forms[0].getBuySell() == UOrderForm.NONE) {
    	return forms;
    }

    forms[0].setPrice(determinePrice(forms[0].getBuySell(), prices));
    forms[0].setQuantity(determineQuantity(forms[0].getBuySell(), prices));
    
    println("day=" + day + ", session=" + session + ", futureprice=" + futurePrices[futurePrices.length - 1] + ", price =" + forms[0].getPrice() + ", action=" + forms[0].getBuySellByString());
    
    return forms;
  }
	
  /**
   * This method determines the order price and returns it.
   * @param action the order division
   * @param prices the order price sequence. Make sure that the latest price is prices[prices.length - 1].
   * @return the order price.
   */
	private int determinePrice(int action, int[] prices)
	{
		int price = 0;
		
		// Get the difference from the two previous prices
		int priceDiff = Math.abs(prices[prices.length - 2] - prices[prices.length - 3]);
		
		// If it is greater than the maximum, set the default value
		if (priceDiff > fMaxPriceOffset)
		{
			priceDiff = fMaxPriceOffset;
		}
		
		// If its buy use the last price (buy at current price)
		if (action == UOrderForm.BUY)
		{
			price = prices[prices.length - 1];
		}
		// If its sell, use the last price + the calculated difference + a random number
		else if (action == UOrderForm.SELL) 
		{
			price = prices[prices.length - 1] + priceDiff + (int)((double)priceDiff / 4.0 * getRandom().nextGaussian());
		}
		
		return price;
	}
	
	/**
	 * This method determines the volume and returns it.
	 * @param action the order division
	 * @param prices the order price sequence. Make sure that the latest price is prices[prices.length - 1].
	 * @return the volume
	 */
	private int determineQuantity(int action, int[] prices)
	{
		int quantity = fQuantity;
		Random rand = getRandom();
		int diff = buyCounter - sellCounter;
		
		// Get the difference between buy and sell (position)
		// If there are more buy than sell then use the difference of this amounts and add a random multiplier to
		// get a final volume amount
		if (diff > 0)
		{
			quantity+= diff * rand.nextInt(3);
		}
		
		return quantity + diff;
	}
	
  /**
   * This method determines the order division and returns it.
   * @param prices the order price sequence. Make sure that the latest price is prices[prices.length - 1].
   * @return the order division.
   */
	private int chooseAction(int[] prices) {
		
		int action = UOrderForm.NONE;
		
		// Get the last 3 prices
		int price0 = prices[prices.length - 1];
		int price1 = prices[prices.length - 2];
		int price2 = prices[prices.length - 3];
		
		// If they have values then process
		if (price0 != 0 && price1 != 0 && price2 != 0)
		{		
			// If price tends to go up, then choose BUY
			if ((price0 > price1) && (price0 > price2))
			{
				action = UOrderForm.BUY;
				buyCounter++;
			}
			// If price tends to go down then choose SELL
			else if ((price0 < price1) && (price0 < price2))
			{
				action = UOrderForm.SELL;
				sellCounter++;
			}
		}
		
		return action;
	}

  /**
   * This method sets up the system parameters.
   * @param args system parameters
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    for (int i = 0; i < args.length; ++i) {
    	String[] strArray = args[i].split("=");
      String key = strArray[0];
      String value = strArray[1];
      if (key.equals(U09_4.DEFAULT_QUANT_KEY)) {
        fQuantity = Integer.parseInt(value);
        println("Default quantity has been changed to " + fQuantity);
      } else if (key.equals(U09_4.MAX_PRICEOFF_KEY)) {
      	fMaxPriceOffset = Integer.parseInt(value);
      	println("MaxPriceOffset has been changed to " + fMaxPriceOffset);
      } else if (key.equals(U09_4.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else {
      	println("Unknown parameter:" + key + " in U09_4.setParameters");
      }
    }
  }
}
