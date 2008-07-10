package org.yeastrc.ms.dao;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.domain.MsDigestionEnzyme;
import org.yeastrc.ms.domain.MsRun;

public class MsRunDAOImplTest extends BaseDAOTestCase {

    private static final int msExperimentId_1 = 1;
    private static final int msExperimentId_2 = 25;
    
    protected void setUp() throws Exception {
        super.setUp();
        runDao = DAOFactory.instance().getMsRunDAO();
        enzymeDao = DAOFactory.instance().getEnzymeDAO();
        scanDao = DAOFactory.instance().getMsScanDAO();
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        runDao.deleteRunsForExperiment(msExperimentId_1);
        runDao.deleteRunsForExperiment(msExperimentId_2);
    }
    
    public void testOperationsOnMsRun() {
        
        // nothing in the database right now
        List<Integer> runIdList = runDao.loadRunIdsForExperiment(msExperimentId_1);
        assertEquals(0, runIdList.size());
        
        MsRun run1 = createRun(msExperimentId_1);
        int runId1 = runDao.saveRun(run1);
        
        // lets make sure it got saved
        runIdList = runDao.loadRunIdsForExperiment(msExperimentId_1);
        assertEquals(1, runIdList.size());
        assertEquals(runId1, runIdList.get(0).intValue());
        checkRun(run1, runDao.loadRun(runId1));
        
        
        // create a run with a different experiment id and save it
        MsRun run2 = createRun(msExperimentId_2);
        int runId2 = runDao.saveRun(run2);
        
        // make sure there is only one run with our original experiment id
        runIdList = runDao.loadRunIdsForExperiment(msExperimentId_1);
        assertEquals(1, runIdList.size());
        
        
        // make sure there is only 1 run with the other experiment id
        runIdList = runDao.loadRunIdsForExperiment(msExperimentId_2);
        assertEquals(1, runIdList.size());
        run2 = runDao.loadRun(runIdList.get(0));
        checkRun(run2, runDao.loadRun(runId2));
    }

    public void testLoadRunsForExperiment() {
        MsRun run = createRun(msExperimentId_1);
        runDao.saveRun(run);
        List <MsRun> runs = runDao.loadExperimentRuns(msExperimentId_1);
        assertEquals(1, runs.size());
        checkRun(run, runs.get(0));
    }
   

    public void testLoadRunsForFileNameAndSha1Sum() {
        MsRun run = createRun(msExperimentId_1);
        runDao.saveRun(run);
        run = createRun(msExperimentId_2);
        runDao.saveRun(run);
        
        List<Integer> runs = runDao.runIdsFor("my_file1.ms2", "sha1sum");
        assertEquals(2, runs.size());
    }
    
    
    public void testDeleteRunsForExperiment() {
        MsRun run = createRun(msExperimentId_1);
        runDao.saveRun(run);
        run = createRun(msExperimentId_1);
        runDao.saveRun(run);
        run = createRun(msExperimentId_2); // different experiment
        runDao.saveRun(run);
        
        int origSize = runDao.loadRunIdsForExperiment(msExperimentId_1).size();
        assertTrue(origSize == 2);
        
        // delete experiment 2 runs
        runDao.deleteRunsForExperiment(msExperimentId_2); 
        assertEquals(origSize, runDao.loadRunIdsForExperiment(msExperimentId_1).size());
        
        runDao.deleteRunsForExperiment(msExperimentId_1); 
        assertEquals(0, runDao.loadRunIdsForExperiment(msExperimentId_1).size());
    }
    
    public void testSaveAndLoadRunWithNoEnzymes() {
        // create a run and save it
        MsRun run = createRun(msExperimentId_1);
        int runId = runDao.saveRun(run);
        
        // read back the run
        MsRun dbRun = runDao.loadRun(runId);
        assertEquals(0, dbRun.getEnzymeList().size());
    }
    
    public void testSaveAndLoadRunWithEnzymeInfo() {
        
        // load some enzymes from the database
        MsDigestionEnzyme enzyme1 = enzymeDao.loadEnzyme(1);
        MsDigestionEnzyme enzyme2 = enzymeDao.loadEnzyme(2);
        MsDigestionEnzyme enzyme3 = enzymeDao.loadEnzyme(3);
        
        assertNotNull(enzyme1);
        assertNotNull(enzyme2);
        assertNotNull(enzyme3);
        
        
        // create a run with enzyme information
        List <MsDigestionEnzyme> enzymeList1 = new ArrayList<MsDigestionEnzyme>(2);
        enzymeList1.add(enzyme1);
        enzymeList1.add(enzyme2);
        MsRun run1 = createRunWEnzymeInfo(msExperimentId_1, enzymeList1);
        
        // save the run
        int runId_1 = runDao.saveRun(run1);
        
        // now read back the run and make sure it has the enzyme information
        MsRun runFromDb_1 = runDao.loadRun(runId_1);
        List<MsDigestionEnzyme> enzymes = runFromDb_1.getEnzymeList();
        assertNotNull(enzymes);
        assertEquals(2, enzymes.size());
        
        
        // save another run for this experiment
        List <MsDigestionEnzyme> enzymeList2 = new ArrayList<MsDigestionEnzyme>(2);
        enzymeList2.add(enzyme3);
        MsRun run2 = createRunWEnzymeInfo(msExperimentId_1, enzymeList2);
        
        
        // save the run
        int runId_2 = runDao.saveRun(run2);
        
        // now read back the run and make sure it has the enzyme information
        MsRun runFromDb_2 = runDao.loadRun(runId_2);
        enzymes = runFromDb_2.getEnzymeList();
        assertNotNull(enzymes);
        assertEquals(1, enzymes.size());
        checkEnzyme(enzyme3, enzymes.get(0));
        
        
        List<MsRun> runsWenzymes = runDao.loadExperimentRuns(msExperimentId_1);
        assertEquals(2, runsWenzymes.size());
        
    }
    

