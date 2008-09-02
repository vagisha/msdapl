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
import org.yeastrc.nr_seq.NRDatabaseUtils;
import org.yeastrc.nr_seq.NRProtein;

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
import y.view.DefaultBackgroundRenderer;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jan 31, 2007
 */

public class ComplexThumbnailer extends Action {

	public static ComplexThumbnailer getInstance() {
		return new ComplexThumbnailer();
	}
	
	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		String dataset = request.getParameter( "dataset" );
		String cutoff = request.getParameter( "cutoff" );
		String orf = request.getParameter( "orf" );
		
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
					p = NRDatabaseUtils.getInstance().findProteinByName(orfName, Species.getInstance( TaxonomyUtils.SACCHAROMYCES_CEREVISIAE ) );
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
		
		for ( int i = 0; i < 100; i++) {
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

	    request.setAttribute("image", bi);		
	
		return mapping.findForward( "Success" );
	}
	
}
