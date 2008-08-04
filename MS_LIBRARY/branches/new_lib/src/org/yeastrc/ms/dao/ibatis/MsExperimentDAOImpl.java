/**
 * MsExperimentDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.MsExperimentDAO;
import org.yeastrc.ms.domain.MsExperiment;
import org.yeastrc.ms.domain.MsExperimentDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsExperimentDAOImpl extends BaseSqlMapDAO implements MsExperimentDAO {
    
    public MsExperimentDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public MsExperimentDb load(int msExperimentId) {
        return (MsExperimentDb)queryForObject("MsExperiment.select", msExperimentId);
    }
    
    @Override
    public List<Integer> selectExperimentIdsForRun(int runId) {
        return queryForList("MsExperiment.selectExperimentIdsForRun");
    }

    @Override
    public List<Integer> selectRunIdsForExperiment(int experimentId) {
        return queryForList("MsExperiment.selectRunIdsForExperiment");
    }
    
    public int save(MsExperiment experiment) {
        return saveAndReturnId("MsExperiment.insert", experiment);
    }
    
    @Override
    public void saveRunExperiment(int experimentId, int runId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("experimentId", experimentId);
        map.put("runId", runId);
        save("MsExperiment.insertRunExperiment", map);
    }
    
    public List<Integer> selectAllExperimentIds() {
        return queryForList("MsExperiment.selectAll");
    }
    
    /**
     * Deletes the experiment ONLY
     */
    public void delete(int msExperimentId) {
        // delete the experiment
        delete("MsExperiment.delete", msExperimentId);
    }
}
