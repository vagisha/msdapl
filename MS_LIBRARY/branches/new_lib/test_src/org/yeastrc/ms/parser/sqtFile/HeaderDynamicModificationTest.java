package org.yeastrc.ms.parser.sqtFile;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.MsSearchModification;

import junit.framework.TestCase;

public class HeaderDynamicModificationTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    

    public void testIsValidDynamicModificationSymbol() {
        Header header = new Header();
        char modSymbol = 0;
        assertTrue(header.isValidDynamicModificationSymbol(modSymbol));
        modSymbol = 'A';
        assertFalse(header.isValidDynamicModificationSymbol(modSymbol));
        modSymbol = '*';
        assertTrue(header.isValidDynamicModificationSymbol(modSymbol));
        modSymbol = 'x';
        assertFalse(header.isValidDynamicModificationSymbol(modSymbol));
    }
    
    public void testAddDynamicModsTestModCount() {
        Header header = new Header();
        String modString = "";
        try {
            header.addDynamicMods(modString);
        }
        catch(IllegalArgumentException e){
            fail("Empty modification string is be valid"+e.getMessage());
        }

        modString = "C=123.4 D=567.8";
        try {
            header.addDynamicMods(modString);
            fail("Multiple dynamic modifications modifications");
        }
        catch(IllegalArgumentException e){
            assertEquals("Invalid dynamic modification string (appears to have > 1 dynamic modification): "+modString, e.getMessage());
        }

        modString = "C# = 123.4";
        try {
            header.addDynamicMods(modString);
        }
        catch(IllegalArgumentException e){fail("Valid dynamic modification string: "+e.getMessage()); e.getMessage();}
    }

    public void testAddDynamicModsTestValidModCharsAndSymbol() {
        Header header = new Header();
        String modString = "=123.4";
        try {
            header.addDynamicMods(modString);
            fail("Missing modification symbol");
        }
        catch(IllegalArgumentException e){
            assertEquals("No modification symbol found: "+modString, e.getMessage());
        }
        modString = "%=123.4";
        try {
            header.addDynamicMods(modString);
            fail("Missing dynamic modification residues");
        }
        catch(IllegalArgumentException e){
            assertEquals("No modification symbol found: "+modString, e.getMessage());
        }
        modString = "Cc=123.4";
        try {
            header.addDynamicMods(modString);
            fail("Invalid modification symbol");
        }
        catch(IllegalArgumentException e){
            assertEquals("Invalid modification symbol: "+modString, e.getMessage());
        }
    }

    public void testAddDynamicModTestValidModMass() {
        Header header = new Header();
        String modString = "C@= ";
        try {
            header.addDynamicMods(modString);
            fail("Missing dynamic modification mass");
        }
        catch(IllegalArgumentException e){
            assertEquals("Invalid dynamic modification string: "+modString.trim(), e.getMessage());
        }

        modString = "C&=abc";
        try {
            header.addDynamicMods(modString);
            fail("Invalid dynamic modification mass");
        }
        catch(NumberFormatException e) {}

    }

    public void testSplit() {
        String s = "=t";
        assertEquals(2, s.split("=").length);
        s = "t=";
        assertEquals(1, s.split("=").length);
    }

    public void testAddDynamicMods() {

        Header header = new Header();
        char[] modChars = new char[]{'A', 'B', 'C'};
        StringBuilder buf = new StringBuilder();
        for (char c: modChars)
            buf.append(c);
        buf.append("*=123.4");
        String modString = buf.toString();

        header.addDynamicMods(modString);
        List<MsSearchModification> mods = header.getDynamicModifications();
        assertEquals(3, mods.size());

        int i = 0; 
        for (MsSearchModification mod: mods) {
            assertEquals(mod.getModifiedResidue(), modChars[i++]);
            assertEquals(mod.getModificationMass(), new BigDecimal("123.4"));
            assertEquals(mod.getModificationSymbol(), '*');
        }

    }
}
