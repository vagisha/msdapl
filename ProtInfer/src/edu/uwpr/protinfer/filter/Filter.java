package edu.uwpr.protinfer.filter;

import java.util.ArrayList;
import java.util.List;

public class Filter <T extends Filterable>{

    public List<T> filter(List<T> filterables, FilterCriteria<T> filterCriteria) {
        List<T> accepted = new ArrayList<T>(filterables.size());
        for (T filterable: filterables) {
            filterable.applyFilter(filterCriteria);
            if (filterable.isAccepted())
                accepted.add(filterable);
        }
        return accepted;
    }
    
}
