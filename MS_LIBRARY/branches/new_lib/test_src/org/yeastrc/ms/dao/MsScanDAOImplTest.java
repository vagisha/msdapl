package org.yeastrc.ms.dao;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.yeastrc.ms.domain.db.MsScan;

public class MsScanDAOImplTest extends BaseDAOTestCase {


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnMsScan() {

        // we don't have any scans in the database right now
        int runId = 12;
        assertEquals(0, scanDao.loadScanIdsForRun(runId).size());

        // insert some scans
        Random random = new Random();
        int[] scanIds = new int[10];
        for (int i = 0; i < 10; i++) {
            int scanNum = random.nextInt(100);
            MsScan scan = makeMsScan(runId, scanNum);
            scanIds[i] = scanDao.save(scan);
        }

        assertEquals(10, scanDao.loadScanIdsForRun(runId).size());

        // make sure we get the correct scanIds
        List<Integer> scanIdList = scanDao.loadScanIdsForRun(runId);
        Collections.sort(scanIdList);
        assertEquals(scanIds.length, scanIdList.size());
        for(int i = 0; i < 10; i++)
            assertEquals(scanIds[i], scanIdList.get(i).intValue());

        // clean up
        scanDao.deleteScansForRun(runId);
        assertEquals(0, scanDao.loadScanIdsForRun(runId).size());

    }

    public void testNullProperties() {

        // try to save a scan without a runId; Should fail since runID in msScan cannot be null
        MsScan scan = new MsScan();
        try {
            scanDao.save(scan);
            fail("Should not be able to save a scan withour a runId");
        }
        catch (RuntimeException e) {}

        // set the run id and try to save again
        scan.setRunId(24);
        int scanId = scanDao.save(scan);
        MsScan scan_db = scanDao.load(scanId);
        assertNotNull(scan_db);
        assertEquals(24, scan_db.getRunId());

        // make sure we have the expected values for all the properties we did NOT set
        assertEquals(-1, scan_db.getStartScanNum());
        assertEquals(-1, scan_db.getEndScanNum());
        assertEquals(0, scan_db.getMsLevel());
        assertNull(scan_db.getPrecursorMz());
        assertEquals(0, scan_db.getPrecursorScanId());
        assertNull(scan_db.getRetentionTime());
        assertNull(scan.getFragmentationType());
        assertEquals(0, scan.getPeaks().getPeaksCount());

        // clean up
        scanDao.deleteScansForRun(24);
        assertNull(scanDao.load(scanId));
    }

}
