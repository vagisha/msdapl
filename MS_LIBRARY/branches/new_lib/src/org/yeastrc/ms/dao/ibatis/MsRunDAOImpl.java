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
import org.yeastrc.ms.domain.IMsEnzyme;
import org.yeastrc.ms.domain.IMsRun;
import org.yeastrc.ms.domain.IMsScan;
import org.yeastrc.ms.domain.IMsRun.RunFileFormat;
import org.yeastrc.ms.domain.db.MsRun;
import org.yeastrc.ms.domain.db.MsScan;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsRunDAOImpl extends BaseSqlMapDAO implements MsRunDAO<IMsRun, MsRun> {

    private MsScanDAO<IMsScan, MsScan> msScanDao;
    private MsDigestionEnzymeDAO enzymeDao;
    
    public MsRunDAOImpl(SqlMapClient sqlMap, MsDigestionEnzymeDAO enzymeDao , 
            MsScanDAO<IMsScan, MsScan> msScanDAO) {
        super(sqlMap);
        this.enzymeDao = enzymeDao;
        this.msScanDao = msScanDAO;
    }

    public int saveRun(IMsRun run, int msExperimentId) {
        
        MsRunDb runDb = new MsRunDb(msExperimentId, run);
        // save the run
        int runId = saveAndReturnId("MsRun.insert", runDb);
        
        // save the enzyme information
        List<? extends IMsEnzyme> enzymes = run.getEnzymeList();
        for (IMsEnzyme enzyme: enzymes) 
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
        enzymeDao.deleteEnzymesByRunId(runId);
        
        // delete scans
        msScanDao.deleteScansForRun(runId);
        
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
            enzymeDao.deleteEnzymesByRunIds(runIds);
        }
        
        for (Integer runId: runIds) {
            // delete scans for this run
            msScanDao.deleteScansForRun(runId);
        }
        
        // finally, delete the runs
        delete("MsRun.deleteByExperimentId", msExperimentId);
        return runIds;
    }

    
    public RunFileFormat getRunFileFormat(int runId) throws Exception {
        IMsRun run = loadRun(runId);
        
        if (run == null) {
            throw new Exception("No run found for runId: "+runId);
        }
        return run.getRunFileFormat();
    }
    
}
