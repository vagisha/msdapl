/**
 * ProteinComplexImageMapGenerator.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: Mar 16, 2007 at 6:24:29 PM
 */

package org.yeastrc.www.test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
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

import y.io.ImageMapOutputHandler;
import y.io.JPGIOHandler;
import y.io.LinkMap;
import y.view.DefaultBackgroundRenderer;
import y.view.DefaultGraph2DRenderer;
import y.view.Graph2D;
import y.view.Graph2DView;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Mar 16, 2007
 *
 * Class definition goes here
 */
public class ProteinComplexImageMapGenerator extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		final String OUTPUT_DIRECTORY = "D:\\tmp\\complexes\\big\\";
		//final int[] publications = { 1, 10, 11, 12, 13, 14, 16 };
		final int[] publications = { 1, 10, 11, 12, 13, 14, 16 };
		//final int[] publications = { 1 };
		
		
		for ( int publication : publications ) {
			
			System.out.println( "\n\nPUBLICATION: " + publication );
			
			// where we're outputting the images
			String directory = OUTPUT_DIRECTORY + publication;
			
			String sql = "SELECT DISTINCT B.proteinID FROM tblComplex AS A INNER JOIN tblComplexMembers AS B ON A.id = B.complexID WHERE A.publicationID = " + publication;
			
			Connection conn = DBConnectionManager.getConnection( "pdr" );
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
			    
			    File mapDirectory = new File (output_directory + "\\map");
			    if (!mapDirectory.exists())
			    	mapDirectory.mkdirs();

			    File imageDirectory = new File (output_directory + "\\image");
			    if (!imageDirectory.exists())
			    	imageDirectory.mkdirs();

			    File mapFile = new File( output_directory + "\\map\\" + protein.getId() + ".map" );
			    if (mapFile.exists()) mapFile.delete();
			    
			    File imageFile = new File( output_directory + "\\image\\" + protein.getId() + ".png" );
			    if (imageFile.exists()) imageFile.delete();
				
			    Graph2D graph = new Graph2D();
			    LinkMap linkMap = new LinkMap();
				NewProteinComplexImageMapMaker pccimm = NewProteinComplexImageMapMaker.getInstance();
				
				// create the graph and image map, skip it if there's a problem making it
				try {
					pccimm.createProteinComplexImageMap(protein, publication, graph, linkMap);
				} catch (Exception e) {
					continue;
				}
				
				JPGIOHandler jpg = new JPGIOHandler();
			    jpg.setQuality((float)(10.0));
			    Graph2DView view = jpg.createDefaultGraph2DView(graph);
			    DefaultGraph2DRenderer renderer = (DefaultGraph2DRenderer)view.getGraph2DRenderer();

			    renderer.setDrawEdgesFirst( true );
			    view.setAntialiasedPainting( true );
			    
			    DefaultBackgroundRenderer bgrenderer = (DefaultBackgroundRenderer)view.getBackgroundRenderer();
			    bgrenderer.setColor( new Color (251, 255, 192 ) );
			    
			    BufferedImage bi = null;
			    
			    try {
			    	bi = (BufferedImage)(view.getImage());
			    } catch (Exception e) { ; }
			    if (bi == null) {
			    	System.out.println( "\tCould not create image... skipping." );
			    	continue;
			    }
				
			    ImageMapOutputHandler htmlIO = new ImageMapOutputHandler();
			    linkMap.setMapName("complex_image_map");
			    htmlIO.setReferences(linkMap);
				
				
				System.out.println( "\tWriting: " + imageFile.getAbsolutePath() );
				ImageIO.write(bi, "png", imageFile );
				
			    System.out.println("\tWriting MAP to " + mapFile.getCanonicalPath());
			    PrintWriter htmlOut = new PrintWriter(new FileWriter( mapFile ));
			    String htmlMap = htmlIO.createHTMLString(graph);
			    htmlOut.println( htmlMap ); 

			    htmlOut.close();
			    htmlOut = null;
				
			}
			try {
				rs.close(); rs = null;
				stmt.close(); stmt = null;
				conn.close(); conn = null;
			} catch (Exception e) { ; }
		}
			
			
		
		return null;
	}
}
