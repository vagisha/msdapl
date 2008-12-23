/*
 * MSCounterNetworkVewier.java
 * Created on Jan 29, 2007
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.bio.taxonomy.TaxonomyUtils;
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
import y.view.NodeLabel;
import y.view.PolyLineEdgeRealizer;
import y.view.ShapeNodeRealizer;
import yext.svg.io.*;
import org.yeastrc.bio.protein.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jan 29, 2007
 */

public class MSCounterNetworkViewer extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		String dataset = request.getParameter( "dataset" );
		String cutoff = request.getParameter( "cutoff" );
		
		
		Connection conn = DBConnectionManager.getConnection( "yrc" );
		Statement stmt = conn.createStatement();
		String sql = "SELECT orfOne, orfTwo FROM PredictedComplexes." + dataset + "Compares WHERE pvalue <= " + cutoff + " AND orfOne <> orfTwo";
		ResultSet rs = stmt.executeQuery( sql );
		
		Set vPairs = new HashSet();
		while (rs.next()) {
			Set tmpSet = new HashSet();
			String orfOne = rs.getString( 1 );
			String orfTwo = rs.getString( 2 );

			NRProtein p1 = NRDatabaseUtils.getInstance().findProteinByName(orfOne, Species.getInstance( TaxonomyUtils.SACCHAROMYCES_CEREVISIAE ) );
			//NRProtein p1 = (NRProtein)NRProteinFactory.getInstance().getProtein( Integer.parseInt( orfOne ) );
			
			if (p1 == null)
				continue;
			
			NRProtein p2 = NRDatabaseUtils.getInstance().findProteinByName(orfTwo, Species.getInstance( TaxonomyUtils.SACCHAROMYCES_CEREVISIAE ) );
			//NRProtein p2 = (NRProtein)NRProteinFactory.getInstance().getProtein( Integer.parseInt( orfTwo ) );
			
			if (p2 == null)
				continue;
			
			tmpSet.add( p1 );
			tmpSet.add( p2 );
			
			if (vPairs.contains( tmpSet ))
				continue;
			
			vPairs.add( tmpSet );
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
		realizer.setFillColor( new Color ( 255, 200, 200 ) );
		realizer.setFillColor2( new Color ( 255, 100, 100 ) );
		realizer.setLineColor( new Color ( 50, 0, 0 ) );

		NodeLabel nl = new NodeLabel();
		nl.setFontSize(12);
		nl.setFontStyle( Font.BOLD );
		realizer.setLabel(nl);		
		
		graph.setDefaultNodeRealizer(realizer);

		EdgeRealizer er = new PolyLineEdgeRealizer();
		er.setArrow(Arrow.NONE);
		er.setLineType( LineType.LINE_4 );

		graph.setDefaultNodeRealizer(realizer);
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

				//String label = p1.getListing();
				String label = ProteinNamerFactory.getInstance().getProteinNamer( p1 ).getListing( p1 );
				graph.setLabelText(node1, label);
				
				graph.setSize(node1, graph.getLabelLayout(node1)[0].getBox().width + 10, graph.getLabelLayout(node1)[0].getBox().height + 5);
				//graph.setSize(node1, 5, 5);
				
				addedNodes.put(p1, node1);
			}

			Node node2 = null;
			if (addedNodes.containsKey(p2))
				node2 = (Node)(addedNodes.get(p2));
			else {
				node2 = graph.createNode();

				//String label = p2.getListing();
				String label = ProteinNamerFactory.getInstance().getProteinNamer( p2 ).getListing( p2 );
				graph.setLabelText(node2, label);
				
				graph.setSize(node2, graph.getLabelLayout(node2)[0].getBox().width + 10, graph.getLabelLayout(node2)[0].getBox().height + 5);
				//graph.setSize(node2, 5, 5);
				
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
	    jpg.setQuality((float)(12.0));

	    //SVGIOHandler svg = new SVGIOHandler();
	    //SVGGraph2DRenderer renderer = new SVGGraph2DRenderer();
	    //renderer.setSVGIOHandler( svg );
	    
	    Graph2DView view = jpg.createDefaultGraph2DView(graph);
	    DefaultGraph2DRenderer renderer = (DefaultGraph2DRenderer)view.getGraph2DRenderer();

	    renderer.setDrawEdgesFirst( true );
	    view.setAntialiasedPainting( true );
	    
	    /*
	    // XML STUFF //
	    File file = new File( "/foo.svg" );
	    FileOutputStream fos = new FileOutputStream( file );
	    svg.write( view.getGraph2D(), fos );
	    fos.close();
		return null;
		// END XML STUFF //
		*/

	    BufferedImage bi = (BufferedImage)(view.getImage());
	    request.setAttribute("image", bi);
		return mapping.findForward( "Success" );
	}
	
	
	private Color getEdgeColor( NRProtein p1, NRProtein p2 ) throws Exception {

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
