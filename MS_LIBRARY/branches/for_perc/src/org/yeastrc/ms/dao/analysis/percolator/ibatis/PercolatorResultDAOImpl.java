package org.yeastrc.ms.dao.analysis.percolator.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;

import com.ibatis.sqlmap.client.SqlMapClient;

public class PercolatorResultDAOImpl extends BaseSqlMapDAO implements PercolatorResultDAO {

    private static final String namespace = "PercolatorResult";
    
    public PercolatorResultDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public PercolatorResult load(int msResultId) {
        return (PercolatorResult) queryForObject(namespace+".select", msResultId);
    }

    @Override
    public List<Integer> loadResultIdsWithPepThreshold(int analysisId, double pep) {
        Map<String, Number> map = new HashMap<String, Number>(2);
        map.put("analysisId", analysisId);
        map.put("pep", pep);
        return queryForList(namespace+".selectResultIdsWPepThreshold", map);
    }

    @Override
    public List<Integer> loadResultIdsWithQvalueThreshold(int analysisId, double qvalue) {
        Map<String, Number> map = new HashMap<String, Number>(2);
        map.put("analysisId", analysisId);
        map.put("qvalue", qvalue);
        return queryForList(namespace+".selectResultIdsWQvalThreshold", map);
    }
    
    @Override
    public List<Integer> loadResultIdsForRunSearch(int runSearchId) {
        return queryForList(namespace+".selectResultIdsForRunSearch", runSearchId);
    }

    @Override
    public List<Integer> loadResultIdsForPercolatorAnalysis(int analysisId) {
        return queryForList(namespace+".selectResultIdsForAnalysis", analysisId);
    }
    
    @Override
    public void save(PercolatorResultDataWId data) {
        save(namespace+".insert", data);
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
            values.append(data.getSearchAnalysisId() == 0 ? "NULL" : data.getSearchAnalysisId());
            values.append(",");
            double qvalue = data.getQvalue();
            values.append(qvalue == -1.0 ? "NULL" : qvalue);
            values.append(",");
            double pep = data.getPosteriorErrorProbability();
            values.append(pep == -1.0 ? "NULL" : pep);
            values.append(",");
            double discrimScore = data.getDiscriminantScore();
            values.append(discrimScore == -1.0 ? "NULL" : discrimScore);
            values.append(")");
        }
        values.deleteCharAt(0);
        save(namespace+".insertAll", values.toString());
    }
   
}
