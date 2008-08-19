package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.dao.search.ibatis.MsSearchModificationDAOImpl.MsSearchResultModSqlMapParam;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationDb;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueModDb;

public interface MsSearchModificationDAO {

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResidueModificationDb> loadStaticModificationsForSearch(int searchId);

    public abstract void saveStaticModification(MsResidueModification mod, int searchId);

    public abstract void deleteStaticModificationsForSearch(int searchId);

    public abstract List<MsResidueModificationDb> loadDynamicModificationsForSearch(int searchId);

    public abstract int saveDynamicModification(MsResidueModification mod, int searchId);

//    public abstract List<MsResidueModificationDb> loadDynamicModificationsForSearch(int searchId);
//
//    public abstract int saveDynamicModification(MsResidueModification mod, int searchId);
    
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
    
    public abstract List<MsResultDynamicResidueModDb> loadDynamicModificationsForSearchResult(
            int resultId);
    
    public abstract void saveDynamicModificationForSearchResult(MsResultDynamicResidueModDb mod, 
            int resultId, int modificationId);

    public abstract void saveAllDynamicModificationForSearchResult(List<MsSearchResultModSqlMapParam> modList);
    
    public void deleteDynamicModificationsForResult(int resultId);

}