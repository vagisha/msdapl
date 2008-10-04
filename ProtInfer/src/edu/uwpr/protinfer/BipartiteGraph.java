package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BipartiteGraph implements Graph{

    private Map<Node, Integer> leftNodes;
    private Map<Node, Integer> rightNodes;
    
    public BipartiteGraph () {
        leftNodes = new HashMap<Node, Integer>();
        rightNodes = new HashMap<Node, Integer>();
    }
    
    public void addEdge(Node from, Node to) {
        addLeftNode(from);
        addRightNode(to);
        from.addAdjacentNode(to);
    }
    
    public List<Node> getNodes() {
        List<Node> allNodes = new ArrayList<Node>(leftNodes.size() + rightNodes.size());
        allNodes.addAll(leftNodes.keySet());
        allNodes.addAll(rightNodes.keySet());
        return allNodes;
    }
    
    void addLeftNode(Node node) {
        
        if (leftNodes.get(node) != null)
            return;
        
        leftNodes.put(node, leftNodes.size());
    }
    
    void addRightNode(Node node) {
        if (rightNodes.get(node) != null)
            return;
        rightNodes.put(node, rightNodes.size());
    }
    
    void printGraph() {
        for (Node n: leftNodes.keySet()) 
            System.out.println(n.toString());
    }
    
    
    public static void main(String[] args) {
        BipartiteGraph graph = new BipartiteGraph();
        String[] leftLabels = new String[] {"A", "B", "C", "D", "E"};
        String[] rightLabels = new String[] {"1", "2", "3", "4", "5", "6"};
        
        for (int i = 0; i < leftLabels.length; i++) {
            graph.addEdge(new Node(leftLabels[i]), new Node(rightLabels[i]));
            graph.addEdge(new Node(leftLabels[i]), new Node(rightLabels[i+1]));
        }
        
        graph.printGraph();
    }
}

