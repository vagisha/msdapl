/**
 * Ms2FileChargeIndependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ms2File.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.domain.ms2File.MS2ChargeIndependentAnalysisDb;
import org.yeastrc.ms.domain.ms2File.MS2Field;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2ChargeIndependentAnalysisDAOImpl extends BaseSqlMapDAO
        implements MS2ChargeIndependentAnalysisDAO {

    public MS2ChargeIndependentAnalysisDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MS2ChargeIndependentAnalysisDb> loadAnalysisForScan(int scanId) {
        return queryForList("MS2ChgIAnalysis.selectAnalysisForScan", scanId);
    }

    public void save(MS2Field analysis, int scanId) {
        MS2ChgIndepAnSqlMapParam analysisDb = new MS2ChgIndepAnSqlMapParam(scanId, analysis.getName(), analysis.getValue());
        save("MS2ChgIAnalysis.insert", analysisDb);
    }

    public void deleteByScanId(int scanId) {
        delete("MS2ChgIAnalysis.deleteByScanId", scanId);
    }

    public static final class MS2ChgIndepAnSqlMapParam {
        private int scanId;
        private String name;
        private String value;
        public MS2ChgIndepAnSqlMapParam(int scanId, String name, String value) {
            this.scanId = scanId;
            this.name = name;
            this.value = value;
        }
        public int getScanId() {
            return scanId;
        }
        public String getName() {
            return name;
        }
        public String getValue() {
            return value;
        }
    }
}
