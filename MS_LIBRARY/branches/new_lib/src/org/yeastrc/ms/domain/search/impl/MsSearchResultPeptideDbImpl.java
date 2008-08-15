package org.yeastrc.ms.domain.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultDynamicModDb;
import org.yeastrc.ms.domain.search.MsSearchResultPeptideDb;

public class MsSearchResultPeptideDbImpl  implements MsSearchResultPeptideDb {

    
    private char[] sequence;
    private char preResidue = '\u0000';
    private char postResidue = '\u0000';
    
    private List<? super MsSearchResultDynamicModDb> dynamicMods;
    
    
    public MsSearchResultPeptideDbImpl() {
        dynamicMods = new ArrayList<MsSearchResultDynamicModDb>();
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
    public List<MsSearchResultDynamicModDb> getDynamicModifications() {
        return (List<MsSearchResultDynamicModDb>) dynamicMods;
    }
    
    public void addDynamicModification(MsSearchResultDynamicModDb modification) {
        dynamicMods.add(modification);
    }
    
    public void setDynamicModifications(List<? super MsSearchResultDynamicModDb> dynaMods) {
        this.dynamicMods = dynaMods;
    }
}
