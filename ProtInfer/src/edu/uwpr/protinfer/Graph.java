/**
 * Graph.java
 * @author Vagisha Sharma
 * Oct 3, 2008
 * @version 1.0
 */

package edu.uwpr.protinfer;

import java.util.List;

public interface Graph {

    public abstract void addEdge(Node src, Node dest);
    
    public abstract void removeNode(Node node);
    
    public abstract List<Node> getNodes();
}
