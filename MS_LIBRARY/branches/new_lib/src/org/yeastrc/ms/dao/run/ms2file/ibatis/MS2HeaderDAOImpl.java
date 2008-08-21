/**
 * Ms2FileRunHeadersDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2HeaderDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2Field;
import org.yeastrc.ms.domain.run.ms2file.MS2HeaderDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2HeaderDAOImpl extends BaseSqlMapDAO implements MS2HeaderDAO {

    public MS2HeaderDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void save(MS2Field header, int runId) {
        MS2HeaderSqlMapParam headerDb = new MS2HeaderSqlMapParam(runId, header.getName(), header.getValue());
        save("MS2Header.insert", headerDb);
    }
    
    public List<MS2HeaderDb> loadHeadersForRun(int runId) {
        return queryForList("MS2Header.selectHeadersForRun", runId);
    }

    public void deleteHeadersForRunId(int runId) {
        delete("MS2Header.deleteByRunId", runId);
    }
    
    public static final class MS2HeaderSqlMapParam implements MS2HeaderDb {
        private int runId;
        private String name;
        private String value;
        public MS2HeaderSqlMapParam(int runId, String name, String value) {
            this.runId = runId;
            this.name = name;
            this.value = value;
        }
        public int getRunId() {
            return runId;
        }
        public String getName() {
            return name;
        }
        public String getValue() {
            return value;
        }
        public int getId() {
            throw new UnsupportedOperationException("getId() not supported by MS2HeaderSqlMapParam");
        }
    }
}
