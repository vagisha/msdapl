/**
 * Peaks.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.ms.util.NumberUtils;

/**
 * Class encapsulating the peak data (m/z and intensity pairs) for a scan. 
 */
public class Peaks {

    private List<Peak> peaks;
    
    public Peaks() {
        peaks = new ArrayList<Peak>();
    }
    
    public int getPeaksCount() {
        return peaks.size();
    }
    
    public void addPeak(String mz, String intensity) throws Exception {
        BigDecimal mzNum = null;
        BigDecimal intensityNum = null;
        try {
            mzNum = new BigDecimal(mz);
        }
        catch (NumberFormatException e) {
            throw new Exception("Invalid mz value: "+mz);
        }
        try {
            intensityNum = new BigDecimal(intensity);
        }
        catch (NumberFormatException e) {
            throw new Exception("Invalid intensity value: "+mz);
        }
        peaks.add(new Peak(mzNum, intensityNum));
    }
    
    // used for storing to database
    protected byte[] getPeakDataBinary() {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        baos = new ByteArrayOutputStream();
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(peaksAsString());
            oos.flush();
            return baos.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (oos != null) {
                try {oos.close();}
                catch (IOException e) {e.printStackTrace();}
            }
        }
        return null;
    }
    
    private String peaksAsString() {
        StringBuilder buf = new StringBuilder();
        for (Peak peak: peaks) {
            buf.append(NumberUtils.trimTrailingZeros(peak.getMzString()));
            buf.append(":");
            buf.append(NumberUtils.trimTrailingZeros(peak.getIntensityString()));
            buf.append(";");
        }
        if (buf.length() > 0)
            buf.deleteCharAt(buf.length() -1);
        return buf.toString();
    }
    
    protected void setPeakDataBinary(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            parsePeaksAsString((String) ois.readObject());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if (ois != null) {
                try {ois.close();} 
                catch (IOException e) {e.printStackTrace();}
            }
        }
    }
    
    private void parsePeaksAsString(String peaksString) throws Exception {
        if (peaksString == null || peaksString.length() == 0)
            return;
        String[] peaks = peaksString.split(";");
        for (String peak: peaks) {
            String[] peakVals = splitPeakVals(peak);
            addPeak(peakVals[0], peakVals[1]);
        }
    }
    
    private String[] splitPeakVals(String peak) throws Exception {
        int i = peak.indexOf(":");
        if (i < 1)
            throw new Exception("Error parsing peak data: "+peak);
        String[] vals = new String[2];
        vals[0] = peak.substring(0, i);
        vals[1] = peak.substring(i+1, peak.length());
        return vals;
    }
    
    public Iterator<Peak> iterator() {
        return peaks.iterator();
    }
    
    public class Peak {

        private BigDecimal intensity;
        private BigDecimal mz;
        
        private Peak(BigDecimal mz, BigDecimal intensity) {
            this.mz = mz;
            this.intensity = intensity;
        }
        /**
         * @return the intensity
         */
        public BigDecimal getIntensity() {
            return intensity;
        }
       
        public String getIntensityString() {
            return intensity.toString();
        }
        /**
         * @return the mz
         */
        public BigDecimal getMz() {
            return mz;
        }
        
        public String getMzString() {
            return mz.toString();
        }
    }
}
