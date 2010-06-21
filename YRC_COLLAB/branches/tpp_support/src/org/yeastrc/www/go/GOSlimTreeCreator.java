/**
 * 
 */
package org.yeastrc.www.go;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.yeastrc.bio.go.GONode;

import y.base.Node;
import y.io.JPGIOHandler;
import y.layout.OrientationLayouter;
import y.layout.hierarchic.HierarchicLayouter;
import y.view.Arrow;
import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.Graph2DView;
import y.view.NodeLabel;
import y.view.PolyLineEdgeRealizer;
import y.view.ShapeNodeRealizer;

/**
 * GOSlimTreeCreator.java
 * @author Vagisha Sharma
 * Jun 18, 2010
 * 
 */
public class GOSlimTreeCreator {

	private final GOSlimAnalysis goSlimAnalysis;
	
	public GOSlimTreeCreator(GOSlimAnalysis goSlimAnalysis) {
		this.goSlimAnalysis = goSlimAnalysis;
	}

	public BufferedImage createGraph() throws Exception {


		Graph2D graph = new Graph2D();
		ShapeNodeRealizer realizer = initGraphingProperties(graph);

		Map<String, Node> addedNodes = new HashMap<String, Node>(); 

		for(GOSlimTerm term: goSlimAnalysis.getTermNodes()) {
			this.modifyRealize(realizer,term);

			Node node = null;
			if (addedNodes.containsKey(term.getAccession()))
				node = (Node)(addedNodes.get(term.getAccession()));
			else {
				node = initNode(graph, term.getTreeLabel());
				addedNodes.put(term.getAccession(), node);
			}

			// add parents of this node
			Set<GONode> parents = term.getGoNode().getParents();
			if (parents != null) {
				Iterator<GONode> piter = parents.iterator();
				while (piter.hasNext()) {
					GONode gparent = piter.next();
					Node parent = null;

					this.modifyRealize(realizer, null); 

					if (addedNodes.containsKey(gparent))
						parent = (Node)(addedNodes.get(gparent));
					else {
						parent = initNode(graph, gparent.toString());

						addedNodes.put(gparent.getAccession(), parent);
					}

					// Create the directed edge in the graph
					graph.createEdge(parent, node);
				}
			}
		}

		HierarchicLayouter layouter = new HierarchicLayouter();
		layouter.setOrientationLayouter(new OrientationLayouter(OrientationLayouter.TOP_TO_BOTTOM));
		layouter.setLayoutStyle(HierarchicLayouter.TREE);
		layouter.doLayout(graph);       

		JPGIOHandler jpg = new JPGIOHandler();
		jpg.setQuality((float)(7.0));
		Graph2DView view = jpg.createDefaultGraph2DView(graph);
		BufferedImage bi = (BufferedImage)(view.getImage());

		return bi;
	}

	private Node initNode(Graph2D graph, String label) {
		Node node;
		node = graph.createNode();

		graph.setLabelText(node, label);

		graph.setSize(node, graph.getLabelLayout(node)[0].getBox().width + 10, graph.getLabelLayout(node)[0].getBox().height + 5);
		return node;
	}

	private ShapeNodeRealizer initGraphingProperties(Graph2D graph) {
		
		ShapeNodeRealizer realizer = new ShapeNodeRealizer();
		realizer.setShapeType(ShapeNodeRealizer.ROUND_RECT);

		NodeLabel nl = new NodeLabel();
		nl.setFontSize(12);
		realizer.setLabel(nl); 

		graph.setDefaultNodeRealizer(realizer);

		EdgeRealizer er = new PolyLineEdgeRealizer();
		er.setArrow(Arrow.SHORT);

		graph.setDefaultNodeRealizer(realizer);
		graph.setDefaultEdgeRealizer(er);
		return realizer;
	}

	private Color getColor(GOSlimTerm term) {

		if(term != null) {
			return getColor(term.getProteinCountForTermPerc() / 100.0);
		}
		return new Color(169,169,169);
	}

	private Color getColor(double perc) {
		int r = 255;
		int g = (int)perc*255;
		int b = (int)perc*255;
		return new Color(r,g,b);
	}

	private void modifyRealize(ShapeNodeRealizer realizer, GOSlimTerm term) {

		realizer.setFillColor(getColor(term));
		if(term != null) {
			NodeLabel nl = new NodeLabel();
			nl.setFontSize(14);
			nl.setTextColor( new Color (255, 255, 255) );
			realizer.setLabel(nl); 
		}
		else {
			NodeLabel nl = new NodeLabel();
            nl.setFontSize(12);
            realizer.setLabel(nl);
		}
		
	}
}
