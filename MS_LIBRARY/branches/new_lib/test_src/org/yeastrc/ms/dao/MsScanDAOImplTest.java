package org.yeastrc.ms.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.ms.dao.run.ibatis.MsScanDAOImpl.DataConversionTypeHandler;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;

public class MsScanDAOImplTest extends BaseDAOTestCase {


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSaveLoadDelete() {
        MsScan scan = makeMsScan(2, 1, DataConversionType.CENTROID); // scanNumber = 2; precursorScanNum = 1;
        int scanId = scanDao.save(scan, 1, 1); // runId = 1; precursorScanId = 1;
        MsScanDb scanDb = scanDao.load(scanId);
        checkScan(scan, scanDb);
        // clean up
        scanDao.deleteScansForRun(1);
        assertNull(scanDao.load(scanId));
    }


    public void testInvalidValues() {
        MsScan scan = makeMsScan(2, 1, DataConversionType.CENTROID); // scanNumber = 2; precursorScanNum = 1;
        try {
            scanDao.save(scan, 0, 1); // runId = 0; precursorScanId  1;
            fail("RunId cannot be 0");
        }
        catch(RuntimeException e){}
    }

    public void testSaveScanWithNoPrecursorScanId() {
        MsScan scan = makeMsScan(2, 1,DataConversionType.CENTROID); // scanNumber = 2; precursorScanNum = 1;
        int scanId = scanDao.save(scan, 1); // runID = 1
        MsScanDb scanDb = scanDao.load(scanId);
        checkScan(scan, scanDb);
        // clean up
        scanDao.deleteScansForRun(1);
        assertNull(scanDao.load(scanId));
    }

    public void testLoadScanIdsForRun() {
        int[] ids = new int[3];
        ids[0] = scanDao.save((makeMsScan(2, 1,DataConversionType.CENTROID)), 3); // runId = 3
        ids[1] = scanDao.save((makeMsScan(3, 1,DataConversionType.CENTROID)), 3);
        ids[2] = scanDao.save((makeMsScan(4, 1,DataConversionType.CENTROID)), 3);
        
        List<Integer> scanIdList = scanDao.loadScanIdsForRun(3);
        Collections.sort(scanIdList);
        assertEquals(3, scanIdList.size());
        for (int i = 0; i < ids.length; i++) {
            assertEquals(Integer.valueOf(ids[i]), scanIdList.get(i));
        }
        // clean up
        scanDao.deleteScansForRun(3);
        assertEquals(0, scanDao.loadScanIdsForRun(3).size());
    }
    
    public void testSaveLoadPeakData() {
        MsScan scan = makeMsScanWithPeakData(2, 1,DataConversionType.CENTROID); // scanNumber = 2; precursorScanNum = 1;
        
        int scanId = scanDao.save(scan, 1, 1); // runId = 1; precursorScanId = 1;
        MsScanDb scanDb = scanDao.load(scanId);
        checkScan(scan, scanDb);
        // clean up
        scanDao.deleteScansForRun(1);
        assertNull(scanDao.load(scanId));
    }

    public void testDataConversionTypeForScan() {
        MsScan scan = makeMsScan(35, 53, null);
        try {
            scanDao.save(scan, 56);
            fail("DataConversionType cannot be null");
        }
        catch(Exception e) {}
        
        scan = makeMsScan(35, 53, DataConversionType.CENTROID);
        int id = scanDao.save(scan, 56);
        MsScanDb scan_db = scanDao.load(id);
        checkScan(scan, scan_db);
        
        scan = makeMsScan(36, 35, DataConversionType.NON_CENTROID);
        id = scanDao.save(scan, 56);
        scan_db = scanDao.load(id);
        checkScan(scan, scan_db);
        
        scan = makeMsScan(37, 35, DataConversionType.UNKNOWN);
        id = scanDao.save(scan, 56);
        scan_db = scanDao.load(id);
        checkScan(scan, scan_db);
        
        // clean up
        scanDao.deleteScansForRun(56);
        assertEquals(0, scanDao.loadScanIdsForRun(56).size());
    }
    
    public void testPeakCount() {
        MsScan scan = makeMsScanWithPeakData(35, 53, DataConversionType.CENTROID);
        assertTrue(scan.getPeakCount() > 0);
        int scanId = scanDao.save(scan, 27);
        MsScanDb scan_db = scanDao.load(scanId);
        checkScan(scan, scan_db);
        assertEquals(scan.getPeakCount(), scan_db.getPeakCount());
        scanDao.delete(scanId);
        assertNull(scanDao.load(scanId));
    }
    
    public static class MsScanTest implements MsScan {

        private int startScanNum;
        private BigDecimal retentionTime;
        private int precursorScanNum;
        private BigDecimal precursorMz;
        private int msLevel;
        private String fragmentationType;
        private int endScanNum;
        private List<String[]> peakList = new ArrayList<String[]>();
        private DataConversionType convType;

        public int getEndScanNum() {
            return this.endScanNum;
        }

        public String getFragmentationType() {
            return this.fragmentationType;
        }

        public int getMsLevel() {
            return this.msLevel;
        }

        public Iterator<String[]> peakIterator() {
            return this.peakList.iterator();
        }

        public BigDecimal getPrecursorMz() {
            return this.precursorMz;
        }

        public int getPrecursorScanNum() {
            return this.precursorScanNum;
        }

        public BigDecimal getRetentionTime() {
            return this.retentionTime;
        }

        public int getStartScanNum() {
            return this.startScanNum;
        }

        public void setRetentionTime(BigDecimal retentionTime) {
            this.retentionTime = retentionTime;
        }

        public void setPrecursorScanNum(int precursorScanNum) {
            this.precursorScanNum = precursorScanNum;
        }

        public void setPrecursorMz(BigDecimal precursorMz) {
            this.precursorMz = precursorMz;
        }

        public void setPeaks(List<String[]> peaks) {
            this.peakList = peaks;
        }

        public int getPeakCount() {
            return peakList.size();
        }
        
        public void setMsLevel(int msLevel) {
            this.msLevel = msLevel;
        }

        public void setFragmentationType(String fragmentationType) {
            this.fragmentationType = fragmentationType;
        }

        public void setEndScanNum(int endScanNum) {
            this.endScanNum = endScanNum;
        }

        public void setStartScanNum(int scanNum) {
            this.startScanNum = scanNum;
        }

        @Override
        public DataConversionType getDataConversionType() {
            return convType;
        }
        
        public void setDataConversionType(DataConversionType convType) {
            this.convType = convType;
        }
    }
}
