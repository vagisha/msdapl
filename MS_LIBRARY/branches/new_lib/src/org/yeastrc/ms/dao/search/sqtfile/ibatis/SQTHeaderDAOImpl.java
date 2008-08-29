/**
 * SQTSearchHeaderDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sqtfile.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTHeaderDAO;
import org.yeastrc.ms.domain.search.sqtfile.SQTField;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTHeaderDAOImpl extends BaseSqlMapDAO implements SQTHeaderDAO {

    public SQTHeaderDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    
    public List<SQTHeaderDb> loadSQTHeadersForRunSearch(int runSearchId) {
        return queryForList("SqtHeader.selectHeadersForRunSearch", runSearchId);
    }
    
    
    public void saveSQTHeader(SQTField header, int runSearchId) {
        save("SqtHeader.insertHeader", new SQTHeaderSqlMapParam(runSearchId, header.getName(), header.getValue()));
    }
    
    
    public void deleteSQTHeadersForRunSearch(int runSearchId) {
        delete("SqtHeader.deleteHeadersForRunSearch", runSearchId);
    }

    public static final class SQTHeaderSqlMapParam implements SQTHeaderDb {
        private int runSearchId;
        private String name;
        private String value;
        public SQTHeaderSqlMapParam(int runSearchId, String name, String value) {
            this.runSearchId = runSearchId;
            this.name = name;
            this.value = value;
        }
        public int getRunSearchId() {
            return runSearchId;
        }
        public String getName() {
            return name;
        }
        public String getValue() {
            return value;
        }
        public int getId() {
            throw new UnsupportedOperationException("getId() not supported by SQTHeaderSqlMapParam");
        }
    }
}
