package org.yeastrc.ms.parser.sequestParams;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.parser.DataProviderException;

public class SequestParamsParserTest extends TestCase {

    private SequestParamsParser parser;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new SequestParamsParser();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testMatchParamValuePair() {
        // matching pattern ([\\S]+)\\s*=\\s*(.+&&[^;])\\s*;{0,1}.*
        String line = "database_name = /net/maccoss/vol2/mouse-contam.fasta";
        Param param = parser.matchParamValuePair(line);
        assertNotNull(param);
        assertEquals("database_name", param.getParamName());
        assertEquals("/net/maccoss/vol2/mouse-contam.fasta", param.getParamValue());

        line = "database_name=/net/maccoss/vol2/mouse-contam.fasta";
        param = parser.matchParamValuePair(line);
        assertNotNull(param);
        assertEquals("database_name", param.getParamName());
        assertEquals("/net/maccoss/vol2/mouse-contam.fasta", param.getParamValue());

        line = "something = something_else = something_else_entirely ; some_description";
        param = parser.matchParamValuePair(line);
        assertNotNull(param);
        assertEquals("something", param.getParamName());
        assertEquals("something_else = something_else_entirely", param.getParamValue());

        line = "something=something_else=something_else_entirely;some_description";
        param = parser.matchParamValuePair(line);
        assertNotNull(param);
        assertEquals("something", param.getParamName());
        assertEquals("something_else=something_else_entirely", param.getParamValue());

        line = "something = ";
        param = parser.matchParamValuePair(line);
        assertNotNull(param);
        assertEquals("something", param.getParamName());
        assertEquals("", param.getParamValue());
    }

    public void testBigDecimalZero() {
        String s = "0.0000";
        BigDecimal bd = new BigDecimal(s);
        assertFalse(bd.doubleValue() > 0);
    }

    public void testMatchEnzyme() {
        String line = "0.  No_Enzyme              0      -           -";
        Matcher m = SequestParamsParser.enzymePattern.matcher(line);
        assertTrue(m.matches());
        MsEnzymeIn enzyme = parser.matchEnzyme(m, "1");
        assertNull(enzyme);
        enzyme = parser.matchEnzyme(m, "0");
        assertNotNull(enzyme);
        assertEquals("No_Enzyme", enzyme.getName());
        assertEquals(Sense.NTERM, enzyme.getSense());
        assertEquals("-", enzyme.getCut());
        assertEquals("-", enzyme.getNocut());
        assertNull(enzyme.getDescription());

        line = "11.  Cymotryp/Modified  1\tFWYL\tPKR";
        m = SequestParamsParser.enzymePattern.matcher(line);
        assertTrue(m.matches());
        enzyme = parser.matchEnzyme(m, "1");
        assertNull(enzyme);
        enzyme = parser.matchEnzyme(m, "11");
        assertNotNull(enzyme);
        assertEquals("Cymotryp/Modified", enzyme.getName());
        assertEquals(Sense.CTERM, enzyme.getSense());
        assertEquals("FWYL", enzyme.getCut());
        assertEquals("PKR", enzyme.getNocut());
        assertNull(enzyme.getDescription());
    }

    public void testStaticResidueModPattern() {
        String param = "add_A_Alanine";
        Matcher m = SequestParamsParser.staticResidueModPattern.matcher(param);
        assertTrue(m.matches());
        assertEquals("A", m.group(1));
    }

    public void testStaticTerminalModPattern() {
        String param = "add_N_terminus";
        Matcher m = SequestParamsParser.staticTermModPattern.matcher(param);
        assertTrue(m.matches());
        assertEquals("N", m.group(1));
    }

