package edu.uwpr.protinfer.infer.graph;

import java.util.List;

public interface IGraph {

    public abstract Vertex addVertex(Vertex vertex);

    public abstract void addEdge(Vertex v1, Vertex v2);

    public abstract int getEdgeCount();

    public abstract List<Vertex> getAdjacentVertices(Vertex vertex);

    public abstract boolean containsEdge(Vertex v1, Vertex v2);

    public abstract List<Vertex> getVertices();

    public abstract void combineVertices(Vertex v1, Vertex v2)
            throws InvalidVertexException;
    
    public abstract boolean removeVertex(Vertex v);

}