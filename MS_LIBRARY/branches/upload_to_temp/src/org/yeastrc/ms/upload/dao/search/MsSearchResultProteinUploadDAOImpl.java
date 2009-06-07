/**
 * MsProteinMatchDAO.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. msProteinMatch
 */
public class MsSearchResultProteinUploadDAOImpl extends AbstractTableCopier implements MsSearchResultProteinDAO {


    private final MsSearchResultProteinDAO proteinDao;
    private final boolean useTempTable;
    
    /**
     * @param proteinDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsSearchResultProteinUploadDAOImpl(MsSearchResultProteinDAO proteinDao, boolean useTempTable) {
        this.proteinDao = proteinDao;
        this.useTempTable = useTempTable;
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<MsSearchResultProtein> loadResultProteins(int resultId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public void save(MsSearchResultProteinIn resultProtein, int resultId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void saveAll(List<MsSearchResultProtein> proteinMatchList) {
        proteinDao.saveAll(proteinMatchList);
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public void delete(int resultId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Will disable keys on the temporary table, if using one
     */
    public void disableKeys() throws SQLException {
        proteinDao.disableKeys();
    }

    @Override
    /**
     * Will enable keys on the temporary table, if using one
     */
    public void enableKeys() throws SQLException {
        proteinDao.enableKeys();
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            // copy entries from the required table
            copyToMainTableFromFile("msProteinMatch", true); // disable keys before copying 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}


