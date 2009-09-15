/**
 * PepxmlDataProvider.java
 * @author Vagisha Sharma
 * Sep 13, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.pepxml.PepXmlSearchScanIn;

/**
 * 
 */
public interface PepxmlDataProvider <T extends PepXmlSearchScanIn> {

    public abstract String getFileName();
    
    public abstract MsRunSearchIn getSearchHeader() throws DataProviderException;
    
    public abstract boolean hasNextSearchScan() throws DataProviderException;
    
    public abstract T getNextSearchScan() throws DataProviderException;
    
    public abstract void close();
}
