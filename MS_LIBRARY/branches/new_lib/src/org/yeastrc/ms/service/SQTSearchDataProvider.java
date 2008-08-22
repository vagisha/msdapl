/**
 * SQTSearchDataProvider.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.parser.DataProviderException;

/**
 * 
 */
public interface SQTSearchDataProvider <E extends SQTSearchScan> {

    public abstract String getFileName();
    
    public abstract SQTRunSearch getSearchHeader() throws DataProviderException;
    
    public abstract boolean hasNextSearchScan();
    
    public abstract E getNextSearchScan() throws DataProviderException;
    
    public abstract void close();
}


