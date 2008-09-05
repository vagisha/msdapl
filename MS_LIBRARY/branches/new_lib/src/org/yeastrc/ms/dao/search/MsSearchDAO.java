package org.yeastrc.ms.dao.search;

import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.SearchProgram;

public interface MsSearchDAO <I extends MsSearch, O extends MsSearchDb>{

    public abstract O loadSearch(int searchId);
    
    /**
     * Saves the search in the database. Also saves:
     * 1. any associated sequence database information used for the search
     * 2. any static modifications used for the search
     * 3. any dynamic modifications used for the search 
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