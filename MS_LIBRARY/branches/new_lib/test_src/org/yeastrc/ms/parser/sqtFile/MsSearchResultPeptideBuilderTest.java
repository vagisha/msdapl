/**
 * MsSearchResultPeptideBuilderTest.java
 * @author Vagisha Sharma
 * Jul 13, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.search.MsSearchModification;
import org.yeastrc.ms.domain.search.MsSearchResultModification;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;

/**
 * 
 */
public class MsSearchResultPeptideBuilderTest extends TestCase {

    SQTSearchResultPeptideBuilder builder = SQTSearchResultPeptideBuilder.instance();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testGetPreResidue() {
        String sequence = "ABCD";
        try {builder.getPreResidue(sequence); fail("No Pre Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get PRE residue: ABCD", e.getMessage());}
        sequence = ".ABCD";
        try {builder.getPreResidue(sequence); fail("No Pre Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get PRE residue: "+sequence, e.getMessage());}
        sequence = "ABCD.EF";
        try{builder.getPreResidue(sequence); fail("No Post Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get PRE residue: "+sequence, e.getMessage());}
        sequence = "A.BCDE.F";
        char preRes = 0;
        try {
            preRes = builder.getPreResidue(sequence);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('A', preRes);
    }
    
    public void testGetPostResidue() {
        String sequence = "ABCD";
        try {builder.getPostResidue(sequence); fail("No Post Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get POST residue: "+sequence, e.getMessage());}
        sequence = ".ABCD";
        try {builder.getPostResidue(sequence); fail("No Post Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get POST residue: "+sequence, e.getMessage());}
        sequence = "ABCD.EF";
        try {builder.getPostResidue(sequence); fail("No Post Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get POST residue: "+sequence, e.getMessage());}
        sequence = "A.BCDE.F";
        char postRes = 0;
        try {
            postRes = builder.getPostResidue(sequence);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('F', postRes);
    }

    public void testRemoveDots() {
        
        String sequence = "A.BC#DE@F.G";
        String dotless = null;
        try {
            dotless = builder.removeDots(sequence);
        }
        catch (SQTParseException e1) {
            fail("Valid peptide sequence");
        }
        assertEquals("BC#DE@F", dotless);
        
        sequence = "ABCD";
        try{builder.removeDots(sequence);fail("No dots");}
        catch(SQTParseException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = ".ABCD";
        try{builder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = "A.BCD";
        try{builder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = "ABCD.";
        try{builder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = "ABC.D";
        try{builder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        try {builder.removeDots("A.BCD.EF"); fail("Dot in the wrong position");}
        catch(SQTParseException e) {assertEquals("Sequence does not have .(dots) in the expected position: A.BCD.EF", e.getMessage());}
        
        try {builder.removeDots("AB.CD.F"); fail("Dot in the wrong position");}
        catch(SQTParseException e) {assertEquals("Sequence does not have .(dots) in the expected position: AB.CD.F", e.getMessage());}
        
        try {builder.removeDots("AB.CD"); fail("Dot in the wrong position");}
        catch(SQTParseException e) {assertEquals("Sequence does not have .(dots) in the expected position: AB.CD", e.getMessage());}
    }
    
    public void testGetOnlyPeptideSequence() {
        String seq = "B123CD#E*";
        try {
            assertEquals("BCDE", builder.getOnlyPeptideSequence(seq));
        }
        catch (SQTParseException e) {
            fail("Valid sequence for this method");
        }
    }
    
    public void testGetResultMods() {
        List<Mod> dynaMods = new ArrayList<Mod>(4);
        dynaMods.add(new Mod('S', '*', "80.0"));
        dynaMods.add(new Mod('T', '*', "80.0"));
        dynaMods.add(new Mod('Y', '*', "80.0"));
        dynaMods.add(new Mod('M', '#', "16.0"));
        
        String seq = "A.S*M#.Z";
        try{builder.getResultMods(seq, dynaMods); fail("Sequence still has dots");}
        catch(SQTParseException e) {assertEquals("No matching modification found: A.; sequence: "+seq, e.getMessage());}
        
        try {
            seq = builder.removeDots(seq);
        }
        catch (SQTParseException e) {
            fail("Valid sequence");
        }
        List<MsSearchResultModification> resultMods = new ArrayList<MsSearchResultModification>(0);
        try {
            resultMods = builder.getResultMods(seq, dynaMods);
        }
        catch (SQTParseException e) {
            fail("Valid sequence");
        }
        assertEquals(2, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsSearchResultModification>() {
            public int compare(MsSearchResultModification o1,
                    MsSearchResultModification o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        MsSearchResultModification mod = resultMods.get(0);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('*', mod.getModificationSymbol());
        assertEquals(0, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('M', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(1, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(16.0), mod.getModificationMass());
    }
    
    public void testGetResultModsInvalid() {
        List<Mod> dynaMods = new ArrayList<Mod>(4);
        dynaMods.add(new Mod('S', '*', "80.0"));
        dynaMods.add(new Mod('T', '*', "80.0"));
        dynaMods.add(new Mod('Y', '*', "80.0"));
        dynaMods.add(new Mod('M', '#', "16.0"));
        
        String seq = "S*M#C*AB";
        try{builder.getResultMods(seq, dynaMods); fail("Invalid mod char");}
        catch(SQTParseException e) {assertEquals("No matching modification found: C*; sequence: "+seq, e.getMessage());}
    
        seq = "S*M@C*AB";
        try{builder.getResultMods(seq, dynaMods); fail("Invalid mod char");}
        catch(SQTParseException e) {assertEquals("No matching modification found: M@; sequence: "+seq, e.getMessage());}
    }
    
    
    public void testBuild1() {
        List<Mod> dynaMods = new ArrayList<Mod>(4);
        dynaMods.add(new Mod('S', '*', "80.0"));
        dynaMods.add(new Mod('T', '*', "80.0"));
        dynaMods.add(new Mod('Y', '*', "80.0"));
        dynaMods.add(new Mod('M', '#', "16.0"));
        
        String seq = "I.QKLRNY*FEAFEM#PG.S";
        MsSearchResultPeptide resultPeptide = null;
        try {
            resultPeptide = builder.build(seq, dynaMods);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('I', resultPeptide.getPreResidue());
        assertEquals('S', resultPeptide.getPostResidue());
        assertEquals("QKLRNYFEAFEMPG", resultPeptide.getPeptideSequence());
        
        List<MsSearchResultModification> resultMods = (List<MsSearchResultModification>) resultPeptide.getDynamicModifications();
        assertEquals(2, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsSearchResultModification>() {
            public int compare(MsSearchResultModification o1,
                    MsSearchResultModification o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        MsSearchResultModification mod = resultMods.get(0);
        assertEquals('Y', mod.getModifiedResidue());
        assertEquals('*', mod.getModificationSymbol());
        assertEquals(5, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('M', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(11, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(16.0), mod.getModificationMass());
        
    }
    
    public void testBuild2() {
        List<Mod> dynaMods = new ArrayList<Mod>(0);
        
        String seq = "L.Gi|62822520|gb|AAY15068.1|SGVSIVNAVTYEPTVAGRPNAV.H";
        String dotLessUpperCase = null;
        try {
            dotLessUpperCase = builder.removeDots(seq).toUpperCase();
        }
        catch (SQTParseException e1) {
            fail("Valid sequence for removeDots method");
        }
        
        try{builder.build(seq, dynaMods); fail("Invalid sequence");}
        catch(SQTParseException e) {assertEquals("No matching modification found: I|; sequence: "+dotLessUpperCase, e.getMessage());}
    }
    
    public void testBuild3() {
        List<Mod> dynaMods = new ArrayList<Mod>(4);
        dynaMods.add(new Mod('S', '*', "80.0"));
        String seq = "A.*SCDS*.Z";
        try {builder.build(seq, dynaMods);fail("Invalid sequence");}
        catch(SQTParseException e){
            String errMsg = "No matching modification found: \u0000*; sequence: *SCDS*";
            assertEquals(errMsg.length(), e.getMessage().length());
            assertEquals(errMsg, e.getMessage());
        }
    }
    
    public void testUpperCase() {
        assertEquals("|", String.valueOf('|').toUpperCase());
        String x = "!@#$%^&*()_+-=,.?:;\"\'~`|{}[]";
        String y = x.toUpperCase();
        assertEquals(x, y);
    }
    
    
    public void testBuildResidueWithTwoPossibleMods() {
        List<Mod> dynaMods = new ArrayList<Mod>(4);
        dynaMods.add(new Mod('S', '*', "80.0"));
        dynaMods.add(new Mod('S', '#', "111.0"));
        dynaMods.add(new Mod('T', '*', "80.0"));
        
        String seq = "I.QKLRS*FEAFS#MPG.S";
        MsSearchResultPeptide resultPeptide = null;
        try {
            resultPeptide = builder.build(seq, dynaMods);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('I', resultPeptide.getPreResidue());
        assertEquals('S', resultPeptide.getPostResidue());
        assertEquals("QKLRSFEAFSMPG", resultPeptide.getPeptideSequence());
        
        List<MsSearchResultModification> resultMods = (List<MsSearchResultModification>) resultPeptide.getDynamicModifications();
        assertEquals(2, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsSearchResultModification>() {
            public int compare(MsSearchResultModification o1,
                    MsSearchResultModification o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        MsSearchResultModification mod = resultMods.get(0);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('*', mod.getModificationSymbol());
        assertEquals(4, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(9, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(111.0), mod.getModificationMass());
    }
    
    public void testBuildOneCharTwoMods() {
        List<Mod> dynaMods = new ArrayList<Mod>(4);
        dynaMods.add(new Mod('S', '*', "80.0"));
        dynaMods.add(new Mod('S', '#', "111.0"));
        dynaMods.add(new Mod('T', '*', "80.0"));
        
        String seq = "I.QKLRS*#FEAFS#MPG.S";
        MsSearchResultPeptide resultPeptide = null;
        try {
            resultPeptide = builder.build(seq, dynaMods);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('I', resultPeptide.getPreResidue());
        assertEquals('S', resultPeptide.getPostResidue());
        assertEquals("QKLRSFEAFSMPG", resultPeptide.getPeptideSequence());
        
        List<MsSearchResultModification> resultMods = (List<MsSearchResultModification>) resultPeptide.getDynamicModifications();
        assertEquals(3, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsSearchResultModification>() {
            public int compare(MsSearchResultModification o1,
                    MsSearchResultModification o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        MsSearchResultModification mod = resultMods.get(0);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('*', mod.getModificationSymbol());
        assertEquals(4, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(4, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(111.0), mod.getModificationMass());
        
        mod = resultMods.get(2);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(9, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(111.0), mod.getModificationMass());
    }
    
    private static class Mod implements MsSearchModification {

        private char modResidue;
        private char modSymbol;
        private BigDecimal modMass;

        public Mod(char modResidue, char modSymbol, String modMass) {
            this.modResidue = modResidue;
            this.modSymbol = modSymbol;
            this.modMass = new BigDecimal(modMass);
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
    }
    
}
