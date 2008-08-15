package org.yeastrc.ms.dao.ms2File;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.MsLibTests;
import org.yeastrc.ms.dao.MsRunDAOImplTest.MsRunTest;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Field;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;
import org.yeastrc.ms.domain.run.ms2file.MS2RunDb;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2HeaderDbImpl;

public class MS2RunDAOImplTest extends MS2BaseDAOtestCase {

    protected void setUp() throws Exception {
        super.setUp();
        MsLibTests.resetDatabase();
        MsLibTests.addEnzymes();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testSaveLoadAndDelete() {
        MS2Run run = makeMS2Run("MyFile1", true, true); // run with enzyme info and headers
        
        assertTrue(run.getHeaderList().size() == 3);
        assertTrue(run.getEnzymeList().size() == 3);
        
        
        int runId = ms2RunDao.saveRun(run, "remoteServer", "remoteDirectory"); // save the run
        
        saveScansForRun(runId, 20); // add scans for this run
        
        
        MS2RunDb run_db = ms2RunDao.loadRun(runId);
        checkRun(run, run_db);
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
    
    private MS2Run makeMS2Run(String fileName, boolean addEnzymes, boolean addHeaders) {
        
        MS2RunTest run = super.makeMS2Run(fileName);
        if (addEnzymes) {
            // load some enzymes from the database
            MsEnzymeDb enzyme1 = enzymeDao.loadEnzyme(1);
            MsEnzymeDb enzyme2 = enzymeDao.loadEnzyme(2);
            MsEnzymeDb enzyme3 = enzymeDao.loadEnzyme(3);
            
            assertNotNull(enzyme1);
            assertNotNull(enzyme2);
            assertNotNull(enzyme3);
            List<MsEnzyme> enzymes = new ArrayList<MsEnzyme>(3);
            enzymes.add(enzyme1);
            enzymes.add(enzyme2);
            enzymes.add(enzyme3);
            
            run.setEnzymeList(enzymes);
        }
       
        if (addHeaders) {
            run.addHeader(makeMS2Header("name1", "value1"));
            run.addHeader(makeMS2Header("name2", "value2"));
            run.addHeader(makeMS2Header("name3", "value3"));
        }
        return run;
    }
    
    private MS2HeaderDbImpl makeMS2Header(String name, String value) {
        MS2HeaderDbImpl header = new MS2HeaderDbImpl();
        header.setName(name);
        header.setValue(value);
        return header;
    }
    
    public static final class MS2RunTest extends MsRunTest implements MS2Run {

        private List<MS2Field> headers = new ArrayList<MS2Field>();
        
        public List<MS2Field> getHeaderList() {
            return headers;
        }
        public void addHeader(MS2Field header) {
            headers.add(header);
        }
    }
    
}
