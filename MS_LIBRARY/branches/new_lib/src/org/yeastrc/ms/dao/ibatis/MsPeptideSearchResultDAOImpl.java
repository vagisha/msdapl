package org.yeastrc.ms.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsPeptideSearchModDAO;
import org.yeastrc.ms.dao.MsPeptideSearchResultDAO;
import org.yeastrc.ms.dao.MsProteinMatchDAO;
import org.yeastrc.ms.domain.IMsSearchModification;
import org.yeastrc.ms.domain.IMsSearchResult;
import org.yeastrc.ms.domain.IMsSearchResultPeptide;
import org.yeastrc.ms.domain.IMsSearchResultProtein;
import org.yeastrc.ms.domain.IMsSearchModification.ModificationType;
import org.yeastrc.ms.domain.db.MsPeptideSearchDynamicMod;
import org.yeastrc.ms.domain.db.MsPeptideSearchResult;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsPeptideSearchResultDAOImpl extends BaseSqlMapDAO 
        implements MsPeptideSearchResultDAO<IMsSearchResult, MsPeptideSearchResult> {

    private MsProteinMatchDAO matchDao;
    private MsPeptideSearchModDAO modDao;
    
    public MsPeptideSearchResultDAOImpl(SqlMapClient sqlMap, MsProteinMatchDAO matchDao,
            MsPeptideSearchModDAO modDao) {
        super(sqlMap);
        this.matchDao = matchDao;
        this.modDao =  modDao;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsPeptideSearchResultDAO#load(int)
     */
    public MsPeptideSearchResult load(int id) {
        return (MsPeptideSearchResult) queryForObject("MsSearchResult.select", id);
    }
    
    public List<Integer> loadResultIdsForSearch(int searchId) {
        return queryForList("MsSearchResult.selectResultIdsForSearch", searchId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsPeptideSearchResultDAO#save(org.yeastrc.ms.dto.MsPeptideSearchResult)
     */
    public int save(IMsSearchResult searchResult, int searchId, int scanId) {
        
        MsSearchResultDb resultDb = new MsSearchResultDb(searchId, scanId, searchResult);
        int resultId = saveAndReturnId("MsSearchResult.insert", resultDb);
        
        // save any protein matches
        for(IMsSearchResultProtein protein: searchResult.getProteinMatchList()) {
            matchDao.save(protein, resultId);
        }
        
        List<MsPeptideSearchDynamicMod> dynaMods = modDao.loadDynamicModificationsForSearch(searchId);
        
        // save any dynamic modifications for this result
        saveDynamicModsForResult(resultId, searchResult.getResultPeptide(), dynaMods);
        
        return resultId;
    }

    // TODO make this more efficient
    void saveDynamicModsForResult(int resultId, IMsSearchResultPeptide peptide,
            List<MsPeptideSearchDynamicMod> dynaMods) {
        
        IMsSearchModification mod = null;
        for (int i = 0; i < peptide.getSequenceLength(); i++) {
            mod = peptide.getDynamicModificationAtIndex(0);
            // there may not be any modification at this position
            // or it may not be a dynamic modification -- skip over
            if (mod == null || mod.getModificationType() == ModificationType.STATIC)
                continue;
            modDao.saveDynamicModificationForSearchResult(resultId, getModificationId(dynaMods, mod), i);
        }
    }
    
    
    int getModificationId(List<MsPeptideSearchDynamicMod> modList, IMsSearchModification mod) {
        
        for (MsPeptideSearchDynamicMod dmod: modList) {
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
