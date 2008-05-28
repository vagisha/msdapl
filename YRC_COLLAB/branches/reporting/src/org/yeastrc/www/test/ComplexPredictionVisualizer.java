/*
 * ComplexPredictionVisualizer.java
 * Created on Jan 31, 2007
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.test;

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
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.bio.taxonomy.TaxonomyUtils;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.nr_seq.*;

import y.view.DefaultGraph2DRenderer;
import y.view.Graph2DRenderer;

import java.awt.Font;
import y.layout.router.OrganicEdgeRouter;
import y.base.Node;
import y.io.JPGIOHandler;
import y.layout.organic.SmartOrganicLayouter;
import y.layout.circular.*;
import y.view.Arrow;
import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.Graph2DView;
import y.view.NodeLabel;
import y.view.PolyLineEdgeRealizer;
import y.view.ShapeNodeRealizer;
import java.awt.Color;
import y.view.LineType;
import org.yeastrc.bio.go.*;
/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jan 31, 2007
 */

public class ComplexPredictionVisualizer extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		String dataset = request.getParameter( "dataset" );
		String cutoff = request.getParameter( "cutoff" );
		String orf = request.getParameter( "orf" );
		String thumbnail = request.getParameter( "thumbnail" );
		
		if (thumbnail != null && thumbnail.equals( "yes") )
			return ComplexThumbnailer.getInstance().execute(mapping, form, request, response);
		
		// holds all pairs of proteins, between which there is an edge
		Set vPairs = new HashSet();
		Set master_proteins = new HashSet();
		
		NRProtein focalProtein = null;
		try {
			if (orf != null && !orf.equals(""))
				focalProtein = NRDatabaseUtils.getInstance().findProteinByName(orf, Species.getInstance( TaxonomyUtils.SACCHAROMYCES_CEREVISIAE ) );
		} catch (Exception e) { ; }
		
		Connection conn = DBConnectionManager.getConnection( "yrc" );
		Statement stmt = conn.createStatement();

		String sql = null;
		if (focalProtein == null) {
			sql = "SELECT complexID FROM PredictedComplexes." + dataset + "Complexes";
			if (cutoff != null && !cutoff.equals(""))
				sql += " WHERE cutoff = '" + cutoff + "'";
		} else {
			if (cutoff != null && !cutoff.equals(""))
				sql = "SELECT a.complexID FROM PredictedComplexes." + dataset + "ComplexMembers AS A INNER JOIN PredictedComplexes." + dataset + "Complexes AS b ON a.complexID = b.complexID WHERE a.orfName = '" + orf + "' AND b.cutoff = '" + cutoff + "'";
			else
				sql = "SELECT complexID FROM PredictedComplexes." + dataset + "ComplexMembers WHERE orfName = '" + orf + "'";	
		}
			
		System.out.println( sql );
		
		ResultSet rs = stmt.executeQuery( sql );
		while (rs.next()) {
			String complexID = rs.getString( 1 );
			Set proteins = new HashSet();
			
			sql = "SELECT orfName FROM PredictedComplexes." + dataset + "ComplexMembers WHERE complexID = '" + complexID + "'";
			Statement stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery( sql );
			while (rs2.next()) {
				String orfName = rs2.getString( 1 );
				NRProtein p = null;
				try {
					//p = NRDatabaseUtils.getInstance().findProteinByName(orfName, Species.getInstance( TaxonomyUtils.SACCHAROMYCES_CEREVISIAE ) );
					p = (NRProtein)NRProteinFactory.getInstance().getProtein( Integer.parseInt( orfName ) );
				} catch (Exception e) {
					continue;
				}
				
				if (p == null)
					continue;
				
				proteins.add( p );
			}
			rs2.close(); rs2 = null;
			stmt2.close(); stmt2 = null;
			
			master_proteins.addAll( proteins );
			
			Iterator pIter1 = proteins.iterator();
			while (pIter1.hasNext()) {
				NRProtein p1 = (NRProtein)pIter1.next();
				
				Iterator pIter2 = proteins.iterator();
				while (pIter2.hasNext()) {
					NRProtein p2 = (NRProtein)pIter2.next();
					
					if (p1.equals(p2))
						continue;
					
					Set tmpSet = new HashSet( 2 );
					tmpSet.add( p1 );
					tmpSet.add( p2 );
					
					if (vPairs.contains( tmpSet))
						continue;
					
					vPairs.add( tmpSet );
				}//end pIter2 iteration
			}//end pIter2 iteration
		}//end ResultSet rs iteration
		
		rs.close(); rs = null;
		stmt.close(); stmt = null;
		conn.close(); conn = null;
		
		//vPairs is now a set of 2-member sets of protein names that all have edges at this cutoff
		System.out.println( "vPairs size: " + vPairs.size() );
		System.out.println( "Proteins size: " + master_proteins.size() );
		
		// DRAW THE PICTURE
		Graph2D graph = new Graph2D();
		Map addedNodes = new HashMap();
		
		ShapeNodeRealizer realizer = new ShapeNodeRealizer();
		realizer.setShapeType(ShapeNodeRealizer.ELLIPSE);
		realizer.setLayer( Graph2DView.FG_LAYER, false );
		realizer.setFillColor( new Color ( 255, 200, 200 ) );
		realizer.setFillColor2( new Color ( 255, 100, 100 ) );
		realizer.setLineColor( new Color ( 50, 0, 0 ) );
		
		NodeLabel nl = new NodeLabel();
		nl.setTextColor( new Color (0, 0, 0) );

		if (master_proteins.size() > 50) {
			nl.setFontSize(10);
		} else if (master_proteins.size() > 30) {
			nl.setFontSize( 12 );
		} else if (master_proteins.size() > 20) {
			nl.setFontSize( 16 );
		} else if (master_proteins.size() > 10 ) {
			nl.setFontSize( 20 );
		} else if (master_proteins.size() > 5 ) {
			nl.setFontSize( 22 );
		} else {
			nl.setFontSize( 24 );
		}

		
		nl.setFontStyle( Font.BOLD );
		realizer.setLabel(nl);
		
		graph.setDefaultNodeRealizer(realizer);

		EdgeRealizer er = new PolyLineEdgeRealizer();
		er.setArrow(Arrow.NONE);
		//er.setLineColor( new Color ( 50, 20, 150 ) );
		er.setLineType( LineType.LINE_4 );
		er.setLayer( Graph2DView.BG_LAYER );
		
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

				String label = p1.getListing();
				graph.setLabelText(node1, label);
				
				graph.setSize(node1, graph.getLabelLayout(node1)[0].getBox().width + 10, graph.getLabelLayout(node1)[0].getBox().height + 5);
				//graph.setSize(node1, 10, 10);
				
				addedNodes.put(p1, node1);
			}

			Node node2 = null;
			if (addedNodes.containsKey(p2))
				node2 = (Node)(addedNodes.get(p2));
			else {
				node2 = graph.createNode();

				String label = p2.getListing();
				graph.setLabelText(node2, label);
				
				graph.setSize(node2, graph.getLabelLayout(node2)[0].getBox().width + 10, graph.getLabelLayout(node2)[0].getBox().height + 5);
				//graph.setSize(node2, 10, 10);
				
				addedNodes.put(p2, node2);
			}			

			// Create the edge in the graph
			
			if (p1.equals( focalProtein) || p2.equals( focalProtein ) )
				er.setLineType( LineType.LINE_4 );
			else
				er.setLineType( LineType.LINE_1 );
			
			er.setLineColor( getEdgeColor( p1, p2 ) );
			graph.createEdge(node1, node2);
		}
		
		
		SmartOrganicLayouter layouter = new SmartOrganicLayouter();
		layouter.setCompactness(0.2);
		layouter.setQualityTimeRatio(1.0);
		layouter.setMinimalNodeDistance(20);
		
		
		/*
		CircularLayouter layouter = new CircularLayouter();
		*/
		
		
		
		layouter.doLayout(graph);
		
		for ( int i = 0; i < 1000; i++) {
			if (graph.getBoundingBox().getWidth() > 800) {
				System.out.println( "Bounding box is size: " + graph.getBoundingBox().getWidth() + " ... redrawing. " );
				layouter.doLayout( graph );
			}
			else
				break;
		}
		
		
	    JPGIOHandler jpg = new JPGIOHandler();
	    jpg.setQuality((float)(10.0));
	    Graph2DView view = jpg.createDefaultGraph2DView(graph);
	    DefaultGraph2DRenderer renderer = (DefaultGraph2DRenderer)view.getGraph2DRenderer();

	    renderer.setDrawEdgesFirst( true );
	    view.setAntialiasedPainting( true );
	    
	    BufferedImage bi = (BufferedImage)(view.getImage());

	    //view.setDrawingMode( Graph2DView.LAYER_MODE );
	    //view.removeAll();
	    //view.paint( bi.getGraphics() );

	    request.setAttribute("image", bi);		
		
		return mapping.findForward( "Success" );
	}
	
	private Color getEdgeColor( NRProtein p1, NRProtein p2 ) throws Exception {

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
		*/
		
		return new Color( 0, 0, 0 );

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
