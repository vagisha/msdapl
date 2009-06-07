/**
 * MsSearchUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 3, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search;

import java.util.List;

import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. msSearch
 */
public class MsSearchUploadDAOImpl extends AbstractTableCopier implements MsSearchDAO {

    private final MsSearchDAO mainSearchDao;
    private final MsSearchDAO searchDao;
    private final boolean useTempTable;
    
    /**
     * @param mainSearchDao -- DAO for the MAIN database table
     * @param searchDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsSearchUploadDAOImpl(MsSearchDAO mainSearchDao, MsSearchDAO searchDao, boolean useTempTable) {
        this.mainSearchDao = mainSearchDao;
        if(searchDao == null)
            this.searchDao = mainSearchDao;
        else
            this.searchDao = searchDao;
        this.useTempTable = useTempTable;
    }
    
    public MsSearch loadSearch(int searchId) {
        MsSearch search = searchDao.loadSearch(searchId);
        if(search == null && useTempTable)
            search = mainSearchDao.loadSearch(searchId);
        return search;
    }
    
    public int saveSearch(MsSearchIn search, int experimentId, int sequenceDatabaseId) {
        return searchDao.saveSearch(search, experimentId, sequenceDatabaseId);
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
    
    @Override
    public List<Integer> getSearchIdsForExperiment(int experimentId) {
        List<Integer> searchIds = searchDao.getSearchIdsForExperiment(experimentId);
        if(searchIds == null || searchIds.size() == 0) {
            if(useTempTable) {
                searchIds = mainSearchDao.getSearchIdsForExperiment(experimentId);
            }
        }
        return searchIds;
    }
    
    /**
     * Deletes the search from the temporary table, is using one.
     */
    public void deleteSearch(int searchId) {
        searchDao.deleteSearch(searchId);
    }

    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            // copy entries from the msSearch table
            copyToMainTableDirect("msSearch");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
