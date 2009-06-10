/**
 * MsProteinMatchDAO.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.dualdb;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.search.MsSearchResultProteinUploadDAO;

/**
 * Deals with the tables:
 * 1. msProteinMatch
 */
public class MsSearchResultProteinUploadDAOImpl implements MsSearchResultProteinUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(MsSearchResultProteinUploadDAOImpl.class.getName());

    private final MsSearchResultProteinUploadDAO proteinDao;
    private final boolean useTempTable;
    
    /**
     * @param proteinDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsSearchResultProteinUploadDAOImpl(MsSearchResultProteinUploadDAO proteinDao, boolean useTempTable) {
        this.proteinDao = proteinDao;
        this.useTempTable = useTempTable;
    }

    @Override
    public void saveAll(List<MsSearchResultProtein> proteinMatchList) {
        proteinDao.saveAll(proteinMatchList);
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
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required table
            copier.copyToMainTableFromFile("msProteinMatch", true); // disable keys before copying 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("msProteinMatch", "resultID"))
            return false;
        return true;
    }
}


