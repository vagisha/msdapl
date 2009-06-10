package org.yeastrc.ms.upload.dao.run.ms2file.ibatis;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;
import org.yeastrc.ms.upload.dao.run.MsRunUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2HeaderUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2RunUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MS2RunUploadDAOIbatisImpl extends BaseSqlMapDAO implements MS2RunUploadDAO {

    private MsRunUploadDAO msRunDao;
    private MS2HeaderUploadDAO ms2HeaderDao;
    
    public MS2RunUploadDAOIbatisImpl(SqlMapClient sqlMap, MsRunUploadDAO msRunDao, MS2HeaderUploadDAO ms2headerDao) {
        super(sqlMap);
        this.msRunDao = msRunDao;
        this.ms2HeaderDao = ms2headerDao;
    }

    /**
     * Saves the run along with MS2 file specific information
     */
    public int saveRun(MS2RunIn run, String serverDirectory) {

        // save the run and location
        int runId = msRunDao.saveRun(run, serverDirectory);
        try {
            for (MS2NameValuePair header: run.getHeaderList()) {
                ms2HeaderDao.save(header, runId);
            }
        }
        catch(RuntimeException e) {
            delete(runId);// this will delete anything that got saved with this runId;
            throw e;
        }
        return runId;
    }

    @Override
    public void saveRunLocation(String serverDirectory, int runId) {
        msRunDao.saveRunLocation(serverDirectory, runId);
    }
    
    @Override
    public int loadMatchingRunLocations(int runId, String serverDirectory) {
        return msRunDao.loadMatchingRunLocations(runId, serverDirectory);
    }
    
    public int loadRunIdForFileNameAndSha1Sum(String fileName, String sha1Sum) {
        return msRunDao.loadRunIdForFileNameAndSha1Sum(fileName, sha1Sum);
    }

    @Override
    public int loadRunIdForSearchAndFileName(int searchId, String runFileName) {
        return msRunDao.loadRunIdForSearchAndFileName(searchId, runFileName);
    }
    
    public void delete(int runId) {
        msRunDao.delete(runId);
    }

    @Override
    public Integer loadRunIdForExperimentAndFileName(int experimentId,
            String runFileName) {
        return msRunDao.loadRunIdForExperimentAndFileName(experimentId, runFileName);
    }
}
