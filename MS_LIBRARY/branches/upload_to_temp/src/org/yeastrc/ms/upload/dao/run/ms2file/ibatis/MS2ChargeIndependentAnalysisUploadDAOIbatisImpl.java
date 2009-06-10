/**
 * Ms2FileChargeIndependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.ms2file.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeIndependentAnalysisWId;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2ChargeIndependentAnalysisWrap;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ChargeIndependentAnalysisUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2ChargeIndependentAnalysisUploadDAOIbatisImpl extends BaseSqlMapDAO
        implements MS2ChargeIndependentAnalysisUploadDAO {

    public MS2ChargeIndependentAnalysisUploadDAOIbatisImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void save(MS2NameValuePair analysis, int scanId) {
        MS2ChargeIndependentAnalysisWrap analysisDb = new MS2ChargeIndependentAnalysisWrap(analysis, scanId);
        save("MS2ChgIAnalysis.insert", analysisDb);
    }

    @Override
    public void saveAll(List<MS2ChargeIndependentAnalysisWId> analysisList) {
        if (analysisList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MS2ChargeIndependentAnalysisWId analysis: analysisList) {
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
}
