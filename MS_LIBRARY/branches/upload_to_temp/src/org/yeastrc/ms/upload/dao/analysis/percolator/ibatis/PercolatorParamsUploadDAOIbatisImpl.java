package org.yeastrc.ms.upload.dao.analysis.percolator.ibatis;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;
import org.yeastrc.ms.upload.dao.analysis.percolator.PercolatorParamsUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

public class PercolatorParamsUploadDAOIbatisImpl extends BaseSqlMapDAO implements PercolatorParamsUploadDAO {

    private static final String namespace = "PercolatorParams";
        
    public PercolatorParamsUploadDAOIbatisImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    @Override
    public void saveParam(PercolatorParam param, int analysisId) {
        PercolatorParamWrap wrap = new PercolatorParamWrap(param, analysisId);
        save(namespace+".insertParam",wrap);
    }

}