    public void testParseDynamicResidueMods() {
        String modString = "";
        try {parser.parseDynamicResidueMods(modString);
        fail("Invalid modString");
        }
        catch(DataProviderException e) {
            assertEquals("Error parsing dynamic residue modification string", e.getErrorMessage());
        }

        modString = "16.0 @# 56.7 S";
        try {parser.parseDynamicResidueMods(modString);
        fail("Invalid modString");
        }
        catch(DataProviderException e) {
            assertEquals("Invalid char(s) for modified residue: @#", e.getErrorMessage());
        }

        modString = "1.0 A 2.0 B 80.0 STY 3.0 C";
        try {parser.parseDynamicResidueMods(modString);
            fail("Invalid modString");
        }
        catch(DataProviderException e) {
            assertEquals("Only three modifications are supported", e.getErrorMessage());
        }

        modString = "79.7663 STY 15.999 M 58.055 C";
        try {
            parser.parseDynamicResidueMods(modString);
        }
        catch (DataProviderException e) {
            fail("Valid dynamic mod string");
            e.printStackTrace();
        }
        List<MsResidueModificationIn> dynaResidueMods = parser.getDynamicResidueMods();
        assertEquals(5, dynaResidueMods.size());

        Collections.sort(dynaResidueMods, new Comparator<MsResidueModificationIn>() {
            public int compare(MsResidueModificationIn o1,
                    MsResidueModificationIn o2) {
                return Character.valueOf(o1.getModifiedResidue()).compareTo(Character.valueOf(o2.getModifiedResidue()));
            }});
        
        assertEquals('C', dynaResidueMods.get(0).getModifiedResidue());
        assertEquals('@', dynaResidueMods.get(0).getModificationSymbol());
        assertEquals(58.055, dynaResidueMods.get(0).getModificationMass().doubleValue());
        assertEquals('M', dynaResidueMods.get(1).getModifiedResidue());
        assertEquals('#', dynaResidueMods.get(1).getModificationSymbol());
        assertEquals(15.999, dynaResidueMods.get(1).getModificationMass().doubleValue());
        
        for (int i = 2; i < dynaResidueMods.size(); i++) {
            assertEquals(79.7663, dynaResidueMods.get(i).getModificationMass().doubleValue());
            assertEquals('*', dynaResidueMods.get(i).getModificationSymbol());
        }
        
        modString = "+80.0000 STY -99.9 GVD 0.0 X";
        
        try {
            parser.parseDynamicResidueMods(modString);
        }
        catch (DataProviderException e) {
            fail("Valid dynamic mod string");
            e.printStackTrace();
        }
        dynaResidueMods = parser.getDynamicResidueMods();
        assertEquals(6, dynaResidueMods.size());

        Collections.sort(dynaResidueMods, new Comparator<MsResidueModificationIn>() {
            public int compare(MsResidueModificationIn o1,
                    MsResidueModificationIn o2) {
                return Character.valueOf(o1.getModifiedResidue()).compareTo(Character.valueOf(o2.getModifiedResidue()));
            }});
        
        assertEquals('D', dynaResidueMods.get(0).getModifiedResidue());
        assertEquals('#', dynaResidueMods.get(0).getModificationSymbol());
        assertEquals(-99.9, dynaResidueMods.get(0).getModificationMass().doubleValue());
        assertEquals('G', dynaResidueMods.get(1).getModifiedResidue());
        assertEquals('#', dynaResidueMods.get(1).getModificationSymbol());
        assertEquals(-99.9, dynaResidueMods.get(1).getModificationMass().doubleValue());
        assertEquals('S', dynaResidueMods.get(2).getModifiedResidue());
        assertEquals('*', dynaResidueMods.get(2).getModificationSymbol());
        assertEquals(80.0, dynaResidueMods.get(2).getModificationMass().doubleValue());
        assertEquals('T', dynaResidueMods.get(3).getModifiedResidue());
        assertEquals('*', dynaResidueMods.get(3).getModificationSymbol());
        assertEquals(80.0, dynaResidueMods.get(3).getModificationMass().doubleValue());
        assertEquals('V', dynaResidueMods.get(4).getModifiedResidue());
        assertEquals('#', dynaResidueMods.get(4).getModificationSymbol());
        assertEquals(-99.9, dynaResidueMods.get(4).getModificationMass().doubleValue());
        assertEquals('Y', dynaResidueMods.get(5).getModifiedResidue());
        assertEquals('*', dynaResidueMods.get(5).getModificationSymbol());
        assertEquals(80.0, dynaResidueMods.get(5).getModificationMass().doubleValue());
        
        modString = "0.0 X 0.0 X 42.04695 RK";
        try {
            parser.parseDynamicResidueMods(modString);
        }
        catch (DataProviderException e) {
            fail("Valid dynamic mod string");
            e.printStackTrace();
        }
        dynaResidueMods = parser.getDynamicResidueMods();
        assertEquals(2, dynaResidueMods.size());
        
        Collections.sort(dynaResidueMods, new Comparator<MsResidueModificationIn>() {
            public int compare(MsResidueModificationIn o1,
                    MsResidueModificationIn o2) {
                return Character.valueOf(o1.getModifiedResidue()).compareTo(Character.valueOf(o2.getModifiedResidue()));
            }});
        assertEquals('K', dynaResidueMods.get(0).getModifiedResidue());
        assertEquals('@', dynaResidueMods.get(0).getModificationSymbol());
        assertEquals(42.04695, dynaResidueMods.get(0).getModificationMass().doubleValue());
        assertEquals('R', dynaResidueMods.get(1).getModifiedResidue());
        assertEquals('@', dynaResidueMods.get(1).getModificationSymbol());
        assertEquals(42.04695, dynaResidueMods.get(1).getModificationMass().doubleValue());
    }

    public void testParseParamsFile() {
        String paramsDir = "resources";
        try {
            parser.parseParams("remote.server", paramsDir);
            // database
            MsSearchDatabaseIn db = parser.getSearchDatabase();
            assertNotNull(db);
            assertEquals("remote.server", db.getServerAddress());
            assertEquals("/net/maccoss/vol2/software/pipeline/dbase/mouse-contam.fasta", db.getServerPath());

            // enzyme
            MsEnzymeIn enzyme = parser.getSearchEnzyme();
            assertNotNull(enzyme);
            assertEquals("No_Enzyme", enzyme.getName());
            assertNull(enzyme.getDescription());
            assertEquals(Sense.NTERM, enzyme.getSense());
            assertEquals("-", enzyme.getCut());
            assertEquals("-", enzyme.getNocut());

            // modifications
            assertEquals(0, parser.getStaticTerminalMods().size());
            assertEquals(0, parser.getDynamicResidueMods().size());

            List<MsResidueModificationIn> resMods = parser.getStaticResidueMods();
            assertEquals(1, resMods.size());
            MsResidueModificationIn mod = resMods.get(0);
            assertEquals('C', mod.getModifiedResidue());
            assertTrue(57.0 == mod.getModificationMass().doubleValue());
            assertEquals('\u0000', mod.getModificationSymbol());

        }
        catch (DataProviderException e) {
            fail("sequest.param is valid");
        }
    }
}
