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
        String databaseName = "my/test/database";
        String accession = "accession_string_1";
        
        assertEquals(25, NrSeqLookupUtil.getProteinId(databaseName, accession));
        
        databaseName = "dummy";
        try {NrSeqLookupUtil.getProteinId(databaseName, accession); fail("No match should be found");}
        catch(NrSeqLookupException e){}
        
        
        databaseName = "my/test/database2";
        try {NrSeqLookupUtil.getProteinId(databaseName, accession); fail("No match should be found");}
        catch(NrSeqLookupException e){}
        
        accession = "accession_string_4";
        assertEquals(28, NrSeqLookupUtil.getProteinId(databaseName, accession));
    }
    
    public void testGetDatabaseId() {
        String database = "my/test/database";
        assertEquals(1, NrSeqLookupUtil.getDatabaseId(database));
        
        database = "my/test/database2";
        assertEquals(2, NrSeqLookupUtil.getDatabaseId(database));
        
        database = "database_does_not_exist";
        try {NrSeqLookupUtil.getDatabaseId(database); fail("No match should be found");}
        catch(NrSeqLookupException e){}
        
        database = null;
        try {NrSeqLookupUtil.getDatabaseId(database); fail("No match should be found");}
        catch(NrSeqLookupException e){}
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
