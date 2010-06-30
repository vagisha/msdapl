/**
 * 
 */
package org.yeastrc.www.go;

import java.awt.Color;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.bio.go.GONode;

import y.base.Node;
import y.view.Arrow;
import y.view.EdgeRealizer;
import y.view.Graph2D;
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

	private final int goSlimTermId;
	private List<GONode> slimTerms;
	private final List<Integer> nrseqProteinIds;
	private final Map<String, GOSlimTerm> nodesWithAnnotations;
	private final int goAspect;
	
	private static final Logger log = Logger.getLogger(GOSlimTreeCreator.class.getName());
	
	public GOSlimTreeCreator(int goSlimTermId, List<Integer> nrseqProteinIds, int goAspect) {
		
		this.nrseqProteinIds = nrseqProteinIds;
		nodesWithAnnotations = new HashMap<String, GOSlimTerm>();
		this.goAspect = goAspect;
		this.goSlimTermId = goSlimTermId;
		
	}

	public List<GONode> getSlimTerms() throws GOException {
		
		if(slimTerms != null)
			return slimTerms;
		
		// Get the GO terms for the given GO Slim
		try {
			slimTerms = GOSlimUtils.getGOSlimTerms(goSlimTermId, goAspect);
		} catch (SQLException e) {
			throw new GOException("Error getting terms for GO Slim Term ID: "+goSlimTermId, e);
		}
		
		return slimTerms;
	}
	
	public GOTree createTree() throws Exception {
		
		log.info("Creating GOTree... for "+nrseqProteinIds.size()+" proteins");
		
		getSlimTerms();
		
		Map<String, GOTreeNode> seen = new HashMap<String, GOTreeNode>();
		Map<String, GOTreeNode> roots = new HashMap<String, GOTreeNode>();
		
		// get all annotations (exact and otherwise) for all proteins in our set
		getAllAnnotations(nrseqProteinIds, goAspect);
		
		log.info("# GO annotations found: "+nodesWithAnnotations.size());
		
		// build a tree with the annotations we have now; mark the terms that are in the GO Slim set.
		for(GONode term: slimTerms) {
			
			GOTreeNode treeNode = seen.get(term.getAccession());
			if(treeNode == null) {
				treeNode = new GOTreeNode(term);
			}
			
			// this is a GO Slim term mark it.
			treeNode.setMarked(true);
			
			GOTreeNode root = getRoot(treeNode, seen);
			if(!roots.containsKey(root.getGoNode().getAccession())) {
				roots.put(root.getGoNode().getAccession(), root);
			}
		}
		GOTree tree = new GOTree();
		for(GOTreeNode root: roots.values()) {
			if(root.getGoNode().isRoot()) { // this will get rid of the root "all" node.
				for(GOTreeNode child: root.getChildren()) {
					tree.addRoots(child);
				}
			}
			else
				tree.addRoots(root);
		}
		
		//getChildren(tree, seen);
		
		//tree.print();
		return tree;
	}
	
	private void getChildren(GOTree tree, Map<String, GOTreeNode> seenNodes) throws Exception {
		
		for(GOTreeNode node: tree.getRoots()) {
			getChildren(node, seenNodes);
		}
	}
	
	private void getChildren(GOTreeNode node, Map<String, GOTreeNode> seenNodes) throws Exception {
		
		if(!seenNodes.containsKey(node.getGoNode().getAccession())) {
			seenNodes.put(node.getGoNode().getAccession(), node);
		}
		
		if(node.isLeaf()) {
			GONode gonode = node.getGoNode();
			Set<GONode> children = gonode.getChildren();
			if(children == null)
				return;
			for(GONode child: children) {
				GOTreeNode cnode = seenNodes.get(child.getAccession());
				if(cnode == null) {
					cnode = new GOTreeNode(child);
					setAnnotations(cnode);
				}
				// add this as a child ONLY if this node has some annotations.
				if(cnode.getNumAnnotated() != 0 ) {
					node.addChild(cnode);
					// recurse ONLY if the number of annotations != number of exact annotations.
					if(cnode.getNumAnnotated() != cnode.getNumExactAnnotated()) {
						getChildren(cnode, seenNodes);
					}
				}
			}
		}
		else {
			for(GOTreeNode child: node.getChildren()) {
				getChildren(child, seenNodes);
			}
		}
	}

	private void getAllAnnotations(List<Integer> nrseqProteinIds, int goAspect) throws GOException {
		
		for(int nrseqProteinId: nrseqProteinIds) {
			
			Set<GONodeAnnotation> annotNodes = null;
			try {
				annotNodes = GoTermSearcher.getAnnotationsForProtein(nrseqProteinId, goAspect);
			} catch (SQLException e) {
				throw new GOException("Error getting terms for GO annotations for protein: "+nrseqProteinId, e);
			}
			
			for(GONodeAnnotation annot: annotNodes) {
				GOSlimTerm node = nodesWithAnnotations.get(annot.getNode().getAccession());
				
				// If we haven't seen this node yet add it to the map
				if(node == null) {
					node = new GOSlimTerm(annot.getNode(), nrseqProteinIds.size());
					nodesWithAnnotations.put(node.getAccession(), node);
				}
				
				node.addProteinIdForTerm(nrseqProteinId);
				if(annot.isExact()) {
					node.addProteinIdForExactTerm(nrseqProteinId);
				}
			}
		}
	}
	
	private GOTreeNode getRoot(GOTreeNode treeNode, Map<String, GOTreeNode> seenNodes) throws Exception {
		
		if(!seenNodes.containsKey(treeNode.getGoNode().getAccession())) {
			seenNodes.put(treeNode.getGoNode().getAccession(), treeNode);
		}
		
		setAnnotations(treeNode);
		
		Set<GONode> parents = treeNode.getGoNode().getParents();
		if(parents == null || parents.size() == 0)
			return treeNode; // this is the root
		
		GOTreeNode root = null;
		for(GONode parent: parents) {
			GOTreeNode pnode = seenNodes.get(parent.getAccession());
			if(pnode == null) {
				pnode = new GOTreeNode(parent);
			}
			pnode.addChild(treeNode);
			root = getRoot(pnode, seenNodes);
		}
		return root;
	}

	private void setAnnotations(GOTreeNode treeNode) {
		GOSlimTerm annotNode = nodesWithAnnotations.get(treeNode.getGoNode().getAccession());
		if(annotNode == null) {
			treeNode.setNumAnnotated(0);
			treeNode.setNumExactAnnotated(0);
		}
		else {
			treeNode.setNumAnnotated(annotNode.getProteinCountForTerm());
			treeNode.setNumExactAnnotated(annotNode.getProteinCountForExactTerm());
		}
	}

