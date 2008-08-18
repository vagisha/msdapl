/**
 * Ms2FileChargeIndependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeIndependentAnalysisDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Field;

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

    @Override
    public void saveAll(List<MS2ChargeIndependentAnalysisDb> analysisList) {
        if (analysisList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MS2ChargeIndependentAnalysisDb analysis: analysisList) {
            values.append("(");
            values.append(analysis.getScanId());
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
        
        save("MS2ChgIAnalysis.insertAll", values.toString());
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
