package org.yeastrc.ms.dao.util;

import junit.framework.TestCase;

public class NrSeqLookupUtilTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetProteinId() {
        String databaseName = "my/test/database";
        String accession = "accession_string_1";
        
        assertEquals(25, NrSeqLookupUtil.getProteinId(databaseName, accession));
        
        databaseName = "dummy";
        assertEquals(0, NrSeqLookupUtil.getProteinId(databaseName, accession));
        
        databaseName = "my/test/database2";
        assertEquals(0, NrSeqLookupUtil.getProteinId(databaseName, accession));
        
        accession = "accession_string_2";
        assertEquals(27, NrSeqLookupUtil.getProteinId(databaseName, accession));
    }
    
    public void testGetDatabaseId() {
        String database = "my/test/database";
        assertEquals(1, NrSeqLookupUtil.getDatabaseId(database));
        
        database = "my/test/database2";
        assertEquals(2, NrSeqLookupUtil.getDatabaseId(database));
        
        database = "database_does_not_exist";
        assertEquals(0, NrSeqLookupUtil.getDatabaseId(database));
        
        database = null;
        assertEquals(0, NrSeqLookupUtil.getDatabaseId(database));
    }
    
    public void testGetProteinAccession() {
        int searchDatabaseId = 1;
        int proteinId = 25;
        
        assertEquals("accession_string_1", NrSeqLookupUtil.getProteinAccession(searchDatabaseId, proteinId));
        
        searchDatabaseId = 2;
        assertNull(NrSeqLookupUtil.getProteinAccession(searchDatabaseId, proteinId));
        
        proteinId = 27;
        assertEquals("accession_string_2", NrSeqLookupUtil.getProteinAccession(searchDatabaseId, proteinId));
    }
}
