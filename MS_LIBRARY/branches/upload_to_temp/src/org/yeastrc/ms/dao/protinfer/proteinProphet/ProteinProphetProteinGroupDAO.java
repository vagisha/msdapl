/**
 * ProteinProphetProteinGroupDAO.java
 * @author Vagisha Sharma
 * Jul 29, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetGroup;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProteinProphetProteinGroupDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinProphetProteinGroup";
    
    public ProteinProphetProteinGroupDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public int saveGroup(ProteinProphetGroup group) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", group);
    }
}
