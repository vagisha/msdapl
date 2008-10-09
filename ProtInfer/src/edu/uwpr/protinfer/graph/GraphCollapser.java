/**
 * GraphCollapser.java
 * @author Vagisha Sharma
 * Oct 6, 2008
 * @version 1.0
 */

package edu.uwpr.protinfer.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphCollapser <L extends Node, R extends Node>{

    private Map<String, List<L>> collapsibleNodesL;
    private Map<String, List<R>> collapsibleNodesR;
    
    public GraphCollapser() {}
    
    public void collapseGraph(IBipartiteGraph<L, R> graph) throws InvalidNodeException {
        collapsibleNodesL = new HashMap<String, List<L>>(graph.getLeftNodes().size());
        collapsibleNodesR = new HashMap<String, List<R>>(graph.getRightNodes().size());
        sortNodes(graph);
//        System.out.println(collapsibleNodesL);
//        System.out.println(collapsibleNodesR);
        collapseNodes(graph);
    }

    private void sortNodes(IBipartiteGraph<L, R> graph) {
        List<L> nodesL = graph.getLeftNodes();
        for (L node: nodesL) {
            String nodeSign = graph.getNodeSignature(node);
            if (collapsibleNodesL.containsKey(nodeSign)) {
                collapsibleNodesL.get(nodeSign).add(node);
            }
            else {
                List<L> cnodes = new ArrayList<L>();
                cnodes.add(node);
                collapsibleNodesL.put(nodeSign, cnodes);
            }
        }
        
        List<R> nodesR = graph.getRightNodes();
        for (R node: nodesR) {
            String nodeSign = graph.getNodeSignature(node);
            if (collapsibleNodesR.containsKey(nodeSign)) {
                collapsibleNodesR.get(nodeSign).add(node);
            }
            else {
                List<R> cnodes = new ArrayList<R>();
                cnodes.add(node);
                collapsibleNodesR.put(nodeSign, cnodes);
            }
        }
    }
    
    private void collapseNodes(IBipartiteGraph<L, R> graph) throws InvalidNodeException {
        // replace each collapsed node with a single node
        Set<String> keys = collapsibleNodesL.keySet();
        for (String key: keys) {
            graph.collapseLeftNodes(collapsibleNodesL.get(key));
        }
        
        keys = collapsibleNodesR.keySet();
        for (String key: keys) {
            graph.collapseRightNodes(collapsibleNodesR.get(key));
        }
    }
}
