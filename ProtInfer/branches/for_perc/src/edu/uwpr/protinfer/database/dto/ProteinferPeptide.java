package edu.uwpr.protinfer.database.dto;


public class ProteinferPeptide extends GenericProteinferPeptide<ProteinferSpectrumMatch, ProteinferIon> {

    public ProteinferPeptide() {
        super();
    }

    @Override
    protected GenericProteinferPeptide<ProteinferSpectrumMatch, ProteinferIon> newPeptide() {
        return new ProteinferPeptide();
    }
}