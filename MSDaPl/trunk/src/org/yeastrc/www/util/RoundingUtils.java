/**
 * RoundingUtils.java
 * @author Vagisha Sharma
 * Mar 2, 2010
 * @version 1.0
 */
package org.yeastrc.www.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 
 */
public class RoundingUtils {

	
	private static RoundingUtils instance = null;
	
	private DecimalFormat roundOneFmt;
	private DecimalFormat roundTwoFmt;
	private DecimalFormat roundFourFmt;
	
	private RoundingUtils() {
		
		roundOneFmt = new DecimalFormat("#0.0");
		roundOneFmt.setRoundingMode(RoundingMode.HALF_UP);
		roundTwoFmt = new DecimalFormat("#0.00");
		roundTwoFmt.setRoundingMode(RoundingMode.HALF_UP);
		roundFourFmt = new DecimalFormat("#0.0000");
		roundFourFmt.setRoundingMode(RoundingMode.HALF_UP);
	}
	
	public static synchronized RoundingUtils getInstance() {
		if(instance == null) {
			instance = new RoundingUtils();
		}
		return instance;
	}
	
	public double roundOne(BigDecimal number) {
        return roundOne(number.doubleValue());
    }
	
    public double roundOne(double num) {
        return Math.round(num*10.0)/10.0;
    }
    
    public String roundOneFormat(double num) {
    	return roundOneFmt.format(num);
    }
    
    public String roundOneFormat(BigDecimal number) {
    	return roundOneFmt.format(number.doubleValue());
    }
    
	public double roundTwo(BigDecimal number) {
        return roundTwo(number.doubleValue());
    }
    public double roundTwo(double num) {
        return Math.round(num*100.0)/100.0;
    }
    
    public String roundTwoFormat(double num) {
    	return roundTwoFmt.format(num);
    }
    
    public String roundTwoFormat(BigDecimal number) {
    	return roundTwoFmt.format(number.doubleValue());
    }
    
    public double roundFour(BigDecimal number) {
        return roundTwo(number.doubleValue());
    }
    public double roundFour(double num) {
        return Math.round(num*10000.0)/10000.0;
    }
    
    public String roundFourFormat(double num) {
    	return roundFourFmt.format(num);
    }
    
    public String roundFourFormat(BigDecimal number) {
    	return roundFourFmt.format(number.doubleValue());
    }
}
