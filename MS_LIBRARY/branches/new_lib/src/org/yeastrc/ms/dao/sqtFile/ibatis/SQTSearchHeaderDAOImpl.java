/**
 * SQTSearchHeaderDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.sqtFile.SQTHeaderDAO;
import org.yeastrc.ms.domain.sqtFile.SQTField;
import org.yeastrc.ms.domain.sqtFile.SQTHeaderDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchHeaderDAOImpl extends BaseSqlMapDAO implements SQTHeaderDAO {

    public SQTSearchHeaderDAOImpl(SqlMapClient sqlMap) {
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
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("searchId", searchId);
        map.put("name", header.getName());
        map.put("value", header.getValue());
        save("SqtHeader.insertHeader", map);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO#deleteSQTHeadersForSearch(int)
     */
    public void deleteSQTHeadersForSearch(int searchId) {
        delete("SqtHeader.deleteHeadersForSearch", searchId);
    }

}
