/**
 * PeakConverterString.java
 * @author Vagisha Sharma
 * Jul 28, 2008
 * @version 1.0
 */
package org.yeastrc.ms.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class PeakConverterString {

    public List<String[]> convert(String peakString) {

        List<String[]> peakList = new ArrayList<String[]>();

        if (peakString == null || peakString.length() == 0)
            return peakList;

        String[] peaksStr = peakString.split("\\n");
        for (String peak: peaksStr) {
            String [] peakVals = splitPeakVals(peak);
            peakList.add(peakVals);
        }
        return peakList;
    }

    private String[] splitPeakVals(String peak) {
        int i = peak.indexOf(" ");
        String[] vals = new String[2];
        vals[0] = peak.substring(0, i);
        if (vals[0].lastIndexOf('.') == -1) vals[0] = vals[0]+".0";
        vals[1] = peak.substring(i+1, peak.length());
        if (vals[1].lastIndexOf('.') == -1) vals[1] = vals[1]+".0";
        return vals;
    }
}
