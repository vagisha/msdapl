/**
 * MsSearchResultDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.util.List;


/**
 * 
 */
public interface MsSearchResultDb extends MsSearchResultBase {

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
    
    /**
     * @return the proteinMatchList
     */
    public abstract List<MsSearchResultProteinDb> getProteinMatchList();
    
    /**
     * @return the peptideResult
     */
    public abstract MsSearchResultPeptideDb getResultPeptide();
   
}
