/**
 * GenericSearchDAO.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search;

import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.SearchProgram;

/**
 * 
 */
public interface GenericSearchDAO <I extends MsSearchIn, O extends MsSearch> {

    public abstract O loadSearch(int searchId);
    
    /**
     * Saves the search in the database. Also saves:
     * 1. any associated sequence database information used for the search
     * 2. any residue (static and dynamic) modifications used for the search
     * 4. any terminal (static and dynamic) modifications
     * 5. any associated enzyme information for the search.
     * @param search
     * @param sequenceDatabaseId -- id from nrseq's tblDatabase table
     * @return database id of the search
     */
    public abstract int saveSearch(I search, int sequenceDatabaseId);
    
    /**
     * Updates the value of the analysisProgramVersion in msSearch table
     * @param searchId
     * @param versionStr
     * @return number of rows updated
     */
    public abstract int updateSearchProgramVersion(int searchId, String versionStr);
    

    /**
     * Updates the value of the analysisProgram in msSearch table
     * @param searchId
     * @param program
     * @return number of rows updated.
     */
    public abstract int updateSearchProgram(int searchId, SearchProgram program);
    
    /**
     * Deletes the search
     * @param searchId
     */
    public abstract void deleteSearch(int searchId);
}
