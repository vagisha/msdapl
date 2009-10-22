/**
 * MascotSearchResultUploadDAOImpl.java
 * @author Vagisha Sharma
 * Oct 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.xtandem.dualdb;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.xtandem.XtandemResultDataWId;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.search.xtandem.XtandemSearchResultUploadDAO;

/**
 * Deals with the tables:
 * 1. XtandemSearchResult
 */
public class XtandemSearchResultUploadDAOImpl implements XtandemSearchResultUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(XtandemSearchResultUploadDAOImpl.class.getName());
    
    private final XtandemSearchResultUploadDAO resultDao;
    private final boolean useTempTable;
    
    /**
     * @param resultDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public XtandemSearchResultUploadDAOImpl(XtandemSearchResultUploadDAO resultDao, boolean useTempTable) {
        this.resultDao = resultDao;
        this.useTempTable = useTempTable;
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
    public int saveResultOnly(MsSearchResultIn searchResult, int runSearchId,
            int scanId) {
        return resultDao.saveResultOnly(searchResult, runSearchId, scanId);
    }
    
    @Override
    public <T extends MsSearchResult> List<Integer> saveResultsOnly(List<T> results) {
        return resultDao.saveResultsOnly(results);
    }
    
    @Override
    public void saveAllXtandemResultData(List<XtandemResultDataWId> resultDataList) {
        resultDao.saveAllXtandemResultData(resultDataList);
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
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required table
            copier.copyToMainTableFromFile("XtandemSearchResult", true); // disable keys on main database table before copying 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("XtandemSearchResult", "resultID"))
            return false;
        return true;
    }
}
