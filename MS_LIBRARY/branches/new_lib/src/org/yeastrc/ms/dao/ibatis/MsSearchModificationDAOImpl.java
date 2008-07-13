/**
 * MsSearchModDAOImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.math.BigDecimal;
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




    //-------------------------------------------------------------------------------------------
    // Class for inserting data into the database.  
    // The save methods will be passed an object of type MsSearchModification, along with 
    // a searchId.  We have 3 options: 
    // 1. Use inline parameters
    // 2. Use a parameter map of type java.util.Map
    // 3. Create a class that holds the MsSearchModification and searchId and use this as the
    // param class.  We use the 3rd options because using a bean in a parameter map helps us 
    // catch any mismatches between the bean property and sql param map when the map is loaded,
    // rather than when the map is first used. With a java.util.Map iBatis has no way of detecting 
    // a name mismatch since a Map is built at runtime rather than compile time. 
    // Usnig a bean is also supposed to have better performance.
    //-------------------------------------------------------------------------------------------
    public class MsSearchModSqlMapParam {

        private int searchId;
        private char modResidue;
        private char modSymbol;
        private BigDecimal modMass;

        public MsSearchModSqlMapParam(int searchId, MsSearchModification mod) {
            this.searchId = searchId;
            this.modResidue = mod.getModifiedResidue();
            this.modSymbol = mod.getModificationSymbol();
            this.modMass = mod.getModificationMass();
        }

        public int getSearchId() {
            return searchId;
        }

        public BigDecimal getModificationMass() {
            return modMass;
        }

        public String getModificationSymbolString() {
            return Character.toString(modSymbol);
        }

        public String getModifiedResidueString() {
            return Character.toString(modResidue);
        }
    }
    
    public class MsSearchResultModSqlMapParam {

        private int resultId;
        private int modId;
        private int modPosition;

        public MsSearchResultModSqlMapParam(int resultId, int modId, MsSearchResultModification mod) {
            this.resultId = resultId;
            this.modId = modId;
            this.modPosition = mod.getModifiedPosition();
        }

        public int getResultId() {
            return resultId;
        }

        public int getModificationId() {
            return modId;
        }

        public int getModifiedPosition() {
            return modPosition;
        }
    }
}
