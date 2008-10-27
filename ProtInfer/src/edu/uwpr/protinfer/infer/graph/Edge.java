package edu.uwpr.protinfer.infer.graph;

public class Edge {

    private Vertex v1;
    private Vertex v2;
    
    public Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
    
    public Vertex getVertex1() {
        return v1;
    }
    
    public Vertex getVertex2() {
        return v2;
    }
    
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || (o.getClass() != this.getClass()))
            return false;
        Edge that = (Edge)o;
        
        return (same(this.v1, that.v1) && same(this.v2, that.v2)) ||
               (same(this.v2, that.v1) && same(this.v1, that.v2)); 
    }
    
    public int hashCode() {
       return v1.getLabel().hashCode() + v2.getLabel().hashCode(); 
    }
    
    private boolean same(Vertex v1, Vertex v2) {
        return v1.getLabel().equals(v2.getLabel());
    }
}
