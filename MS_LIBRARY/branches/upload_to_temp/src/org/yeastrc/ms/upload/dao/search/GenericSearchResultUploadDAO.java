/**
 * GenericSearchResultDAO.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;

/**
 * 
 */
public interface GenericSearchResultUploadDAO {

    public abstract int numResultsForRunSearchScanChargeMass(int runSearchId, int scanId, int charge, BigDecimal mass);
    
    public abstract List<MsSearchResult> loadResultForSearchScanChargePeptide(int runSearchId, int scanId, int charge, String peptide);
    
    
    /**
     * Saves the search result in the msPeptideSearchResult table. 
     * Any data associated with the result (e.g. protein matches, dynamic modifications)
     * are NOT saved.
     * @param searchResult
     * @param runSearchId
     * @param scanId
     * @return
     */
    public abstract int saveResultOnly(MsSearchResultIn searchResult, int runSearchId, int scanId);
    
    
    public abstract <T extends MsSearchResult> List<Integer> saveResultsOnly(List<T> results);
    
    /**
     * Deletes the search results (msRunSearchResult table) with the 
     * given runSearchId
     * @param resultId
     */
    public abstract void deleteResultsForRunSearch(int runSearchId);
}
