/**
 * TestApp.java
 * @author Vagisha Sharma
 * Oct 3, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer;

import java.util.List;

public class TestApp {

    public static void main(String[] args) {
        BipartiteGraph graph = new BipartiteGraph();
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
        
        ConnectedComponentFinder compFinder = new ConnectedComponentFinder(graph);
        int count = 0;
        while(compFinder.hasComponent()) {
            Graph component = compFinder.getNextComponent();
            if (component == null) {
                System.out.println("Null component found!");
                break;
            }
            printComponent(component);
            count++;
        }
        System.out.println("Number of components found: "+count);
        
        GraphCollapser collapser = new GraphCollapser();
        collapser.collapseGraph(graph);
        System.out.println("AFTER COLLAPSING");
        graph.printGraph();
        
        GreedySetCover setCoverFinder = new GreedySetCover();
        List<Node> setCover = setCoverFinder.getGreedySetCover(graph);
        printSetCover(setCover);
    }

    private static void printSetCover(List<Node> setCover) {
        StringBuilder buf = new StringBuilder();
        for (Node n: setCover)
            buf.append(n.getLabel()+", ");
        System.out.println("SET COVER: "+buf.toString());
    }

    private static void printComponent(Graph component) {
        List<Node> nodes = component.getNodes();
        StringBuilder buf = new StringBuilder();
        for (Node node: nodes)
            buf.append(node.getLabel()+", ");
        System.out.println(buf.toString());
    }
}
