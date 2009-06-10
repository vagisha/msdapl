/**
 * MsRunSearchAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Dec 29, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.ibatis;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.upload.dao.analysis.MsRunSearchAnalysisUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsRunSearchAnalysisUploadDAOIbatisImpl extends BaseSqlMapDAO implements MsRunSearchAnalysisUploadDAO {

    private static final String namespace = "MsRunSearchAnalysis";
    
    public MsRunSearchAnalysisUploadDAOIbatisImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public int save(MsRunSearchAnalysis runSearchAnalysis) {
        return saveAndReturnId(namespace+".insert", runSearchAnalysis);
    }

}
