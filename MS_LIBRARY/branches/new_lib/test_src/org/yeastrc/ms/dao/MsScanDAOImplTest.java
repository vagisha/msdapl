package org.yeastrc.ms.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.ms.domain.DataConversionType;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;

public class MsScanDAOImplTest extends BaseDAOTestCase {


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSaveLoadDelete() {
        MsScan scan = makeMsScan(2, 1); // scanNumber = 2; precursorScanNum = 1;
        int scanId = scanDao.save(scan, 1, 1); // runId = 1; precursorScanId = 1;
        MsScanDb scanDb = scanDao.load(scanId);
        checkScan(scan, scanDb);
        // clean up
        scanDao.deleteScansForRun(1);
        assertNull(scanDao.load(scanId));
    }


    public void testInvalidValues() {
        MsScan scan = makeMsScan(2, 1); // scanNumber = 2; precursorScanNum = 1;
        try {
            scanDao.save(scan, 0, 1); // runId = 0; precursorScanId  1;
            fail("RunId cannot be 0");
        }
        catch(RuntimeException e){}
    }

    public void testSaveScanWithNoPrecursorScanId() {
        MsScan scan = makeMsScan(2, 1); // scanNumber = 2; precursorScanNum = 1;
        int scanId = scanDao.save(scan, 1); // runID = 1
        MsScanDb scanDb = scanDao.load(scanId);
        checkScan(scan, scanDb);
        // clean up
        scanDao.deleteScansForRun(1);
        assertNull(scanDao.load(scanId));
    }

    public void testLoadScanIdsForRun() {
        int[] ids = new int[3];
        ids[0] = scanDao.save((makeMsScan(2, 1)), 3); // runId = 3
        ids[1] = scanDao.save((makeMsScan(3, 1)), 3);
        ids[2] = scanDao.save((makeMsScan(4, 1)), 3);
        
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
        MsScan scan = makeMsScanWithPeakData(2, 1); // scanNumber = 2; precursorScanNum = 1;
        
        int scanId = scanDao.save(scan, 1, 1); // runId = 1; precursorScanId = 1;
        MsScanDb scanDb = scanDao.load(scanId);
        checkScan(scan, scanDb);
        // clean up
        scanDao.deleteScansForRun(1);
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
