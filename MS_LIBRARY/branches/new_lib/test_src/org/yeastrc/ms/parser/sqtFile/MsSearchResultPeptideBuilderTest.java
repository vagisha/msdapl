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

import org.yeastrc.ms.domain.MsSearchModification;
import org.yeastrc.ms.domain.MsSearchResultModification;
import org.yeastrc.ms.domain.MsSearchResultPeptide;

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
    
//    public void testRemoveAccession() {
//        String seq = "34|emb|CAB44792.1|S.PELPATSLLQERW.A";
//        assertEquals("S.PELPATSLLQERW.A", builder.removeAccession(seq));
//        
//        seq = "S.PELPATSLLQERW.A";
//        assertEquals("S.PELPATSLLQERW.A", builder.removeAccession(seq));
//        
//        seq = "34|emb|CAB44792.1|";
//        assertEquals("", builder.removeAccession(seq));
//        
//        seq = "L.Gi|62822520|gb|AAY15068.1|SGVSIVNAVTYEPTVAGRPNAV.H";
//        assertEquals("SGVSIVNAVTYEPTVAGRPNAV.H", builder.removeAccession(seq));
//    }
    
    public void testGetPreResidue() {
        String sequence = "ABCD";
        try {builder.getPreResidue(sequence); fail("No Pre Residue");}
        catch(IllegalArgumentException e) {assertEquals("Invalid peptide sequence; cannot get PRE residue: ABCD", e.getMessage());}
        sequence = ".ABCD";
        try {builder.getPreResidue(sequence); fail("No Pre Residue");}
        catch(IllegalArgumentException e) {assertEquals("Invalid peptide sequence; cannot get PRE residue: "+sequence, e.getMessage());}
        sequence = "ABCD.EF";
        try{builder.getPreResidue(sequence); fail("No Post Residue");}
        catch(IllegalArgumentException e) {assertEquals("Invalid peptide sequence; cannot get PRE residue: "+sequence, e.getMessage());}
        sequence = "A.BCDE.F";
        assertEquals('A', builder.getPreResidue(sequence));
    }
    
    public void testGetPostResidue() {
        String sequence = "ABCD";
        try {builder.getPostResidue(sequence); fail("No Post Residue");}
        catch(IllegalArgumentException e) {assertEquals("Invalid peptide sequence; cannot get POST residue: "+sequence, e.getMessage());}
        sequence = ".ABCD";
        try {builder.getPostResidue(sequence); fail("No Post Residue");}
        catch(IllegalArgumentException e) {assertEquals("Invalid peptide sequence; cannot get POST residue: "+sequence, e.getMessage());}
        sequence = "ABCD.EF";
        try {builder.getPostResidue(sequence); fail("No Post Residue");}
        catch(IllegalArgumentException e) {assertEquals("Invalid peptide sequence; cannot get POST residue: "+sequence, e.getMessage());}
        sequence = "A.BCDE.F";
        assertEquals('F', builder.getPostResidue(sequence));
    }

    public void testRemoveDots() {
        
        String sequence = "A.BCDEF.G";
        assertEquals("BCDEF", builder.removeDots(sequence));
        
        sequence = "ABCD";
        try{builder.removeDots(sequence);fail("No dots");}
        catch(IllegalArgumentException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = ".ABCD";
        try{builder.removeDots(sequence);fail("Only 1 dot");}
        catch(IllegalArgumentException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = "A.BCD";
        try{builder.removeDots(sequence);fail("Only 1 dot");}
        catch(IllegalArgumentException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = "ABCD.";
        try{builder.removeDots(sequence);fail("Only 1 dot");}
        catch(IllegalArgumentException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = "ABC.D";
        try{builder.removeDots(sequence);fail("Only 1 dot");}
        catch(IllegalArgumentException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        try {builder.removeDots("A.BCD.EF"); fail("Dot in the wrong position");}
        catch(IllegalArgumentException e) {assertEquals("Sequence does not have .(dots) in the expected position: A.BCD.EF", e.getMessage());}
        
        try {builder.removeDots("AB.CD.F"); fail("Dot in the wrong position");}
        catch(IllegalArgumentException e) {assertEquals("Sequence does not have .(dots) in the expected position: AB.CD.F", e.getMessage());}
        
        try {builder.removeDots("AB.CD"); fail("Dot in the wrong position");}
        catch(IllegalArgumentException e) {assertEquals("Sequence does not have .(dots) in the expected position: AB.CD", e.getMessage());}
    }
    
    public void testGetOnlyPeptideSequence() {
        String seq = "B1CD#E*";
        assertEquals("BCDE", builder.getOnlyPeptideSequence(seq));
//        seq = "A..B";
//        try {
//            assertEquals("", builder.getOnlyPeptideSequence(seq));
//            fail("Invalid peptide sequence");
//        }
//        catch(IllegalArgumentException e){}
    }
    
    public void testGetResultMods() {
        List<Mod> dynaMods = new ArrayList<Mod>(4);
        dynaMods.add(new Mod('S', '*', "80.0"));
        dynaMods.add(new Mod('T', '*', "80.0"));
        dynaMods.add(new Mod('Y', '*', "80.0"));
        dynaMods.add(new Mod('M', '#', "16.0"));
        
        String seq = "A.S*M#.Z";
        try{builder.getResultMods(seq, dynaMods); fail("Sequence still has dots");}
        catch(IllegalArgumentException e) {assertEquals("No modification found for residue: A; sequence: "+seq, e.getMessage());}
        
        seq = builder.removeDots(seq);
        List<MsSearchResultModification> resultMods = builder.getResultMods(seq, dynaMods);
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
        catch(IllegalArgumentException e) {assertEquals("No modification found for residue: C; sequence: "+seq, e.getMessage());}
    
//        seq = "S*M@C*AB";
//        try{builder.getResultMods(seq, dynaMods); fail("Invalid mod char");}
//        catch(IllegalArgumentException e) {assertEquals("No modification found for residue: C; sequence: "+seq, e.getMessage());}
    }
    
    
    public void testBuild1() {
        List<Mod> dynaMods = new ArrayList<Mod>(4);
        dynaMods.add(new Mod('S', '*', "80.0"));
        dynaMods.add(new Mod('T', '*', "80.0"));
        dynaMods.add(new Mod('Y', '*', "80.0"));
        dynaMods.add(new Mod('M', '#', "16.0"));
        
        String seq = "I.QKLRNY*FEAFEM#PG.S";
        MsSearchResultPeptide resultPeptide = builder.build(seq, dynaMods);
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
        MsSearchResultPeptide resultPeptide = builder.build(seq, dynaMods);
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
        MsSearchResultPeptide resultPeptide = builder.build(seq, dynaMods);
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
    
    public void testBuild2() {
        List<Mod> dynaMods = new ArrayList<Mod>(0);
        
        String seq = "L.Gi|62822520|gb|AAY15068.1|SGVSIVNAVTYEPTVAGRPNAV.H";
        String dotLessUpperCase = builder.removeDots(seq).toUpperCase();
        
        
        
//        MsSearchResultPeptide resultPeptide = builder.build(seq, dynaMods);
        try{builder.build(seq, dynaMods); fail("Invalid sequence");}
        catch(IllegalArgumentException e) {assertEquals("No modification found for residue: I; sequence: "+dotLessUpperCase, e.getMessage());}
//        assertEquals('\u0000', resultPeptide.getPreResidue());
//        assertEquals('H', resultPeptide.getPostResidue());
//        assertEquals("SGVSIVNAVTYEPTVAGRPNAV", resultPeptide.getPeptideSequence());
//        
//        List<MsSearchResultModification> resultMods = (List<MsSearchResultModification>) resultPeptide.getDynamicModifications();
//        assertEquals(0, resultMods.size());
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
