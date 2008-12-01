package edu.uwpr.protinfer.database.dto;


public class ProteinferPeptide extends BaseProteinferPeptide<ProteinferSpectrumMatch> {

    public ProteinferPeptide() {
        super();
    }
    
    public ProteinferPeptide(int proteinferId) {
        super(proteinferId);
    }
}
