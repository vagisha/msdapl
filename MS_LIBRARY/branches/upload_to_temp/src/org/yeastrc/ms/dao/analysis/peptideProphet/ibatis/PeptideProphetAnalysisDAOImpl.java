/**
 * PeptideProphetAnalysisUploadDAOImpl.java
 * @author Vagisha Sharma
 * Sep 11, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.analysis.peptideProphet.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetAnalysisDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetAnalysis;
import org.yeastrc.ms.util.StringUtils;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class PeptideProphetAnalysisDAOImpl extends BaseSqlMapDAO implements
    PeptideProphetAnalysisDAO{

    private static final String namespace = "PeptideProphetAnalysis";
    
    private final MsSearchAnalysisDAO analysisDao;
    
    public PeptideProphetAnalysisDAOImpl(SqlMapClient sqlMap, 
                MsSearchAnalysisDAO analysisDao) {
        super(sqlMap);
        this.analysisDao = analysisDao;
    }

    @Override
    public PeptideProphetAnalysis load(int analysisId) {
        return (PeptideProphetAnalysis) queryForObject(namespace+".select", analysisId);
    }

    @Override
    public PeptideProphetAnalysis loadAnalysisForFileName(String fileName, int searchId) {
        // get all the runSearchIds for the search
        List<Integer> analysisIds = analysisDao.getAnalysisIdsForSearch(searchId);
        if(analysisIds == null || analysisIds.size() == 0)
            return null;
        String idString = StringUtils.makeCommaSeparated(analysisIds);
        System.out.println(idString);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("filename", fileName);
        map.put("analysisIds", "("+idString+")");
        
        return (PeptideProphetAnalysis) queryForObject(namespace+".selectAnalysisForFileName", map);
    }
   
}
