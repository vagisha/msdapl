package org.yeastrc.ms.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PeptideResultSequence {

    private char[] sequence;
    private Map<Character, BigDecimal> staticModResidues;
    private Map<Integer, MsSearchResultDynamicMod> dynaModPositions;
    
    private static final char emptyChar = '\u0000';
    private static final BigDecimal noMassMod = new BigDecimal(0);
    
    
    
    public PeptideResultSequence(char[] sequence) {
        this.sequence = sequence;
        staticModResidues = new HashMap<Character, BigDecimal>();
        dynaModPositions = new HashMap<Integer, MsSearchResultDynamicMod>();
    }
    
    public void addStaticModification(char modChar, BigDecimal modMass) {
        staticModResidues.put(modChar, modMass);
    }
    
    public void addDynamicModification(int position, MsSearchResultDynamicMod dynaModPosition) {
        
        // make sure the position is valid for our sequence
        if (position < 0 || position >= sequence.length)
            throw new ArrayIndexOutOfBoundsException("Invalid modification index: "+position);
        
        // make sure the character in the modification is the same as the character in the sequence
        // at the given index
        if (sequence[position] != dynaModPosition.getModifiedResidue())
            throw new IllegalArgumentException("Character in dynamic modification ("
                    +dynaModPosition.getModifiedResidue()+
                    ") is not the same as character in sequence: "+sequence[position]+" at position: "+position);
        
        dynaModPositions.put(position, dynaModPosition);
    }
    
    public ModificationSymbolAndMass getModificationAtIndex(int index) {
        if (index < 0 || index >= sequence.length) 
            throw new ArrayIndexOutOfBoundsException("invalid index for peptide sequence: "+index);
        
        // check if there is a dynamic modification at this index
        MsSearchResultDynamicMod dynaMod = dynaModPositions.get(index);
        if (dynaMod != null)
            return new ModificationSymbolAndMass(dynaMod.getModificationSymbol(), dynaMod.getModificationMass());
        
        // if no dynamic modification was found check if the residue at this index has a static modification
        char residue = sequence[index];
        BigDecimal staticModMass = staticModResidues.get(residue);
        if (staticModMass != null)
            return new ModificationSymbolAndMass(emptyChar, staticModMass);
        
        // if no modifications were found at this index return a "black" modification
        return new ModificationSymbolAndMass(emptyChar, noMassMod);
    }
    
    public void setSequence(char[] sequence) {
        this.sequence = sequence;
    }
    
    public char[] getSequence() {
        return sequence;
    }
    
    public String getSequenceString() {
        return new String(sequence);
    }
    
    private static class ModificationSymbolAndMass {
        private char modificationCharacter;
        private BigDecimal modificationMass;
        
        public ModificationSymbolAndMass(char modChar, BigDecimal modMass) {
            modificationCharacter = modChar;
            modificationMass = modMass;
        }

        /**
         * @return the modificationCharacter
         */
        public char getModificationCharacter() {
            return modificationCharacter;
        }

        /**
         * @return the modificationMass
         */
        public BigDecimal getModificationMass() {
            return modificationMass;
        }
    }
}
