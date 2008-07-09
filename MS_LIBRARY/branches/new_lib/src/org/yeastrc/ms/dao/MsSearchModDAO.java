package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.IMsSearchDynamicMod;
import org.yeastrc.ms.dto.IMsSearchMod;
import org.yeastrc.ms.dto.MsSearchDynamicMod;
import org.yeastrc.ms.dto.MsSearchMod;
import org.yeastrc.ms.dto.MsSearchResultDynamicMod;

public interface MsSearchModDAO {

    public abstract List<MsSearchMod> loadStaticModificationsForSearch(
            int searchId);

    public abstract void saveStaticModification(IMsSearchMod mod);

    public abstract void deleteStaticModificationsForSearch(int searchId);

    public abstract List<MsSearchDynamicMod> loadDynamicModificationsForSearch(
            int searchId);

    public abstract int saveDynamicModification(IMsSearchDynamicMod mod);

    /**
     * This will delete all dynamic modifications for a search result.
     * If any of the modifications are related to results from the search 
     * they are deleted as well (from the msDynamicModResult table).
     * @param searchId
     */
    public abstract void deleteDynamicModificationsForSearch(int searchId);
    
    public abstract List<MsSearchResultDynamicMod> loadDynamicModificationsForSearchResult(
            int resultId);
    
    public abstract void saveDynamicModificationForSearchResult(int resultId, int modificationId, int position);

    public void deleteDynamicModificationsForResult(int resultId);

}