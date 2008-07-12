/**
 * MsSearchModDAOImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsSearchModificationDAO;
import org.yeastrc.ms.domain.MsSearchModification;
import org.yeastrc.ms.domain.MsSearchModificationDb;
import org.yeastrc.ms.domain.MsSearchResultDynamicModDb;
import org.yeastrc.ms.domain.MsSearchResultModification;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchModificationDAOImpl extends BaseSqlMapDAO implements MsSearchModificationDAO {

    public MsSearchModificationDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search
    //-------------------------------------------------------------------------------------------
    public List<MsSearchModificationDb> loadStaticModificationsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectStaticModsForSearch", searchId);
    }
    
    public void saveStaticModification(MsSearchModification mod, int searchId) {
        MsSearchModSqlMapParam modDb = new MsSearchModSqlMapParam(searchId, mod);
        save("MsSearchMod.insertStaticMod", modDb);
    }
    
    public void deleteStaticModificationsForSearch(int searchId) {
        delete("MsSearchMod.deleteStaticModsForSearch", searchId);
    }
    
    
    public List<MsSearchModificationDb> loadDynamicModificationsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectDynaModsForSearch", searchId);
    }
    
    public int saveDynamicModification(MsSearchModification mod, int searchId) {
        MsSearchModSqlMapParam modDb = new MsSearchModSqlMapParam(searchId, mod);
        return saveAndReturnId("MsSearchMod.insertDynaMod", modDb);
    }
    
    public void deleteDynamicModificationsForSearch(int searchId) {
        List<Integer> modIds = queryForList("MsSearchMod.selectDynaModIdsForSearch", searchId);
        // delete entries linking these dynamic modifications to search results
        deleteResultDynaModsForModIds(modIds);
        // delete the dynamic modifications
        delete("MsSearchMod.deleteDynaModsForSearch", searchId);
    }
    
    private void deleteResultDynaModsForModIds(List<Integer> modIds) {
        if (modIds.size() > 0) {
            StringBuilder buf = new StringBuilder();
            buf.append("(");
            for (Integer id: modIds) {
                buf.append(id);
                buf.append(",");
            }
            buf.deleteCharAt(buf.length() - 1); // remove the last comma
            buf.append(")");
            String idListString = buf.toString();
            delete("MsSearchMod.deleteDynaModsForModIds", idListString);
        }
    }

    
    //-------------------------------------------------------------------------------------------
    // Modifications (dynamic only) associated with a search result
    //-------------------------------------------------------------------------------------------
    public List<MsSearchResultDynamicModDb> loadDynamicModificationsForSearchResult(
            int resultId) {
        return queryForList("MsSearchMod.selectDynaModsForSearchResult", resultId);
    }

    public void saveDynamicModificationForSearchResult(MsSearchResultModification mod, int resultId,
            int modificationId) {
        MsSearchResultModSqlMapParam modDb = new MsSearchResultModSqlMapParam(resultId, modificationId, mod);
        save("MsSearchMod.insertResultDynaMod", modDb);
    }

    public void deleteDynamicModificationsForResult(int resultId) {
        delete("MsSearchMod.deleteDynaModsForSearchResult", resultId);
    }
   
}
