/**
 * TempSchemaManager.java
 * @author Vagisha Sharma
 * May 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.upload.dao.TableCopier;
import org.yeastrc.ms.upload.dao.TableCopyException;
import org.yeastrc.ms.upload.dao.UploadDAOFactory;
import org.yeastrc.ms.upload.dao.general.MsExperimentUploadDAO;


/**
 * 
 */
public class TempSchemaManager {

    
    private static final Logger log = Logger.getLogger(TempSchemaManager.class.getName());
    
    private static final TempSchemaManager instance = new TempSchemaManager();
    
    private TempSchemaManager() {}
    
    public static TempSchemaManager getInstance() {
        return instance;
    }
    
    /**
     * Create an empty copy of the main database.  If the temporary database already
     * exists it is dropped first.
     * @throws SQLException 
     * @throws TempSchemaManagerException 
     */
    public void createTempSchema() throws SQLException, TempSchemaManagerException {
        
        log.info("Creating temporary database: "+ConnectionFactory.tempDbName());
        DatabaseCopier instance = DatabaseCopier.getInstance();
        try {
            instance.copyDatabase(ConnectionFactory.masterDbName(), ConnectionFactory.tempDbName());
        }
        catch (DatabaseCopyException e) {
            throw new TempSchemaManagerException("Could not create temp schema: "+e.getMessage(), e);
        }
    }

    
    public void checkBeforeCopy() throws SQLException, TempSchemaManagerException, TableCopyException {
        // make sure the tables still match
        // in the main and temp databases
        DatabaseCopier instance = DatabaseCopier.getInstance();
        try {
            instance.matchTables(ConnectionFactory.masterDbName(), ConnectionFactory.tempDbName());
        }
        catch (DatabaseCopyException e) {
            throw new TempSchemaManagerException("tables don't match in the original and copied database: "+
                    e.getMessage(), e);
        }
        checkTables();
    }
    
    /**
     * Dump the contents of the temporary database into the main database;
     * @throws SQLException 
     * @throws TempSchemaManagerException 
     * @throws TableCopyException 
     */
    public void flushToMainDatabase() throws SQLException, TempSchemaManagerException, TableCopyException {
        
        // copy the tables from the temp to main database
        copyTables();
    }

    private void checkTables() throws TableCopyException {
        
        UploadDAOFactory daoFactory = UploadDAOFactory.getInstance();
        checkTable(daoFactory.getMsExperimentDAO());     // msExperiment, msExperimentRun
        
        checkTable(daoFactory.getEnzymeDAO());           // msSearchEnzyme, msRunEnzyme
        
        checkTable(daoFactory.getMsRunDAO());            // msRun, msRunLocation
        checkTable(daoFactory.getMS2FileRunDAO());       // MS2FileHeader
        checkTable(daoFactory.getMsScanDAO());           // msScan, msScanData
        checkTable(daoFactory.getMS2FileScanDAO());      // MS2FileScanCharge, MS2FileChargeDependentAnalysis, MS2FileChargeIndependentAnalysis
        
        checkTable(daoFactory.getMsSearchDAO());         // msSearch
        checkTable(daoFactory.getMsSequenceDatabaseDAO());// msSearchDatabase
        checkTable(daoFactory.getMsRunSearchDAO());      // msRunSearch
        checkTable(daoFactory.getMsSearchResultDAO());   // msRunSearchResult
        checkTable(daoFactory.getMsSearchModDAO());      // msSearchStaticMod, msSearchTerminalStaticMod
                                                        // msSearchDynamicMod, msSearchTerminalDynamicMod
                                                        // msDynamicModResult, msTerminalDynamicModResult
        checkTable(daoFactory.getMsProteinMatchDAO());   // msProteinMatch
        
        checkTable(daoFactory.getSqtHeaderDAO());        // SQTFileHeader
        checkTable(daoFactory.getSqtRunSearchDAO());     // 
        checkTable(daoFactory.getSqtSpectrumDAO());      // SQTSpectrumData
        
        checkTable(daoFactory.getSequestSearchDAO());    // SQTParams
        checkTable(daoFactory.getSequestResultDAO());    // SQTSearchResult
        
        checkTable(daoFactory.getProlucidSearchDAO());   // ProLuCIDParams
        checkTable(daoFactory.getProlucidResultDAO());   // ProLuCIDSearchResult
        
        checkTable(daoFactory.getMsSearchAnalysisDAO()); // msSearchAnalysis
        checkTable(daoFactory.getMsRunSearchAnalysisDAO());  // msRunSearchAnalysis
        checkTable(daoFactory.getPercoltorParamsDAO());  // PercolatorParams
        checkTable(daoFactory.getPercolatorResultDAO()); // PercolatorResult
    }
    
