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

import org.yeastrc.ms.dao.MsEnzymeDAO;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.domain.MsEnzyme;
import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsRunDb;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;
import org.yeastrc.ms.domain.MsRun.RunFileFormat;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsRunDAOImpl extends BaseSqlMapDAO implements MsRunDAO<MsRun, MsRunDb> {

    private MsScanDAO<MsScan, MsScanDb> msScanDao;
    private MsEnzymeDAO enzymeDao;
    
    public MsRunDAOImpl(SqlMapClient sqlMap, MsEnzymeDAO enzymeDao , 
            MsScanDAO<MsScan, MsScanDb> msScanDAO) {
        super(sqlMap);
        this.enzymeDao = enzymeDao;
        this.msScanDao = msScanDAO;
    }

    public int saveRun(MsRun run, int msExperimentId) {
        
        MsRunSqlMapParam runDb = new MsRunSqlMapParam(msExperimentId, run);
        // save the run
        int runId = saveAndReturnId("MsRun.insert", runDb);
        
        // save the enzyme information
        List<? extends MsEnzyme> enzymes = run.getEnzymeList();
        for (MsEnzyme enzyme: enzymes) 
            enzymeDao.saveEnzymeforRun(enzyme, runId);
        
        return runId;
    }

    public MsRunDb loadRun(int runId) {
        return (MsRunDb) queryForObject("MsRun.select", runId);
    }
    

    public List<MsRunDb> loadExperimentRuns(int msExperimentId) {
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
        enzymeDao.deleteEnzymesForRun(runId);
        
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
            enzymeDao.deleteEnzymesForRuns(runIds);
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
        MsRun run = loadRun(runId);
        
        if (run == null) {
            throw new Exception("No run found for runId: "+runId);
        }
        return run.getRunFileFormat();
    }
    
}
