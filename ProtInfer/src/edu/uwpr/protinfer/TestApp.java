/**
 * TestApp.java
 * @author Vagisha Sharma
 * Oct 3, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer;

import java.util.List;

import edu.uwpr.protinfer.graph.BipartiteGraph;
import edu.uwpr.protinfer.graph.ConnectedComponentFinder;
import edu.uwpr.protinfer.graph.GraphCollapser;
import edu.uwpr.protinfer.graph.GreedySetCover;
import edu.uwpr.protinfer.graph.InvalidNodeException;
import edu.uwpr.protinfer.graph.Node;
import edu.uwpr.protinfer.graph.NodeCombiner;

public class TestApp {

    public static void main(String[] args) throws InvalidNodeException {
        NodeCombiner<Node> nodeCombiner = new NodeCombiner<Node>(){
            @Override
            public Node combineNodes(List <Node> nodes) throws InvalidNodeException {
                if (nodes.size() == 0) {
                    throw new InvalidNodeException("");
                }
                StringBuilder buf = new StringBuilder();
                for (Node n: nodes) {
                    buf.append("_"+n.getLabel());
                }
                buf.deleteCharAt(0);
                Node newNode = new Node(buf.toString());
               return newNode;
            }};
            
        BipartiteGraph<Node, Node> graph = new BipartiteGraph<Node, Node>(nodeCombiner, nodeCombiner);
        String[] leftLabels = new String[] {"A", "B", "C", "D", "E"};
        String[] rightLabels = new String[] {"1", "2", "3", "4", "5", "6"};
        
        for (int i = 0; i < leftLabels.length; i++) {
            graph.addEdge(new Node(leftLabels[i]), new Node(rightLabels[i]));
            graph.addEdge(new Node(leftLabels[i]), new Node(rightLabels[i+1]));
        }
        
        leftLabels = new String[] {"a", "b", "c", "d", "e"};
        rightLabels = new String[] {"10", "20", "30", "40", "50", "60"};
        
        for (int i = 0; i < leftLabels.length; i++) {
            graph.addEdge(new Node(leftLabels[i]), new Node(rightLabels[i]));
            graph.addEdge(new Node(leftLabels[i]), new Node(rightLabels[i+1]));
        }
        
        leftLabels = new String[] {"X", "Y", "Z", "ZZ"};
        rightLabels = new String[] {"x", "y", "z"};
        String[] uniqueLabels = new String[]{"xx", "yy", "zz"};
        
        for (int i = 0; i < leftLabels.length; i++) {
            graph.addEdge(new Node(leftLabels[i]), new Node(rightLabels[0]));
            graph.addEdge(new Node(leftLabels[i]), new Node(rightLabels[1]));
            graph.addEdge(new Node(leftLabels[i]), new Node(rightLabels[2]));
            graph.addEdge(new Node(leftLabels[i]), new Node(uniqueLabels[i>= uniqueLabels.length ? uniqueLabels.length -1 : i]));
        }
        graph.addEdge(new Node(leftLabels[0]), new Node("max"));
        graph.addEdge(new Node(leftLabels[1]), new Node("max"));
        
        graph.printGraph();
        
        // collapse the graph
        GraphCollapser<Node, Node> collapser = new GraphCollapser<Node, Node>();
        collapser.collapseGraph(graph);
        
        System.out.println("AFTER COLLAPSING");
        graph.printGraph();
        
        // get connected components;
        ConnectedComponentFinder compFinder = new ConnectedComponentFinder();
        int components = compFinder.findAllConnectedComponents(graph);
        System.out.println("Found "+components+" connected components");
        compFinder.printConnectedComponents(graph);
        
        // get the set cover
        GreedySetCover<Node, Node> setCoverFinder = new GreedySetCover<Node, Node>();
        List<Node> setCover = setCoverFinder.getGreedySetCover(graph);
        printSetCover(setCover);
    }

    private static void printSetCover(List<Node> setCover) {
        StringBuilder buf = new StringBuilder();
        for (Node n: setCover)
            buf.append(n.getLabel()+", ");
        System.out.println("SET COVER: "+buf.toString());
    }
}
