/**
 * SQTSearchDataProvider.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.zip.DataFormatException;

import org.yeastrc.ms.domain.sqtFile.SQTSearch;
import org.yeastrc.ms.parser.sqtFile.SQTSearchDataProviderImpl.ScanResultIterator;

/**
 * 
 */
public interface SQTSearchDataProvider {

    public abstract String getFileName();
    
    public abstract SQTSearch getSearchData() throws DataFormatException;
    
    public ScanResultIterator scanResultIterator();
    
    public void close();
    
}
