/* ReportProjectsSearcher.java
 * Created on Jun 24, 2004
 */
package org.yeastrc.progress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Jun 24, 2004
 *
 */
public class ReportProjectsSearcher {

	/**
	 * Get a list of Project objects to include in a certain year's annual report
	 * @return
	 */
	public static List search(int year) throws Exception {
		ArrayList retList = new ArrayList();

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			// Our SQL statement
			String sqlStr =  "SELECT projectID FROM tblReportProjects WHERE reportYear = ?";
			
			stmt = conn.prepareStatement(sqlStr);
			stmt.setInt(1, year);

			// Our results
			rs = stmt.executeQuery();

			// Iterate over list and populate our return list
			while (rs.next()) {
				int projectID = rs.getInt("projectID");
				
				try {
					Project p = ProjectFactory.getProject(projectID);
					retList.add(p);
					
				} catch (Exception e) { ; }
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
