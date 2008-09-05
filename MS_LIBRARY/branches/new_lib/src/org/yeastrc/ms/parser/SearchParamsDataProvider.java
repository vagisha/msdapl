/**
 * SearchParamsDataProvider.java
 * @author Vagisha Sharma
 * Aug 22, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.SearchProgram;

/**
 * 
 */
public interface SearchParamsDataProvider {

    public abstract MsSearchDatabase getSearchDatabase();

    public abstract MsEnzymeIn getSearchEnzyme();

    public abstract List<MsResidueModificationIn> getDynamicResidueMods();

    public abstract List<MsResidueModificationIn> getStaticResidueMods();

    public abstract List<MsTerminalModificationIn> getStaticTerminalMods();

    public abstract List<MsTerminalModificationIn> getDynamicTerminalMods();
    
    public abstract SearchProgram getSearchProgram();
    
}
