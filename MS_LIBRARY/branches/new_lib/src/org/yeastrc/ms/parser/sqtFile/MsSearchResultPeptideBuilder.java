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
        
        resultSequence = removeAccession(resultSequence);
        
        if (resultSequence.length() < 3)
            throw new IllegalArgumentException("sequence appears to be invalid: "+resultSequence);
        final char preResidue = getPreResidue(resultSequence);
        final char postResidue = getPostResidue(resultSequence);
        final List<MsSearchResultModification> resultMods = getResultMods(resultSequence, dynaMods);
        final String justPeptide = getOnlyPeptideSequence(resultSequence);
        
//        final char preResidue = 0;
//      final char postResidue = 0;
//      final List<MsSearchResultModification> resultMods = new ArrayList<MsSearchResultModification>(0);
//      final String justPeptide = resultSequence;
        
        return new MsSearchResultPeptide() {

            public List<? extends MsSearchResultModification> getDynamicModifications() {
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
        int dotIdx = sequence.indexOf('.');
        if (dotIdx == 1)
            return sequence.charAt(0);
        else return 0;
        // if dotIdx == -1 : no dot found e.g ABCD
        // if dotIdx == 0  : no pre-residue before dot e.g .ABCD
        // if dotIdx > 1   : this dot may be the one before the post-residue.  e.g ABCD.E
    }
    
    char getPostResidue(String sequence) {
        int dotIdx = sequence.lastIndexOf('.');
        if (dotIdx == sequence.length() - 2)
            return sequence.charAt(sequence.length() -1);
        else return 0;
        // if dotIdx == -1 : no dot found e.g ABCD
        // if dotIdx == seqLen-1  : no post-residue after dot e.g ABCD.
        // if dotIdx < seqLen-2   : this dot may be the one after the pre-residue.  e.g A.BCDE
    }
    
    List<MsSearchResultModification> getResultMods(String peptide, List<? extends MsSearchModification> dynaMods) {
        
        // remove any dots 
        peptide = removeDots(peptide);
        
        // create a map of the dynamic modifications for the search for easy access.
        Map<Character, MsSearchModification> modMap = new HashMap<Character, MsSearchModification>(dynaMods.size());
        for (MsSearchModification mod: dynaMods)
            modMap.put(mod.getModifiedResidue(), mod);
        
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
            MsSearchModification matchingMod = modMap.get(modChar);
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
    String removeAccession(String sequence) {
        int idx = sequence.lastIndexOf('|');
        if (idx == -1)  return sequence;
        return sequence.substring(idx+1);
    }
    
    String removeDots(String sequence) {
        int f = sequence.indexOf('.');
        if (f == 0 || f == 1)                                           sequence = sequence.substring(f+1);
        
        int e = sequence.indexOf('.');
        if (e == sequence.length() -1 || e == sequence.length() -2)     sequence = sequence.substring(0, e);
        
        if (e != -1 && e < sequence.length() - 2)
            throw new IllegalArgumentException("Sequence does not have a .(dot) in the right position: "+sequence);
       
        return sequence;
    }

    String getOnlyPeptideSequence(String sequence) {
        String dotLess = removeDots(sequence);
        dotLess = dotLess.toUpperCase();
        char[] residueChars = new char[dotLess.length()];
        int j = 0;
        for (int i = 0; i < dotLess.length(); i++) {
            char x = dotLess.charAt(i);
            if (isResidue(x))
                residueChars[j++] = x;
        }
        dotLess = String.valueOf(residueChars).trim();
        if (dotLess.length() == 0)
            throw new IllegalArgumentException("No residues found: "+sequence);
        return dotLess;
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
