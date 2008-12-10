/**
 * PeakStringBuilder.java
 * @author Vagisha Sharma
 * Jul 12, 2008
 * @version 1.0
 */
package org.yeastrc.ms.util;


/**
 * 
 */
public class PeakStringBuilder {

    private StringBuilder peakBuffer;

    public PeakStringBuilder() {
        peakBuffer = new StringBuilder();
    }

    public void addPeak(String mz, String rt) {

        peakBuffer.append(trimTrailingZeros(mz));
        peakBuffer.append(" ");
        peakBuffer.append(trimTrailingZeros(rt));
        peakBuffer.append("\n");
    }

    public String getPeaksAsString() {
        if (peakBuffer.length() > 0)
            return peakBuffer.substring(0, peakBuffer.length() - 1); // remove last new-line.
        return "";
    }

    public static String trimTrailingZeros(String number) {

        if (number.lastIndexOf('0') == -1)   return number;
        if (number.lastIndexOf('.') == -1)   return number;
        

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
