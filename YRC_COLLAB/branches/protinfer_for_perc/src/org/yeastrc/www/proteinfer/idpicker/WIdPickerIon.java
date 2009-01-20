package org.yeastrc.www.proteinfer.idpicker;

import org.yeastrc.ms.domain.search.MsSearchResult;

import edu.uwpr.protinfer.database.dto.GenericProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class WIdPickerIon {

    private GenericProteinferIon<? extends ProteinferSpectrumMatch> ion;
    private MsSearchResult bestSpectrumMatch;
    private boolean uniqueToProteinGrp = false;
    
    
    public <T extends MsSearchResult, I extends GenericProteinferIon<? extends ProteinferSpectrumMatch>> WIdPickerIon(I ion, T psm) {
        this.ion = ion;
        this.bestSpectrumMatch = psm;
    }

    public int getScanId() {
        return bestSpectrumMatch.getScanId();
    }

    public GenericProteinferIon<? extends ProteinferSpectrumMatch> getIon() {
        return ion;
    }
    
    public MsSearchResult getBestSpectrumMatch() {
        return bestSpectrumMatch;
    }

    public boolean getIsUniqueToProteinGroup() {
        return uniqueToProteinGrp;
    }
    
    public void setIsUniqueToProteinGroup(boolean isUnique) {
        this.uniqueToProteinGrp = isUnique;
    }
    
    public String getIonSequence() {
        return removeTerminalResidues(bestSpectrumMatch.getResultPeptide().getModifiedPeptide());
    }
    
    public int getCharge() {
        return ion.getCharge();
    }
    
    public int getSpectrumCount() {
        return ion.getSpectrumCount();
    }
    
    private static String removeTerminalResidues(String peptide) {
        int f = peptide.indexOf('.');
        int l = peptide.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? peptide.length() : l;
        return peptide.substring(f, l);
    }
    
//    public boolean equals(Object that) {
//        if(that == this)
//            return true;
//        if(!(that instanceof WIdPickerIon))
//            return false;
//        return (this.ion.getCharge() == ((WIdPickerIon)that).getIon().getCharge() &&
//                this.ion.getSequence() == ((WIdPickerIon)that).getIon().getSequence());
//    }
//    
//    public int hashCode() {
//        String identifier = this.ion.getSequence()+"_charge"+this.ion.getCharge();
//        return identifier.hashCode();
//    }
}
