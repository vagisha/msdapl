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

import org.yeastrc.ms.dao.MsSequenceDatabaseDAO;
import org.yeastrc.ms.dto.IMsSequenceDatabase;
import org.yeastrc.ms.dto.MsSequenceDatabase;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSequenceDatabaseDAOImpl extends BaseSqlMapDAO implements MsSequenceDatabaseDAO {

    public MsSequenceDatabaseDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.MsSequenceDatabaseDAO#loadSearchDatabases(int)
     */
    public List<MsSequenceDatabase> loadSearchDatabases(int searchId) {
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
    public int saveSearchDatabase(IMsSequenceDatabase database, int searchId) {
        
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
    
    
    private List<Integer> loadMatchingDatabaseIds(IMsSequenceDatabase database) {
        return queryForList("MsDatabase.selectDatabaseIdMatchAllCols", database);
    }
    
    private int saveDatabase(IMsSequenceDatabase database) {
        return saveAndReturnId("MsDatabase.insertDatabase", database);
    }
    
}
