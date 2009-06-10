/**
 * MsExperimentDAOImpl.java
 * @author Vagisha Sharma
 * Sep 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.general.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.upload.dao.general.MsExperimentUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsExperimentUploadDAOIbatisImpl extends BaseSqlMapDAO implements MsExperimentUploadDAO {

    public MsExperimentUploadDAOIbatisImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public List<Integer> getAllExperimentIds() {
        return queryForList("MsExperiment.selectAllExperimentIds");
    }

    @Override
    public MsExperiment loadExperiment(int experimentId) {
        return (MsExperiment) queryForObject("MsExperiment.selectExperiment", experimentId);
    }

    @Override
    public int saveExperiment(MsExperiment experiment) {
        return saveAndReturnId("MsExperiment.insertExperiment", experiment);
    }

    @Override
    public void saveExperimentRun(int experimentId, int runId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("experimentId", experimentId);
        map.put("runId", runId);
        // if an entry for this experimentId and runId already exists don't 
        // upload another one
        if (getMatchingExptRunCount(experimentId, runId) == 0)
            save("MsExperiment.insertExperimentRun", map);
    }
    
    public void updateLastUpdateDate(int experimentId) {
       update("MsExperiment.updateLastUpdate", experimentId); 
    }
    
    private int getMatchingExptRunCount(int experimentId, int runId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("experimentId", experimentId);
        map.put("runId", runId);
        Integer cnt = (Integer) queryForObject("MsExperiment.getExperimentRunCount", map);
        if (cnt == null)
            return 0;
        return cnt;
    }

    @Override
    public void deleteExperiment(int experimentId) {
        delete("MsExperiment.deleteExperiment", experimentId);
    }
}
