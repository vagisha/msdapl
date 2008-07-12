package org.yeastrc.ms.dao.ms2File;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.impl.MsDigestionEnzymeDb;
import org.yeastrc.ms.domain.impl.MsRunDbImpl;
import org.yeastrc.ms.domain.ms2File.MS2Run;
import org.yeastrc.ms.domain.ms2File.impl.MS2HeaderDbImpl;
import org.yeastrc.ms.domain.ms2File.impl.MS2RunDbImpl;

public class MS2FileRunDAOImplTest extends MS2BaseDAOtestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testSaveLoadAndDelete() {
        
        MS2RunDbImpl run = makeMS2Run(1, true, true); // run with enzyme info and headers
        
        assertTrue(run.getHeaderList().size() > 0);
        assertTrue(run.getEnzymeList().size() > 0);
        
        
        int runId = ms2RunDao.saveRun(run, 0); // save the run
        saveScansForRun(runId, 20); // add scans for this run
        
        
        MS2RunDbImpl run_db = ms2RunDao.loadRun(runId);
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
        MS2Run run = makeMS2Run(1, true, true); // run with enzyme info and headers
        ms2RunDao.saveRun(run, 0);
        run = makeMS2Run(1, true, true);
        ms2RunDao.saveRun(run, 0);
        
        List<MS2RunDbImpl> runList = ms2RunDao.loadExperimentRuns(1);
        assertEquals(2, runList.size());
        
        for (MS2Run r: runList) {
            assertEquals(3, r.getHeaderList().size());
        }
        
        ms2RunDao.deleteRunsForExperiment(1);
        runList = ms2RunDao.loadExperimentRuns(1);
        assertEquals(0, runList.size());
    }
    
    
    private MS2RunDbImpl makeMS2Run(int msExperimentId, boolean addEnzymes, boolean addHeaders) {
        
        MS2RunDbImpl run = null;
        if (addEnzymes) {
            // load some enzymes from the database
            MsDigestionEnzymeDb enzyme1 = enzymeDao.loadEnzyme(1);
            MsDigestionEnzymeDb enzyme2 = enzymeDao.loadEnzyme(2);
            MsDigestionEnzymeDb enzyme3 = enzymeDao.loadEnzyme(3);
            
            assertNotNull(enzyme1);
            assertNotNull(enzyme2);
            assertNotNull(enzyme3);
            List<MsDigestionEnzymeDb> enzymes = new ArrayList<MsDigestionEnzymeDb>(3);
            enzymes.add(enzyme1);
            enzymes.add(enzyme2);
            enzymes.add(enzyme3);
            
            MsRunDbImpl msRun = createRunWEnzymeInfo(msExperimentId, enzymes);
            run = new MS2RunDbImpl(msRun);
            checkRun(msRun, run);
        }
        else {
            run = new MS2RunDbImpl(this.createRun(msExperimentId));
        }
        if (addHeaders) {
            run.addMS2Header(makeMS2Header("name1", "value1"));
            run.addMS2Header(makeMS2Header("name2", "value2"));
            run.addMS2Header(makeMS2Header("name3", "value3"));
        }
        return run;
    }
    
    private MS2HeaderDbImpl makeMS2Header(String name, String value) {
        MS2HeaderDbImpl header = new MS2HeaderDbImpl();
        header.setName(name);
        header.setValue(value);
        return header;
    }
    
}
