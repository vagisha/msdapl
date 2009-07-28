package org.yeastrc.ms.parser.sqtFile.sequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.SQTSearchResult;


public class SequestResult extends SQTSearchResult implements SequestSearchResultIn {

    private MsSearchResultPeptide resultPeptide = null;
    
    private int numMatchingIons = -1;
    private int numPredictedIons = -1;
    
    private List<MsResidueModificationIn> searchDynaResidueMods;
    
    private int xcorrRank = -1;
    private int spRank = -1;
    private BigDecimal calclatedMass; // Calculated M+H+ value for this sequence
    private BigDecimal deltaCN; 
    private BigDecimal xcorr;
    private BigDecimal sp;
    private Double evalue;
    
    public SequestResult(List<MsResidueModificationIn> searchDynaResidueMods) {
        super();
        if (searchDynaResidueMods != null)
            this.searchDynaResidueMods = searchDynaResidueMods;
        else
            this.searchDynaResidueMods = new ArrayList<MsResidueModificationIn>(0);
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
    public void setCalculatedMass(BigDecimal mass) {
        this.calclatedMass = mass;
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

    /**
     * @param evalue
     */
    public void setEvalue(Double evalue) {
        this.evalue = evalue;
    }
    
    public MsSearchResultPeptide buildPeptideResult() throws SQTParseException {
        if (resultPeptide != null)
            return resultPeptide;
        
        resultPeptide = SequestResultPeptideBuilder.instance().build(getOriginalPeptideSequence(), searchDynaResidueMods, null);
        return resultPeptide;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("M\t");
        buf.append(xcorrRank);
        buf.append("\t");
        buf.append(spRank);
        buf.append("\t");
        buf.append(calclatedMass);
        buf.append("\t");
        buf.append(deltaCN.stripTrailingZeros());
        buf.append("\t");
        buf.append(xcorr.stripTrailingZeros());
        buf.append("\t");
        buf.append(sp.stripTrailingZeros());
        buf.append("\t");
        buf.append(numMatchingIons);
        buf.append("\t");
        buf.append(numPredictedIons);
        buf.append("\t");
        buf.append(getOriginalPeptideSequence());
        buf.append("\t");
        buf.append(getValidationStatus());
    
        buf.append("\n");
    
        for (MsSearchResultProteinIn locus: getProteinMatchList()) {
            buf.append(locus.toString());
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() -1); // delete last new line
        return buf.toString();
    }
    
    @Override
    public SequestResultData getSequestResultData() {
        return new SequestResultData() {

            public BigDecimal getCalculatedMass() {
                return calclatedMass;
            }
            public BigDecimal getDeltaCN() {
                return deltaCN;
            }
            public Double getEvalue() {
                return evalue;
            }
            public int getMatchingIons() {
                return numMatchingIons;
            }
            public int getPredictedIons() {
                return numPredictedIons;
            }
            public BigDecimal getSp() {
                return sp;
            }
            public int getSpRank() {
                return spRank;
            }
            public BigDecimal getxCorr() {
                return xcorr;
            }
            public int getxCorrRank() {
                return xcorrRank;
            }};
    }
}
