/**
 * ProteinPropertiesDAO.java
 * @author Vagisha Sharma
 * Nov 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.database.dao;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dto.ProteinProperties;

/**
 * 
 */
public class ProteinPropertiesDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinProperties";

    public ProteinPropertiesDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public void save(ProteinProperties properties) {
        save(sqlMapNameSpace+".insert", properties);
    }
    
    public ProteinProperties load(int nrseqProteinId) {
        return (ProteinProperties) queryForObject(sqlMapNameSpace+".select", nrseqProteinId); 
    }
}
