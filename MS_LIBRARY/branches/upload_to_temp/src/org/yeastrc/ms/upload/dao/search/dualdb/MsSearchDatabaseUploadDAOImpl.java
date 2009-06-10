/**
 * MsSearchDatabaseUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 3, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.dualdb;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.search.MsSearchDatabaseUploadDAO;

/**
 * Deals with the tables:
 * 1. msSequenceDatabaseDetail
 * 2. msSearchDatabase
 */
public class MsSearchDatabaseUploadDAOImpl implements MsSearchDatabaseUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(MsSearchDatabaseUploadDAOImpl.class.getName());
    
    private final MsSearchDatabaseUploadDAO searchDatabaseDao;
    private final MsSearchDatabaseUploadDAO mainSearchDatabaseDao;
    private final boolean useTempTable;
    
    /**
     * @param mainSearchDatabaseDao -- DAO for the MAIN database table
     * @param searchDatabaseDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsSearchDatabaseUploadDAOImpl(MsSearchDatabaseUploadDAO mainSearchDatabaseDao, 
            MsSearchDatabaseUploadDAO searchDatabaseDao, 
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
    public MsSearchDatabaseUploadDAOImpl(MsSearchDatabaseUploadDAO mainSearchDatabaseDao, boolean useTempTable) {
        this(mainSearchDatabaseDao, mainSearchDatabaseDao, useTempTable);
    }

    public int saveSearchDatabase(MsSearchDatabase database, int searchId) {
        
        int databaseId = 0;
        try{ databaseId = database.getId();}
        catch(Exception e){}
        if(databaseId == 0) {
            List<Integer> dbIds = loadMatchingDatabaseIds(database); // this will come from the main database
            if (dbIds.size() > 0) {
                databaseId = dbIds.get(0);
            }
            else {
                databaseId = saveDatabase(database); // will be saved to the main database
            }
        }
        linkDatabaseAndSearch(databaseId, searchId); // entry created in the temo database.
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
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the msSearchDatabase table
            copier.copyToMainTableDirect("msSearchDatabase");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
