/**
 * MsExperimentDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.List;

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
    
    public List<Integer> selectAllExperimentIds() {
        return queryForList("MsExperiment.selectAll");
    }
    
    public void delete(int msExperimentId) {
        delete("MsExperiment.delete", msExperimentId);
    }
    
    public void deleteAll() {
        deleteAll("MsExperiment.deleteAll");
    }
    
}
