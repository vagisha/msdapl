package org.yeastrc.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.yeastrc.db.DBConnectionManager;

/**
 * This class is to provide a set of  methods and variable definitions that
 * will provide utility functionality for researchers
 *
 * @version 2010-07-01
 * @author Michael Riffle <mriffle@u.washington.edu>
 */
public class ResearcherUtils {

	// Our single instance of this class
	private static final ResearcherUtils INSTANCE = new ResearcherUtils();
	
	// private constructor
	private ResearcherUtils() { }
	
	/**
	 * Get the instance of this class
	 * @return The instance of this class
	 */
	public static ResearcherUtils getInstance() { return INSTANCE; }
	
	/**
	 * Determine whether this researcher is a PI on any project
	 * @param r
	 * @return
	 * @throws Exception
	 */
	public boolean isPI( Researcher r ) throws Exception {
		
		boolean isPI = false;
		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnectionManager.getConnection("yrc");
			
			String sql = "SELECT projectID FROM tblProjects WHERE projectPI = ? LIMIT 1";
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, r.getID() );
			
			rs = stmt.executeQuery();
			
			if( rs.next() )
				isPI = true;

			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
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
		
		
		return isPI;
	}
}
