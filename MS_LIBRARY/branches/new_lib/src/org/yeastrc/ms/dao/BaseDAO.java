/**
 * BaseDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class BaseDAO {
    
    protected final SqlMapClient sqlMap;
    
    public BaseDAO() {
        sqlMap = SqlMapClientFactory.instance().getSqlMapClient();
    }
}
