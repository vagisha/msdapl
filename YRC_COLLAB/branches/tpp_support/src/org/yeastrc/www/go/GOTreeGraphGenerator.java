/*
 * GOTreeGraphGenerator.java
 * Created on Feb 2, 2007
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.go;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;

import y.base.Node;
import y.io.LinkInfo;
import y.io.LinkMap;
import y.layout.OrientationLayouter;
import y.layout.hierarchic.HierarchicLayouter;
import y.view.Arrow;
import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.NodeLabel;
import y.view.PolyLineEdgeRealizer;
import y.view.ShapeNodeRealizer;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Feb 2, 2007
 */

public class GOTreeGraphGenerator {
	
	private int fontSize = 10;
	
	public void getGOImageMap( GONode seedNode, Graph2D graph, LinkMap linkMap ) throws Exception {
		Map addedNodes = new HashMap();
		this.seedNode = seedNode;
		
		ShapeNodeRealizer realizer = new ShapeNodeRealizer();

		realizer.setShapeType(ShapeNodeRealizer.ROUND_RECT);
		graph.setDefaultNodeRealizer(realizer);

		EdgeRealizer er = new PolyLineEdgeRealizer();
		er.setArrow(Arrow.SHORT);

		graph.setDefaultNodeRealizer(realizer);
		graph.setDefaultEdgeRealizer(er);
		
		Set gonodes = new HashSet();
		gonodes.add( seedNode );
		
		int totalNodes = 0;
		int numChildren = 0;
		
		if (!seedNode.equals( GOUtils.getRootNode() )) {
		
			// Add two layers of parents (if there are that many) to the set to show
			Set parents = new HashSet();
			try { parents.addAll( seedNode.getParents() ); } catch (Exception e ) { ; }
			if ( parents != null && parents.size() > 0 ) {
				Iterator iter = parents.iterator();
				while (iter.hasNext()) {
					GONode node = (GONode)iter.next();
					if (node == null) continue;
					
					gonodes.add( node );
					totalNodes++;
					
					try {
						totalNodes += node.getParents().size();
						gonodes.addAll( node.getParents() );
					} catch (Exception e) { ; }
					
				}
			}
			
			
			// Add two layers of children (if there are that many) to the set to show
			Set children = new HashSet();
			try {
				children.addAll( seedNode.getChildren() );
				numChildren = seedNode.getChildren().size();
			} catch (Exception e ) { ; }
			if ( children != null && children.size() > 0 ) {
				Iterator iter = children.iterator();
				while (iter.hasNext()) {
					GONode node = (GONode)iter.next();
					if (node == null) continue;
					
					gonodes.add( node );
					totalNodes++;
					
					//try { gonodes.addAll( node.getChildren() ); } catch (Exception e) { ; }
				}
			}
		} else {
			
			gonodes.add( seedNode );
			gonodes.add( GOUtils.getAspectRootNode( GOUtils.BIOLOGICAL_PROCESS ) );
			gonodes.add( GOUtils.getAspectRootNode( GOUtils.CELLULAR_COMPONENT ) );
			gonodes.add( GOUtils.getAspectRootNode( GOUtils.MOLECULAR_FUNCTION ) );
			
		}
		
		if (totalNodes < 5)
			fontSize = 14;
		else if (totalNodes < 10)
			fontSize = 12;
		else
			fontSize = 10;
		
		// add vertices to graph
		Iterator iter = gonodes.iterator();
		while (iter.hasNext()) {
			GONode gonode = (GONode)iter.next();
			if( gonode == null) continue;
			
			this.modifyRealize( realizer, gonode );
			
			Node node = graph.createNode();

			String label = gonode.toString();
			label = label.replaceAll(" ", "\n");
			label = label.replaceAll( "_", "\n" );
			
			graph.setLabelText(node, label);
			
			graph.setSize(node, graph.getLabelLayout(node)[0].getBox().width + 10, graph.getLabelLayout(node)[0].getBox().height + 5);
			
			addedNodes.put(gonode, node);
			
			LinkInfo link = new LinkInfo();
			link.setAttribute(LinkInfo.HTML_REFERENCE, "/pdr/viewGONode.do?acc=" + gonode.getAccession());
			link.setAttribute(LinkInfo.HTML_ALT, gonode.getDefinition());
			link.setAttribute(LinkInfo.HTML_TITLE, gonode.getName());
			linkMap.put(node, link);	
		}
		

		// add edges to graph
		Set doneEdges = new HashSet();
		Iterator iter1 = gonodes.iterator();
		while (iter1.hasNext()) {
			GONode node1 = (GONode)iter1.next();
			if (node1 == null) continue;
			
			Set parents = node1.getParents();
			Set children = node1.getChildren();
			
			Iterator iter2 = gonodes.iterator();
			while (iter2.hasNext()) {
				GONode node2 = (GONode)iter2.next();
				if (node2 == null) continue;
				if (node1 == node2) continue;

				Set testSet = new HashSet( 2 );
				testSet.add( node1 );
				testSet.add( node2 );
				if (doneEdges.contains( testSet )) continue;
				
				doneEdges.add( testSet );
				
				if (parents!= null && parents.contains( node2 ))
					graph.createEdge( (Node)addedNodes.get( node2 ), (Node)addedNodes.get( node1 ) );
				else if (children != null && children.contains( node2 ))
					graph.createEdge( (Node)addedNodes.get( node1 ), (Node)addedNodes.get( node2 ) );
			}
		}

		HierarchicLayouter layouter = new HierarchicLayouter();
		layouter.setOrientationLayouter(new OrientationLayouter(OrientationLayouter.TOP_TO_BOTTOM));
		
		/*
		if (numChildren < 8)
			layouter.setOrientationLayouter(new OrientationLayouter(OrientationLayouter.TOP_TO_BOTTOM));
		else
			layouter.setOrientationLayouter(new OrientationLayouter(OrientationLayouter.LEFT_TO_RIGHT ) );
		*/
		
		layouter.setLayoutStyle( HierarchicLayouter.PENDULUM );
		layouter.setRoutingStyle( HierarchicLayouter.ROUTE_POLYLINE );
		
		
		layouter.doLayout(graph);	    
	}
	
