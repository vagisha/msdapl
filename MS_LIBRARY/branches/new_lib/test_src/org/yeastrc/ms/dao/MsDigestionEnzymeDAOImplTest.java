package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsDigestionEnzyme;

import junit.framework.TestCase;

public class MsDigestionEnzymeDAOImplTest extends TestCase {

    private MsDigestionEnzymeDAO enzymeDao;
    
    protected void setUp() throws Exception {
        super.setUp();
        enzymeDao = DAOFactory.instance().getEnzymeDAO();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadEnzymeString() {
        MsDigestionEnzyme enzyme = enzymeDao.loadEnzyme("trypsin");
        assertNotNull(enzyme);
        
        enzyme = enzymeDao.loadEnzyme("xyz");
        assertNull(enzyme);
    }

    public void testLoadEnzymeStringIntStringString() {
        MsDigestionEnzyme enzyme = enzymeDao.loadEnzyme("trypsin", 1, "KR", "P");
        assertNotNull(enzyme);
        assertEquals("trypsin".toUpperCase(), enzyme.getName().toUpperCase());
        assertEquals(1, enzyme.getSense());
        assertEquals("KR", enzyme.getCut());
        assertEquals("P", enzyme.getNocut());
        
        enzyme = enzymeDao.loadEnzyme("trypsin", 0, "KR", "P");
        assertNull(enzyme);
    }
    
    public void testSaveEnzymeForRun() {
        // this is an enzyme that should already be in the database
        MsDigestionEnzyme enzyme = enzymeDao.loadEnzyme("trypsin", 1, "KR", "P");
        assertNotNull(enzyme);
        
        int eid1 = enzyme.getId();
        
        int runId = 20;
        
        // create a link between the enzyme and the runId
        int enzymeId = enzymeDao.saveEnzymeforRun(enzyme, runId);
        // make sure no new entry was created for the enzyme
        assertEquals(eid1, enzymeId);
        
        // this enzyme should NOT be in the database
        enzyme = enzymeDao.loadEnzyme("Dummy", 0, "ABC", null);
        assertNull(enzyme);
        
        enzyme = new MsDigestionEnzyme();
        enzyme.setName("Dummy");
        enzyme.setCut("ABC");
        enzyme.setSense((short)0);
        
        // create a link beween the enzyme and the runID
        enzymeId = enzymeDao.saveEnzymeforRun(enzyme, runId);
        // make sure a new entry was created for the enzyme
        assertNotSame(eid1, enzymeId);
        
        // make sure we now have two enzyme entries for this run;
        List<MsDigestionEnzyme> enzymes = enzymeDao.loadEnzymesForRun(20);
        assertEquals(2, enzymes.size());
        
        
        // clean up 
        // remove entries from the msRunEnzyme table
        enzymeDao.deleteRunEnzymes(runId);
        // remove the new entry we created in the msDigestionEnzyme table
        enzymeDao.deleteEnzymeById(enzymeId);
        
    }
    
    public void testSaveEnzymeForRunEnzymeName() {
        // an enzyme with this name should not exist in the database;
        MsDigestionEnzyme enzyme = enzymeDao.loadEnzyme("Dummy");
        assertNull(enzyme);
        
        int runId = 20;
        // trying to link this run and enzyme should not succeed.
        assertFalse(enzymeDao.saveEnzymeForRun("Dummy", runId));
        
        // this is an enzyme that exists in the database
        enzyme = new MsDigestionEnzyme();
        enzyme.setName("Dummy2");
        enzyme.setCut("ABC");
        enzyme.setSense((short)0);
        enzymeDao.saveEnzyme(enzyme);
        enzyme = enzymeDao.loadEnzyme("Dummy2", 0, "ABC", null);
        assertNotNull(enzyme);
        
        // we should be able to link this enzyme to the run
        assertTrue(enzymeDao.saveEnzymeForRun("Dummy2", runId));
        
        // make sure we have a single enzyme entry for this run;
        List<MsDigestionEnzyme> enzymes = enzymeDao.loadEnzymesForRun(20);
        assertEquals(1, enzymes.size());
        
        // clean up
        // remove entries from the msRunEnzyme table
        enzymeDao.deleteRunEnzymes(runId);
        // remove the new entry we created in the msDigestionEnzyme table
        enzymeDao.deleteEnzymeById(enzyme.getId());
    }
    
}
