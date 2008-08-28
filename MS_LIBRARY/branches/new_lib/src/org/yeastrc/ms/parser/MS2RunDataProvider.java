/**
 * MS2RunDataProvider.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import org.yeastrc.ms.domain.run.ms2file.MS2Run;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;

/**
 * 
 */
public interface MS2RunDataProvider  {

    public abstract String getFileName();
    
    public abstract MS2Run getRunHeader() throws DataProviderException;
    
    public abstract boolean hasNextScan();
    
    public abstract MS2Scan getNextScan() throws DataProviderException;
    
    public abstract void close();
}
