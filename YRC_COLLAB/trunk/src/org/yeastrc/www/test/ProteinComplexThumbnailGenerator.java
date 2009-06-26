/**
 * ProteinComplexThumbnailGenerator.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: Mar 16, 2007 at 1:59:27 PM
 */

package org.yeastrc.www.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;


/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Mar 16, 2007
 *
 * Class definition goes here
 */
public class ProteinComplexThumbnailGenerator extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		final String OUTPUT_DIRECTORY = "D:\\tmp\\complexes\\thumbnails\\";
		
		// 1  10  11  12  13  14  16 23
		final int[] publications = { 24 };
		
		Connection conn = DBConnectionManager.getConnection( "pdr" );
		
		
		for ( int publication : publications ) {
			
			System.out.println( "\n\nPUBLICATION: " + publication );
			
			// where we're outputting the images
			String directory = OUTPUT_DIRECTORY + publication;
			
			String sql = "SELECT DISTINCT B.proteinID FROM tblComplex AS A INNER JOIN tblComplexMembers AS B ON A.id = B.complexID WHERE A.publicationID = " + publication;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			while (rs.next()) {
				NRProtein protein = (NRProtein)NRProteinFactory.getInstance().getProtein( rs.getInt( 1 ) );
				System.out.println( "\n\tProtein: " + protein.getListing() );
				
				String proteinString = "" + protein.getId();
				
			    String output_directory = proteinString.substring(proteinString.length() - 1, proteinString.length());
			    output_directory = proteinString.substring( proteinString.length() - 2, proteinString.length() - 1 ) + "\\" + output_directory;
			    output_directory = proteinString.substring( proteinString.length() - 3, proteinString.length() - 2 ) + "\\" + output_directory;
			    output_directory = directory + "\\" + output_directory;
			    
			    File directoryFile = new File (output_directory);
			    if (!directoryFile.exists())
			    	directoryFile.mkdirs();
			    
			    File outputFile = new File( output_directory + "\\" + protein.getId() + ".png" );
			    if (outputFile.exists()) outputFile.delete();
				
				BufferedImage image = NewProteinComplexThumbnailMaker.getInstance().getThumbnail(protein, publication);
				if (image == null)
					continue;
				
				System.out.println( "\tWriting: " + outputFile.getAbsolutePath() );
				ImageIO.write(image, "png", outputFile );
			}			
		}
		
		
		return null;
	}

}
