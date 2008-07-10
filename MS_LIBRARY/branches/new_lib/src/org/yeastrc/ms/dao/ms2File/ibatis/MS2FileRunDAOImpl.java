package org.yeastrc.ms.dao.ms2File.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.dao.ms2File.MS2FileHeaderDAO;
import org.yeastrc.ms.domain.IMsRun;
import org.yeastrc.ms.domain.IMsRun.RunFileFormat;
import org.yeastrc.ms.domain.db.MsRun;
import org.yeastrc.ms.domain.ms2File.IHeader;
import org.yeastrc.ms.domain.ms2File.IMS2Run;
import org.yeastrc.ms.domain.ms2File.IMS2Scan;
import org.yeastrc.ms.domain.ms2File.db.MS2FileRun;
import org.yeastrc.ms.domain.ms2File.db.MS2FileScan;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MS2FileRunDAOImpl extends BaseSqlMapDAO implements MsRunDAO<IMS2Run, MS2FileRun> {

    private MsRunDAO<IMsRun, MsRun> msRunDao;
    private MS2FileHeaderDAO ms2HeaderDao;
    private MsScanDAO<IMS2Scan, MS2FileScan> ms2ScanDao;
    
    
    
    public MS2FileRunDAOImpl(SqlMapClient sqlMap, MsRunDAO<IMsRun, MsRun> msRunDao,
            MS2FileHeaderDAO ms2HeaderDao, MsScanDAO<IMS2Scan, MS2FileScan> ms2ScanDao) {
        super(sqlMap);
        this.msRunDao = msRunDao;
        this.ms2HeaderDao = ms2HeaderDao;
        this.ms2ScanDao = ms2ScanDao;
    }

    public RunFileFormat getRunFileFormat(int runId) throws Exception {
        return msRunDao.getRunFileFormat(runId);
    }

    /**
     * Saves the run along with MS2 file specific information
     */
    public int saveRun(IMS2Run run, int msExperimentId) {

        // save the run
        int runId = msRunDao.saveRun(run, 0);

        MS2FileHeaderDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
        for (IHeader header: run.getHeaderList()) {
            headerDao.save(header, runId);
        }
        return runId;
    }


    public MS2FileRun loadRun(int runId) {
        // MsRun.select has a discriminator and will instantiate the
        // appropriate type of run object
        return (MS2FileRun) queryForObject("MsRun.select", runId);
    }


    public List<MS2FileRun> loadExperimentRuns(int msExperimentId) {
        return queryForList("MsRun.selectRunsForExperiment", msExperimentId);
    }

    public List<Integer> loadRunIdsForExperiment(int msExperimentId) {
        return msRunDao.loadRunIdsForExperiment(msExperimentId);
    }

    public List<Integer> runIdsFor(String fileName, String sha1Sum) {

        return msRunDao.runIdsFor(fileName, sha1Sum);
    }


    /**
     * This will delete all the runs associated with the given experimentId, along with
     * any enzyme entries (msRunEnzyme table) associated with the runs, as well as the scans
     * MS2 file-format specific information is deleted too.
     * 
     * @param msExperimentId
     * @return List of run IDs that were deleted
     */
    public List<Integer> deleteRunsForExperiment(int msExperimentId) {

        List<Integer> runIds = loadRunIdsForExperiment(msExperimentId);
        for (Integer runId: runIds)
            delete(runId);
        
        return runIds;
    }

    public void delete(int runId) {

        // delete any headers
        ms2HeaderDao.deleteHeadersForRunId(runId);

        // delete any scans
        ms2ScanDao.deleteScansForRun(runId);

        msRunDao.delete(runId);

    }
}
