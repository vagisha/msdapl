package org.yeastrc.www.sandbox;

import ij.process.ColorProcessor;

import java.io.ByteArrayOutputStream;
import java.sql.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.db.DBConnectionManager;

import org.yeastrc.microscopy.*;

import com.sun.media.jai.codecimpl.TIFFImageEncoder;

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
			System.out.println( "Processing " + expID );
			
			FullFieldImageSearcher ffis = FullFieldImageSearcher.getInstance();
			ffis.setExperimentID( expID );
			List<FullFieldImage> images = ffis.search();
			
			System.out.println( "\tGot " + images.size() + " images." );
			
			try {
			
			// loop through all images in the experiment
			int[] r = new int[ 535 * 512 ];
			int[] g = new int[ 535 * 512 ];
			int[] b = new int[ 535 * 512 ];

			// should be 3 images per experiment, assign pixels to appropriate channel arrays
			
			System.out.println( "\tAssigning color channels." );
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
			System.out.println( "\tNormalizing color channels." );
			normalizePixels( r );
			normalizePixels( g );
			normalizePixels( b );
			
			// create an 8-bit array of pixel values from the 16-bit arrays so we can make a 3x8bit RGB image
			byte[] reds = new byte[ 535 * 512 ];
			byte[] greens = new byte[ 535 * 512 ];
			byte[] blues = new byte[ 535 * 512 ];
						
			System.out.println( "\tAssigning byte values to color channels." );
			for( int i = 0; i < r.length; i++ ) {
				reds[ i ] = (byte)( r[ i ] / 256 );
				greens[ i ] = (byte)( g[ i ] / 256 );
				blues[ i ] = (byte)( b[ i ] / 256 );
			}
			
			
			// combine channels into a RGB TIFF
			System.out.println("\tCreating TIFF." );
			ColorProcessor processor = new ColorProcessor( 535, 512 );
			processor.setRGB( reds, greens, blues );
			
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	TIFFImageEncoder encoder = new TIFFImageEncoder (baos, null);
	    	encoder.encode ( processor.getBufferedImage() );
			
			// safe TIFF to database
	    	System.out.println("\tSaving TIFF to database.\n" );
	    	Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rt = st.executeQuery( "SELECT * FROM tblLocImgFullField WHERE ffID = 0");
	    	
			rt.moveToInsertRow();
			
			rt.updateInt( "locExpID", expID );
			rt.updateString( "ffComments", "Merged image built by YRC." );
			rt.updateString( "ffEXFilter", "MERGED" );
			rt.updateString( "ffEMFilter", "MERGED" );
			rt.updateBytes( "ffImgData", baos.toByteArray() );
			
			rt.insertRow();
			
			rt.close(); rt = null;
			st.close(); st = null;
			
			} catch ( Exception e) {
				System.out.println( "Got an error, skipping this one..." );
				continue;
			}
			
				
		}// end looping through experiments
		
		rs.close(); rs = null;
		stmt.close(); stmt = null;
		conn.close(); conn = null;
		
		System.out.println( "Done." );
		
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
