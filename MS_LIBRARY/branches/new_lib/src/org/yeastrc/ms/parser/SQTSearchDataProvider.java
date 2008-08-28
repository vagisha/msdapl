/**
 * SQTSearchDataProvider.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;

/**
 * 
 */
public interface SQTSearchDataProvider {

    public abstract String getFileName();
    
    public abstract SQTRunSearch getSearchHeader() throws DataProviderException;
    
    public abstract boolean hasNextSearchScan();
    
    public abstract SQTSearchScan getNextSearchScan() throws DataProviderException;
    
    public abstract void close();
}


