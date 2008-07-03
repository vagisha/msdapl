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

import org.yeastrc.ms.dao.ms2File.MS2FileHeaderDAO;
import org.yeastrc.ms.dto.IMsRun;
import org.yeastrc.ms.dto.MsDigestionEnzyme;
import org.yeastrc.ms.dto.MsRun.RunFileFormat;
import org.yeastrc.ms.dto.ms2File.MS2FileHeader;
import org.yeastrc.ms.dto.ms2File.MS2FileRun;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsRunDAOImpl extends BaseSqlMapDAO implements MsRunDAO {

    public MsRunDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveRun(IMsRun run) {
        
        // save the run
        int runId = saveAndReturnId("MsRun.insert", run);
        run.setId(runId);
        
        // save the enzyme information
        List<MsDigestionEnzyme> enzymes = run.getEnzymeList();
        MsDigestionEnzymeDAO enzymeDao = DAOFactory.instance().getEnzymeDAO();
        for (MsDigestionEnzyme enzyme: enzymes) 
            enzymeDao.saveEnzymeforRun(enzyme, runId);
        
        // save any file-format specific information
        if (run instanceof MS2FileRun) {
            saveMS2FileInformation((MS2FileRun)run);
        }
        
        return runId;
    }
    
    
    private void saveMS2FileInformation(MS2FileRun run) {
        List<MS2FileHeader> headers = run.getMS2Headers();
        MS2FileHeaderDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
        for (MS2FileHeader header: headers) {
            header.setRunId(run.getId());
            headerDao.save(header);
        }
    }


    public IMsRun loadRun(int runId) {
        return (IMsRun) queryForObject("MsRun.select", runId);
    }
    
    public IMsRun loadRunForFormat(int runId) {
        IMsRun run = loadRun(runId);
        if (run.getRunFileFormat() == RunFileFormat.MS2) {
            run = loadMS2FileRun(run);
        }
        return run;
    }
    
    
    private IMsRun loadMS2FileRun(IMsRun run) {
        MS2FileRun ms2Run = new MS2FileRun(run);
        MS2FileHeaderDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
        ms2Run.setMS2Headers(headerDao.loadHeadersForRun(run.getId()));
        return ms2Run;
    }


    public List<IMsRun> loadExperimentRuns(int msExperimentId) {
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
    
    
    /**
     * This will delete all the runs associated with the given experimentId, along with
     * any enzyme entries (msRunEnzyme table) associated with the runs, as well as the scans
     * File-format specific information is deleted too.
     * 
     * @param msExperimentId
     * @return List of run IDs that were deleted
     */
    public List<Integer> deleteRunsForExperiment(int msExperimentId) {
        List<Integer> runIds = loadRunIdsForExperiment(msExperimentId);
        
        if (runIds.size() > 0) {
            // delete any file-format specific information
            deleteFileFormatSpecificData(runIds);
            
            // delete enzyme associations
            MsDigestionEnzymeDAO enzymeDao = DAOFactory.instance().getEnzymeDAO();
            enzymeDao.deleteEnzymesByRunIds(runIds);
            
            // delete scans for this run
            MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
            scanDao.deleteScansForRuns(runIds);
            
            // finally, delete the runs
            delete("MsRun.deleteByExperimentId", msExperimentId);
        }
        return runIds;
    }

    private void deleteFileFormatSpecificData(List<Integer> runIds) {
        
        // we assume all runs are from the same experiment, so their file formats should be the same.
        // we look at the first run to determine the file format for all the runs in the list.
        IMsRun run = loadRun(runIds.get(0));
        if (run.getRunFileFormat() == RunFileFormat.MS2) {
            MS2FileHeaderDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
            headerDao.deleteHeadersForRunIds(runIds);
        }
    }
    
    public RunFileFormat getRunFileFormat(int runId) {
        IMsRun run = loadRun(runId);
        
        if (run == null) {
            throw new RuntimeException("No run found for runId: "+runId);
        }
        return run.getRunFileFormat();
    }
   
}
