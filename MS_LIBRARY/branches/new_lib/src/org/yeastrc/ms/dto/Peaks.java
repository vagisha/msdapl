/**
 * Peaks.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * 
 */
public class Peaks {

    private BigDecimal[] mzList;
    private float[] intensityList;
    
    
    public int getPeaksCount() {
        if (mzList != null)
            return mzList.length;
        return 0;
    }
    
    // method will be used by parsers
    public void setPeakData(List<String> mzList, List<String> intensityList) throws Exception {
        if (mzList == null)
            throw new Exception("m/z list cannot be null");
        if (intensityList == null)
            throw new Exception("intensity list cannot be null");
        if (mzList.size() != intensityList.size()) 
            throw new Exception("m/z list and intensity list must have the same number of elements");
        
        setMzList(mzList);
        setIntensityList(intensityList);
    }
    
    private void setMzList(List <String> mzStrList) throws Exception {
        mzList = new BigDecimal[mzStrList.size()];
        int i = 0;
        for (String mz: mzStrList) 
            mzList[i++] = new BigDecimal(mz);
    }
    
    private void setIntensityList(List <String> intStrList) throws Exception {
        intensityList = new float[intStrList.size()];
        int i = 0;
        for (String intensity: intStrList)
            intensityList[i++] = Float.parseFloat(intensity);
    }
    
    // used for storing to database
    protected byte[] getPeakDataBinary() {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        baos = new ByteArrayOutputStream();
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(mzList);
            oos.writeObject(intensityList);
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
    
    protected void setPeakDataBinary(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            mzList = (BigDecimal[]) ois.readObject();
            intensityList = (float[]) ois.readObject();
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
    
    public static boolean isValidPeakMz(String mz) {
        try {
            new BigDecimal(mz);
        }
        catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public static boolean isValidPeakIntensity(String intensity) {
        try {
            Float.parseFloat(intensity);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public PeaksIterator iterator() {
        return new PeaksIterator();
    }
    
    public class PeaksIterator implements Iterator<Peak> {

        private int index = 0;
        
        @Override
        public boolean hasNext() {
            return index < getPeaksCount();
        }

        @Override
        public Peak next() {
            if (hasNext()) {
                return new Peak(mzList[index], intensityList[index]);
            }
            throw new IndexOutOfBoundsException("Only "+getPeaksCount()+" peaks!");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("PeakIterator does not implement remove()");
        }
    }
    
    public class Peak {

        private float intensity;
        private BigDecimal mz;
        
        public Peak(BigDecimal mz, float intensity) {
            this.mz = mz;
            this.intensity = intensity;
        }
        /**
         * @return the intensity
         */
        public float getIntensity() {
            return intensity;
        }
       
        public String getIntensityString() {
            return String.valueOf(intensity);
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
