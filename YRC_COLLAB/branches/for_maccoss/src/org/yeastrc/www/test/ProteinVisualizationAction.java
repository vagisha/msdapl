/*
 * ProteinVisualizationAction.java
 * Created on Oct 19, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.test;

import java.awt.Dimension;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.nr_seq.NRProteinFactory;
import java.util.*;
import org.yeastrc.microscopy.*;

import com.sun.media.imageio.stream.RawImageInputStream;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 19, 2006
 */

public class ProteinVisualizationAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		String[] hydrophobic = { "G", "A", "V", "L", "I", "M", "F", "W", "P" };
		Arrays.sort( hydrophobic );

		
		String[] polar = { "S", "T", "C", "Y", "N", "Q" };
		Arrays.sort( polar );
		
		String[] negative = { "D", "E" };
		String[] positive = { "H", "K", "R" };

		
		
		String prot1 = request.getParameter( "prot1" );
		String prot2 = request.getParameter( "prot2" );
		
		if (prot2 == null)
			prot2 = prot1;
		
		// the sequences of the two proteins to analyze
		String seq2 = NRProteinFactory.getInstance().getProtein( Integer.parseInt( prot1 ) ).getPeptide().getSequenceString();
		String seq1 = NRProteinFactory.getInstance().getProtein( Integer.parseInt( prot2 ) ).getPeptide().getSequenceString();
		
		int[][] pixelMatrix = new int[seq1.length()][seq2.length()];
		char[] seq1chars = seq1.toCharArray();
		char[] seq2chars = seq2.toCharArray();
		
		Connection conn = DBConnectionManager.getConnection( "yrc" );
		
		for (int i = 0; i < seq1chars.length; i++ ) {
			for (int k = 0; k < seq2chars.length; k++ ) {
				
				String sql = "SELECT score FROM ProteinVisualization.Matrix1 WHERE res1 = '" + seq1chars[i] + "' AND res2 = '" + seq2chars[k] + "'";
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery( sql );
				rs.next();
				
				int score = rs.getInt( 1 );
				
				/*
				if (seq1chars[i] != seq2chars[k]) {
					score = score * 4;
				} else {
					score = 255;
				}
				*/
				
				//score = 255 - score;
				pixelMatrix[i][k] = score;
				
				rs.close(); rs = null;
				stmt.close(); stmt = null;
			}
		}
		
		conn.close(); conn = null;
		
		
		
		// create tiff from pixel matrix
		byte[] bit8image = new byte[ seq1chars.length * seq2chars.length * 3];
		//byte[] bit8image = new byte[ seq1chars.length * seq2chars.length ];
		int pindex = 0;
		for (int i = 0; i < pixelMatrix.length; i++ ) {
			
			for (int k = 0; k < pixelMatrix[i].length; k ++ ) {

				
				
				boolean phobic1 = false;
				boolean phobic2 = false;
				boolean neg1 = false;
				boolean neg2 = false;
				boolean pos1 = false;
				boolean pos2 = false;
				
				if (Arrays.binarySearch( hydrophobic, String.valueOf( seq1chars[i]) ) > 0 )
					phobic1 = true;
				
				if (Arrays.binarySearch (hydrophobic, String.valueOf( seq2chars[k]) ) > 0)
					phobic2 = true;
				
				if (Arrays.binarySearch( negative, String.valueOf( seq1chars[i]) ) > 0 )
					neg1 = true;
				
				if (Arrays.binarySearch( negative, String.valueOf( seq2chars[k]) ) > 0 )
					neg2 = true;

				if (Arrays.binarySearch( positive, String.valueOf( seq1chars[i]) ) > 0 )
					pos1 = true;
				
				if (Arrays.binarySearch( positive, String.valueOf( seq2chars[k]) ) > 0 )
					pos2 = true;
				
				
				// both are hydrophilic
				if (!phobic1 && !phobic2) {
					
					if (neg1 && neg2) {		// cyan
						bit8image[ pindex ] = (byte)0;
						bit8image[ pindex + (seq1chars.length * seq2chars.length) ] = (byte)pixelMatrix[i][k];
						bit8image[ pindex + (seq1chars.length * seq2chars.length) * 2 ] = (byte)pixelMatrix[i][k];						
					} else if (pos1 && pos2) {	// magenta
						bit8image[ pindex ] = (byte)pixelMatrix[i][k];
						bit8image[ pindex + (seq1chars.length * seq2chars.length) ] = (byte)0;
						bit8image[ pindex + (seq1chars.length * seq2chars.length) * 2 ] = (byte)pixelMatrix[i][k];							
					} else if (!pos1 && !pos2 && !neg1 && !neg2) {	// green
						bit8image[ pindex ] = (byte)0;
						bit8image[ pindex + (seq1chars.length * seq2chars.length) ] = (byte)pixelMatrix[i][k];
						bit8image[ pindex + (seq1chars.length * seq2chars.length) * 2 ] = (byte)0;	
					} else {	// blue
						bit8image[ pindex ] = (byte)0;
						bit8image[ pindex + (seq1chars.length * seq2chars.length) ] = (byte)0;
						bit8image[ pindex + (seq1chars.length * seq2chars.length) * 2 ] = (byte)pixelMatrix[i][k];
					}

				// both are hydrophobic, color yellow
				} else if (phobic1 && phobic2 ) {
					bit8image[ pindex ] = (byte)pixelMatrix[i][k];
					bit8image[ pindex + (seq1chars.length * seq2chars.length) ] = (byte)pixelMatrix[i][k];
					bit8image[ pindex + (seq1chars.length * seq2chars.length) * 2 ] = (byte)0;					

				// one is hydrophobic, one is hydrophilic, color red
				} else {
					bit8image[ pindex ] = (byte)pixelMatrix[i][k];
					bit8image[ pindex + (seq1chars.length * seq2chars.length) ] = (byte)pixelMatrix[i][k];
					bit8image[ pindex + (seq1chars.length * seq2chars.length) * 2 ] = (byte)pixelMatrix[i][k];
				}
				
				
				
				//bit8image[ pindex ] = (byte)pixelMatrix[i][k];
				
				pindex++;
				
			}
			
		}
		
		
		/*
        ImageInputStream iis = new MemoryCacheImageInputStream( new ByteArrayInputStream( bit8image ) );
        int height = seq1chars.length;
        int width = seq2chars.length;

        SampleModel sm = new BandedSampleModel( DataBuffer.TYPE_BYTE, width, height, 1 );
        Dimension[] dd = { new Dimension(width,height) };
        long[] ll = {0};
        ColorSpace colorSpace = ColorSpace.getInstance( ColorSpace.CS_GRAY );
        ColorModel colorModel = new ComponentColorModel( colorSpace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE );

        ImageTypeSpecifier its = new ImageTypeSpecifier(colorModel, sm);

        RawImageInputStream rs = new RawImageInputStream( iis, its, ll, dd );
        rs.setByteOrder( ByteOrder.LITTLE_ENDIAN );
        BufferedImage image = ImageIO.read( rs );
        */
		
		
		
        // Create a BufferedImage out of the data array
        int width = seq2chars.length;
        int height = seq1chars.length;
		ImageInputStream iis = new MemoryCacheImageInputStream( new ByteArrayInputStream( bit8image ) );
        SampleModel sm = new BandedSampleModel( DataBuffer.TYPE_BYTE, width, height, 3 );
        Dimension[] dd = { new Dimension(width,height) };
        long[] ll = {0};
        ColorSpace colorSpace = ColorSpace.getInstance( ColorSpace.CS_LINEAR_RGB );
        ColorModel colorModel = new ComponentColorModel( colorSpace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE );

        ImageTypeSpecifier its = new ImageTypeSpecifier(colorModel, sm);

        RawImageInputStream rs = new RawImageInputStream( iis, its, ll, dd );
        rs.setByteOrder( ByteOrder.LITTLE_ENDIAN );

        BufferedImage image = ImageIO.read( rs );
        image = ImageConverter.getInstance().convertImage( image, "tiff" );        	
        
		
		
		
		ImageIO.write( image, "tiff", new File( "c:\\tmp\\" + prot1 + "_" + prot2 + ".tif" ) );
		
		return mapping.findForward( null );
		
	}
}
