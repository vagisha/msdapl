/**
 * BaseDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibatis.sqlmap.client.SqlMapClient;

public class BaseSqlMapDAO {
    
    protected final SqlMapClient sqlMap;
    
    protected static final Logger log = Logger.getLogger(BaseSqlMapDAO.class);
    
    public BaseSqlMapDAO(SqlMapClient sqlMap) {
        this.sqlMap = sqlMap;
    }
    
    /**
     * @param statementName
     * @param parameterObject
     * @return returns the object matching the query parameters; null if no matching object was found
     * or if the query failed.
     */
    public Object queryForObject(String statementName, Object parameterObject) {
        try {
            return sqlMap.queryForObject(statementName, parameterObject);
        }
        catch (SQLException e) {
            log.error("Could not execute select statement", e);
            return null;
        }
    }
    
    /**
     * @param statementName
     * @param parameterObject
     * @return returns a List of objects matching the query parameters; null if the query failed
     */
    public List queryForList(String statementName, Object parameterObject) {
        try {
            return sqlMap.queryForList(statementName, parameterObject);
        }
        catch (SQLException e) {
            log.error("Could not execute select list statement", e);
            return null;
        }
    }
    
    /**
     * @param statementName
     * @param parameterObject
     * @return returns the database id of the saved object; 0 if the object could not be saved.
     */
    public int saveAndReturnId(String statementName, Object parameterObject) {
        try {
            return (Integer) sqlMap.insert(statementName, parameterObject);
        }
        catch (SQLException e) {
            log.error("Could not execute save statement", e);
            return 0;
        }
    }
    
    /**
     * @param statementName
     * @param parameterObject
     * @return true if the object was saved successfully in the database; 0 otherwise. 
     */
    public boolean save(String statementName, Object parameterObject) {
        try {
            sqlMap.insert(statementName, parameterObject);
            return true;
        }
        catch (SQLException e) {
            log.error("Could not execute save statement", e);
            return false;
        }
    }
}
