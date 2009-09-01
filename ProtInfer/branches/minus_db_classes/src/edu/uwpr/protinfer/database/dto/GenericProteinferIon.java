/**
 * ProteinferIon.java
 * @author Vagisha Sharma
 * Dec 21, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.database.dto;

import java.util.List;

/**
 * 
 */
public class GenericProteinferIon <T extends ProteinferSpectrumMatch>{

    private int id;
    private int pinferPeptideId;
    private int charge;
    private int modificationStateId;
//    private String sequence;
    
    private T bestSpectrumMatch;
    private int spectrumCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProteinferPeptideId() {
        return pinferPeptideId;
    }
    
    public void setProteinferPeptideId(int pinferPeptideId) {
        this.pinferPeptideId = pinferPeptideId;
    }
    
//    public String getSequence() {
//        return sequence;
//    }
//
//    public void setSequence(String sequence) {
//        this.sequence = sequence;
//    }
    
    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getModificationStateId() {
        return modificationStateId;
    }

    public void setModificationStateId(int modificationStateId) {
        this.modificationStateId = modificationStateId;
    }

    public T getBestSpectrumMatch() {
        return bestSpectrumMatch;
    }

    /**
     * This method will keep the keep the first match in the list and ignore the rest.
     * @param bestSpectrumMatches
     */
    public void setBestSpectrumMatchList(List<T> bestSpectrumMatches) {
        if(bestSpectrumMatches != null && bestSpectrumMatches.size() > 0)
            this.bestSpectrumMatch = bestSpectrumMatches.get(0);
    }
    
    public int getSpectrumCount() {
        return spectrumCount;
    }

    public void setSpectrumCount(int spectrumCount) {
        this.spectrumCount = spectrumCount;
    }
}
