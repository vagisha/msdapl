/**
 * MsExperimentDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.sql.SQLException;

import org.yeastrc.ms.MsExperiment;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsExperimentDAO {

    private final SqlMapClient sqlMap;
    
    public MsExperimentDAO () {
        sqlMap = SqlMapClientFactory.instance().getSqlMapClient();
    }
    
    public MsExperiment get(int msExperimentId) {
        try {
            return (MsExperiment)sqlMap.queryForObject("MsExperiment.select", msExperimentId);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int insert(MsExperiment experiment) {
        try {
            return (Integer) sqlMap.insert("MsExperiment.insert", experiment);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public void update(MsExperiment experiment) {
        
    }
    
    public void delete(int msExperimentId) {
        
    }
    
    public void delete(MsExperiment experiment) {
        
    }
}
