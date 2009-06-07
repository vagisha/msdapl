package org.yeastrc.ms.upload.dao.search;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. msRunSearchResult
 */

public class MsSearchResultUploadDAOImpl extends AbstractTableCopier implements MsSearchResultDAO {

    private final MsSearchResultDAO mainResultDao;
    private final MsSearchResultDAO resultDao;
    private final boolean useTempTable;
    
    /**
     * @param mainResultDao -- DAO for the MAIN database table
     * @param resultDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsSearchResultUploadDAOImpl(MsSearchResultDAO mainResultDao, MsSearchResultDAO resultDao, boolean useTempTable) {
        this.mainResultDao = mainResultDao;
        if(resultDao == null)
            this.resultDao = mainResultDao;
        else
            this.resultDao = resultDao;
        this.useTempTable = useTempTable;
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public MsSearchResult load(int id) {
        throw new UnsupportedOperationException();
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
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadResultIdsForRunSearch(int runSearchId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadResultIdsForRunSearch(int runSearchId, int limit, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadResultIdsForSearch(int searchId) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadResultIdsForSearch(int searchId, int limit, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int numRunSearchResults(int runSearchId) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int numSearchResults(int searchId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadResultIdsForSearchScanCharge(int runSearchId, int scanId, int charge) {
        throw new UnsupportedOperationException();
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
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadResultIdsForSearchScan(int runSearchId, int scanId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int save(int searchId, MsSearchResultIn searchResult, int runSearchId, int scanId) {
        throw new UnsupportedOperationException();
    }
    
    public int saveResultOnly(MsSearchResultIn searchResult, int runSearchId, int scanId) {

        String sql = "INSERT INTO msRunSearchResult ( ";
        sql +=       "runSearchID, scanID, charge, observedMass, peptide, preResidue, postResidue, validationStatus )";
        sql +=       " VALUES (";
        
        // runSearchID
        if(runSearchId == 0)
            sql +=   "NULL, ";
        else
            sql += runSearchId+", ";
        
        // scanID
        if(scanId == 0)
            sql +=   "NULL, ";
        else
            sql +=   scanId+", ";
        
        // charge
        if(searchResult.getCharge() == 0)
            sql +=   "NULL, ";
        else
            sql +=   searchResult.getCharge()+", ";
        
        // observedMass
        if(searchResult.getObservedMass() == null)
            sql +=   "NULL, ";
        else
            sql +=   searchResult.getObservedMass()+", ";
        
        // peptide
        String peptide = searchResult.getResultPeptide().getPeptideSequence();
        if(peptide == null)
            sql +=   "NULL, ";
        else
            sql +=   "\""+peptide+"\", ";
        
        // preResidue
        String preResidue = Character.toString(searchResult.getResultPeptide().getPreResidue());
        if(preResidue == null)
            sql +=   "NULL, ";
        else
            sql +=   "\""+preResidue+"\", ";
        
        // postResidue
        String postResidue = Character.toString(searchResult.getResultPeptide().getPostResidue());
        if(postResidue == null)
            sql +=   "NULL, ";
        else
            sql +=   "\""+postResidue+"\", ";
        
        // validationStatus
        ValidationStatus validationStatus = searchResult.getValidationStatus();
        if(validationStatus == null || validationStatus == ValidationStatus.UNKNOWN)
            sql +=   "NULL ";
        else
            sql +=   "\""+Character.toString(validationStatus.getStatusChar())+"\"";
        sql += ")";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getTempDbConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            
            rs = stmt.getGeneratedKeys();
            if(rs.next())
                return rs.getInt(1);
            else
                throw new RuntimeException("Failed to get auto_increment key for sql: "+sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new RuntimeException("Failed to execute sql: "+sql, e);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
            try {if(rs != null) rs.close();}
            catch(SQLException e){}
        }
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
            // copy entries from the required table
            copyToMainTableFromFile("msRunSearchResult", true); // disable keys before copying 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
