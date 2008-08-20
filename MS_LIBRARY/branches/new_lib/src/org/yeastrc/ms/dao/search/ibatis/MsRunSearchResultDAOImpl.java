package org.yeastrc.ms.dao.search.ibatis;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsRunSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;
import org.yeastrc.ms.domain.search.MsRunSearchResult;
import org.yeastrc.ms.domain.search.MsRunSearchResultDb;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.ValidationStatus;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class MsRunSearchResultDAOImpl extends BaseSqlMapDAO 
        implements MsRunSearchResultDAO<MsRunSearchResult, MsRunSearchResultDb> {

    private MsSearchResultProteinDAO matchDao;
    private MsSearchModificationDAO modDao;
    
    public MsRunSearchResultDAOImpl(SqlMapClient sqlMap, MsSearchResultProteinDAO matchDao,
            MsSearchModificationDAO modDao) {
        super(sqlMap);
        this.matchDao = matchDao;
        this.modDao = modDao;
    }

    public MsRunSearchResultDb load(int id) {
        return (MsRunSearchResultDb) queryForObject("MsRunSearchResult.select", id);
    }
    
    public List<Integer> loadResultIdsForRunSearch(int searchId) {
        return queryForList("MsRunSearchResult.selectResultIdsForSearch", searchId);
    }
    
    public List<Integer> loadResultIdsForSearchScanCharge(int searchId, int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("searchId", searchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return queryForList("MsRunSearchResult.selectResultIdsForSearchScanCharge", map);
    }
    
    public int save(MsRunSearchResult searchResult, String searchDbName, int searchId, int scanId) {
        
        int resultId = saveResultOnly(searchResult, searchId, scanId);
        
        // save any protein matches
        for(MsSearchResultProtein protein: searchResult.getProteinMatchList()) {
            matchDao.save(protein, searchDbName, resultId);
        }
        
        // save any dynamic modifications for this result
        saveDynamicModsForResult(searchId, resultId, searchResult.getResultPeptide());
        
        return resultId;
    }
    
    public int saveResultOnly(MsRunSearchResult searchResult, int searchId, int scanId) {

        MsSearchResultSqlMapParam resultDb = new MsSearchResultSqlMapParam(searchId, scanId, searchResult);
        return saveAndReturnId("MsRunSearchResult.insert", resultDb);
    }

    void saveDynamicModsForResult(int searchId, int resultId, MsSearchResultPeptide peptide) {
        
        saveDynamicResidueMods(searchId, resultId, peptide);
        saveDynamicTerminalMods(searchId, resultId, peptide);
    }

    private void saveDynamicResidueMods(int searchId, int resultId,
            MsSearchResultPeptide peptide) {
        for (MsResultDynamicResidueMod mod: peptide.getResidueDynamicModifications()) {
            if (mod == null)
                continue;
            int modId = DynamicModLookupUtil.instance().getDynamicResidueModificationId(searchId, mod);
            modDao.saveDynamicResidueModForResult(mod, resultId, modId);
        }
    }
    
    private void saveDynamicTerminalMods(int searchId, int resultId,
            MsSearchResultPeptide peptide) {
        for (MsTerminalModification mod: peptide.getTerminalDynamicModifications()) {
            if (mod == null)
                continue;
            int modId = DynamicModLookupUtil.instance().getDynamicTerminalModificationId(searchId, mod);
            modDao.saveDynamicTerminalModForResult(resultId, modId);
        }
    }
    
    public void delete(int resultId) {
        delete("MsRunSearchResult.delete", resultId);
    }

    /**
     * Convenience class for encapsulating searchId, scanId and search result
     */
    public class MsSearchResultSqlMapParam implements MsRunSearchResult {

        private int runSearchId;
        private int scanId;
        private MsRunSearchResult result;
        
        public MsSearchResultSqlMapParam(int runSearchId, int scanId, MsRunSearchResult result) {
            this.runSearchId = runSearchId;
            this.scanId = scanId;
            this.result = result;
        }
        public int getSearchId() {
            return runSearchId;
        }
        public int getScanId() {
            return scanId;
        }

        public int getCharge() {
            return result.getCharge();
        }

        public List<MsSearchResultProtein> getProteinMatchList() {
            return result.getProteinMatchList();
        }

        public MsSearchResultPeptide getResultPeptide() {
            return result.getResultPeptide();
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
        @Override
        public int getScanNumber() {
            throw new UnsupportedOperationException("getScanNumber is not supported by MsSearchResultSqlMapParam");
        }
    }

    /**
     * Type handler for converting between ValidationType and SQL's CHAR type.
     */
    public static final class ValidationStatusTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            String statusStr = getter.getString();
            if (getter.wasNull() || statusStr.length() == 0)
                return ValidationStatus.UNKNOWN;
            return ValidationStatus.instance(statusStr.charAt(0));
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            ValidationStatus status = (ValidationStatus) parameter;
            if (status == null || status == ValidationStatus.UNKNOWN)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(Character.toString(status.getStatusChar()));
        }

        public Object valueOf(String s) {
            if (s == null || s.length() == 0)
                return ValidationStatus.UNKNOWN;
            return ValidationStatus.instance(s.charAt(0));
        }
        
    }
}
