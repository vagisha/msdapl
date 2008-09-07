package org.yeastrc.ms.domain.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;

public class SearchResultPeptideBean  implements MsSearchResultPeptide {

    
    private char[] sequence;
    private char preResidue = MsModification.EMPTY_CHAR;
    private char postResidue = MsModification.EMPTY_CHAR;
    
    private List<MsResultResidueMod> dynaResidueMods;
    private List<MsResultTerminalMod> dynaTerminalMods;
    
    
    public SearchResultPeptideBean() {
        dynaResidueMods = new ArrayList<MsResultResidueMod>();
        dynaTerminalMods = new ArrayList<MsResultTerminalMod>();
    }

    @Override
    public String getPeptideSequence() {
        return String.valueOf(sequence);
    }

    @Override
    public int getSequenceLength() {
        if (sequence != null)
            return sequence.length;
        return 0;
    }
    
    /**
     * @param preResidue the preResidue to set
     */
    public void setPreResidue(char preResidue) {
        this.preResidue = preResidue;
    }
    
    @Override
    public char getPreResidue() {
        return preResidue;
    }
    
    /**
     * @param postResidue the postResidue to set
     */
    public void setPostResidue(char postResidue) {
        this.postResidue = postResidue;
    }
    
    @Override
    public char getPostResidue() {
        return postResidue;
    }
    

    public void setPeptideSequence(String sequence) {
        this.sequence = sequence.toCharArray();
    }
    
    //-----------------------------------------------------------------------------------------
    // DYNAMIC MODIFICATIONS
    //-----------------------------------------------------------------------------------------
    public List<MsResultResidueMod> getResultDynamicResidueModifications() {
        return (List<MsResultResidueMod>) dynaResidueMods;
    }
    
    public void setDynamicResidueModifications(List<MsResultResidueMod> dynaMods) {
        this.dynaResidueMods = dynaMods;
    }
    
    //-----------------------------------------------------------------------------------------
    // TERMINAL DYNAMIC MODIFICATIONS
    //-----------------------------------------------------------------------------------------
    public List<MsResultTerminalMod> getResultDynamicTerminalModifications() {
        return (List<MsResultTerminalMod>) dynaTerminalMods;
    }
    
    public void setDynamicTerminalModifications(List<MsResultTerminalMod> termDynaMods) {
        this.dynaTerminalMods = termDynaMods;
    }
}
