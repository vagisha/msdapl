/**
 * BipartiteGraph.java
 * @author Vagisha Sharma
 * Oct 3, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BipartiteGraph <L extends Node, R extends Node> implements IBipartiteGraph<L, R> {

    private Map<String, L> leftNodes;
    private Map<String, R> rightNodes;
    
    private Map<String, List<R>> l2rEdges; // String is label of L node
    private Map<String, List<L>> r2lEdges; // String is label of R node
    
    private final NodeCombiner<L> lcombiner;
    private final NodeCombiner<R> rcombiner;
    
    public BipartiteGraph (NodeCombiner<L> lcombiner, NodeCombiner<R> rcombiner) {
        leftNodes = new HashMap<String, L>();
        rightNodes = new HashMap<String, R>();
        l2rEdges = new HashMap<String, List<R>>();
        r2lEdges = new HashMap<String, List<L>>();
        this.lcombiner = lcombiner;
        this.rcombiner = rcombiner;
    }
    
    public void addEdge(L from, R to) {
        L l = addLeftNode(from);
        R r = addRightNode(to);
        
        // add edge going from L to R
        List<R> l2r_e = l2rEdges.get(l.getLabel());
        if (l2r_e == null)
            l2r_e = new ArrayList<R>();
        l2r_e.add(r);
        l2rEdges.put(l.getLabel(), l2r_e);
        
        // add edge going from R to L
        List<L> r2l_e = r2lEdges.get(r.getLabel());
        if (r2l_e == null)
            r2l_e = new ArrayList<L>();
        r2l_e.add(l);
        r2lEdges.put(r.getLabel(), r2l_e);
    }
    
    public void removeNode(Node node) {
        if (leftNodes.containsKey(node.getLabel())) {
            removeLeftNode((L) node);
        }
        else if (rightNodes.containsKey(node.getLabel())) {
            removeRightNode((R) node);
        } 
        else 
            System.out.println("Graph does not contain node: "+node.getLabel());
    }
    
    public void removeLeftNode(L node) {
        if (leftNodes.containsKey(node.getLabel())) {
            removeEdgesWithL(node);
            leftNodes.remove(node.getLabel());
        }
    }
    
    public void removeRightNode(R node) {
        if (rightNodes.containsKey(node.getLabel())) {
            removeEdgesWithR(node);
            rightNodes.remove(node.getLabel());
        } 
    }
    
    private void removeEdgesWithR(R node) {
        List<L> adjNodes = r2lEdges.get(node.getLabel());
        for (L adj: adjNodes) {
            l2rEdges.get(adj.getLabel()).remove(node);
        }
        r2lEdges.remove(node.getLabel());
    }
    
    private void removeEdgesWithL(L node) {
        List<R> adjNodes = l2rEdges.get(node.getLabel());
        for (R adj: adjNodes) {
            r2lEdges.get(adj.getLabel()).remove(node);
        }
        l2rEdges.remove(node.getLabel());
    }
    
    public List<Node> getAllNodes() {
        List<Node> allNodes = new ArrayList<Node>(leftNodes.size() + rightNodes.size());
        allNodes.addAll(leftNodes.values());
        allNodes.addAll(rightNodes.values());
        return allNodes;
    }
    
    public List<L> getLeftNodes() {
        List<L> allNodes = new ArrayList<L>(leftNodes.size());
        allNodes.addAll(leftNodes.values());
        return allNodes;
    }
    
    public List<R> getRightNodes() {
        List<R> allNodes = new ArrayList<R>(rightNodes.size());
        allNodes.addAll(rightNodes.values());
        return allNodes;
    }
    
    L addLeftNode(L node) {
        L l = leftNodes.get(node.getLabel());
        if (l != null)
            return l;
        
        leftNodes.put(node.getLabel(), node);
        return node;
    }
    
    R addRightNode(R node) {
        R r = rightNodes.get(node.getLabel());
        if (r != null)
            return r;
        rightNodes.put(node.getLabel(), node);
        return node;
    }
    
    public void printGraph() {
        System.out.println("Left Nodes: "+leftNodes.size());
        System.out.println("Right Nodes: "+rightNodes.size());
        for (L n: leftNodes.values()) {
            System.out.print(n.getLabel()+" --> ");
            List<R> adjNodes = getAdjacentNodesL(n);
            for (R adj: adjNodes) 
                System.out.print(adj.getLabel()+", ");
            System.out.println();
        }
    }

    
    @Override
    public void collapseLeftNodes(List<L> nodes) throws InvalidNodeException {
        if (nodes.size() < 2)
            return;
        L[] nodeArr = (L[]) new Node[nodes.size()];
        L newNode = lcombiner.combineNodes(nodes.toArray(nodeArr));
        L oldNode = nodes.get(0);
        // make a copy and iterate over it to avoid ConcurrentModificationException
        List<R> adjNodes = new ArrayList<R>(l2rEdges.get(oldNode.getLabel()));
        for (R adj: adjNodes) {
            addEdge(newNode, adj);
        }
        for (L ln: nodes) {
            removeLeftNode(ln);
        }
    }

    @Override
    public void collapseRightNodes(List<R> nodes) throws InvalidNodeException {
        if (nodes.size() < 2)
            return;
        R[] nodeArr = (R[]) new Node[nodes.size()];
        R newNode = rcombiner.combineNodes(nodes.toArray(nodeArr));
        R oldNode = nodes.get(0);
        // make a copy and iterate over it to avoid ConcurrentModificationException
        List<L> adjNodes = new ArrayList<L>(r2lEdges.get(oldNode.getLabel()));
        for (L adj: adjNodes) {
            addEdge(adj, newNode);
        }
        for (R rn: nodes) {
            removeRightNode(rn);
        }
    }

    @Override
    public List<R> getAdjacentNodesL(L node) {
        return l2rEdges.get(node.getLabel());
    }

    @Override
    public List<L> getAdjacentNodesR(R node) {
        return r2lEdges.get(node.getLabel());
    }

    @Override
    public List<Node> getAdjacentNodes(Node node) {
        L ln = leftNodes.get(node.getLabel());
        if (ln != null)
            return (List<Node>) getAdjacentNodesL(ln);
        
        R rn = rightNodes.get(node.getLabel());
        if (rn != null)
            return (List<Node>) getAdjacentNodesR(rn);
        
        System.out.println("Node not found in graph: "+node.getLabel());
        return null;
    }

    @Override
    public String getNodeSignature(Node node) {
        List<Node> adjNodes = getAdjacentNodes(node);
        StringBuilder buf = new StringBuilder();
        for (Node n: adjNodes) {
            buf.append(","+n.getLabel());
        }
        if (buf.length() > 0)
            buf.deleteCharAt(0); // remove first comma
        return buf.toString();
    }
}

