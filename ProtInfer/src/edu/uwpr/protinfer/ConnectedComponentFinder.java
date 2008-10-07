/**
 * ConnectedComponentFinder.java
 * @author Vagisha Sharma
 * Oct 6, 2008
 * @version 1.0
 */

package edu.uwpr.protinfer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class ConnectedComponentFinder {

    private final List<Node> allNodes;
    private Set<Node> visitedNodes;
    private List<Node> component;
    private int currentIndex;
    
    public ConnectedComponentFinder(Graph graph) {
        this.allNodes = graph.getNodes();
        visitedNodes = new HashSet<Node>();
        component = new ArrayList<Node>();
        currentIndex = 0;
    }
    
    public boolean hasComponent() {
        return visitedNodes.size() < allNodes.size();
    }
    
    public Graph getNextComponent() {
        
        component.clear(); // clear the last component
        Node n = null;
        for (; currentIndex < allNodes.size(); currentIndex++) {
            Node temp = allNodes.get(currentIndex);
            if (isNodeVisited(temp))
                continue;
            n = temp;
            break;
        }
        
        if (n == null) {
            System.out.println("No un-visited vertex found!");
            return null;
        }
        
        dfsNonRecursive(n);
        
        return new Graph() {

            public void addEdge(Node src, Node dest) {
                throw new UnsupportedOperationException();
            }

            public List<Node> getNodes() {
                return component;
            }

            @Override
            public void removeNode(Node node) {
                throw new UnsupportedOperationException();
            }};
    }
    
    private void dfsRecursive (Node n) {
        visitNode(n);
        Iterator<Node> nodeIt = n.getAdjIterator();
        while (nodeIt.hasNext()) {
            Node child = nodeIt.next();
            if (isNodeVisited(child))
                continue;
            dfsRecursive(child);
        }
    }
    
    private void dfsNonRecursive(Node root) {
        Stack<Node> stack = new Stack<Node>();
        stack.push(root);
        visitNode(root);
        
        while (!stack.isEmpty()) {
            Node node = stack.pop();
            Iterator<Node> nodeIt = node.getAdjIterator();
            while (nodeIt.hasNext()) {
                Node child = nodeIt.next();
                if (isNodeVisited(child))
                    continue;
                stack.push(child);
                visitNode(child);
            }
        }
    }
    
    private boolean isNodeVisited(Node node) {
        return visitedNodes.contains(node);
    }
    
    private void visitNode(Node node) {
        visitedNodes.add(node);
        component.add(node);
    }
}
