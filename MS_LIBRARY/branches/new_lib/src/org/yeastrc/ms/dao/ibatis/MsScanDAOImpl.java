/**
 * MsScanDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;
import org.yeastrc.ms.util.PeakStringBuilder;
import org.yeastrc.ms.util.PeakUtils;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsScanDAOImpl extends BaseSqlMapDAO implements MsScanDAO<MsScan, MsScanDb> {

    public MsScanDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int save(MsScan scan, int runId, int precursorScanId) {
        MsScanSqlMapParam scanDb = new MsScanSqlMapParam(runId, precursorScanId, scan);
        return saveAndReturnId("MsScan.insert", scanDb);
    }

    public int save(MsScan scan, int runId) {
        return save(scan, runId, 0); // a value of 0 for precursorScanId should insert NULL in the database.
    }
    
    public MsScanDb load(int scanId) {
        return (MsScanDb) queryForObject("MsScan.select", scanId);
    }

    public List<Integer> loadScanIdsForRun(int runId) {
        return queryForList("MsScan.selectScanIdsForRun", runId);
    }

    public int loadScanIdForScanNumRun(int scanNum, int runId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("scanNum", scanNum);
        map.put("runId", runId);
        Integer id = (Integer)queryForObject("MsScan.selectScanIdForScanNumRun", map);
        if (id != null) return id;
        return 0;
    }
    
    public void delete(int scanId) {
        delete("MsScan.delete", scanId);
    }

    public void deleteScansForRun(int runId) {
        delete("MsScan.deleteByRunId", runId);
    }

    /**
     * Convenience class for encapsulating a MsScan along with the associated runId 
     * and precursorScanId (if any)
     */
    public class MsScanSqlMapParam implements MsScan {

        private int runId;
        private int precursorScanId;
        private MsScan scan;

        public MsScanSqlMapParam(int runId, int precursorScanId, MsScan scan) {
            this.runId = runId;
            this.precursorScanId = precursorScanId;
            this.scan = scan;
        }

        /**
         * @return the runId
         */
        public int getRunId() {
            return runId;
        }

        public byte[] getPeakByteArray() throws IOException {
            Iterator<String[]> peakIterator = scan.peakIterator();
            String[] peak = null;
            PeakStringBuilder builder = new PeakStringBuilder();
            while(peakIterator.hasNext()) {
                peak = peakIterator.next();
                builder.addPeak(peak[0], peak[1]);
            }
            return PeakUtils.encodePeakString(builder.getPeaksAsString());
        }
        
        public int getPrecursorScanId() {
            return precursorScanId;
        }

        public int getEndScanNum() {
            return scan.getEndScanNum();
        }

        public String getFragmentationType() {
            return scan.getFragmentationType();
        }

        public int getMsLevel() {
            return scan.getMsLevel();
        }

        public BigDecimal getPrecursorMz() {
            return scan.getPrecursorMz();
        }

        public int getPrecursorScanNum() {
            return scan.getPrecursorScanNum();
        }

        public BigDecimal getRetentionTime() {
            return scan.getRetentionTime();
        }

        public int getStartScanNum() {
            return scan.getStartScanNum();
        }

        public Iterator<String[]> peakIterator() {
            return scan.peakIterator();
        }
    }
}
