/**
 * MsSearchResultDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

/**
 * 
 */
public interface MsSearchResultDb extends MsSearchResult {

    /**
     * @return database id of the search this result belongs to.
     */
    public abstract int getSearchId();
    
    /**
     * @return database id of the scan for which this result was returned. 
     */
    public abstract int getScanId();
    
    /**
     * @return database id of the result.
     */
    public abstract int getId();
}
