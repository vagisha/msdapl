/**
 * MsSearchDatabaseUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 3, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search;

import java.util.List;

import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. msSequenceDatabaseDetail
 * 2. msSearchDatabase
 */
public class MsSearchDatabaseUploadDAOImpl extends AbstractTableCopier implements MsSearchDatabaseDAO {

    private final MsSearchDatabaseDAO searchDatabaseDao;
    private final MsSearchDatabaseDAO mainSearchDatabaseDao;
    private final boolean useTempTable;
    
    /**
     * @param mainSearchDatabaseDao -- DAO for the MAIN database table
     * @param searchDatabaseDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsSearchDatabaseUploadDAOImpl(MsSearchDatabaseDAO mainSearchDatabaseDao, 
            MsSearchDatabaseDAO searchDatabaseDao, 
            boolean useTempTable) {
        this.mainSearchDatabaseDao = mainSearchDatabaseDao;
        if(searchDatabaseDao == null)
            this.searchDatabaseDao = mainSearchDatabaseDao;
        else
            this.searchDatabaseDao = searchDatabaseDao;
        this.useTempTable = useTempTable;
    }
    
    /**
     * @param mainSearchDatabaseDao -- DAO for the MAIN database table.  If a temp table is being used this DAO 
     *                                 will be used for the temp table as well.
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsSearchDatabaseUploadDAOImpl(MsSearchDatabaseDAO mainSearchDatabaseDao, boolean useTempTable) {
        this(mainSearchDatabaseDao, mainSearchDatabaseDao, useTempTable);
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<MsSearchDatabase> loadSearchDatabases(int searchId) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Method not supported -- not used for upload
     */
    public void deleteSearchDatabases(int searchId) {
        throw new UnsupportedOperationException();
    }
    
    public int saveSearchDatabase(MsSearchDatabase database, int searchId) {
        
        int databaseId = 0;
        try{ databaseId = database.getId();}
        catch(Exception e){}
        if(databaseId == 0) {
            List<Integer> dbIds = loadMatchingDatabaseIds(database);
            if (dbIds.size() > 0) {
                databaseId = dbIds.get(0);
            }
            else {
                databaseId = saveDatabase(database);
            }
        }
        linkDatabaseAndSearch(databaseId, searchId);
        return databaseId;
    }
    
    
    public List<Integer> loadMatchingDatabaseIds(MsSearchDatabase database) {
        return mainSearchDatabaseDao.loadMatchingDatabaseIds(database);
    }
    
    public int saveDatabase(MsSearchDatabase database) {
        return mainSearchDatabaseDao.saveDatabase(database);
    }

    @Override
    public int getSequenceDatabaseId(String serverPath) {
        return mainSearchDatabaseDao.getSequenceDatabaseId(serverPath);
    }

    @Override
    public void linkDatabaseAndSearch(int databaseId, int searchId) {
        searchDatabaseDao.linkDatabaseAndSearch(databaseId, searchId);
    }

    @Override
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            // copy entries from the msSearchDatabase table
            copyToMainTableDirect("msSearchDatabase");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
