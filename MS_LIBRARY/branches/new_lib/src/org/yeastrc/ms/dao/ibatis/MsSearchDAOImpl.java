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
import java.util.List;

import org.yeastrc.ms.dao.MsEnzymeDAO;
import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.dao.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.MsSearchModificationDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.MsEnzymeDAO.EnzymeProperties;
import org.yeastrc.ms.domain.MsEnzyme;
import org.yeastrc.ms.domain.MsSearch;
import org.yeastrc.ms.domain.MsSearchDatabase;
import org.yeastrc.ms.domain.MsSearchDb;
import org.yeastrc.ms.domain.MsSearchModification;
import org.yeastrc.ms.domain.MsSearchResult;
import org.yeastrc.ms.domain.MsSearchResultDb;
import org.yeastrc.ms.domain.SearchFileFormat;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class MsSearchDAOImpl extends BaseSqlMapDAO 
        implements MsSearchDAO<MsSearch, MsSearchDb> {

    private MsSearchDatabaseDAO seqDbDao;
    private MsSearchModificationDAO modDao;
    private MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao;
    private MsEnzymeDAO enzymeDao;
    
    public MsSearchDAOImpl(SqlMapClient sqlMap, 
            MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao,
            MsSearchDatabaseDAO seqDbDao,
            MsSearchModificationDAO modDao,
            MsEnzymeDAO enzymeDao) {
        super(sqlMap);
        this.resultDao = resultDao;
        this.seqDbDao = seqDbDao;
        this.modDao = modDao;
        this.enzymeDao = enzymeDao;
    }
    
    @Override
    public int getMaxSearchGroupId() {
        Integer groupId = (Integer)queryForObject("MsSearch.selectMaxSearchGroupId", null);
        if (groupId != null)
            return groupId;
        return 0;
    }
    
    public MsSearchDb loadSearch(int searchId) {
        return (MsSearchDb) queryForObject("MsSearch.select", searchId);
    }
    
    public List<MsSearchDb> loadSearchesForRun(int runId) {
        return queryForList("MsSearch.selectSearchesForRun", runId);
    }
    
    public List<Integer> loadSearchIdsForRun(int runId) {
        return queryForList("MsSearch.selectSearchIdsForRun", runId);
    }
    
    public int saveSearch(MsSearch search, int runId, int searchGroupId) {
        MsSearchSqlMapParam searchDb = new MsSearchSqlMapParam(runId, search, searchGroupId);
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
        
        // NOTE: all of this is now done by the MySQL trigger
//        // delete all results for this search
//        resultDao.deleteResultsForSearch(searchId);
//        
//        // delete any sequence database(s) associated with this search
//        seqDbDao.deleteSearchDatabases(searchId);
//        
//        // delete any static and dynamic modifications used for this search
//        modDao.deleteStaticModificationsForSearch(searchId);
//        modDao.deleteDynamicModificationsForSearch(searchId);
//        
//        // delete any enzymes used for this search
//        enzymeDao.deleteEnzymesForSearch(searchId);
        
        // finally delete the search
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
    public class MsSearchSqlMapParam implements MsSearch {

        private int runId;
        private int searchGroupId;
        private MsSearch search;
        
        public MsSearchSqlMapParam(int runId, MsSearch search, int searchGroupId) {
            this.runId = runId;
            this.searchGroupId = searchGroupId;
            this.search = search;
        }

        /**
         * @return the runId
         */
        public int getRunId() {
            return runId;
        }
        
        public int getSearchGroupId() {
            return searchGroupId;
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
            return search.getSearchEngineName();
        }

        public String getSearchEngineVersion() {
            return search.getSearchEngineVersion();
        }

        @Override
        public List<MsEnzyme> getEnzymeList() {
            return search.getEnzymeList();
        }
    }
}
