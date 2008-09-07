/**
 * MsSequenceDatabaseDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.domain.search.MsSearchDatabase;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchDatabaseDAOImpl extends BaseSqlMapDAO implements MsSearchDatabaseDAO {

    public MsSearchDatabaseDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MsSearchDatabase> loadSearchDatabases(int searchId) {
        return queryForList("MsDatabase.selectSearchDatabases", searchId);
    }
    
    public void deleteSearchDatabases(int searchId) {
        delete("MsDatabase.deleteSearchDatabases", searchId);
    }
    
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
