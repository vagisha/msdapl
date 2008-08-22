package org.yeastrc.ms.parser.sequestParams;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.Database;

public class SequestParamsParserTest extends TestCase {

    private SequestParamsParser parser;
    
    protected void setUp() throws Exception {
        super.setUp();
        parser = new SequestParamsParser("remove.server");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testMatchParamValuePair() {
        // matching pattern ([\\S]+)\\s*=\\s*(.+&&[^;])\\s*;{0,1}.*
        String line = "database_name = /net/maccoss/vol2/mouse-contam.fasta";
        SequestParam param = parser.matchParamValuePair(line);
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
        MsEnzyme enzyme = parser.matchEnzyme(m, "1");
        assertNull(enzyme);
        enzyme = parser.matchEnzyme(m, "0");
        assertNotNull(enzyme);
        assertEquals("No_Enzyme", enzyme.getName());
        assertEquals(Sense.CTERM, enzyme.getSense());
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
        assertEquals(Sense.NTERM, enzyme.getSense());
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
    
    
    public void testParseParamsFile() {
        String file = "resources/sequest.params";
        try {
            parser.parseParamsFile(file);
            Database db = parser.getSearchDatabase();
            assertNotNull(db);
            assertEquals("remove.server", db.getServerAddress());
            assertEquals("/net/maccoss/vol2/software/pipeline/dbase/mouse-contam.fasta", db.getServerPath());
            assertEquals(0, db.getSequenceLength());
            assertEquals(0, db.getProteinCount());
            
            MsEnzyme enzyme = parser.getSearchEnzyme();
            assertNotNull(enzyme);
            assertEquals("No_Enzyme", enzyme.getName());
            assertNull(enzyme.getDescription());
            assertEquals(Sense.CTERM, enzyme.getSense());
            assertEquals("-", enzyme.getCut());
            assertEquals("-", enzyme.getNocut());
            
            assertEquals(0, parser.getStaticTerminalMods().size());
            assertEquals(0, parser.getDynamicResidueMods().size());
            
            List<MsResidueModification> resMods = parser.getStaticResidueMods();
            assertEquals(1, resMods.size());
            MsResidueModification mod = resMods.get(0);
            assertEquals('C', mod.getModifiedResidue());
            assertTrue(57.0 == mod.getModificationMass().doubleValue());
            assertEquals('\u0000', mod.getModificationSymbol());
           
        }
        catch (DataProviderException e) {
            fail("sequest.param is valid");
        }
    }
}
