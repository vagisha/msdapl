/**
 * PeakConverterDouble.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class PeakConverterDouble  {

    
    public List<double[]> convert(String peakString) {
        
        List<double[]> peakList = new ArrayList<double[]>();
        
        if (peakString == null || peakString.length() == 0)
            return peakList;
        
        String[] peaksStr = peakString.split(";");
        for (String peak: peaksStr) {
            String [] peakVals = splitPeakVals(peak);
            double[] peakData = new double[2];
            peakData[0] = Double.parseDouble(peakVals[0]);
            peakData[1] = Double.parseDouble(peakVals[1]);
            peakList.add(peakData);
        }
        
        return peakList;
    }

    private String[] splitPeakVals(String peak) {
        int i = peak.indexOf(":");
        String[] vals = new String[2];
        vals[0] = peak.substring(0, i);
        vals[1] = peak.substring(i+1, peak.length());
        return vals;
    }
}
