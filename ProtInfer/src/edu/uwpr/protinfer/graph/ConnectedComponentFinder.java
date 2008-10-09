/**
 * ConnectedComponentFinder.java
 * @author Vagisha Sharma
 * Oct 6, 2008
 * @version 1.0
 */

package edu.uwpr.protinfer.graph;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import edu.uwpr.protinfer.PeptideNode;
import edu.uwpr.protinfer.ProteinNode;


public class ConnectedComponentFinder {

    private IBipartiteGraph<? extends Node, ? extends Node> graph;
    private int componentIndex = 0;
    
    public  ConnectedComponentFinder() {
        graph = null;
        componentIndex = 0;
    }
    
    public int findAllConnectedComponents(IBipartiteGraph<? extends Node, ? extends Node> graph) {
        this.graph = graph;
        List<Node> nodes = graph.getAllNodes();
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (isNodeVisited(node)) {
                continue;
            }
            else {
                dfs(node); // this will give us one component
            }
            componentIndex++;
        }
        return componentIndex;
    }
    
    public void printConnectedComponents(IBipartiteGraph<? extends Node, ? extends Node> graph) {
        List<Node> allNodes = graph.getAllNodes();
        Collections.sort(allNodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return new Integer(o1.getComponentIndex()).compareTo(new Integer(o2.getComponentIndex()));
            }});
        int currComponent = -1;
        for (Node node: allNodes) {
            int idx = node.getComponentIndex();
            if (idx != currComponent) {
                currComponent = idx;
                System.out.println("COMPONENT: "+currComponent);
            }
            System.out.print(node.getLabel()+" --> ");
            List<Node> adjNodes = graph.getAdjacentNodes(node);
            for (Node pn: adjNodes) {
                System.out.print(pn.getLabel()+", ");
            }
            System.out.println();
        }
    }
    
    
    private void dfs(Node root) {
        Stack<Node> stack = new Stack<Node>();
        stack.push(root);
        visitNode(root);
        
        while (!stack.isEmpty()) {
            Node node = stack.pop();
            List<Node> adjNodes = graph.getAdjacentNodes(node);
            for (Node child: adjNodes) {
                if (isNodeVisited(child))
                    continue;
                stack.push(child);
                visitNode(child);
            }
        }
    }
    
    private boolean isNodeVisited(Node node) {
        return node.getComponentIndex() > -1;
    }
    
    private void visitNode(Node node) {
        node.setComponentIndex(componentIndex);
    }
}
