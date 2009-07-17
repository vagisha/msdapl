/**
 * InteractPepXmlDataProvider.java
 * @author Vagisha Sharma
 * Jul 14, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.pepxml.PepXmlSearchScanIn;

/**
 * 
 */
public interface InteractPepXmlDataProvider <T extends PepXmlSearchScanIn> {

    public abstract boolean hasNextRunSearch() throws DataProviderException;
    
    public abstract String getFileName();
    
    public abstract MsRunSearchIn getSearchHeader() throws DataProviderException;
    
    public abstract boolean hasNextSearchScan() throws DataProviderException;
    
    public abstract T getNextSearchScan() throws DataProviderException;
    
    public abstract void close();
}
