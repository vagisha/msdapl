package edu.uwpr.protinfer.pepxml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SearchHit {

    private String peptide;
    private char preResidue;
    private char postResidue;
    private int numPredictedIons;
    private int numMatchedIons;
    private BigDecimal calcNeutralMass;
//    private String matchProteinAccession;
    private int numMatchingProteins;
    
    private BigDecimal xcorr;
    private BigDecimal deltaCn;
    private BigDecimal spScore;
    private int spRank;
    private int xcorrRank;
    
    private List<ProteinHit> proteinHits;
    
    public SearchHit() {
        proteinHits = new ArrayList<ProteinHit>();
    }

    /**
     * @return the peptide
     */
    public String getPeptide() {
        return peptide;
    }

    /**
     * @param peptide the peptide to set
     */
    public void setPeptide(String peptide) {
        this.peptide = peptide;
    }

    /**
     * @return the preResidue
     */
    public char getPreResidue() {
        return preResidue;
    }

    /**
     * @param preResidue the preResidue to set
     */
    public void setPreResidue(char preResidue) {
        this.preResidue = preResidue;
    }

    /**
     * @return the postResidue
     */
    public char getPostResidue() {
        return postResidue;
    }

    /**
     * @param postResidue the postResidue to set
     */
    public void setPostResidue(char postResidue) {
        this.postResidue = postResidue;
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
     * @return the numMatchedIons
     */
    public int getNumMatchedIons() {
        return numMatchedIons;
    }

    /**
     * @param numMatchedIons the numMatchedIons to set
     */
    public void setNumMatchedIons(int numMatchedIons) {
        this.numMatchedIons = numMatchedIons;
    }

    /**
     * @return the calcNeutralMass
     */
    public BigDecimal getCalcNeutralMass() {
        return calcNeutralMass;
    }

    /**
     * @param calcNeutralMass the calcNeutralMass to set
     */
    public void setCalcNeutralMass(BigDecimal calcNeutralMass) {
        this.calcNeutralMass = calcNeutralMass;
    }

    public void addProteinHit(ProteinHit protein) {
        this.proteinHits.add(protein);
    }
    
    public List<ProteinHit> getProteinHits() {
        return proteinHits;
    }
    
    public ProteinHit getFirstProteinHit() {
        return proteinHits.get(0);
    }
    
    public boolean hasUniqueProteinHit() {
        return proteinHits.size() == 1;
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
     * @return the deltaCn
     */
    public BigDecimal getDeltaCn() {
        return deltaCn;
    }

    /**
     * @param deltaCn the deltaCn to set
     */
    public void setDeltaCn(BigDecimal deltaCn) {
        this.deltaCn = deltaCn;
    }

    /**
     * @return the spScore
     */
    public BigDecimal getSpScore() {
        return spScore;
    }

    /**
     * @param spScore the spScore to set
     */
    public void setSpScore(BigDecimal spScore) {
        this.spScore = spScore;
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
     * @return the numMatchingProteins
     */
    public int getNumMatchingProteins() {
        return numMatchingProteins;
    }

    /**
     * @param numMatchingProteins the numMatchingProteins to set
     */
    public void setNumMatchingProteins(int numMatchingProteins) {
        this.numMatchingProteins = numMatchingProteins;
    }
}
