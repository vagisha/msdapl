/*
 * R3DDataSaver.java
 * Created on Jun 21, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.internal.microscopy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.microscopy.Experiment;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 21, 2006
 */

public class R3DDataSaver {

	// private constructor
	private R3DDataSaver() { }

	/**
	 * Get an instance of this class
	 * @return
	 */
	public static R3DDataSaver getInstance() {
		return new R3DDataSaver();
	}
	
	/**
	 * Associates the supplied R3D binary data with the supplied experiment in the database.  An experiment may only have
	 * one set of R3D data associated with it, so this data will replace any data currently in the database for this experiment.
	 * @param experiment
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public int save( Experiment experiment, byte[] data ) throws Exception {
		
		if (experiment == null)
			throw new Exception( "Experiment may not be null." );
		
		if (data == null)
			throw new Exception( "R3D data may not be null." );
		
		if (experiment.getId() == 0)
			throw new Exception( "Experiment has no ID.  Make sure to save it to the database first." );
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			// Get our connection to the database.
			conn = DBConnectionManager.getConnection("yrc");
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = null;
			
			// Get our updatable result set
			String sql = "SELECT * FROM tblR3DData WHERE locExpID = " + experiment.getId();
			rs = stmt.executeQuery(sql);
			
			if (rs.next()) {
				
				// we're replacing an entry in the database for this experiment
				
				rs.updateBytes( "r3dData", data );
				rs.updateRow();
				
				
			} else {
				
				// we're adding a new entry to the database for this experiment
				rs.moveToInsertRow();
				rs.updateBytes( "r3dData", data);
				rs.updateInt( "locExpID", experiment.getId() );
				rs.insertRow();
				
			}
			
			
			// close all our db stuff
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
		
		
		
		return 1;
	}
	
}


