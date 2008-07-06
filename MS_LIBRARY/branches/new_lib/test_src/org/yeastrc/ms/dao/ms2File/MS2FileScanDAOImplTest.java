package org.yeastrc.ms.dao.ms2File;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.yeastrc.ms.dto.ms2File.MS2FileScan;


public class MS2FileScanDAOImplTest extends MS2BaseDAOtestCase {

    private final int runId = 35;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        
        // clean up
        ms2ScanDao.deleteScansForRun(runId);
        assertEquals(0, ms2ScanDao.loadScanIdsForRun(runId).size());
    }

    public void testOperationsOnMS2FileScan() {
        
        // there should not be anything in the database right now
        assertEquals(0, ms2ScanDao.loadScanIdsForRun(1).size());
        
        // put some scans in the database
        Random random = new Random();
        int[] scanIds = new int[10];
        for (int i = 0; i < 10; i++) {
            int scanNum = random.nextInt(100);
            MS2FileScan scan = makeMS2FileScan(runId, scanNum, false, false);
            scanIds[i] = ms2ScanDao.save(scan);
        }
        
        assertEquals(10, ms2ScanDao.loadScanIdsForRun(runId).size());

        // make sure we get the correct scanIds
        List<Integer> scanIdList = ms2ScanDao.loadScanIdsForRun(runId);
        Collections.sort(scanIdList);
        assertEquals(scanIds.length, scanIdList.size());
        for(int i = 0; i < 10; i++)
            assertEquals(scanIds[i], scanIdList.get(i).intValue());

        // get the scan for the first scan id and make sure it does NOT have any 
        // charge dependent analysis or scan charges associated with it
        MS2FileScan scan = ms2ScanDao.load(scanIds[0]);
        assertNotNull(scan);
        assertEquals(0, scan.getChargeIndependentAnalysisList().size());
        assertEquals(0, scan.getScanChargeList().size());
        
        // save a scan WITH both charge independent analysis and scan charges
        scan = makeMS2FileScan(runId, 25, true, true);
        int scanId = ms2ScanDao.save(scan);
        MS2FileScan scan_db = ms2ScanDao.load(scanId);
        assertEquals(3, scan_db.getChargeIndependentAnalysisList().size());
        assertEquals(2, scan_db.getScanChargeList().size());
        
        // delete the scan and make sure everything got deleted
        ms2ScanDao.deleteScansForRun(runId);
        assertNull(ms2ScanDao.load(scanId));
        assertEquals(0, chargeDao.loadScanChargesForScan(scanId).size());
        assertEquals(0, iAnalDao.loadAnalysisForScan(scanId).size());
        
    }
    
    protected MS2FileScan makeMS2FileScan(int runId, int startScanNum, boolean addScanCharges, boolean addChgIAnalysis) {
        MS2FileScan scan = new MS2FileScan(makeMsScan(runId, startScanNum));
        if (addScanCharges) {
            scan.addScanCharge(makeMS2ScanCharge(2, "100.0", true));
            scan.addScanCharge(makeMS2ScanCharge(3, "200.0", true));
        }
        if (addChgIAnalysis) {
            scan.addChargeIndependentAnalysis(makeIAnalysis("name_1", "value_1"));
            scan.addChargeIndependentAnalysis(makeIAnalysis("name_2", "value_2"));
            scan.addChargeIndependentAnalysis(makeIAnalysis("name_3", "value_3"));
        }
        return scan;
    }
}
