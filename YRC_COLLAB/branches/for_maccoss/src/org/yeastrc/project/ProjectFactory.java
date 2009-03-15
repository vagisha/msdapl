/*
 * Projects.java
 *
 * Created February 4, 2004
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;



/**
 * This class provides static methods for obtaining Project objects
 *
 * @version 2004-02-04
 * @author Michael Riffle <mriffle@u.washington.edu>
 */
public class ProjectFactory {
	
	/**
	 * Get a Project object from the supplied project ID.  The true class for the Project returned
	 * is the type of Project it is (e.g. Collaboration or Training)
	 * @param projectID The project ID to return
	 * @return The project corresponding to that ID.
	 */
	public static Project getProject(int projectID) throws SQLException, InvalidProjectTypeException, InvalidIDException {
		ArrayList retList = new ArrayList();

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			// Our SQL statement
			String sqlStr =  "SELECT projectType FROM tblProjects WHERE ";
				   sqlStr += "projectID = ?";
			
			stmt = conn.prepareStatement(sqlStr);
			stmt.setInt(1, projectID);

			// Our results
			rs = stmt.executeQuery();

			// Iterate over list and populate our return list
			if (rs.next()) {
				String type = rs.getString("projectType");
				Project proj;
				
				if (type.equals("C")) {
					proj = new Collaboration();
				} else {
					throw new InvalidProjectTypeException("Type wasn't C, D, T or Tech...");
				}
				
				proj.load(projectID);
				
				rs.close();
				rs = null;
				
				stmt.close();
				stmt = null;
							
				conn.close();
				conn = null;
				
				return proj;
			} else {
				throw new InvalidIDException("No Project found with ID " + projectID);
			}
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