/**
 * SequestSearchResultUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.sequest;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.domain.search.sequest.SequestResultFilterCriteria;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. SQTSearchResult
 */
public class SequestSearchResultUploadDAOImpl extends AbstractTableCopier implements SequestSearchResultDAO {

    private final SequestSearchResultDAO resultDao;
    private final boolean useTempTable;
    
    /**
     * @param resultDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public SequestSearchResultUploadDAOImpl(SequestSearchResultDAO resultDao, boolean useTempTable) {
        this.resultDao = resultDao;
        this.useTempTable = useTempTable;
    }
    
    public SequestSearchResult load(int resultId) {
        return resultDao.load(resultId);
    }
    
    @Override
    public List<MsSearchResult> loadResultForSearchScanChargePeptide(int runSearchId,
            int scanId, int charge, String peptide) {
        return resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, charge, peptide);
    }
    
    @Override
    public List<Integer> loadResultIdsForRunSearch(int runSearchId) {
        return resultDao.loadResultIdsForRunSearch(runSearchId);
    }

    @Override
    public List<Integer> loadResultIdsForRunSearch(int runSearchId, int limit,
            int offset) {
        return resultDao.loadResultIdsForRunSearch(runSearchId, limit, offset);
    }

    @Override
    public List<Integer> loadResultIdsForSearch(int searchId) {
        return resultDao.loadResultIdsForSearch(searchId);
    }

    @Override
    public List<Integer> loadResultIdsForSearch(int searchId, int limit,
            int offset) {
        return resultDao.loadResultIdsForSearch(searchId, limit, offset);
    }

    @Override
    public int numRunSearchResults(int runSearchId) {
        return resultDao.numRunSearchResults(runSearchId);
    }

    @Override
    public int numSearchResults(int searchId) {
        return resultDao.numSearchResults(searchId);
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadTopResultIdsForRunSearch(int runSearchId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadResultIdsForRunSearch(int runSearchId,
            SequestResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadResultIdsForSearch(int searchId,
            SequestResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Method not supported -- not used for upload
     */
    public List<SequestSearchResult> loadTopResultsForRunSearchN(int runSearchId, boolean getDynaResMods) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<Integer> loadResultIdsForSearchScanCharge(int runSearchId, int scanId, int charge) {
        return resultDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, charge);
    }
    
    @Override
    public int numResultsForRunSearchScanChargeMass(int runSearchId,
            int scanId, int charge, BigDecimal mass) {
        return resultDao.numResultsForRunSearchScanChargeMass(runSearchId, scanId, charge, mass);
    }
    
    @Override
    public List<Integer> loadResultIdsForSearchScan(int runSearchId, int scanId) {
        return resultDao.loadResultIdsForSearchScan(runSearchId, scanId);
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int save(int searchId, SequestSearchResultIn searchResult, int runSearchId, int scanId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int saveResultOnly(SequestSearchResultIn searchResult, int runSearchId,
            int scanId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void saveAllSequestResultData(List<SequestResultDataWId> resultDataList) {
        resultDao.saveAllSequestResultData(resultDataList);
    }
    
    /**
     * Deletes the search result and any Sequest specific information associated with the result
     * @param resultId
     */
    public void delete(int resultId) {
        resultDao.delete(resultId);
    }

    @Override
    public void deleteResultsForRunSearch(int runSearchId) {
        resultDao.deleteResultsForRunSearch(runSearchId);
    }
    
    @Override
    public void disableKeys() throws SQLException {
        resultDao.disableKeys();
    }

    @Override
    public void enableKeys() throws SQLException {
        resultDao.enableKeys();
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            // copy entries from the required table
            copyToMainTableFromFile("SQTSearchResult", true); // disable keys on main database table before copying 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
