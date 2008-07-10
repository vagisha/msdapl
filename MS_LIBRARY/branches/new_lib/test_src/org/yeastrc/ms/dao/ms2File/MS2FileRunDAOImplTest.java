package org.yeastrc.ms.dao.ms2File;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.db.MsDigestionEnzyme;
import org.yeastrc.ms.domain.db.MsRun;
import org.yeastrc.ms.domain.ms2File.MS2FileHeader;
import org.yeastrc.ms.domain.ms2File.MS2FileRun;

public class MS2FileRunDAOImplTest extends MS2BaseDAOtestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testSaveLoadAndDelete() {
        
        MS2FileRun run = makeMS2Run(1, true, true); // run with enzyme info and headers
        
        assertTrue(run.getHeaderList().size() > 0);
        assertTrue(run.getEnzymeList().size() > 0);
        
        
        int runId = ms2RunDao.saveRun(run); // save the run
        saveScansForRun(runId, 20); // add scans for this run
        
        
        MS2FileRun run_db = ms2RunDao.loadRun(runId);
        assertEquals(run.getEnzymeList().size(), run_db.getEnzymeList().size());
        assertEquals(run.getHeaderList().size(), run_db.getHeaderList().size());
        assertEquals(run.getEnzymeList().size(), enzymeDao.loadEnzymesForRun(runId).size());
        assertEquals(run.getHeaderList().size(), ms2HeaderDao.loadHeadersForRun(runId).size());
        assertEquals(20, ms2ScanDao.loadScanIdsForRun(runId).size());
        
        // get the ids of the scans for this run
        List<Integer> scanIds = ms2ScanDao.loadScanIdsForRun(runId);
        for (Integer scanId: scanIds) {
            assertEquals(2, chargeDao.loadScanChargeIdsForScan(scanId).size());
            assertEquals(3, iAnalDao.loadAnalysisForScan(scanId).size());
        }
        
        // delete the run and make sure everything gets deleted
        ms2RunDao.delete(runId);
        assertNull(ms2RunDao.loadRun(runId));
        assertEquals(0, enzymeDao.loadEnzymesForRun(runId).size());
        assertEquals(0, ms2HeaderDao.loadHeadersForRun(runId).size());
        assertEquals(0, ms2ScanDao.loadScanIdsForRun(runId).size());
        for (Integer scanId: scanIds) {
            assertEquals(0, chargeDao.loadScanChargeIdsForScan(scanId).size());
            assertEquals(0, iAnalDao.loadAnalysisForScan(scanId).size());
        }
    }
    
    public void testLoadExperimentRuns() {
        // do we get a list of type List<MS2FileRun>?
        MS2FileRun run = makeMS2Run(1, true, true); // run with enzyme info and headers
        ms2RunDao.saveRun(run);
        run = makeMS2Run(1, true, true);
        ms2RunDao.saveRun(run);
        
        List<MS2FileRun> runList = ms2RunDao.loadExperimentRuns(1);
        assertEquals(2, runList.size());
        
        for (MS2FileRun r: runList) {
            assertEquals(3, r.getHeaderList().size());
        }
        
        ms2RunDao.deleteRunsForExperiment(1);
        runList = ms2RunDao.loadExperimentRuns(1);
        assertEquals(0, runList.size());
    }
    
    
    private MS2FileRun makeMS2Run(int msExperimentId, boolean addEnzymes, boolean addHeaders) {
        
        MS2FileRun run = null;
        if (addEnzymes) {
            // load some enzymes from the database
            MsDigestionEnzyme enzyme1 = enzymeDao.loadEnzyme(1);
            MsDigestionEnzyme enzyme2 = enzymeDao.loadEnzyme(2);
            MsDigestionEnzyme enzyme3 = enzymeDao.loadEnzyme(3);
            
            assertNotNull(enzyme1);
            assertNotNull(enzyme2);
            assertNotNull(enzyme3);
            List<MsDigestionEnzyme> enzymes = new ArrayList<MsDigestionEnzyme>(3);
            enzymes.add(enzyme1);
            enzymes.add(enzyme2);
            enzymes.add(enzyme3);
            
            MsRun msRun = createRunWEnzymeInfo(msExperimentId, enzymes);
            run = new MS2FileRun(msRun);
            checkRun(msRun, run);
        }
        else {
            run = new MS2FileRun(this.createRun(msExperimentId));
        }
        if (addHeaders) {
            run.addMS2Header(makeMS2Header("name1", "value1"));
            run.addMS2Header(makeMS2Header("name2", "value2"));
            run.addMS2Header(makeMS2Header("name3", "value3"));
        }
        return run;
    }
    
    private MS2FileHeader makeMS2Header(String name, String value) {
        MS2FileHeader header = new MS2FileHeader();
        header.setName(name);
        header.setValue(value);
        return header;
    }
    
}
