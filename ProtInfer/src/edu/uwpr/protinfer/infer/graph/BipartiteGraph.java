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
    
    private Map<String, Set<Vertex>> adjList;
    
    
    public BipartiteGraph() {
        verticesL = new HashMap<String, Vertex>();
        verticesR = new HashMap<String, Vertex>();
        adjList = new HashMap<String, Set<Vertex>>();
    }
    
    /**
     * Adds a vertex to "left" set of vertices of the bipartite graph.
     */
    public Vertex addVertex(Vertex vertex) {
        return addLeftVertex(vertex);
    }
    
    /**
     * If a vertex with the same label is already in the set of "left" edges
     * it is returned, otherwise the given vertex is added and returned. 
     * If the vertex could not be added (e.g. if it is already part of the 
     * set of "right" edges) the return value is null.
     * @param vertex
     * @return
     */
    public Vertex addLeftVertex(Vertex vertex) {
        // don't add if this vertex is already in the set of "right" vertices
        if (verticesR.get(vertex.getLabel()) != null)
            return null;
        Vertex v = verticesL.get(vertex.getLabel());
        if (v != null)
            return v;
        verticesL.put(vertex.getLabel(), vertex);
        return vertex;
    }
    
    /**
     * If a vertex with the same label is already in the set of "right" edges
     * it is returned, otherwise the given vertex is added and returned. 
     * If the vertex could not be added (e.g. if it is already part of the 
     * set of "left" edges) the return value is null.
     * @param vertex
     * @return
     */
    public Vertex addRightVertex(Vertex vertex) {
        if(verticesL.get(vertex.getLabel()) != null)
            return null;
        Vertex v = verticesR.get(vertex.getLabel());
        if (v != null)
            return v;
        verticesR.put(vertex.getLabel(), vertex);
        return vertex;
    }
    
    /**
     * If v1 is not already in the set of "left" vertices it is added.
     * If v2 is not already in the set of "right" vertices it is added.
     * If either v1 or v2 could not be added to the set of vertices, 
     * the edge is not added.
     * An edge is added between v1 and v2 it is does not already exist.
     */
    public void addEdge(Vertex v1, Vertex v2) {
       v1 = addLeftVertex(v1);
       v2 = addRightVertex(v2);
       
       if (v1 == null || v2 == null)
           return;
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

    public void combineVertices(Vertex v1, Vertex v2) throws InvalidVertexException {
        combineLeftVertices(v1, v2);
    }
    
    public void combineLeftVertices(Vertex v1, Vertex v2) throws InvalidVertexException {
        Vertex v1_o = verticesL.get(v1.getLabel());
        if (v1_o == null) {
            throw new InvalidVertexException("Vertex "+v1.getLabel()+" not found in graph");
        }
        Vertex v2_o = verticesL.get(v2.getLabel());
        if (v2_o == null) {
            throw new InvalidVertexException("Vertex "+v2.getLabel()+" not found in graph");
        }
       combineGraphVertices(v1_o, v2_o);
    }
    
    public void combineRightVertices(Vertex v1, Vertex v2) throws InvalidVertexException {
        Vertex v1_o = verticesL.get(v1.getLabel());
        if (v1_o == null) {
            throw new InvalidVertexException("Vertex "+v1.getLabel()+" not found in graph");
        }
        Vertex v2_o = verticesL.get(v2.getLabel());
        if (v2_o == null) {
            throw new InvalidVertexException("Vertex "+v2.getLabel()+" not found in graph");
        }
        combineGraphVertices(v1_o, v2_o);
    }
    
    private void combineGraphVertices(Vertex v1_o, Vertex v2_o) {
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
        if (removeLeftVertex(v) || !removeRightVertex(v))
            return true;
        else
            return false;
    }
    
    public boolean removeLeftVertex(Vertex v) {
        if (!verticesL.containsKey(v.getLabel()))
            return false;
        Set<Vertex> adj = adjList.get(v.getLabel());
        for(Vertex av: adj) {
            adjList.get(av.getLabel()).remove(v);
        }
        adjList.remove(v.getLabel());
        verticesL.remove(v);
        return true;
    }
    
    public boolean removeRightVertex(Vertex v) {
        if (!verticesR.containsKey(v.getLabel()))
            return false;
        Set<Vertex> adj = adjList.get(v.getLabel());
        for(Vertex av: adj) {
            adjList.get(av.getLabel()).remove(v);
        }
        adjList.remove(v.getLabel());
        verticesR.remove(v);
        return true;
    }
}
