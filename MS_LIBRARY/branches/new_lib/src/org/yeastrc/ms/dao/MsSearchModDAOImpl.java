/**
 * MsSearchModDAOImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dto.MsSearchDynamicMod;
import org.yeastrc.ms.dto.MsSearchMod;
import org.yeastrc.ms.dto.MsSearchResultDynamicMod;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchModDAOImpl extends BaseSqlMapDAO implements MsSearchModDAO {

    public MsSearchModDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsSearchModDAO#loadStaticModificationsForSearch(int)
     */
    public List<MsSearchMod> loadStaticModificationsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectStaticModsForSearch", searchId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsSearchModDAO#saveStaticModification(org.yeastrc.ms.dto.MsSearchMod)
     */
    public void saveStaticModification(MsSearchMod mod) {
        save("MsSearchMod.insertStaticMod", mod);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsSearchModDAO#deleteStaticModificationsForSearch(int)
     */
    public void deleteStaticModificationsForSearch(int searchId) {
        delete("MsSearchMod.deleteStaticModsForSearch", searchId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsSearchModDAO#loadDynamicModificationsForSearch(int)
     */
    public List<MsSearchDynamicMod> loadDynamicModificationsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectDynaModsForSearch", searchId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsSearchModDAO#saveDynamicModification(org.yeastrc.ms.dto.MsSearchDynamicMod)
     */
    public int saveDynamicModification(MsSearchDynamicMod mod) {
        return saveAndReturnId("MsSearchMod.insertDynaMod", mod);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsSearchModDAO#deleteDynamicModificationsForSearch(int)
     */
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
}
