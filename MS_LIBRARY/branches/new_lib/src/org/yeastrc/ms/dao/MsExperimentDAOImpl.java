/**
 * MsExperimentDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import org.yeastrc.ms.dto.MsExperiment;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsExperimentDAOImpl extends BaseSqlMapDAO implements MsExperimentDAO {
    
    public MsExperimentDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public MsExperiment load(int msExperimentId) {
        return (MsExperiment)queryForObject("MsExperiment.select", msExperimentId);
    }
    
    public int save(MsExperiment experiment) {
        return saveAndReturnId("MsExperiment.insert", experiment);
    }
    
}
