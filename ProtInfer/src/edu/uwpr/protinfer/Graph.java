package edu.uwpr.protinfer;

import java.util.List;

public interface Graph {

    public abstract void addEdge(Node src, Node dest);
    
    public abstract List<Node> getNodes();
}
