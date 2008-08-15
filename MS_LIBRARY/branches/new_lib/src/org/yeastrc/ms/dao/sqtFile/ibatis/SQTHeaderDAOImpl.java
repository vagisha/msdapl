/**
 * SQTSearchHeaderDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.sqtFile.SQTHeaderDAO;
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
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO#loadSQTHeadersForSearch(int)
     */
    public List<SQTHeaderDb> loadSQTHeadersForSearch(int searchId) {
        return queryForList("SqtHeader.selectHeadersForSearch", searchId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO#saveSQTHeader(org.yeastrc.ms.dto.sqtFile.SQTSearchHeader)
     */
    public void saveSQTHeader(SQTField header, int searchId) {
        save("SqtHeader.insertHeader", new SQTHeaderSqlMapParam(searchId, header.getName(), header.getValue()));
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO#deleteSQTHeadersForSearch(int)
     */
    public void deleteSQTHeadersForSearch(int searchId) {
        delete("SqtHeader.deleteHeadersForSearch", searchId);
    }

    public static final class SQTHeaderSqlMapParam {
        private int searchId;
        private String name;
        private String value;
        public SQTHeaderSqlMapParam(int searchId, String name, String value) {
            this.searchId = searchId;
            this.name = name;
            this.value = value;
        }
        public int getSearchId() {
            return searchId;
        }
        public String getName() {
            return name;
        }
        public String getValue() {
            return value;
        }
    }
}
