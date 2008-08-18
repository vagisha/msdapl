package org.yeastrc.ms.dao.ibatis;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.MsSearchModificationDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.search.MsRunSearchResult;
import org.yeastrc.ms.domain.search.MsRunSearchResultDb;
import org.yeastrc.ms.domain.search.MsSearchResultModification;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.MsSearchModification.ModificationType;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class MsSearchResultDAOImpl extends BaseSqlMapDAO 
        implements MsSearchResultDAO<MsRunSearchResult, MsRunSearchResultDb> {

    private MsSearchResultProteinDAO matchDao;
    private MsSearchModificationDAO modDao;
    
    public MsSearchResultDAOImpl(SqlMapClient sqlMap, MsSearchResultProteinDAO matchDao,
            MsSearchModificationDAO modDao) {
        super(sqlMap);
        this.matchDao = matchDao;
        this.modDao =  modDao;
    }

    public MsRunSearchResultDb load(int id) {
        return (MsRunSearchResultDb) queryForObject("MsSearchResult.select", id);
    }
    
    public List<Integer> loadResultIdsForSearch(int searchId) {
        return queryForList("MsSearchResult.selectResultIdsForSearch", searchId);
    }
    
    public List<Integer> loadResultIdsForSearchScanCharge(int searchId, int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("searchId", searchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return queryForList("MsSearchResult.selectResultIdsForSearchScanCharge", map);
    }
    
    public int save(MsRunSearchResult searchResult, int searchId, int scanId) {
        
        int resultId = saveResultOnly(searchResult, searchId, scanId);
        
        // save any protein matches
        for(MsSearchResultProtein protein: searchResult.getProteinMatchList()) {
            matchDao.save(protein, resultId);
        }
        
        // save any dynamic modifications for this result
        saveDynamicModsForResult(searchId, resultId, searchResult.getResultPeptide());
        
        return resultId;
    }
    
    public int saveResultOnly(MsRunSearchResult searchResult, int searchId, int scanId) {

        MsSearchResultSqlMapParam resultDb = new MsSearchResultSqlMapParam(searchId, scanId, searchResult);
        return saveAndReturnId("MsSearchResult.insert", resultDb);
    }

    void saveDynamicModsForResult(int searchId, int resultId, MsSearchResultPeptide peptide) {
        
        for (MsSearchResultModification mod: peptide.getDynamicModifications()) {
            if (mod == null || mod.getModificationType() == ModificationType.STATIC)
                continue;
            int modId = DynamicModLookupUtil.instance().getDynamicModificationId(searchId, 
                    mod.getModifiedResidue(), mod.getModificationMass());
            modDao.saveDynamicModificationForSearchResult(mod, resultId, modId);
        }
    }
    
    public void delete(int resultId) {
        
        // delete any protein matches for this result
        matchDao.delete(resultId);
        
        // delete any dynamic modifications associated with this result
        modDao.deleteDynamicModificationsForResult(resultId);
        
        delete("MsSearchResult.delete", resultId);
    }

    public void deleteResultsForSearch(int searchId) {
       List<Integer> resultIds = loadResultIdsForSearch(searchId);
       for (Integer id: resultIds) 
           delete(id);
    }
    
    
    /**
     * Convenience class for encapsulating searchId, scanId and search result
     */
    public class MsSearchResultSqlMapParam implements MsRunSearchResult {

        private int searchId;
        private int scanId;
        private MsRunSearchResult result;
        
        public MsSearchResultSqlMapParam(int searchId, int scanId, MsRunSearchResult result) {
            this.searchId = searchId;
            this.scanId = scanId;
            this.result = result;
        }
        public int getSearchId() {
            return searchId;
        }
        public int getScanId() {
            return scanId;
        }


        public BigDecimal getCalculatedMass() {
            return result.getCalculatedMass();
        }

        public int getCharge() {
            return result.getCharge();
        }

        public int getNumIonsMatched() {
            return result.getNumIonsMatched();
        }

        public int getNumIonsPredicted() {
            return result.getNumIonsPredicted();
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
