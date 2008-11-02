package edu.uwpr.protinfer.infer.graph;

import java.util.List;

public interface IGraph {

    public abstract List<IVertex<?>> getAdjacentVertices(IVertex<?> vertex);

    public abstract List<IVertex<?>> getAllVertices();

}