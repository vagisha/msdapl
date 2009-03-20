package org.yeastrc.www.proteinfer.idpicker;

import org.yeastrc.ms.domain.search.MsSearchResult;

import edu.uwpr.protinfer.database.dto.GenericProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class WIdPickerIon {

    private GenericProteinferIon<? extends ProteinferSpectrumMatch> ion;
    private MsSearchResult bestSpectrumMatch;
    private boolean uniqueToProteinGrp = false;
    
    public WIdPickerIon(GenericProteinferIon<? extends ProteinferSpectrumMatch> ion, MsSearchResult psm) {
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
    
    protected static String removeTerminalResidues(String peptide) {
        int f = peptide.indexOf('.');
        int l = peptide.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? peptide.length() : l;
        return peptide.substring(f, l);
    }

}