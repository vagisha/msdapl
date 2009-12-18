/* Y2HUtils.java
 * Created on Apr 22, 2004
 */
package org.yeastrc.y2h;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;

/**
 * Set of static methods for working with Y2H data
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Apr 22, 2004
 *
 */
public class Y2HUtils {

	/**
	 * Given the supplied Y2HScreen object, will return a list of ScreenResult objects for results from that screen
	 * @param screen The Y2HScreen object for which to find results
	 * @return A list of Y2HScreenResult objects
	 */
	public static List getScreenResults(Y2HScreen screen) throws SQLException, Exception {
		return Y2HUtils.getScreenResults(screen.getID());
	}

	/**
	 * Given the supplied screen ID, will return a list of ScreenResult objects for results from that screen
	 * @param screenID
	 * @return A list of ScreenResult objects
	 */
	public static List getScreenResults(int screenID) throws SQLException, Exception {
		List retList = new ArrayList();
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT resultID, numHits, preyORF FROM tblY2HScreenResult WHERE screenID = " + screenID + " ORDER BY numHits DESC, preyORF";
			rs = stmt.executeQuery(sqlStr);
			
			while (rs.next()) {
				int rID = rs.getInt("resultID");
				Y2HScreenResult sr = new Y2HScreenResult();

				try { sr.load(rID); }
				catch (InvalidIDException e) { continue; }
				
				retList.add(sr);
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
