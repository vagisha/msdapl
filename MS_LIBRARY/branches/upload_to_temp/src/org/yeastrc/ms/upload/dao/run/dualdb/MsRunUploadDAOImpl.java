/**
 * MsRunUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jun 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.run.dualdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.domain.run.MsRunIn;
import org.yeastrc.ms.upload.dao.BaseJDBCUploadDAO;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.run.MsRunUploadDAO;

/**
 * Deals with the tables: 
 * 1. msRun
 * 2. msRunLocation
 */
public class MsRunUploadDAOImpl extends BaseJDBCUploadDAO implements MsRunUploadDAO, TableCopier {

    private static final Logger log = Logger.getLogger(MsRunUploadDAOImpl.class.getName());
    
    private final MsRunUploadDAO runDao;
    private final MsRunUploadDAO mainRunDao; 
    private final boolean useTempTable;
    
    /**
     * @param mainRunDao -- DAO for the MAIN database table
     * @param runDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsRunUploadDAOImpl(MsRunUploadDAO mainRunDao, MsRunUploadDAO runDao, boolean useTempTable) {
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
    
    public int loadRunIdForFileNameAndSha1Sum(String fileName, String sha1Sum) {
        // look in the main database table first
        int runId = mainRunDao.loadRunIdForFileNameAndSha1Sum(fileName, sha1Sum);
        if(runId == 0 && useTempTable) {
            runId = runDao.loadRunIdForFileNameAndSha1Sum(fileName, sha1Sum);
        }
        return runId;
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
                close(conn, stmt, rs);
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
                close(conn, stmt, rs);
            }
        }
        return runId;
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

    /**
     * Deletes run from the temp table, if temp table is bing used.
     */
    public void delete(int runId) {
        // delete the run
        runDao.delete(runId);
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    public void copyToMainTable() throws TableCopyException {
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required tables
            copier.copyToMainTableDirect("msRun");
            copier.copyToMainTableDirect("msRunLocation");
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("msRun", "id"))
            return false;
        if(!copier.checkColumnValues("msExperimentRun", "experimentID"))
            return false;
        return true;
    }
}
