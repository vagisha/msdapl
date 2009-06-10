/**
 * SQTRunSearchUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sqtfile.dualdb;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTRunSearchUploadDAO;

/**
 * Does not deals with with any tables of its own.  Uses other DAOs that
 * deal with SQTFileHeader and msRunSearch tables.
 */
public class SQTRunSearchUploadDAOImpl implements SQTRunSearchUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(SQTRunSearchUploadDAOImpl.class.getName());
    
    private final SQTRunSearchUploadDAO runSearchDao;
    private final boolean useTempTable;
    
    /**
     * @param runSearchDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public SQTRunSearchUploadDAOImpl(SQTRunSearchUploadDAO runSearchDao, boolean useTempTable) {
        this.runSearchDao = runSearchDao;
        this.useTempTable = useTempTable;
    }
    
    public SQTRunSearch loadRunSearch(int runSearchId) {
        return runSearchDao.loadRunSearch(runSearchId);
    }
    
    @Override
    public int loadIdForSearchAndFileName(int searchId, String filename) {
        return runSearchDao.loadIdForSearchAndFileName(searchId, filename);
    }
    
    /**
     * Saves the search as well as any SQT headers associated with the search.
     * @param runSearch
     * @return
     */
    public int saveRunSearch (SQTRunSearch runSearch) {
        return runSearchDao.saveRunSearch(runSearch);
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
