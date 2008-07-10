package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.IMsSearchModification;
import org.yeastrc.ms.domain.db.MsPeptideSearchDynamicMod;
import org.yeastrc.ms.domain.db.MsPeptideSearchStaticMod;
import org.yeastrc.ms.domain.db.MsSearchResultDynamicMod;

public interface MsPeptideSearchModDAO {

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search
    //-------------------------------------------------------------------------------------------
    public abstract List<MsPeptideSearchStaticMod> loadStaticModificationsForSearch(
            int searchId);

    public abstract void saveStaticModification(IMsSearchModification mod, int searchId);

    public abstract void deleteStaticModificationsForSearch(int searchId);

    public abstract List<MsPeptideSearchDynamicMod> loadDynamicModificationsForSearch(
            int searchId);

    public abstract int saveDynamicModification(IMsSearchModification mod, int searchId);

    
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
    
    public abstract List<MsSearchResultDynamicMod> loadDynamicModificationsForSearchResult(
            int resultId);
    
    public abstract void saveDynamicModificationForSearchResult(int resultId, int modificationId, int position);

    public void deleteDynamicModificationsForResult(int resultId);

}