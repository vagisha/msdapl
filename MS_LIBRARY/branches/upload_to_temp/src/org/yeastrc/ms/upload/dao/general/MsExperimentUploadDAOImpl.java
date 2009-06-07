/**
 * MsExperimentDAOImpl.java
 * @author Vagisha Sharma
 * Sep 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.general;

import java.util.List;

import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. msExperiment
 * 2. msExperimentRun
 */
public class MsExperimentUploadDAOImpl extends AbstractTableCopier implements MsExperimentDAO {

    private final MsExperimentDAO exptDao;
    private final MsExperimentDAO mainExptDao;
    private final boolean useTempTable;
    
    /**
     * @param mainExptDao -- DAO for the MAIN database table
     * @param exptDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsExperimentUploadDAOImpl(MsExperimentDAO mainExptDao, MsExperimentDAO exptDao, boolean useTempTable) {
        this.mainExptDao = mainExptDao;
        this.exptDao = exptDao;
        this.useTempTable = useTempTable;
    }

    @Override
    /**
     * Returns all the experiment Ids in the temp table (if one is being used)
     * Otherwise, all experiment Ids in the main table are returned
     */
    public List<Integer> getAllExperimentIds() {
        return exptDao.getAllExperimentIds();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> getExperimentIdsForRun(int runId) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> getRunIdsForExperiment(int experimentId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MsExperiment loadExperiment(int experimentId) {
        MsExperiment expt = exptDao.loadExperiment(experimentId);
        if(expt == null) {
            // If the experiment is not found in the temp table, it could be in the 
            // main table.  this will happen if we had a partial failed upload and the 
            // experiment table got copied to the main database before the upload 
            // for the experiment was restarted.
            expt = mainExptDao.loadExperiment(experimentId);
        }
        return expt;
    }

    @Override
    public int saveExperiment(MsExperiment experiment) {
        return exptDao.saveExperiment(experiment);
    }
    
    @Override
    public void saveExperimentRun(int experimentId, int runId) {
        exptDao.saveExperimentRun(experimentId, runId);
    }
    
    public void updateLastUpdateDate(int experimentId) {
       exptDao.updateLastUpdateDate(experimentId); 
    }
    
    @Override
    public void updateComments(int experimentId, String comments) {
       exptDao.updateComments(experimentId, comments);
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int getMatchingExptRunCount(int experimentId, int runId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteExperiment(int experimentId) {
       log.info("Deleting experiment ID: "+experimentId);
       exptDao.deleteExperiment(experimentId); // deletes experiment from the temporary table
       if(useTempTable) {     
           log.info("Deleting experimentID: "+experimentId+" from the main database");
           mainExptDao.deleteExperiment(experimentId);  // deletes experiment from the main table
                                                        // if main table is different from temporary table.
       }
    }

    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            // copy entries from msExperiment table
            copyToMainTableDirect("msExperiment");
            // copy entries from msExperimentRun table
            copyToMainTableDirect("msExperimentRun");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
