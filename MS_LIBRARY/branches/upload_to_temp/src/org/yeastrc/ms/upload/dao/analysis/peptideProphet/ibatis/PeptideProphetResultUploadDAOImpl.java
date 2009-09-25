/**
 * PeptideProphetResultDAOImpl.java
 * @author Vagisha Sharma
 * Jul 30, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.peptideProphet.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.PeptideProphetResultUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class PeptideProphetResultUploadDAOImpl extends BaseSqlMapDAO implements
        PeptideProphetResultUploadDAO {

    private static final String namespace = "PeptideProphetResult"; 
    
    public PeptideProphetResultUploadDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public PeptideProphetResult loadForProphetResultId(int peptideProphetResultId) {
        return (PeptideProphetResult) queryForObject(namespace+".select", peptideProphetResultId);
    }
    
    @Override
    public PeptideProphetResult loadForRunSearchAnalysis(int searchResultId, int runSearchAnalysisId) {
       Map<String, Integer> map = new HashMap<String, Integer>(4);
       map.put("searchResultId", searchResultId);
       map.put("runSearchAnalysisId", runSearchAnalysisId);
       return (PeptideProphetResult) queryForObject(namespace+".selectForRunSearchAnalysis", map);
    }

    @Override
    public PeptideProphetResult loadForSearchAnalysis(int searchResultId, int searchAnalysisId) {
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("searchResultId", searchResultId);
        map.put("searchAnalysisId", searchAnalysisId);
        return (PeptideProphetResult) queryForObject(namespace+".selectForSearchAnalysis", map);
    }

    @Override
    public void saveAllPeptideProphetResultData(
            List<PeptideProphetResultDataWId> dataList) {
        
        if(dataList == null || dataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for ( PeptideProphetResultDataWId data: dataList) {
            values.append(",(");
            values.append(data.getSearchResultId() == 0 ? "NULL" : data.getSearchResultId());
            values.append(",");
            values.append(data.getRunSearchAnalysisId() == 0 ? "NULL" : data.getRunSearchAnalysisId());
            values.append(",");
            double probability = data.getProbability();
            values.append(probability == -1.0 ? "NULL" : probability);
            values.append(",");
            double fVal = data.getfVal();
            values.append(fVal == -1.0 ? "NULL" : fVal);
            values.append(",");
            int ntt = data.getNumEnzymaticTermini();
            values.append(ntt == -1 ? "NULL" : ntt);
            values.append(",");
            int nmc = data.getNumMissedCleavages();
            values.append(nmc == -1 ? "NULL" : nmc);
            values.append(",");
            values.append(data.getMassDifference());
            values.append(",");
            values.append(data.getProbabilityNet_0() == -1.0 ? "NULL" : data.getProbabilityNet_0());
            values.append(",");
            values.append(data.getProbabilityNet_1() == -1.0 ? "NULL" : data.getProbabilityNet_1());
            values.append(",");
            values.append(data.getProbabilityNet_2() == -1.0 ? "NULL" : data.getProbabilityNet_2());
            values.append(")");
        }
        values.deleteCharAt(0);
        save(namespace+".insertAll", values.toString());
    }

}
