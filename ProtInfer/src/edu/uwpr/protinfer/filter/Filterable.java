package edu.uwpr.protinfer.filter;

public interface Filterable{

    public abstract void applyFilter(FilterCriteria<? extends Filterable> filterCriteria);
    
    public abstract void setAccepted();
    
    public abstract void setNotAccepted();
    
    public abstract boolean isAccepted();
}
