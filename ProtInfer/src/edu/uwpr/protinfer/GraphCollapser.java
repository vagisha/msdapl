/**
 * GraphCollapser.java
 * @author Vagisha Sharma
 * Oct 6, 2008
 * @version 1.0
 */

package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphCollapser {

    private Map<String, List<Node>> collapsibleNodesL;
    private Map<String, List<Node>> collapsibleNodesR;
    
    public GraphCollapser() {}
    
    public void collapseGraph(BipartiteGraph graph) {
        collapsibleNodesL = new HashMap<String, List<Node>>(graph.getLeftNodes().size());
        collapsibleNodesR = new HashMap<String, List<Node>>(graph.getRightNodes().size());
        sortNodes(graph);
        collapseNodes(graph);
    }

    private void sortNodes(BipartiteGraph graph) {
        List<Node> nodes = graph.getLeftNodes();
        for (Node node: nodes) {
            String nodeSign = getNodeSignature(node);
            if (collapsibleNodesL.containsKey(nodeSign)) {
                collapsibleNodesL.get(nodeSign).add(node);
            }
            else {
                List<Node> cnodes = new ArrayList<Node>();
                cnodes.add(node);
                collapsibleNodesL.put(nodeSign, cnodes);
            }
        }
        
        nodes = graph.getRightNodes();
        for (Node node: nodes) {
            String nodeSign = getNodeSignature(node);
            if (collapsibleNodesR.containsKey(nodeSign)) {
                collapsibleNodesR.get(nodeSign).add(node);
            }
            else {
                List<Node> cnodes = new ArrayList<Node>();
                cnodes.add(node);
                collapsibleNodesR.put(nodeSign, cnodes);
            }
        }
    }
    
    private void collapseNodes(Graph graph) {
        // replace each collapsed node with a single node
        Set<String> keys = collapsibleNodesL.keySet();
        for (String key: keys) {
            collapseLNodes(graph,collapsibleNodesL.get(key));
        }
        
        keys = collapsibleNodesR.keySet();
        for (String key: keys) {
            collapseRNodes(graph,collapsibleNodesR.get(key));
        }
    }

    private void collapseLNodes(Graph graph, List<Node> nodeList) {
        if (nodeList.size() < 2)
            return;
        // new label is a concatenation of labels of the nodes being collapsed
        StringBuilder buf = new StringBuilder();
        List<Node> adjNodes = nodeList.get(0).getAdjNodes();
        for (Node n: nodeList) {
            buf.append(n.getLabel());
            graph.removeNode(n); // remove old nodes
        }
        
        // add a new node
        Node newNode = new Node(buf.toString());
        for (Node adjNode: adjNodes)
            graph.addEdge(newNode, adjNode);
    }
    
    private void collapseRNodes(Graph graph, List<Node> nodeList) {
        if (nodeList.size() < 2)
            return;
        // new label is a concatenation of labels of the nodes being collapsed
        StringBuilder buf = new StringBuilder();
        List<Node> adjNodes = nodeList.get(0).getAdjNodes();
        for (Node n: nodeList) {
            buf.append(n.getLabel());
            graph.removeNode(n); // remove old nodes
        }
        
        // add a new node
        Node newNode = new Node(buf.toString());
        for (Node adjNode: adjNodes)
            graph.addEdge(adjNode, newNode);
    }


    private String getNodeSignature(Node node) {
        List<String> adjLabels = new ArrayList<String>(node.getAdjacentNodeCount());
        Iterator<Node> nodeIt = node.getAdjIterator();
        while(nodeIt.hasNext()) {
            adjLabels.add(nodeIt.next().getLabel());
        }
        StringBuilder buf = new StringBuilder();
        for (String label: adjLabels)
            buf.append(label);
        return buf.toString();
    }
}
