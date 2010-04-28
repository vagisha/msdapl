/**
 * NrseqDatabaseDAO.java
 * @author Vagisha Sharma
 * Jan 25, 2010
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.ms.domain.nrseq.NrDatabase;

/**
 * 
 */
public class NrseqDatabaseDAO {

    private static NrseqDatabaseDAO instance;
    
    private NrseqDatabaseDAO() {}
    
    public static NrseqDatabaseDAO getInstance() {
    	if(instance == null)
    		instance = new NrseqDatabaseDAO();
        return instance;
    }
    
    public List<NrDatabase> getDatabases(String description) throws SQLException {
        
        // Get our connection to the database.
        Connection conn = DBConnectionManager.getConnection(DBConnectionManager.NRSEQ);
        Statement stmt = null;
        ResultSet rs = null;    

        try {
            stmt = conn.createStatement();

            // query string
            String sqlStr = "SELECT * FROM tblDatabase ";
            if(description != null && description.trim().length() > 0)
                sqlStr += "WHERE description = \"" + description+"\"";
            sqlStr += " ORDER BY id DESC";
            
            rs = stmt.executeQuery(sqlStr);

            List<NrDatabase> databases = new ArrayList<NrDatabase>();
            while(rs.next()) {
                NrDatabase db = new NrDatabase();
                db.setId(rs.getInt("id"));
                db.setName(rs.getString("name"));
                db.setDescription(rs.getString("description"));
                databases.add(db);
            }
            return databases;
        }
        finally {

            // Always make sure result sets and statements are closed,
            // and the connection is returned to the pool
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { ; }
                rs = null;
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { ; }
                stmt = null;
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { ; }
                conn = null;
            }
        }
    }
    
    public NrDatabase getDatabase(int id) throws SQLException {
        
        // Get our connection to the database.
        Connection conn = DBConnectionManager.getConnection(DBConnectionManager.NRSEQ);
        Statement stmt = null;
        ResultSet rs = null;    

        try {
            stmt = conn.createStatement();

            // query string
            String sqlStr = "SELECT * FROM tblDatabase ";
            sqlStr += "WHERE id = "+id;
            
            rs = stmt.executeQuery(sqlStr);

            if(rs.next()) {
                NrDatabase db = new NrDatabase();
                db.setId(rs.getInt("id"));
                db.setName(rs.getString("name"));
                db.setDescription(rs.getString("description"));
                return db;
            }
            return null;
        }
        finally {

            // Always make sure result sets and statements are closed,
            // and the connection is returned to the pool
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { ; }
                rs = null;
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { ; }
                stmt = null;
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { ; }
                conn = null;
            }
        }
    }
    
    public NrDatabase getDatabase(String name) throws SQLException {
        
        // Get our connection to the database.
        Connection conn = DBConnectionManager.getConnection(DBConnectionManager.NRSEQ);
        Statement stmt = null;
        ResultSet rs = null;    

        try {
            stmt = conn.createStatement();

            // query string
            String sqlStr = "SELECT * FROM tblDatabase ";
            sqlStr += "WHERE name = \""+name+"\"";
            
            rs = stmt.executeQuery(sqlStr);

            if(rs.next()) {
                NrDatabase db = new NrDatabase();
                db.setId(rs.getInt("id"));
                db.setName(rs.getString("name"));
                db.setDescription(rs.getString("description"));
                return db;
            }
            return null;
        }
        finally {

            // Always make sure result sets and statements are closed,
            // and the connection is returned to the pool
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { ; }
                rs = null;
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { ; }
                stmt = null;
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { ; }
                conn = null;
            }
        }
    }
   
}
