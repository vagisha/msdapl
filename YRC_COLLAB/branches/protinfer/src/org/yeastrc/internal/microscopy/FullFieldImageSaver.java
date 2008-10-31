/*
 * FullFieldImageSaver.java
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
import org.yeastrc.microscopy.FullFieldImage;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 21, 2006
 */

public class FullFieldImageSaver {

	// private constructor
	private FullFieldImageSaver() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static FullFieldImageSaver getInstance() {
		return new FullFieldImageSaver();
	}
	
	/**
	 * Saves the data in the supplied microscopy experiment full field image object to the database
	 * If it's not already in the database, a new entry will be made.  If it already exists in the database,
	 * its data will be updated.  NOTE:  The IMAGE DATA IS NOT SAVED HERE, USE THE IMAGESAVER CLASS
	 *<BR><BR>
	 * If this is a new record for the database, the id is also saved in the object passed into
	 * this method
	 * 
	 * @param image The fullfieldimage object to save
	 * @return The numerical ID of this object in the database
	 * @throws Exception If there is a problem
	 */
	public int save( FullFieldImage image ) throws Exception {
		
		// we got null for the image?!
		if (image == null)
			throw new Exception( "Got null for the image when we tried to save it." );
		
		Experiment experiment = image.getExperiment();
		if (experiment == null)
			throw new Exception( "The image has a null experiment." );
		
		if (experiment.getId() == 0)
			throw new Exception( "The experiment has an id of 0.  Be sure to save the experiment before the images." );
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			// Get our connection to the database.
			conn = DBConnectionManager.getConnection("yrc");
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = null;
			
			// Get our updatable result set
			String sql = "SELECT * FROM tblLocImgFullField WHERE ffID = " + image.getId();
			rs = stmt.executeQuery(sql);
			
			
			if (image.getId() > 0) {
			
				// updating this entry in the database

				// we're supposed to be updating, but it wasn't found in the database
				if (!rs.next())
					throw new Exception ( "Attempted to updated the record in the database, but it wasn't found..." );

				
				rs.updateInt( "locExpID", experiment.getId() );
				
				if (image.getComments() != null && !image.getComments().equals( "" ))
					rs.updateString( "ffComments", image.getComments() );
				else
					rs.updateNull( "ffComments" );
				
				rs.updateString( "ffTimePoint", image.getTimePoint() );
				rs.updateString( "ffPhotosensorReading", image.getPhotosensorReading() );
				rs.updateString( "ffExposureTime", image.getExposureTime() );
				rs.updateString( "ffEXFilter", image.getEXFilter() );
				rs.updateString( "ffEMFilter", image.getEMFilter() );
				rs.updateString( "ffNDFilter", image.getNDFilter() );
				rs.updateString( "ffStageCoordinates", image.getStageCoordinates() );
				rs.updateString( "ffIntensityMin", image.getIntensityMin() );
				rs.updateString( "ffIntensityMax", image.getIntensityMax() );
				rs.updateString( "ffIntensityAvg", image.getIntensityAvg() );
				
				// Update the row
				rs.updateRow();
				
			} else {
				
				// new entry for the database
				rs.moveToInsertRow();
				
				rs.updateInt( "locExpID", experiment.getId() );
				
				if (image.getComments() != null && !image.getComments().equals( "" ))
					rs.updateString( "ffComments", image.getComments() );
				else
					rs.updateNull( "ffComments" );
				
				rs.updateString( "ffTimePoint", image.getTimePoint() );
				rs.updateString( "ffPhotosensorReading", image.getPhotosensorReading() );
				rs.updateString( "ffExposureTime", image.getExposureTime() );
				rs.updateString( "ffEXFilter", image.getEXFilter() );
				rs.updateString( "ffEMFilter", image.getEMFilter() );
				rs.updateString( "ffNDFilter", image.getNDFilter() );
				rs.updateString( "ffStageCoordinates", image.getStageCoordinates() );
				rs.updateString( "ffIntensityMin", image.getIntensityMin() );
				rs.updateString( "ffIntensityMax", image.getIntensityMax() );
				rs.updateString( "ffIntensityAvg", image.getIntensityAvg() );
				
				// Update the row
				rs.insertRow();
				
				// Get the ID generated for this item
				rs.last();
				image.setId( rs.getInt( "ffID" ) );
				
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
