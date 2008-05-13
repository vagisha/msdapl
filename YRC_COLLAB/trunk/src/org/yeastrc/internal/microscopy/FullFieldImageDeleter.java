/*
 * FullFieldImageDeleter.java
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
import org.yeastrc.microscopy.FullFieldImage;
import org.yeastrc.microscopy.SelectedRegionImage;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jul 10, 2006
 */

public class FullFieldImageDeleter {

	// private constructor
	private FullFieldImageDeleter() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static FullFieldImageDeleter getInstance() {
		return new FullFieldImageDeleter();
	}
	
	public void delete( FullFieldImage image ) throws Exception {
		
		if (image == null || image.getId() == 0)
			return;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			
			conn = DBConnectionManager.getConnection("yrc");
			
			// delete the selected regions from this image
			List selectedRegions = image.getSelectedRegions();
			Iterator iter = selectedRegions.iterator();
			while (iter.hasNext()) {
				SelectedRegionDeleter.getInstance().delete( (SelectedRegionImage)iter.next() );
			}
			
			// delete the GO annotations for this image
			String sql = "DELETE FROM tblLocalizationGOAnnotations WHERE ffImgID = ?";
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, image.getId() );
			stmt.executeUpdate();
			
			
			// delete this image
			
			sql = "DELETE FROM tblLocImgFullField WHERE ffID = ?";
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, image.getId() );
			stmt.executeUpdate();

			// close all our db stuff
			//rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
			// Set the Experiment ID to 0
			image.setId( 0 );

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
