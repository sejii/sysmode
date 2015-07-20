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
 * 
 * 
 * STUDENT NUMBER	: 14M52050
 * STUDENT NAME		: SATHIYANANTHAVEL MAYURI
 * DEPARTMENT			: BIOLOGICAL SCIENCES
 * GROUP NUMBER		: 21
 * MEMBER NUMBER	: 4		
 * 
 */
package strategy_v2;

import java.util.Random;

/**
 * This is a template class for your original agent class.
 * Make a copy of this file and modify the class name to "U<student_number>".
 */
public class U21_4 extends UAgent {

  //The definitions of field variables are here.
  
  /** the default value of the over-bought threshold */
  public static final double DEFAULT_OVERBOUGHT_THRESHOLD = 80.0;
	
  /** the default value of the over-sold threshold */
  public static final double DEFAULT_OVERSOLD_THRESHOLD = 20.0;
	
  /** the default value of the maximum order volume */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** the default value of the minimum order volume */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** the default value of the maximum position */
  public static final int DEFAULT_MAX_POSITION = 300;
  
  /** the default value of the sliding window*/
  public static final int DEFAULT_SLIDING_WINDOW = 8;
  
  /** the threshold considered as over-bought - In future price might go down */
  private double fOverBought = DEFAULT_OVERBOUGHT_THRESHOLD;
  
  /** the threshold considered as over-sold - In future price might go up */
  private double fOverSold = DEFAULT_OVERSOLD_THRESHOLD;

  /** the maximum order volume */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** the minimum order volume */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** the maximum position */
  private int fMaxPosition = DEFAULT_MAX_POSITION;
  
  /** the sliding window */
  private int fSlidingWindow = DEFAULT_SLIDING_WINDOW;
  
  /** the parameter name of the number of sessions in the short term */
  public static final String OVERBOUGHT_THRESHOLD_KEY = "OverBought";

  /** the parameter name of the number of sessions in the medium term */
  public static final String OVERSOLD_THRESHOLD_KEY = "OverSold";
  
  /** the parameter name of the maximum order volume */
  public static final String MAX_QUANT_KEY = "MaxQuant";

  /** the parameter name of the minimum order volume */
  public static final String MIN_QUANT_KEY = "MinQuant";
	
  /** the parameter name of the maximum position */
  public static final String MAX_POSITION_KEY = "MaxPosition";
	
  /** the parameter name of the sliding window */
  public static final String SLIDING_WINDOW_KEY = "SlidingWindow";
	
  private double fPreviousUpEMA = 0.0;
  private double fPreviousDownEMA = 0.0;
  private double fPreviousRSIValue = 0.0;
  private double fAlpha = 0.5;
	
  // Ideally it should keep the traded volume also with it self so that profit can be calculated. 


/**
   * Constructor.
   * @param loginName the login name
   * @param passwd the password
   * @param realName the real name
   * @param seed the random seed
   */
	public U21_4(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);

