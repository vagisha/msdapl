package org.yeastrc.ms.dao;

import java.util.Arrays;
import java.util.List;

import org.yeastrc.ms.dao.MsDigestionEnzymeDAO.EnzymeProperties;
import org.yeastrc.ms.domain.IMsEnzyme;
import org.yeastrc.ms.domain.db.MsDigestionEnzyme;

public class MsDigestionEnzymeDAOImplTest extends BaseDAOTestCase {

    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadEnzymeString() {
        List<MsDigestionEnzyme> enzymes = enzymeDao.loadEnzymes("trypsin");
        assertNotNull(enzymes);
        assertEquals(1, enzymes.size());
        
        enzymes = enzymeDao.loadEnzymes("xyz");
        assertEquals(0, enzymes.size());
    }

    public void testLoadEnzymeStringIntStringString() {
        // load an enzyme we know exists in the database
        List<MsDigestionEnzyme> enzymes = enzymeDao.loadEnzymes("trypsin", 1, "KR", "P");
        assertEquals(1, enzymes.size());
        MsDigestionEnzyme enzyme = enzymes.get(0);
        assertNotNull(enzyme);
        assertEquals("trypsin".toUpperCase(), enzyme.getName().toUpperCase());
        assertEquals(1, enzyme.getSense());
        assertEquals("KR", enzyme.getCut());
        assertEquals("P", enzyme.getNocut());
        
        // thi senzyme does not exist in the database.
        enzymes = enzymeDao.loadEnzymes("trypsin", 0, "KR", "P");
        assertEquals(0, enzymes.size());
    }
    
    public void testSaveEnzymeForRunCheckName() {
        // load an enzyme we know exists in the database
        List<MsDigestionEnzyme> enzymes = enzymeDao.loadEnzymes("trypsin", 1, "KR", "P");
        assertEquals(1, enzymes.size());
        MsDigestionEnzyme enzyme = enzymes.get(0);
        int enzyme_db_id = enzyme.getId();
        
        int runId = 20; 
        
        // try to link a runid with with this enzyme
        // the database id returned by the save method should be the same as for the enzyme above
        int enzymeId_1 = enzymeDao.saveEnzymeforRun(enzyme, runId);
        assertEquals(enzymeId_1, enzyme_db_id);
        
        // we know this enzyme does not exist in the database
        enzymes = enzymeDao.loadEnzymes("Dummy", 0, "ABC", null);
        assertEquals(0, enzymes.size());
        
        // save the enzyme
        enzyme = makeDigestionEnzyme("Dummy", 0, "ABC", null);
        
        // create a link between the enzyme and the runID
        // this should also save a new entry in the msDigestionEnzyme table
        int enzymeId_2 = enzymeDao.saveEnzymeforRun(enzyme, runId);
        // make sure a new entry was created for the enzyme
        assertNotSame(enzymeId_1, enzymeId_2);

        // make sure we now have two enzyme entries for this run;
        enzymes = enzymeDao.loadEnzymesForRun(20);
        assertEquals(2, enzymes.size());


        // try to create another link for this enzyme to another runId. 
        // This time specify the parameters that will be used to look for 
        // a matching run in the database;
        enzyme = new MsDigestionEnzyme();
        enzyme.setName("Dummy");
        EnzymeProperties[] properties = new EnzymeProperties[]{EnzymeProperties.NAME};
        int enzymeId_3 = enzymeDao.saveEnzymeforRun(enzyme, 30, Arrays.asList(properties));
        // this should not have saved a new enzyme so the returned id should the the same as before
        assertEquals(enzymeId_3, enzymeId_2);
        
        
        // clean up 
        // remove entries from the msRunEnzyme table
        enzymeDao.deleteEnzymesByRunId(runId);
        enzymeDao.deleteEnzymesByRunId(30);
        // remove the new entry we created in the msDigestionEnzyme table
        enzymeDao.deleteEnzymeById(enzymeId_2);
        
    }
    
    public void testSenseValue() {
        MsDigestionEnzyme enzyme = new MsDigestionEnzyme();
        assertEquals(-1, enzyme.getSense());
        
        enzyme.setName("Dummy");
        
        int enzymeId = enzymeDao.saveEnzyme(enzyme);
        
        IMsEnzyme enzyme_db = enzymeDao.loadEnzyme(enzymeId);
        assertEquals(-1, enzyme_db.getSense());
        
        // clean up
        enzymeDao.deleteEnzymeById(enzymeId);
        assertNull(enzymeDao.loadEnzyme(enzymeId));
        
    }
    
}
