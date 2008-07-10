package org.yeastrc.ms.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MsSearchResultPeptide implements IMsSearchResultPeptide {

    
    private char[] sequence;
    private char preResidue;
    private char postResidue;
    
    private Map<Character, BigDecimal> staticModResidues;
    private Map<Integer, MsSearchResultDynamicMod> dynaModPositions;
    
    
    public MsSearchResultPeptide() {
        staticModResidues = new HashMap<Character, BigDecimal>();
        dynaModPositions = new HashMap<Integer, MsSearchResultDynamicMod>();
    }
    
    @Override
    public IMsSearchModification getDynamicModificationAtIndex(int index) {
        return dynaModPositions.get(index);
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
    public void addDynamicModification(MsSearchResultDynamicMod modification) {
        this.dynaModPositions.put(modification.getModifiedPosition(), modification);
    }
    
    public void setDynamicModifications(List<MsSearchResultDynamicMod> dynaMods) {
        for (MsSearchResultDynamicMod mod: dynaMods)
            addDynamicModification(mod);
    }
    
    //-----------------------------------------------------------------------------------------
    // STATIC MODIFICATIONS
    //-----------------------------------------------------------------------------------------
    public void addStaticModification(MsPeptideSearchStaticMod staticMod) {
       staticModResidues.put(staticMod.getModifiedResidue(), staticMod.getModificationMass());
    }
    
    public void setStaticModifications(List<MsPeptideSearchStaticMod> staticMods) {
        for (MsPeptideSearchStaticMod mod: staticMods)
            addStaticModification(mod);
    }
}
