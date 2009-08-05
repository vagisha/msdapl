package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResult;

import edu.uwpr.protinfer.database.dto.GenericProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class WIdPickerIonForProtein extends WIdPickerIon {

    private List<Character> ntermResidues = new ArrayList<Character>();
    private List<Character> cTermResidues = new ArrayList<Character>();
    
    public WIdPickerIonForProtein(GenericProteinferIon<? extends ProteinferSpectrumMatch> ion, MsSearchResult psm) {
        super(ion, psm);
    }
    
    public void addTerminalResidues(char nterm, char cterm) {
        this.ntermResidues.add(nterm);
        this.cTermResidues.add(cterm);
    }
    
    public String getIonSequence() {
        
        if(getBestSpectrumMatch() == null)
            return null;
        String seq = removeTerminalResidues(getBestSpectrumMatch().getResultPeptide().getModifiedPeptide());
        seq = "."+seq+".";
        for(int i = 0; i < ntermResidues.size(); i++) {
            seq = "("+ntermResidues.get(i)+")"+seq+"("+cTermResidues.get(i)+")";
        }
        return seq;
    }
}
