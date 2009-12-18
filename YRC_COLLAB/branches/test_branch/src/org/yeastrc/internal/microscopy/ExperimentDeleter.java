/*
 * ExperimentDeleter.java
 * Created on Jul 10, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.internal.microscopy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.microscopy.Experiment;
import org.yeastrc.microscopy.FullFieldImage;


/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jul 10, 2006
 */

public class ExperimentDeleter {

	// private constructor
	private ExperimentDeleter() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static ExperimentDeleter getInstance() {
		return new ExperimentDeleter();
	}
	
	/**
	 * Deletes the supplied experiment from the database, and cascades downwards, deleting all associated full field images, r3d data, etc
	 * @param experiment
	 * @throws Exception
	 */
	public void delete( Experiment experiment ) throws Exception {
		
		if (experiment == null || experiment.getId() == 0)
			return;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		//ResultSet rs = null;
		
		try {
			
			conn = DBConnectionManager.getConnection("yrc");
			
			// Delete the FullField images
			List ffImages = experiment.getFullFieldImages();
			Iterator iter = ffImages.iterator();
			while (iter.hasNext()) {
				FullFieldImageDeleter.getInstance().delete( (FullFieldImage)iter.next() );
			}
			
			// Delete the R3D data entry
			String sql = "DELETE FROM tblR3DData WHERE locExpID = ?";
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, experiment.getId() );
			stmt.executeUpdate();
			stmt.close(); stmt = null;
			
			// Delete the Experiment
			sql = "DELETE FROM tblLocExperiments WHERE locExpID = ?";
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, experiment.getId() );
			stmt.executeUpdate();
			
			// close all our db stuff
			//rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
			// Set the Experiment ID to 0
			experiment.setId( 0 );
			
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
