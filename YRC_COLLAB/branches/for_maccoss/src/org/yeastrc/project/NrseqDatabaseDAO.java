/**
 * NrseqDatabaseDAO.java
 * @author Vagisha Sharma
 * Jan 25, 2010
 * @version 1.0
 */
package org.yeastrc.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.grant.GrantDAO;
import org.yeastrc.grant.ProjectGrantDAO;
import org.yeastrc.group.Group;
import org.yeastrc.group.GroupDAO;

/**
 * 
 */
public class NrseqDatabaseDAO {

    private static final NrseqDatabaseDAO instance = new NrseqDatabaseDAO();
    
    public static NrseqDatabaseDAO instance() {
        return instance;
    }
    
    public List<NrseqDatabase> getDatabases(String description) throws SQLException {
        
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

            List<NrseqDatabase> databases = new ArrayList<NrseqDatabase>();
            while(rs.next()) {
                NrseqDatabase db = new NrseqDatabase();
                db.setId(rs.getInt("id"));
                db.setFastaFileName(rs.getString("name"));
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
   
}
