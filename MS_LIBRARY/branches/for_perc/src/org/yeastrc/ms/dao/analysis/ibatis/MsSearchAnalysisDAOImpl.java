package org.yeastrc.ms.dao.analysis.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.Program;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsSearchAnalysisDAOImpl extends BaseSqlMapDAO implements MsSearchAnalysisDAO {

    private static final String namespace = "MsSearchAnalysis";
    
    
    public MsSearchAnalysisDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public MsSearchAnalysis load(int analysisId) {
        return (MsSearchAnalysis) queryForObject(namespace+".select", analysisId);
    }

    @Override
    public List<Integer> getAnalysisIdsForSearch(int searchId) {
        return queryForList(namespace+".selectAnalysisIdsForSearch", searchId);
    }
    
    @Override
    public List<Integer> getSearchIdsForAnalysis(int analysisId) {
        return queryForList(namespace+".selectSearchIdsForAnalysis", analysisId);
    }
    
    @Override
    public int save(MsSearchAnalysis analysis) {
        return saveAndReturnId(namespace+".insert", analysis);
    }

    @Override
    public int updateAnalysisProgram(int analysisId, Program program) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("analysisProgram", program);
        map.put("analysisId", analysisId);
        return update(namespace+".updateAnalysisProgram", map);
    }

    @Override
    public int updateAnalysisProgramVersion(int analysisId, String versionStr) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("analysisId", analysisId);
        map.put("analysisProgramVersion", versionStr);
        return update(namespace+".updateAnalysisProgramVersion", map);
    }
    
    @Override
    public void delete(int analysisId) {
        delete(namespace+".delete", analysisId);
    }
    
}
