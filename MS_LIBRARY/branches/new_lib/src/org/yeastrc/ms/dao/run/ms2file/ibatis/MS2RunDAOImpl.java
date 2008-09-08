package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2HeaderDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.domain.run.MsRunLocation;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MS2RunDAOImpl extends BaseSqlMapDAO implements MS2RunDAO {

    private MsRunDAO msRunDao;
    
    public MS2RunDAOImpl(SqlMapClient sqlMap, MsRunDAO msRunDao) {
        super(sqlMap);
        this.msRunDao = msRunDao;
    }

    public RunFileFormat getRunFileFormat(int runId) throws Exception {
        return msRunDao.getRunFileFormat(runId);
    }

    /**
     * Saves the run along with MS2 file specific information
     */
    public int saveRun(MS2RunIn run, String serverAddress, String serverDirectory) {

        // save the run and location
        int runId = msRunDao.saveRun(run, serverAddress, serverDirectory);
        try {
            MS2HeaderDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
            for (MS2NameValuePair header: run.getHeaderList()) {
                headerDao.save(header, runId);
            }
        }
        catch(RuntimeException e) {
            delete(runId);// this will delete anything that got saved with this runId;
            throw e;
        }
        return runId;
    }

    @Override
    public void saveRunLocation(String serverAddress, String serverDirectory,
            int runId) {
        msRunDao.saveRunLocation(serverAddress, serverDirectory, runId);
    }
    
    public MS2Run loadRun(int runId) {
        // MsRun.select has a discriminator and will instantiate the
        // appropriate type of run object
        return (MS2Run) queryForObject("MsRun.select", runId);
    }

    @Override
    public List<MS2Run> loadRuns(List<Integer> runIdList) {
        if (runIdList.size() == 0)
            return new ArrayList<MS2Run>(0);
        StringBuilder buf = new StringBuilder();
        for (Integer i: runIdList) {
            buf.append(","+i);
        }
        buf.deleteCharAt(0);
        return queryForList("MsRun.selectRuns", buf.toString());
    }
    
    @Override
    public List<MsRunLocation> loadLocationsForRun(int runId) {
        return msRunDao.loadLocationsForRun(runId);
    }

    @Override
    public List<MsRunLocation> loadMatchingRunLocations(int runId,
            String serverAddress, String serverDirectory) {
        return msRunDao.loadMatchingRunLocations(runId, serverAddress, serverDirectory);
    }
    
    @Override
    public List<Integer> loadRunIdsForFileName(String fileName) {
        return msRunDao.loadRunIdsForFileName(fileName);
    }
    
    public List<Integer> loadRunIdsForFileNameAndSha1Sum(String fileName, String sha1Sum) {
        return msRunDao.loadRunIdsForFileNameAndSha1Sum(fileName, sha1Sum);
    }

    @Override
    public int loadRunIdForSearchAndFileName(int searchId, String runFileName) {
        return msRunDao.loadRunIdForSearchAndFileName(searchId, runFileName);
    }
    
    public void delete(int runId) {
        msRunDao.delete(runId);
    }
}
