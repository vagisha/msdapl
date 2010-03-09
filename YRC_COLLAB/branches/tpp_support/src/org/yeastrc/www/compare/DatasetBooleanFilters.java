/**
 * ProteinDatasetComparisonFilters.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.compare.dataset.Dataset;

/**
 * 
 */
public class DatasetBooleanFilters {

    private List<Dataset> andFilters;
    private List<Dataset> orFilters;
    private List<Dataset> notFilters;
    private List<Dataset> xorFilters;
    
    public DatasetBooleanFilters() {
        andFilters = new ArrayList<Dataset>();
        orFilters = new ArrayList<Dataset>();
        notFilters = new ArrayList<Dataset>();
        xorFilters = new ArrayList<Dataset>();
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

    public List<Dataset> getXorFilters() {
        return xorFilters;
    }
    
    public void setXorFilters(List<Dataset> xorFilters) {
        this.xorFilters = xorFilters;
    }
    
}
