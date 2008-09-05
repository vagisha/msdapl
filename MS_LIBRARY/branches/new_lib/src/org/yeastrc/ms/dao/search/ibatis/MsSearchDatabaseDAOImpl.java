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
import org.yeastrc.ms.domain.search.MsSearchDatabaseDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchDatabaseDAOImpl extends BaseSqlMapDAO implements MsSearchDatabaseDAO {

    public MsSearchDatabaseDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MsSearchDatabaseDb> loadSearchDatabases(int searchId) {
        return queryForList("MsDatabase.selectSearchDatabases", searchId);
    }
    
    public void deleteSearchDatabases(int searchId) {
        delete("MsDatabase.deleteSearchDatabases", searchId);
    }
    
    public int saveSearchDatabase(MsSearchDatabase database, int searchId, int sequenceDatabaseId) {
        
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("searchId", searchId);
        
        List<Integer> dbIds = loadMatchingDatabaseIds(new MsSearchDatabaseDbSqlMapParam(database, sequenceDatabaseId));
        if (dbIds.size() > 0) {
            map.put("databaseId", dbIds.get(0));
        }
        else {
            map.put("databaseId", saveDatabase(new MsSearchDatabaseDbSqlMapParam(database, sequenceDatabaseId)));
        }
        save("MsDatabase.insertSearchDatabase", map);
        return map.get("databaseId");
    }
    
    
    private List<Integer> loadMatchingDatabaseIds(MsSearchDatabaseDb database) {
        return queryForList("MsDatabase.selectDatabaseIdMatchAllCols", database);
    }
    
    private int saveDatabase(MsSearchDatabaseDb database) {
        return saveAndReturnId("MsDatabase.insertDatabase", database);
    }
    
    public static final class MsSearchDatabaseDbSqlMapParam implements MsSearchDatabaseDb {

        private MsSearchDatabase db;
        private int sequenceDatabaseId;
        
        public MsSearchDatabaseDbSqlMapParam(MsSearchDatabase db, int sequenceDatabaseId) {
            this.db = db;
            this.sequenceDatabaseId = sequenceDatabaseId;
        }
        
        public String getDatabaseFileName() {
            throw new UnsupportedOperationException("getDatabaseFileName() not supported by MsSearchDatabaseDbSqlMapParam");
        }

        @Override
        public int getId() {
            throw new UnsupportedOperationException("getId() not supported by MsSearchDatabaseDbSqlMapParam");
        }

        @Override
        public int getSequenceDatabaseId() {
            return sequenceDatabaseId;
        }

        @Override
        public String getServerAddress() {
            return db.getServerAddress();
        }

        @Override
        public String getServerPath() {
            return db.getServerPath();
        }
    }
}
