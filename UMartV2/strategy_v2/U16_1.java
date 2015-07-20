
package strategy_v2;

import java.util.Random;
import java.util.StringTokenizer;

public class U16_1 extends UAgent {

  public static final int DEFAULT_MAX_QUANT = 300;

  public static final int DEFAULT_MIN_QUANT = 20;

  public static final int DEFAULT_MAX_POSITION = 300;

  private int fMaxQuant = DEFAULT_MAX_QUANT;

  private int fMinQuant = DEFAULT_MIN_QUANT;

  private int fMaxPosition = DEFAULT_MAX_POSITION;

	public static final String MAX_QUANT_KEY = "MaxQuant";

	public static final String MIN_QUANT_KEY = "MinQuant";

	public static final String MAX_POSITION_KEY = "MaxPosition";


	public U16_1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	public int getMinQuant() {
		return fMinQuant;
	}

	public int getMaxQuant() {
		return fMaxQuant;
	}
	public int getMaxPosition() {
		return fMaxPosition;
	}
  public UOrderForm[] makeOrderForms(int day, int session,
                                      int maxDays, int noOfSessionsPerDay,
                                      int[] spotPrices, int[] futuresPrices,
                                      int position, long money) {
  	int price = UOrderForm.INVALID_PRICE;
  	Random rand = getRandom();
  	UOrderForm[] forms = new UOrderForm[1];
  	forms[0] = new UOrderForm();
  	int[] prices = futuresPrices;
    forms[0].setBuySell(chooseAction(prices, position));
    println("");
    print("day=" + day + ", session=" + session
          + ", futures=" + prices[prices.length - 1]);
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
    forms[0].setQuantity(  fMaxQuant*100/(2000-price)  + 1);
    print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
           + ", quantity=" + forms[0].getQuantity());
    return forms;
  }


  private int determinePrice(int action, int[] prices) {
  	double C = prices[prices.length - 1];
  	int price = UOrderForm.INVALID_PRICE;
  	int widthOfPrice = Math.abs(prices[prices.length - 1] - prices[prices.length - 2]);
  	if (action == UOrderForm.BUY) {

      price = 1900;
    } else if (action == UOrderForm.SELL) {
      price = 2100;
    }
  	if (price < 0) {
  		price = 1;
  	}
  	return price;
  }

    private int chooseAction(int[] prices,int position) {
  	int action = UOrderForm.NONE;
  	double previousfutureprice6 = prices[prices.length - 6];

  	double C = prices[prices.length - 1];

  	  	if (C<2150)//
  	{
  			action = UOrderForm.BUY;


  			if (position >fMaxPosition){
  				action = UOrderForm.NONE;
  			}
  		} else if (C>2170)
  		{
  			action = UOrderForm.SELL;
  			if (position < -fMaxPosition){
  				action = UOrderForm.NONE;
  			}//

  		}return action;
  }

  public void setParameters(String[] args) {
    super.setParameters(args);
    for (int i = 0; i < args.length; ++i) {
      StringTokenizer st = new StringTokenizer(args[i], "= ");
      String key = st.nextToken();
      String value = st.nextToken();
      if
       (key.equals(U16_1.MIN_QUANT_KEY)) {
        fMinQuant = Integer.parseInt(value);
        println("MinQuant has been changed to " + fMinQuant);
      } else if (key.equals(U16_1.MAX_QUANT_KEY)) {
        fMaxQuant = Integer.parseInt(value);
        println("MaxQuant has been changed to " + fMaxQuant);
      } else if (key.equals(U16_1.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else {
      	println("Unknown parameter:" + key + " in U20_4Agent.setParameters");
      }
    }
  }
}
