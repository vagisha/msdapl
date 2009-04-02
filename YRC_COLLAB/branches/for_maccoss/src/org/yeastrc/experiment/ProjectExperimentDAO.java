/**
 * ProjectExperimentDAO.java
 * @author Vagisha Sharma
 * Apr 2, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.db.DBConnectionManager;

/**
 * 
 */
public class ProjectExperimentDAO {

    private static ProjectExperimentDAO instance = new ProjectExperimentDAO();
    
    private ProjectExperimentDAO() {}
    
    public static ProjectExperimentDAO instance() {
        return instance;
    }
    
    public List<Integer> getProjectExperimentIds(int projectId) throws SQLException {
        
        Connection conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
        Statement stmt = null;
        ResultSet rs = null;

        List<Integer> experimentIds = new ArrayList<Integer>();
        try {

            // Check if this project and experiment are already linked
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT experimentID FROM tblProjectExperiment WHERE projectID="+projectId);
            
            while(rs.next()) {
                experimentIds.add(rs.getInt("experimentID"));
            }
        }
        finally {

            // Always make sure statements are closed,
            // and the connection is returned to the pool
            if(rs != null) {
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
        return experimentIds;
    }
    
}
