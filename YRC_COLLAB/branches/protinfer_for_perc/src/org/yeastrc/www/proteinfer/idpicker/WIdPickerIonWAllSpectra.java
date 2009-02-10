package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResult;

import edu.uwpr.protinfer.database.dto.GenericProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class WIdPickerIonWAllSpectra {

    private GenericProteinferIon<? extends ProteinferSpectrumMatch> ion;
    private List<WIdPickerSpectrumMatch> psmList;
    private boolean uniqueToProteinGrp = false;
    
    private List<Character> ntermResidues = new ArrayList<Character>();
    private List<Character> cTermResidues = new ArrayList<Character>();
    
    public WIdPickerIonWAllSpectra(GenericProteinferIon<? extends ProteinferSpectrumMatch> ion, 
            List<WIdPickerSpectrumMatch> psmList) {
        this.ion = ion;
        this.psmList = psmList;
    }
    
    public void addTerminalResidues(char nterm, char cterm) {
        this.ntermResidues.add(nterm);
        this.cTermResidues.add(cterm);
    }
    
    public List<WIdPickerSpectrumMatch> getPsmList() {
        return psmList;
    }
    
    public String getIonSequence() {
        if(psmList.size() > 0) {
            MsSearchResult res = psmList.get(0).getSpectrumMatch();
            String seq = removeTerminalResidues(res.getResultPeptide().getModifiedPeptide());
            seq = "."+seq+".";
            for(int i = 0; i < ntermResidues.size(); i++) {
                seq = "("+ntermResidues.get(i)+")"+seq+"("+cTermResidues.get(i)+")";
            }
            return seq;
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