    public void testSaveAndDeleteRunsWithEnzymeInfoAndScans() {
        
        // load some enzymes from the database
        MsDigestionEnzyme enzyme1 = enzymeDao.loadEnzyme(1);
        MsDigestionEnzyme enzyme2 = enzymeDao.loadEnzyme(2);
        MsDigestionEnzyme enzyme3 = enzymeDao.loadEnzyme(3);
        
        assertNotNull(enzyme1);
        assertNotNull(enzyme2);
        assertNotNull(enzyme3);
        
        
        // create a run with enzyme information
        List <MsDigestionEnzyme> enzymeList1 = new ArrayList<MsDigestionEnzyme>(2);
        enzymeList1.add(enzyme1);
        enzymeList1.add(enzyme2);
        MsRun run1 = createRunWEnzymeInfo(msExperimentId_1, enzymeList1);
        
        // save the run
        int runId_1 = runDao.saveRun(run1);
        
        // now read back the run and make sure it has the enzyme information
        MsRun runFromDb_1 = runDao.loadRun(runId_1);
        List<MsDigestionEnzyme> enzymes = runFromDb_1.getEnzymeList();
        assertNotNull(enzymes);
        assertEquals(2, enzymes.size());
        
        
        // save another run for ANOTHER experiment
        List <MsDigestionEnzyme> enzymeList2 = new ArrayList<MsDigestionEnzyme>(2);
        enzymeList2.add(enzyme3);
        MsRun run2 = createRunWEnzymeInfo(msExperimentId_2, enzymeList2);
        
        
        // save the run
        int runId_2 = runDao.saveRun(run2);
        
        
        // save some scans for the runs
        saveScansForRun(runId_1, 10);
        saveScansForRun(runId_2, 5);
        
        // make sure the run and associated enzyme information got saved (RUN 1)
        assertEquals(1, runDao.loadExperimentRuns(msExperimentId_1).size());
        assertEquals(2, enzymeDao.loadEnzymesForRun(runId_1).size());
        assertEquals(10, scanDao.loadScanIdsForRun(runId_1).size());
        
        // make sure the run and associated enzyme information got saved (RUN 2)
        assertEquals(1, runDao.loadExperimentRuns(msExperimentId_2).size());
        assertEquals(1, enzymeDao.loadEnzymesForRun(runId_2).size());
        assertEquals(5, scanDao.loadScanIdsForRun(runId_2).size());
        
        // now delete the runs for experiment 1
        runDao.deleteRunsForExperiment(msExperimentId_1);
        
        // make sure the run is deleted ...
        assertEquals(0, runDao.loadExperimentRuns(msExperimentId_1).size());
        // ... and the associated enzyme information is deleted ...
        assertEquals(0, enzymeDao.loadEnzymesForRun(runId_1).size());
        // ... and all scans for the run are deleted.
        assertEquals(0, scanDao.loadScanIdsForRun(runId_1).size());
        
        // make sure nothing was delete for experiment 2
        assertEquals(1, runDao.loadExperimentRuns(msExperimentId_2).size());
        assertEquals(1, enzymeDao.loadEnzymesForRun(runId_2).size());
        assertEquals(5, scanDao.loadScanIdsForRun(runId_2).size());
        
        // now delete the runs for experiment 1
        runDao.deleteRunsForExperiment(msExperimentId_2);
        
        // make sure the run is deleted ...
        assertEquals(0, runDao.loadExperimentRuns(msExperimentId_2).size());
        // ... and the associated enzyme information is deleted ...
        assertEquals(0, enzymeDao.loadEnzymesForRun(runId_2).size());
        // ... and all scans for the run are deleted.
        assertEquals(0, scanDao.loadScanIdsForRun(runId_2).size());
        
    }

    
    protected void checkEnzyme(MsDigestionEnzyme e1, MsDigestionEnzyme e2) {
        assertEquals(e1.getName(), e2.getName());
        assertEquals(e1.getSense(), e2.getSense());
        assertEquals(e1.getCut(), e2.getCut());
        assertEquals(e1.getNocut(), e2.getNocut());
        assertEquals(e1.getDescription(), e2.getDescription());
    }
}
