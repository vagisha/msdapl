package edu.uwpr.protinfer.infer.graph;

public interface Vertex {

    public String getLabel();
    
    public int getComponentIndex();
    
    public void setComponentIndex(int index);
    
    public boolean isVisited();
    
    public void setVisited(boolean visited);
    
    public Vertex combineWith(Vertex v);
}
