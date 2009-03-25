package org.yeastrc.ms.domain.search.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.search.MsModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.util.AminoAcidUtils;

public class SearchResultPeptideBean  implements MsSearchResultPeptide {

    
    private char[] sequence;
    private String modifiedSequence;
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
    //--------------------------------------------------------------------------------------O---
    public List<MsResultResidueMod> getResultDynamicResidueModifications() {
        return (List<MsResultResidueMod>) dynaResidueMods;
    }
    
    public void setDynamicResidueModifications(List<MsResultResidueMod> dynaMods) {
        this.dynaResidueMods = dynaMods;
        this.modifiedSequence = null;
    }
    
    //-----------------------------------------------------------------------------------------
    // TERMINAL DYNAMIC MODIFICATIONS
    //-----------------------------------------------------------------------------------------
    public List<MsResultTerminalMod> getResultDynamicTerminalModifications() {
        return (List<MsResultTerminalMod>) dynaTerminalMods;
    }
    
    public void setDynamicTerminalModifications(List<MsResultTerminalMod> termDynaMods) {
        this.dynaTerminalMods = termDynaMods;
        this.modifiedSequence = null;
    }
    
    /**
     * Returns the modified peptide in a program specific format
     * @return
     */
    public String getModifiedPeptidePS() {
        
        
        if (dynaResidueMods.size() == 0) {
//            modifiedSequence = preResidue+"."+String.valueOf(sequence)+"."+postResidue;
            return String.valueOf(sequence);
        }
        else {
            String origseq = String.valueOf(sequence);
            int lastIdx = 0;
            StringBuilder seq = new StringBuilder();
            sortDynaResidueModifications();
            for (MsResultResidueMod mod: dynaResidueMods) {
                seq.append(origseq.subSequence(lastIdx, mod.getModifiedPosition()+1)); // get sequence up to an including the modified position.
                char modSymbol = mod.getModificationSymbol();
                if(modSymbol == '\u0000') {
                    seq.append("["+Math.round(mod.getModificationMass().doubleValue() +
                            AminoAcidUtils.avgMass(origseq.charAt(mod.getModifiedPosition())))+"]");
                }
                else {
                    seq.append(modSymbol);
                }
                
                lastIdx = mod.getModifiedPosition()+1;
            }
            if (lastIdx < origseq.length())
                seq.append(origseq.subSequence(lastIdx, origseq.length()));
            
            return seq.toString();
//            modifiedSequence = preResidue+"."+modifiedSequence+"."+postResidue;
        }
    }
    
    /**
     * Returns the modified peptide sequence: e.g. PEP[80]TIDE
     * @return
     */
    public String getModifiedPeptide() {
        
        if (modifiedSequence != null)
            return modifiedSequence;
        
        if (dynaResidueMods.size() == 0) {
//            modifiedSequence = preResidue+"."+String.valueOf(sequence)+"."+postResidue;
            modifiedSequence = String.valueOf(sequence);
        }
        else {
            String origseq = String.valueOf(sequence);
            int lastIdx = 0;
            StringBuilder seq = new StringBuilder();
            sortDynaResidueModifications();
            for (MsResultResidueMod mod: dynaResidueMods) {
                seq.append(origseq.subSequence(lastIdx, mod.getModifiedPosition()+1)); // get sequence up to an including the modified position.
                seq.append("["+Math.round(mod.getModificationMass().doubleValue() +
                        AminoAcidUtils.avgMass(origseq.charAt(mod.getModifiedPosition())))+"]");
                
                lastIdx = mod.getModifiedPosition()+1;
            }
            if (lastIdx < origseq.length())
                seq.append(origseq.subSequence(lastIdx, origseq.length()));
            
            modifiedSequence = seq.toString();
//            modifiedSequence = preResidue+"."+modifiedSequence+"."+postResidue;
        }
        
        return modifiedSequence;
    }
    
    private void sortDynaResidueModifications() {
        Collections.sort(dynaResidueMods, new Comparator<MsResultResidueMod>(){
            public int compare(MsResultResidueMod o1, MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
    }
}
