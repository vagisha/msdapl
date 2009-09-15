package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.ValidationStatus;

/**
 * Represents a 'M' line in the SQT file
 */
public abstract class SQTSearchResult implements MsSearchResultIn {

    private String sequence;
    private char validationStatus;
    private int charge;
    private BigDecimal observedMass;
    private int scanNumber;
    private List<MsSearchResultProteinIn> matchingLoci;
    private MsSearchResultPeptide resultPeptide = null;

    public SQTSearchResult() {
        matchingLoci = new ArrayList<MsSearchResultProteinIn>();
    }

    @Override
    public int getScanNumber() {
        return this.scanNumber;
    }

    public void setScanNumber(int scanNumber) {
        this.scanNumber = scanNumber;
    }
    
    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getCharge() {
        return charge;
    }
    
    public void setObservedMass(BigDecimal observedMass) {
        this.observedMass = observedMass;
    }
    
    public BigDecimal getObservedMass() {
        return this.observedMass;
    }

    public void addMatchingLocus(String accession, String description) {
        DbLocus locus = new DbLocus(accession, description);
        addMatchingProteinMatch(locus);
    }

    public void addMatchingProteinMatch(MsSearchResultProteinIn locus) {
        matchingLoci.add(locus);
    }

    public List<MsSearchResultProteinIn> getProteinMatchList() {
        return this.matchingLoci;
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
     * @param sequence the sequence to set
     */
    public void setOriginalPeptideSequence(String sequence) {
        this.sequence = sequence;
    }
    
    protected String getOriginalPeptideSequence() {
        return sequence;
    }

    public MsSearchResultPeptide getResultPeptide() {
        if (resultPeptide != null)
            return resultPeptide;
        try {
            resultPeptide = buildPeptideResult();
        }
        catch (SQTParseException e) {
           throw new RuntimeException("Error building result peptide",e);
        }
        return resultPeptide;
    }
   
   

    public abstract MsSearchResultPeptide buildPeptideResult() throws SQTParseException;
    
   
}