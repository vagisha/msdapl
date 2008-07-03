package org.yeastrc.ms.dao;

import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.yeastrc.ms.dto.IMsRun;
import org.yeastrc.ms.dto.IMsScan;
import org.yeastrc.ms.dto.MsDigestionEnzyme;
import org.yeastrc.ms.dto.MsRun;
import org.yeastrc.ms.dto.MsScan;

public class MsRunDAOImplTest extends TestCase {

    private MsRunDAO runDao;
    private MsDigestionEnzymeDAO enzymeDao;
    private MsScanDAO scanDao;
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
    
    public void testLoadRunIdsForExperiment() {
        List<Integer> runIdList = runDao.loadRunIdsForExperiment(msExperimentId_1);
        assertEquals(0, runIdList.size());
        
        IMsRun run = createRun(msExperimentId_1);
        runDao.saveRun(run);
        
        runIdList = runDao.loadRunIdsForExperiment(msExperimentId_1);
        assertEquals(1, runIdList.size());
        
        // create a run with a different experiment id and save it
        run = createRun(msExperimentId_2);
        runDao.saveRun(run);
        
        // make sure there is only one run with our original experiment id
        runIdList = runDao.loadRunIdsForExperiment(msExperimentId_1);
        assertEquals(1, runIdList.size());
        run = runDao.loadRun(runIdList.get(0));
        assertNotNull(run);
        assertEquals(msExperimentId_1, run.getMsExperimentId());
        
        // make sure there is only 1 run with the other experiment id
        runIdList = runDao.loadRunIdsForExperiment(msExperimentId_2);
        assertEquals(1, runIdList.size());
        run = runDao.loadRun(runIdList.get(0));
        assertEquals(msExperimentId_2, run.getMsExperimentId());
    }

    public void testLoadRunsForExperiment() {
        IMsRun run = createRun(msExperimentId_1);
        runDao.saveRun(run);
        List <IMsRun> runs = runDao.loadExperimentRuns(msExperimentId_1);
        assertEquals(1, runs.size());
        run = runs.get(0);
        checkRun(run);
    }
    
    public void testLoad() {
        IMsRun run = createRun(msExperimentId_1);
        runDao.saveRun(run);
        List<Integer> runIdList = runDao.loadRunIdsForExperiment(msExperimentId_1);
        assertTrue(runIdList.size() > 0);
        run = runDao.loadRun(runIdList.get(0));
        checkRun(run);
    }

    private void checkRun(IMsRun run) {
        assertEquals(msExperimentId_1, run.getMsExperimentId());
        assertEquals(MsRun.RunFileFormat.MS2.toString(), run.getFileFormat());
        assertEquals("my_file1.ms2", run.getFileName());
        assertEquals("Data dependent", run.getAcquisitionMethod());
        assertEquals("Dummy run", run.getComment());
        assertEquals("ms2Convert", run.getConversionSW());
        assertEquals("1.0", run.getConversionSWVersion());
        assertEquals("options string", run.getConversionSWOptions());
        assertEquals("profile", run.getDataType());
        assertEquals("ETD", run.getInstrumentModel());
        assertEquals("Thermo", run.getInstrumentVendor());
        assertNull(run.getInstrumentSN());
        assertEquals("sha1sum", run.getSha1Sum());
    }

    public void testLoadRunsForFileNameAndSha1Sum() {
        IMsRun run = createRun(msExperimentId_1);
        runDao.saveRun(run);
        run = createRun(msExperimentId_2);
        runDao.saveRun(run);
        
        List<Integer> runs = runDao.runIdsFor("my_file1.ms2", "sha1sum");
        assertEquals(2, runs.size());
    }
    
    
    public void testDeleteRunsForExperiment() {
        IMsRun run = createRun(msExperimentId_1);
        runDao.saveRun(run);
        run = createRun(msExperimentId_2);
        runDao.saveRun(run);
        
        int origSize = runDao.loadRunIdsForExperiment(msExperimentId_1).size();
        assertTrue(origSize > 0);
        // use a different experiment id from what we have in the database
        runDao.deleteRunsForExperiment(msExperimentId_2); 
        assertEquals(origSize, runDao.loadRunIdsForExperiment(msExperimentId_1).size());
        
        runDao.deleteRunsForExperiment(msExperimentId_1); 
        assertEquals(0, runDao.loadRunIdsForExperiment(msExperimentId_1).size());
    }
    
    public void testSaveAndLoadRunWithNoEnzymes() {
        // create a run and save it
        IMsRun run = createRun(msExperimentId_1);
        int runId = runDao.saveRun(run);
        
        // read back the run
        IMsRun dbRun = runDao.loadRun(runId);
        assertEquals(0, dbRun.getEnzymeList().size());
    }
    
