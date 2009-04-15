/**
 * ProteinDatasetComparisonFilters.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ProteinDatasetComparisonFilters {

    private List<Dataset> andFilters;
    private List<Dataset> orFilters;
    private List<Dataset> notFilters;
    
    public ProteinDatasetComparisonFilters() {
        andFilters = new ArrayList<Dataset>();
        orFilters = new ArrayList<Dataset>();
        notFilters = new ArrayList<Dataset>();
    }

    public List<Dataset> getAndFilters() {
        return andFilters;
    }

    public void setAndFilters(List<Dataset> andFilters) {
        this.andFilters = andFilters;
    }

    public List<Dataset> getOrFilters() {
        return orFilters;
    }

    public void setOrFilters(List<Dataset> orFilters) {
        this.orFilters = orFilters;
    }

    public List<Dataset> getNotFilters() {
        return notFilters;
    }

    public void setNotFilters(List<Dataset> notFilters) {
        this.notFilters = notFilters;
    }
    
    
}
