package org.yeastrc.ms.upload.dao.analysis.percolator.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.upload.dao.analysis.percolator.PercolatorResultUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

public class PercolatorResultUploadDAOIbatisImpl extends BaseSqlMapDAO implements PercolatorResultUploadDAO {

    private static final String namespace = "PercolatorResult";
    
    public PercolatorResultUploadDAOIbatisImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public void saveAllPercolatorResultData(List<PercolatorResultDataWId> dataList) {
        if(dataList == null || dataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for ( PercolatorResultDataWId data: dataList) {
            values.append(",(");
            values.append(data.getResultId() == 0 ? "NULL" : data.getResultId());
            values.append(",");
            values.append(data.getRunSearchAnalysisId() == 0 ? "NULL" : data.getRunSearchAnalysisId());
            values.append(",");
            double qvalue = data.getQvalue();
            values.append(qvalue == -1.0 ? "NULL" : qvalue);
            values.append(",");
            double pep = data.getPosteriorErrorProbability();
            values.append(pep == -1.0 ? "NULL" : pep);
            values.append(",");
            values.append(data.getDiscriminantScore());
            values.append(",");
            values.append(data.getPredictedRetentionTime());
            values.append(")");
        }
        values.deleteCharAt(0);
        save(namespace+".insertAll", values.toString());
    }
}
