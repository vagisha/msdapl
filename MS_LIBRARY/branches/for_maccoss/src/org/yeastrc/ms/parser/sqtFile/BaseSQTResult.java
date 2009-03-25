/**
 * BaseSQTResult.java
 * @author Vagisha Sharma
 * Feb 2, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;

/**
 * 
 */
public class BaseSQTResult extends SQTSearchResult {

    private final PeptideResultBuilder peptideResultBuilder;
    
    private MsSearchResultPeptide resultPeptide = null;
    
    private List<MsResidueModificationIn> searchDynaResidueMods;
    private List<MsTerminalModificationIn> searchDynaTermMods;
    
    public BaseSQTResult(PeptideResultBuilder peptideResultBuilder,
            List<MsResidueModificationIn> searchDynaResidueMods, 
            List<MsTerminalModificationIn> searchDynaTermMods) {
        
        this.peptideResultBuilder = peptideResultBuilder;
        
        if (searchDynaResidueMods != null)
            this.searchDynaResidueMods = searchDynaResidueMods;
        else
            this.searchDynaResidueMods = new ArrayList<MsResidueModificationIn>(0);
        
        if (searchDynaTermMods != null)
            this.searchDynaTermMods = searchDynaTermMods;
        else
            this.searchDynaTermMods = new ArrayList<MsTerminalModificationIn>(0);
    }
    
    @Override
    public MsSearchResultPeptide buildPeptideResult() throws SQTParseException {
        if (resultPeptide != null)
            return resultPeptide;
        
        resultPeptide = peptideResultBuilder.build(getOriginalPeptideSequence(), searchDynaResidueMods, searchDynaTermMods);
        return resultPeptide;
    }
}
