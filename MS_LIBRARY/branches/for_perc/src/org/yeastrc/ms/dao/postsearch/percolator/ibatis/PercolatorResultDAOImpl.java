package org.yeastrc.ms.dao.postsearch.percolator.ibatis;

import java.util.List;

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
    public List<Integer> loadResultIdsForRunSearch(int runSearchId) {
        return queryForList(namespace+".selectResultIdsForRunSearch", runSearchId);
    }

    @Override
    public int save(PercolatorResultDataWId data) {
        return saveAndReturnId(namespace+".insert", data);
    }

    @Override
    public void saveAllPercolatorResultData(List<PercolatorResultDataWId> dataList) {
        // TODO Auto-generated method stub

    }

}
