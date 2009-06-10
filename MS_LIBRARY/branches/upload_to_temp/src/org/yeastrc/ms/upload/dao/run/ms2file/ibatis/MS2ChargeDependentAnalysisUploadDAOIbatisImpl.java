/**
 * Ms2FileChargeDependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.ms2file.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeDependentAnalysisWId;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2ChargeDependentAnalysisWrap;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ChargeDependentAnalysisUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2ChargeDependentAnalysisUploadDAOIbatisImpl extends BaseSqlMapDAO
        implements MS2ChargeDependentAnalysisUploadDAO {

    public MS2ChargeDependentAnalysisUploadDAOIbatisImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void save(MS2NameValuePair analysis, int scanChargeId) {
        MS2ChargeDependentAnalysisWrap analysisDb = new MS2ChargeDependentAnalysisWrap(analysis, scanChargeId);
        save("MS2ChgDAnalysis.insert", analysisDb);
    }

    @Override
    public void saveAll(List<MS2ChargeDependentAnalysisWId> analysisList) {
        if (analysisList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MS2ChargeDependentAnalysisWId analysis: analysisList) {
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
}
