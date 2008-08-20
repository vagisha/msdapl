/**
 * MsSearchModDAOImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationDb;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueModDb;
import org.yeastrc.ms.domain.search.MsResultDynamicTerminalModDb;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModificationDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchModificationDAOImpl extends BaseSqlMapDAO implements MsSearchModificationDAO {

    public MsSearchModificationDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public List<MsResidueModificationDb> loadStaticResidueModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectResidueStaticModsForSearch", searchId);
    }

    public void saveStaticResidueMod(MsResidueModification mod, int searchId) {
        MsResidueModSqlMapParam modDb = new MsResidueModSqlMapParam(searchId, mod);
        save("MsSearchMod.insertResidueStaticMod", modDb);
    }

    public void deleteStaticResidueModsForSearch(int searchId) {
        delete("MsSearchMod.deleteResidueStaticModsForSearch", searchId);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public List<MsResidueModificationDb> loadDynamicResidueModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectResidueDynaModsForSearch", searchId);
    }

    public int saveDynamicResidueMod(MsResidueModification mod, int searchId) {
        MsResidueModSqlMapParam modDb = new MsResidueModSqlMapParam(searchId, mod);
        return saveAndReturnId("MsSearchMod.insertResidueDynaMod", modDb);
    }

    public void deleteDynamicModificationsForSearch(int searchId) {
        delete("MsSearchMod.deleteResidueDynaModsForSearch", searchId);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public List<MsTerminalModificationDb> loadStaticTerminalModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectTerminalStaticModsForSearch", searchId);
    }

    public int saveStaticTerminalMod(MsTerminalModification mod, int searchId) {
        MsTerminalModSqlMapParam modDb = new MsTerminalModSqlMapParam(searchId, mod);
        return saveAndReturnId("MsSearchMod.insertTerminalStaticMod", modDb);
    }

    public void deleteStaticTerminalModsForSearch(int searchId) {
        delete("MsSearchMod.deleteTerminalStaticModsForSearch", searchId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public  List<MsTerminalModificationDb> loadDynamicTerminalModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectTerminalDynaModsForSearch", searchId);
    }

    public  int saveDynamicTerminalMod(MsTerminalModification mod, int searchId) {
        MsTerminalModSqlMapParam modDb = new MsTerminalModSqlMapParam(searchId, mod);
        return saveAndReturnId("MsSearchMod.insertTerminalDynaMod", modDb);
    }

    public  void deleteDynamicTerminalModsForSearch(int searchId) {
        delete("MsSearchMod.deleteTerminalDynaModsForSearch", searchId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC RESIDUE) associated with a search result
    //-------------------------------------------------------------------------------------------
    public List<MsResultDynamicResidueModDb> loadDynamicResidueModsForResult(
            int resultId) {
        return queryForList("MsSearchMod.selectDynaResidueModsForResult", resultId);
    }

    public void saveDynamicResidueModForResult(MsResultDynamicResidueMod mod, int resultId,
            int modificationId) {
        MsResultResidueModSqlMapParam modDb = new MsResultResidueModSqlMapParam(resultId, modificationId, mod.getModifiedPosition());
        save("MsSearchMod.insertResultDynaResidueMod", modDb);
    }
    
    public void saveAllDynamicResidueModsForResult(List<MsResultDynamicResidueModDb> modList) {
        if (modList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MsResultDynamicResidueModDb mod: modList) {
            values.append(",(");
            values.append(mod.getResultId());
            values.append(",");
            values.append(mod.getModificationId());
            values.append(",");
            values.append(mod.getModifiedPosition());
            values.append(")");
        }
        values.deleteCharAt(0);
        save("MsSearchMod.insertAllResultDynaResidueMods", values.toString());
    }

    public void deleteDynamicResidueModsForResult(int resultId) {
        delete("MsSearchMod.deleteDynaResidueModsForResult", resultId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC TERMINAL) associated with a search result
    //-------------------------------------------------------------------------------------------
    public List<MsResultDynamicTerminalModDb> loadDynamicTerminalModsForResult(
            int resultId) {
        return queryForList("MsSearchMod.selectDynaTerminalModsForResult", resultId);
    }

    public void saveDynamicTerminalModForResult(int resultId, int modificationId) {
        MsResultTerminalModSqlMapParam modDb = new MsResultTerminalModSqlMapParam(resultId, modificationId);
        save("MsSearchMod.insertResultDynaTerminalMod", modDb);
    }
    
    public void saveAllDynamicTerminalModsForResult(List<MsResultDynamicTerminalModDb> modList) {
        if (modList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MsResultDynamicTerminalModDb mod: modList) {
            values.append(",(");
            values.append(mod.getResultId());
            values.append(",");
            values.append(mod.getModificationId());
            values.append(")");
        }
        values.deleteCharAt(0);
        save("MsSearchMod.insertAllResultDynaTerminalMods", values.toString());
    }

    public void deleteDynamicTerminalModsForResult(int resultId) {
        delete("MsSearchMod.deleteDynaTerminalModsForResult", resultId);
    }


    //-------------------------------------------------------------------------------------------
    // Class for inserting data into the database.  
    // The save methods will be passed an object of type MsResidueModification, along with 
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
    public static final class MsResidueModSqlMapParam implements MsResidueModificationDb {

        private int searchId;
        private MsResidueModification mod;

        public MsResidueModSqlMapParam(int searchId, MsResidueModification mod) {
            this.searchId = searchId;
            this.mod = mod;
        }

        public int getSearchId() {
            return searchId;
        }

        public BigDecimal getModificationMass() {
            return mod.getModificationMass();
        }

        public int getId() {
            throw new UnsupportedOperationException("getId() method not supported by MsResidueModSqlMapParam");
        }
        
        public char getModifiedResidue() {
            return mod.getModifiedResidue();
        }

        public char getModificationSymbol() {
            return mod.getModificationSymbol();
        }
    }
    
    public static final class MsTerminalModSqlMapParam implements MsTerminalModificationDb {

        private int searchId;
        private MsTerminalModification mod;

        public MsTerminalModSqlMapParam(int searchId, MsTerminalModification mod) {
            this.searchId = searchId;
            this.mod = mod;
        }

        public int getSearchId() {
            return searchId;
        }

        public BigDecimal getModificationMass() {
            return mod.getModificationMass();
        }

        @Override
        public int getId() {
            throw new UnsupportedOperationException("getId() method not supported by MsTerminalModSqlMapParam");
        }
        
        public char getModificationSymbol() {
            return mod.getModificationSymbol();
        }

        @Override
        public Terminal getModifiedTerminal() {
            return mod.getModifiedTerminal();
        }
    }
    
    public static final class MsResultResidueModSqlMapParam {

        private int resultId;
        private int modId;
        private int modPosition;

        public MsResultResidueModSqlMapParam(int resultId, int modId, int modPosition) {
            this.resultId = resultId;
            this.modId = modId;
            this.modPosition = modPosition;
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
    
    public static final class MsResultTerminalModSqlMapParam {

        private int resultId;
        private int modId;

        public MsResultTerminalModSqlMapParam(int resultId, int modId) {
            this.resultId = resultId;
            this.modId = modId;
        }

        public int getResultId() {
            return resultId;
        }

        public int getModificationId() {
            return modId;
        }
    }
}
