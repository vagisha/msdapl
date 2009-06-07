/**
 * MsRunUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsRunIn;
import org.yeastrc.ms.domain.run.MsRunLocation;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.upload.dao.AbstractTableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;

/**
 * Deals with the tables: 
 * 1. msRun
 * 2. msRunLocation
 */
public class MsRunUploadDAOImpl extends AbstractTableCopier implements MsRunDAO {

    
    private final MsRunDAO runDao;
    private final MsRunDAO mainRunDao; 
    private final boolean useTempTable;
    
    /**
     * @param mainRunDao -- DAO for the MAIN database table
     * @param runDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsRunUploadDAOImpl(MsRunDAO mainRunDao, MsRunDAO runDao, boolean useTempTable) {
        this.mainRunDao = mainRunDao;
        if(runDao == null)
            this.runDao = mainRunDao;
        else
            this.runDao = runDao;
        this.useTempTable = useTempTable;
    }

    public int saveRun(MsRunIn run, String serverDirectory) {
        return runDao.saveRun(run, serverDirectory);
    }

    @Override
    public void saveRunLocation(final String serverDirectory,
            final int runId) {
        runDao.saveRunLocation(serverDirectory, runId);
    }
    
    /**
     * Method not supported -- not used for upload
     */
    public MsRun loadRun(int runId) {
        throw new UnsupportedOperationException();
    }
    
    public int loadRunIdForFileNameAndSha1Sum(String fileName, String sha1Sum) {
        // look in the main database table first
        int runId = mainRunDao.loadRunIdForFileNameAndSha1Sum(fileName, sha1Sum);
        if(runId == 0 && useTempTable) {
            runId = runDao.loadRunIdForFileNameAndSha1Sum(fileName, sha1Sum);
        }
        return runId;
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<Integer> loadRunIdsForFileName(String fileName) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public String loadFilenameForRun(int runId) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int loadRunIdForSearchAndFileName(int searchId, String runFileName) {
        // look in the temp database tables first
        int runId = runDao.loadRunIdForSearchAndFileName(searchId, runFileName);
        if(runId == 0 && useTempTable) {
            // If we are here it means that the search was uploaded to the 
            // temporary tables but the run is in the main database. 
            // We need to query across the two databases;
            
            String sql = "SELECT r.id FROM ";
            sql +=       ConnectionFactory.masterDbName()+".msRun as r, ";
            sql +=       "msRunSearch as s ";
            sql +=       "WHERE s.searchID="+searchId;
            sql +=       " AND r.filename=\""+runFileName+"\" ";
            sql +=       "AND s.runID = r.id "; 
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = ConnectionFactory.getTempDbConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                if(rs.next())
                    runId = rs.getInt(1);
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
        return runId;
    }
    
    @Override
    public Integer loadRunIdForExperimentAndFileName(int experimentId,
            String runFileName) {
        
        int runId = runDao.loadRunIdForExperimentAndFileName(experimentId, runFileName);
        if(runId == 0 &&  useTempTable) {
            // If we are here it means that the experiment was uploaded to the 
            // temporary tables but the run is in the main database. 
            // We need to query across the two databases;
            String sql = "SELECT r.id FROM ";
            sql +=       ConnectionFactory.masterDbName()+".msRun as r, ";
            sql +=       "msExperimentRun as er ";
            sql +=       "WHERE er.experimentID="+experimentId;
            sql +=       " AND r.filename=\""+runFileName+"\" ";
            sql +=       " AND er.runID = r.id";
            
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = ConnectionFactory.getTempDbConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                if(rs.next())
                    runId = rs.getInt(1);
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
        return runId;
    }
    
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<MsRunLocation> loadLocationsForRun(int runId) {
       throw new UnsupportedOperationException();
    }

    @Override
    public int loadMatchingRunLocations(final int runId, final String serverDirectory) {
        // look in the main database table first
        int matchcount = mainRunDao.loadMatchingRunLocations(runId, serverDirectory);
        if(matchcount == 0 && useTempTable) {
            // look in the temp database table
            matchcount += runDao.loadMatchingRunLocations(runId, serverDirectory);
        }
        return matchcount;
    }

    @Override
    /**
     * Method not supported -- not used for upload
     */
    public List<MsRun> loadRuns(List<Integer> runIdList) {
       throw new UnsupportedOperationException();
    }
    
    /**
     * Delete only the top level run; everything else is deleted via SQL triggers.
     */
    public void delete(int runId) {
        runDao.delete(runId);
    }
   
    @Override
    /**
     * Method not supported -- not used for upload
     */
    public RunFileFormat getRunFileFormat(int runId) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            // copy entries from the required tables
            copyToMainTableDirect("msRun");
            copyToMainTableDirect("msRunLocation");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
}
