/* DisseminationUtils.java
 * Created on Jun 22, 2004
 */
package org.yeastrc.project;

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
 * @version 1.0, Jun 22, 2004
 *
 */
public class DisseminationUtils {

	/**
	 * Gets a list of all unshipped dissemination projects
	 * @return A list of all unshipped dissemination projects
	 * @throws SQLException If there is a database error
	 * @throws InvalidIDException If a dissemination project with an invalid ID is found
	 */
	public static List getUnshippedProjects() throws SQLException, InvalidIDException {
		ArrayList retList = new ArrayList();

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			// Our SQL statement
			String sqlStr =  "SELECT p.projectID FROM tblProjects AS p INNER JOIN tblDissemination AS d ON p.projectID = d.projectID WHERE d.shipShipped = 'F' ORDER BY p.projectSubmitDate DESC";
			stmt = conn.prepareStatement(sqlStr);
		
			// Our results
			rs = stmt.executeQuery();

			// Iterate over list and populate our return list
			while (rs.next()) {
				int projectID = rs.getInt(1);
				retList.add(ProjectFactory.getProject(projectID));	
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