		// Initialize the field variables here.

	}
	
	
	/**
	 * Getter Methods
	 */
	
	public double getOverBought(){
		return fOverBought;
	}
	
	public double getOverSold(){
		return fOverSold;
	}
	
	public int getMinQuant(){
		return fMinQuant;
	}
	
	public int getMaxQuant(){
		return fMaxQuant;
	}
	
	public int getMaxPosition(){
		return fMaxPosition;
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
		// added future price as well 
		//println("day=" + day + ", session=" + session + ", spot=" +  spotPrices[spotPrices.length - 1] + ", futures=" +  futurePrices[futurePrices.length - 1]);

		// An order form with an order division of "NONE" is made and returned.
		Random rand = getRandom();
		UOrderForm[] forms = new UOrderForm[1];
		forms[0] = new UOrderForm();
		forms[0].setBuySell(UOrderForm.NONE);
	  int[] prices = futurePrices;
		//int[] prices = spotPrices;
	  	int action = chooseAction(prices);
	  	forms[0].setBuySell(action);
	  	println("");
	    print("day=" + day + ", session=" + session
	           + ", futures=" + prices[prices.length - 1] 
	          // + ", spotlenght=" + prices.length
	           + ", RSIvalue=" + fPreviousRSIValue
	           + ", upEMA=" + fPreviousUpEMA
	           + ", downema=" + fPreviousDownEMA);
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
	    if(forms[0].getPrice()== 1){
	    	forms[0].setBuySell(UOrderForm.NONE);
	    }
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
	
	private double calculateUpEMAValue(double currentvalue){
		double currentemavalue = 0.0;
		double diff=fAlpha ;
		if (fPreviousUpEMA == 0.0){
			currentemavalue = currentvalue;
		}else{
			currentemavalue = fPreviousUpEMA + fAlpha * (currentvalue - fPreviousUpEMA);
			print(", diff= "+diff);
		}
		fPreviousUpEMA = currentemavalue;
		return currentemavalue;
	}
	
	private double calculateDownEMAValue(double currentvalue){
		double currentemavalue = 0.0;
		double diff=fAlpha;
		if (fPreviousDownEMA == 0.0){
			currentemavalue = currentvalue;
		}else{
			diff=0.0;
			currentemavalue = fPreviousDownEMA + fAlpha * (currentvalue - fPreviousDownEMA);
			print(", diff= "+diff);
		}
		fPreviousDownEMA = currentemavalue;
		return currentemavalue;
	}
	
	private double calculateRSIValue(int[] prices){
		double RSValue = 0.0;
		double RSIValue = 0.0;
		//Calculate RSI after first price added to the exchange
		if(prices.length >= 2 && prices[prices.length-1] != -1){
			if(prices[prices.length-1]>prices[prices.length-2]){
				RSValue = calculateUpEMAValue((double) prices[prices.length-1]-prices[prices.length-2])/fPreviousDownEMA;
				RSIValue = 100 - 100/(1+RSValue);
			}else if(prices[prices.length-1]<prices[prices.length-2]){
				RSValue = fPreviousUpEMA/calculateDownEMAValue((double) prices[prices.length-2]-prices[prices.length-1]);
				RSIValue = 100 - 100/(1+RSValue);
			}else{
				RSIValue = fPreviousRSIValue;
			}
		}
		//fPreviousRSIValue = RSIValue;
		return RSIValue;
	}
	
	private int chooseAction(int[] prices){
		int action = UOrderForm.NONE;
		double RSIValue = calculateRSIValue(prices);
		
		if(RSIValue != fPreviousRSIValue && RSIValue > fOverBought){
			action = UOrderForm.SELL;
		}else if (RSIValue != fPreviousRSIValue && RSIValue < fOverSold && RSIValue > 0.0){
			action = UOrderForm.BUY;
		}else{
			action = UOrderForm.NONE;
		}		
		fPreviousRSIValue = RSIValue;
		return action;
	}

  /**
   * This method sets up the system parameters.
   * @param args system parameters
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    // This method does not need to be overridden.
    for (int i=0; i< args.length; i++){
			String[] strArray = args[i].split("=");
			String key = strArray [0];
			String value = strArray [1];
			if (key.equals(U21_4.OVERBOUGHT_THRESHOLD_KEY)){
				fOverBought = Double.parseDouble(value);
				println("Overbought has been changed to "+ fOverBought);
			}else if (key.equals(U21_4.OVERSOLD_THRESHOLD_KEY)){
				fOverSold = Double.parseDouble(value);
				println("OverSold has been changed to "+ fOverSold);
			}else if (key.equals(U21_4.MIN_QUANT_KEY)){
				fMinQuant = Integer.parseInt(value);
				println("MinQuant has been changed to "+ fMinQuant);
			}else if (key.equals(U21_4.MAX_QUANT_KEY)){
				fMaxQuant = Integer.parseInt(value);
				println("MaxQuant has been changed to "+ fMaxQuant);
			}else if (key.equals(U21_4.MAX_POSITION_KEY)){
				fMaxPosition = Integer.parseInt(value);
				println("MaxPosition has been changed to "+ fMaxPosition);
			}else if (key.equals(U21_4.SLIDING_WINDOW_KEY)){
				fSlidingWindow = Integer.parseInt(value);
				println ("SlidingWindow has been changed to "+ fSlidingWindow);
				fAlpha = (double) 1/fSlidingWindow;
				println ("Alpha has been changed to "+ fAlpha);
			}
			else{
				println("Unknown parameter:"+ key + " in U21_4.setParameters");
			}
		}
  }
}