    private void copyTables() throws TableCopyException {
        
        long s = System.currentTimeMillis();
        UploadDAOFactory daoFactory = UploadDAOFactory.getInstance();
        copyTable(daoFactory.getMsExperimentDAO());     // msExperiment, msExperimentRun
        
        copyTable(daoFactory.getEnzymeDAO());           // msSearchEnzyme, msRunEnzyme
        
        copyTable(daoFactory.getMsRunDAO());            // msRun, msRunLocation
        copyTable(daoFactory.getMS2FileRunDAO());       // MS2FileHeader
        copyTable(daoFactory.getMsScanDAO());           // msScan, msScanData
        copyTable(daoFactory.getMS2FileScanDAO());      // MS2FileScanCharge, MS2FileChargeDependentAnalysis, MS2FileChargeIndependentAnalysis
        
        copyTable(daoFactory.getMsSearchDAO());         // msSearch
        copyTable(daoFactory.getMsSequenceDatabaseDAO());// msSearchDatabase
        copyTable(daoFactory.getMsRunSearchDAO());      // msRunSearch
        copyTable(daoFactory.getMsSearchResultDAO());   // msRunSearchResult
        copyTable(daoFactory.getMsSearchModDAO());      // msSearchStaticMod, msSearchTerminalStaticMod
                                                        // msSearchDynamicMod, msSearchTerminalDynamicMod
                                                        // msDynamicModResult, msTerminalDynamicModResult
        copyTable(daoFactory.getMsProteinMatchDAO());   // msProteinMatch
        
        copyTable(daoFactory.getSqtHeaderDAO());        // SQTFileHeader
        copyTable(daoFactory.getSqtRunSearchDAO());     // 
        copyTable(daoFactory.getSqtSpectrumDAO());      // SQTSpectrumData
        
        copyTable(daoFactory.getSequestSearchDAO());    // SQTParams
        copyTable(daoFactory.getSequestResultDAO());    // SQTSearchResult
        
        copyTable(daoFactory.getProlucidSearchDAO());   // ProLuCIDParams
        copyTable(daoFactory.getProlucidResultDAO());   // ProLuCIDSearchResult
        
        copyTable(daoFactory.getMsSearchAnalysisDAO()); // msSearchAnalysis
        copyTable(daoFactory.getMsRunSearchAnalysisDAO());  // msRunSearchAnalysis
        copyTable(daoFactory.getPercoltorParamsDAO());  // PercolatorParams
        copyTable(daoFactory.getPercolatorResultDAO()); // PercolatorResult
        
        long e = System.currentTimeMillis();
        log.info("COPIED TABLES IN : "+((e - s)/(1000L * 60L))+" minutes");
    }
    
    public void rollBackMainTable() {
        
        log.warn("Rolling back all tables copied....");
        UploadDAOFactory daoFactory = UploadDAOFactory.getInstance();
        
        // Delete the experiments
        MsExperimentUploadDAO exptDao = daoFactory.getMsExperimentDAO();
        List<Integer> experimentIds = exptDao.getAllExperimentIds(); // experiment ids from the temp table
        for(int exptId: experimentIds)
            // This will delete the experiment from the main and temp tables
            exptDao.deleteExperiment(exptId);
        
        
        // Delete any searches that are left over after deleting the experiments
        List<Integer> searchIds = getAllSearchIds(); // search ids from the temp table
        for(int searchId: searchIds)
            deleteEntry("msSearch", searchId);
        
        // Delete any search analyses that are left over after deleting the experiments and searches
        List<Integer> analysisIds = getAllAnalysisIds(); // analysis ids from the temp table
        for(int analysisId: analysisIds)
            deleteEntry("msSearchAnalysis", analysisId);
        
        // If any runs are left delete them too. Deleting experiments does not delete 
        // the associated runs
        List<Integer> runIds = getAllRunIds();
        for(int runId: runIds)
            deleteEntry("msRun", runId);
        
    }
    
    private List<Integer> getAllRunIds() {
        String sql = "SELECT id FROM msRun";
        return getAllIds(sql);
    }

    private List<Integer> getAllAnalysisIds() {
        String sql = "SELECT id FROM msSearchAnalysis";
        return getAllIds(sql);
    }

    private List<Integer> getAllSearchIds() {
        String sql = "SELECT id FROM msSearch";
        return getAllIds(sql);
    }

    private List<Integer> getAllIds(String sql) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Integer> ids = new ArrayList<Integer>();
        try {
            conn = ConnectionFactory.getTempMsDataConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while(rs.next())
                ids.add(rs.getInt(1));
        }
        catch(SQLException e) {
            log.error("Error executing sql: "+sql);
        }
        finally {
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
            
            if(rs != null) try { rs.close(); rs = null;}
            catch (SQLException e) {}
        }
        return ids;
    }
    
    private void deleteEntry(String tableName, int id) {
        
        Connection conn = null;
        try {
            conn = ConnectionFactory.getTempMsDataConnection();
        }
        catch (SQLException e) {
            log.error("", e);
        }
        if(conn != null) deleteEntry(conn, tableName, id);
        
        try {
            conn = ConnectionFactory.getMainMsDataConnection();
        }
        catch (SQLException e) {
            log.error("", e);
        }
        if(conn != null) deleteEntry(conn, tableName, id);
    }
    
    private void deleteEntry(Connection conn, String tableName, int id) {
        
        String sql = "DELETE FROM "+tableName+" WHERE id="+id;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch(SQLException e) {
            log.error("Error executing sql: "+sql, e);
        }
        finally {
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
    }
    
    private void copyTable(Object tableDao) throws TableCopyException {
        if(tableDao instanceof TableCopier) {
            ((TableCopier)tableDao).copyToMainTable();
        }
    }
    
    private void checkTable(Object tableDao) throws TableCopyException {
        if(tableDao instanceof TableCopier) {
            if(!((TableCopier)tableDao).checkBeforeCopy()) {
                throw new TableCopyException("Check before copying tables for"+tableDao.getClass().getName()+" failed");
            }
        }
    }
    
}
