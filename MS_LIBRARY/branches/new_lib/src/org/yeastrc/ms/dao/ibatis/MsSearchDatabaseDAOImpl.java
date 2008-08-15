/**
 * MsSequenceDatabaseDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.MsSearchDatabaseDAO;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchDatabaseDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchDatabaseDAOImpl extends BaseSqlMapDAO implements MsSearchDatabaseDAO {

    public MsSearchDatabaseDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsSequenceDatabaseDAO#loadSearchDatabases(int)
     */
    public List<MsSearchDatabaseDb> loadSearchDatabases(int searchId) {
        return queryForList("MsDatabase.selectSearchDatabases", searchId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsSequenceDatabaseDAO#deleteSearchDatabases(int)
     */
    public void deleteSearchDatabases(int searchId) {
        delete("MsDatabase.deleteSearchDatabases", searchId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsSequenceDatabaseDAO#saveSearchDatabase(org.yeastrc.ms.dto.MsSequenceDatabase, int)
     */
    public int saveSearchDatabase(MsSearchDatabase database, int searchId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("searchId", searchId);
        
        List<Integer> dbIds = loadMatchingDatabaseIds(database);
        if (dbIds.size() > 0) {
            map.put("databaseId", dbIds.get(0));
        }
        else {
            map.put("databaseId", saveDatabase(database));
        }
        save("MsDatabase.insertSearchDatabase", map);
        return map.get("databaseId");
    }
    
    
    private List<Integer> loadMatchingDatabaseIds(MsSearchDatabase database) {
        return queryForList("MsDatabase.selectDatabaseIdMatchAllCols", database);
    }
    
    private int saveDatabase(MsSearchDatabase database) {
        return saveAndReturnId("MsDatabase.insertDatabase", database);
    }
    
}
