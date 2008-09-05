package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.ResultModIdentifier;
import org.yeastrc.ms.domain.search.ResultResidueModIdentifier;

public interface MsSearchModificationDAO {

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResidueModification> loadStaticResidueModsForSearch(int searchId);

    public abstract void saveStaticResidueMod(MsResidueModificationIn mod, int searchId);

    public abstract void deleteStaticResidueModsForSearch(int searchId);

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResidueModification> loadDynamicResidueModsForSearch(int searchId);

    public abstract int loadMatchingDynamicResidueModId(int searchId, MsResidueModificationIn mod);
    
    public abstract int saveDynamicResidueMod(MsResidueModificationIn mod, int searchId);

    /**
     * This will delete all dynamic modifications for a search.
     * If any of the modifications are related to results from the search 
     * they are deleted as well (from the msDynamicModResult table).
     * @param searchId
     */
    public abstract void deleteDynamicResidueModsForSearch(int searchId);
    
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsTerminalModification> loadStaticTerminalModsForSearch(int searchId);

    public abstract void saveStaticTerminalMod(MsTerminalModificationIn mod, int searchId);

    public abstract void deleteStaticTerminalModsForSearch(int searchId);
    
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsTerminalModification> loadDynamicTerminalModsForSearch(int searchId);
    
    public abstract int loadMatchingDynamicTerminalModId(int searchId, MsTerminalModificationIn mod);

    public abstract int saveDynamicTerminalMod(MsTerminalModificationIn mod, int searchId);

    public abstract void deleteDynamicTerminalModsForSearch(int searchId);
    
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search result (DYNAMIC RESIDUE)
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResultDynamicResidueMod> loadDynamicResidueModsForResult(int resultId);
    
    public abstract void saveDynamicResidueModForResult(int resultId, int modificationId, int modifiedPosition);
    
    public abstract void saveDynamicResidueModForResult(ResultResidueModIdentifier modIdentifier);

    public abstract void saveAllDynamicResidueModsForResult(List<ResultResidueModIdentifier> modList);
    
    public void deleteDynamicResidueModsForResult(int resultId);
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search result (DYNAMIC TERMINAL)
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResultTerminalMod> loadDynamicTerminalModsForResult(int resultId);
    
    public abstract void saveDynamicTerminalModForResult(int resultId, int modificationId);
    
    public abstract void saveDynamicTerminalModForResult(ResultModIdentifier modIdentifier);

    public abstract void saveAllDynamicTerminalModsForResult(List<ResultModIdentifier> modList);
    
    public void deleteDynamicTerminalModsForResult(int resultId);
    
}