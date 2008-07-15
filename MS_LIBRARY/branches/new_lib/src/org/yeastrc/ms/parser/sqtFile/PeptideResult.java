package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.MsSearchResultPeptide;
import org.yeastrc.ms.domain.MsSearchResultProtein;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResult;

/**
 * Represents a 'M' line in the SQT file
 */
public class PeptideResult implements SQTSearchResult {

    private int xcorrRank;
    private int spRank;
    private BigDecimal mass; // Calculated M+H+ value for this sequence
    private BigDecimal deltaCN; 
    private BigDecimal xcorr;
    private BigDecimal sp;
    private int numMatchingIons;    // Fragment ions matching this sequence 
    private int numPredictedIons;   // Fragment ions predicted for this sequence 
    private String sequence;
    private char validationStatus;  // Manual validation status:
    // Y: Yes, this is a valid ID
    // M: This ID may be valid
    // N: No, this is an invalid ID
    // U: This ID has not been validated 

    private int charge;

    private List<DbLocus> matchingLoci;

    private List<DynamicModification> seachDynaMods;

    public PeptideResult(List<DynamicModification> searchDynamicMods) {
        matchingLoci = new ArrayList<DbLocus>();
        if (searchDynamicMods != null)
            this.seachDynaMods = searchDynamicMods;
        else
            seachDynaMods = new ArrayList<DynamicModification>(0);
    }

    /**
     * @return the xcorrRank
     */
    public int getxCorrRank() {
        return xcorrRank;
    }

    /**
     * @param xcorrRank the xcorrRank to set
     */
    public void setxCorrRank(int xcorrRank) {
        this.xcorrRank = xcorrRank;
    }

    /**
     * @return the spRank
     */
    public int getSpRank() {
        return spRank;
    }

    /**
     * @param spRank the spRank to set
     */
    public void setSpRank(int spRank) {
        this.spRank = spRank;
    }

    /**
     * @return the mass
     */
    public BigDecimal getCalculatedMass() {
        return mass;
    }

    /**
     * @param mass the mass to set
     */
    public void setMass(BigDecimal mass) {
        this.mass = mass;
    }

    /**
     * @return the deltaCN
     */
    public BigDecimal getDeltaCN() {
        return deltaCN;
    }

    /**
     * @param deltaCN the deltaCN to set
     */
    public void setDeltaCN(BigDecimal deltaCN) {
        this.deltaCN = deltaCN;
    }

    /**
     * @return the xcorr
     */
    public BigDecimal getxCorr() {
        return xcorr;
    }

    /**
     * @param xcorr the xcorr to set
     */
    public void setXcorr(BigDecimal xcorr) {
        this.xcorr = xcorr;
    }

    /**
     * @return the sp
     */
    public BigDecimal getSp() {
        return sp;
    }

    /**
     * @param sp the sp to set
     */
    public void setSp(BigDecimal sp) {
        this.sp = sp;
    }

    /**
     * @return the numMatchingIons
     */
    public int getNumIonsMatched() {
        return numMatchingIons;
    }

    /**
     * @param numMatchingIons the numMatchingIons to set
     */
    public void setNumMatchingIons(int numMatchingIons) {
        this.numMatchingIons = numMatchingIons;
    }

    /**
     * @return the numPredictedIons
     */
    public int getNumIonsPredicted() {
        return numPredictedIons;
    }

    /**
     * @param numPredictedIons the numPredictedIons to set
     */
    public void setNumPredictedIons(int numPredictedIons) {
        this.numPredictedIons = numPredictedIons;
    }

    /**
     * @return the sequence
     */
    public String getResultSequence() {
        return sequence;
    }

    /**
     * @param sequence the sequence to set
     */
    public void setResultSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * @return the validationStatus
     */
    public ValidationStatus getValidationStatus() {
        return ValidationStatus.instance(this.validationStatus);
    }

    /**
     * @param validationStatus the validationStatus to set
     */
    public void setValidationStatus(char validationStatus) {
        this.validationStatus = validationStatus;
    }

    /**
     * @return the matchingLoci
     */
    public List<DbLocus> getMatchingLoci() {
        return matchingLoci;
    }

    /**
     * @param matchingLoci the matchingLoci to set
     */
    public void setMatchingLoci(List<DbLocus> matchingLoci) {
        this.matchingLoci = matchingLoci;
    }

    public void addMatchingLocus(String accession, String description) {
        DbLocus locus = new DbLocus(accession, description);
        addMatchingLocus(locus);
    }

    public void addMatchingLocus(DbLocus locus) {
        matchingLoci.add(locus);
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
        buf.append(deltaCN);
        buf.append("\t");
        buf.append(xcorr);
        buf.append("\t");
        buf.append(sp);
        buf.append("\t");
        buf.append(numMatchingIons);
        buf.append("\t");
        buf.append(numPredictedIons);
        buf.append("\t");
        buf.append(sequence);
        buf.append("\t");
        buf.append(validationStatus);

        buf.append("\n");

        for (DbLocus locus: matchingLoci) {
            buf.append(locus.toString());
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() -1); // delete last new line
        return buf.toString();
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getCharge() {
        return charge;
    }

    public List<? extends MsSearchResultProtein> getProteinMatchList() {
        return this.matchingLoci;
    }

    public MsSearchResultPeptide getResultPeptide() {
        return MsSearchResultPeptideBuilder.instance().build(sequence, seachDynaMods);
    }
    
}
