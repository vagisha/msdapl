package org.yeastrc.ms.dao.search;

import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDb;

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
     * @param runId
     * @param experimentId
     * @return database id of the search
     */
    public abstract int saveSearch(I search);

    /**
     * Deletes the search
     * @param searchId
     */
    public abstract void deleteSearch(int searchId);

}