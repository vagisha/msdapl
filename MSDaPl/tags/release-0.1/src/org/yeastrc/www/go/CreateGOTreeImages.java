/*
 * CreateGOTreeImages.java
 * Created on Feb 2, 2007
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.go;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GOCache;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.db.DBConnectionManager;

import y.io.ImageMapOutputHandler;
import y.io.JPGIOHandler;
import y.io.LinkMap;
import y.view.Graph2D;
import y.view.Graph2DView;

import org.yeastrc.bio.go.*;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Feb 2, 2007
 */

public class CreateGOTreeImages extends Action {

	private static final String IMAGE_DIRECTORY = "D:\\tmp\\go_images\\";
	private static final String MAP_DIRECTORY = "D:\\tmp\\go_maps\\";
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
			throws Exception {

		
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("go");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		
		String sql = "SELECT DISTINCT acc FROM term WHERE ( acc LIKE 'GO%' OR acc = '" + GOUtils.getRootNode().getAccession() + "' )";
		//String sql = "SELECT DISTINCT acc FROM term WHERE acc = 'GO:0044444'";
		stmt = conn.prepareStatement( sql );
		rs = stmt.executeQuery();
		while (rs.next()) {
			String acc = rs.getString( 1 );
			
			GONode node = null;
			
			try {
				node = GOCache.getInstance().getGONode( acc );
			} catch (Exception e ) {
				continue;
			}
			
			if (node == null)
				continue;
			
			
		    Graph2D graph = new Graph2D();
		    LinkMap linkMap = new LinkMap();
			GOTreeGraphGenerator gen = new GOTreeGraphGenerator();
			gen.getGOImageMap( node, graph, linkMap );
			
			
			JPGIOHandler jpg = new JPGIOHandler();
		    jpg.setQuality((float)(7.0));
		    Graph2DView view = jpg.createDefaultGraph2DView(graph);
			
		    ImageMapOutputHandler htmlIO = new ImageMapOutputHandler();
		    linkMap.setMapName("go_image_map");
		    htmlIO.setReferences(linkMap);
			
		    
		    String imageDirectory = node.getAccession().substring(node.getAccession().length() - 1, node.getAccession().length());
		    imageDirectory = node.getAccession().substring( node.getAccession().length() - 2, node.getAccession().length() - 1 ) + "\\" + imageDirectory;
		    imageDirectory = node.getAccession().substring( node.getAccession().length() - 3, node.getAccession().length() - 2 ) + "\\" + imageDirectory;
		    imageDirectory = IMAGE_DIRECTORY + imageDirectory;
		    
		    File directory = new File (imageDirectory);
		    if (!directory.exists())
		    	directory.mkdirs();
		    
		    String filename = node.getAccession();
		    if( filename.startsWith( "GO" ) )
		    	filename = filename.substring(3, 10);
		    
		    System.out.println(" Filename: " + filename );
		    
		    File file = new File( imageDirectory + "\\" + filename + ".jpg" );
		    if (file.exists()) file.delete();
		    
		    System.out.println("Writing JPEG to " + file.getCanonicalPath());
		    FileOutputStream fos = new FileOutputStream ( file );
		    jpg.write(graph, fos );
		    fos.close();
		    fos = null;

		    String mapDirectory = node.getAccession().substring(node.getAccession().length() - 1, node.getAccession().length());
		    mapDirectory = node.getAccession().substring( node.getAccession().length() - 2, node.getAccession().length() - 1 ) + "\\" + mapDirectory;
		    mapDirectory = node.getAccession().substring( node.getAccession().length() - 3, node.getAccession().length() - 2 ) + "\\" + mapDirectory;
		    mapDirectory = MAP_DIRECTORY + mapDirectory;
		    
		    directory = new File (mapDirectory);
		    if (!directory.exists())
		    	directory.mkdirs();
		    
		    file = new File( mapDirectory + "\\" + filename + ".map" );
		    if (file.exists()) file.delete();
		    
		    System.out.println("Writing HTML to " + file.getCanonicalPath());
		    PrintWriter htmlOut = new PrintWriter(new FileWriter( file ));
		    String htmlMap = htmlIO.createHTMLString(graph);
		    htmlOut.println( htmlMap ); 

		    htmlOut.close();
		    htmlOut = null;
			
		}
		
		System.out.println( "Done" );
		
		return null;
	}
	
}
