package org.yeastrc.ms.parser.prolucidParams;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser.PrimaryScore;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser.SecondaryScore;

public class ProlucidParamsParserTest extends TestCase {

    private String format1file = "resources/prolucid_search_format1.xml";
    private String format2file = "resources/prolucid_search_format2.xml";

    private ProlucidParamsParser parser;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new ProlucidParamsParser();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetSearchDatabase() {
        // format 1
        try {
            parser.parseParamsFile("remote.server", format1file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        MsSearchDatabase db = parser.getSearchDatabase();
        assertNotNull(db);
        assertEquals("remote.server", db.getServerAddress());
        assertEquals("/bluefish/people-b/applications/yates/dbase/EBI-IPI_mouse_3.06_05-10-2005_origrev_con.fasta", db.getServerPath());
        assertEquals(0, db.getSequenceLength());
        assertEquals(0, db.getProteinCount());

        // format 2
        try {
            parser.parseParamsFile("remote.server", format2file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        db = parser.getSearchDatabase();
        assertNotNull(db);
        assertEquals("remote.server", db.getServerAddress());
        assertEquals("/garibaldi/people-b/applications/yates/dbase/WormBase_C-elegans_wp180_08-19-2007_reversed.fasta", db.getServerPath());
        assertEquals(0, db.getSequenceLength());
        assertEquals(0, db.getProteinCount());
    }

    public void testGetSearchEnzyme() {
        // format 1
        try {
            parser.parseParamsFile("remote.server", format1file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        MsEnzyme enzyme = parser.getSearchEnzyme();
        assertNotNull(enzyme);
        assertEquals("trypsin", enzyme.getName());
        assertEquals(Sense.CTERM, enzyme.getSense());
        assertEquals("RK", enzyme.getCut());
        assertNull(enzyme.getNocut());
        assertNull(enzyme.getDescription());

        // format 2
        try {
            parser.parseParamsFile("remote.server", format2file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        enzyme = parser.getSearchEnzyme();
        assertNotNull(enzyme);
        assertEquals("trypsin", enzyme.getName());
        assertEquals(Sense.CTERM, enzyme.getSense());
        assertEquals("RK", enzyme.getCut());
        assertNull(enzyme.getNocut());
        assertNull(enzyme.getDescription());
    }

    public void testGetDynamicResidueMods() {
        // format 1
        try {
            parser.parseParamsFile("remote.server", format1file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        List<MsResidueModification> dynaResMods = parser.getDynamicResidueMods();
        assertNotNull(dynaResMods);
        assertEquals(2, dynaResMods.size());
        Collections.sort(dynaResMods, new Comparator<MsResidueModification>() {
            public int compare(MsResidueModification o1,
                    MsResidueModification o2) {
                return Character.valueOf(o1.getModifiedResidue()).compareTo(Character.valueOf(o2.getModifiedResidue()));
            }});
        assertEquals('S', dynaResMods.get(0).getModifiedResidue());
        assertEquals('#', dynaResMods.get(0).getModificationSymbol());
        assertEquals(80.0, dynaResMods.get(0).getModificationMass().doubleValue());
        assertEquals('T', dynaResMods.get(1).getModifiedResidue());
        assertEquals('#', dynaResMods.get(1).getModificationSymbol());
        assertEquals(80.0, dynaResMods.get(1).getModificationMass().doubleValue());
        
        // format 2
        try {
            parser.parseParamsFile("remote.server", format2file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        dynaResMods = parser.getDynamicResidueMods();
        assertNotNull(dynaResMods);
        assertEquals(5, dynaResMods.size());
        Collections.sort(dynaResMods, new Comparator<MsResidueModification>() {
            public int compare(MsResidueModification o1,
                    MsResidueModification o2) {
                return Character.valueOf(o1.getModifiedResidue()).compareTo(Character.valueOf(o2.getModifiedResidue()));
            }});
        assertEquals('A', dynaResMods.get(0).getModifiedResidue());
        assertEquals('B', dynaResMods.get(1).getModifiedResidue());
        assertEquals('S', dynaResMods.get(2).getModifiedResidue());
        assertEquals('T', dynaResMods.get(3).getModifiedResidue());
        assertEquals('Y', dynaResMods.get(4).getModifiedResidue());
        
        for (int i = 0; i < 2; i++) {
            assertEquals('p', dynaResMods.get(i).getModificationSymbol());
            assertEquals(-18.01056, dynaResMods.get(i).getModificationMass().doubleValue());
        }
        
        for (int i = 2; i < dynaResMods.size(); i++) {
            assertEquals('p', dynaResMods.get(i).getModificationSymbol());
            assertEquals(79.9663, dynaResMods.get(i).getModificationMass().doubleValue());
        }
    }

    public void testGetStaticResidueMods() {
        // format 1
        try {
            parser.parseParamsFile("remote.server", format1file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        List<MsResidueModification> staticResMods = parser.getStaticResidueMods();
        assertNotNull(staticResMods);
        assertEquals(1, staticResMods.size());
        
        assertEquals('C', staticResMods.get(0).getModifiedResidue());
        assertEquals('\u0000', staticResMods.get(0).getModificationSymbol());
        assertEquals(57.02146, staticResMods.get(0).getModificationMass().doubleValue());
        
        // format 2
        try {
            parser.parseParamsFile("remote.server", format2file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        staticResMods = parser.getStaticResidueMods();
        assertNotNull(staticResMods);
        assertEquals(20, staticResMods.size());
    }

    public void testGetStaticTerminalMods() {
        // format 1
        try {
            parser.parseParamsFile("remote.server", format1file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        List<MsTerminalModification> staticTermMods = parser.getStaticTerminalMods();
        assertNotNull(staticTermMods);
        assertEquals(2, staticTermMods.size());
        
        assertEquals(Terminal.NTERM, staticTermMods.get(0).getModifiedTerminal());
        assertEquals('*', staticTermMods.get(0).getModificationSymbol());
        assertEquals(987.654, staticTermMods.get(0).getModificationMass().doubleValue());
        
        assertEquals(Terminal.CTERM, staticTermMods.get(1).getModifiedTerminal());
        assertEquals('x', staticTermMods.get(1).getModificationSymbol());
        assertEquals(10.0, staticTermMods.get(1).getModificationMass().doubleValue());
        
        
        // format 2
        try {
            parser.parseParamsFile("remote.server", format2file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        staticTermMods = parser.getStaticTerminalMods();
        assertNotNull(staticTermMods);
        assertEquals(2, staticTermMods.size());
        
        assertEquals(Terminal.NTERM, staticTermMods.get(0).getModifiedTerminal());
        assertEquals('*', staticTermMods.get(0).getModificationSymbol());
        assertEquals(123.4567, staticTermMods.get(0).getModificationMass().doubleValue());
        
        assertEquals(Terminal.CTERM, staticTermMods.get(1).getModifiedTerminal());
        assertEquals('*', staticTermMods.get(1).getModificationSymbol());
        assertEquals(100.0, staticTermMods.get(1).getModificationMass().doubleValue());
    }

    public void testGetDynamicTerminalMods() {
        // format 1
        try {
            parser.parseParamsFile("remote.server", format1file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        List<MsTerminalModification> dynaTermMods = parser.getDynamicTerminalMods();
        assertNotNull(dynaTermMods);
        assertEquals(2, dynaTermMods.size());
        
        assertEquals(Terminal.NTERM, dynaTermMods.get(0).getModifiedTerminal());
        assertEquals('*', dynaTermMods.get(0).getModificationSymbol());
        assertEquals(156.1011, dynaTermMods.get(0).getModificationMass().doubleValue());
        
        assertEquals(Terminal.CTERM, dynaTermMods.get(1).getModifiedTerminal());
        assertEquals('y', dynaTermMods.get(1).getModificationSymbol());
        assertEquals(123.4567, dynaTermMods.get(1).getModificationMass().doubleValue());
        
        // format 2
        try {
            parser.parseParamsFile("remote.server", format2file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        dynaTermMods = parser.getDynamicTerminalMods();
        assertNotNull(dynaTermMods);
        assertEquals(2, dynaTermMods.size());
        
        assertEquals(Terminal.NTERM, dynaTermMods.get(0).getModifiedTerminal());
        assertEquals('*', dynaTermMods.get(0).getModificationSymbol());
        assertEquals(9876.5432, dynaTermMods.get(0).getModificationMass().doubleValue());
        
        assertEquals(Terminal.CTERM, dynaTermMods.get(1).getModifiedTerminal());
        assertEquals('*', dynaTermMods.get(1).getModificationSymbol());
        assertEquals(200.0, dynaTermMods.get(1).getModificationMass().doubleValue());
    }

    public void testGetPrimaryScoreType() {
        // format 1
        try {
            parser.parseParamsFile("remote.server", format1file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        assertNotNull(parser.getPrimaryScoreType());
        assertEquals(PrimaryScore.XCORR, parser.getPrimaryScoreType());
        assertNotNull(parser.getSecondaryScoreType());
        assertEquals(SecondaryScore.DELTA_CN, parser.getSecondaryScoreType());
        
        // format 2
        try {
            parser.parseParamsFile("remote.server", format2file);
        }
        catch (DataProviderException e) {
            e.printStackTrace();
            fail("Valid file");
        }
        assertNotNull(parser.getPrimaryScoreType());
        assertEquals(PrimaryScore.BIN_PROB, parser.getPrimaryScoreType());
        assertNotNull(parser.getSecondaryScoreType());
        assertEquals(SecondaryScore.DELTA_CN, parser.getSecondaryScoreType());
    }
}
