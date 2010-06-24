/* PublicationUtils.java
 * Created on Jun 9, 2004
 */
package org.yeastrc.progress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Jun 7, 2004
 *
 */
public class PublicationUtils {

	public static List getAllPublications() throws SQLException, InvalidIDException {
		List retList = new ArrayList();
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			// Our SQL statement
			String sqlStr = "SELECT id FROM tblPublications WHERE reportYear = 2010 ORDER BY reportYear, id";
			stmt = conn.prepareStatement(sqlStr);
			
			// Our results
			rs = stmt.executeQuery();

			// Iterate over list and populate our return list
			while (rs.next()) {
				int id = rs.getInt("id");
				Publication pub = new Publication();
				pub.load(id);
				
				retList.add(pub);
			}
			
			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
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

		return retList;
	}

	public static List getPublicationsByProject(int projectID) throws SQLException, InvalidIDException {
		List retList = new ArrayList();
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			// Our SQL statement
			String sqlStr = "SELECT id FROM tblPublications WHERE reportYear = 2010 AND projectID = ? ORDER BY reportYear, id";
			stmt = conn.prepareStatement(sqlStr);
			stmt.setInt(1, projectID);
			
			// Our results
			rs = stmt.executeQuery();

			// Iterate over list and populate our return list
			while (rs.next()) {
				int id = rs.getInt("id");
				Publication pub = new Publication();
				pub.load(id);
				
				retList.add(pub);
			}
			
			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
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

		return retList;
	}

}