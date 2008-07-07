package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 'M' line in the SQT file
 */
public class PeptideResult {

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
    
    private List<DbLocus> matchingLoci;
    
    public PeptideResult() {
        matchingLoci = new ArrayList<DbLocus>();
    }

    /**
     * @return the xcorrRank
     */
    public int getXcorrRank() {
        return xcorrRank;
    }

    /**
     * @param xcorrRank the xcorrRank to set
     */
    public void setXcorrRank(int xcorrRank) {
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
    public BigDecimal getMass() {
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
    public BigDecimal getXcorr() {
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
    public int getNumMatchingIons() {
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
    public int getNumPredictedIons() {
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
    public String getSequence() {
        return sequence;
    }

    /**
     * @param sequence the sequence to set
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * @return the validationStatus
     */
    public char getValidationStatus() {
        return validationStatus;
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
}
