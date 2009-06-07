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
public abstract class AbstractTableCopier implements TableCopier {

    protected static final Logger log = Logger.getLogger(AbstractTableCopier.class.getName());

    
    protected void copyToMainTableDirect(String tableName) throws TableCopyException {
        copyToMainTableDirect(tableName, false);
    }
    
    protected void copyToMainTableDirect(String tableName, boolean disableKeys) throws TableCopyException {

        log.info("Copying table (direct) "+tableName);
        
        // disable keys on the main database table, if required
        if(disableKeys)
            disableKeys(tableName);
        
        String sql = "INSERT INTO "+ConnectionFactory.masterDbName()+"."+tableName+" SELECT * from "+tableName;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getTempDbConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
            
            // enable keys on the main database table, if required
            if(disableKeys)
                enableKeys(tableName);
        }
    }
    
    protected void copyToMainTableFromFile(String tableName) throws TableCopyException {
        copyToMainTableFromFile(tableName, false);
    }
    
    protected void copyToMainTableFromFile(String tableName, boolean disableKeys) throws TableCopyException {
        
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
            conn = ConnectionFactory.getTempDbConnection();
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
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
            try {if(rs != null) rs.close();}
            catch(SQLException e){}
        }
    }

    private void loadFileContents(String tableName, String dumpFile)
            throws TableCopyException {
        String sql = "LOAD DATA INFILE \""+dumpFile+"\" INTO TABLE "+tableName;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }

    private void writeOutTableContents(String tableName, String dumpFile)
            throws TableCopyException {
        
        String sql = "SELECT * FROM "+tableName+" INTO OUTFILE \""+dumpFile+"\"";
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getTempDbConnection();
            stmt = conn.createStatement();
            stmt.executeQuery(sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }
    
    // Disable keys on the main database table
    private void disableKeys(String tableName) throws TableCopyException {
        
        log.info("DISABLING KEYS ON "+tableName);
        String sql = "ALTER TABLE "+ConnectionFactory.masterDbName()+"."+tableName+" DISABLE KEYS";
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getTempDbConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }
    
    // Enable keys on the main database table
    private void enableKeys(String tableName) throws TableCopyException {
        
        log.info("ENABLING KEYS ON "+tableName+"....");
        String sql = "ALTER TABLE "+ConnectionFactory.masterDbName()+"."+tableName+" ENABLE KEYS";
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getTempDbConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new TableCopyException("Failed to execute sql: "+sql, e);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
        log.info("ENABLED KEYS ON "+tableName);
    }
}
