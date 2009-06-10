/**
 * MsExperimentDAOImpl.java
 * @author Vagisha Sharma
 * Sep 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.general.dualdb;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.general.MsExperimentUploadDAO;

/**
 * Deals with the tables:
 * 1. msExperiment
 * 2. msExperimentRun
 */
public class MsExperimentUploadDAOImpl implements MsExperimentUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(MsExperimentUploadDAOImpl.class.getName());
    
    private final MsExperimentUploadDAO exptDao;
    private final MsExperimentUploadDAO mainExptDao;
    private final boolean useTempTable;
    
    /**
     * @param mainExptDao -- DAO for the MAIN database table
     * @param exptDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsExperimentUploadDAOImpl(MsExperimentUploadDAO mainExptDao, 
            MsExperimentUploadDAO exptDao, boolean useTempTable) {
        this.mainExptDao = mainExptDao;
        if(exptDao == null)
            this.exptDao = mainExptDao;
        else
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
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from msExperiment table
            copier.copyToMainTableDirect("msExperiment");
            // copy entries from msExperimentRun table
            copier.copyToMainTableDirect("msExperimentRun");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
   
}
