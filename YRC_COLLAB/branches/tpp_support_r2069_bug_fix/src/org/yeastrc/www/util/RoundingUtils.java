/**
 * RoundingUtils.java
 * @author Vagisha Sharma
 * Mar 2, 2010
 * @version 1.0
 */
package org.yeastrc.www.util;

import java.math.BigDecimal;

/**
 * 
 */
public class RoundingUtils {

	
	private static RoundingUtils instance = null;
	
	private RoundingUtils() {}
	
	public static RoundingUtils getInstance() {
		if(instance == null) {
			instance = new RoundingUtils();
		}
		return instance;
	}
	
	public double roundTwo(BigDecimal number) {
        return roundTwo(number.doubleValue());
    }
    public double roundTwo(double num) {
        return Math.round(num*100.0)/100.0;
    }
    
    public double roundFour(BigDecimal number) {
        return roundTwo(number.doubleValue());
    }
    public double roundFour(double num) {
        return Math.round(num*10000.0)/10000.0;
    }
}
