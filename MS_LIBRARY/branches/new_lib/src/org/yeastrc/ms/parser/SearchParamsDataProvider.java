/**
 * SearchParamsDataProvider.java
 * @author Vagisha Sharma
 * Aug 22, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;

/**
 * 
 */
public interface SearchParamsDataProvider {

    public abstract MsSearchDatabase getSearchDatabase();

    public abstract MsEnzyme getSearchEnzyme();

    public abstract List<MsResidueModification> getDynamicResidueMods();

    public abstract List<MsResidueModification> getStaticResidueMods();

    public abstract List<MsTerminalModification> getStaticTerminalMods();

    public abstract List<MsTerminalModification> getDynamicTerminalMods();
    
    public abstract String getSearchProgramName();
    
}
