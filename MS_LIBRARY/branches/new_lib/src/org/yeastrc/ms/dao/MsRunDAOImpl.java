/**
 * MsRunDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dto.MsDigestionEnzyme;
import org.yeastrc.ms.dto.MsRun;
import org.yeastrc.ms.dto.MsRunWithEnzymeInfo;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsRunDAOImpl extends BaseSqlMapDAO implements MsRunDAO {

    public MsRunDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    
    public int saveRun(MsRun run) {
        return saveAndReturnId("MsRun.insert", run);
    }
    
    public int saveRunWithEnzymeInfo(MsRunWithEnzymeInfo run) {
        int runId = saveRun(run);
        List<MsDigestionEnzyme> enzymes = run.getEnzymeList();
        MsDigestionEnzymeDAO enzymeDao = DAOFactory.instance().getEnzymeDAO();
        for (MsDigestionEnzyme enzyme: enzymes) 
            enzymeDao.saveEnzymeforRun(enzyme, runId);
        return runId;
    }
    
    
    public MsRun loadRun(int runId) {
        return (MsRun) queryForObject("MsRun.select", runId);
    }
    
    public MsRunWithEnzymeInfo loadRunWithEmzymeInfo(int runId) {
        return (MsRunWithEnzymeInfo) queryForObject("MsRun.select_wEnzyme", runId);
    }
    
    
    public List<MsRun> loadExperimentRuns(int msExperimentId) {
        return queryForList("MsRun.selectRunsForExperiment", msExperimentId);
    }
    
    public List<MsRunWithEnzymeInfo> loadExperimentRunsWithEnzymeInfo(
            int msExperimentId) {
        return queryForList("MsRun.selectRunsForExperiment_wEnzyme", msExperimentId);
    }
    
    
    public List<MsRun> loadRuns(String fileName,
            String sha1Sum) {
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("fileName", fileName);
        map.put("sha1Sum", sha1Sum);
        
        return queryForList("MsRun.selectRunsForFileNameAndSha1Sum", map);
    }
    
    public List<MsRunWithEnzymeInfo> loadRunsWithEnzymeInfo(String fileName,
            String sha1Sum) {
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("fileName", fileName);
        map.put("sha1Sum", sha1Sum);
        
        return queryForList("MsRun.selectRunsForFileNameAndSha1Sum_wEnzyme", map);
    }
    
    
    public List<Integer> loadRunIdsForExperiment(int msExperimentId) {
        return queryForList("MsRun.selectRunIdsForExperiment", msExperimentId);
    }
    
    public void deleteRunsForExperiment(int msExperimentId) {
        List<Integer> runIds = loadRunIdsForExperiment(msExperimentId);
        delete("MsRun.deleteByExperimentId", msExperimentId);
        MsDigestionEnzymeDAO enzymeDao = DAOFactory.instance().getEnzymeDAO();
        for (Integer runId: runIds) 
            enzymeDao.deleteRunEnzymes(runId);
    }
}
