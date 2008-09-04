package org.yeastrc.ms.dao.util;

import org.yeastrc.ms.dao.nrseq.NrSeqLookupException;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;

import junit.framework.TestCase;

public class NrSeqLookupUtilTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetProteinId() {
        String databaseName = "database";
        String accession = "accession_string_1";
        
        assertEquals(25, NrSeqLookupUtil.getProteinId(databaseName, accession));
        
        databaseName = "dummy";
        int id = NrSeqLookupUtil.getProteinId(databaseName, accession);
        assertEquals(0, id);
        
        
        databaseName = "database2";
        id = NrSeqLookupUtil.getProteinId(databaseName, accession);
        assertEquals(0, id);
        
        accession = "accession_string_4";
        assertEquals(28, NrSeqLookupUtil.getProteinId(databaseName, accession));
    }
    
    public void testGetDatabaseId() {
        String database = "database";
        assertEquals(1, NrSeqLookupUtil.getDatabaseId(database));
        
        database = "database2";
        assertEquals(2, NrSeqLookupUtil.getDatabaseId(database));
        
        database = "database_does_not_exist";
        int id = NrSeqLookupUtil.getDatabaseId(database);
        assertEquals(0, id);
        
        database = null;
        id = NrSeqLookupUtil.getDatabaseId(database);
        assertEquals(0, id);
    }
    
    public void testGetProteinAccession() {
        int searchDatabaseId = 1;
        int proteinId = 25;
        
        assertEquals("accession_string_1", NrSeqLookupUtil.getProteinAccession(searchDatabaseId, proteinId));
        
        searchDatabaseId = 2;
        try {NrSeqLookupUtil.getProteinAccession(searchDatabaseId, proteinId); fail("No match should be found");}
        catch(NrSeqLookupException e){}
        
        proteinId = 28;
        assertEquals("accession_string_4", NrSeqLookupUtil.getProteinAccession(searchDatabaseId, proteinId));
    }
}
