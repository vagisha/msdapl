package org.yeastrc.ms.parser.ms2File;

import junit.framework.TestCase;

public class Ms2FileReaderTest extends TestCase {

    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testParseHeader() {
        Ms2FileReader reader = new Ms2FileReader();
        String header = "H\t FilteringProgram\tParc";
        
        String[] parsed = reader.parseHeader(header);
        assertEquals(2, parsed.length);
        assertEquals("FilteringProgram", parsed[0]);
        assertEquals("Parc", parsed[1]);
        
        header = "H\t FilteringProgram  Parc1, Parc2 !@#$";
        parsed = reader.parseHeader(header);
        assertEquals(2, parsed.length);
        assertEquals("FilteringProgram", parsed[0]);
        assertEquals("Parc1, Parc2 !@#$", parsed[1]);
        
        header = "H\t FilteringProgram ";
        parsed = reader.parseHeader(header);
        assertEquals(2, parsed.length);
        assertEquals("FilteringProgram", parsed[0]);
        assertEquals("", parsed[1]);
        
        header = "H\t FilteringProgram";
        parsed = reader.parseHeader(header);
        assertEquals(2, parsed.length);
        assertEquals("FilteringProgram", parsed[0]);
        assertEquals("", parsed[1]);
    }
}
