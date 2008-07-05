/**
 * MsPeptideSearchResult.java
 * @author Vagisha Sharma
 * July 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class MsPeptideSearchResult {

    private static final char EMPTY_CHAR = '\u0000';
    
    private int id; // unique id (database) for this search result
    private int searchId; // id (database) of the search this result belongs to
    private int scanId; // id (database) of the scan with which this result is associated
    
    private int charge; 
    private BigDecimal calculatedMass;
    private int numIonsMatched;
    private int numIonsPredicted;
    private char preResidue = EMPTY_CHAR; // residue to the left of the N-term cleavage side
    private char postResidue = EMPTY_CHAR; // residue to the right of the C-term cleavage side
    private char validationStatus = EMPTY_CHAR;
    
    private String peptide;
    
    private List<MsSearchResultDynamicMod> dynaMods;
    private List<MsSearchMod> staticMods;
    private PeptideResultSequence peptideResult;
    
    private List<MsProteinMatch> proteinMatchList;
    
    
    public MsPeptideSearchResult() {
        proteinMatchList = new ArrayList<MsProteinMatch>();
        dynaMods = new ArrayList<MsSearchResultDynamicMod>();
        staticMods = new ArrayList<MsSearchMod>();
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return the searchId
     */
    public int getSearchId() {
        return searchId;
    }

    /**
     * @param searchId the searchId to set
     */
    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    /**
     * @return the scanId
     */
    public int getScanId() {
        return scanId;
    }

    /**
     * @param scanId the scanId to set
     */
    public void setScanId(int scanId) {
        this.scanId = scanId;
    }

    /**
     * @return the charge
     */
    public int getCharge() {
        return charge;
    }

    /**
     * @param charge the charge to set
     */
    public void setCharge(int charge) {
        this.charge = charge;
    }

    /**
     * @return the calculatedMass
     */
    public BigDecimal getCalculatedMass() {
        return calculatedMass;
    }

    /**
     * @param calculatedMass the calculatedMass to set
     */
    public void setCalculatedMass(BigDecimal calculatedMass) {
        this.calculatedMass = calculatedMass;
    }

    /**
     * @return the numIonsMatched
     */
    public int getNumIonsMatched() {
        return numIonsMatched;
    }

    /**
     * @param numIonsMatched the numIonsMatched to set
     */
    public void setNumIonsMatched(int numIonsMatched) {
        this.numIonsMatched = numIonsMatched;
    }

    /**
     * @return the numPredictedIons
     */
    public int getNumIonsPredicted() {
        return numIonsPredicted;
    }

    /**
     * @param numPredictedIons the numPredictedIons to set
     */
    public void setNumIonsPredicted(int numPredictedIons) {
        this.numIonsPredicted = numPredictedIons;
    }

    /**
     * @return the preResidue
     */
    public char getPreResidue() {
        return preResidue;
    }

    public String getPreResidueString() {
        if (preResidue == EMPTY_CHAR)
            return null;
        return new Character(preResidue).toString();
    }
    
    /**
     * @param preResidue the preResidue to set
     */
    public void setPreResidue(char preResidue) {
        this.preResidue = preResidue;
    }

    public void setPreResidueString(String preResidue) {
        if (preResidue == null || preResidue.length() > 0)
            this.preResidue = preResidue.charAt(0);
    }
    
    /**
     * @return the postResidue
     */
    public char getPostResidue() {
        return postResidue;
    }
    
    public String getPostResidueString() {
        if (postResidue == EMPTY_CHAR)
            return null;
        return new Character(postResidue).toString();
    }

    /**
     * @param postResidue the postResidue to set
     */
    public void setPostResidue(char postResidue) {
        this.postResidue = postResidue;
    }

    public void setPostResidueString(String postResidue) {
        if (postResidue == null || postResidue.length() > 0)
            this.postResidue = postResidue.charAt(0);
    }
    
    /**
     * @return the validationStatus
     */
    public char getValidationStatus() {
        return validationStatus;
    }

    public String getValidationStatusString() {
        if (validationStatus == EMPTY_CHAR)
            return null;
        return new Character(validationStatus).toString();
    }
    
    /**
     * @param validationStatus the validationStatus to set
     */
    public void setValidationStatus(char validationStatus) {
        this.validationStatus = validationStatus;
    }

    public void setValidationStatusString(String status) {
        if (status == null || status.length() > 0)
            this.validationStatus = status.charAt(0);
    }
    
    /**
     * @return the proteinMatchList
     */
    public List<MsProteinMatch> getProteinMatchList() {
        return proteinMatchList;
    }

    /**
     * @param proteinMatchList the proteinMatchList to set
     */
    public void setProteinMatchList(List<MsProteinMatch> proteinMatchList) {
        this.proteinMatchList = proteinMatchList;
    }
    
    public void addProteinMatch(MsProteinMatch match) {
        proteinMatchList.add(match);
    }
    
    
    //-----------------------------------------------------------------------------------------
    // PEPTIDE SEQUENCE
    //-----------------------------------------------------------------------------------------
    
    /**
     * @return the peptideResult
     */
    public PeptideResultSequence getPeptideResult() {
        if (peptideResult != null)
            return peptideResult;
        
        peptideResult = new PeptideResultSequence(getPeptide().toCharArray());
        
        for (MsSearchResultDynamicMod mod: dynaMods)
            peptideResult.addDynamicModification(mod.getModificationPosition(), mod);
        
        for(MsSearchMod mod: staticMods)
            peptideResult.addStaticModification(mod.getModifiedResidue(), mod.getModificationMass());
        
        return peptideResult;
    }
    
    public String getPeptide() {
        return peptide;
    }
    
    public void setPeptide(String peptide) {
        this.peptide = peptide;
    }
    
    //-----------------------------------------------------------------------------------------
    // DYNAMIC MODIFICATIONS
    //-----------------------------------------------------------------------------------------
    public void addDynamicModification(MsSearchResultDynamicMod modification) {
        this.dynaMods.add(modification);
    }
    
    public void setDynamicModifications(List<MsSearchResultDynamicMod> dynaMods) {
        this.dynaMods = dynaMods;
    }
    
    public List<MsSearchResultDynamicMod> getDynamicModifications() {
        return this.dynaMods;
    }
    
    //-----------------------------------------------------------------------------------------
    // STATIC MODIFICATIONS
    //-----------------------------------------------------------------------------------------
    public void addStaticModification(MsSearchMod staticMod) {
       staticMods.add(staticMod);
    }
    
    public void setStaticModifications(List<MsSearchMod> staticMods) {
        this.staticMods = staticMods;
    }
    
    public List<MsSearchMod> getStaticModifications() {
        return staticMods;
    }
}
