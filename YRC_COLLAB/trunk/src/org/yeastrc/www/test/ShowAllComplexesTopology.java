/*
 * MSCounterNetworkVewier.java
 * Created on Jan 29, 2007
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.nr_seq.*;
import y.base.Node;
import y.io.JPGIOHandler;
import y.layout.organic.SmartOrganicLayouter;
import y.view.Arrow;
import y.view.DefaultGraph2DRenderer;
import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.Graph2DView;
import y.view.LineType;
import y.view.PolyLineEdgeRealizer;
import y.view.ShapeNodeRealizer;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jan 29, 2007
 */

public class ShowAllComplexesTopology extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		int publication = Integer.parseInt( request.getParameter( "dataset" ) );
		Set vPairs = new HashSet();
		
		Connection conn = DBConnectionManager.getConnection( "pdr" );
		Statement stmt = conn.createStatement();
		String sql = "SELECT id FROM tblComplex WHERE publicationID = " + publication;
		ResultSet rs = stmt.executeQuery( sql );
		
		while( rs.next() ) {
			
			sql = "SELECT proteinID FROM tblComplexMembers WHERE complexID = " + rs.getInt( 1 );
			Set<Integer> pp = new HashSet<Integer>();
			
			Statement pstmt = conn.createStatement();
			ResultSet prs = pstmt.executeQuery( sql );
			while ( prs.next() ) {
				pp.add( prs.getInt( 1 ) );
			}
			
			prs.close(); prs = null;
			pstmt.close(); pstmt = null;
			
			for( Integer pi : pp ) {
				
				// added because we need species test
				NRProtein p1 = (NRProtein)NRProteinFactory.getInstance().getProtein( pi );
				
				for( Integer pk : pp ) {
					
					if ( pk.equals( pi ) ) continue;
					
					// added because we need species test
					NRProtein p2 = (NRProtein)NRProteinFactory.getInstance().getProtein( pk );
					
					// do species test
					if ( !p1.getSpecies().equals( p2.getSpecies() ) ) continue;
					
					Set<Integer> tmpSet = new HashSet<Integer>();
					tmpSet.add( pi );
					tmpSet.add( pk );
					
					
					
					
					vPairs.add( tmpSet );
					
				}
			}	
		}
		
		rs.close(); rs = null;
		stmt.close(); stmt = null;
		conn.close(); conn = null;
		
		//vPairs is now a set of 2-member sets of protein names that all have edges at this cutoff

		
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
			String p1 = null;
			String p2 = null;

			Iterator tmpIter = tmpPair.iterator();
			
			while (tmpIter.hasNext()) {
				if (p1 == null) p1 = String.valueOf( tmpIter.next() );
				else p2 = String.valueOf( tmpIter.next() );
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

			er.setLineColor( this.getEdgeColor(p1, p2) );
			graph.createEdge(node1, node2);
		}
		
		
		SmartOrganicLayouter layouter = new SmartOrganicLayouter();
		layouter.setCompactness(0.2);
		layouter.setQualityTimeRatio(1.0);
		layouter.setMinimalNodeDistance(20);

		layouter.doLayout(graph);
		
		
		
		//BalloonLayouter layouter = new BalloonLayouter();
		
		//OrthogonalLayouter layouter = new OrthogonalLayouter();
		
		
		/*
		HierarchicLayouter layouter = new HierarchicLayouter();
		//OrganicLayouter layouter = new OrganicLayouter();
		layouter.setOrientationLayouter(new OrientationLayouter(OrientationLayouter.TOP_TO_BOTTOM));
		//layouter.setOrientationLayouter(new OrientationLayouter(OrientationLayouter.LEFT_TO_RIGHT));
		layouter.setLayoutStyle(HierarchicLayouter.TREE);
	    layouter.doLayout(graph);	    
	    */
		
		layouter.doLayout(graph);
		
	    JPGIOHandler jpg = new JPGIOHandler();
	    jpg.setQuality((float)(7.0));

	    Graph2DView view = jpg.createDefaultGraph2DView(graph);
	    DefaultGraph2DRenderer renderer = (DefaultGraph2DRenderer)view.getGraph2DRenderer();

	    renderer.setDrawEdgesFirst( true );
	    view.setAntialiasedPainting( true );
	    
	    BufferedImage bi = (BufferedImage)(view.getImage());
		
		
	    request.setAttribute("image", bi);		
		
		return mapping.findForward( "Success" );
	}
	
	
	private Color getEdgeColor( String p1, String p2 ) throws Exception {

		return new Color( 0, 0, 0 );
		
		/*
		int minDist = getMinDistance( p1, p2 );		

		if (minDist == 0)
			return new Color( 218, 0, 215 );
		
		if (minDist == 1)
			return new Color( 15, 0, 218 );
		
		if (minDist == 2)
			return new Color( 0, 210, 218 );
		
		if (minDist == 3)
			return new Color( 0, 218, 41 );
		
		if (minDist == 4)
			return new Color( 185, 218, 0 );
		
		if (minDist == 5)
			return new Color( 218, 164, 0 );
		
		if (minDist < 99999)
			return new Color( 218, 0, 0 );
		
		return new Color( 0, 0, 0 );
		*/
	}
	
	private int getMinDistance( NRProtein p1, NRProtein p2 ) throws Exception {
		
		int minDistance = 99999;
		
		if (p1 == null || p2 == null)
			return minDistance;
		
		Set g1 = null;
		Set g2 = null;
		
		g1 = (Set)p1.getGOBiologicalProcess();
		g2 = (Set)p2.getGOBiologicalProcess();

		if (g1 == null) {
			g1 = new HashSet();
			g1.add( GOUtils.getAspectRootNode( GOUtils.BIOLOGICAL_PROCESS ) );
		}

		if (g2 == null) {
			g2 = new HashSet();
			g2.add( GOUtils.getAspectRootNode( GOUtils.BIOLOGICAL_PROCESS ) );
		}
		
		// need to properly handle unknowns
		
		Iterator i1 = g1.iterator();
		while (i1.hasNext()) {
			GONode node1 = (GONode)i1.next();
			
			if (node1.getName().equals( "biological process unknown") ) {
				System.out.println( "node1 is unknown proc." );
				return 0;
			}

			Iterator i2 = g2.iterator();
			while (i2.hasNext()) {
				GONode node2 = (GONode)i2.next();

				if (node2.getName().equals( "biological process unknown") ) {
					System.out.println( "node2 is unknown proc." );
					return 0;
				}
					
				
				int distance = GOUtils.getShortestDistance( node1, node2 );
				//System.out.println( distance );
				
				if (distance < 0)
					distance *= -1;
				
				if (distance < minDistance)
					minDistance = distance;
					
			}	
		}
		return minDistance;	
	}
	
}
