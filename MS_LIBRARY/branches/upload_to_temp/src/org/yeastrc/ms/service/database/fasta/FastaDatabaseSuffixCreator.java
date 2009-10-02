/**
 * FastaDatabaseSuffixCreator.java
 * @author Vagisha Sharma
 * Oct 1, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.database.fasta;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.nrseq.NrDbProteinFull;
import org.yeastrc.ms.service.database.DatabaseCopyException;
import org.yeastrc.ms.util.TimeUtils;

/**
 * 
 */
public class FastaDatabaseSuffixCreator {

    private String nrseqDbName;
    private DataSource nrseqDs;
    
    private String tableName;
    
    private List<Suffix> suffixCache;
    private static final int BUF_SIZE = 500;
    
    private static final Logger log = Logger.getLogger(FastaDatabaseSuffixCreator.class.getName());
    
    public void createSuffixTable(int databaseId) throws SQLException {
        
        // set up our datasource
        nrseqDbName = ConnectionFactory.nrseqDbName();
        nrseqDs = ConnectionFactory.getDataSource(nrseqDbName);
        
        suffixCache = new ArrayList<Suffix>();
        
        tableName = getSuffixTableName(databaseId);
        
        // first check if a table for this database already exists
        if(checkTableExists(tableName)) {
            log.info("Table "+tableName+" already exists");
            return;
        }
        
        // create the table
        createTable(tableName);
        
        // add an index on the table
        addTableIndex(tableName);
        
        // save the suffixes in the table
        saveSuffixes(tableName, databaseId);
        
    }
    
    public static String getSuffixTableName(int databaseId) {
        return "suffix_db_"+databaseId;
    }
   
    public void saveSuffixes(String tableName, int databaseId) throws SQLException {
        
        // get all the ids from tblProteinDatabase for the given databaseID
        List<Integer> dbProteinIds = NrSeqLookupUtil.getDbProteinIdsForDatabase(databaseId);
        System.out.println("# proteins: "+dbProteinIds.size()+" for database: "+databaseId);
        
        // some proteins in a fasta file have the same sequence.  We will not create suffixes twice
        Set<Integer> seenSequenceIds = new HashSet<Integer>(dbProteinIds.size());
        
        long s = System.currentTimeMillis();
        
        for(int dbProteinId: dbProteinIds) {
            NrDbProteinFull protein = NrSeqLookupUtil.getDbProteinFull(dbProteinId);
            
            if(seenSequenceIds.contains(protein.getSequenceId()))
                continue;
            else
                seenSequenceIds.add(protein.getSequenceId());
            
            String sequence = NrSeqLookupUtil.getProteinSequenceForNrSeqDbProtId(dbProteinId);
            
            createSuffixes(sequence, protein.getSequenceId(), dbProteinId);
            
            if(this.suffixCache.size() >= BUF_SIZE)
                flushCache();
        }
        
        if(this.suffixCache.size() >= BUF_SIZE)
            flushCache();
        
        long e = System.currentTimeMillis();
        log.info("Total time to create table: "+TimeUtils.timeElapsedSeconds(s, e));
    }

    
    private void createSuffixes(String sequence, int sequenceId, int dbProteinId) throws SQLException {
        
        for(int i = 0; i < sequence.length(); i++) {
            int end = Math.min(i+255, sequence.length());
            String subseq = sequence.substring(i, end);
            Suffix suffix = new Suffix();
            suffix.dbProteinId = dbProteinId;
            suffix.sequenceId = sequenceId;
            suffix.suffix = subseq;
            this.suffixCache.add(suffix);
        }
    }
    
    private void flushCache() throws SQLException {
        
//        log.info("Flushing...");
        
        Connection conn = null;
        Statement stmt = null;
        StringBuilder sql = new StringBuilder("INSERT INTO "+tableName+" VALUES ");
        for(Suffix suffix: this.suffixCache) {
            sql.append("(");
            sql.append(suffix.sequenceId+", ");
            sql.append(suffix.dbProteinId+", ");
            sql.append("'"+suffix.suffix+"'");
            sql.append("),");
        }
        sql.deleteCharAt(sql.length() - 1);
        
        try {
            conn = this.nrseqDs.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql.toString());
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
        }
        this.suffixCache.clear();
    }

    
    private boolean checkTableExists(String tableName) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.nrseqDs.getConnection();
            String sql = "SHOW TABLES LIKE '"+tableName+"'";
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next())
                return true;
            else
                return false;
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
            if(rs != null)    try {rs.close();} catch(SQLException e){}
        }
    }
    
    private void createTable(String tableName) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.nrseqDs.getConnection();
            String sql = "CREATE TABLE "+tableName+" (sequenceID INT UNSIGNED NOT NULL, dbProteinID INT UNSIGNED NOT NULL, suffix VARCHAR(255) NOT NULL)";
            
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
        }
    }
    
    private void addTableIndex(String tableName) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.nrseqDs.getConnection();
            String sql = "ALTER TABLE "+tableName+" ADD INDEX (suffix(10))";
            
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
        }
    }
    
    
    public static void main(String[] args) throws DatabaseCopyException, SQLException, IOException {
     
        FastaDatabaseSuffixCreator creator = new FastaDatabaseSuffixCreator();
        creator.createSuffixTable(123);
    }
 
 
    private class Suffix {
        
        int dbProteinId;
        int sequenceId;
        String suffix;
        public int getDbProteinId() {
            return dbProteinId;
        }
        public void setDbProteinId(int dbProteinId) {
            this.dbProteinId = dbProteinId;
        }
        public int getSequenceId() {
            return sequenceId;
        }
        public void setSequenceId(int sequenceId) {
            this.sequenceId = sequenceId;
        }
        public String getSuffix() {
            return suffix;
        }
        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }
}
