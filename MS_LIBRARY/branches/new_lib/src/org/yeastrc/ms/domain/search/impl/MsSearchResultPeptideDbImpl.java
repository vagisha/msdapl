package org.yeastrc.ms.domain.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsResultDynamicResidueModDb;
import org.yeastrc.ms.domain.search.MsResultDynamicTerminalModDb;
import org.yeastrc.ms.domain.search.MsSearchResultPeptideDb;

public class MsSearchResultPeptideDbImpl  implements MsSearchResultPeptideDb {

    
    private char[] sequence;
    private char preResidue = '\u0000';
    private char postResidue = '\u0000';
    
    private List<? super MsResultDynamicResidueModDb> dynamicMods;
    private List<? super MsResultDynamicTerminalModDb> terminalDynamicMods;
    
    
    public MsSearchResultPeptideDbImpl() {
        dynamicMods = new ArrayList<MsResultDynamicResidueModDb>();
        terminalDynamicMods = new ArrayList<MsResultDynamicTerminalModDb>();
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
    public List<MsResultDynamicResidueModDb> getResultDynamicResidueModifications() {
        return (List<MsResultDynamicResidueModDb>) dynamicMods;
    }
    
    public void addDynamicResidueModification(MsResultDynamicResidueModDb modification) {
        dynamicMods.add(modification);
    }
    
    public void setDynamicResidueModifications(List<? super MsResultDynamicResidueModDb> dynaMods) {
        this.dynamicMods = dynaMods;
    }
    
    //-----------------------------------------------------------------------------------------
    // TERMINAL DYNAMIC MODIFICATIONS
    //-----------------------------------------------------------------------------------------
    public List<MsResultDynamicTerminalModDb> getResultDynamicTerminalModifications() {
        return (List<MsResultDynamicTerminalModDb>) terminalDynamicMods;
    }
    
    public void addDynamicTerminalModification(MsResultDynamicTerminalModDb modification) {
        terminalDynamicMods.add(modification);
    }
    
    public void setDynamicTerminalModifications(List<? super MsResultDynamicTerminalModDb> termDynaMods) {
        this.terminalDynamicMods = termDynaMods;
    }
}
