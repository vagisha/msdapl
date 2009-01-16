package org.yeastrc.ms.parser.sqtFile.percolator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.SQTSearchResult;
import org.yeastrc.ms.parser.sqtFile.prolucid.ProlucidResultPeptideBuilder;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestResultPeptideBuilder;

public class PercolatorAnalysisResult extends SQTSearchResult implements PercolatorResultIn {

    private MsSearchResultPeptide resultPeptide = null;
    
    private int numMatchingIons = -1;
    private int numPredictedIons = -1;
    
    private List<MsResidueModificationIn> searchDynaResidueMods;
    private List<MsTerminalModificationIn> searchDynaTermMods;
    
    private int xcorrRank = -1;
    private int spRank = -1;
    private BigDecimal mass; // Calculated M+H+ value for this sequence
    private BigDecimal deltaCN; 
    private double pep = -1.0;
    private Double discriminantScore = null;
    private double qvalue = -1.0;
    
    private Program searchProgram;
    
    
    public PercolatorAnalysisResult(List<MsResidueModificationIn> searchDynaResidueMods, List<MsTerminalModificationIn> searchDynaTermMods,
            Program searchProgram) {
        super();
        if (searchDynaResidueMods != null)
            this.searchDynaResidueMods = searchDynaResidueMods;
        else
            this.searchDynaResidueMods = new ArrayList<MsResidueModificationIn>(0);
        
        if (searchDynaTermMods != null)
            this.searchDynaTermMods = searchDynaTermMods;
        else
            this.searchDynaTermMods = new ArrayList<MsTerminalModificationIn>(0);
        
        this.searchProgram = searchProgram;
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

    
    @Override
    public Double getDiscriminantScore() {
        return discriminantScore;
    }
    
    public void setDiscriminantScore(Double score) {
        this.discriminantScore = score;
    }

    @Override
    public double getPosteriorErrorProbability() {
        return pep;
    }
    
    public void setPosteriorErrorProbability(double pep) {
        this.pep = pep;
    }

    @Override
    public double getQvalue() {
        return qvalue;
    }
    
    public void setQvalue(double qvalue) {
        this.qvalue = qvalue;
    }

    /**
     * This method returns null. Percolated SQT files do not have predicted retention time.
     */
    @Override
    public BigDecimal getPredictedRetentionTime() {
        return null;
    }
    
    public MsSearchResultPeptide buildPeptideResult() throws SQTParseException {
        if (resultPeptide != null)
            return resultPeptide;
        
        if(searchProgram == null)
            throw new SQTParseException("Cannot parse peptide string without know the name of the search program that created it", SQTParseException.FATAL);
        
        if(searchProgram == Program.SEQUEST || searchProgram == Program.EE_NORM_SEQUEST)
            resultPeptide = SequestResultPeptideBuilder.instance().build(getOriginalPeptideSequence(), searchDynaResidueMods);
        
        else if (searchProgram == Program.PROLUCID)
            resultPeptide = ProlucidResultPeptideBuilder.instance().build(getOriginalPeptideSequence(), searchDynaResidueMods, searchDynaTermMods);

        return resultPeptide;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("M\t");
        buf.append(xcorrRank);
        buf.append("\t");
        buf.append(spRank);
        buf.append("\t");
        buf.append(mass);
        buf.append("\t");
        buf.append(deltaCN.stripTrailingZeros());
        buf.append("\t");
        buf.append(qvalue);
        if(pep != -1.0) {
            buf.append("\t");
            buf.append(pep);
        }
        if(discriminantScore != -1.0) {
            buf.append("\t");
            buf.append(discriminantScore);
        }
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

}
