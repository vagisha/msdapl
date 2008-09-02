/*
 * GOAnnotationSaver.java
 * Created on Jun 21, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.internal.microscopy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.yeastrc.bio.go.GONode;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.microscopy.FullFieldImage;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 21, 2006
 */

public class GOAnnotationSaver {

	// private constructor
	private GOAnnotationSaver() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static GOAnnotationSaver getInstance() {
		return new GOAnnotationSaver();
	}
	
	
	/**
	 * Associate the given go term with the supplied full field image in the database
	 * If the given go term is already associated with this image, nothing will happen
	 * 
	 * @param image
	 * @param gonode
	 * @return
	 * @throws Exception
	 */
	public int save( FullFieldImage image, GONode gonode ) throws Exception {
		
		if (image == null)
			throw new Exception( "Image may not be null." );
		
		if (gonode == null)
			throw new Exception( "GONode may not be null." );
		
		if (image.getId() == 0)
			throw new Exception( "Image must be saved before associating go terms.  ID was 0." );
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			// Get our connection to the database.
			conn = DBConnectionManager.getConnection("yrc");
			
			// Get our result set
			String sql = "SELECT * FROM tblLocalizationGOAnnotations WHERE goAcc = ? AND ffImgID = ?";
			stmt = conn.prepareStatement( sql );
			stmt.setString( 1, gonode.getAccession() );
			stmt.setInt( 2, image.getId() );
			
			rs = stmt.executeQuery();

			if (!rs.next()) {

				PreparedStatement pstmt = null;
				
				try {
					sql = "INSERT INTO tblLocalizationGOAnnotations (ffImgID, goAcc) VALUES (?, ?)";
					pstmt = conn.prepareStatement(sql);

					pstmt.setInt( 1, image.getId() );
					pstmt.setString( 2, gonode.getAccession() );
					pstmt.executeUpdate();					

					pstmt.close(); pstmt = null;

				} finally {
					if (pstmt != null) {
						try {
							pstmt.close(); pstmt = null;
						} catch (Exception e) { ; }
					}
				}
				
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
