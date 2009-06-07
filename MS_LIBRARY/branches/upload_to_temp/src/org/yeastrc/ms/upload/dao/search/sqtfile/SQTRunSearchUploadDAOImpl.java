/**
 * SQTRunSearchUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sqtfile;

import java.util.List;

import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Does not deals with with any tables of its own.  Uses other DAOs that
 * deal with SQTFileHeader and msRunSearch tables.
 */
public class SQTRunSearchUploadDAOImpl extends AbstractTableCopier implements SQTRunSearchDAO {

    private final SQTRunSearchDAO runSearchDao;
    private final boolean useTempTable;
    
    /**
     * @param runSearchDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public SQTRunSearchUploadDAOImpl(SQTRunSearchDAO runSearchDao, boolean useTempTable) {
        this.runSearchDao = runSearchDao;
        this.useTempTable = useTempTable;
    }
    
    public SQTRunSearch loadRunSearch(int runSearchId) {
        return runSearchDao.loadRunSearch(runSearchId);
    }
    
    public List<Integer> loadRunSearchIdsForSearch(int searchId) {
        return runSearchDao.loadRunSearchIdsForSearch(searchId);
    }
    
    public List<Integer> loadRunSearchIdsForRun(int runId) {
        return runSearchDao.loadRunSearchIdsForRun(runId);
    }
    
    public int loadIdForRunAndSearch(int runId, int searchId) {
        return runSearchDao.loadIdForRunAndSearch(runId, searchId);
    }
    
    @Override
    public int loadIdForSearchAndFileName(int searchId, String filename) {
        return runSearchDao.loadIdForSearchAndFileName(searchId, filename);
    }
    
    @Override
    public String loadFilenameForRunSearch(int runSearchId) {
        return runSearchDao.loadFilenameForRunSearch(runSearchId);
    }
    
    @Override
    public Program loadSearchProgramForRunSearch(int runSearchId) {
        return runSearchDao.loadSearchProgramForRunSearch(runSearchId);
    }
    
    @Override
    public int numResults(int runSearchId) {
        return runSearchDao.numResults(runSearchId);
    }
    
    /**
     * Saves the search as well as any SQT headers associated with the search.
     * @param runSearch
     * @return
     */
    public int saveRunSearch (SQTRunSearch runSearch) {
        return runSearchDao.saveRunSearch(runSearch);
    }
    
    /**
     * Deletes the search
     * @param searchId
     */
    public void deleteRunSearch (int searchId) {
        runSearchDao.deleteRunSearch(searchId);
    }

    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            log.info("No tables to copy in "+SQTRunSearchUploadDAOImpl.class.getName());
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