	private Color getColor(GONode node) {
		
		if (node.getAspect() == GOUtils.BIOLOGICAL_PROCESS) {
			if (node.equals( seedNode ) )
				return new Color(155, 50, 50);
			
			return new Color(255, 139, 139);
		}
		
		if (node.getAspect() == GOUtils.CELLULAR_COMPONENT) {
			if (node.equals( seedNode ) )
				return new Color(50, 155, 50);
			
			return new Color(150, 255, 139);
		}
		
		if (node.getAspect() == GOUtils.MOLECULAR_FUNCTION) {
			if (node.equals( seedNode ) )
				return new Color(50, 50, 155);
			
			return new Color(155, 139, 255);
		}
		
		return new Color(169,169,169);
	}
	
	private void modifyRealize(ShapeNodeRealizer realizer, GONode node) {
		
		realizer.setFillColor( this.getColor( node ) );
		
		if (node.equals( seedNode ) ) {
			
			//realizer.setShapeType( ShapeNodeRealizer.RECT_3D );
			
			NodeLabel nl = new NodeLabel();
			nl.setFontSize(fontSize + 2);
			nl.setTextColor( new Color (255, 255, 255) );
			realizer.setLabel(nl);		
			
		} else {
			
			//realizer.setShapeType( ShapeNodeRealizer.ROUND_RECT );
			
			NodeLabel nl = new NodeLabel();
			nl.setFontSize(fontSize);
			realizer.setLabel(nl);
			
		}
	}

	private GONode seedNode;
}
