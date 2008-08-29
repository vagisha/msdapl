/**
 * SQTSpectrumDataDAO.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sqtfile.ibatis;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanDb;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTSearchScanDbImpl;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchScanDAOImpl extends BaseSqlMapDAO implements SQTSearchScanDAO {

    public SQTSearchScanDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public SQTSearchScanDbImpl load(int runSearchId, int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("runSearchId", runSearchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return (SQTSearchScanDbImpl) queryForObject("SqtSpectrum.select", map);
    }
    
    public void save(SQTSearchScan scanData, int searchId, int scanId) {
        SQTSearchScanSqlMapParam scanDataDb = new SQTSearchScanSqlMapParam(searchId, scanId, scanData);
        save("SqtSpectrum.insert", scanDataDb);
    }
    
    public void deleteForRunSearch(int runSearchId) {
        delete("SqtSpectrum.deleteForRunSearch", runSearchId);
    }

    public static final class SQTSearchScanSqlMapParam implements SQTSearchScanDb {

        private int runSearchId;
        private int scanId;
        private SQTSearchScan scan;
        
        public SQTSearchScanSqlMapParam(int runSearchId, int scanId, SQTSearchScan scan) {
            this.runSearchId = runSearchId;
            this.scanId = scanId;
            this.scan = scan;
        }

        public int getRunSearchId() {
            return runSearchId;
        }

        public int getScanId() {
            return scanId;
        }

        public int getCharge() {
            return scan.getCharge();
        }

        public BigDecimal getLowestSp() {
            return scan.getLowestSp();
        }

        public int getProcessTime() {
            return scan.getProcessTime();
        }

        public String getServerName() {
            return scan.getServerName();
        }

        public BigDecimal getTotalIntensity() {
            return scan.getTotalIntensity();
        }

        public List<SequestSearchResult> getScanResults() {
            throw new UnsupportedOperationException("getScanResults is not supported by SQTSearchScanSqlMapParam");
        }

        @Override
        public int getSequenceMatches() {
            return scan.getSequenceMatches();
        }

        @Override
        public BigDecimal getObservedMass() {
            return scan.getObservedMass();
        }
    }
}
