package org.yeastrc.ms.dao;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.yeastrc.ms.dto.MsRun;
import org.yeastrc.ms.dto.MsScan;

public class MsScanDAOImplTest extends TestCase {

    private MsScanDAO scanDao;
    private MsRunDAO runDao;
    
    protected void setUp() throws Exception {
        super.setUp();
        scanDao = DAOFactory.instance().getMsScanDAO();
        runDao = DAOFactory.instance().getMsRunDAO();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testLoadScanIdsForRun() {
        // insert a run
        MsRun run = new MsRun();
        run.setMsExperimentId(1124);
        int runId = runDao.saveRun(run);
        assertTrue(runId > 0);
        
        // insert some scans for the run
        Random random = new Random();
        int[] scanIds = new int[10];
        for (int i = 0; i < 10; i++) {
            int scanNum = random.nextInt(100);
            MsScan scan = new MsScan();
            scan.setRunId(runId);
            scan.setStartScanNum(scanNum);
            scanIds[i] = scanDao.save(scan);
        }
        
        // make sure we get the correct scanIds
        List<Integer> scanIdList = scanDao.loadScanIdsForRun(runId);
        Collections.sort(scanIdList);
        assertEquals(scanIds.length, scanIdList.size());
        for(int i = 0; i < 10; i++)
            assertEquals(scanIds[i], scanIdList.get(i).intValue());
    }

}
