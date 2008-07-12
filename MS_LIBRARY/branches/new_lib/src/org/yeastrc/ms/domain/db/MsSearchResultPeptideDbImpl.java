package org.yeastrc.ms.domain.db;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.MsSearchResultDynamicModDb;
import org.yeastrc.ms.domain.MsSearchResultPeptideDb;

public class MsSearchResultPeptideDbImpl  implements MsSearchResultPeptideDb {

    
    private char[] sequence;
    private char preResidue;
    private char postResidue;
    
    private List<? super MsSearchResultDynamicModDb> dynamicMods;
    
    
    public MsSearchResultPeptideDbImpl() {
        dynamicMods = new ArrayList<MsSearchResultDynamicModDb>();
    }
    

    @Override
    public String getPeptideSequence() {
        return String.valueOf(sequence);
    }

    @Override
    public char getPostResidue() {
        return postResidue;
    }

    @Override
    public char getPreResidue() {
        return preResidue;
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

    /**
     * @param postResidue the postResidue to set
     */
    public void setPostResidue(char postResidue) {
        this.postResidue = postResidue;
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
