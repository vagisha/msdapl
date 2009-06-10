/**
 * MsSearchModificationUploadDAOIbatisImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.ibatis;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueModIds;
import org.yeastrc.ms.domain.search.MsResultTerminalModIds;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.upload.dao.search.MsSearchModificationUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class MsSearchModificationUploadDAOIbatisImpl extends BaseSqlMapDAO implements MsSearchModificationUploadDAO {

    public MsSearchModificationUploadDAOIbatisImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public void saveStaticResidueMod(MsResidueModification mod) {
        save("MsSearchMod.insertStaticResidueMod", mod);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC RESIDUE) 
    //-------------------------------------------------------------------------------------------
    public List<MsResidueModification> loadDynamicResidueModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectDynamicResidueModsForSearch", searchId);
    }

    public int saveDynamicResidueMod(MsResidueModification mod) {
        return saveAndReturnId("MsSearchMod.insertDynamicResidueMod", mod);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (STATIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public void saveStaticTerminalMod(MsTerminalModification mod) {
        save("MsSearchMod.insertStaticTerminalMod", mod);
    }

    //-------------------------------------------------------------------------------------------
    // Modifications associated with a search (DYNAMIC TERMINAL) 
    //-------------------------------------------------------------------------------------------
    public  List<MsTerminalModification> loadDynamicTerminalModsForSearch(int searchId) {
        return queryForList("MsSearchMod.selectDynamicTerminalModsForSearch", searchId);
    }

    public  int saveDynamicTerminalMod(MsTerminalModification mod) {
        return saveAndReturnId("MsSearchMod.insertDynamicTerminalMod", mod);
    }
    
    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC RESIDUE) associated with a search result
    //-------------------------------------------------------------------------------------------
    public void saveAllDynamicResidueModsForResult(List<MsResultResidueModIds> modList) {
        if (modList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MsResultResidueModIds mod: modList) {
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

    //-------------------------------------------------------------------------------------------
    // Modifications (DYNAMIC TERMINAL) associated with a search result
    //-------------------------------------------------------------------------------------------
    public void saveAllDynamicTerminalModsForResult(List<MsResultTerminalModIds> modList) {
        if (modList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MsResultTerminalModIds mod: modList) {
            values.append(",(");
            values.append(mod.getResultId() == 0 ? "NULL" : mod.getResultId());
            values.append(",");
            values.append(mod.getModificationId() == 0 ? "NULL" : mod.getModificationId());
            values.append(")");
        }
        values.deleteCharAt(0);
        save("MsSearchMod.insertAllResultDynamicTerminalMods", values.toString());
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
            if (status == null || status.charValue() == 0)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(status.toString());
        }

        public Object valueOf(String s) {
            return stringToChar(s);
        }
        
        private Character stringToChar(String charStr) {
            // if charStr is NULL the value (\u0000) will be used for modificationSymbol
            if (charStr == null)
                return Character.valueOf('\u0000');
            if (charStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert \""+charStr+"\" to Character");
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
