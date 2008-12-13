package org.yeastrc.ms.dao.postsearch.percolator.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.postsearch.percolator.PercolatorResultDAO;
import org.yeastrc.ms.domain.postsearch.percolator.PercolatorResult;
import org.yeastrc.ms.domain.postsearch.percolator.PercolatorResultDataWId;

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
    public List<Integer> loadResultIdsForPercolatorOutput(int percOutputId) {
        return queryForList(namespace+".selectResultIdsForPercolatorOutput", percOutputId);
    }

    @Override
    public List<Integer> loadResultIdsWithPepThreshold(int percOutputId, double pep) {
        Map<String, Number> map = new HashMap<String, Number>(2);
        map.put("percOutputId", percOutputId);
        map.put("pep", pep);
        return queryForList(namespace+".selectResultIdsWPepThreshold", map);
    }

    @Override
    public List<Integer> loadResultIdsWithQvalueThreshold(int percOutputId, double qvalue) {
        Map<String, Number> map = new HashMap<String, Number>(2);
        map.put("percOutputId", percOutputId);
        map.put("qvalue", qvalue);
        return queryForList(namespace+".selectResultIdsWQvalThreshold", map);
    }
    
    @Override
    public List<Integer> loadResultIdsForRunSearch(int runSearchId) {
        return queryForList(namespace+".selectResultIdsForRunSearch", runSearchId);
    }

    @Override
    public int save(PercolatorResultDataWId data) {
        return saveAndReturnId(namespace+".insert", data);
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
            values.append(data.getPercolatorOutputId() == 0 ? "NULL" : data.getPercolatorOutputId());
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
