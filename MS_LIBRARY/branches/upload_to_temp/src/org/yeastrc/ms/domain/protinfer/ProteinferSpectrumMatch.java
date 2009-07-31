package org.yeastrc.ms.domain.protinfer;

public class ProteinferSpectrumMatch {

    private int id;
    private int pinferIonId;
    private int msRunSearchResultId;
    private int rank; // rank of this spectrum match for the peptide (not the ion).
    

    public ProteinferSpectrumMatch() {}
    
    public ProteinferSpectrumMatch(int pinferIonId, int msRunSearchResultId) {
        this.pinferIonId = pinferIonId;
        this.msRunSearchResultId = msRunSearchResultId;
    }
    
    public int getProteinferIonId() {
        return pinferIonId;
    }
    
    public void setProteinferIonId(int pinferIonId) {
        this.pinferIonId = pinferIonId;
    }
    
    public int getMsRunSearchResultId() {
        return msRunSearchResultId;
    }
    
    public void setMsRunSearchResultId(int msRunSearchResultId) {
        this.msRunSearchResultId = msRunSearchResultId;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}
