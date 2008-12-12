package org.yeastrc.ms.dao.postsearch.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.postsearch.MsPostSearchAnalysisDAO;
import org.yeastrc.ms.domain.postsearch.MsPostSearchAnalysis;
import org.yeastrc.ms.domain.postsearch.MsPostSearchAnalysisIn;
import org.yeastrc.ms.domain.search.SearchProgram;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsPostSearchAnalysisDAOImpl extends BaseSqlMapDAO implements MsPostSearchAnalysisDAO {

    private static final String namespace = "MsPostSearchAnalysis";
    
    
    public MsPostSearchAnalysisDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public MsPostSearchAnalysis load(int analysisId) {
        return (MsPostSearchAnalysis) queryForObject(namespace+".select", analysisId);
    }

    @Override
    public List<Integer> getAnalysisIdsForSearch(int searchId) {
        return queryForList(namespace+".selectAnalysisIdsForSearch", searchId);
    }
    
    @Override
    public int save(MsPostSearchAnalysisIn analysis, int searchId) {
        MsPostSearchAnalysisWrap wrap = new MsPostSearchAnalysisWrap(searchId, analysis);
        return saveAndReturnId(namespace+".insert", wrap);
    }

    @Override
    public int updateAnalysisProgram(int analysis, SearchProgram program) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("analysisId", analysis);
        map.put("analysisProgram", program);
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
