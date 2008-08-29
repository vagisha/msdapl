/**
 * MsSearchModDAOImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.math.BigDecimal;
import java.sql.SQLException;
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
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

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
        return queryForList("MsSearchMod.selectStaticResidueModsForSearch", searchId);
    }

    public void saveStaticResidueMod(MsResidueModification mod, int searchId) {
        MsResidueModSqlMapParam modDb = new MsResidueModSqlMapParam(searchId, mod);
        save("MsSearchMod.insertStaticResidueMod", modDb);
    }

    public void deleteStaticResidueModsForSearch(int searchId) {
        delete("MsSearchMod.deleteStaticResidueModsForSearch", searchId);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public List<MsResidueModificationDb> loadDynamicResidueModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectDynamicResidueModsForSearch", searchId);
    }

    public int saveDynamicResidueMod(MsResidueModification mod, int searchId) {
        MsResidueModSqlMapParam modDb = new MsResidueModSqlMapParam(searchId, mod);
        return saveAndReturnId("MsSearchMod.insertDynamicResidueMod", modDb);
    }

    public void deleteDynamicModificationsForSearch(int searchId) {
        delete("MsSearchMod.deleteDynamicResidueModsForSearch", searchId);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public List<MsTerminalModificationDb> loadStaticTerminalModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectStaticTerminalModsForSearch", searchId);
    }

    public void saveStaticTerminalMod(MsTerminalModification mod, int searchId) {
        MsTerminalModSqlMapParam modDb = new MsTerminalModSqlMapParam(searchId, mod);
        save("MsSearchMod.insertStaticTerminalMod", modDb);
    }

    public void deleteStaticTerminalModsForSearch(int searchId) {
        delete("MsSearchMod.deleteStaticTerminalModsForSearch", searchId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public  List<MsTerminalModificationDb> loadDynamicTerminalModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectDynamicTerminalModsForSearch", searchId);
    }

    public  int saveDynamicTerminalMod(MsTerminalModification mod, int searchId) {
        MsTerminalModSqlMapParam modDb = new MsTerminalModSqlMapParam(searchId, mod);
        return saveAndReturnId("MsSearchMod.insertDynamicTerminalMod", modDb);
    }

    public  void deleteDynamicTerminalModsForSearch(int searchId) {
        delete("MsSearchMod.deleteDynamicTerminalModsForSearch", searchId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC RESIDUE) associated with a search result
    //-------------------------------------------------------------------------------------------
    public List<MsResultDynamicResidueModDb> loadDynamicResidueModsForResult(
            int resultId) {
        return queryForList("MsSearchMod.selectDynamicResidueModsForResult", resultId);
    }

    public void saveDynamicResidueModForResult(MsResultDynamicResidueMod mod, int resultId,
            int modificationId) {
        MsResultResidueModSqlMapParam modDb = new MsResultResidueModSqlMapParam(resultId, modificationId, mod.getModifiedPosition());
        save("MsSearchMod.insertResultDynamicResidueMod", modDb);
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
        save("MsSearchMod.insertAllResultDynamicResidueMods", values.toString());
    }

    public void deleteDynamicResidueModsForResult(int resultId) {
        delete("MsSearchMod.deleteDynamicResidueModsForResult", resultId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC TERMINAL) associated with a search result
    //-------------------------------------------------------------------------------------------
    public List<MsResultDynamicTerminalModDb> loadDynamicTerminalModsForResult(
            int resultId) {
        return queryForList("MsSearchMod.selectDynamicTerminalModsForResult", resultId);
    }

    public void saveDynamicTerminalModForResult(int resultId, int modificationId) {
        MsResultTerminalModSqlMapParam modDb = new MsResultTerminalModSqlMapParam(resultId, modificationId);
        save("MsSearchMod.insertResultDynamicTerminalMod", modDb);
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
        save("MsSearchMod.insertAllResultDynamicTerminalMods", values.toString());
    }

    public void deleteDynamicTerminalModsForResult(int resultId) {
        delete("MsSearchMod.deleteDynamicTerminalModsForResult", resultId);
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
    
    /**
     * Type handler for converting between Java's Character and SQL's CHAR type.
     */
    public static final class CharTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToChar(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            Character status = (Character) parameter;
            if (status == null)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(status.toString());
        }

        public Object valueOf(String s) {
            return stringToChar(s);
        }
        
        private Character stringToChar(String charStr) {
            // if charStr is NULL the value (\u0000) will be used for modificationSymbol
            if (charStr == null || charStr.length() == 0)
                return Character.valueOf('\u0000');
            if (charStr.length() > 1)
                throw new IllegalArgumentException("Cannot convert "+charStr+" to Character");
            return Character.valueOf(charStr.charAt(0));
        }
    }
    
    /**
     * Type handler for converting between MsTerminalModification.Terminal and SQL's CHAR type.
     */
    public static final class TerminalTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToTerminal(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            Terminal terminal = (Terminal) parameter;
            if (terminal == null)
                throw new IllegalArgumentException("Terminal value for terminal modification cannot be null");
//                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(String.valueOf(terminal.toChar()));
        }

        public Object valueOf(String s) {
            return stringToTerminal(s);
        }
        
        private Terminal stringToTerminal(String termStr) {
            if (termStr == null)
                throw new IllegalArgumentException("String representing MsTerminalModification.Terminal cannot be null");
            if (termStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert "+termStr+" to Terminal");
            Terminal term = Terminal.instance(Character.valueOf(termStr.charAt(0)));
            if (term == null)
                throw new IllegalArgumentException("Invalid Terminal value: "+termStr);
            return term;
        }
    }
}
