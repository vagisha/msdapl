package org.yeastrc.ms.parser.sqtFile;

import junit.framework.TestCase;

import org.yeastrc.ms.parser.ParserException;

public class SQTParserTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testParseScan() {
//        String line = "S  00016\t00016\t1\t0 \t shamu046\t 742.52000\t 0.0\t0.0 \t0";
        String line = "S       01718   01718   1       0       node0269        993.88000        0.0    0.0     0";
        SQTFileReader reader = new SQTFileReader();
        try {
            reader.parseScan(line);
        }
        catch (ParserException e) {
            fail("Valid scan line");
            e.printStackTrace();
        }
        
    }
    
    
    public void testParseLocus() {
        SQTFileReader reader = new SQTFileReader();
        
        String locus = "H\tName\tValue";
        try {reader.parseLocus(locus); fail("Not a 'L' line");}
        catch(ParserException e){}
        
        locus = "L";
        try {reader.parseLocus(locus); fail("Invalid 'L' line");}
        catch(ParserException e){}
        
        locus = "L locus ";
        try {
            DbLocus loc = reader.parseLocus(locus);
            assertEquals("locus", loc.getAccession());
            assertEquals("", loc.getDescription());
        }
        catch (ParserException e) {
            fail("Exception parsing valid locus line");
            e.printStackTrace();
        }
        
        locus = "L locus description for locus ";
        try {
            DbLocus loc = reader.parseLocus(locus);
            assertEquals("locus", loc.getAccession());
            assertEquals("description for locus", loc.getDescription());
        }
        catch (ParserException e) {
            fail("Exception parsing valid locus line");
            e.printStackTrace();
        }
        
        locus = "L       Placeholder satisfying DTASelect";
        try {
            DbLocus loc = reader.parseLocus(locus);
            assertEquals("Placeholder", loc.getAccession());
            assertEquals("satisfying DTASelect", loc.getDescription());
        }
        catch (ParserException e) {
            fail("Exception parsing valid locus line");
            e.printStackTrace();
        }
        
        locus = "L       ORFP:YKL160W";
        try {
            DbLocus loc = reader.parseLocus(locus);
            assertEquals("ORFP:YKL160W", loc.getAccession());
            assertEquals("", loc.getDescription());
        }
        catch (ParserException e) {
            fail("Exception parsing valid locus line");
            e.printStackTrace();
        }
    }

    
    public void parsePeptideResult() {
        
    }
}
