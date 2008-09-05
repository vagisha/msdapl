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
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.ResultModIdentifier;
import org.yeastrc.ms.domain.search.ResultResidueModIdentifier;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.impl.ResultModIdentifierImpl;
import org.yeastrc.ms.domain.search.impl.ResultResidueModIdentifierImpl;

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
    public List<MsResidueModification> loadStaticResidueModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectStaticResidueModsForSearch", searchId);
    }

    public void saveStaticResidueMod(MsResidueModificationIn mod, int searchId) {
        MsResidueModSqlMapParam modDb = new MsResidueModSqlMapParam(searchId, mod);
        save("MsSearchMod.insertStaticResidueMod", modDb);
    }

    public void deleteStaticResidueModsForSearch(int searchId) {
        delete("MsSearchMod.deleteStaticResidueModsForSearch", searchId);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public List<MsResidueModification> loadDynamicResidueModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectDynamicResidueModsForSearch", searchId);
    }

    public int saveDynamicResidueMod(MsResidueModificationIn mod, int searchId) {
        MsResidueModSqlMapParam modDb = new MsResidueModSqlMapParam(searchId, mod);
        return saveAndReturnId("MsSearchMod.insertDynamicResidueMod", modDb);
    }

    public void deleteDynamicResidueModsForSearch(int searchId) {
        delete("MsSearchMod.deleteDynamicResidueModsForSearch", searchId);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public List<MsTerminalModification> loadStaticTerminalModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectStaticTerminalModsForSearch", searchId);
    }

    public void saveStaticTerminalMod(MsTerminalModificationIn mod, int searchId) {
        MsTerminalModSqlMapParam modDb = new MsTerminalModSqlMapParam(searchId, mod);
        save("MsSearchMod.insertStaticTerminalMod", modDb);
    }

    public void deleteStaticTerminalModsForSearch(int searchId) {
        delete("MsSearchMod.deleteStaticTerminalModsForSearch", searchId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public  List<MsTerminalModification> loadDynamicTerminalModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectDynamicTerminalModsForSearch", searchId);
    }

    public  int saveDynamicTerminalMod(MsTerminalModificationIn mod, int searchId) {
        MsTerminalModSqlMapParam modDb = new MsTerminalModSqlMapParam(searchId, mod);
        return saveAndReturnId("MsSearchMod.insertDynamicTerminalMod", modDb);
    }

    public  void deleteDynamicTerminalModsForSearch(int searchId) {
        delete("MsSearchMod.deleteDynamicTerminalModsForSearch", searchId);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC RESIDUE) associated with a search result
    //-------------------------------------------------------------------------------------------
    public List<MsResultDynamicResidueMod> loadDynamicResidueModsForResult(
            int resultId) {
        return queryForList("MsSearchMod.selectDynamicResidueModsForResult", resultId);
    }

    @Override
    public int loadMatchingDynamicResidueModId(int searchId,
            MsResidueModificationIn mod) {
        MsResidueModSqlMapParam modDb = new MsResidueModSqlMapParam(searchId, mod);
        Integer modId = (Integer)queryForObject("MsSearchMod.selectMatchingDynaResModId", modDb);
        if (modId == null)
            return 0;
        return modId;
    }
    
    public void saveDynamicResidueModForResult(int resultId,
            int modificationId, int modifiedPosition) {
        ResultResidueModIdentifierImpl modDb = new ResultResidueModIdentifierImpl(resultId, modificationId, modifiedPosition);
        this.saveDynamicResidueModForResult(modDb);
    }
    
    public void saveDynamicResidueModForResult(ResultResidueModIdentifier modIdentifier) {
        save("MsSearchMod.insertResultDynamicResidueMod", modIdentifier);
    }
    
    public void saveAllDynamicResidueModsForResult(List<ResultResidueModIdentifier> modList) {
        if (modList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (ResultResidueModIdentifier mod: modList) {
            values.append(",(");
            values.append(mod.getResultId() == 0 ? "NULL" : mod.getResultId());
            values.append(",");
            values.append(mod.getModificationId() == 0 ? "NULL" : mod.getModificationId());
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
    public List<MsResultTerminalMod> loadDynamicTerminalModsForResult(
            int resultId) {
        return queryForList("MsSearchMod.selectDynamicTerminalModsForResult", resultId);
    }

    @Override
    public int loadMatchingDynamicTerminalModId(
            int searchId, MsTerminalModificationIn mod) {
        MsTerminalModSqlMapParam modDb = new MsTerminalModSqlMapParam(searchId, mod);
        Integer modId = (Integer)queryForObject("MsSearchMod.selectMatchingDynaTermModId", modDb);
        if (modId == null)
            return 0;
        return modId;
    }
    
    public void saveDynamicTerminalModForResult(int resultId, int modificationId) {
        ResultModIdentifierImpl modDb = new ResultModIdentifierImpl(resultId, modificationId);
        this.saveDynamicTerminalModForResult(modDb);
    }
    
    public void saveDynamicTerminalModForResult(ResultModIdentifier modIdentifier) {
        save("MsSearchMod.insertResultDynamicTerminalMod", modIdentifier);
    }
    
    public void saveAllDynamicTerminalModsForResult(List<ResultModIdentifier> modList) {
        if (modList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (ResultModIdentifier mod: modList) {
            values.append(",(");
            values.append(mod.getResultId() == 0 ? "NULL" : mod.getResultId());
            values.append(",");
            values.append(mod.getModificationId() == 0 ? "NULL" : mod.getModificationId());
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
    public static final class MsResidueModSqlMapParam implements MsResidueModification {

        private int searchId;
        private MsResidueModificationIn mod;

        public MsResidueModSqlMapParam(int searchId, MsResidueModificationIn mod) {
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
    
    public static final class MsTerminalModSqlMapParam implements MsTerminalModification {

        private int searchId;
        private MsTerminalModificationIn mod;

        public MsTerminalModSqlMapParam(int searchId, MsTerminalModificationIn mod) {
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
