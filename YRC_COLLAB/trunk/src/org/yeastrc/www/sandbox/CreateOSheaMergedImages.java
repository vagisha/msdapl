package org.yeastrc.www.sandbox;

import java.sql.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.db.DBConnectionManager;

import org.yeastrc.microscopy.*;
import java.util.*;

/**
 * Go through and created a "merged" channel for all OShea experiments and put the
 * merged image into the appropriate PDR database table
 * 
 * @author Mike
 *
 */
public class CreateOSheaMergedImages extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
	
		// loop through all oshea experiments
		Connection conn = DBConnectionManager.getConnection( "pdr" );
		Statement stmt = conn.createStatement();
		
		String sql = "SELECT locExpID FROM tblLocExperiments WHERE projectID = 800218";
		ResultSet rs = stmt.executeQuery( sql );
		while ( rs.next() ) {
			int expID = rs.getInt( 1 );
			FullFieldImageSearcher ffis = FullFieldImageSearcher.getInstance();
			ffis.setExperimentID( expID );
			List<FullFieldImage> images = ffis.search();
			
			// loop through all images in the experiment
			int[] r = new int[ 535 * 512 ];
			int[] g = new int[ 535 * 512 ];
			int[] b = new int[ 535 * 512 ];

			// should be 3 images per experiment, assign pixels to appropriate channel arrays
			
			if ( (images.get( 0 ) ).getEMFilter().equals( "GFP" ) )
				( images.get( 0 ) ).getImage().getRaster().getSamples( 0, 0, 535, 512, 0, g );
			else throw new Exception( "DIDN'T GET GFP IN FIRST CHANNEL" );
			
			if ( (images.get( 1 ) ).getEMFilter().equals( "DAPI" ) ) {
				( images.get( 1 ) ).getImage().getRaster().getSamples( 0, 0, 535, 512, 0, b );
				( images.get( 2 ) ).getImage().getRaster().getSamples( 0, 0, 535, 512, 0, r );
			} else if ( (images.get( 1 ) ).getEMFilter().equals( "RFP" ) ) {
				( images.get( 1 ) ).getImage().getRaster().getSamples( 0, 0, 535, 512, 0, r );
				( images.get( 2 ) ).getImage().getRaster().getSamples( 0, 0, 535, 512, 0, b );
			}
			else throw new Exception( "DIDN'T GET DAPI OR RFP IN SECOND CHANNEL" );
			
			// normalize each channel for viewing
			normalizePixels( r );
			normalizePixels( g );
			normalizePixels( b );
			
			// combine channels into a RGB TIFF
			
			
			// safe TIFF to database
			
			
				
		}// end looping through experiments
		
		
		return null;
	}
	
	// normalize pixels in pixel array for maximal viewing
	private void normalizePixels( int[] pixels ) {
		
		int min = getMinPixel( pixels );
		int max = getMaxPixel( pixels );
		
		for ( int i = 0; i < pixels.length; i++ ) {
			int pix = pixels[ i ];
        	double intensity = ( ( (double)pix - (double)min ) / ( (double)max - (double)min ) ) * (double)65535;
        	pixels[ i ] = (int)intensity;
		}
		
	}
	
	private int getMinPixel( int[] pixels ) {
		int min = 65535;
		for ( int i : pixels ) {
			if ( i < min ) min = i;
		}
		return min;
	}
	
	private int getMaxPixel( int[] pixels ) {
		int max = 0;
		for ( int i : pixels ) {
			if ( i > max ) max = i;
		}
		return max;
	}
	
}
