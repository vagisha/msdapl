package edu.uwpr.protinfer.infer.graph;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class ConnectedComponentFinder {

    
    private int componentIndex = 0;
    
    public int findAllConnectedComponents(IGraph graph) {
        componentIndex = 0;
        
        List<Vertex> vertices = graph.getVertices();
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            if (vertex.isVisited()) {
                continue;
            }
            else {
                dfs(graph, vertex); // this will give us one component
            }
            componentIndex++;
        }
        return componentIndex;
    }
    
    public void printConnectedComponents(IGraph graph) {
        List<Vertex> allVertices = graph.getVertices();
        Collections.sort(allVertices, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                return new Integer(o1.getComponentIndex()).compareTo(new Integer(o2.getComponentIndex()));
            }});
        int currComponent = -1;
        for (Vertex vertex: allVertices) {
            int idx = vertex.getComponentIndex();
            if (idx != currComponent) {
                currComponent = idx;
                System.out.println("COMPONENT: "+currComponent);
            }
            System.out.print(vertex.getLabel()+" --> ");
            List<Vertex> adjVertices = graph.getAdjacentVertices(vertex);
            for (Vertex adj: adjVertices) {
                System.out.print(adj.getLabel()+", ");
            }
            System.out.println();
        }
    }
    
    
    private void dfs(IGraph graph, Vertex root) {
        Stack<Vertex> stack = new Stack<Vertex>();
        stack.push(root);
        visitVertex(root);
        
        while (!stack.isEmpty()) {
            Vertex vertex = stack.pop();
            List<Vertex> adjVertices = graph.getAdjacentVertices(vertex);
            for (Vertex child: adjVertices) {
                if (child.isVisited())
                    continue;
                stack.push(child);
                visitVertex(child);
            }
        }
    }
    
    
    private void visitVertex(Vertex vertex) {
        vertex.setVisited(true);
        vertex.setComponentIndex(componentIndex);
    }
}
