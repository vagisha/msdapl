/**
 * MS2RunDataProvider.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.Iterator;

import org.yeastrc.ms.domain.ms2File.MS2Run;
import org.yeastrc.ms.domain.ms2File.MS2Scan;

/**
 * 
 */
public interface MS2RunDataProvider  {

    public abstract String getFileName();
    
    public abstract String getSha1Sum();
    
    public abstract MS2Run getRunData();
    
    public abstract Iterator<MS2Scan> scanIterator();
}
