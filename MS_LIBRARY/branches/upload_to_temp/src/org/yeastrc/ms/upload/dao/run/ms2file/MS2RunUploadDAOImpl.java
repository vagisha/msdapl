/**
 * MS2RunUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.ms2file;

import java.util.List;

import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.domain.run.MsRunLocation;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;
import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with tables:
 * 1. MS2FileHeader
 * 2. msRunSearch
 * 3. msRunLocation
 */
public class MS2RunUploadDAOImpl extends AbstractTableCopier implements MS2RunDAO {

    private final MS2RunDAO mainMs2RunDao;
    private final MS2RunDAO ms2RunDao; 
    private final boolean useTempTable;
    
    /**
     * @param mainMs2RunDao -- DAO for the MAIN database table
     * @param ms2RunDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MS2RunUploadDAOImpl(MS2RunDAO mainMs2RunDao, MS2RunDAO ms2RunDao, boolean useTempTable) {
        this.ms2RunDao = ms2RunDao;
        this.mainMs2RunDao = mainMs2RunDao;
        this.useTempTable = useTempTable;
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public RunFileFormat getRunFileFormat(int runId) throws Exception {
        throw new UnsupportedOperationException();
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
    
    /**
     * Method not supported -- not used for upload
     */
    public MS2Run loadRun(int runId) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<MS2Run> loadRuns(List<Integer> runIdList) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<MsRunLocation> loadLocationsForRun(int runId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int loadMatchingRunLocations(int runId, String serverDirectory) {
        // look in the main database table first
        int matchcount = mainMs2RunDao.loadMatchingRunLocations(runId, serverDirectory);
        if(matchcount == 0 && useTempTable) {
            // look in the temp database table
            matchcount += ms2RunDao.loadMatchingRunLocations(runId, serverDirectory);
        }
        return matchcount;
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadRunIdsForFileName(String fileName) {
        throw new UnsupportedOperationException();
    }
    
    public int loadRunIdForFileNameAndSha1Sum(String fileName, String sha1Sum) {
        // look in the main database table first
        int runId = mainMs2RunDao.loadRunIdForFileNameAndSha1Sum(fileName, sha1Sum);
        if(runId == 0 && useTempTable) {
            runId = ms2RunDao.loadRunIdForFileNameAndSha1Sum(fileName, sha1Sum);
        }
        return runId;
    }

    @Override
    /**
     * Method not supported
     */
    public int loadRunIdForSearchAndFileName(int searchId, String runFileName) {
        throw new UnsupportedOperationException();
    }
    
    public void delete(int runId) {
        ms2RunDao.delete(runId);
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public String loadFilenameForRun(int runId) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public Integer loadRunIdForExperimentAndFileName(int experimentId,
            String runFileName) {
        throw new UnsupportedOperationException();
    }
   
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            // copy entries from the required tables
            copyToMainTableDirect("MS2FileHeader");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
