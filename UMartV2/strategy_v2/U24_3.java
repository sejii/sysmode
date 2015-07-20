package strategy_v2 ;

import java.util.*;

public class U24_3 extends UAgent {
    private int fShortTerm;
    private int fMediumTerm;
    private int fMinQuant;
    private int fMaxQuant;
    private int fMaxPosition;
    private double fPreviousShortTermMovingAverage;
    private double fPreviousMediumTermMovingAverage;
    
    public static final int DEFAULT_SHORT_TERM = 8;
    public static final int DEFAULT_MEDIUM_TERM = 16;
    public static final int DEFAULT_MIN_QUANT = 10;
    public static final int DEFAULT_MAX_QUANT = 50;
    public static final int DEFAULT_MAX_POSITION = 300;
    public static final String SHORT_TERM_KEY = "ShortTerm";
    public static final String MEDIUM_TERM_KEY = "MediumTerm";
    public static final String MIN_QUANT_KEY = "MinQuant";
    public static final String MAX_QUANT_KEY = "MaxQuant";
    public static final String MAX_POSITION_KEY = "MaxPosition";

    private Random randGen;
    
    public U24_3(String loginName,
                               String passwd,
                               String realName,
                               int seed) {
        super(loginName, passwd, realName, seed);
        randGen = getRandom();
        fShortTerm = DEFAULT_SHORT_TERM;
        fMediumTerm = DEFAULT_MEDIUM_TERM;
        fMinQuant = DEFAULT_MIN_QUANT;
        fMaxQuant = DEFAULT_MAX_QUANT;
        fMaxPosition = DEFAULT_MAX_POSITION;
    }
    
    public int getShortTerm() { return fShortTerm; }
    public int getMediumTerm() { return fMediumTerm; }
    public int getMinQuant() { return fMinQuant; }
    public int getMaxQuant() { return fMaxQuant; }
    public int getMaxPosition() { return fMaxPosition; }

    private double nextGaussian(double ave, double sigma) {
        return randGen.nextGaussian() * sigma + ave;
    }

    int determinePrice(int action, int[] prices) {
        //int price = prices[prices.length-1];
        int price = getLatestPrice(prices);
        int diff = prices[prices.length-2] - price;
        if(action == UOrderForm.BUY) {
            return Math.max(1, price + (int)nextGaussian((double)diff, diff/4.0));
        } else if(action == UOrderForm.SELL) {
            return Math.max(1, price - (int)nextGaussian((double)diff, diff/4.0));
        }
        return UOrderForm.INVALID_PRICE;
    }
    
    public UOrderForm[] makeOrderForms(int day,
                                       int session,
                                       int maxDays,
                                       int noOfSessionsPerDay,
                                       int[] spotPrices,
                                       int[] futurePrices,
                                       int position,
                                       long money) {
        int action = chooseAction(futurePrices);
        if((action == UOrderForm.SELL && position < -fMaxPosition) ||
           (action == UOrderForm.BUY && position > fMaxPosition)) {
            action = UOrderForm.NONE;
        }
        int price = determinePrice(action, spotPrices);

        UOrderForm order = new UOrderForm();
        UOrderForm[] orders = {order};
        order.setBuySell(action);
        order.setPrice(price);
        if(action != UOrderForm.NONE) {
            order.setQuantity(fMinQuant + randGen.nextInt(fMaxQuant - fMinQuant + 1));
        }
        
        int latestPrice = getLatestPrice(futurePrices);
        println("day=" + day + ", session=" + session + ", latestPrice=" + latestPrice
                + ", " + order.getBuySellByString() + ", price=" + order.getPrice()
                + ", quantity=" + order.getQuantity());
        
        return orders;
    }

    double calculateMovingAverage(int[] prices, int term) {
        double sum = 0.0;
        for(int i = 0; i < term ; ++i) {
            if (prices[prices.length - 1 - i] < 0) {
                return (double) UOrderForm.INVALID_PRICE;
            }
            sum += (double) prices[prices.length - 1 - i];
        }
        return sum / (double) term ;
    }

    int chooseAction(int[] prices) {
        double short_ave = calculateMovingAverage(prices, fShortTerm);
        double med_ave = calculateMovingAverage(prices, fMediumTerm);
        double prev_short_ave = fPreviousShortTermMovingAverage;
        double prev_med_ave = fPreviousMediumTermMovingAverage;
        int action = UOrderForm.NONE;
        if(short_ave < med_ave && prev_short_ave > prev_med_ave) {
            action = UOrderForm.BUY;
        } else if(short_ave > med_ave && prev_short_ave < prev_med_ave) {
            action = UOrderForm.SELL;
        }

        fPreviousShortTermMovingAverage = short_ave;
        fPreviousMediumTermMovingAverage = med_ave;
        return action;
    }
    
    public void setParameters(String[] args) {
        super.setParameters (args);
        for(String arg : args) {
            String[] strArray = arg.split("=");
            String key = strArray[0];
            String value = strArray[1];
            if (key.equals(UMovingAverageAgent.SHORT_TERM_KEY)) {
                fShortTerm = Integer.parseInt(value);
            } else if(key.equals(UMovingAverageAgent.MEDIUM_TERM_KEY)) {
                fMediumTerm = Integer.parseInt(value);
            } else if(key.equals(UMovingAverageAgent.MIN_QUANT_KEY)) {
                fMinQuant = Integer.parseInt(value);
            } else if(key.equals(UMovingAverageAgent.MAX_QUANT_KEY)) {
                fMaxQuant = Integer.parseInt(value);
            } else if(key.equals(UMovingAverageAgent.MAX_POSITION_KEY)) {
                fMaxPosition = Integer.parseInt(value);
            } else {
                println("Unknown parameter :" + key + "in UMovingAverageAgent.setParameters");
            }
        
        }
    }
}
