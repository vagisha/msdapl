/**
 * MsPeptideSearchDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.general.MsEnzymeDAO;
import org.yeastrc.ms.dao.general.MsEnzymeDAO.EnzymeProperties;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.SearchProgram;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class MsSearchDAOImpl extends BaseSqlMapDAO implements MsSearchDAO<MsSearch, MsSearchDb> {

    private MsSearchDatabaseDAO seqDbDao;
    private MsSearchModificationDAO modDao;
    private MsEnzymeDAO enzymeDao;
    
    public MsSearchDAOImpl(SqlMapClient sqlMap, 
            MsSearchDatabaseDAO seqDbDao,
            MsSearchModificationDAO modDao,
            MsEnzymeDAO enzymeDao) {
        super(sqlMap);
        this.seqDbDao = seqDbDao;
        this.modDao = modDao;
        this.enzymeDao = enzymeDao;
    }
    
    public MsSearchDb loadSearch(int searchId) {
        return (MsSearchDb) queryForObject("MsSearch.select", searchId);
    }
    
    public int saveSearch(MsSearch search, int sequenceDatabaseId) {
        
        final int searchId = saveAndReturnId("MsSearch.insert", search);
        
        try {
            // save any database information associated with the search 
            for (MsSearchDatabaseIn seqDb: search.getSearchDatabases()) {
                seqDbDao.saveSearchDatabase(seqDb, searchId, sequenceDatabaseId);
            }

            // save any static residue modifications used for the search
            for (final MsResidueModificationIn staticMod: search.getStaticResidueMods()) {
                MsResidueModification mod = new MsResidueModification() {
                    public int getId() {throw new UnsupportedOperationException();}
                    public int getSearchId() {return searchId;}
                    public char getModifiedResidue() {return staticMod.getModifiedResidue();}
                    public BigDecimal getModificationMass() {return staticMod.getModificationMass();}
                    public char getModificationSymbol() {return staticMod.getModificationSymbol();}
                    };
                modDao.saveStaticResidueMod(mod);
            }

            // save any dynamic residue modifications used for the search
            for (final MsResidueModificationIn dynaMod: search.getDynamicResidueMods()) {
                MsResidueModification mod = new MsResidueModification() {
                    public int getId() {throw new UnsupportedOperationException();}
                    public int getSearchId() {return searchId;}
                    public char getModifiedResidue() {return dynaMod.getModifiedResidue();}
                    public BigDecimal getModificationMass() {return dynaMod.getModificationMass();}
                    public char getModificationSymbol() {return dynaMod.getModificationSymbol();}
                    };
                modDao.saveDynamicResidueMod(mod);
            }

            // save any static terminal modifications used for the search
            for (final MsTerminalModificationIn staticMod: search.getStaticTerminalMods()) {
                MsTerminalModification mod = new MsTerminalModification(){
                    public int getId() {throw new UnsupportedOperationException();}
                    public int getSearchId() {return searchId;}
                    public Terminal getModifiedTerminal() {return staticMod.getModifiedTerminal();}
                    public BigDecimal getModificationMass() {return staticMod.getModificationMass();}
                    public char getModificationSymbol() {return staticMod.getModificationSymbol();}
                    };
                modDao.saveStaticTerminalMod(mod);
            }

            // save any dynamic residue modifications used for the search
            for (final MsTerminalModificationIn dynaMod: search.getDynamicTerminalMods()) {
                MsTerminalModification mod = new MsTerminalModification(){
                    public int getId() {throw new UnsupportedOperationException();}
                    public int getSearchId() {return searchId;}
                    public Terminal getModifiedTerminal() {return dynaMod.getModifiedTerminal();}
                    public BigDecimal getModificationMass() {return dynaMod.getModificationMass();}
                    public char getModificationSymbol() {return dynaMod.getModificationSymbol();}
                    };
                modDao.saveDynamicTerminalMod(mod);
            }

            // save any enzymes used for the search
            List<MsEnzymeIn> enzymes = search.getEnzymeList();
            for (MsEnzymeIn enzyme: enzymes) 
                // use the enzyme name attribute only to look for a matching enzyme.
                enzymeDao.saveEnzymeforSearch(enzyme, searchId, Arrays.asList(new EnzymeProperties[] {EnzymeProperties.NAME}));
        }
        catch(RuntimeException e) {
            deleteSearch(searchId); // this will delete anything that got saved with the searchId
            throw e;
        }
        return searchId;
    }
    
    @Override
    public int updateSearchProgramVersion(int searchId,
            String versionStr) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("searchId", searchId);
        map.put("analysisProgramVersion", versionStr);
        return update("MsSearch.updateAnalysisProgramVersion", map);
    }
    
    @Override
    public int updateSearchProgram(int searchId, SearchProgram program) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("searchId", searchId);
        map.put("analysisProgram", program);
        return update("MsSearch.updateAnalysisProgram", map);
    }
    
    public void deleteSearch(int searchId) {
        delete("MsSearch.delete", searchId);
    }
    
    //---------------------------------------------------------------------------------------
    /** 
     * Type handler for converting between SearchProgram and JDBC's VARCHAR types. 
     */
    public static class SearchProgramTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            String program = getter.getString();
            if (getter.wasNull())
                return SearchProgram.UNKNOWN;
            return SearchProgram.instance(program);
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            if (parameter == null)
                setter.setNull(java.sql.Types.VARCHAR);
            else
                setter.setString(((SearchProgram)parameter).name());
        }

        public Object valueOf(String s) {
            return SearchProgram.instance(s);
        }
    }
}
