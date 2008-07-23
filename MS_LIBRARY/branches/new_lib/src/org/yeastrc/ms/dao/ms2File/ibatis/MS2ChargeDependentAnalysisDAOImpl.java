/**
 * Ms2FileChargeDependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ms2File.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.domain.ms2File.MS2ChargeDependentAnalysisDb;
import org.yeastrc.ms.domain.ms2File.MS2Field;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2ChargeDependentAnalysisDAOImpl extends BaseSqlMapDAO
        implements MS2ChargeDependentAnalysisDAO {

    public MS2ChargeDependentAnalysisDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MS2ChargeDependentAnalysisDb> loadAnalysisForScanCharge(int scanChargeId) {
        return queryForList("MS2ChgDAnalysis.selectAnalysisForCharge", scanChargeId);
    }

    public void save(MS2Field analysis, int scanChargeId) {
        MS2ChgDepAnSqlMapParam analysisDb = new MS2ChgDepAnSqlMapParam(scanChargeId, analysis.getName(), analysis.getValue());
        save("MS2ChgDAnalysis.insert", analysisDb);
    }

    @Override
    public void saveAll(List<MS2ChargeDependentAnalysisDb> analysisList) {
        if (analysisList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MS2ChargeDependentAnalysisDb analysis: analysisList) {
            values.append("(");
            values.append(analysis.getScanChargeId());
            values.append(",");
            String name = analysis.getName();
            if (name != null)   values.append("\"");
            values.append(name);
            if (name != null)   values.append("\"");
            values.append(",");
            String val = analysis.getValue();
            if (val != null)   values.append("\"");
            values.append(val);
            if (val != null)   values.append("\"");
            values.append("),");
        }
        values.deleteCharAt(values.length() - 1);
        
        save("MS2ChgDAnalysis.insertAll", values.toString());
    }
    
    public void deleteByScanChargeId(int scanChargeId) {
        delete("MS2ChgDAnalysis.deleteByScanChargeId", scanChargeId);
    }

    public static final class MS2ChgDepAnSqlMapParam {
        private int scanChargeId;
        private String name;
        private String value;
        public MS2ChgDepAnSqlMapParam(int scanChargeId, String name, String value) {
            this.scanChargeId = scanChargeId;
            this.name = name;
            this.value = value;
        }
        public int getScanChargeId() {
            return scanChargeId;
        }
        public String getName() {
            return name;
        }
        public String getValue() {
            return value;
        }
    }
}
