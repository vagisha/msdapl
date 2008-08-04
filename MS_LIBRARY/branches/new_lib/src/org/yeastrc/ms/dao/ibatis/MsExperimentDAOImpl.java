/**
 * MsExperimentDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.ArrayList;
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
    public List<Integer> loadExperimentIdsForRun(int runId) {
        return queryForList("MsExperiment.selectExperimentIdsForRun", runId);
    }

    @Override
    public List<Integer> loadRunIdsForExperiment(int experimentId) {
        return queryForList("MsExperiment.selectRunIdsForExperiment", experimentId);
    }
    
    @Override
    public List<Integer> loadRunIdsUniqueToExperiment(int experimentId) {
        List<Integer> runIdsForExp = loadRunIdsForExperiment(experimentId);
        List<Integer> uniqueRunIds = new ArrayList<Integer>();
        for (Integer runId: runIdsForExp) {
            if (loadExperimentIdsForRun(runId).size() == 1)
                uniqueRunIds.add(runId);
        }
        return uniqueRunIds;
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
     * Deletes the experiment and matching entries in msExperimentRun table
     */
    public void delete(int msExperimentId) {
        // delete the experiment
        delete("MsExperiment.delete", msExperimentId);
        // delete matching entries in msExperimentRun table
        delete("MsExperiment.deleteExperimentRuns", msExperimentId);
    }
}
