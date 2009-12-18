/* Y2HScreenSearcher.java
 * Created on Jun 16, 2004
 */
package org.yeastrc.y2h;

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
 * @version 1.0, Jun 16, 2004
 *
 */
public class Y2HScreenSearcher {

	/**
	 * Performs the search of the Screen table, based on the search parameters set via method calls such as setProjectID()
	 * @return A List of the matching Screen objects
	 * @throws SQLException if there is a database error
	 */
	public List search() throws SQLException, InvalidIDException, Exception {
		List retList = new ArrayList();
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			// Our SQL statement
			String sqlStr =  "SELECT screenID FROM tblY2HScreen";
		
			if (this.getProjectID() != 0) {
				sqlStr += " WHERE projectID = " + this.getProjectID();
			}
			
			// Order by the most recently uploaded data if requested
			if (this.mostRecent) {
				sqlStr += " ORDER BY uploadDate DESC";
			}
			
			// Limit the # of results returned to the requested amount
			if (numResults != 0) {
				sqlStr += " LIMIT " + numResults;
			}
			

			stmt = conn.prepareStatement(sqlStr);

			// Our results
			rs = stmt.executeQuery();

			// Iterate over list and populate our return list
			while (rs.next()) {
				Y2HScreen ys = new Y2HScreen();
				ys.load(rs.getInt("screenID"));			
				retList.add(ys);
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

	public Y2HScreenSearcher() {
		projectID = 0;
		researcherID = 0;	
	}

	private int projectID;
	private int researcherID;
	private int numResults;
	private boolean mostRecent;
	
	
	
	/**
	 * @return Returns the mostRecent.
	 */
	public boolean isMostRecent() {
		return mostRecent;
	}
	/**
	 * @param mostRecent The mostRecent to set.
	 */
	public void setMostRecent(boolean mostRecent) {
		this.mostRecent = mostRecent;
	}
	/**
	 * @return Returns the numResults.
	 */
	public int getNumResults() {
		return numResults;
	}
	/**
	 * @param numResults The numResults to set.
	 */
	public void setNumResults(int numResults) {
		this.numResults = numResults;
	}
	/**
	 * Get the projectID that is currently set
	 * @return
	 */
	public int getProjectID() {
		return projectID;
	}

	/**
	 * Get the researcherID that is currently set
	 * @return
	 */
	public int getResearcherID() {
		return researcherID;
	}

	/**
	 * Set the projectID to which all returned Screens must belong
	 * @param i
	 */
	public void setProjectID(int i) {
		projectID = i;
	}

	/**
	 * Set the researcherID, such that the supplied researcher has access to all of the returned Screens
	 * @param i
	 */
	public void setResearcherID(int i) {
		researcherID = i;
	}

}
