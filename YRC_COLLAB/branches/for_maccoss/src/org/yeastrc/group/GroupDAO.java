/**
 * GroupDAO.java
 * @author Vagisha Sharma
 * Mar 22, 2009
 * @version 1.0
 */
package org.yeastrc.group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;

/**
 * 
 */
public class GroupDAO {

    private static final GroupDAO instance = new GroupDAO();
    
    public static GroupDAO instance() {
        return instance;
    }
    
    public Group load(int groupId) throws SQLException, InvalidIDException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {

            String sql = "SELECT * FROM tblYRCGroups WHERE groupID="+groupId;

            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Group group = new Group();
                group.setId(rs.getInt("groupID"));
                group.setName(rs.getString("groupName"));
                group.setDescription(rs.getString("groupDesc"));
                return group;
            }
            else {
                throw new InvalidIDException("Load failed due to invalid Group ID.");
            }

        } finally {

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
    
    
    public List<Group> loadProjectGroups(int projectId) throws SQLException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            String sql = "SELECT g.*, pg.* "+
            "FROM tblYRCGroups AS g, projectGroup AS pg "+
            "WHERE pg.projectID="+projectId+" "+
            "AND pg.groupID=g.groupID "+
            "ORDER BY g.groupID";

            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            List<Group> groups = new ArrayList<Group>();
            while (rs.next()) {
                Group group = new Group();
                group.setId(rs.getInt("groupID"));
                group.setName(rs.getString("groupName"));
                group.setDescription(rs.getString("groupDesc"));
                groups.add(group);
            }
            return groups;

        } finally {

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
    
    public void saveProjectGroups(int projectId, List<Integer>groupIds) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            // Our SQL statement
            String sqlStr = "SELECT * FROM projectGroup WHERE projectID = " + projectId;

            // Our results
            rs = stmt.executeQuery(sqlStr);

            while(rs.next()) {
                int groupId = rs.getInt("groupID");
                int idx = groupIds.indexOf(groupId);
                // if this project is no longer part of this group remove this entry.
                if(idx == -1) {
                    rs.deleteRow();
                }
                // project is part of the group, remove it from the list so that we know
                // we have seen this entry.
                else {
                    groupIds.remove(idx);
                }
            }

            // If there are new groups this project is a part of save them
            if(groupIds.size() > 0) {
                for(Integer grpId: groupIds) {
                    rs.moveToInsertRow();
                    rs.updateInt("projectID", projectId);
                    rs.updateInt("groupID", grpId);
                    rs.insertRow();
                }
            }

        } finally {

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
