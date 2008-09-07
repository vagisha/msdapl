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
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.SQTSearchResult;

/**
 * 
 */
public class ProlucidResult extends SQTSearchResult implements ProlucidSearchResultIn{

    private MsSearchResultPeptide resultPeptide = null;
    
    private int numMatchingIons = -1;
    private int numPredictedIons = -1;
    
    private List<MsResidueModificationIn> searchDynaResidueMods;
    private List<MsTerminalModificationIn> searchDynaTermMods;
    
    private BigDecimal mass; // Calculated M+H+ value for this sequence
    
    private int xcorrRank = -1;
    private int spRank = -1;
    
    private Double binomialScore;
    private BigDecimal sp;
    private BigDecimal xcorr;
    private Double zscore;
    private BigDecimal deltaCN;
    
    
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
    }
    
    /**
     * @param numMatchingIons the numMatchingIons to set
     */
    public void setNumMatchingIons(int numMatchingIons) {
        this.numMatchingIons = numMatchingIons;
    }

    /**
     * @param numPredictedIons the numPredictedIons to set
     */
    public void setNumPredictedIons(int numPredictedIons) {
        this.numPredictedIons = numPredictedIons;
    }
    
    /**
     * @param xcorrRank the xcorrRank to set
     */
    public void setxCorrRank(int xcorrRank) {
        this.xcorrRank = xcorrRank;
    }

    /**
     * @param spRank the spRank to set
     */
    public void setSpRank(int spRank) {
        this.spRank = spRank;
    }

    /**
     * @param mass the mass to set
     */
    public void setMass(BigDecimal mass) {
        this.mass = mass;
    }

    /**
     * @param deltaCN the deltaCN to set
     */
    public void setDeltaCN(BigDecimal deltaCN) {
        this.deltaCN = deltaCN;
    }

    /**
     * @param xcorr the xcorr to set
     */
    public void setXcorr(BigDecimal xcorr) {
        this.xcorr = xcorr;
    }

    /**
     * @param sp the sp to set
     */
    public void setSp(BigDecimal sp) {
        this.sp = sp;
    }
    
    public void setBinomialScore(Double binomialScore) {
        this.binomialScore = binomialScore;
    }
    
    public void setZscore(Double zscore) {
        this.zscore = zscore;
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
        return new ProlucidResultData() {

            @Override
            public Double getBinomialProbability() {
                return binomialScore;
            }

            @Override
            public BigDecimal getCalculatedMass() {
                return mass;
            }

            @Override
            public BigDecimal getDeltaCN() {
                return deltaCN;
            }

            @Override
            public int getMatchingIons() {
                return numMatchingIons;
            }

            @Override
            public int getPredictedIons() {
                return numPredictedIons;
            }

            @Override
            public BigDecimal getSp() {
                return sp;
            }

            @Override
            public int getSpRank() {
                return spRank;
            }

            @Override
            public Double getZscore() {
                return zscore;
            }

            @Override
            public BigDecimal getxCorr() {
                return xcorr;
            }

            @Override
            public int getxCorrRank() {
                return xcorrRank;
            }};
    }
}
