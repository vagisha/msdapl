/**
 * SequestSearchDAO.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sequest;

import java.util.List;

import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. SQTParams
 * 2. msSearch (only for the loadSearch method)
 */
public class SequestSearchUploadDAOImpl extends AbstractTableCopier implements SequestSearchDAO {

    private final SequestSearchDAO searchDao;
    private final boolean useTempTable;
    
    
    /**
     * @param searchDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public SequestSearchUploadDAOImpl(SequestSearchDAO searchDao, boolean useTempTable) {
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
    public MassType getFragmentMassType(int searchId) {
        return searchDao.getFragmentMassType(searchId);
    }

    @Override
    public MassType getParentMassType(int searchId) {
        return searchDao.getParentMassType(searchId);
    }
    
    @Override
    public String getSearchParamValue(int searchId, String paramName) {
       return searchDao.getSearchParamValue(searchId, paramName);
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
            // copy entries from the required table
            copyToMainTableDirect("SQTParams"); 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
