/**
 * SQTSearchDataProvider.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import org.yeastrc.ms.domain.sqtFile.SQTSearch;
import org.yeastrc.ms.domain.sqtFile.SQTSearchScan;

/**
 * 
 */
public interface SQTSearchDataProvider {

    public abstract String getFileName();
    
    public abstract SQTSearch getSearchHeader() throws Exception;
    
    public abstract boolean hasNextSearchScan();
    
    public abstract SQTSearchScan getNextSearchScan() throws Exception;
    
    public abstract void close();
}


