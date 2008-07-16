package org.yeastrc.ms.dao;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.MsEnzyme;
import org.yeastrc.ms.domain.MsEnzymeDb;
import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsRunDb;
import org.yeastrc.ms.domain.RunFileFormat;

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
    
    public void testSaveAndLoad() {
        MsRun run = createDefaultRun();
        int runId = runDao.saveRun(run, msExperimentId_1);
        MsRunDb runDb = runDao.loadRun(runId);
        checkRun(run, runDb);
    }
    
    public void testDeleteRunsForExperiment() {
        runDao.saveRun(createDefaultRun(), msExperimentId_1);
        runDao.saveRun(createDefaultRun(), msExperimentId_1);
        runDao.saveRun(createDefaultRun(), msExperimentId_2); // different experiment
        
        int origSize = runDao.loadRunIdsForExperiment(msExperimentId_1).size();
        assertTrue(origSize == 2);
        
        // delete experiment 2 runs
        runDao.deleteRunsForExperiment(msExperimentId_2); 
        assertEquals(origSize, runDao.loadRunIdsForExperiment(msExperimentId_1).size());
        
        runDao.deleteRunsForExperiment(msExperimentId_1); 
        assertEquals(0, runDao.loadRunIdsForExperiment(msExperimentId_1).size());
    }
    
    public void testSaveWithInvalidExpId() {
        MsRun run1 = createDefaultRun();
        try {
            runDao.saveRun(run1, 0);// we should not be able to save a run with experimentId == 0
            fail("Should not be able to save run with experimentId of 0");
        }
        catch(RuntimeException e) {}
    }
    
    public void testSaveAndLoadRunFileFormats() {
        MsRun run = createRunForFormat(RunFileFormat.MS2);
        int runId = runDao.saveRun(run, msExperimentId_1);
        MsRunDb runDb = runDao.loadRun(runId);
        assertEquals(RunFileFormat.MS2, runDb.getRunFileFormat());
        
        run = createRunForFormat(RunFileFormat.UNKNOWN);
        runId = runDao.saveRun(run, msExperimentId_1);
        runDb = runDao.loadRun(runId);
        assertEquals(RunFileFormat.UNKNOWN, runDb.getRunFileFormat());
        
        run = createRunForFormat(null);
        runId = runDao.saveRun(run, msExperimentId_1);
        runDb = runDao.loadRun(runId);
        assertEquals(RunFileFormat.UNKNOWN, runDb.getRunFileFormat());
    }
    
    public void testLoadRunsForExperiment() {
        MsRun run = createDefaultRun();
        runDao.saveRun(run, msExperimentId_1);
        runDao.saveRun(createDefaultRun(), msExperimentId_2);
        List <MsRunDb> runs = runDao.loadExperimentRuns(msExperimentId_1);
        assertEquals(1, runs.size());
        assertEquals(1, runDao.loadExperimentRuns(msExperimentId_2).size());
        checkRun(run, runs.get(0));
    }
   

    public void testLoadRunsForFileNameAndSha1Sum() {
        MsRun run = createDefaultRun();
        runDao.saveRun(run, msExperimentId_1);
        run = createDefaultRun();
        runDao.saveRun(run, msExperimentId_2);
        
        List<Integer> runs = runDao.runIdsFor(run.getFileName(), run.getSha1Sum());
        assertEquals(2, runs.size());
    }
    
    public void testSaveAndLoadRunWithNoEnzymes() {
        // create a run and save it
        int runId = runDao.saveRun(createDefaultRun(), msExperimentId_1);
        
        // read back the run
        MsRunDb dbRun = runDao.loadRun(runId);
        assertEquals(0, dbRun.getEnzymeList().size());
    }
    
    public void testSaveAndLoadRunWithEnzymeInfo() {
        
        // load some enzymes from the database
        MsEnzymeDb enzyme1 = enzymeDao.loadEnzyme(1);
        MsEnzymeDb enzyme2 = enzymeDao.loadEnzyme(2);
        MsEnzymeDb enzyme3 = enzymeDao.loadEnzyme(3);
        
        assertNotNull(enzyme1);
        assertNotNull(enzyme2);
        assertNotNull(enzyme3);
        
        // create a run with enzyme information
        List <MsEnzyme> enzymeList1 = new ArrayList<MsEnzyme>(2);
        enzymeList1.add(enzyme1);
        enzymeList1.add(enzyme2);
        MsRun run1 = createRunWEnzymeInfo(enzymeList1);
        
        // save the run
        int runId_1 = runDao.saveRun(run1, msExperimentId_1);
        
        // now read back the run and make sure it has the enzyme information
        MsRunDb runFromDb_1 = runDao.loadRun(runId_1);
        List<MsEnzymeDb> enzymes = runFromDb_1.getEnzymeList();
        assertNotNull(enzymes);
        assertEquals(2, enzymes.size());
        
        // save another run for this experiment
        List <MsEnzyme> enzymeList2 = new ArrayList<MsEnzyme>(1);
        enzymeList2.add(enzyme3);
        MsRun run2 = createRunWEnzymeInfo(enzymeList2);
        
        // save the run
        int runId_2 = runDao.saveRun(run2, msExperimentId_1);
        
        // now read back the run and make sure it has the enzyme information
        MsRunDb runFromDb_2 = runDao.loadRun(runId_2);
        enzymes = runFromDb_2.getEnzymeList();
        assertNotNull(enzymes);
        assertEquals(1, enzymes.size());
        checkEnzyme(enzyme3, enzymes.get(0));
        
    }
    

    public void testSaveAndDeleteRunsWithEnzymeInfoAndScans() {
        
        // load some enzymes from the database
        MsEnzymeDb enzyme1 = enzymeDao.loadEnzyme(1);
        MsEnzymeDb enzyme2 = enzymeDao.loadEnzyme(2);
        MsEnzymeDb enzyme3 = enzymeDao.loadEnzyme(3);
        
        assertNotNull(enzyme1);
        assertNotNull(enzyme2);
        assertNotNull(enzyme3);
        
        
        // create a run with enzyme information
        List <MsEnzyme> enzymeList1 = new ArrayList<MsEnzyme>(2);
        enzymeList1.add(enzyme1);
        enzymeList1.add(enzyme2);
        MsRun run1 = createRunWEnzymeInfo(enzymeList1);
        
        // save the run
        int runId_1 = runDao.saveRun(run1, msExperimentId_1);
        
        // now read back the run and make sure it has the enzyme information
        MsRunDb runFromDb_1 = runDao.loadRun(runId_1);
        List<MsEnzymeDb> enzymes = runFromDb_1.getEnzymeList();
        assertNotNull(enzymes);
        assertEquals(2, enzymes.size());
        
        
        // save another run for ANOTHER experiment
        List <MsEnzyme> enzymeList2 = new ArrayList<MsEnzyme>(1);
        enzymeList2.add(enzyme3);
        MsRun run2 = createRunWEnzymeInfo(enzymeList2);
        
        // save the run
        int runId_2 = runDao.saveRun(run2, msExperimentId_2);
        
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

    
    
    public static class MsRunTest implements MsRun {

        
        private String sha1Sum;
        private RunFileFormat runFileFormat;
        private String instrumentVendor;
        private String instrumentSN;
        private String instrumentModel;
        private String fileName;
        private List<MsEnzyme> enzymeList = new ArrayList<MsEnzyme>();
        private String dataType;
        private String creationDate;
        private String conversionSWVersion;
        private String conversionSWOptions;
        private String conversionSW;
        private String comment;
        private String aquisitionMethod;

        public void setSha1Sum(String sha1Sum) {
            this.sha1Sum = sha1Sum;
        }

        public void setRunFileFormat(RunFileFormat runFileFormat) {
            this.runFileFormat = runFileFormat;
        }

        public void setInstrumentVendor(String instrumentVendor) {
            this.instrumentVendor = instrumentVendor;
        }

        public void setInstrumentSN(String instrumentSN) {
            this.instrumentSN = instrumentSN;
        }

        public void setInstrumentModel(String instrumentModel) {
            this.instrumentModel = instrumentModel;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public void setEnzymeList(List<MsEnzyme> enzymeList) {
            this.enzymeList = enzymeList;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public void setCreationDate(String creationDate) {
            this.creationDate = creationDate;
        }

        public void setConversionSWVersion(String conversionSWVersion) {
            this.conversionSWVersion = conversionSWVersion;
        }

        public void setConversionSWOptions(String conversionSWOptions) {
            this.conversionSWOptions = conversionSWOptions;
        }

        public void setConversionSW(String conversionSW) {
            this.conversionSW = conversionSW;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public void setAcquisitionMethod(String squisitionMethod) {
            this.aquisitionMethod = squisitionMethod;
        }

        public String getAcquisitionMethod() {
            return this.aquisitionMethod;
        }

        public String getComment() {
            return this.comment;
        }

        public String getConversionSW() {
            return this.conversionSW;
        }

        public String getConversionSWOptions() {
            return this.conversionSWOptions;
        }

        public String getConversionSWVersion() {
            return this.conversionSWVersion;
        }

        public String getCreationDate() {
            return this.creationDate;
        }

        public String getDataType() {
            return this.dataType;
        }

        public List<MsEnzyme> getEnzymeList() {
            return this.enzymeList;
        }

        public String getFileName() {
            return this.fileName;
        }

        public String getInstrumentModel() {
            return this.instrumentModel;
        }

        public String getInstrumentSN() {
            return this.instrumentSN;
        }

        public String getInstrumentVendor() {
            return this.instrumentVendor;
        }

        public RunFileFormat getRunFileFormat() {
            return this.runFileFormat;
        }

        public String getSha1Sum() {
            return this.sha1Sum;
        }
        
    }
}
