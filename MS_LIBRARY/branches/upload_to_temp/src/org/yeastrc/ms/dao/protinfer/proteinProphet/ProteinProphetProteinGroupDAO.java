/**
 * ProteinProphetProteinGroupDAO.java
 * @author Vagisha Sharma
 * Jul 29, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProteinProphetProteinGroupDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinProphetProteinGroup";
    
    public ProteinProphetProteinGroupDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public int saveGroup(int pinferId, double probability, int groupNumber) {
        
    }
}
