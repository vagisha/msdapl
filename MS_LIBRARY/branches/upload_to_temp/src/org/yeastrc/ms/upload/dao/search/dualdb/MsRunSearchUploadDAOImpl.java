/**
 * MsRunSearchDAOImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.dualdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.TableCopyUtil;
import org.yeastrc.ms.upload.dao.search.MsRunSearchUploadDAO;

/**
 * Deals with the tables:
 * 1. msRunSearch
 */
public class MsRunSearchUploadDAOImpl implements MsRunSearchUploadDAO, TableCopier {
    
    private static final Logger log = Logger.getLogger(MsRunSearchUploadDAOImpl.class.getName());
    
    private final MsRunSearchUploadDAO mainRunSearchDao;
    private final MsRunSearchUploadDAO runSearchDao;
    private final boolean useTempTable;
    
    /**
     * @param mainRunSearchDao -- DAO for the MAIN database table
     * @param runSearchDao -- DAO for the TEMP database table (if a temporary database is being used for the upload)
     * @param useTempTable -- if true, a temporary database is used for the msExperiment table.
     */
    public MsRunSearchUploadDAOImpl(MsRunSearchUploadDAO mainRunSearchDao, MsRunSearchUploadDAO runSearchDao,
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
                conn = ConnectionFactory.getTempMsDataConnection();
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
    
    public int saveRunSearch(MsRunSearch search) {
        return runSearchDao.saveRunSearch(search);
    }
    
    //------------------------------------------------------------------------------------------------
    // COPY DATA TO MAIN TABLES
    //------------------------------------------------------------------------------------------------
    @Override
    public void copyToMainTable() throws TableCopyException {
        
        if(useTempTable) {
            TableCopyUtil copier = TableCopyUtil.getInstance();
            // copy entries from the required table
            copier.copyToMainTableDirect("msRunSearch"); 
        }
        else {
            log.warn("Cannot copy to main tables; not using temp tables.");
        }
    }
    
    @Override
    public boolean checkBeforeCopy() throws TableCopyException {
        TableCopyUtil copier = TableCopyUtil.getInstance();
        if(!copier.checkColumnValues("msRunSearch", "id"))
            return false;
        return true;
    }
}
