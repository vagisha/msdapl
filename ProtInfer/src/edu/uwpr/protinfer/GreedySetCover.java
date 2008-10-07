/**
 * GreedySetCover.java
 * @author Vagisha Sharma
 * Oct 6, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 
 */
public class GreedySetCover {
    
    public GreedySetCover() {}
    
    public List<Node> getGreedySetCover(BipartiteGraph graph) {
        List<Node> leftNodes = graph.getLeftNodes();
        PriorityQueue<Node> queue = new PriorityQueue<Node>(leftNodes.size(), new NodeComparator());
        for (Node node: leftNodes) 
            queue.add(node);
        
        List<Node> setCover = new ArrayList<Node>();
        
        while (!queue.isEmpty()) {
            // take a look at the node with the max. outgoing edges
            Node n = queue.peek();
            
            // if there are no outgoing edges it means we already have a set cover.
            if (n.getAdjacentNodeCount() == 0)
                break;
            
            // add this node to the set cover
            setCover.add(n); 
            // remove all nodes adjacent to this node from the graph
            // make a copy to avoid ConcurrentModificationException
            List<Node> adjNodes = new ArrayList<Node>(n.getAdjacentNodeCount());
            adjNodes.addAll(n.getAdjNodes());
            for (Node adjn: adjNodes) {
                graph.removeNode(adjn);
            }
            
            // now remove this node from the queue; this will re-adjust the queue so that the 
            // node with max outgoing edges is again at the top.
            queue.remove();
        }
        return setCover;
    }
    
    private static final class NodeComparator implements Comparator<Node> {

        @Override
        public int compare(Node node1, Node node2) {
            if (node1.getAdjacentNodeCount() > node2.getAdjacentNodeCount())
                return -1;
            else if (node1.getAdjacentNodeCount() < node2.getAdjacentNodeCount())
                return 1;
            else
                return 0;
        }
        
    }
}
