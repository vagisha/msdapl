package org.yeastrc.ms.dao.ms2File;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.yeastrc.ms.domain.ms2File.IMS2Scan;


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
            IMS2Scan scan = makeMS2FileScan(runId, scanNum, false, false);
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
        IMS2Scan scan = ms2ScanDao.load(scanIds[0]);
        assertNotNull(scan);
        assertEquals(0, scan.getChargeIndependentAnalysisList().size());
        assertEquals(0, scan.getScanChargeList().size());
        
        // save a scan WITH both charge independent analysis and scan charges
        scan = makeMS2FileScan(runId, 25, true, true);
        int scanId = ms2ScanDao.save(scan);
        IMS2Scan scan_db = ms2ScanDao.load(scanId);
        assertEquals(3, scan_db.getChargeIndependentAnalysisList().size());
        assertEquals(2, scan_db.getScanChargeList().size());
        
        // delete the scan and make sure everything got deleted
        ms2ScanDao.deleteScansForRun(runId);
        assertNull(ms2ScanDao.load(scanId));
        assertEquals(0, chargeDao.loadScanChargesForScan(scanId).size());
        assertEquals(0, iAnalDao.loadAnalysisForScan(scanId).size());
        
    }
}
