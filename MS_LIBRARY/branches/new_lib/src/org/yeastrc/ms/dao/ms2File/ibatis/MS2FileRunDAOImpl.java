package org.yeastrc.ms.dao.ms2File.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.MsDigestionEnzymeDAO;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.dao.ms2File.MS2FileHeaderDAO;
import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsRun.RunFileFormat;
import org.yeastrc.ms.domain.ms2File.MS2FileHeader;
import org.yeastrc.ms.domain.ms2File.MS2FileRun;
import org.yeastrc.ms.domain.ms2File.MS2FileScan;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MS2FileRunDAOImpl extends BaseSqlMapDAO implements MsRunDAO<MS2FileRun> {

    public MS2FileRunDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }


    public RunFileFormat getRunFileFormat(int runId) throws Exception {
        MsRunDAO<MsRun> runDao = DAOFactory.instance().getMsRunDAO();
        return runDao.getRunFileFormat(runId);
    }

    /**
     * Saves the run along with MS2 file specific information
     */
    public int saveRun(MS2FileRun run) {

        // save the run
        MsRunDAO<MsRun> runDao = DAOFactory.instance().getMsRunDAO();
        int runId = runDao.saveRun(run);

        MS2FileHeaderDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
        List<MS2FileHeader> headers = run.getHeaderList();
        for (MS2FileHeader header: headers) {
            header.setRunId(runId);
            headerDao.save(header);
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
        MsRunDAO<MsRun> runDao = DAOFactory.instance().getMsRunDAO();
        return runDao.loadRunIdsForExperiment(msExperimentId);
    }

    public List<Integer> runIdsFor(String fileName, String sha1Sum) {

        MsRunDAO<MsRun> runDao = DAOFactory.instance().getMsRunDAO();
        return runDao.runIdsFor(fileName, sha1Sum);
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

        if (runIds.size() > 0) {
            // delete enzyme associations
            MsDigestionEnzymeDAO enzymeDao = DAOFactory.instance().getEnzymeDAO();
            enzymeDao.deleteEnzymesByRunIds(runIds);
            
            // delete any MS2 header data for the given run ids
            MS2FileHeaderDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
            headerDao.deleteHeadersForRunIds(runIds);
        }

        for (Integer runId: runIds) {
            // delete scans for this run
            MsScanDAO<MS2FileScan> scanDao = DAOFactory.instance().getMS2FileScanDAO();
            scanDao.deleteScansForRun(runId);
        }

        // finally, delete the runs
        delete("MsRun.deleteByExperimentId", msExperimentId);
        return runIds;
    }

    public void delete(int runId) {

        // delete any headers
        MS2FileHeaderDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
        headerDao.deleteHeadersForRunId(runId);

        // delete enzyme information
        MsDigestionEnzymeDAO enzymeDao = DAOFactory.instance().getEnzymeDAO();
        enzymeDao.deleteEnzymesByRunId(runId);

        // delete any scans
        MsScanDAO<MS2FileScan> scanDao = DAOFactory.instance().getMS2FileScanDAO();
        scanDao.deleteScansForRun(runId);

        MsRunDAO<MsRun> runDao = DAOFactory.instance().getMsRunDAO();
        runDao.delete(runId);

        // delete the run
        delete("MsRun.delete", runId);

    }
}
