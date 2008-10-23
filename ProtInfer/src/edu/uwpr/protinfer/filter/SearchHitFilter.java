                                                            package edu.uwpr.protinfer.filter;

import java.util.Comparator;
import java.util.List;

import edu.uwpr.protinfer.SearchHit;

public interface SearchHitFilter {
    
    public abstract List<SearchHit> filterSearchHits(List<SearchHit> searchHits, Comparator<SearchHit> comparator) throws FilterException;
}
