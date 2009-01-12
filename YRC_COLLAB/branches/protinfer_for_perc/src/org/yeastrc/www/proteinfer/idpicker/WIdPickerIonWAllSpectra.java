package org.yeastrc.www.proteinfer.idpicker;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResult;

import edu.uwpr.protinfer.database.dto.GenericProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class WIdPickerIonWAllSpectra {

    private GenericProteinferIon<? extends ProteinferSpectrumMatch> ion;
    private List<WIdPickerSpectrumMatch> psmList;
    private boolean uniqueToProteinGrp = false;
    
    public WIdPickerIonWAllSpectra(GenericProteinferIon<? extends ProteinferSpectrumMatch> ion, 
            List<WIdPickerSpectrumMatch> psmList) {
        this.ion = ion;
        this.psmList = psmList;
    }
    
    public List<WIdPickerSpectrumMatch> getPsmList() {
        return psmList;
    }
    
    public String getIonSequence() {
        if(psmList.size() > 0) {
            MsSearchResult res = psmList.get(0).getSpectrumMatch();
            return removeTerminalResidues(res.getResultPeptide().getModifiedPeptideSequence());
        }
        else
            return null;
    }
    
    private static String removeTerminalResidues(String peptide) {
        int f = peptide.indexOf('.');
        int l = peptide.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? peptide.length() : l;
        return peptide.substring(f, l);
    }
    
    public int getCharge() {
        return ion.getCharge();
    }
    
    public boolean getIsUniqueToProteinGroup() {
        return uniqueToProteinGrp;
    }
    
    public void setIsUniqueToProteinGroup(boolean unique) {
        this.uniqueToProteinGrp = unique;
    }
    
    public int getSpectrumCount() {
        return psmList.size();
    }
}
