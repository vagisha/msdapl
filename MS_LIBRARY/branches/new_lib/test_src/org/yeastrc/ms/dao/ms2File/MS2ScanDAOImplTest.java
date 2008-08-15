package org.yeastrc.ms.dao.ms2File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.yeastrc.ms.dao.MsScanDAOImplTest.MsScanTest;
import org.yeastrc.ms.domain.DataConversionType;
import org.yeastrc.ms.domain.ms2File.MS2Field;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.domain.ms2File.MS2ScanCharge;
import org.yeastrc.ms.domain.ms2File.MS2ScanDb;


public class MS2ScanDAOImplTest extends MS2BaseDAOtestCase {

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
            MS2Scan scan = makeMS2Scan(scanNum, 0, DataConversionType.CENTROID, false, false); // precursorScanNum = 0;
            scanIds[i] = ms2ScanDao.save(scan, runId);
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
        MS2ScanDb scanDb = ms2ScanDao.load(scanIds[0]);
        assertNotNull(scanDb);
        assertEquals(0, scanDb.getChargeIndependentAnalysisList().size());
        assertEquals(0, scanDb.getScanChargeList().size());
        
        // save a scan WITH both charge independent analysis and scan charges
        MS2Scan scan = makeMS2Scan(420, 0, DataConversionType.CENTROID, true, true);// scanNum = 420; precursorScanNum = 0s
        int scanId = ms2ScanDao.save(scan, runId);
        MS2ScanDb scan_db = ms2ScanDao.load(scanId);
        assertEquals(DataConversionType.CENTROID, scan_db.getDataConversionType());
        assertEquals(3, scan_db.getChargeIndependentAnalysisList().size());
        assertEquals(2, scan_db.getScanChargeList().size());
        
        // delete the scan and make sure everything got deleted
        ms2ScanDao.deleteScansForRun(runId);
        assertNull(ms2ScanDao.load(scanId));
        assertEquals(0, chargeDao.loadScanChargesForScan(scanId).size());
        assertEquals(0, iAnalDao.loadAnalysisForScan(scanId).size());
        
    }
    
    public void testDataConversionTypeForScan() {
        MS2Scan scan1 = makeMS2Scan(23, 32, null, false, false);
        try {
            ms2ScanDao.save(scan1, 56);
            fail("DataConversionTupe cannot be null");
        }
        catch(IllegalArgumentException e) {e.printStackTrace();}
        
        int id1 = ms2ScanDao.save(scan1, 56);
        MS2ScanDb scan1_db = ms2ScanDao.load(id1);
        super.checkScan(scan1, scan1_db);
    }
    public static final class MS2ScanTest extends MsScanTest implements MS2Scan {

        private List<MS2Field> analysisList = new ArrayList<MS2Field>();
        private List<MS2ScanCharge> scanChargeList = new ArrayList<MS2ScanCharge>();
        
        public List<MS2Field> getChargeIndependentAnalysisList() {
            return analysisList;
        }

        public List<MS2ScanCharge> getScanChargeList() {
            return scanChargeList;
        }

        public void addScanCharge(MS2ScanCharge scanCharge) {
            scanChargeList.add(scanCharge);
        }

        public void addChargeIndependentAnalysis(MS2Field analysis) {
            analysisList.add(analysis);
        }
    }
}
