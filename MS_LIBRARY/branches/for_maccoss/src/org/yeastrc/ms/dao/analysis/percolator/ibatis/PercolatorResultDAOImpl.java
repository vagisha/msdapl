package org.yeastrc.ms.dao.analysis.percolator.ibatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorResultBean;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.ResultResidueModBean;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;

import com.ibatis.sqlmap.client.SqlMapClient;

public class PercolatorResultDAOImpl extends BaseSqlMapDAO implements PercolatorResultDAO {

    private static final String namespace = "PercolatorResult";
    
    private MsRunSearchDAO runSearchDao;
    private MsRunSearchAnalysisDAO runSearchAnalysisDao;
    private MsSearchModificationDAO modDao;
    
    public PercolatorResultDAOImpl(SqlMapClient sqlMap, MsRunSearchAnalysisDAO rsaDao,
            MsRunSearchDAO runSearchDao, MsSearchModificationDAO modDao) {
        super(sqlMap);
        this.runSearchDao = runSearchDao;
        this.runSearchAnalysisDao = rsaDao;
        this.modDao = modDao;
    }

    @Override
    public PercolatorResult load(int msResultId) {
        return (PercolatorResult) queryForObject(namespace+".select", msResultId);
    }

    @Override
    public List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId) {
        return queryForList(namespace+".selectResultIdsForRunSearchAnalysis", runSearchAnalysisId);
    }
    
    @Override
    public List<Integer> loadResultIdsForRunSearchAnalysis(int runSearchAnalysisId, int limit, int offset) {
        Map<String, Integer> map = new HashMap<String, Integer>(5);
        map.put("runSearchAnalysisId", runSearchAnalysisId);
        map.put("limit", limit);
        map.put("offset", offset);
        return queryForList(namespace+".selectResultIdsLimitedForRunSearchAnalysis", map);
    }

    @Override
    public List<Integer> loadResultIdsForRunSearchAnalysisScan(int runSearchAnalysisId, int scanId) {
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("runSearchAnalysisId", runSearchAnalysisId);
        map.put("scanId", scanId);
        return queryForList(namespace+".selectResultIdsForRunSearchAnalysisScan", map);
    }
    
    @Override
    public List<Integer> loadResultIdsForAnalysis(int analysisId) {
        return queryForList(namespace+".selectResultIdsForAnalysis", analysisId);
    }
    
    @Override
    public List<Integer> loadResultIdsForAnalysis(int searchAnalyisId, int limit, int offset) {
        Map<String, Integer> map = new HashMap<String, Integer>(5);
        map.put("searchAnalyisId", searchAnalyisId);
        map.put("limit", limit);
        map.put("offset", offset);
        return queryForList(namespace+".selectResultIdsLimitedForAnalysis", map);
    }
    
    @Override
    public int numRunAnalysisResults(int runSearchAnalysisId) {
        return (Integer)queryForObject(namespace+".countRunSearchAnalysisResults", runSearchAnalysisId);
    }
    
    @Override
    public int numAnalysisResults(int searchAnalysisId) {
        return (Integer)queryForObject(namespace+".countSearchAnalysisResults", searchAnalysisId);
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
    public List<Integer> loadResultIdsForSearchAnalysis(int searchAnalysisId,
            PercolatorResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        
        if(filterCriteria == null)
            filterCriteria = new PercolatorResultFilterCriteria();
        
        if(sortCriteria == null)
            sortCriteria = new ResultSortCriteria(null, null);
        
        int offset =  sortCriteria.getOffset() == null ? 0 : sortCriteria.getOffset();
        
        // If we don't have filters and nothing to sort by use the simple method
        if(!filterCriteria.hasFilters() && sortCriteria.getSortBy() == null) {
            return loadResultIdsForAnalysis(searchAnalysisId); 
        }

        boolean useScanTable = filterCriteria.hasScanFilter() || SORT_BY.isScanRelated(sortCriteria.getSortBy());
        boolean useModsTable = filterCriteria.hasMofificationFilter();
        boolean useResultsTable = filterCriteria.superHasFilters() 
                                || SORT_BY.isSearchRelated(sortCriteria.getSortBy())
                                || useScanTable
                                || useModsTable;
        
        boolean userPercTable = filterCriteria.hasFilters() || SORT_BY.isPercolatorRelated(sortCriteria.getSortBy());
        
        // If we don't have any filters on the msRunSearchResult, msScan and modifications tables use a simpler query
        if(!useScanTable && !useResultsTable && !useModsTable && !userPercTable) {
            if(sortCriteria.getLimitCount() != null)
                return loadResultIdsForAnalysis(searchAnalysisId, sortCriteria.getLimitCount(), offset);
            else 
                return loadResultIdsForAnalysis(searchAnalysisId); 
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT pres.resultID FROM ( ");
        sql.append("msRunSearchAnalysis AS rsa, PercolatorResult AS pres");
        if(useResultsTable)
            sql.append(", msRunSearchResult AS res");
        if(useScanTable)
            sql.append(", msScan AS scan");
        sql.append(" ) ");
        
        if(useModsTable) {
            sql.append("LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) ");
        }
        
        sql.append("WHERE rsa.searchAnalysisID = "+searchAnalysisId+" ");
        sql.append("AND rsa.id = pres.runSearchAnalysisID ");
        if(useResultsTable)
            sql.append("AND res.id = pres.resultID ");
        if(useScanTable) {
            sql.append("AND res.scanID = scan.id ");
        }
        
        // filter of scan number
        if(filterCriteria.hasScanFilter()) {
            sql.append("AND "+filterCriteria.makeScanFilterSql());
        }
        // filter on retention time
        if(filterCriteria.hasRTFilter()) {
            sql.append("AND "+filterCriteria.makeRTFilterSql());
        }
        // filter on charge
        if(filterCriteria.hasChargeFilter()) {
            sql.append("AND "+filterCriteria.makeChargeFilterSql());
        }
        // observed mass filter
        if(filterCriteria.hasMassFilter()) {
            sql.append("AND "+filterCriteria.makeMassFilterSql());
        }
        // peptide filter
        if(filterCriteria.hasPeptideFilter()) {
            sql.append("AND "+filterCriteria.makePeptideSql());
        }
        // modifications filter
        if(filterCriteria.hasMofificationFilter()) {
            sql.append("AND "+filterCriteria.makeModificationFilter());
        }
        // QValue filter
        if(filterCriteria.hasQValueFilter()) {
            sql.append("AND "+filterCriteria.makeQValueFilterSql());
        }
        // PEP filter
        if(filterCriteria.hasPepFilter()) {
            sql.append("AND "+filterCriteria.makePepFilterSql());
        }
        // Discriminant Score (SVM score filter)
        if(filterCriteria.hasDsFilter()) {
            sql.append("AND "+filterCriteria.makeDsFilterSql());
        }
        
        
        if(sortCriteria != null) {
            if(sortCriteria.getSortBy() != null) {
                sql.append("ORDER BY "+sortCriteria.getSortBy().getColumnName());
            }
            else {
                sql.append("ORDER BY pres.resultID ");
            }
        }
        
        if(sortCriteria.getLimitCount() != null) {
            sql.append("LIMIT "+sortCriteria.getLimitCount()+", "+offset);
        }
        
        System.out.println(sql.toString());
        
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = super.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql.toString());
            
            List<Integer> resultIds = new ArrayList<Integer>();
            
            while ( rs.next() ) {
                resultIds.add(rs.getInt("resultID"));
            }
            return resultIds;
        }
        catch (Exception e) {
            log.error("Failed to execute query: "+sql.toString(), e);
            throw new RuntimeException("Failed to execute query: "+sql, e);
        }
        finally {
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
    }
    
    @Override
    public List<Integer> loadResultIdsForRunSearchAnalysis(
            int runSearchAnalysisId,
            PercolatorResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        
        if(filterCriteria == null)
            filterCriteria = new PercolatorResultFilterCriteria();
        
        if(sortCriteria == null)
            sortCriteria = new ResultSortCriteria(null, null);
        
        int offset =  sortCriteria.getOffset() == null ? 0 : sortCriteria.getOffset();
        
        // If we don't have filters and nothing to sort by use the simple method
        if(!filterCriteria.hasFilters() && sortCriteria.getSortBy() == null) {
            return loadResultIdsForRunSearchAnalysis(runSearchAnalysisId); 
        }
        
        
        boolean useScanTable = filterCriteria.hasScanFilter() || SORT_BY.isScanRelated(sortCriteria.getSortBy());
        boolean useModsTable = filterCriteria.hasMofificationFilter();
        boolean useResultsTable = filterCriteria.superHasFilters() 
                                || SORT_BY.isSearchRelated(sortCriteria.getSortBy())
                                || useScanTable
                                || useModsTable;
        
        boolean userPercTable = filterCriteria.hasFilters() || SORT_BY.isPercolatorRelated(sortCriteria.getSortBy());
        
        // If we don't have any filters on the msRunSearchResult, msScan and modifications tables use a simpler query
        if(!useScanTable && !useResultsTable && !useModsTable && !userPercTable) {
            if(sortCriteria.getLimitCount() != null)
                return loadResultIdsForRunSearchAnalysis(runSearchAnalysisId, sortCriteria.getLimitCount(), offset);
            else 
                return loadResultIdsForRunSearchAnalysis(runSearchAnalysisId); 
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT pres.resultID FROM ( ");
        sql.append("PercolatorResult as pres");
        if(useResultsTable)
            sql.append(", msRunSearchResult AS res");
        if(useScanTable)
            sql.append(", msScan AS scan");
        sql.append(" ) ");
        
        if(useModsTable) {
            sql.append("LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) ");
        }
        
        sql.append("WHERE pres.runSearchAnalysisID = "+runSearchAnalysisId+" ");
        if(useResultsTable)
            sql.append("AND res.id = pres.resultID ");
        if(useScanTable) {
            sql.append("AND res.scanID = scan.id ");
        }
        
        // filter of scan number
        if(filterCriteria.hasScanFilter()) {
            sql.append("AND "+filterCriteria.makeScanFilterSql());
        }
        // filter on retention time
        if(filterCriteria.hasRTFilter()) {
            sql.append("AND "+filterCriteria.makeRTFilterSql());
        }
        // filter on charge
        if(filterCriteria.hasChargeFilter()) {
            sql.append("AND "+filterCriteria.makeChargeFilterSql());
        }
        // observed mass filter
        if(filterCriteria.hasMassFilter()) {
            sql.append("AND "+filterCriteria.makeMassFilterSql());
        }
        // peptide filter
        if(filterCriteria.hasPeptideFilter()) {
            sql.append("AND "+filterCriteria.makePeptideSql());
        }
        // modifications filter
        if(filterCriteria.hasMofificationFilter()) {
            sql.append("AND "+filterCriteria.makeModificationFilter());
        }
        // QValue filter
        if(filterCriteria.hasQValueFilter()) {
            sql.append("AND "+filterCriteria.makeQValueFilterSql());
        }
        // PEP filter
        if(filterCriteria.hasPepFilter()) {
            sql.append("AND "+filterCriteria.makePepFilterSql());
        }
        // Discriminant Score (SVM score filter)
        if(filterCriteria.hasDsFilter()) {
            sql.append("AND "+filterCriteria.makeDsFilterSql());
        }
        
        
        if(sortCriteria != null) {
            if(sortCriteria.getSortBy() != null) {
                sql.append("ORDER BY "+sortCriteria.getSortBy().getColumnName());
            }
            else {
                sql.append("ORDER BY pres.resultID ");
            }
        }
        
        if(sortCriteria.getLimitCount() != null) {
            sql.append("LIMIT "+sortCriteria.getLimitCount()+", "+offset);
        }
        
        System.out.println(sql.toString());
        
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = super.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql.toString());
            
            List<Integer> resultIds = new ArrayList<Integer>();
            
            while ( rs.next() ) {
                resultIds.add(rs.getInt("resultID"));
            }
            return resultIds;
        }
        catch (Exception e) {
            log.error("Failed to execute query: "+sql.toString(), e);
            throw new RuntimeException("Failed to execute query: "+sql, e);
        }
        finally {
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
    }

    
    @Override
    public List<PercolatorResult> loadTopPercolatorResultsN(
            int runSearchAnalysisId, Double qvalue, Double pep, Double discriminantScore,
            boolean getDynaResMods) {
        if(!getDynaResMods)
            return loadTopPercolatorResultsNNoMods(runSearchAnalysisId, qvalue, pep, discriminantScore);
        else
            return loadTopResultsForRunSearchNWMods(runSearchAnalysisId, qvalue, pep, discriminantScore);
    }
    
    private List<PercolatorResult> loadTopResultsForRunSearchNWMods(
            int runSearchAnalysisId, Double qvalue, Double pep, Double discriminantScore) {
        
        // get the dynamic residue modifications for the search
        MsRunSearchAnalysis msa = runSearchAnalysisDao.load(runSearchAnalysisId);
        if(msa == null) {
            log.error("No runSearchAnalysis found with ID: "+runSearchAnalysisId);
            throw new IllegalArgumentException("No run search analysis found with ID: "+runSearchAnalysisId);
        }
        MsRunSearch runSearch = runSearchDao.loadRunSearch(msa.getRunSearchId());
        if(runSearch == null) {
            log.error("No run search found with ID: "+msa.getRunSearchId());
            throw new IllegalArgumentException("No run search found with ID: "+msa.getRunSearchId());
        }
        
        List<MsResidueModification> searchDynaMods = modDao.loadDynamicResidueModsForSearch(runSearch.getSearchId());
        Map<Integer, MsResidueModification> dynaModMap = new HashMap<Integer, MsResidueModification>();
        for(MsResidueModification mod: searchDynaMods) {
            dynaModMap.put(mod.getId(), mod);
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT * FROM (msRunSearchResult AS res, PercolatorResult AS pres) "+
                     "LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) "+
                     "WHERE res.id = pres.resultID "+
                     "AND pres.runSearchAnalysisID = ? ";
        if(qvalue != null)
            sql +=   " AND qvalue <= "+qvalue;
        if(pep != null && pep < 1.0) 
            sql +=   " AND pep <= "+pep;
        if(discriminantScore != null)
            sql +=   " AND discriminantScore >= "+discriminantScore;

        sql +=       " ORDER BY res.id";

        try {
            conn = super.getConnection();
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, runSearchAnalysisId );
            rs = stmt.executeQuery();
            
            List<PercolatorResult> resultList = new ArrayList<PercolatorResult>();
            
            PercolatorResultBean lastResult = null;
            List<MsResultResidueMod> resultDynaMods = new ArrayList<MsResultResidueMod>();
            
            
            while ( rs.next() ) {
            
                int resultId = rs.getInt("id");
                
                if(lastResult == null || resultId != lastResult.getId()) {
                    
                    if(lastResult != null) {
                        lastResult.getResultPeptide().setDynamicResidueModifications(resultDynaMods);
                    }
                    
                    PercolatorResultBean result = makePercolatorResult(rs);
                    resultList.add(result);
                    
                    lastResult = result;
                    resultDynaMods = new ArrayList<MsResultResidueMod>();
                }
                
                int modId = rs.getInt("modID");
                if(modId != 0) {
                    ResultResidueModBean resMod = makeResultResidueMod(rs, dynaModMap.get(modId));
                    
                    resultDynaMods.add(resMod);
                }
            
            }
            if(lastResult != null)
                lastResult.getResultPeptide().setDynamicResidueModifications(resultDynaMods);
            
            return resultList;
          
        }
        catch (Exception e) {
            log.error("Failed to execute query: "+sql, e);
            throw new RuntimeException("Failed to execute query: "+sql, e);
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
    }
    
    private ResultResidueModBean makeResultResidueMod(ResultSet rs, MsResidueModification searchDynaMod)
                throws SQLException {
        ResultResidueModBean resMod = new ResultResidueModBean();
        resMod.setModifiedPosition(rs.getInt("position"));
        resMod.setModificationMass(searchDynaMod.getModificationMass());
        resMod.setModificationSymbol(searchDynaMod.getModificationSymbol());
        resMod.setModifiedResidue(searchDynaMod.getModifiedResidue());
        return resMod;
    }
    
    private List<PercolatorResult> loadTopPercolatorResultsNNoMods(
            int runSearchAnalysisId, Double qvalue, Double pep, Double discriminantScore) {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        StringBuilder buf = new StringBuilder();
        buf.append("SELECT * from msRunSearchResult as res, PercolatorResult as pres ");
        buf.append("WHERE res.id = pres.resultID ");
        buf.append("AND pres.runSearchAnalysisID = ?");
        if(qvalue != null)
            buf.append(" AND qvalue <= "+qvalue);
        if(pep != null)
            buf.append(" AND pep <= "+pep);
        if(discriminantScore != null)
            buf.append(" AND discriminantScore >= "+discriminantScore);
        buf.append(" ORDER BY res.id");
        String sql = buf.toString();
        
        try {
            
            conn = super.getConnection();
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, runSearchAnalysisId );
            rs = stmt.executeQuery();
            
            List<PercolatorResult> resultList = new ArrayList<PercolatorResult>();
            
            while ( rs.next() ) {
            
                PercolatorResultBean result = makePercolatorResult(rs);
                resultList.add(result);
            }
            
            return resultList;
        }
        catch (Exception e) {
            log.error("Failed to execute query: "+sql, e);
            throw new RuntimeException("Failed to execute query: "+sql, e);
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
    }

    private PercolatorResultBean makePercolatorResult(ResultSet rs)
            throws SQLException {
        PercolatorResultBean result = new PercolatorResultBean();
        result.setId(rs.getInt("id"));
        result.setRunSearchId(rs.getInt("runSearchID"));
        result.setRunSearchAnalysisId(rs.getInt("runSearchAnalysisID"));
        result.setScanId(rs.getInt("scanID"));
        result.setCharge(rs.getInt("charge"));
        result.setObservedMass(rs.getBigDecimal("observedMass"));
        String vStatus = rs.getString("validationStatus");
        if(vStatus != null)
            result.setValidationStatus(ValidationStatus.instance(vStatus.charAt(0)));
        result.setQvalue(rs.getDouble("qvalue"));
        if(rs.getObject("pep") != null)
            result.setQvalue(rs.getDouble("pep"));
        if(rs.getObject("discriminantScore") != null)
            result.setDiscriminantScore(rs.getDouble("discriminantScore"));
        result.setPredictedRetentionTime(rs.getBigDecimal("predictedRetentionTime"));
        
        SearchResultPeptideBean peptide = new SearchResultPeptideBean();
        peptide.setPeptideSequence(rs.getString("peptide"));
        String preRes = rs.getString("preResidue");
        if(preRes != null)
            peptide.setPreResidue(preRes.charAt(0));
        String postRes = rs.getString("postResidue");
        if(postRes != null)
            peptide.setPostResidue(postRes.charAt(0));
        result.setResultPeptide(peptide);
        return result;
    }
}
