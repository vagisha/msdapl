/**
 * EnrichedGOTermsGraphCreator.java
 * @author Vagisha Sharma
 * Jun 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
 * 
 */
public class EnrichedGOTermsGraphCreator {

    private final Map<GONode, EnrichedGOTerm> enrichedTermMap;
//    private final Set<GONode> allNodes;
    
    public EnrichedGOTermsGraphCreator(List<EnrichedGOTerm> terms) {
        this.enrichedTermMap = new HashMap<GONode, EnrichedGOTerm>((int) (terms.size()*1.5));
//        this.allNodes = new HashSet<GONode>();
        for(EnrichedGOTerm term: terms) {
            GONode node = term.getGoNode();
            enrichedTermMap.put(node, term);
//            allNodes.add(node);
        }
    }
    
    public BufferedImage createGraph() throws Exception {
        
       
//        addAllNodeParents();
        
        Graph2D graph = new Graph2D();
        ShapeNodeRealizer realizer = initGraphingProperties(graph);
        
        Map<GONode, Node> addedNodes = new HashMap<GONode, Node>();
        
        for(GONode gnode: enrichedTermMap.keySet()) {
            this.modifyRealize(realizer, gnode);
            
            Node node = null;
            if (addedNodes.containsKey(gnode))
                node = (Node)(addedNodes.get(gnode));
            else {
                node = initNode(graph, gnode);
                
                addedNodes.put(gnode, node);
            }
            
            // add parents of this node
            Set<GONode> parents = gnode.getParents();
            if (parents != null) {
                Iterator<GONode> piter = parents.iterator();
                while (piter.hasNext()) {
                    GONode gparent = piter.next();
                    Node parent = null;

                    this.modifyRealize(realizer, gparent);
                    
                    if (addedNodes.containsKey(gparent))
                        parent = (Node)(addedNodes.get(gparent));
                    else {
                        parent = initNode(graph, gparent);
                    
                        addedNodes.put(gparent, parent);
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

    private Node initNode(Graph2D graph, GONode gnode) {
        Node node;
        node = graph.createNode();

        String label = gnode.toString();
        label = label.replaceAll(" ", "\n");
        
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
    
//    private void addAllNodeParents() throws Exception {
//        for(GONode node: enrichedTermMap.keySet()) {
//            allNodes.addAll(GOUtils.getAllParents(node));
//        }
//    }
    
    
    private Color getColor(GONode node) {
        
        EnrichedGOTerm enrichedTerm = enrichedTermMap.get(node);
        
        if(enrichedTerm != null) {
            return getColor(enrichedTerm.getPValue());
        }
        
        return new Color(169,169,169);
    }
    
    private Color getColor(double pValue) {
        int r = 255;
        int g = (int)pValue*255;
        int b = (int)pValue*255;
        return new Color(r,g,b);
    }
    
    private void modifyRealize(ShapeNodeRealizer realizer, GONode node) {
        
        realizer.setFillColor(this.getColor( node ));
        
        if (enrichedTermMap.get(node) != null) {
            
            //realizer.setShapeType( ShapeNodeRealizer.RECT_3D );
            NodeLabel nl = new NodeLabel();
            nl.setFontSize(14);
            nl.setTextColor( new Color (255, 255, 255) );
            realizer.setLabel(nl);      
            
        } else {
            
            //realizer.setShapeType( ShapeNodeRealizer.ROUND_RECT );
            NodeLabel nl = new NodeLabel();
            nl.setFontSize(12);
            realizer.setLabel(nl);
        }
    }
}
