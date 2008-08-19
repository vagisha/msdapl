package org.yeastrc.ms.domain.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsResultDynamicResidueModDb;
import org.yeastrc.ms.domain.search.MsSearchResultPeptideDb;
import org.yeastrc.ms.domain.search.MsTerminalModificationDb;

public class MsSearchResultPeptideDbImpl  implements MsSearchResultPeptideDb {

    
    private char[] sequence;
    private char preResidue = '\u0000';
    private char postResidue = '\u0000';
    
    private List<? super MsResultDynamicResidueModDb> dynamicMods;
    private List<? super MsTerminalModificationDb> terminalDynamicMods;
    
    
    public MsSearchResultPeptideDbImpl() {
        dynamicMods = new ArrayList<MsResultDynamicResidueModDb>();
        terminalDynamicMods = new ArrayList<MsTerminalModificationDb>();
    }

    @Override
    public String getPeptideSequence() {
        return String.valueOf(sequence);
    }

    @Override
    public int getSequenceLength() {
        return sequence.length;
    }
    
    /**
     * @param preResidue the preResidue to set
     */
    public void setPreResidue(char preResidue) {
        this.preResidue = preResidue;
    }
    
    public void setPreResidueString(String preResidue) {
        if (preResidue == null || preResidue.length() == 0)
            return;
        this.preResidue = preResidue.charAt(0);
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

    
    public void setPostResidueString(String postResidue) {
        if (postResidue == null || postResidue.length() == 0)
            return;
        this.postResidue = postResidue.charAt(0);
    }
    

    public void setPeptideSequence(String sequence) {
        this.sequence = sequence.toCharArray();
    }
    
    //-----------------------------------------------------------------------------------------
    // DYNAMIC MODIFICATIONS
    //-----------------------------------------------------------------------------------------
    public List<MsResultDynamicResidueModDb> getDynamicModifications() {
        return (List<MsResultDynamicResidueModDb>) dynamicMods;
    }
    
    public void addDynamicModification(MsResultDynamicResidueModDb modification) {
        dynamicMods.add(modification);
    }
    
    public void setDynamicModifications(List<? super MsResultDynamicResidueModDb> dynaMods) {
        this.dynamicMods = dynaMods;
    }
    
    //-----------------------------------------------------------------------------------------
    // TERMINAL DYNAMIC MODIFICATIONS
    //-----------------------------------------------------------------------------------------
    public List<MsTerminalModificationDb> getTerminalDynamicModifications() {
        return (List<MsTerminalModificationDb>) terminalDynamicMods;
    }
    
    public void addDynamicModification(MsTerminalModificationDb modification) {
        terminalDynamicMods.add(modification);
    }
    
    public void setTerminalDynamicModifications(List<? super MsTerminalModificationDb> termDynaMods) {
        this.terminalDynamicMods = termDynaMods;
    }
}
