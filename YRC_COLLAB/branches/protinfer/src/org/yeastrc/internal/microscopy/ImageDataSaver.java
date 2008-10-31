/*
 * ImageDataSaver.java
 * Created on Jun 21, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.internal.microscopy;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.microscopy.FullFieldImage;
import org.yeastrc.microscopy.SelectedRegionImage;


/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 21, 2006
 */

public class ImageDataSaver {

	// private constructor
	private ImageDataSaver() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static ImageDataSaver getInstance() {
		return new ImageDataSaver();
	}
	
	/**
	 * Save the image data to the supplied FullFieldImage  A given image can only have 1 set of associated image data, so this method
	 * will replace the image data associated w/ the supplied image
	 * @param image
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public int save( FullFieldImage image, RenderedImage imageData ) throws Exception {
		
		if (image == null)
			throw new Exception( "Image may not be null." );
		
		if (imageData == null)
			throw new Exception( "Data may not be null." );
		
		if (image.getId() == 0)
			throw new Exception( "Image ID is 0.  Make sure to save image before saving data for the image." );
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
						
			// Get our connection to the database.
			conn = DBConnectionManager.getConnection("yrc");
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = null;
			
			// Get our updatable result set
			String sql = "SELECT ffID, ffImgData FROM tblLocImgFullField WHERE ffID = " + image.getId();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				
				// write this image out to a ByteArrayOutputStream
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ImageIO.write( imageData, "tiff", bos );
				
				// Save it
				rs.updateBytes( "ffImgData", bos.toByteArray() );
				rs.updateRow();
				
				bos = null;				
				
			} else {
				
				throw new Exception ( "No rows in the database associated with the supplied image (eventhough it has an ID)." );
				
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

	
	
	
	/**
	 * Save the image data to the supplied FullFieldImage  A given image can only have 1 set of associated image data, so this method
	 * will replace the image data associated w/ the supplied image
	 * @param image
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public int save( SelectedRegionImage image, RenderedImage imageData ) throws Exception {
		
		if (image == null)
			throw new Exception( "Image may not be null." );
		
		if (imageData == null)
			throw new Exception( "Data may not be null." );
		
		if (image.getId() == 0)
			throw new Exception( "Image ID is 0.  Make sure to save image before saving data for the image." );
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			// Get our connection to the database.
			conn = DBConnectionManager.getConnection("yrc");
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = null;
			
			// Get our updatable result set
			String sql = "SELECT srID, srImgData FROM tblLocImgSR WHERE srID = " + image.getId();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {

				// write this image out to a ByteArrayOutputStream
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ImageIO.write( imageData, "tiff", bos );
				
				// Save it
				rs.updateBytes( "srImgData", bos.toByteArray() );
				rs.updateRow();

				bos = null;	
				
			} else {
				
				throw new Exception ( "No rows in the database associated with the supplied image (eventhough it has an ID)." );
				
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
