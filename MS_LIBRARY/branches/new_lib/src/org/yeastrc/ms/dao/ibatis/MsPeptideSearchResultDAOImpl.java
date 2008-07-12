package org.yeastrc.ms.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsSearchModificationDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.MsSearchModification;
import org.yeastrc.ms.domain.MsSearchModificationDb;
import org.yeastrc.ms.domain.MsSearchResult;
import org.yeastrc.ms.domain.MsSearchResultDb;
import org.yeastrc.ms.domain.MsSearchResultPeptide;
import org.yeastrc.ms.domain.MsSearchResultProtein;
import org.yeastrc.ms.domain.MsSearchModification.ModificationType;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsPeptideSearchResultDAOImpl extends BaseSqlMapDAO 
        implements MsSearchResultDAO<MsSearchResult, MsSearchResultDb> {

    private MsSearchResultProteinDAO matchDao;
    private MsSearchModificationDAO modDao;
    
    public MsPeptideSearchResultDAOImpl(SqlMapClient sqlMap, MsSearchResultProteinDAO matchDao,
            MsSearchModificationDAO modDao) {
        super(sqlMap);
        this.matchDao = matchDao;
        this.modDao =  modDao;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsPeptideSearchResultDAO#load(int)
     */
    public MsSearchResultDb load(int id) {
        return (MsSearchResultDb) queryForObject("MsSearchResult.select", id);
    }
    
    public List<Integer> loadResultIdsForSearch(int searchId) {
        return queryForList("MsSearchResult.selectResultIdsForSearch", searchId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsPeptideSearchResultDAO#save(org.yeastrc.ms.dto.MsPeptideSearchResult)
     */
    public int save(MsSearchResult searchResult, int searchId, int scanId) {
        
        MsSearchResultSqlMapParam resultDb = new MsSearchResultSqlMapParam(searchId, scanId, searchResult);
        int resultId = saveAndReturnId("MsSearchResult.insert", resultDb);
        
        // save any protein matches
        for(MsSearchResultProtein protein: searchResult.getProteinMatchList()) {
            matchDao.save(protein, resultId);
        }
        
        List<MsSearchModificationDb> dynaMods = modDao.loadDynamicModificationsForSearch(searchId);
        
        // save any dynamic modifications for this result
        saveDynamicModsForResult(resultId, searchResult.getResultPeptide(), dynaMods);
        
        return resultId;
    }

    // TODO make this more efficient
    void saveDynamicModsForResult(int resultId, MsSearchResultPeptide peptide,
            List<MsSearchModificationDb> dynaMods) {
        
        for (MsSearchModification mod: peptide.getDynamicModifications()) {
            // there may not be any modification at this position
            // or it may not be a dynamic modification -- skip over
            if (mod == null || mod.getModificationType() == ModificationType.STATIC)
                continue;
            modDao.saveDynamicModificationForSearchResult(resultId, getModificationId(dynaMods, mod), i);
        }
    }
    
    
    int getModificationId(List<MsSearchModificationDb> modList, MsSearchModification mod) {
        
        for (MsSearchModificationDb dmod: modList) {
            if (dmod.getModificationMass().doubleValue() == mod.getModificationMass().doubleValue() &&
                dmod.getModifiedResidue() == mod.getModifiedResidue())
                return dmod.getId();
        }
        throw new RuntimeException("No dynamic modification found for modificaiton: "
                +mod.getModifiedResidue()+"; "+mod.getModificationMass());
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsPeptideSearchResultDAO#delete(int)
     */
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
