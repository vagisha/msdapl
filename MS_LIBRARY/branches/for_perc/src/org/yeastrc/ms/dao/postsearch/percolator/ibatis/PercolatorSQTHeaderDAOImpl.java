package org.yeastrc.ms.dao.postsearch.percolator.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.postsearch.percolator.PercolatorSQTHeaderDAO;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;

import com.ibatis.sqlmap.client.SqlMapClient;

public class PercolatorSQTHeaderDAOImpl extends BaseSqlMapDAO implements PercolatorSQTHeaderDAO {

    private static final String namespace = "PercolatorSqtHeader";
        
    public PercolatorSQTHeaderDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public void deleteSQTHeadersForPercolatorOutput(int percOutputId) {
        delete(namespace+".deleteHeadersForPercolatorOutput", percOutputId);
    }

    @Override
    public List<SQTHeaderItem> loadSQTHeadersForPercolatorOutput(int percOutputId) {
        return queryForList(namespace+".selectHeadersForPercolatorOutput", percOutputId);
    }

    @Override
    public void saveSQTHeader(SQTHeaderItem headerItem, int percOutputId) {
        PercolatorSQTHeaderWrap wrap = new PercolatorSQTHeaderWrap(headerItem, percOutputId);
        save(namespace+".insertHeader",wrap);
    }

}
