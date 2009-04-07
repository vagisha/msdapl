/**
 * PeakConverterDouble.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.run.Peak;

/**
 * 
 */
public class PeakConverterNumber  {

    private static PeakConverterNumber instance;
    
    private PeakConverterNumber(){}
    
    public static PeakConverterNumber instance() {
        if(instance == null)
            instance = new PeakConverterNumber();
        return instance;
    }
    
    public List<Peak> convert(String peakString) {
        
        List<Peak> peakList = new ArrayList<Peak>();
        
        if (peakString == null || peakString.length() == 0)
            return peakList;
        
        String[] peaksStr = peakString.split("\\n");
        for (String peakStr: peaksStr) {
            String [] peakVals = splitPeakVals(peakStr);
            Peak peak = new Peak(Double.parseDouble(peakVals[0]), Float.parseFloat(peakVals[1]));
            peakList.add(peak);
        }
        return peakList;
    }

    private String[] splitPeakVals(String peak) {
        int i = peak.indexOf(" ");
        String[] vals = new String[2];
        vals[0] = peak.substring(0, i);
        vals[1] = peak.substring(i+1, peak.length());
        return vals;
    }
    
    public List<Peak> convert(byte[] peakData, boolean hasNumbers) throws IOException {
        
        if(!hasNumbers) {
            return convert(new String(peakData));
        }
        else {
            ByteArrayInputStream bis = null;
            DataInputStream dis = null;
            List<Peak> peaks = new ArrayList<Peak>();
            try {
                bis = new ByteArrayInputStream(peakData);
                dis = new DataInputStream(bis);
                while(true) {
                    try {
                        double mz = dis.readDouble();
                        float intensity = dis.readFloat();
                        peaks.add(new Peak(mz, intensity));
                    }
                    catch (EOFException e) {
                        break;
                    }
                }
            }
            finally {
                if(dis != null) dis.close();
                if(bis != null) bis.close();
            }
            return peaks;
        }
    }
}
