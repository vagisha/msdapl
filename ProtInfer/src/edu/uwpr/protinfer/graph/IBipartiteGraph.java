/**
 * Graph.java
 * @author Vagisha Sharma
 * Oct 3, 2008
 * @version 1.0
 */

package edu.uwpr.protinfer.graph;

import java.util.List;


public interface IBipartiteGraph <L extends Node, R extends Node>{

    public abstract void addEdge(L src, R dest);
    
//    public abstract void removeNode(Node node);
    
    public abstract void removeLeftNode(L node);
    
    public abstract void removeRightNode(R node);
    
    public abstract List<Node> getAllNodes();
    
    public abstract List<L> getLeftNodes();
    
    public abstract List<R> getRightNodes();
    
    public abstract List<Node> getAdjacentNodes(Node node);
    
    public abstract List<L> getAdjacentNodesR(R node);
    
    public abstract List<R> getAdjacentNodesL(L node);
    
    public abstract void collapseLeftNodes(List<L> nodes) throws InvalidNodeException;
    
    public abstract void collapseRightNodes(List<R> nodes) throws InvalidNodeException;
    
    public abstract String getNodeSignature(Node node);
}
