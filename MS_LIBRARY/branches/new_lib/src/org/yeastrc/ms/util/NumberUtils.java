package org.yeastrc.ms.util;

public class NumberUtils {

    private NumberUtils() {}
    
    public static String trimTrailingZeros(String number) {
        
        if (number.lastIndexOf('0') == -1)   return number;
        
        int e = number.length() - 1;
        while(number.charAt(e) == '0') {
            e--;
        }
        if (number.charAt(e) == '.')
            return number.substring(0, e);
        else
            return number.substring(0, e+1);
    }
}
