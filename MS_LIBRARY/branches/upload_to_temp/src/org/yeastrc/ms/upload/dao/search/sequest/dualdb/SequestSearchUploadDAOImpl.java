/**
 * SequestSearchDAO.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sequest.dualdb;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.search.sequest.SequestSearchUploadDAO;

/**
 * Deals with the tables:
 * 1. SQTParams
 * 2. msSearch (only for the loadSearch method)
 */
public class SequestSearchUploadDAOImpl implements SequestSearchUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(SequestSearchUploadDAOImpl.class.getName());
    
    private final SequestSearchUploadDAO searchDao;
    private final boolean useTempTable;
    
    
    /**
     * @param searchDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public SequestSearchUploadDAOImpl(SequestSearchUploadDAO searchDao, boolean useTempTable) {
        this.searchDao = searchDao;
        this.useTempTable = useTempTable;
    }
    
    public SequestSearch loadSearch(int searchId) {
        return searchDao.loadSearch(searchId);
    }
    
    public int saveSearch(SequestSearchIn search, int experimentId, int sequenceDatabaseId) {
       return searchDao.saveSearch(search, experimentId, sequenceDatabaseId);
    }
    
    @Override
    public List<Integer> getSearchIdsForExperiment(int experimentId) {
        return searchDao.getSearchIdsForExperiment(experimentId);
    }
    
    @Override
    public int updateSearchProgramVersion(int searchId,
            String versionStr) {
        return searchDao.updateSearchProgramVersion(searchId, versionStr);
    }
    
    @Override
    public int updateSearchProgram(int searchId, Program program) {
        return searchDao.updateSearchProgram(searchId, program);
    }
    
    public void deleteSearch(int searchId) {
        searchDao.deleteSearch(searchId);
    }

    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required table
            copier.copyToMainTableDirect("SQTParams"); 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("SQTParams", "id"))
            return false;
        return true;
    }
}
