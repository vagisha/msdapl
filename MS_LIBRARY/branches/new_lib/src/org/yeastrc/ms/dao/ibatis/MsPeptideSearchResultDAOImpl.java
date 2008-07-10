package org.yeastrc.ms.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsPeptideSearchModDAO;
import org.yeastrc.ms.dao.MsPeptideSearchResultDAO;
import org.yeastrc.ms.dao.MsProteinMatchDAO;
import org.yeastrc.ms.dto.IMsSearchResultProtein;
import org.yeastrc.ms.dto.MsPeptideSearchResult;
import org.yeastrc.ms.dto.MsProteinMatch;
import org.yeastrc.ms.dto.MsSearchResultDynamicMod;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsPeptideSearchResultDAOImpl extends BaseSqlMapDAO implements MsPeptideSearchResultDAO {

    
    public MsPeptideSearchResultDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
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
    public int save(MsPeptideSearchResult searchResult) {
        
        int resultId = saveAndReturnId("MsSearchResult.insert", searchResult);
        
        // save any protein matches
        MsProteinMatchDAO matchDao = DAOFactory.instance().getMsProteinMatchDAO();
        List<MsProteinMatch> resultProteins = searchResult.getProteinMatchList();
        for(IMsSearchResultProtein protein: resultProteins) {
            matchDao.save(protein, resultId);
        }
        
        // save any dynamic modifications for this result
//        MsPeptideSearchModDAO modDao = DAOFactory.instance().getMsSearchModDAO();
//        List<MsSearchResultDynamicMod> dynaMods = searchResult.getResultPeptide().get
//        for (MsSearchResultDynamicMod mod: dynaMods) {
//            modDao.saveDynamicModificationForSearchResult(resultId, 
//                            mod.getModificationId(), 
//                            mod.getModifiedPosition());
//        }
        
        return resultId;
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsPeptideSearchResultDAO#delete(int)
     */
    public void delete(int resultId) {
        
        delete("MsSearchResult.delete", resultId);
        
        // delete any protein matches for this result
        MsProteinMatchDAO matchDao = DAOFactory.instance().getMsProteinMatchDAO();
        matchDao.delete(resultId);
        
        // delete any dynamic modifications associated with this result
        MsPeptideSearchModDAO modDao = DAOFactory.instance().getMsSearchModDAO();
        modDao.deleteDynamicModificationsForResult(resultId);
    }

    public void deleteResultsForSearch(int searchId) {
       List<Integer> resultIds = loadResultIdsForSearch(searchId);
       for (Integer id: resultIds) 
           delete(id);
    }

   
    
}
