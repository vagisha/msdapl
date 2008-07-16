/**
 * MsSearchResultPeptideBuilder.java
 * @author Vagisha Sharma
 * Jul 13, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.MsSearchModification;
import org.yeastrc.ms.domain.MsSearchResultModification;
import org.yeastrc.ms.domain.MsSearchResultPeptide;

/**
 * 
 */
public final class MsSearchResultPeptideBuilder {

    public static final MsSearchResultPeptideBuilder instance = new MsSearchResultPeptideBuilder();

    private MsSearchResultPeptideBuilder() {}

    public static MsSearchResultPeptideBuilder instance() {
        return instance;
    }

    public MsSearchResultPeptide build(String resultSequence, List<? extends MsSearchModification> dynaMods) {
        if (resultSequence == null || resultSequence.length() == 0)
            throw new IllegalArgumentException("sequence cannot be null or empty");
        
//        System.out.println("BUILDING");
//        resultSequence = removeAccession(resultSequence);
        
        if (resultSequence.length() < 5)
            throw new IllegalArgumentException("sequence appears to be invalid: "+resultSequence);
        resultSequence = resultSequence.toUpperCase();
        final char preResidue = getPreResidue(resultSequence);
        final char postResidue = getPostResidue(resultSequence);
        String dotless = removeDots(resultSequence);
        final List<MsSearchResultModification> resultMods = getResultMods(dotless, dynaMods);
        final String justPeptide = getOnlyPeptideSequence(dotless);
        
//        final char preResidue = 0;
//      final char postResidue = 0;
//      final List<MsSearchResultModification> resultMods = new ArrayList<MsSearchResultModification>(0);
//      final String justPeptide = resultSequence;
//        System.out.println(resultSequence+", "+preResidue+", "+postResidue+", "+justPeptide); 
        return new MsSearchResultPeptide() {

            public List<MsSearchResultModification> getDynamicModifications() {
                return resultMods;
            }
            public String getPeptideSequence() {
                return justPeptide;
            }
            public char getPostResidue() {
                return postResidue;
            }
            public char getPreResidue() {
                return preResidue;
            }
            public int getSequenceLength() {
                if (justPeptide == null)    return 0;
                return justPeptide.length();
            }};
    }

    char getPreResidue(String sequence) {
        if (sequence.charAt(1) == '.')
            return sequence.charAt(0);
        throw new IllegalArgumentException("Invalid peptide sequence; cannot get PRE residue: "+sequence);
    }
    
    char getPostResidue(String sequence) {
        if (sequence.charAt(sequence.length() - 2) == '.')
            return sequence.charAt(sequence.length() -1);
        throw new IllegalArgumentException("Invalid peptide sequence; cannot get POST residue: "+sequence);
    }
    
    List<MsSearchResultModification> getResultMods(String peptide, List<? extends MsSearchModification> dynaMods) {
        
        // create a map of the dynamic modifications for the search for easy access.
        Map<String, MsSearchModification> modMap = new HashMap<String, MsSearchModification>(dynaMods.size());
        for (MsSearchModification mod: dynaMods)
            modMap.put(mod.getModifiedResidue()+""+mod.getModificationSymbol(), mod);
        
        List<MsSearchResultModification> resultMods = new ArrayList<MsSearchResultModification>();
        char modChar = 0;
        int modCharIndex = -1;
        for (int i = 0; i < peptide.length(); i++) {
            char x = peptide.charAt(i);
            // if this is a valid residue skip over it
            if (isResidue(x))   {
                modChar = x;
                modCharIndex++;
                continue;
            }
            MsSearchModification matchingMod = modMap.get(modChar+""+x);
            if (matchingMod == null)
                throw new IllegalArgumentException("No modification found for residue: "+modChar+"; sequence: "+peptide);
            if (x != matchingMod.getModificationSymbol())
                throw new IllegalArgumentException("Modification symbol does not match. "+
                        "Search modification is: "+makeString(matchingMod)+
                        "; Result modification: "+modChar+", "+x);
            
            // found a match!!
            resultMods.add(new ResultMod(modChar, x, matchingMod.getModificationMass(), modCharIndex));
        }
        
        return resultMods;
    }

    private String makeString(MsSearchModification mod) {
        StringBuilder buf = new StringBuilder();
        buf.append(mod.getModifiedResidue());
        buf.append(", ");
        buf.append(mod.getModificationSymbol());
        buf.append(", ");
        buf.append(mod.getModificationMass());
        return buf.toString();
    }
    
    // Handle this case: 34|emb|CAB44792.1|S.PELPATSLLQERW.A
    // This method should return S.PELPATSLLQERW.A for the example above.
//    String removeAccession(String sequence) {
//        int idx = sequence.lastIndexOf('|');
//        if (idx == -1)  return sequence;
//        return sequence.substring(idx+1);
//    }
    
    String removeDots(String sequence) {
        if (sequence.charAt(1) != '.' || sequence.charAt(sequence.length() - 2) != '.')
            throw new IllegalArgumentException("Sequence does not have .(dots) in the expected position: "+sequence);
        return sequence.substring(2, sequence.length() - 2);
    }

    String getOnlyPeptideSequence(String sequence) {
        char[] residueChars = new char[sequence.length()];
        int j = 0;
        for (int i = 0; i < sequence.length(); i++) {
            char x = sequence.charAt(i);
            if (isResidue(x))
                residueChars[j++] = x;
        }
        sequence = String.valueOf(residueChars).trim();
        if (sequence.length() == 0)
            throw new IllegalArgumentException("No residues found: "+sequence);
        return sequence;
    }
    
    private boolean isResidue(char residue) {
        return residue >= 'A' && residue <= 'Z';
    }
    
    private static class ResultMod implements MsSearchResultModification {

        private char modResidue;
        private char modSymbol;
        private BigDecimal modMass;
        private int position;

        public ResultMod(char modResidue, char modSymbol, BigDecimal modMass, int position) {
            this.modResidue = modResidue;
            this.modSymbol = modSymbol;
            this.modMass = modMass;
            this.position = position;
        }
        public BigDecimal getModificationMass() {
            return modMass;
        }

        public char getModificationSymbol() {
            return modSymbol;
        }

        public ModificationType getModificationType() {
            return ModificationType.DYNAMIC;
        }

        public char getModifiedResidue() {
            return modResidue;
        }
        
        public int getModifiedPosition() {
            return this.position;
        }
    }
}