//	public BufferedImage createGraph() throws Exception {
//
//
//		Graph2D graph = new Graph2D();
//		ShapeNodeRealizer realizer = initGraphingProperties(graph);
//
//		Map<String, Node> addedNodes = new HashMap<String, Node>(); 
//		Set<String> edges = new HashSet<String>();
//
//		for(GOSlimTerm term: goSlimAnalysis.getTermNodes()) {
//			this.modifyRealize(realizer,term);
//
//			Node node = null;
//			if (addedNodes.containsKey(term.getAccession()))
//				node = (Node)(addedNodes.get(term.getAccession()));
//			else {
//				node = initNode(graph, term.getTreeLabel());
//				addedNodes.put(term.getAccession(), node);
//			}
//
//			// add parents of this node
//			addParents(term.getGoNode(), addedNodes, edges, realizer, graph);
//			addChildren(term.getGoNode(), addedNodes, edges, realizer, graph);
//		}
//
//		HierarchicLayouter layouter = new HierarchicLayouter();
//		layouter.setOrientationLayouter(new OrientationLayouter(OrientationLayouter.LEFT_TO_RIGHT));
//		layouter.setLayoutStyle(HierarchicLayouter.TREE);
//		layouter.doLayout(graph);       
//
//		JPGIOHandler jpg = new JPGIOHandler();
//		jpg.setQuality((float)(7.0));
//		Graph2DView view = jpg.createDefaultGraph2DView(graph);
//		BufferedImage bi = (BufferedImage)(view.getImage());
//
//		return bi;
//	}

	private void addParents(GONode child, Map<String, Node> addedNodes, Set<String> edges, 
			ShapeNodeRealizer realizer, Graph2D graph) throws Exception {
		
		Node node = addedNodes.get(child.getAccession());
		
		Set<GONode> parents = child.getParents();
		if (parents != null) {
			Iterator<GONode> piter = parents.iterator();
			while (piter.hasNext()) {
				GONode gparent = piter.next();
				Node parent = null;

				this.modifyRealize(realizer, null); 

				if (addedNodes.containsKey(gparent.getAccession()))
					parent = addedNodes.get(gparent.getAccession());
				else {
					parent = initNode(graph, gparent.toString());

					addedNodes.put(gparent.getAccession(), parent);
				}

				// Create the directed edge in the graph, if one does not already exist
				String edgeKey = gparent.getAccession()+"_"+child.getAccession();
				if(!edges.contains(edgeKey)) {
					edges.add(edgeKey);
					graph.createEdge(parent, node);
				}
				
				// recursion -- add all parents of the parent
				addParents(gparent, addedNodes,edges, realizer, graph);
			}
		}
	}
	
	private void addChildren(GONode parent, Map<String, Node> addedNodes, Set<String> edges, 
			ShapeNodeRealizer realizer, Graph2D graph) throws Exception {
		
		Node node = addedNodes.get(parent.getAccession());
		
		Set<GONode> children = parent.getChildren();
		if (children != null) {
			
			System.out.println("Adding children for: "+parent.getAccession()+" found "+children.size()+" children");
			Iterator<GONode> piter = children.iterator();
			while (piter.hasNext()) {
				GONode gchild = piter.next();
				Node child = null;

				this.modifyRealize(realizer, null); 

				if (addedNodes.containsKey(gchild.getAccession()))
					child = addedNodes.get(gchild.getAccession());
				else {
					child = initNode(graph, gchild.toString());

					addedNodes.put(gchild.getAccession(), child);
					System.out.println("\t Adding child: "+gchild.getAccession());
				}

				// Create the directed edge in the graph, if one does not already exist
				String edgeKey = parent.getAccession()+"_"+gchild.getAccession();
				if(!edges.contains(edgeKey)) {
					System.out.println("\t Adding child edge: "+gchild.getAccession());
					edges.add(edgeKey);
					graph.createEdge(node, child);
				}
				
				// recursion -- add all parents of the parent
				//addChildren(gchild, addedNodes,edges, realizer, graph);
			}
		}
		
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
