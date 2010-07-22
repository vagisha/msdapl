package org.yeastrc.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.yeastrc.db.DBConnectionManager;

public class ResearcherAutocompleteSearcher {

	private static ResearcherAutocompleteSearcher INSTANCE = new ResearcherAutocompleteSearcher();
	public static ResearcherAutocompleteSearcher getInstance() { return INSTANCE; }
	
	private ResearcherAutocompleteSearcher() { }
	
	/**
	 * Search the researchers table for the query string, to be used in auto complete form
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public Map<String, Integer> search( String query ) throws Exception {
		
		// Use LinkedHashMap to preserve order of the keys
		Map<String, Integer> retMap = new LinkedHashMap<String, Integer>();

		if( query == null || query.equals( "" ) )
			return retMap;
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "SELECT researcherID, researcherFirstName, researcherLastName, researcherEmail FROM tblResearchers WHERE researcherLastName LIKE ?";
			stmt = conn.prepareStatement( sql );
			stmt.setString( 1, query + "%" );
			
			rs = stmt.executeQuery();
			
			while ( rs.next() ) {
				retMap.put( rs.getString( 3 ) + ", " + rs.getString( 2 ) + " <" + rs.getString( 4 ) + ">", rs.getInt( 1 ) );
			}
			
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
		
		return retMap;
	}
	
	
}
