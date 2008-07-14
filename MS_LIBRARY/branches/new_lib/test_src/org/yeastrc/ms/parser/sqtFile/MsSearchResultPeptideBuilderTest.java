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

    public void testRemoveDots() {
        
        String seq = "A.BCDEF.G";
        assertEquals("BCDEF", builder.removeDots(seq));
        
        try {builder.removeDots("ABCD"); fail("No dots in the sequence");}
        catch(IllegalArgumentException e) {assertEquals("Sequence does not have a .(dot): ABCD", e.getMessage());}
        
        try {builder.removeDots(".ABCD"); fail("No dots in the sequence");}
        catch(IllegalArgumentException e) {assertEquals("First and last index of .(dot) cannot be the same: .ABCD", e.getMessage());}
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
    
    public void testBuild() {
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
        
        List<MsSearchResultModification> resultMods = builder.getResultMods(seq, dynaMods);
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
