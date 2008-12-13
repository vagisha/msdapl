package org.yeastrc.ms.dao.postsearch.percolator.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.postsearch.percolator.PercolatorOutputDAO;
import org.yeastrc.ms.domain.postsearch.percolator.PercolatorOutput;

import com.ibatis.sqlmap.client.SqlMapClient;

public class PercolatorOutputDAOImpl extends BaseSqlMapDAO implements PercolatorOutputDAO {

    private static final String namespace = "PercolatorOutput";
    
    public PercolatorOutputDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public PercolatorOutput load(int percOutputId) {
        return (PercolatorOutput) queryForObject(namespace+".select", percOutputId);
    }

    @Override
    public List<Integer> loadOutputIds(int percId) {
        return queryForList(namespace+".selectOutputIdsForPercolator", percId);
    }

    @Override
    public int save(PercolatorOutput percOutput) {
        return saveAndReturnId(namespace+".insert", percOutput);
    }

    @Override
    public void delete(int percOutputId) {
        delete(namespace+".delete", percOutputId);
    }
}
