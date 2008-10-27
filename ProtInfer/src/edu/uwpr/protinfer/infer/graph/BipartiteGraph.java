package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BipartiteGraph implements IGraph {
    
    private Map<String, Vertex> verticesL;
    private Map<String, Vertex> verticesR;
    
    private Map<String, List<Vertex>> adjList;
    
    private Set<Edge> edges;
    
    public BipartiteGraph() {
        verticesL = new HashMap<String, Vertex>();
        verticesR = new HashMap<String, Vertex>();
        adjList = new HashMap<String, List<Vertex>>();
        edges = new HashSet<Edge>();
    }
    
    /**
     * Adds a vertex to one set of vertices of the bipartite graph.
     */
    public Vertex addVertex(Vertex vertex) {
       return addLeftVertex(vertex);
    }
    
    public Vertex addLeftVertex(Vertex vertex) {
        Vertex v = verticesL.get(vertex.getLabel());
        if (v != null)
            return v;
        verticesL.put(vertex.getLabel(), v);
        return vertex;
    }
    
    public Vertex addRightVertex(Vertex vertex) {
        Vertex v = verticesR.get(vertex.getLabel());
        if (v != null)
            return v;
        verticesR.put(vertex.getLabel(), v);
        return vertex;
    }
    
    public void addEdge(Vertex v1, Vertex v2) {
       v1 = addLeftVertex(v1);
       v2 = addRightVertex(v2);
       
       if (edges.contains(new Edge(v1, v2)))
           return;
       
       edges.add(new Edge(v1, v2));
       addGraphEdge(v1, v2);
       addGraphEdge(v2, v1);
       
    }
    
    private void addGraphEdge(Vertex from, Vertex to) {
        List<Vertex> adjVertices = adjList.get(from.getLabel());
        if (adjVertices == null) {
            adjVertices = new ArrayList<Vertex>();
            adjList.put(from.getLabel(), adjVertices);
        }
        adjVertices.add(to);
    }
    
    public int getEdgeCount() {
        return edges.size();
    }
    
    public List<Vertex> getAdjacentVertices(Vertex vertex) {
        return adjList.get(vertex.getLabel());
    }
    
    public boolean containsEdge(Vertex v1, Vertex v2) {
        return edges.contains(new Edge(v1, v2));
    }
    
    public List<Vertex> getVertices() {
        List<Vertex> vList = new ArrayList<Vertex>(verticesL.size());
        vList.addAll(verticesL.values());
        vList.addAll(verticesR.values());
        return vList;
    }
    
    public List<Vertex> getLeftVertices() {
        List<Vertex> vList = new ArrayList<Vertex>(verticesL.size());
        vList.addAll(verticesL.values());
        return vList;
    }
    
    public List<Vertex> getRightVertices() {
        List<Vertex> vList = new ArrayList<Vertex>(verticesL.size());
        vList.addAll(verticesR.values());
        return vList;
    }

    @Override
    public void combineVertices(Vertex v1, Vertex v2) throws InvalidVertexException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean removeVertex(Vertex v) {
        if (!vertices.containsKey(v.getLabel()))
            return;
        List<Vertex> adj = adjList.get(v.getLabel());
        for(Vertex av: adj) {
            edges.remove(new Edge(v, av));
        }
        adjList.remove(v.getLabel());
        vertices.remove(v);
    }
    
    public 
}