    public void testSaveAndLoadRunWithEnzymeInfo() {
        IMsRun run = createRunWEnzymeInfo(msExperimentId_1);
        
        // get a list of the enzymes currently in the database
        MsDigestionEnzyme enzyme1 = enzymeDao.loadEnzyme(1);
        assertNotNull(enzyme1);
        MsDigestionEnzyme enzyme2 = enzymeDao.loadEnzyme(2);
        assertNotNull(enzyme2);
        
        // add some enzymes to the run
        run.addEnzyme(enzyme1);
        run.addEnzyme(enzyme2);
        
        // save the run
        int runId_1 = runDao.saveRun(run);
        
        // now read back the run and make sure it has the enzyme information
        IMsRun runFromDb = runDao.loadRunForFormat(runId_1);
        List<MsDigestionEnzyme> enzymes = runFromDb.getEnzymeList();
        assertNotNull(enzymes);
        assertEquals(2, enzymes.size());
        
        // save another run for this experiment
        run = createRunWEnzymeInfo(msExperimentId_1);
        MsDigestionEnzyme enzyme3 = enzymeDao.loadEnzyme(3);
        assertNotNull(enzyme3);
        MsDigestionEnzyme enzyme4 = enzymeDao.loadEnzyme(4);
        assertNotNull(enzyme4);
        
        // add the enzymes to the run
        run.addEnzyme(enzyme3);
        run.addEnzyme(enzyme4);
        
        // save the run
        int runId_2 = runDao.saveRun(run);
        
        List<IMsRun> runsWenzymes = runDao.loadExperimentRuns(msExperimentId_1);
        assertEquals(2, runsWenzymes.size());
        
        // make sure the enzymes associated with the runs are right;
        List<MsDigestionEnzyme> enzymes_1 = runsWenzymes.get(0).getEnzymeList();
        assertEquals(2, enzymes_1.size());
        assertEquals(enzyme1.getName(), enzymes_1.get(0).getName());
        assertEquals(enzyme2.getName(), enzymes_1.get(1).getName());
        
        List<MsDigestionEnzyme> enzymes_2 = runsWenzymes.get(1).getEnzymeList();
        assertEquals(2, enzymes_2.size());
        assertEquals(enzyme3.getName(), enzymes_2.get(0).getName());
        assertEquals(enzyme4.getName(), enzymes_2.get(1).getName());
        
    }
    
    public void testSaveAndDeleteRunsWithEnzymeInfoAndScans() {
        
        IMsRun run = createRunWEnzymeInfo(msExperimentId_1);
        
        // get a list of the enzymes currently in the database
        MsDigestionEnzyme enzyme1 = enzymeDao.loadEnzyme(1);
        assertNotNull(enzyme1);
        MsDigestionEnzyme enzyme2 = enzymeDao.loadEnzyme(2);
        assertNotNull(enzyme2);
        
        // add the enzymes to the run
        run.addEnzyme(enzyme1);
        run.addEnzyme(enzyme2);
        
        // save the run
        int runId = runDao.saveRun(run);
        
        // save some scans for the run
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int scanNum = random.nextInt(100);
            IMsScan scan = new MsScan();
            scan.setRunId(runId);
            scan.setStartScanNum(scanNum);
            scanDao.save(scan);
        }
        
        // make sure the run and associated enzyme information got saved
        List<IMsRun> runsWenzymes = runDao.loadExperimentRuns(msExperimentId_1);
        assertEquals(1, runsWenzymes.size());
        List<MsDigestionEnzyme> enzymes = enzymeDao.loadEnzymesForRun(runId);
        assertEquals(2, enzymes.size());
        List<Integer> scanIdList = scanDao.loadScanIdsForRun(runId);
        assertEquals(10, scanIdList.size());
        
        // now delete the run
        runDao.deleteRunsForExperiment(msExperimentId_1);
        
        // make sure the run is deleted ...
        runsWenzymes = runDao.loadExperimentRuns(msExperimentId_1);
        assertEquals(0, runsWenzymes.size());
        // ... and the associated enzyme information is deleted ...
        enzymes = enzymeDao.loadEnzymesForRun(runId);
        assertEquals(0, enzymes.size());
        // ... and all scans for the run are deleted.
        scanIdList = scanDao.loadScanIdsForRun(runId);
        assertEquals(0, scanIdList.size());
        
    }

    private IMsRun createRunWEnzymeInfo(int msExperimentId) {
        MsRun run = new MsRun();
        run.setMsExperimentId(msExperimentId);
        run.setFileFormat(MsRun.RunFileFormat.MS2.toString());
        run.setFileName("my_file1.ms2");
        run.setAcquisitionMethod("Data dependent");
        run.setComment("Dummy run");
        run.setConversionSW("ms2Convert");
        run.setConversionSWVersion("1.0");
        run.setConversionSWOptions("options string");
        run.setDataType("profile");
        run.setInstrumentModel("ETD");
        run.setInstrumentVendor("Thermo");
        run.setSha1Sum("sha1sum");
        return run;
    }
    
    
    private IMsRun createRun(int msExperimentId) {
        IMsRun run = new MsRun();
        run.setMsExperimentId(msExperimentId);
        run.setFileFormat(MsRun.RunFileFormat.MS2.toString());
        run.setFileName("my_file1.ms2");
        run.setAcquisitionMethod("Data dependent");
        run.setComment("Dummy run");
        run.setConversionSW("ms2Convert");
        run.setConversionSWVersion("1.0");
        run.setConversionSWOptions("options string");
        run.setDataType("profile");
        run.setInstrumentModel("ETD");
        run.setInstrumentVendor("Thermo");
        run.setSha1Sum("sha1sum");
        return run;
    }
}
