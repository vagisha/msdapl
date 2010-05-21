package edu.uwpr.protinfer.database.dto;

public class ProteinferSpectrumMatch {

    private int id;
    private int proteinferPeptideId;
    private int msRunSearchResultId;
//    private int rank;
    

    public ProteinferSpectrumMatch() {}
    
    public ProteinferSpectrumMatch(int pinferPeptideId, int msRunSearchResultId) {
        this.proteinferPeptideId = pinferPeptideId;
        this.msRunSearchResultId = msRunSearchResultId;
    }
    
    public int getProteinferPeptideId() {
        return proteinferPeptideId;
    }
    
    public void setProteinferPeptideId(int pinferPeptideId) {
        this.proteinferPeptideId = pinferPeptideId;
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
    
//    public int getRank() {
//        return rank;
//    }
//
//    public void setRank(int rank) {
//        this.rank = rank;
//    }

}
