package org.yeastrc.ms.upload.dao.search.dualdb;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.search.MsSearchResultUploadDAO;

/**
 * Deals with the tables:
 * 1. msRunSearchResult
 */

public class MsSearchResultUploadDAOImpl implements MsSearchResultUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(MsSearchResultUploadDAOImpl.class.getName());
    
    private final MsSearchResultUploadDAO mainResultDao;
    private final MsSearchResultUploadDAO resultDao;
    private final boolean useTempTable;
    
    /**
     * @param mainResultDao -- DAO for the MAIN database table
     * @param resultDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsSearchResultUploadDAOImpl(MsSearchResultUploadDAO mainResultDao, MsSearchResultUploadDAO resultDao, 
            boolean useTempTable) {
        this.mainResultDao = mainResultDao;
        if(resultDao == null)
            this.resultDao = mainResultDao;
        else
            this.resultDao = resultDao;
        this.useTempTable = useTempTable;
    }

    @Override
    public List<MsSearchResult> loadResultForSearchScanChargePeptide(int runSearchId,
            int scanId, int charge, String peptide) {
        List<MsSearchResult> results = resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, charge, peptide);
        if(results == null || results.size() == 0) {
            if(useTempTable) {
                results = mainResultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, charge, peptide);
            }
        }
        return results;
    }
    
    @Override
    public int numResultsForRunSearchScanChargeMass(int runSearchId, int scanId,
            int charge, BigDecimal mass) {
        int count = resultDao.numResultsForRunSearchScanChargeMass(runSearchId, scanId, charge, mass);
        if(count == 0) {
            count = mainResultDao.numResultsForRunSearchScanChargeMass(runSearchId, scanId, charge, mass);
        }
        return count;
    }
    
    public int saveResultOnly(MsSearchResultIn searchResult, int runSearchId, int scanId) {
        return resultDao.saveResultOnly(searchResult, runSearchId, scanId);
    }

    @Override
    public <T extends MsSearchResult> List<Integer> saveResultsOnly(List<T> results) {
        return resultDao.saveResultsOnly(results);
    }
    
    @Override
    /**
     * Deletes results from the temporary table, if using one
     */
    public void deleteResultsForRunSearch(int runSearchId) {
        resultDao.deleteResultsForRunSearch(runSearchId);
    }
    
    @Override
    /**
     * Will disable keys on the temporary table, if using one
     */
    public void disableKeys() throws SQLException {
        resultDao.disableKeys();
    }

    @Override
    /**
     * Will enable keys on the temporary table, if using one
     */
    public void enableKeys() throws SQLException {
        resultDao.enableKeys();
    }
    
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required table
            copier.copyToMainTableFromFile("msRunSearchResult", true); // disable keys before copying 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("msRunSearchResult", "id"))
            return false;
        return true;
    }
}
