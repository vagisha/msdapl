package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationDb;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueModDb;
import org.yeastrc.ms.domain.search.MsResultDynamicTerminalModDb;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModificationDb;

public interface MsSearchModificationDAO {

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResidueModificationDb> loadStaticResidueModsForSearch(int searchId);

    public abstract void saveStaticResidueMod(MsResidueModification mod, int searchId);

    public abstract void deleteStaticResidueModsForSearch(int searchId);

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResidueModificationDb> loadDynamicResidueModsForSearch(int searchId);

    public abstract int saveDynamicResidueMod(MsResidueModification mod, int searchId);

    /**
     * This will delete all dynamic modifications for a search.
     * If any of the modifications are related to results from the search 
     * they are deleted as well (from the msDynamicModResult table).
     * @param searchId
     */
    public abstract void deleteDynamicModificationsForSearch(int searchId);
    
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsTerminalModificationDb> loadStaticTerminalModsForSearch(int searchId);

    public abstract void saveStaticTerminalMod(MsTerminalModification mod, int searchId);

    public abstract void deleteStaticTerminalModsForSearch(int searchId);
    
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsTerminalModificationDb> loadDynamicTerminalModsForSearch(int searchId);

    public abstract int saveDynamicTerminalMod(MsTerminalModification mod, int searchId);

    public abstract void deleteDynamicTerminalModsForSearch(int searchId);
    
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search result (DYNAMIC RESIDUE)
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResultDynamicResidueModDb> loadDynamicResidueModsForResult(int resultId);
    
    public abstract void saveDynamicResidueModForResult(MsResultDynamicResidueMod mod, 
            int resultId, int modificationId);

    public abstract void saveAllDynamicResidueModsForResult(List<MsResultDynamicResidueModDb> modList);
    
    public void deleteDynamicResidueModsForResult(int resultId);
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search result (DYNAMIC TERMINAL)
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResultDynamicTerminalModDb> loadDynamicTerminalModsForResult(int resultId);
    
    public abstract void saveDynamicTerminalModForResult(int resultId, int modificationId);

    public abstract void saveAllDynamicTerminalModsForResult(List<MsResultDynamicTerminalModDb> modList);
    
    public void deleteDynamicTerminalModsForResult(int resultId);
    
}