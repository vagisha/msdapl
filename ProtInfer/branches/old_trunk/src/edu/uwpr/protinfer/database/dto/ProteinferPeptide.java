package edu.uwpr.protinfer.database.dto;


public class ProteinferPeptide extends BaseProteinferPeptide<ProteinferSpectrumMatch> {

    public ProteinferPeptide() {
        super();
    }

    @Override
    /**
     * Returns the first match. Subclasses should override this method.
     */
    public ProteinferSpectrumMatch getBestSpectrumMatch() {
        return this.getSpectrumMatchList().get(0);
    }
    
    
}
