/**
 * ProlucidSearchDAOImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.prolucid;

import java.util.List;

import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchDAO;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearch;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchIn;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. SQTParams
 * 2. ProLuCIDParams (only for the loadSearch method)
 */
public class ProlucidSearchUploadDAOImpl extends AbstractTableCopier implements ProlucidSearchDAO {

    
    private final ProlucidSearchDAO searchDao;
    private final boolean useTempTable;
    
    /**
     * @param searchDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public ProlucidSearchUploadDAOImpl(ProlucidSearchDAO searchDao, boolean useTempTable) {
        this.searchDao = searchDao;
        this.useTempTable = useTempTable;
    }
    
    public ProlucidSearch loadSearch(int searchId) {
        return searchDao.loadSearch(searchId);
    }
    
    public int saveSearch(ProlucidSearchIn search, int experimentId, int sequenceDatabaseId) {
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
            copyToMainTableDirect("ProLuCIDParams");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
