/**
 * MsPeptideSearchResult.java
 * @author Vagisha Sharma
 * July 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.MsSearchResultDb;
import org.yeastrc.ms.domain.MsSearchResultPeptideDb;
import org.yeastrc.ms.domain.MsSearchResultProteinDb;


public class MsSearchResultDbImpl implements MsSearchResultDb {

    private int id; // unique id (database) for this search result
    private int searchId; // id (database) of the search this result belongs to
    private int scanId; // id (database) of the scan with which this result is associated

    private int charge; 
    private BigDecimal calculatedMass;
    private int numIonsMatched;
    private int numIonsPredicted;
    private ValidationStatus validationStatus = ValidationStatus.UNVALIDATED;

    private List<? super MsSearchResultProteinDb> proteinMatchList;

    private MsSearchResultPeptideDb resultPeptide;


    public MsSearchResultDbImpl() {
        proteinMatchList = new ArrayList<MsSearchResultProteinDb>();
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
     * @return the numIonsPredicted
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

    
    // ----------------------------------------------------------------------------------------
    // Validation status for this result
    // ----------------------------------------------------------------------------------------
    /**
     * @param validationStatus the validationStatus to set
     */
    public void setValidationStatus(ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    public void setValidationStatusString(String status) {
        if (status != null && status.length() > 0)
            this.validationStatus = ValidationStatus.getStatusForChar(Character.valueOf(status.charAt(0)));
    }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public String getValidationStatusString() {
        if (validationStatus == null)
            return null;
        return Character.toString(validationStatus.getStatusChar());
    }
   
    // ----------------------------------------------------------------------------------------
    // Peptide sequence information for this result
    // ----------------------------------------------------------------------------------------
    public MsSearchResultPeptideDb getResultPeptide() {
        return resultPeptide;
    }
    
    public void setResultPeptide(MsSearchResultPeptideDb peptide) {
        this.resultPeptide = peptide;
    }
    
    public char getPreResidue() {
        return resultPeptide.getPreResidue();
    }
    
    public char getPostResidue() {
        return resultPeptide.getPostResidue();
    }

    public String getPeptideSequence() {
        return resultPeptide.getPeptideSequence();
    }

    // ----------------------------------------------------------------------------------------
    // Protein matches to this result
    // ----------------------------------------------------------------------------------------
    /**
     * @param proteinMatchList the proteinMatchList to set
     */
    public void setProteinMatchList(List<MsSearchResultProteinDb> proteinMatchList) {
        this.proteinMatchList = proteinMatchList;
    }

    public void addProteinMatch(MsSearchResultProteinDb match) {
        proteinMatchList.add(match);
    }

    public List<MsSearchResultProteinDb> getProteinMatchList() {
        return (List<MsSearchResultProteinDb>) proteinMatchList;
    }
}
