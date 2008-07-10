/**
 * MsSearchModDAOImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.MsPeptideSearchModDAO;
import org.yeastrc.ms.domain.IMsSearchModification;
import org.yeastrc.ms.domain.MsPeptideSearchDynamicMod;
import org.yeastrc.ms.domain.MsPeptideSearchStaticMod;
import org.yeastrc.ms.domain.MsSearchResultDynamicMod;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsPeptideSearchModDAOImpl extends BaseSqlMapDAO implements MsPeptideSearchModDAO {

    public MsPeptideSearchModDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search
    //-------------------------------------------------------------------------------------------
    public List<MsPeptideSearchStaticMod> loadStaticModificationsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectStaticModsForSearch", searchId);
    }
    
    public void saveStaticModification(IMsSearchModification mod, int searchId) {
        MsPeptideSearchModDB modDb = new MsPeptideSearchModDB();
        modDb.mod = mod;
        modDb.searchId = searchId;
        save("MsSearchMod.insertStaticMod", modDb);
    }
    
    public void deleteStaticModificationsForSearch(int searchId) {
        delete("MsSearchMod.deleteStaticModsForSearch", searchId);
    }
    
    
    public List<MsPeptideSearchDynamicMod> loadDynamicModificationsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectDynaModsForSearch", searchId);
    }
    
    public int saveDynamicModification(IMsSearchModification mod, int searchId) {
        MsPeptideSearchModDB modDb = new MsPeptideSearchModDB();
        modDb.mod = mod;
        modDb.searchId = searchId;
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
    public List<MsSearchResultDynamicMod> loadDynamicModificationsForSearchResult(
            int resultId) {
        return queryForList("MsSearchMod.selectDynaModsForSearchResult", resultId);
    }

    public void saveDynamicModificationForSearchResult(int resultId,
            int modificationId, int position) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("resultId", resultId);
        map.put("modId", modificationId);
        map.put("position", position);
        save("MsSearchMod.insertResultDynaMod", map);
    }

    public void deleteDynamicModificationsForResult(int resultId) {
        delete("MsSearchMod.deleteDynaModsForSearchResult", resultId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Class for inserting data into the database.  
    // The save methods will be passed an object of type IMsSearchModification, along with 
    // a searchId.  We have 3 options: 
    // 1. Use inline parameters
    // 2. Use a parameter map of type java.util.Map
    // 3. Create a class that holds the IMsSearchModification and searchId and use this as the
    // param class.  This 3rd options is used since using a bean in a parameter map gives us
    // helps us catch a mismatch in the bean property and sql param map when the map is loaded,
    // rather than when the map us used. With a Map iBatis has no way of detecting a name mismatch
    // since a Map is built at runtime rather than compile time. 
    //-------------------------------------------------------------------------------------------
    public static final class MsPeptideSearchModDB {
        private IMsSearchModification mod;
        private int searchId;
        /**
         * @return the mod
         */
        public IMsSearchModification getMod() {
            return mod;
        }
        /**
         * @return the searchId
         */
        public int getSearchId() {
            return searchId;
        }
    }
}
