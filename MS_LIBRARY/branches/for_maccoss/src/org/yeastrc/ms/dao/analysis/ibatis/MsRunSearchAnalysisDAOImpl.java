/**
 * MsRunSearchAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Dec 29, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.analysis.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsRunSearchAnalysisDAOImpl extends BaseSqlMapDAO implements MsRunSearchAnalysisDAO {

    private static final String namespace = "MsRunSearchAnalysis";
    
    public MsRunSearchAnalysisDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public int save(MsRunSearchAnalysis runSearchAnalysis) {
        return saveAndReturnId(namespace+".insert", runSearchAnalysis);
    }
    
    @Override
    public MsRunSearchAnalysis load(int runSearchAnalysisId) {
        return (MsRunSearchAnalysis) queryForObject(namespace+".select", runSearchAnalysisId);
    }
    
    @Override
    public List<Integer> getRunSearchAnalysisIdsForAnalysis(int analysisId) {
        return queryForList(namespace+".selectIdsForAnalysis", analysisId);
    }

    @Override
    public String loadFilenameForRunSearchAnalysis(int runSearchAnalysisId) {
        String filename = (String)queryForObject(namespace+".selectFileNameForRunSearchAnalysisId", runSearchAnalysisId);
        if(filename == null)
            return null;
        int idx = filename.lastIndexOf('.');
        if (idx != -1)
            filename = filename.substring(0, idx);
        return filename;
    }

}
