/**
 * BipartiteGraph.java
 * @author Vagisha Sharma
 * Oct 3, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BipartiteGraph implements Graph{

    private Map<String, Node> leftNodes;
    private Map<String, Node> rightNodes;
    
    public BipartiteGraph () {
        leftNodes = new HashMap<String, Node>();
        rightNodes = new HashMap<String, Node>();
    }
    
    public void addEdge(Node from, Node to) {
        Node l = addLeftNode(from);
        Node r = addRightNode(to);
        l.addAdjacentNode(r);
        r.addAdjacentNode(l);
    }
    
    public void removeNode(Node node) {
       if (leftNodes.containsKey(node.getLabel())) {
           Iterator<Node> nodeIt = node.getAdjIterator();
           while(nodeIt.hasNext()) {
               nodeIt.next().removeAdjacentNode(node);
           }
           leftNodes.remove(node.getLabel());
       }
       else if (rightNodes.containsKey(node.getLabel())) {
           Iterator<Node> nodeIt = node.getAdjIterator();
           while(nodeIt.hasNext()) {
               nodeIt.next().removeAdjacentNode(node);
           }
           rightNodes.remove(node.getLabel());
       }
       else 
           System.out.println("Graph does not contain node: "+node.getLabel());
    }
    
    public List<Node> getNodes() {
        return getAllNodes();
    }
    
    public List<Node> getAllNodes() {
        List<Node> allNodes = new ArrayList<Node>(leftNodes.size() + rightNodes.size());
        allNodes.addAll(leftNodes.values());
        allNodes.addAll(rightNodes.values());
        return allNodes;
    }
    
    public List<Node> getLeftNodes() {
        List<Node> allNodes = new ArrayList<Node>(leftNodes.size());
        allNodes.addAll(leftNodes.values());
        return allNodes;
    }
    
    public List<Node> getRightNodes() {
        List<Node> allNodes = new ArrayList<Node>(rightNodes.size());
        allNodes.addAll(rightNodes.values());
        return allNodes;
    }
    
    Node addLeftNode(Node node) {
        Node l = leftNodes.get(node.getLabel());
        if (l != null)
            return l;
        
        leftNodes.put(node.getLabel(), node);
        return node;
    }
    
    Node addRightNode(Node node) {
        Node r = rightNodes.get(node.getLabel());
        if (r != null)
            return r;
        rightNodes.put(node.getLabel(), node);
        return node;
    }
    
    void printGraph() {
        System.out.println("Left Nodes: "+leftNodes.size());
        System.out.println("Right Nodes: "+rightNodes.size());
        for (Node n: leftNodes.values()) 
            System.out.println(n.toString());
    }
}

