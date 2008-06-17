/**
 * SqlMapClientFactory.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.io.IOException;
import java.io.Reader;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * 
 */
public class SqlMapClientFactory {

    private static final SqlMapClientFactory instance = new SqlMapClientFactory();
    
    public SqlMapClientFactory () {}
    
    public static final SqlMapClientFactory instance() {
        return instance;
    }
    
    public SqlMapClient getSqlMapClient() {
        String resource = "SqlMapConfig.xml";
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(resource);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        SqlMapClient sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
        return sqlMap;
    }
}
