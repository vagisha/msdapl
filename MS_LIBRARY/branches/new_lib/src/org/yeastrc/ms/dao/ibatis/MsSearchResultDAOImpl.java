package org.yeastrc.ms.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsSearchModificationDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.MsSearchResult;
import org.yeastrc.ms.domain.MsSearchResultDb;
import org.yeastrc.ms.domain.MsSearchResultModification;
import org.yeastrc.ms.domain.MsSearchResultPeptide;
import org.yeastrc.ms.domain.MsSearchResultProtein;
import org.yeastrc.ms.domain.MsSearchModification.ModificationType;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsSearchResultDAOImpl extends BaseSqlMapDAO 
        implements MsSearchResultDAO<MsSearchResult, MsSearchResultDb> {

    private MsSearchResultProteinDAO matchDao;
    private MsSearchModificationDAO modDao;
    
    public MsSearchResultDAOImpl(SqlMapClient sqlMap, MsSearchResultProteinDAO matchDao,
            MsSearchModificationDAO modDao) {
        super(sqlMap);
        this.matchDao = matchDao;
        this.modDao =  modDao;
    }

    public MsSearchResultDb load(int id) {
        return (MsSearchResultDb) queryForObject("MsSearchResult.select", id);
    }
    
    public List<Integer> loadResultIdsForSearch(int searchId) {
        return queryForList("MsSearchResult.selectResultIdsForSearch", searchId);
    }
    
    public int save(MsSearchResult searchResult, int searchId, int scanId) {
        
        MsSearchResultSqlMapParam resultDb = new MsSearchResultSqlMapParam(searchId, scanId, searchResult);
        int resultId = saveAndReturnId("MsSearchResult.insert", resultDb);
        
        // save any protein matches
        for(MsSearchResultProtein protein: searchResult.getProteinMatchList()) {
            matchDao.save(protein, resultId);
        }
        
        // save any dynamic modifications for this result
        saveDynamicModsForResult(searchId, resultId, searchResult.getResultPeptide());
        
        return resultId;
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
    
}
