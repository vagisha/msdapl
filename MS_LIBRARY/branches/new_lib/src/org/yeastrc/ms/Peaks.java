/**
 * Peaks.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 */
public class Peaks {

    List <Peak> peaks;
    
    public Peaks() {
        peaks = new ArrayList<Peak>();
    }
    
    public int getPeaksCount() {
        return peaks.size();
    }
    
    public Iterator<Peak> getIterator() {
        return peaks.iterator();
    }
    
    public void addPeak(float mz, double intensity) {
        peaks.add(new Peak(mz, intensity));
    }
}
