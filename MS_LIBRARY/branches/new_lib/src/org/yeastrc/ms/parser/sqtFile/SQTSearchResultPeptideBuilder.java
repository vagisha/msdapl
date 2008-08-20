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

import org.yeastrc.ms.domain.search.MsSearchModification;
import org.yeastrc.ms.domain.search.MsSearchResultModification;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;

/**
 * 
 */
public final class SQTSearchResultPeptideBuilder {

    public static final SQTSearchResultPeptideBuilder instance = new SQTSearchResultPeptideBuilder();

    private SQTSearchResultPeptideBuilder() {}

    public static SQTSearchResultPeptideBuilder instance() {
        return instance;
    }

    public MsSearchResultPeptide build(String resultSequence, List<? extends MsSearchModification> dynaMods) 
    throws SQTParseException {
        if (resultSequence == null || resultSequence.length() == 0)
            throw new SQTParseException("sequence cannot be null or empty");
        
//        System.out.println("BUILDING");
        
        if (resultSequence.length() < 5)
            throw new SQTParseException("sequence appears to be invalid: "+resultSequence);
        resultSequence = resultSequence.toUpperCase();
        final char preResidue = getPreResidue(resultSequence);
        final char postResidue = getPostResidue(resultSequence);
        String dotless = removeDots(resultSequence);
        final List<MsSearchResultModification> resultMods = getResultMods(dotless, dynaMods);
        final String justPeptide = getOnlyPeptideSequence(dotless);
        
        return new MsSearchResultPeptide() {

            public List<MsSearchResultModification> getDynamicResidueMods() {
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

    char getPreResidue(String sequence) throws SQTParseException {
        if (sequence.charAt(1) == '.')
            return sequence.charAt(0);
        throw new SQTParseException("Invalid peptide sequence; cannot get PRE residue: "+sequence);
    }
    
    char getPostResidue(String sequence) throws SQTParseException {
        if (sequence.charAt(sequence.length() - 2) == '.')
            return sequence.charAt(sequence.length() -1);
        throw new SQTParseException("Invalid peptide sequence; cannot get POST residue: "+sequence);
    }
    
    List<MsSearchResultModification> getResultMods(String peptide, List<? extends MsSearchModification> dynaMods) throws SQTParseException {
        
        // create a map of the dynamic modifications for the search for easy access.
        Map<String, MsSearchModification> modMap = new HashMap<String, MsSearchModification>(dynaMods.size());
        for (MsSearchModification mod: dynaMods)
            modMap.put(mod.getModifiedResidue()+""+mod.getModificationSymbol(), mod);
        
        List<MsSearchResultModification> resultMods = new ArrayList<MsSearchResultModification>();
        char modifiedChar = 0;
        int modCharIndex = -1;
        for (int i = 0; i < peptide.length(); i++) {
            char x = peptide.charAt(i);
            // if this is a valid residue skip over it
            if (isResidue(x))   {
                modifiedChar = x;
                modCharIndex++;
                continue;
            }
            MsSearchModification matchingMod = modMap.get(modifiedChar+""+x);
            if (matchingMod == null)
                throw new SQTParseException("No matching modification found: "+modifiedChar+x+"; sequence: "+peptide);
            
            // found a match!!
            resultMods.add(new ResultMod(modifiedChar, x, matchingMod.getModificationMass(), modCharIndex));
        }
        
        return resultMods;
    }

    static String removeDots(String sequence) throws SQTParseException {
        if (sequence.charAt(1) != '.' || sequence.charAt(sequence.length() - 2) != '.')
            throw new SQTParseException("Sequence does not have .(dots) in the expected position: "+sequence);
        return sequence.substring(2, sequence.length() - 2);
    }

    static String getOnlyPeptideSequence(String sequence) throws SQTParseException {
        char[] residueChars = new char[sequence.length()];
        int j = 0;
        for (int i = 0; i < sequence.length(); i++) {
            char x = sequence.charAt(i);
            if (isResidue(x))
                residueChars[j++] = x;
        }
        sequence = String.valueOf(residueChars).trim();
        if (sequence.length() == 0)
            throw new SQTParseException("No residues found: "+sequence);
        return sequence;
    }
    
    static private boolean isResidue(char residue) {
        return residue >= 'A' && residue <= 'Z';
    }
    
    public static String getOnlyPeptide(String peptideAndExtras) throws SQTParseException {
        String dotless = removeDots(peptideAndExtras);
        return getOnlyPeptideSequence(dotless);
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
