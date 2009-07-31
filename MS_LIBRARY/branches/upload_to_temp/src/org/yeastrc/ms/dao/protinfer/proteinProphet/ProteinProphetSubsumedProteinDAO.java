/**
 * ProteinProphetSubsumedProteinDAO.java
 * @author Vagisha Sharma
 * Jul 30, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProteinProphetSubsumedProteinDAO extends BaseSqlMapDAO {

    public ProteinProphetSubsumedProteinDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public void saveSubsumedProtein(int pinferId, int subsumedProteinId, int subsumingProteinId) {
        // TODO
    }

}
