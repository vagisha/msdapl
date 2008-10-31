/*
 * ExperimentSaver.java
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
import org.yeastrc.nr_seq.NRProtein;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 21, 2006
 */

public class ExperimentSaver {

	// private constructor
	private ExperimentSaver() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static ExperimentSaver getInstance() {
		return new ExperimentSaver();
	}
	
	/**
	 * Saves the data in the supplied microscopy experiment object to the database
	 * If it's not already in the database, a new entry will be made.  If it already exists in the database,
	 * its data will be updated.  NOTE:  The R3D raw data IS NOT SAVED OR UPDATED USING THIS METHOD.  USE THE
	 * R3DSAVER CLASS INSTEAD
	 *<BR><BR>
	 * If this is a new record for the database, the id of the experiment is also saved in the experiment passed into
	 * this method
	 * 
	 * @param experiment The experiment to save
	 * @return The numerical ID of this experiment in the database
	 * @throws Exception If there is a problem
	 */
	public int save(Experiment experiment) throws Exception {
		
		// experiment is null?!
		if (experiment == null)
			throw new Exception( "Experiment may not be null." );
		
		// don't have a project
		if (experiment.getProjectID() < 1)
			throw new Exception( "Experiment must have a projectID to be saved." );
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		//System.out.println("Saving experiment...");
		
		try {
			
			// Get our connection to the database.
			conn = DBConnectionManager.getConnection("yrc");
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = null;
			
			// Get our updatable result set
			String sql = "SELECT * FROM tblLocExperiments WHERE locExpID = " + experiment.getId();
			rs = stmt.executeQuery(sql);
			
			
			if (experiment.getId() > 0) {
			
				// we are updating this experiment
				
				// we're supposed to be updating, but it wasn't found in the database
				if (!rs.next())
					throw new Exception ( "Attempted to updated the record in the database, but it wasn't found..." );

				// set the values in the database
				rs.updateInt( "projectID", experiment.getProjectID() );
				rs.updateString( "locLogText", experiment.getLogText() );
				rs.updateDate( "locExpDate", new java.sql.Date( experiment.getExperimentDate().getTime() ) );
				rs.updateString( "locOpticsObjective", experiment.getOpticsObjective() );
				rs.updateString( "locOpticsLensID", experiment.getOpticsLensID() );
				rs.updateDouble( "locOpticsAuxMagn", experiment.getOpticsAuxMargin() );
				rs.updateString( "locCameraType", experiment.getCameraType() );
				rs.updateString( "locCameraGain", experiment.getCameraGain() );
				rs.updateString( "locCameraSpeed", experiment.getCameraSpeed() );
				rs.updateString( "locCameraTemp", experiment.getCameraTemperature() );
				rs.updateString( "locImgXYDim", experiment.getXYDimensions() );
				rs.updateString( "locImgZWTDim", experiment.getZWTDimensions() );
				rs.updateString( "locImgPixelSize", experiment.getPixelSize() );
				rs.updateString( "locImgBinning", experiment.getBinning() );
				rs.updateString( "locImgFlatFieldCal", experiment.getFlatFieldCal() );
				rs.updateString( "locCellStatus", experiment.getCellStatus() );
				rs.updateString( "locCellTreatment", experiment.getCellTreatment() );
				rs.updateString( "locCellGrowthMedium", experiment.getCellGrowthMedium() );
				rs.updateString( "locCellGrowthTemp", experiment.getCellGrowthTemperature() );
				
				if (experiment.getComments() != null && !experiment.getComments().equals( "" ))
					rs.updateString( "locComments", experiment.getComments());
				else
					rs.updateNull( "locComments" );
				
				rs.updateString( "locORF1Tag", experiment.getTag1() );
				
				if (experiment.getTag2() != null && !experiment.getTag2().equals( "" ))
					rs.updateString( "locORF2Tag", experiment.getTag2());
				else
					rs.updateNull( "locORF2Tag" );
				
				if (experiment.getTag3() != null && !experiment.getTag3().equals( "" ))
					rs.updateString( "locORF3Tag", experiment.getTag3());
				else
					rs.updateNull( "locORF3Tag" );
				
				rs.updateInt( "baitProtein1ID", ((NRProtein)experiment.getBait1()).getId() );
				
				if (experiment.getBait2() != null)
					rs.updateInt( "baitProtein2ID", ((NRProtein)experiment.getBait2()).getId() );
				else
					rs.updateNull( "baitProtein2ID" );
				
				if (experiment.getBait3() != null)
					rs.updateInt( "baitProtein3ID", ((NRProtein)experiment.getBait3()).getId() );				
				else
					rs.updateNull( "baitProtein3ID" );

				// Update the row
				rs.updateRow();
				
			} else {
				
				//System.out.println("Inserting row...");
				
				// this is a new experiment
				rs.moveToInsertRow();
				
				// set the values in the database
				rs.updateInt( "projectID", experiment.getProjectID() );
				rs.updateString( "locLogText", experiment.getLogText() );
				rs.updateDate( "locExpDate", new java.sql.Date( experiment.getExperimentDate().getTime() ) );
				rs.updateString( "locOpticsObjective", experiment.getOpticsObjective() );
				rs.updateString( "locOpticsLensID", experiment.getOpticsLensID() );
				rs.updateDouble( "locOpticsAuxMagn", experiment.getOpticsAuxMargin() );
				rs.updateString( "locOpticsDichroicMirror", experiment.getOpticsDichroicMirror() );
				rs.updateString( "locCameraType", experiment.getCameraType() );
				rs.updateString( "locCameraGain", experiment.getCameraGain() );
				rs.updateString( "locCameraSpeed", experiment.getCameraSpeed() );
				rs.updateString( "locCameraTemp", experiment.getCameraTemperature() );
				rs.updateString( "locImgXYDim", experiment.getXYDimensions() );
				rs.updateString( "locImgZWTDim", experiment.getZWTDimensions() );
				rs.updateString( "locImgPixelSize", experiment.getPixelSize() );
				rs.updateString( "locImgBinning", experiment.getBinning() );
				rs.updateString( "locImgFlatFieldCal", experiment.getFlatFieldCal() );
				rs.updateString( "locCellStatus", experiment.getCellStatus() );
				rs.updateString( "locCellTreatment", experiment.getCellTreatment() );
				rs.updateString( "locCellGrowthMedium", experiment.getCellGrowthMedium() );
				rs.updateString( "locCellGrowthTemp", experiment.getCellGrowthTemperature() );
				
				if (experiment.getComments() != null && !experiment.getComments().equals( "" ))
					rs.updateString( "locComments", experiment.getComments());
				
				rs.updateString( "locORF1Tag", experiment.getTag1() );
				
				if (experiment.getTag2() != null && !experiment.getTag2().equals( "" ))
					rs.updateString( "locORF2Tag", experiment.getTag2());
				
				if (experiment.getTag3() != null && !experiment.getTag3().equals( "" ))
					rs.updateString( "locORF3Tag", experiment.getTag3());				
				
				rs.updateInt( "baitProtein1ID", ((NRProtein)experiment.getBait1()).getId() );
				
				if (experiment.getBait2() != null)
					rs.updateInt( "baitProtein2ID", ((NRProtein)experiment.getBait2()).getId() );
				
				if (experiment.getBait3() != null)
					rs.updateInt( "baitProtein3ID", ((NRProtein)experiment.getBait3()).getId() );				

				// Update the row
				//System.out.println("Calling insertRow()");
				rs.insertRow();
				
				// Get the ID generated for this item
				rs.last();
				
				// set the id in the experiment
				experiment.setId( rs.getInt( "locExpID" ) );
				
				//System.out.println("Experiment ID:" + experiment.getId() );
				
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
		
		
		
		return experiment.getId();
	}
	
}
