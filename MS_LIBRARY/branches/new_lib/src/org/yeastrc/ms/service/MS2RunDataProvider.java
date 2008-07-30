/**
 * MS2RunDataProvider.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import org.yeastrc.ms.domain.ms2File.MS2Run;
import org.yeastrc.ms.domain.ms2File.MS2Scan;

/**
 * 
 */
public interface MS2RunDataProvider  {

    public abstract String getFileName();
    
    public abstract MS2Run getRunHeader() throws Exception;
    
    public abstract boolean hasNextScan();
    
    public abstract MS2Scan getNextScan() throws Exception;
    
    public abstract void close();
}
