/**
 * AbstractTableCopier.java
 * @author Vagisha Sharma
 * Jun 3, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.service.MsDataUploadProperties;

/**
 * 
 */
public class TableCopyUtil {

    protected static final Logger log = Logger.getLogger(TableCopyUtil.class.getName());

    private static TableCopyUtil instance;
    
    private TableCopyUtil() {}
    
    public static TableCopyUtil getInstance() {
        if(instance == null)
            instance = new TableCopyUtil();
        return instance;
    }
    
    public void copyToMainTableDirect(String tableName) throws TableCopyException {
        copyToMainTableDirect(tableName, false);
    }
    
    public void copyToMainTableDirect(String tableName, boolean disableKeys) throws TableCopyException {

        log.info("Copying table (direct) "+tableName);
        
        // disable keys on the main database table, if required
        if(disableKeys)
            disableKeys(tableName);
        
        String sql = "INSERT INTO "+ConnectionFactory.masterDbName()+"."+tableName+" SELECT * from "+tableName;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getTempMsDataConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
            
            // enable keys on the main database table, if required
            if(disableKeys)
                enableKeys(tableName);
        }
    }
    
    public void copyToMainTableFromFile(String tableName) throws TableCopyException {
        copyToMainTableFromFile(tableName, false);
    }
    
    public void copyToMainTableFromFile(String tableName, boolean disableKeys) throws TableCopyException {
        
        log.info("Copying table (from file) "+tableName);
        
        // Get the number of rows in the temp database table
        int rowCountToCopy = getRowCount(tableName);
        if(rowCountToCopy == 0) {
            log.info("No data to copy in table: "+tableName);
            return;
        }
        
        // Path to the temp directory where we will be writing out the table contents
        String tempDir = MsDataUploadProperties.getMysqlTempDirectory();
        if(tempDir == null) {
            throw new TableCopyException("No directory found for writing out table data");
        }
        
        File directory = new File(tempDir);
        if(!directory.exists()) {
            throw new TableCopyException("mysql temp directory does not exist: "+tempDir);
        }
        if(!directory.canWrite()) {
            throw new TableCopyException("Cannot write to mysql temp directory: "+tempDir);
        }
        
        // Path to the file into which we will be writing out the table contents
        String dumpFile = tempDir + File.separator + tableName+".sql";
        
        File file = new File(dumpFile);
        if(file.exists()) {
            if(!file.delete()) {
                throw new TableCopyException("Could not delete existing file: "+dumpFile);
            }
        }
        if(file.exists()) {
            throw new TableCopyException("File still existing file: "+dumpFile);
        }
        
        // write out the contents of the table to a tab delimited file
        writeOutTableContents(tableName, dumpFile);
        
        
        // If the file was written successfully, load it into the main database table
        if(!file.exists()) {
            throw new TableCopyException("File was not written: "+dumpFile);
        }
        
        // disable keys on the main database table, if required
        if(disableKeys)
            disableKeys(tableName);
        try {
            loadFileContents(tableName, dumpFile);
        }
        finally {
            // enable keys on the main database table, if required
            if(disableKeys)
                enableKeys(tableName);
        }
    }

    private int getRowCount(String tableName) throws TableCopyException {
        String sql = "SELECT COUNT(*) FROM "+tableName;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getTempMsDataConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next())
                return rs.getInt(1);
            return 0;
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            if(rs != null) try { rs.close(); rs = null;}
            catch (SQLException e) {}
            
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
    }

    private void loadFileContents(String tableName, String dumpFile)
            throws TableCopyException {
        String sql = "LOAD DATA INFILE \""+dumpFile+"\" INTO TABLE "+tableName;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getMainMsDataConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
    }

    private void writeOutTableContents(String tableName, String dumpFile)
            throws TableCopyException {
        
        String sql = "SELECT * FROM "+tableName+" INTO OUTFILE \""+dumpFile+"\"";
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getTempMsDataConnection();
            stmt = conn.createStatement();
            stmt.executeQuery(sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
    }
    
    // Disable keys on the main database table
    private void disableKeys(String tableName) throws TableCopyException {
        
        log.info("DISABLING KEYS ON "+tableName);
        String sql = "ALTER TABLE "+ConnectionFactory.masterDbName()+"."+tableName+" DISABLE KEYS";
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getTempMsDataConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
    }
    
    // Enable keys on the main database table
    private void enableKeys(String tableName) throws TableCopyException {
        
        log.info("ENABLING KEYS ON "+tableName+"....");
        String sql = "ALTER TABLE "+ConnectionFactory.masterDbName()+"."+tableName+" ENABLE KEYS";
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getTempMsDataConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
        log.info("ENABLED KEYS ON "+tableName);
    }
    
    public boolean checkColumnValues(String tableName, String columnName) throws TableCopyException {
        log.info("Checking primary key: "+columnName+" on table: "+tableName);
        
        int numRowsInTemp = getRecordCount(tableName, true); // look in temp table
        if(numRowsInTemp == 0) // if the temp table is empty we will not be copying anything. 
            return true;
        
        numRowsInTemp = getRecordCount(tableName, false); // look in main table
        if(numRowsInTemp == 0) // if the main table is empty there will not be any conflicts. 
            return true;
        
        int maxValInMain = selectVal(tableName, columnName, true,true); // main table; max value
        int minValInTemp = selectVal(tableName, columnName, false,false); // temp table; min value
        
        return maxValInMain < minValInTemp;
    }
    
    
    private int selectVal(String tableName, String columnName, boolean mainTable, boolean maxVal) throws TableCopyException {
        
        String sql = "SELECT ";
        if(maxVal)
            sql += "max(";
        else
            sql += "min(";
        sql +=columnName+") FROM "+tableName;
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = mainTable ? ConnectionFactory.getMainMsDataConnection() : ConnectionFactory.getTempMsDataConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next())
                return rs.getInt(1);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            if(rs != null) try { rs.close(); rs = null;}
            catch (SQLException e) {}
            
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
        return 0;
    }
    

    private int getRecordCount(String tableName, boolean tempTable) throws TableCopyException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT count(*) FROM "+tableName;
        try {
            conn = tempTable ? ConnectionFactory.getTempMsDataConnection() : ConnectionFactory.getMainMsDataConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next()) {
                return rs.getInt(1);
            }
            else {
                throw new TableCopyException("No results for query: "+sql);
            }
        }
        catch(SQLException e) {
            throw new TableCopyException("Error executing query: "+sql, e);
        }
        finally {
            if(rs != null) try { rs.close(); rs = null;}
            catch (SQLException e) {}
            
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
    }
}
