/**
 * Node.java
 * @author Vagisha Sharma
 * Oct 3, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node {
    
    private String label = "";
    private List<Node> adjNodes;
    
    public Node(String label) {
        this.label = label;
        this.adjNodes = new ArrayList<Node>();
    }
    public String getLabel() {
        return label;
    }
    
    public int getEdgeCount() {
        return adjNodes.size();
    }
    
    public void addAdjacentNode(Node node) {
        adjNodes.add(node);
    }
    
    public void removeAdjacentNode(Node node) {
        adjNodes.remove(node);
    }
    
    public Iterator<Node> getAdjIterator() {
        return adjNodes.iterator();
    }
    
    public List<Node> getAdjNodes() {
        return adjNodes;
    }
    
    public int getAdjacentNodeCount() {
        return adjNodes.size();
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.label);
        buf.append(" --> ");
        for (Node n: adjNodes) 
            buf.append(n.getLabel()+", ");
        if (buf.charAt(buf.length() - 1) == ',')
            buf.deleteCharAt(buf.length() - 1);
        return buf.toString();
    }
    
    public int hashCode() {
        return label.hashCode();
    }
}