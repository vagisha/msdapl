package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;
import java.util.List;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.MsSearchModification;

public class HeaderStaticModificationTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testIsValidStaticModCharString () {
        SQTHeader header = new SQTHeader();
        String modCharString = "";
        assertFalse(header.isValidModCharString(modCharString));
        modCharString = "A";
        assertTrue(header.isValidModCharString(modCharString));
        modCharString = "*A";
        assertFalse(header.isValidModCharString(modCharString));
        modCharString = "a";
        assertFalse(header.isValidModCharString(modCharString));
        modCharString = "A*";
        assertFalse(header.isValidModCharString(modCharString));
        modCharString = "A ";
        assertFalse(header.isValidModCharString(modCharString));
        modCharString = "PEPTIDE";
        assertTrue(header.isValidModCharString(modCharString));
    }

    public void testAddStaticModsTestModCount() {
        SQTHeader header = new SQTHeader();
        String modString = "";
        try {
            header.addStaticMods(modString);
        }
        catch(IllegalArgumentException e){
            fail("Empty modification string is be valid"+e.getMessage());
        }

        modString = "C=123.4 D=567.8";
        try {
            header.addStaticMods(modString);
            fail("Multiple static modifications modifications");
        }
        catch(IllegalArgumentException e){
            assertEquals("Invalid static modification string (appears to have > 1 static modification): "+modString, e.getMessage());
        }

        modString = "C = 123.4";
        try {
            header.addStaticMods(modString);
        }
        catch(IllegalArgumentException e){fail("Valid static modification string");}
    }

    public void testAddStaticModsTestValidModChars() {
        SQTHeader header = new SQTHeader();
        String modString = "=123.4";
        try {
            header.addStaticMods(modString);
            fail("Missing static modification residues");
        }
        catch(IllegalArgumentException e){
            assertEquals("No residues found for static modification: "+modString, e.getMessage());
        }

        modString = "C#=123.4";
        try {
            header.addStaticMods(modString);
            fail("Invalid static modification residue");
        }
        catch(IllegalArgumentException e){
            assertEquals("Invalid residues found found for static modification"+modString, e.getMessage());
        }
    }

    public void testAddStaticModTestValidModMass() {
        SQTHeader header = new SQTHeader();
        String modString = "C= ";
        try {
            header.addStaticMods(modString);
            fail("Missing static modification mass");
        }
        catch(IllegalArgumentException e){
            assertEquals("Invalid static modification string: "+modString.trim(), e.getMessage());
        }

        modString = "C=abc";
        try {
            header.addStaticMods(modString);
            fail("Invalid static modification mass");
        }
        catch(NumberFormatException e) {}

    }

    public void testSplit() {
        String s = "=t";
        assertEquals(2, s.split("=").length);
        s = "t=";
        assertEquals(1, s.split("=").length);
    }

    public void testAddStaticMods() {

        SQTHeader header = new SQTHeader();
        char[] modChars = new char[]{'A', 'B', 'C'};
        StringBuilder buf = new StringBuilder();
        for (char c: modChars)
            buf.append(c);
        buf.append("=123.4");
        String modString = buf.toString();

        header.addStaticMods(modString);
        List<MsSearchModification> mods = header.getStaticModifications();
        assertEquals(3, mods.size());

        int i = 0; 
        for (MsSearchModification mod: mods) {
            assertEquals(mod.getModifiedResidue(), modChars[i++]);
            assertEquals(mod.getModificationMass(), new BigDecimal("123.4"));
        }

    }
}
