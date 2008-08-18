/**
 * MsPeptideSearchResult.java
 * @author Vagisha Sharma
 * July 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsRunSearchResultDb;
import org.yeastrc.ms.domain.search.MsSearchResultPeptideDb;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;
import org.yeastrc.ms.domain.search.ValidationStatus;


public class MsSearchResultDbImpl implements MsRunSearchResultDb {

    private int id; // unique id (database) for this search result
    private int runSearchId; // id (database) of the run search this result belongs to
    private int scanId; // id (database) of the scan with which this result is associated

    private int charge; 
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
    public int getRunSearchId() {
        return runSearchId;
    }

    /**
     * @param searchId the searchId to set
     */
    public void setSearchId(int searchId) {
        this.runSearchId = searchId;
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

    // ----------------------------------------------------------------------------------------
    // Validation status for this result
    // ----------------------------------------------------------------------------------------
    /**
     * @param validationStatus the validationStatus to set
     */
    public void setValidationStatus(ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
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
