package edu.uwpr.protinfer.infer.graph;

import java.util.List;

public interface Vertex {

    public String getLabel();
    
    public int getComponentIndex();
    
    public void setComponentIndex(int index);
    
    public boolean isVisited();
    
    public void setVisited(boolean visited);
    
    public Vertex combineWith(Vertex v);
    
    public Vertex combineWith(List<Vertex> vertices);
}
