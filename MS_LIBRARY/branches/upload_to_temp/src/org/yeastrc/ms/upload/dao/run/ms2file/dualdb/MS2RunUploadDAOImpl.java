/**
 * MS2RunUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.ms2file.dualdb;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.run.MsRunUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2RunUploadDAO;

/**
 * Deals with tables:
 * 1. MS2FileHeader
 * 2. msRunSearch
 * 3. msRunLocation
 */
public class MS2RunUploadDAOImpl implements MS2RunUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(MS2RunUploadDAOImpl.class.getName());
    
    private final MS2RunUploadDAO ms2RunDao; 
    private final MsRunUploadDAO msRunDao;
    private final boolean useTempTable;
    
    /**
     * @param ms2RunDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param msRunDao
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MS2RunUploadDAOImpl(MS2RunUploadDAO ms2RunDao, MsRunUploadDAO msRunDao, boolean useTempTable) {
        this.ms2RunDao = ms2RunDao;
        this.msRunDao = msRunDao;
        this.useTempTable = useTempTable;
    }

    /**
     * Saves the run along with MS2 file specific information
     */
    public int saveRun(MS2RunIn run, String serverDirectory) {
        return ms2RunDao.saveRun(run, serverDirectory);
    }

    @Override
    public void saveRunLocation(String serverDirectory, int runId) {
        ms2RunDao.saveRunLocation(serverDirectory, runId);
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
    
    @Override
    public Integer loadRunIdForExperimentAndFileName(int experimentId,
            String runFileName) {
        return msRunDao.loadRunIdForExperimentAndFileName(experimentId, runFileName);
    }
   
    public void delete(int runId) {
        msRunDao.delete(runId);
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            // copy entries from the required tables
            TableCopyUtil.getInstance().copyToMainTableDirect("MS2FileHeader");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
