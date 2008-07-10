/**
 * MsRunDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.MsDigestionEnzymeDAO;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.domain.MsDigestionEnzyme;
import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsRun.RunFileFormat;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsRunDAOImpl extends BaseSqlMapDAO implements MsRunDAO<MsRun> {

    public MsRunDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveRun(MsRun run) {
        
        // save the run
        int runId = saveAndReturnId("MsRun.insert", run);
        run.setId(runId);
        
        // save the enzyme information
        List<MsDigestionEnzyme> enzymes = run.getEnzymeList();
        MsDigestionEnzymeDAO enzymeDao = DAOFactory.instance().getEnzymeDAO();
        for (MsDigestionEnzyme enzyme: enzymes) 
            enzymeDao.saveEnzymeforRun(enzyme, runId);
        
        return runId;
    }

    public MsRun loadRun(int runId) {
        return (MsRun) queryForObject("MsRun.select", runId);
    }
    

    public List<MsRun> loadExperimentRuns(int msExperimentId) {
        return queryForList("MsRun.selectRunsForExperiment", msExperimentId);
    }
    
    public List<Integer> loadRunIdsForExperiment(int msExperimentId) {
        return queryForList("MsRun.selectRunIdsForExperiment", msExperimentId);
    }
    
    public List<Integer> runIdsFor(String fileName, String sha1Sum) {
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("fileName", fileName);
        map.put("sha1Sum", sha1Sum);
        
        return queryForList("MsRun.selectRunIdsForFileNameAndSha1Sum", map);
    }
    
    
    public void delete(int runId) {
        
        // delete enzyme information first
        MsDigestionEnzymeDAO enzymeDao = DAOFactory.instance().getEnzymeDAO();
        enzymeDao.deleteEnzymesByRunId(runId);
        
        // delete scans
        MsScanDAO<MsScan> scanDao = DAOFactory.instance().getMsScanDAO();
        scanDao.deleteScansForRun(runId);
        
        // delete the run
        delete("MsRun.delete", runId);
    }
   
    
    /**
     * This will delete all the runs associated with the given experimentId, along with
     * any enzyme entries (msRunEnzyme table) associated with the runs, as well as the scans
     * 
     * @param msExperimentId
     * @return List of run IDs that were deleted
     */
    public List<Integer> deleteRunsForExperiment(int msExperimentId) {
        List<Integer> runIds = loadRunIdsForExperiment(msExperimentId);
        
        if (runIds.size() > 0) {
            // delete enzyme associations
            MsDigestionEnzymeDAO enzymeDao = DAOFactory.instance().getEnzymeDAO();
            enzymeDao.deleteEnzymesByRunIds(runIds);
        }
        
        for (Integer runId: runIds) {
            // delete scans for this run
            MsScanDAO<MsScan> scanDao = DAOFactory.instance().getMsScanDAO();
            scanDao.deleteScansForRun(runId);
        }
        
        // finally, delete the runs
        delete("MsRun.deleteByExperimentId", msExperimentId);
        return runIds;
    }

    
    public RunFileFormat getRunFileFormat(int runId) throws Exception {
        MsRun run = loadRun(runId);
        
        if (run == null) {
            throw new Exception("No run found for runId: "+runId);
        }
        return run.getRunFileFormat();
    }

}
