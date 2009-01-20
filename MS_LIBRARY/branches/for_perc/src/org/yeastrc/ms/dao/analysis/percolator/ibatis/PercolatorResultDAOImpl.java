package org.yeastrc.ms.dao.analysis.percolator.ibatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorResultBean;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;

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
    public List<Integer> loadResultIdsWithPepThreshold(int runSearchAnalysisId, double pep) {
        Map<String, Number> map = new HashMap<String, Number>(2);
        map.put("rsAnalysisId", runSearchAnalysisId);
        map.put("pep", pep);
        return queryForList(namespace+".selectResultIdsWPepThreshold", map);
    }

    @Override
    public List<Integer> loadResultIdsWithQvalueThreshold(int runSearchAnalysisId, double qvalue) {
        Map<String, Number> map = new HashMap<String, Number>(2);
        map.put("rsAnalysisId", runSearchAnalysisId);
        map.put("qvalue", qvalue);
        return queryForList(namespace+".selectResultIdsWQvalThreshold", map);
    }
    
    @Override
    public List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId) {
        return queryForList(namespace+".selectResultIdsForRunSearchAnalysis", runSearchAnalysisId);
    }

    @Override
    public List<Integer> loadResultIdsForAnalysis(int analysisId) {
        return queryForList(namespace+".selectResultIdsForAnalysis", analysisId);
    }
    
    @Override
    public void save(PercolatorResultDataWId data) {
        save(namespace+".insert", data);
    }

    @Override
    public void saveAllPercolatorResultData(List<PercolatorResultDataWId> dataList) {
        if(dataList == null || dataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for ( PercolatorResultDataWId data: dataList) {
            values.append(",(");
            values.append(data.getResultId() == 0 ? "NULL" : data.getResultId());
            values.append(",");
            values.append(data.getRunSearchAnalysisId() == 0 ? "NULL" : data.getRunSearchAnalysisId());
            values.append(",");
            double qvalue = data.getQvalue();
            values.append(qvalue == -1.0 ? "NULL" : qvalue);
            values.append(",");
            double pep = data.getPosteriorErrorProbability();
            values.append(pep == -1.0 ? "NULL" : pep);
            values.append(",");
            values.append(data.getDiscriminantScore());
            values.append(",");
            values.append(data.getPredictedRetentionTime());
            values.append(")");
        }
        values.deleteCharAt(0);
        save(namespace+".insertAll", values.toString());
    }

    @Override
    public List<PercolatorResult> loadResultsWithScoreThresholdForRunSearchAnalysis(
            int runSearchId, double qvalue, double pep, double discriminantScore) {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            conn = super.getConnection();
            StringBuilder buf = new StringBuilder();
            buf.append("SELECT * from msRunSearchResult as res, PercolatorResult as pres ");
            buf.append("WHERE ");
            buf.append("res.id = pres.resultID");
            buf.append(" AND ");
            buf.append("pres.runSearchAnalysisID = ?");
            if(qvalue != -1.0) {
                buf.append(" AND qValue <= "+qvalue);
            }
            if(pep != -1.0) {
                buf.append(" AND PEP <= "+pep);
            }
            if(discriminantScore != -1.0) {
                buf.append(" AND discriminantScore <= "+discriminantScore);
            }
//            buf.append(" GROUP BY res.scanID, res.charge ORDER BY res.id");
            buf.append(" ORDER BY res.id");
            
            String sql = buf.toString();
            
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, runSearchId );
            rs = stmt.executeQuery();
            
            List<PercolatorResult> resultList = new ArrayList<PercolatorResult>();
            
            while ( rs.next() ) {
            
                PercolatorResultBean result = new PercolatorResultBean();
                result.setId(rs.getInt("id"));
                result.setRunSearchId(rs.getInt("runSearchID"));
                result.setScanId(rs.getInt("scanID"));
                result.setCharge(rs.getInt("charge"));
                SearchResultPeptideBean peptide = new SearchResultPeptideBean();
                peptide.setPeptideSequence(rs.getString("peptide"));
                String preRes = rs.getString("preResidue");
                if(preRes != null)
                    peptide.setPreResidue(preRes.charAt(0));
                String postRes = rs.getString("postResidue");
                if(postRes != null)
                    peptide.setPostResidue(postRes.charAt(0));
                result.setResultPeptide(peptide);
                String vStatus = rs.getString("validationStatus");
                if(vStatus != null)
                    result.setValidationStatus(ValidationStatus.instance(vStatus.charAt(0)));
                result.setQvalue(rs.getDouble("qValue"));
                if(rs.getObject("PEP") != null)
                    result.setQvalue(rs.getDouble("PEP"));
                if(rs.getObject("discriminantScore") != null)
                    result.setDiscriminantScore(rs.getDouble("discriminantScore"));
                result.setPredictedRetentionTime(rs.getBigDecimal("predictedRetentionTime"));
                
                resultList.add(result);
            
            }
            rs.close(); rs = null;
            stmt.close(); stmt = null;
            conn.close(); conn = null;
            
            return resultList;
          
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            
            if (rs != null) {
                try { rs.close(); rs = null; } catch (Exception e) { ; }
            }

            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
        return null;
    }
   
}
