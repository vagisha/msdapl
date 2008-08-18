/**
 * MsPeptideSearchDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.util.Arrays;
import java.util.List;

import org.yeastrc.ms.dao.general.MsEnzymeDAO;
import org.yeastrc.ms.dao.general.MsEnzymeDAO.EnzymeProperties;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.MsSearchModification;

import com.ibatis.sqlmap.client.SqlMapClient;

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
    
    public int saveSearch(MsSearch search) {
        
        int searchId = saveAndReturnId("MsSearch.insert", search);
        
        // save any database information associated with the search 
        for (MsSearchDatabase seqDb: search.getSearchDatabases()) {
            seqDbDao.saveSearchDatabase(seqDb, searchId);
        }
        
        // save any static modifications used for the search
        for (MsSearchModification staticMod: search.getStaticModifications()) {
            modDao.saveStaticModification(staticMod, searchId);
        }
        
        // save any dynamic modifications used for the search
        for (MsSearchModification dynaMod: search.getDynamicModifications()) {
            modDao.saveDynamicModification(dynaMod, searchId);
        }
        
        // save any enzymes used for the search
        List<MsEnzyme> enzymes = search.getEnzymeList();
        for (MsEnzyme enzyme: enzymes) 
            // use the enzyme name attribute only to look for a matching enzyme.
            enzymeDao.saveEnzymeforSearch(enzyme, searchId, Arrays.asList(new EnzymeProperties[] {EnzymeProperties.NAME}));
        
        
        return searchId;
    }
    
    public void deleteSearch(int searchId) {
        delete("MsSearch.delete", searchId);
    }

}
