package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uwpr.protinfer.assemble.idpicker.graph.InvalidNodeException;

public class GraphCollapser {

    private Map<String, List<Vertex>> leftVertexMap;
    private Map<String, List<Vertex>> rightVertexMap;
    private BipartiteGraph graph;
    
    public GraphCollapser() {}
    
    public void collapseGraph(BipartiteGraph graph) throws InvalidNodeException {
        this.graph = graph;
        leftVertexMap = new HashMap<String, List<Vertex>>(graph.getLeftVertices().size());
        rightVertexMap = new HashMap<String, List<Vertex>>(graph.getRightVertices().size());
        orderVerticesByAdjacentMembers();
        collapseVertices();
    }

    private void orderVerticesByAdjacentMembers() {
        List<Vertex> nodesL = graph.getLeftVertices();
        for (Vertex vertex: nodesL) {
            String adjSign = getVertexSignature(vertex, graph);
            if (leftVertexMap.containsKey(adjSign)) {
                leftVertexMap.get(adjSign).add(vertex);
            }
            else {
                List<Vertex> vertices = new ArrayList<Vertex>();
                vertices.add(vertex);
                leftVertexMap.put(adjSign, vertices);
            }
        }
        
        List<Vertex> nodesR = graph.getRightVertices();
        for (Vertex vertex: nodesR) {
            String nodeSign = getVertexSignature(vertex, graph);
            if (rightVertexMap.containsKey(nodeSign)) {
                rightVertexMap.get(nodeSign).add(vertex);
            }
            else {
                List<Vertex> vertices = new ArrayList<Vertex>();
                vertices.add(vertex);
                rightVertexMap.put(nodeSign, vertices);
            }
        }
    }
    
    private String getVertexSignature(Vertex v, IGraph graph) {
        StringBuilder buf = new StringBuilder();
        for (Vertex adj: graph.getAdjacentVertices(v)) {
            buf.append(adj.getLabel());
        }
        return buf.toString();
    }
    
    private void collapseVertices() throws InvalidNodeException {
        // replace each collapsed node with a single node
        Set<String> keys = leftVertexMap.keySet();
        for (String key: keys) {
            List<Vertex> toCombine = leftVertexMap.get(key);
            if (toCombine.size() > 1) {
                Vertex newVertex = toCombine.get(0).combineWith(toCombine.subList(1, toCombine.size()));
                for(Vertex v: toCombine) 
                    graph.removeLeftVertex(v);
                
                graph.addLeftVertex(newVertex);
                List<Vertex> adjV = graph.getAdjacentVertices(toCombine.get(0));
                for(Vertex adj: adjV) {
                    graph.addEdge(newVertex, adj);
                }
                
            }
        }
        
        keys = rightVertexMap.keySet();
        for (String key: keys) {
            List<Vertex> toCombine = rightVertexMap.get(key);
            if (toCombine.size() > 1) {
                Vertex newVertex = toCombine.get(0).combineWith(toCombine.subList(1, toCombine.size()));
                for(Vertex v: toCombine) 
                    graph.removeRightVertex(v);
                
                graph.addRightVertex(newVertex);
                List<Vertex> adjV = graph.getAdjacentVertices(toCombine.get(0));
                for(Vertex adj: adjV) {
                    graph.addEdge(newVertex, adj);
                }
            }
        }
    }
}
