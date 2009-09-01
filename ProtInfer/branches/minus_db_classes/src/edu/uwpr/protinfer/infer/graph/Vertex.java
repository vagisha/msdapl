package edu.uwpr.protinfer.infer.graph;


public abstract class Vertex<T extends Vertex<?>> implements IVertex <T>{

    private boolean isVisited;
    private int componentIndex;
    private String label;
    
    public Vertex(String label) {
        this.label = label;
    }
    
    @Override
    public int getComponentIndex() {
        return componentIndex;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public boolean isVisited() {
        return isVisited;
    }

    @Override
    public void setComponentIndex(int index) {
        this.componentIndex = index;
    }

    @Override
    public void setVisited(boolean visited) {
        this.isVisited = visited;
    }
    
    /**
     * If this vertex is a collapsed vertex with several members return the member 
     * count, otherwise return 1.
     * @return
     */
    public abstract int getMemberCount();
}
