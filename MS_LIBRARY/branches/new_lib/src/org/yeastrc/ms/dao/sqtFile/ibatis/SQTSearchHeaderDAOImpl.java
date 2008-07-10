/**
 * SQTSearchHeaderDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO;
import org.yeastrc.ms.domain.sqtFile.SQTSearchHeader;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchHeaderDAOImpl extends BaseSqlMapDAO implements SQTSearchHeaderDAO {

    public SQTSearchHeaderDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO#loadSQTHeadersForSearch(int)
     */
    public List<SQTSearchHeader> loadSQTHeadersForSearch(int searchId) {
        return queryForList("SqtHeader.selectHeadersForSearch", searchId);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO#saveSQTHeader(org.yeastrc.ms.dto.sqtFile.SQTSearchHeader)
     */
    public void saveSQTHeader(SQTSearchHeader header) {
        save("SqtHeader.insertHeader", header);
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO#deleteSQTHeadersForSearch(int)
     */
    public void deleteSQTHeadersForSearch(int searchId) {
        delete("SqtHeader.deleteHeadersForSearch", searchId);
    }

}
