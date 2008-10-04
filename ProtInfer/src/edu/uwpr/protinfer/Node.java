/**
 * Node.java
 * @author Vagisha Sharma
 * Oct 3, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer;

import java.util.ArrayList;
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
}