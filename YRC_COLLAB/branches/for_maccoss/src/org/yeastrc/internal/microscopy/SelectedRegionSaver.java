/*
 * SelectedRegionSaver.java
 * Created on Jun 21, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.internal.microscopy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.microscopy.FullFieldImage;
import org.yeastrc.microscopy.SelectedRegionImage;
/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 21, 2006
 */

public class SelectedRegionSaver {

	// private constructor
	private SelectedRegionSaver() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static SelectedRegionSaver getInstance() {
		return new SelectedRegionSaver();
	}
	
	/**
	 * Save a SelectedRegionImage to the database.  Will not save the binary image data.
	 * @param sr
	 * @return
	 * @throws Exception
	 */
	public int save( SelectedRegionImage image ) throws Exception {
		
		// we got null for the image?!
		if (image == null)
			throw new Exception( "Got null for the image when we tried to save it." );
		
		FullFieldImage ffImage = image.getFullFieldImage();
		if (ffImage == null)
			throw new Exception( "The image has a null full field image." );
		
		if (ffImage.getId() == 0)
			throw new Exception( "The full field image has an id of 0.  Be sure to save the full field image before the SR images." );
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			// Get our connection to the database.
			conn = DBConnectionManager.getConnection("yrc");
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = null;
			
			// Get our updatable result set
			String sql = "SELECT srID, ffID, srComments FROM tblLocImgSR WHERE srID = " + image.getId();
			rs = stmt.executeQuery(sql);
			
			
			if (image.getId() > 0) {
				
					// updating this entry in the database

					// we're supposed to be updating, but it wasn't found in the database
					if (!rs.next())
						throw new Exception ( "Attempted to updated the record in the database, but it wasn't found..." );

					rs.updateInt( "ffID", image.getFullFieldImage().getId() );
					if (image.getComments() == null || image.getComments().equals(""))
						rs.updateNull( "srComments");
					else
						rs.updateString( "srComments", image.getComments() );
					
					// Update the row
					rs.updateRow();
					
				} else {
					
					// new entry for the database
					rs.moveToInsertRow();
					
					rs.updateInt( "ffID", image.getFullFieldImage().getId() );
					if (image.getComments() == null || image.getComments().equals(""))
						rs.updateNull( "srComments");
					else
						rs.updateString( "srComments", image.getComments() );

					// Update the row
					rs.insertRow();
					
					// Get the ID generated for this item
					rs.last();
					image.setId( rs.getInt( "srID" ) );
					
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
		
		
		return image.getId();
	}
	
	
}
