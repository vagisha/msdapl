/**
 * MsSequenceDatabaseDAOImplTest.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dto.MsSequenceDatabase;

import junit.framework.TestCase;

/**
 * 
 */
public class MsSequenceDatabaseDAOImplTest extends TestCase {

    private MsSequenceDatabaseDAO seqDbDao = DAOFactory.instance().getMsSequenceDatabaseDAO();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSearchDatabase() {
        
        int searchId_1 = 1;
        int searchId_2 = 2;
        
        // try to select databases for the search ids; make sure no databases are found
        List<MsSequenceDatabase> dbs_1 = seqDbDao.loadSearchDatabases(searchId_1);
        assertEquals(0, dbs_1.size());
        
        List<MsSequenceDatabase> dbs_2 = seqDbDao.loadSearchDatabases(searchId_2);
        assertEquals(0, dbs_2.size());
        
        // create a sequence database and assign it to both the search ids
        MsSequenceDatabase db1 = new MsSequenceDatabase();
        db1.setServerAddress("serverAddress_1");
        db1.setServerPath("serverPath_1");
        db1.setProteinCount(20);
        db1.setSequenceLength(100);
        
        // assign this database to searchId_1; this will return the id from the msSequenceDatabaseDetails table
        int db1_id = seqDbDao.saveSearchDatabase(db1, searchId_1);
        // we assign the same database to searchId_2.  Since no new entry should be created in 
        // msSequenceDatabaseDetails table, the returned id should be the same as the one above
        assertEquals(db1_id, seqDbDao.saveSearchDatabase(db1, searchId_2));
        
        // create another dababase with some null values
        MsSequenceDatabase db2 = new MsSequenceDatabase();
        db2.setServerAddress("serverAddress_1");
        db2.setProteinCount(20);
        
        // assign the database to searchId_1; we should get a different id since a new entry will be
        // created in msSequenceDatabaseDetails
        int db2_id = seqDbDao.saveSearchDatabase(db2, searchId_1);
        assertNotSame(db1_id, db2_id);
        
        // load the databases associated with searchId_1 and check the returned objects
        List<MsSequenceDatabase> searchId_1_dbs = seqDbDao.loadSearchDatabases(searchId_1);
        assertEquals(2, searchId_1_dbs.size());
        
        // sort the results by db id
        Collections.sort(searchId_1_dbs, new Comparator<MsSequenceDatabase> () {
            public int compare(MsSequenceDatabase o1, MsSequenceDatabase o2) {
                return new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
            }});
        
        assertEquals(searchId_1_dbs.get(0).getServerAddress(), db1.getServerAddress());
        assertEquals(searchId_1_dbs.get(0).getServerPath(), db1.getServerPath());
        assertEquals(searchId_1_dbs.get(0).getSequenceLength(), db1.getSequenceLength());
        assertEquals(searchId_1_dbs.get(0).getProteinCount(), db1.getProteinCount());
        
        assertEquals(searchId_1_dbs.get(1).getServerAddress(), db2.getServerAddress());
        assertEquals(searchId_1_dbs.get(1).getServerPath(), db2.getServerPath());
        assertEquals(searchId_1_dbs.get(1).getSequenceLength(), db2.getSequenceLength());
        assertEquals(searchId_1_dbs.get(1).getProteinCount(), db2.getProteinCount());
        
        // delete sequence database associations for searchId_1;
        seqDbDao.deleteSearchDatabases(searchId_1);
        searchId_1_dbs = seqDbDao.loadSearchDatabases(searchId_1);
        assertEquals(0, searchId_1_dbs.size());
        
        // we should still have one sequence db associated with searchId_2
        List<MsSequenceDatabase> searchId_2_dbs = seqDbDao.loadSearchDatabases(searchId_2);
        assertEquals(1, searchId_2_dbs.size());
        
        // delete sequence database associations for searchId_2
        seqDbDao.deleteSearchDatabases(searchId_2);
        searchId_2_dbs = seqDbDao.loadSearchDatabases(searchId_2);
        assertEquals(0, searchId_2_dbs.size());
        
    }
}
