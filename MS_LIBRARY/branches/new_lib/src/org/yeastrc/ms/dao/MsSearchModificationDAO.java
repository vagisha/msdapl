package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.MsSearchModification;
import org.yeastrc.ms.domain.MsSearchModificationDb;
import org.yeastrc.ms.domain.MsSearchResultDynamicModDb;
import org.yeastrc.ms.domain.MsSearchResultModification;

public interface MsSearchModificationDAO {

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search
    //-------------------------------------------------------------------------------------------
    public abstract List<MsSearchModificationDb> loadStaticModificationsForSearch(int searchId);

    public abstract void saveStaticModification(MsSearchModification mod, int searchId);

    public abstract void deleteStaticModificationsForSearch(int searchId);

    public abstract List<MsSearchModificationDb> loadDynamicModificationsForSearch(int searchId);

    public abstract int saveDynamicModification(MsSearchModification mod, int searchId);

    
    /**
     * This will delete all dynamic modifications for a search.
     * If any of the modifications are related to results from the search 
     * they are deleted as well (from the msDynamicModResult table).
     * @param searchId
     */
    public abstract void deleteDynamicModificationsForSearch(int searchId);
    
    //-------------------------------------------------------------------------------------------
    // Modifications (dynamic only) associated with a search result
    //-------------------------------------------------------------------------------------------
    
    public abstract List<MsSearchResultDynamicModDb> loadDynamicModificationsForSearchResult(
            int resultId);
    
    public abstract void saveDynamicModificationForSearchResult(MsSearchResultModification mod, 
            int resultId, int modificationId);

    public void deleteDynamicModificationsForResult(int resultId);

}