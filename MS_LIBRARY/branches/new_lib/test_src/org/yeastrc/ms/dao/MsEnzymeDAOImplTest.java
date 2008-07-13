package org.yeastrc.ms.dao;

import java.util.Arrays;
import java.util.List;

import org.yeastrc.ms.dao.MsEnzymeDAO.EnzymeProperties;
import org.yeastrc.ms.domain.MsEnzyme;
import org.yeastrc.ms.domain.MsEnzymeDb;
import org.yeastrc.ms.domain.MsEnzyme.Sense;

public class MsEnzymeDAOImplTest extends BaseDAOTestCase {

    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadEnzymeString() {
        List<MsEnzymeDb> enzymes = enzymeDao.loadEnzymes("trypsin");
        assertNotNull(enzymes);
        assertEquals(1, enzymes.size());
        
        enzymes = enzymeDao.loadEnzymes("xyz");
        assertEquals(0, enzymes.size());
    }

    public void testLoadEnzymeStringIntStringString() {
        // load an enzyme we know exists in the database
        List<MsEnzymeDb> enzymes = enzymeDao.loadEnzymes("trypsin", Sense.NTERM, "KR", "P");
        assertEquals(1, enzymes.size());
        MsEnzymeDb enzyme = enzymes.get(0);
        assertNotNull(enzyme);
        assertEquals("trypsin".toUpperCase(), enzyme.getName().toUpperCase());
        assertEquals(Sense.NTERM, enzyme.getSense());
        assertEquals("KR", enzyme.getCut());
        assertEquals("P", enzyme.getNocut());
        
        // this enzyme does not exist in the database.
        enzymes = enzymeDao.loadEnzymes("trypsin", Sense.UNKNOWN, "KR", "P");
        assertEquals(0, enzymes.size());
    }
    
    public void testSaveEnzymeForRunCheckName() {
        // load an enzyme we know exists in the database
        List<MsEnzymeDb> enzymes = enzymeDao.loadEnzymes("trypsin", Sense.NTERM, "KR", "P");
        assertEquals(1, enzymes.size());
        MsEnzymeDb oEnzyme = enzymes.get(0);
        int enzyme_db_id = oEnzyme.getId();
        
        int runId1 = 20; 
        int runId2 = 30;
        
        // try to link a runid with with this enzyme
        // the database id returned by the save method should be the same as for the enzyme above
        int enzymeId_1 = enzymeDao.saveEnzymeforRun(oEnzyme, runId1);
        assertEquals(enzymeId_1, enzyme_db_id);
        
        // we know this enzyme does not exist in the database
        enzymes = enzymeDao.loadEnzymes("Dummy", Sense.UNKNOWN, "ABC", null);
        assertEquals(0, enzymes.size());
        
        // create and save the enzyme
        MsEnzyme iEnzyme = makeDigestionEnzyme("Dummy", Sense.UNKNOWN, "ABC", null);
        // create a link between the enzyme and the runID
        // this should also save a new entry in the msDigestionEnzyme table
        int enzymeId_2 = enzymeDao.saveEnzymeforRun(iEnzyme, runId1);
        // make sure a new entry was created for the enzyme
        assertNotSame(enzymeId_1, enzymeId_2);

        // make sure we now have two enzyme entries for this run;
        enzymes = enzymeDao.loadEnzymesForRun(runId1);
        assertEquals(2, enzymes.size());


        // try to create another link for this enzyme to another runId. 
        // This time specify the parameters that will be used to look for 
        // a matching run in the database;
        iEnzyme = makeDigestionEnzyme("Dummy", null, null, null);
        EnzymeProperties[] properties = new EnzymeProperties[]{EnzymeProperties.NAME};
        int enzymeId_3 = enzymeDao.saveEnzymeforRun(iEnzyme, runId2, Arrays.asList(properties));
        // this should not have saved a new enzyme so the returned id should the the same as before
        assertEquals(enzymeId_3, enzymeId_2);
        
        
        // clean up 
        // remove entries from the msRunEnzyme table
        enzymeDao.deleteEnzymesForRun(runId1);
        assertEquals(0, enzymeDao.loadEnzymesForRun(runId1).size());
        enzymeDao.deleteEnzymesForRun(runId2);
        assertEquals(0, enzymeDao.loadEnzymesForRun(runId2).size());
        
    }
    
    public void testSenseValue() {
        
        MsEnzyme enzyme = super.makeDigestionEnzyme("Dummy", Sense.UNKNOWN, null, null);
        assertEquals(Sense.UNKNOWN, enzyme.getSense());
        int enzymeId = enzymeDao.saveEnzyme(enzyme);
        MsEnzymeDb enzyme_db = enzymeDao.loadEnzyme(enzymeId);
        assertEquals(Sense.UNKNOWN, enzyme_db.getSense());
        
        enzyme = super.makeDigestionEnzyme("Dummy", Sense.CTERM, null, null);
        enzymeId = enzymeDao.saveEnzyme(enzyme);
        enzyme_db = enzymeDao.loadEnzyme(enzymeId);
        assertEquals(Sense.CTERM, enzyme_db.getSense());
        
    }
    
    public static final class MsEnzymeTest implements MsEnzyme {

        private String name;
        private Sense sense;
        private String cut;
        private String nocut;

        public MsEnzymeTest(String name, Sense sense, String cut, String nocut) {
            this.name = name;
            this.sense = sense;
            this.cut = cut;
            this.nocut = nocut;
        }
        
        public String getCut() {
            return cut;
        }

        public String getDescription() {
            return null;
        }

        public String getName() {
            return name;
        }

        public String getNocut() {
            return nocut;
        }

        public Sense getSense() {
            return sense;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSense(Sense sense) {
            this.sense = sense;
        }

        public void setCut(String cut) {
            this.cut = cut;
        }

        public void setNocut(String nocut) {
            this.nocut = nocut;
        }
    }
}
