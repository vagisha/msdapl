/**
 * ProlucidResult.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile.prolucid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultIn;
import org.yeastrc.ms.domain.search.prolucid.impl.ProlucidResultDataBean;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.SQTSearchResult;

/**
 * 
 */
public class ProlucidResult extends SQTSearchResult implements ProlucidSearchResultIn{

    private MsSearchResultPeptide resultPeptide = null;
    
    private List<MsResidueModificationIn> searchDynaResidueMods;
    private List<MsTerminalModificationIn> searchDynaTermMods;
    
    private ProlucidResultDataBean resultData;
    
    
    public ProlucidResult(List<MsResidueModificationIn> searchDynaResidueMods, List<MsTerminalModificationIn> searchDynaTermMods) {
        super();
        if (searchDynaResidueMods != null)
            this.searchDynaResidueMods = searchDynaResidueMods;
        else
            this.searchDynaResidueMods = new ArrayList<MsResidueModificationIn>(0);
        
        if (searchDynaTermMods != null)
            this.searchDynaTermMods = searchDynaTermMods;
        else
            this.searchDynaTermMods = new ArrayList<MsTerminalModificationIn>(0);
        
        resultData = new ProlucidResultDataBean();
    }
    
    public void setNumMatchingIons(int numMatchingIons) {
       resultData.setMatchingIons(numMatchingIons);
    }

    public void setNumPredictedIons(int numPredictedIons) {
        resultData.setPredictedIons(numPredictedIons);
    }
    
    public void setPrimaryScoreRank(int primaryScoreRank) {
        resultData.setPrimaryScoreRank(primaryScoreRank);
    }

    public void setSecondaryScoreRank(int secondaryScoreRank) {
        resultData.setSecondaryScoreRank(secondaryScoreRank);
    }

    public void setMass(BigDecimal mass) {
        resultData.setCalculatedMass(mass);
    }

    public void setDeltaCN(BigDecimal deltaCN) {
        resultData.setDeltaCN(deltaCN);
    }
    
    public void setPrimaryScore(Double primaryScore) {
        resultData.setPrimaryScore(primaryScore);
    }
    
    public void setSecondaryScore(Double secondaryScore) {
        resultData.setSecondaryScore(secondaryScore);
    }
    
    @Override
    public MsSearchResultPeptide buildPeptideResult() throws SQTParseException {
        if (resultPeptide != null)
            return resultPeptide;
        
        resultPeptide = ProlucidResultPeptideBuilder.instance().build(getOriginalPeptideSequence(), searchDynaResidueMods, searchDynaTermMods);
        return resultPeptide;
    }

    @Override
    public ProlucidResultData getProlucidResultData() {
        return resultData;
    }
}
