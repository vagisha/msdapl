/**
 * MsPeptideSearchDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.MsEnzymeDAO;
import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.dao.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.MsSearchModificationDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.MsEnzymeDAO.EnzymeProperties;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchModification;
import org.yeastrc.ms.domain.search.MsRunSearchResult;
import org.yeastrc.ms.domain.search.MsRunSearchResultDb;
import org.yeastrc.ms.domain.search.SearchFileFormat;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class MsSearchDAOImpl extends BaseSqlMapDAO 
        implements MsSearchDAO<MsRunSearch, MsRunSearchDb> {

    private MsSearchDatabaseDAO seqDbDao;
    private MsSearchModificationDAO modDao;
    private MsSearchResultDAO<MsRunSearchResult, MsRunSearchResultDb> resultDao;
    private MsEnzymeDAO enzymeDao;
    
    public MsSearchDAOImpl(SqlMapClient sqlMap, 
            MsSearchResultDAO<MsRunSearchResult, MsRunSearchResultDb> resultDao,
            MsSearchDatabaseDAO seqDbDao,
            MsSearchModificationDAO modDao,
            MsEnzymeDAO enzymeDao) {
        super(sqlMap);
        this.resultDao = resultDao;
        this.seqDbDao = seqDbDao;
        this.modDao = modDao;
        this.enzymeDao = enzymeDao;
    }
    
    public MsRunSearchDb loadSearch(int searchId) {
        return (MsRunSearchDb) queryForObject("MsSearch.select", searchId);
    }
    
    public List<MsRunSearchDb> loadSearchesForRun(int runId) {
        return queryForList("MsSearch.selectSearchesForRun", runId);
    }
    
    @Override
    public List<Integer> loadSearchIdsForExperiment(int experimentId) {
        return queryForList("MsSearch.selectSearchIdsForExperiment", experimentId);
    }
    
    public List<Integer> loadSearchIdsForRun(int runId) {
        return queryForList("MsSearch.selectSearchIdsForRun", runId);
    }
    
    @Override
    public int loadSearchIdForRunAndExperiment(int runId, int experimentId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("runId", runId);
        map.put("experimentId", experimentId);
        Integer searchId = (Integer)queryForObject("MsSearch.selectSearchIdForRunAndExperiment", map);
        if (searchId != null)
            return searchId;
        return 0;
    }
    
    public int saveSearch(MsRunSearch search, int runId, int experimentId) {
        MsSearchSqlMapParam searchDb = new MsSearchSqlMapParam(runId, search, experimentId);
        int searchId = saveAndReturnId("MsSearch.insert", searchDb);
        
        // save any database information associated with the search 
        for (MsSearchDatabase seqDb: search.getSearchDatabases()) {
            seqDbDao.saveSearchDatabase(seqDb, searchId);
        }
        
        // save any static modifications used for the search
        for (MsSearchModification staticMod: search.getStaticModifications()) {
            modDao.saveStaticModification(staticMod, searchId);
        }
        
        // save any dynamic modifications used for the search
        for (MsSearchModification dynaMod: search.getDynamicModifications()) {
            modDao.saveDynamicModification(dynaMod, searchId);
        }
        
        // save any enzymes used for the search
        List<MsEnzyme> enzymes = search.getEnzymeList();
        for (MsEnzyme enzyme: enzymes) 
            // use the enzyme name attribute only to look for a matching enzyme.
            enzymeDao.saveEnzymeforSearch(enzyme, searchId, Arrays.asList(new EnzymeProperties[] {EnzymeProperties.NAME}));
        
        
        return searchId;
    }
    
    public void deleteSearch(int searchId) {
        delete("MsSearch.delete", searchId);
    }

    
    //---------------------------------------------------------------------------------------
    /** 
     * Type handler for converting between SearchFileFormat and JDBC's VARCHAR types. 
     */
    public static class SearchFileFormatTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            String format = getter.getString();
            if (getter.wasNull())
                return SearchFileFormat.UNKNOWN;
            return SearchFileFormat.instance(format);
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            if (parameter == null)
                setter.setNull(java.sql.Types.VARCHAR);
            else
                setter.setString(((SearchFileFormat)parameter).name());
        }

        public Object valueOf(String s) {
            return SearchFileFormat.instance(s);
        }
    }
    //---------------------------------------------------------------------------------------
    
    //---------------------------------------------------------------------------------------
    
    /**
     * Convenience class for encapsulating a MsRunSearch and associated runId
     */
    public class MsSearchSqlMapParam implements MsRunSearch {

        private int runId;
        private int experimentId;
        private MsRunSearch search;
        
        public MsSearchSqlMapParam(int runId, MsRunSearch search, int experimentId) {
            this.runId = runId;
            this.experimentId = experimentId;
            this.search = search;
        }

        /**
         * @return the runId
         */
        public int getRunId() {
            return runId;
        }
        
        public int getExperimentId() {
            return experimentId;
        }
        
        public List<MsSearchModification> getDynamicModifications() {
            return search.getDynamicModifications();
        }

        public List<MsSearchDatabase> getSearchDatabases() {
            return search.getSearchDatabases();
        }

        public List<MsSearchModification> getStaticModifications() {
            return search.getStaticModifications();
        }

        public BigDecimal getFragmentMassTolerance() {
            return search.getFragmentMassTolerance();
        }

        public String getFragmentMassType() {
            return search.getFragmentMassType();
        }

        public SearchFileFormat getSearchFileFormat() {
            return search.getSearchFileFormat();
        }

        public BigDecimal getPrecursorMassTolerance() {
            return search.getPrecursorMassTolerance();
        }

        public String getPrecursorMassType() {
            return search.getPrecursorMassType();
        }

        public Date getSearchDate() {
            return search.getSearchDate();
        }

        public int getSearchDuration() {
            return search.getSearchDuration();
        }

        public String getSearchEngineName() {
            return search.getAnalysisProgramName();
        }

        public String getSearchEngineVersion() {
            return search.getAnalysisProgramVersion();
        }

        @Override
        public List<MsEnzyme> getEnzymeList() {
            return search.getEnzymeList();
        }
    }

}
