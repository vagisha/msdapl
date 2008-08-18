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
import org.yeastrc.ms.domain.search.sequest.SequestRunSearchResult;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTSearchScanDbImpl;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchScanDAOImpl extends BaseSqlMapDAO implements SQTSearchScanDAO {

    public SQTSearchScanDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public SQTSearchScanDbImpl load(int searchId, int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("searchId", searchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return (SQTSearchScanDbImpl) queryForObject("SqtSpectrum.select", map);
    }
    
    public void save(SQTSearchScan scanData, int searchId, int scanId) {
        SQTSearchScanSqlMapParam scanDataDb = new SQTSearchScanSqlMapParam(searchId, scanId, scanData);
        save("SqtSpectrum.insert", scanDataDb);
    }
    
    public void deleteForSearch(int searchId) {
        delete("SqtSpectrum.deleteForSearch", searchId);
    }

    public static final class SQTSearchScanSqlMapParam implements SQTSearchScan {

        private int searchId;
        private int scanId;
        private SQTSearchScan scan;
        
        public SQTSearchScanSqlMapParam(int searchId, int scanId, SQTSearchScan scan) {
            this.searchId = searchId;
            this.scanId = scanId;
            this.scan = scan;
        }

        public int getSearchId() {
            return searchId;
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

        public List<SequestRunSearchResult> getScanResults() {
            throw new UnsupportedOperationException("getScanResults is not supported by SQTSearchScanSqlMapParam");
        }

        @Override
        public int getScanNumber() {
            return scan.getScanNumber();
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
