/*
 * SelectedRegionDeleter.java
 * Created on Jul 10, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.internal.microscopy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.microscopy.SelectedRegionImage;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jul 10, 2006
 */

public class SelectedRegionDeleter {

	// private constructor
	private SelectedRegionDeleter() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static SelectedRegionDeleter getInstance() {
		return new SelectedRegionDeleter();
	}
	
	public void delete( SelectedRegionImage image ) throws Exception {
		
		if (image == null || image.getId() == 0)
			return;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		//ResultSet rs = null;
		
		try {
			
			conn = DBConnectionManager.getConnection("yrc");
			
			String sql = "DELETE FROM tblLocImgSR WHERE srID = ?";
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, image.getId() );
			stmt.executeUpdate();
			
			// Set the Experiment ID to 0
			image.setId( 0 );
			
			// close all our db stuff
			//rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
		}
		finally {

			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
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
