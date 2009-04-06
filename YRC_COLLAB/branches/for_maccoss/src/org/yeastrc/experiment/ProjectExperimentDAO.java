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
import org.yeastrc.project.Project;

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
    
    public List<Integer> getExperimentIdsForProjects(List<Project> projects) throws SQLException {
        if(projects == null || projects.size() == 0) 
            return new ArrayList<Integer>(0);
        
        String projIdStr = "";
        for(Project proj: projects) {
            projIdStr += ","+proj.getID();
        }
        projIdStr = projIdStr.substring(1); // remove first comma
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            
            String sql = "SELECT experimentID FROM tblProjectExperiment WHERE projectID in ("+projIdStr+")";
                    
            conn = DBConnectionManager.getConnection( "yrc" );
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            List<Integer> experimentIds = new ArrayList<Integer>();
            while (rs.next()) {
                experimentIds.add( rs.getInt("experimentID"));
            }
            return experimentIds;
            
        } finally {
            
            if (rs != null) {
                try { rs.close(); rs = null; } catch (Exception e) { ; }
            }

            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
    }
    
}