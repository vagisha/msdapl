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

    MsSearchResultPeptideBuilder builder = MsSearchResultPeptideBuilder.instance();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testRemoveAccession() {
        String seq = "34|emb|CAB44792.1|S.PELPATSLLQERW.A";
        assertEquals("S.PELPATSLLQERW.A", builder.removeAccession(seq));
        
        seq = "S.PELPATSLLQERW.A";
        assertEquals("S.PELPATSLLQERW.A", builder.removeAccession(seq));
        
        seq = "34|emb|CAB44792.1|";
        assertEquals("", builder.removeAccession(seq));
        
        seq = "L.Gi|62822520|gb|AAY15068.1|SGVSIVNAVTYEPTVAGRPNAV.H";
        assertEquals("SGVSIVNAVTYEPTVAGRPNAV.H", builder.removeAccession(seq));
    }
    
    public void testGetPreResidue() {
        String sequence = "ABCD";
        assertEquals(0, builder.getPreResidue(sequence));
        sequence = ".ABCD";
        assertEquals(0, builder.getPreResidue(sequence));
        sequence = "ABCD.EF";
        assertEquals(0, builder.getPreResidue(sequence));
        sequence = "A.BCDE.F";
        assertEquals('A', builder.getPreResidue(sequence));
    }
    
    public void testGetPostResidue() {
        String sequence = "ABCD";
        assertEquals(0, builder.getPostResidue(sequence));
        sequence = ".ABCD";
        assertEquals(0, builder.getPostResidue(sequence));
        sequence = "ABCD.EF";
        assertEquals(0, builder.getPostResidue(sequence));
        sequence = "A.BCDE.F";
        assertEquals('F', builder.getPostResidue(sequence));
    }

    public void testRemoveDots() {
        
        String sequence = "A.BCDEF.G";
        assertEquals("BCDEF", builder.removeDots(sequence));
        
        sequence = "ABCD";
        assertEquals("ABCD", builder.removeDots(sequence));
        
        sequence = ".ABCD";
        assertEquals("ABCD", builder.removeDots(sequence));
        
        sequence = "A.BCD";
        assertEquals("BCD", builder.removeDots(sequence));
        
        sequence = "ABCD.";
        assertEquals("ABCD", builder.removeDots(sequence));
        
        sequence = "ABC.D";
        assertEquals("ABC", builder.removeDots(sequence));
        
        try {builder.removeDots("A.BCD.EF"); fail("Dot in the wrong position");}
        catch(IllegalArgumentException e) {assertEquals("Sequence does not have a .(dot) in the right position: BCD.EF", e.getMessage());}
        
        try {builder.removeDots("AB.CD.F"); fail("Dot in the wrong position");}
        catch(IllegalArgumentException e) {assertEquals("Sequence does not have a .(dot) in the right position: AB.CD.F", e.getMessage());}
        
        try {builder.removeDots("AB.CD"); fail("Dot in the wrong position");}
        catch(IllegalArgumentException e) {assertEquals("Sequence does not have a .(dot) in the right position: AB.CD", e.getMessage());}
    }
    
    public void testGetOnlyPeptideSequence() {
        String seq = "A.B1CD#E*.F";
        assertEquals("BCDE", builder.getOnlyPeptideSequence(seq));
        seq = "A..B";
        try {
            assertEquals("", builder.getOnlyPeptideSequence(seq));
            fail("Invalid peptide sequence");
        }
        catch(IllegalArgumentException e){}
    }
    
    public void testGetResultMods() {
        List<Mod> dynaMods = new ArrayList<Mod>(4);
        dynaMods.add(new Mod('S', '*', "80.0"));
        dynaMods.add(new Mod('T', '*', "80.0"));
        dynaMods.add(new Mod('Y', '*', "80.0"));
        dynaMods.add(new Mod('M', '#', "16.0"));
        
        String seq = "A.S*M#.Z";
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
        
        
        seq = "A.SM.Z";
        resultMods = builder.getResultMods(seq, dynaMods);
        assertEquals(0, resultMods.size());
        
        seq = ".SM.";
        resultMods = builder.getResultMods(seq, dynaMods);
        assertEquals(0, resultMods.size());
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
    
    public void testBuild2() {
        List<Mod> dynaMods = new ArrayList<Mod>(0);
        
        String seq = "L.Gi|62822520|gb|AAY15068.1|SGVSIVNAVTYEPTVAGRPNAV.H";
        MsSearchResultPeptide resultPeptide = builder.build(seq, dynaMods);
        assertEquals('\u0000', resultPeptide.getPreResidue());
        assertEquals('H', resultPeptide.getPostResidue());
        assertEquals("SGVSIVNAVTYEPTVAGRPNAV", resultPeptide.getPeptideSequence());
        
        List<MsSearchResultModification> resultMods = (List<MsSearchResultModification>) resultPeptide.getDynamicModifications();
        assertEquals(0, resultMods.size());
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
