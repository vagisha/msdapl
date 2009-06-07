/**
 * MsRunSearchDAOImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables:
 * 1. msRunSearch
 */
public class MsRunSearchUploadDAOImpl extends AbstractTableCopier implements MsRunSearchDAO {
    
    private final MsRunSearchDAO mainRunSearchDao;
    private final MsRunSearchDAO runSearchDao;
    private final boolean useTempTable;
    
    /**
     * @param mainRunSearchDao -- DAO for the MAIN database table
     * @param runSearchDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsRunSearchUploadDAOImpl(MsRunSearchDAO mainRunSearchDao, MsRunSearchDAO runSearchDao,
            boolean useTempTable) {
        this.mainRunSearchDao = mainRunSearchDao;
        if(runSearchDao == null)
            this.runSearchDao = mainRunSearchDao;
        else
            this.runSearchDao = runSearchDao;
        this.useTempTable = useTempTable;
    }
    
    @Override
    public MsRunSearch loadRunSearch(int runSearchId) {
        MsRunSearch rs = runSearchDao.loadRunSearch(runSearchId);
        if(rs == null && useTempTable) {
            rs = mainRunSearchDao.loadRunSearch(runSearchId);
        }
        return rs;
    }
    
    @Override
    /**
     * Returns the runSearchIds for the given search in the temporary tables.
     */
    public List<Integer> loadRunSearchIdsForSearch(int searchId) {
        return runSearchDao.loadRunSearchIdsForSearch(searchId);
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadRunSearchIdsForRun(int runId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int loadIdForRunAndSearch(int runId, int searchId) {
       throw new UnsupportedOperationException();
    }
    
    @Override
    public int loadIdForSearchAndFileName(int searchId, String filename) {
        
        // look in the temporary table first (this will work if both the run
        // AND search are in the temporary tables)
        int runSearchId = runSearchDao.loadIdForSearchAndFileName(searchId, filename);
        if(runSearchId == 0) {
            // look in the main table (this will work if both the run
            // AND search are in the main tables)
            runSearchId = mainRunSearchDao.loadIdForSearchAndFileName(searchId, filename);
        }
        
        if(runSearchId == 0) {
            // look for the run in the main table and the search in the temporary table
            String sql = "SELECT rs.id FROM msRunSearch AS rs, ";
            sql +=       ConnectionFactory.masterDbName()+".msRun AS run ";
            sql +=       "WHERE rs.runID = run.id ";
            sql +=       "AND rs.searchID="+searchId+" ";
            sql +=       "AND run.filename=\""+filename+"\"";
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = ConnectionFactory.getTempDbConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                
                if(rs.next())
                    runSearchId = rs.getInt(1);
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
        
        return runSearchId;
    }
    
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public String loadFilenameForRunSearch(int runSearchId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public Program loadSearchProgramForRunSearch(int runSearchId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public int numResults(int runSearchId) {
        throw new UnsupportedOperationException();
    }
    
    public int saveRunSearch(MsRunSearch search) {
        return runSearchDao.saveRunSearch(search);
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public void deleteRunSearch(int runSearchId) {
        throw new UnsupportedOperationException();
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            // copy entries from the required table
            copyToMainTableDirect("msRunSearch"); 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
