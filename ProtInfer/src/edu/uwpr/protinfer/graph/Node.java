/**
 * Node.java
 * @author Vagisha Sharma
 * Oct 3, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.graph;


public class Node {
    
    private String label = "";
    private int componentIndex = -1;
    
    public Node(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public void setComponentIndex(int index) {
        this.componentIndex = index;
    }
    
    public int getComponentIndex() {
        return this.componentIndex;
    }
    
    public String getLongLabel() {
        return getLabel();
    }
    
    public String toString() {
        return getLabel();
    }
}