/**
 * ProteinDatabaseLookupUtil.java
 * @author Vagisha Sharma
 * May 16, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;

/**
 * 
 */
public class ProteinDatabaseLookupUtil {

    private static ProteinDatabaseLookupUtil instance;
    
    private static final Logger log = Logger.getLogger(ProteinDatabaseLookupUtil.class.getName());
    
    private ProteinDatabaseLookupUtil() {}
    
    public static ProteinDatabaseLookupUtil getInstance() {
        if(instance == null)
            instance = new ProteinDatabaseLookupUtil();
        return instance;
    }
    
    public List<Integer> getDatabaseIdsForProteinInference(int pinferId) {
        
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        
        List<Integer> searchIds = runDao.loadSearchIdsForProteinferRun(pinferId);
        if(searchIds.size() == 0) {
            log.error("No search Ids found for protein inference ID: "+pinferId);
        }
        
        Set<Integer> databaseIds = new HashSet<Integer>();
        for(int searchId: searchIds) {
            MsSearch search = searchDao.loadSearch(searchId);
            for(MsSearchDatabase db: search.getSearchDatabases()) {
                databaseIds.add(db.getSequenceDatabaseId());
            }
        }
        return new ArrayList<Integer>(databaseIds);
    }
    
    public List<Integer> getDatabaseIdsForProteinInference(List<Integer> pinferIds) {
        
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        
        Set<Integer> searchIds = new HashSet<Integer>();
        for(int pinferId: pinferIds) {
            List<Integer> ids = runDao.loadSearchIdsForProteinferRun(pinferId);
            if(ids.size() == 0) {
                log.error("No search Ids found for protein inference ID: "+pinferId);
            }
            searchIds.addAll(ids);
        }
        
        Set<Integer> databaseIds = new HashSet<Integer>();
        for(int searchId: searchIds) {
            MsSearch search = searchDao.loadSearch(searchId);
            for(MsSearchDatabase db: search.getSearchDatabases()) {
                databaseIds.add(db.getSequenceDatabaseId());
            }
        }
        return new ArrayList<Integer>(databaseIds);
    }
}
