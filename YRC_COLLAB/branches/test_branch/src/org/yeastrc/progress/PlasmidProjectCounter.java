/*
 * PlasmidProjectCounter.java
 * Created on Jun 15, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.progress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.yeastrc.db.DBConnectionManager;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 15, 2006
 */

public class PlasmidProjectCounter {

	// private constructor
	private PlasmidProjectCounter() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static PlasmidProjectCounter getInstance() {
		return new PlasmidProjectCounter();
	}
	
	/**
	 * Get the number of plasmid projects in this year's report for the given type (M, T or MT)
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public int countProjects(String type) throws Exception {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;

		// Our SQL statement
		String sqlStr =  "SELECT count(*) FROM tblReportProjects AS a INNER JOIN tblProjects AS b ON a.projectID = b.projectID " +
				"INNER JOIN tblDissemination AS c ON b.projectID = c.projectID WHERE a.reportYear = 2009 AND b.projectType = 'D' AND " +
				"(c.shipPlasmidType = '" + type + "' OR c.shipPlasmidType = 'MT')";
			
		stmt = conn.prepareStatement( sqlStr );
		rs = stmt.executeQuery();
		rs.next();
		
		int num = rs.getInt( 1 );
		
		rs.close();
		stmt.close();
		conn.close();
		
		return num;
	}
	
}
