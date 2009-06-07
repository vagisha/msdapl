/**
 * ProlucidSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.prolucid;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataWId;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultIn;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;


/**
 * Deals with the tables:
 * 1. ProLuCIDSearchResult
 */
public class ProlucidSearchResultUploadDAOImpl extends AbstractTableCopier implements ProlucidSearchResultDAO {

    private final ProlucidSearchResultDAO resultDao;
    private final boolean useTempTable;
    
    /**
     * @param resultDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public ProlucidSearchResultUploadDAOImpl(ProlucidSearchResultDAO resultDao, boolean useTempTable) {
        this.resultDao = resultDao;
        this.useTempTable = useTempTable;
    }

    @Override
    public ProlucidSearchResult load(int resultId) {
        return resultDao.load(resultId);
    }

    @Override
    public List<MsSearchResult> loadResultForSearchScanChargePeptide(int runSearchId,
            int scanId, int charge, String peptide) {
        return resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, charge, peptide);
    }
    
    @Override
    public int numResultsForRunSearchScanChargeMass(int runSearchId,
            int scanId, int charge, BigDecimal mass) {
        return resultDao.numResultsForRunSearchScanChargeMass(runSearchId, scanId, charge, mass);
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
    
    /**
     * Method not supported -- not used for upload
     */
    public List<ProlucidSearchResult> loadTopResultsForRunSearchN(int runSearchId, boolean getDynaResMods) {
        throw new UnsupportedOperationException();
    }
 
    @Override
    public List<Integer> loadResultIdsForSearchScanCharge(int runSearchId,
            int scanId, int charge) {
        return resultDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, charge);
    }

    @Override
    public List<Integer> loadResultIdsForSearchScan(int runSearchId, int scanId) {
        return resultDao.loadResultIdsForSearchScan(runSearchId, scanId);
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int save(int searchId, ProlucidSearchResultIn searchResult, int runSearchId, int scanId) {
       throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int saveResultOnly(ProlucidSearchResultIn searchResult,
            int runSearchId, int scanId) {
        throw new UnsupportedOperationException();
    }

    /**
     * resultID, 
        primaryScoreRank,
        secondaryScoreRank,
        primaryScore,
        secondaryScore,
        deltaCN, 
        calculatedMass,
        matchingIons,
        predictedIons
     */
    @Override
    public void saveAllProlucidResultData(
            List<ProlucidResultDataWId> resultDataList) {
        resultDao.saveAllProlucidResultData(resultDataList);
    }

    @Override
    public void delete(int resultId) {
        resultDao.delete(resultId);
    }

    @Override
    public void deleteResultsForRunSearch(int runSearchId) {
        resultDao.deleteResultsForRunSearch(runSearchId);
    }
    
  //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            // copy entries from the required table
            copyToMainTableFromFile("ProLuCIDSearchResult", true); // disable keys on main database table before copying 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
