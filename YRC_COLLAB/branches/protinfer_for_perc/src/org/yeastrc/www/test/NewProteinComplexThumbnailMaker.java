/**
 * ProteinComplexThumbnailMaker.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: Mar 16, 2007 at 1:55:09 PM
 */

package org.yeastrc.www.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;

import y.base.Node;
import y.io.JPGIOHandler;
import y.layout.organic.SmartOrganicLayouter;
import y.view.Arrow;
import y.view.DefaultBackgroundRenderer;
import y.view.DefaultGraph2DRenderer;
import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.Graph2DView;
import y.view.LineType;
import y.view.PolyLineEdgeRealizer;
import y.view.ShapeNodeRealizer;



/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Mar 16, 2007
 *
 * Class definition goes here
 */
public class NewProteinComplexThumbnailMaker {

	private NewProteinComplexThumbnailMaker() { }
	public static NewProteinComplexThumbnailMaker getInstance() {
		return new NewProteinComplexThumbnailMaker();
	}
	
	public BufferedImage getThumbnail( NRProtein focalProtein, int publicationID ) throws Exception {

		// holds all pairs of proteins, between which there is an edge
		Set vPairs = new HashSet();
		Set master_proteins = new HashSet();

		Connection conn = DBConnectionManager.getConnection( "pdr" );
		Statement stmt = conn.createStatement();

		
		String sql = "SELECT DISTINCT proteinID FROM tblComplexMembers WHERE complexID IN ( SELECT complexID FROM tblComplexMembers AS a INNER JOIN tblComplex AS b ON a.complexID = b.id WHERE a.proteinID = " + focalProtein.getId() + " AND b.publicationID = " + publicationID + ")";
		ResultSet rs = stmt.executeQuery( sql );
		while (rs.next()) {
			NRProtein p = (NRProtein)NRProteinFactory.getInstance().getProtein( rs.getInt( 1 ) );
			master_proteins.add( p );
		}
		rs.close(); rs = null;
		System.out.println( "\t\tFound " + master_proteins.size() + " cocomplexed proteins, determining edges..." );
		
		Iterator p1i = master_proteins.iterator();
		while (p1i.hasNext()) {
			NRProtein p1 = (NRProtein)p1i.next();
			
			Iterator p2i = master_proteins.iterator();
			while (p2i.hasNext()) {
				NRProtein p2 = (NRProtein)p2i.next();
				
				if (p1.getId() == p2.getId()) continue;
				
				// see if p1 and p2 are cocomplexed
				sql = "SELECT complexID FROM tblComplexMembers WHERE proteinID = " + p1.getId() + " AND complexID IN ( SELECT complexID FROM tblComplexMembers WHERE proteinID = " + p2.getId() + ")";
				Statement stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery( sql );
				if (rs2.next()) {
					Set tmpSet = new HashSet( 2 );
					tmpSet.add( p1 );
					tmpSet.add( p2 );
					
					if (vPairs.contains( tmpSet))
						continue;
					
					vPairs.add( tmpSet );
				}
				rs2.close(); rs2 = null;
				stmt2.close(); stmt2 = null;
			}
		}
		
		stmt.close(); stmt = null;
		conn.close(); conn = null;
		
		if (master_proteins.size() < 2)
			return null;
		
		//vPairs is now a set of 2-member sets of protein names that all have edges at this cutoff
		//System.out.println( "vPairs size: " + vPairs.size() );
		//System.out.println( "Proteins size: " + master_proteins.size() );
		
		// DRAW THE PICTURE
		Graph2D graph = new Graph2D();
		Map addedNodes = new HashMap();
		
		ShapeNodeRealizer realizer = new ShapeNodeRealizer();
		realizer.setShapeType(ShapeNodeRealizer.ELLIPSE);
		realizer.setFillColor( new Color ( 0, 0, 0 ) );
		realizer.setFillColor2( new Color ( 0, 0, 0 ) );
		realizer.setLineColor( new Color ( 0, 0, 0 ) );
		
		graph.setDefaultNodeRealizer(realizer);

		EdgeRealizer er = new PolyLineEdgeRealizer();
		er.setArrow(Arrow.NONE);
		er.setLineColor( new Color ( 0, 0, 0 ) );
		er.setLineType( LineType.LINE_1 );
		
		graph.setDefaultEdgeRealizer(er);
		
		Iterator iter = vPairs.iterator();
		while (iter.hasNext()) {
			Set tmpPair = (Set)iter.next();
			NRProtein p1 = null;
			NRProtein p2 = null;

			Iterator tmpIter = tmpPair.iterator();
			while (tmpIter.hasNext()) {
				if (p1 == null) p1 = (NRProtein)tmpIter.next();
				else p2 = (NRProtein)tmpIter.next();
			}
			
			if (p1 == null || p2 == null)
				continue;
			
			Node node1 = null;
			if (addedNodes.containsKey(p1))
				node1 = (Node)(addedNodes.get(p1));
			else {
				node1 = graph.createNode();
				graph.setSize(node1, 5, 5);
				
				addedNodes.put(p1, node1);
			}

			Node node2 = null;
			if (addedNodes.containsKey(p2))
				node2 = (Node)(addedNodes.get(p2));
			else {
				node2 = graph.createNode();
				graph.setSize(node2, 5, 5);
				
				addedNodes.put(p2, node2);
			}			

			// Create the edge in the graph
			er.setLineType( LineType.LINE_1 );

			graph.createEdge(node1, node2);
		}
		
		
		SmartOrganicLayouter layouter = new SmartOrganicLayouter();
		layouter.setCompactness(0.3);
		layouter.setQualityTimeRatio(1.0);
		layouter.setMinimalNodeDistance(5);
		
		layouter.doLayout(graph);
		
		for ( int i = 0; i < 10; i++) {
			if (graph.getBoundingBox().getWidth() > 160) {
				System.out.println( "Bounding box is size: " + graph.getBoundingBox().getWidth() + " ... redrawing. " );
				layouter.doLayout( graph );
			}
			else
				break;
		}
		
	    JPGIOHandler jpg = new JPGIOHandler();
	    jpg.setQuality((float)(10.0));
	    
	    Graph2DView view = jpg.createDefaultGraph2DView(graph);
	    view.setAntialiasedPainting( true );

	    
	    DefaultGraph2DRenderer renderer = (DefaultGraph2DRenderer)view.getGraph2DRenderer();
	    renderer.setDrawEdgesFirst( true );

	    DefaultBackgroundRenderer bgrenderer = (DefaultBackgroundRenderer)view.getBackgroundRenderer();
	    bgrenderer.setColor( new Color (251, 255, 192 ) );
	 	    
	    BufferedImage bi = (BufferedImage)(view.getImage());
	    
	    // we need to get this down to 150 wide
	    if (bi.getWidth( null ) > 150) {
	    	double scale = 150.0 / (double)bi.getWidth( null );
	    	
	    	bi = toBufferedImage( bi.getScaledInstance(150, (int)(bi.getHeight( null ) * scale), Image.SCALE_AREA_AVERAGING), bi.getType() );	    	
	    }
	    
	    
	    return bi;
	}
	
    private BufferedImage toBufferedImage(Image image, int type) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        BufferedImage result = new BufferedImage(w, h, type);
        Graphics2D g = result.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }
}
