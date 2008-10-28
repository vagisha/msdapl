package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph implements IGraph {

    private Map<String, Vertex> vertices;
    private Map<String, Set<Vertex>> adjList;
    
    public Graph() {
        vertices = new HashMap<String, Vertex>();
        adjList = new HashMap<String, Set<Vertex>>();
    }
    
    public Vertex addVertex(Vertex vertex) {
        Vertex v = vertices.get(vertex.getLabel());
        if (v != null)
            return v;
        vertices.put(vertex.getLabel(), vertex);
        return vertex;
    }
    
    public void addEdge(Vertex v1, Vertex v2) {
       v1 = addVertex(v1);
       v2 = addVertex(v2);
       
       addGraphEdge(v1, v2);
       addGraphEdge(v2, v1);
       
    }
    
    private void addGraphEdge(Vertex from, Vertex to) {
        Set<Vertex> adjVertices = adjList.get(from.getLabel());
        if (adjVertices == null) {
            adjVertices = new HashSet<Vertex>();
            adjList.put(from.getLabel(), adjVertices);
        }
        adjVertices.add(to);
    }
    
    public int getEdgeCount() {
        int count = 0;
        for (Set<Vertex> adjV: adjList.values())
            count += adjV.size();
        return count/2;
    }
    
    public List<Vertex> getAdjacentVertices(Vertex vertex) {
        List<Vertex> list = new ArrayList<Vertex>(adjList.get(vertex.getLabel()).size());
        list.addAll(adjList.get(vertex.getLabel()));
        return list;
    }
    
    public boolean containsEdge(Vertex v1, Vertex v2) {
        return adjList.get(v1.getLabel()).contains(v2);
    }
    
    public List<Vertex> getVertices() {
        List<Vertex> vList = new ArrayList<Vertex>(vertices.size());
        vList.addAll(vertices.values());
        return vList;
    }
    
    public void combineVertices(Vertex v1, Vertex v2) throws InvalidVertexException {
        Vertex v1_o = vertices.get(v1.getLabel());
        if (v1_o == null) {
            throw new InvalidVertexException("Vertex "+v1.getLabel()+" not found in graph");
        }
        Vertex v2_o = vertices.get(v2.getLabel());
        if (v2_o == null) {
            throw new InvalidVertexException("Vertex "+v2.getLabel()+" not found in graph");
        }
        
        Vertex v_combined = v1_o.combineWith(v2_o);
        Set<Vertex> adjV = getCommonAdjVertices(v1_o, v2_o);
        
        for (Vertex av: adjV) {
            addEdge(v_combined, av);
        }
        
        removeVertex(v1_o);
        removeVertex(v2_o);
    }
    
    private Set<Vertex> getCommonAdjVertices(Vertex v1, Vertex v2) {
        Set<Vertex> adj1 = adjList.get(v1.getLabel());
        Set<Vertex> adj2 = adjList.get(v2.getLabel());
        
        Set<Vertex> allAdj = new HashSet<Vertex>(adj1.size() + adj2.size());
        allAdj.addAll(adj1);
        allAdj.addAll(adj2);
        return allAdj;
    }
    
    /**
     * Removes the vertex and all adges containing this vertex
     * Returns false if the graph does not contain the vertex
     */
    public boolean removeVertex(Vertex v) {
        if (!vertices.containsKey(v.getLabel()))
            return false;
        Set<Vertex> adj = adjList.get(v.getLabel());
        for(Vertex av: adj) {
            adjList.get(av.getLabel()).remove(v);
        }
        adjList.remove(v.getLabel());
        vertices.remove(v);
        return true;
    }
}
