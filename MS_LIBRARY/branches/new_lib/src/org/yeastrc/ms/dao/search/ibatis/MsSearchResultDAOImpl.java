package org.yeastrc.ms.dao.search.ibatis;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.search.MsResultResidueModIn;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultDb;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.MsResidueModificationImpl;
import org.yeastrc.ms.domain.search.impl.MsTerminalModificationImpl;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class MsSearchResultDAOImpl extends BaseSqlMapDAO 
        implements MsSearchResultDAO<MsSearchResult, MsSearchResultDb> {

    private MsSearchResultProteinDAO matchDao;
    private MsSearchModificationDAO modDao;
    
    public MsSearchResultDAOImpl(SqlMapClient sqlMap, MsSearchResultProteinDAO matchDao,
            MsSearchModificationDAO modDao) {
        super(sqlMap);
        this.matchDao = matchDao;
        this.modDao = modDao;
    }

    public MsSearchResultDb load(int id) {
        return (MsSearchResultDb) queryForObject("MsSearchResult.select", id);
    }
    
    public List<Integer> loadResultIdsForRunSearch(int runSearchId) {
        return queryForList("MsSearchResult.selectResultIdsForRunSearch", runSearchId);
    }
    
    public List<Integer> loadResultIdsForSearchScanCharge(int runSearchId, int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("runSearchId", runSearchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return queryForList("MsSearchResult.selectResultIdsForRunSearchScanCharge", map);
    }
    
    public int save(int searchId, String searchDbName, MsSearchResult searchResult, int runSearchId, int scanId) {
        
        int resultId = saveResultOnly(searchResult, runSearchId, scanId);
        
        // save any protein matches
        for(MsSearchResultProtein protein: searchResult.getProteinMatchList()) {
            matchDao.save(protein, searchDbName, resultId);
        }
        
        // save any dynamic (residue and terminal) modifications for this result
        saveDynamicModsForResult(searchId, resultId, searchResult.getResultPeptide());
        
        return resultId;
    }
    
    public int saveResultOnly(MsSearchResult searchResult, int runSearchId, int scanId) {

        MsSearchResultSqlMapParam resultDb = new MsSearchResultSqlMapParam(runSearchId, scanId, searchResult);
        return saveAndReturnId("MsSearchResult.insert", resultDb);
    }

    private void saveDynamicModsForResult(int searchId, int resultId, MsSearchResultPeptide peptide) {
        
        saveDynamicResidueMods(searchId, resultId, peptide);
        saveDynamicTerminalMods(searchId, resultId, peptide);
    }

    private void saveDynamicResidueMods(int searchId, int resultId,
            MsSearchResultPeptide peptide) {
        for (MsResultResidueModIn mod: peptide.getResultDynamicResidueModifications()) {
            if (mod == null)
                continue;
            int modId = modDao.loadMatchingDynamicResidueModId(new MsResidueModificationImpl(mod, searchId));
            modDao.saveDynamicResidueModForResult(resultId, modId, mod.getModifiedPosition());
        }
    }
    
    private void saveDynamicTerminalMods(int searchId, int resultId,
            MsSearchResultPeptide peptide) {
        for (MsTerminalModificationIn mod: peptide.getDynamicTerminalModifications()) {
            if (mod == null)
                continue;
            int modId = modDao.loadMatchingDynamicTerminalModId(new MsTerminalModificationImpl(mod, searchId));
            modDao.saveDynamicTerminalModForResult(resultId, modId);
        }
    }
    
    public void delete(int resultId) {
        delete("MsSearchResult.delete", resultId);
    }

    /**
     * Convenience class for encapsulating runSearchId, scanId and search result
     */
    public class MsSearchResultSqlMapParam {

        private int runSearchId;
        private int scanId;
        private MsSearchResult result;
        
        public MsSearchResultSqlMapParam(int runSearchId, int scanId, MsSearchResult result) {
            this.runSearchId = runSearchId;
            this.scanId = scanId;
            this.result = result;
        }
        public int getRunSearchId() {
            return runSearchId;
        }
        public int getScanId() {
            return scanId;
        }
        public int getCharge() {
            return result.getCharge();
        }
        public ValidationStatus getValidationStatus() {
            return result.getValidationStatus();
        }
        public String getPeptideSequence() {
            return result.getResultPeptide().getPeptideSequence();
        }
        public String getPreResidueString() {
            return Character.toString(result.getResultPeptide().getPreResidue());
        }
        public String getPostResidueString() {
            return Character.toString(result.getResultPeptide().getPostResidue());
        }
        public int getSequenceLength() {
            return result.getResultPeptide().getSequenceLength();
        }
    }

    /**
     * Type handler for converting between ValidationType and SQL's CHAR type.
     */
    public static final class ValidationStatusTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToValidationStatus(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            ValidationStatus status = (ValidationStatus) parameter;
            if (status == null || status == ValidationStatus.UNKNOWN)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(Character.toString(status.getStatusChar()));
        }

        public Object valueOf(String statusStr) {
            return stringToValidationStatus(statusStr);
        }

        private Object stringToValidationStatus(String statusStr) {
            if (statusStr == null)
                return ValidationStatus.UNKNOWN;
            if (statusStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert \""+statusStr+"\" to ValidationStatus");
            ValidationStatus status = ValidationStatus.instance(statusStr.charAt(0));
            if (status == ValidationStatus.UNKNOWN)
                throw new IllegalArgumentException("Unrecognized validation status: "+statusStr);
            return status;
        }
    }
}
