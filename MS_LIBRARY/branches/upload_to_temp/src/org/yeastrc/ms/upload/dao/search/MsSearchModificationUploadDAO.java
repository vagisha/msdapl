package org.yeastrc.ms.upload.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueModIds;
import org.yeastrc.ms.domain.search.MsResultTerminalModIds;
import org.yeastrc.ms.domain.search.MsTerminalModification;

public interface MsSearchModificationUploadDAO {

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public abstract void saveStaticResidueMod(MsResidueModification mod);


    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsResidueModification> loadDynamicResidueModsForSearch(int searchId);

    public abstract int saveDynamicResidueMod(MsResidueModification mod);


    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public abstract void saveStaticTerminalMod(MsTerminalModification mod);

    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public abstract List<MsTerminalModification> loadDynamicTerminalModsForSearch(int searchId);

    public abstract int saveDynamicTerminalMod(MsTerminalModification mod);

    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search result (DYNAMIC RESIDUE)
    //-------------------------------------------------------------------------------------------
    public abstract void saveAllDynamicResidueModsForResult(List<MsResultResidueModIds> modList);

    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search result (DYNAMIC TERMINAL)
    //-------------------------------------------------------------------------------------------
    public abstract void saveAllDynamicTerminalModsForResult(List<MsResultTerminalModIds> modList);
    
    
}