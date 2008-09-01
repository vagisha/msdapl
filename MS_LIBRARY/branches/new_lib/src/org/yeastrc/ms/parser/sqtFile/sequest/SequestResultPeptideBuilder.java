/**
 * MsSearchResultPeptideBuilder.java
 * @author Vagisha Sharma
 * Jul 13, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile.sequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.parser.ResultResidueModification;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;

/**
 * 
 */
public final class SequestResultPeptideBuilder {

    public static final SequestResultPeptideBuilder instance = new SequestResultPeptideBuilder();

    private SequestResultPeptideBuilder() {}

    public static SequestResultPeptideBuilder instance() {
        return instance;
    }

    public MsSearchResultPeptide build(String resultSequence, 
                List<? extends MsResidueModification> dynaResidueMods) 
    throws SQTParseException {
        if (resultSequence == null || resultSequence.length() == 0)
            throw new SQTParseException("sequence cannot be null or empty");
        
        if (dynaResidueMods == null)
            dynaResidueMods = new ArrayList<MsResidueModification>(0);
        
        if (resultSequence.length() < 5)
            throw new SQTParseException("sequence appears to be invalid: "+resultSequence);
        resultSequence = resultSequence.toUpperCase();
        final char preResidue = getPreResidue(resultSequence);
        final char postResidue = getPostResidue(resultSequence);
        String dotless = removeDots(resultSequence);
        final List<MsResultDynamicResidueMod> resultMods = getResultMods(dotless, dynaResidueMods);
        final String justPeptide = getOnlyPeptideSequence(dotless);
        
        return new MsSearchResultPeptide() {

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
            }
            public List<MsResultDynamicResidueMod> getResultDynamicResidueModifications() {
                return resultMods;
            }
            public List<MsTerminalModification> getDynamicTerminalModifications() {
                return new ArrayList<MsTerminalModification>(0);
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
    
    List<MsResultDynamicResidueMod> getResultMods(String peptide, List<? extends MsResidueModification> dynaMods) throws SQTParseException {
        
        // create a map of the dynamic modifications for the search for easy access.
        Map<String, MsResidueModification> modMap = new HashMap<String, MsResidueModification>(dynaMods.size());
        for (MsResidueModification mod: dynaMods)
            modMap.put(mod.getModifiedResidue()+""+mod.getModificationSymbol(), mod);
        
        List<MsResultDynamicResidueMod> resultMods = new ArrayList<MsResultDynamicResidueMod>();
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
            MsResidueModification matchingMod = modMap.get(modifiedChar+""+x);
            if (matchingMod == null)
                throw new SQTParseException("No matching modification found: "+modifiedChar+x+"; sequence: "+peptide);
            
            // found a match!!
            resultMods.add(new ResultResidueModification(modifiedChar, x, matchingMod.getModificationMass(), modCharIndex));
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
}
