package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph implements IGraph {

    private Map<String, Vertex> vertices;
    private Map<String, List<Vertex>> adjList;
    private Set<Edge> edges;
    
    public Graph() {
        vertices = new HashMap<String, Vertex>();
        adjList = new HashMap<String, List<Vertex>>();
        edges = new HashSet<Edge>();
    }
    
    public Vertex addVertex(Vertex vertex) {
        Vertex v = vertices.get(vertex.getLabel());
        if (v != null)
            return v;
        vertices.put(vertex.getLabel(), v);
        return vertex;
    }
    
    public void addEdge(Vertex v1, Vertex v2) {
       v1 = addVertex(v1);
       v2 = addVertex(v2);
       
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
        List<Vertex> adjV = getCommonAdjVertices(v1_o, v2_o);
        
        for (Vertex av: adjV) {
            addEdge(v_combined, av);
        }
        
        removeVertex(v1_o);
        removeVertex(v2_o);
    }
    
    /**
     * Removes the vertex and all adges containing this vertex
     * Returns false if the graph does not contain the vertex
     */
    public boolean removeVertex(Vertex v) {
        if (!vertices.containsKey(v.getLabel()))
            return false;
        List<Vertex> adj = adjList.get(v.getLabel());
        for(Vertex av: adj) {
            edges.remove(new Edge(v, av));
        }
        adjList.remove(v.getLabel());
        vertices.remove(v);
        return true;
    }
    
    private List<Vertex> getCommonAdjVertices(Vertex v1, Vertex v2) {
        List<Vertex> adj1 = adjList.get(v1.getLabel());
        List<Vertex> adj2 = adjList.get(v2.getLabel());
        
        List<Vertex> allAdj = new ArrayList<Vertex>(adj1.size() + adj2.size());
        allAdj.addAll(adj1);
        allAdj.addAll(adj2);
        
        Collections.sort(allAdj, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }});
        List<Vertex> combinedAdj = new ArrayList<Vertex>(allAdj.size());
        String lastLabel = null;
        
        for (Vertex v: allAdj) {
            if (v.getLabel().equals(lastLabel))
                continue;
            lastLabel = v.getLabel();
            combinedAdj.add(v);
        }
        return combinedAdj;
    }
}
