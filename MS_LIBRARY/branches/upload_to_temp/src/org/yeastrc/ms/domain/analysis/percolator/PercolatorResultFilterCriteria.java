/**
 * PercolatorResultsFilter.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.percolator;

import org.yeastrc.ms.domain.search.ResultFilterCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;

/**
 * 
 */
public class PercolatorResultFilterCriteria extends ResultFilterCriteria {

    private Double minQValue;
    private Double maxQValue;
    private Double minPep;
    private Double maxPep;
    private Double minDs; // discriminant score or SVM score
    private Double maxDs;
    
    public boolean hasFilters() {
        if(super.hasFilters())
            return true;
        
        return (hasQValueFilter() ||
                hasPepFilter() ||
                hasDsFilter());
    }
    
    public boolean superHasFilters() {
        return super.hasFilters();
    }

    //-------------------------------------------------------------
    // QVALUE FILTER
    //-------------------------------------------------------------
    public Double getMinQValue() {
        return minQValue;
    }

    public void setMinQValue(Double minQValue) {
        this.minQValue = minQValue;
    }

    public Double getMaxQValue() {
        return maxQValue;
    }

    public void setMaxQValue(Double maxQValue) {
        this.maxQValue = maxQValue;
    }

    public boolean hasQValueFilter() {
      return (minQValue != null || maxQValue != null);
    }
    
    public String makeQValueFilterSql() {
        return makeFilterSql(SORT_BY.QVAL.getColumnName(), minQValue, maxQValue);
    }
    
    //-------------------------------------------------------------
    // PEP FILTER
    //-------------------------------------------------------------
    public Double getMinPep() {
        return minPep;
    }

    public void setMinPep(Double minPep) {
        this.minPep = minPep;
    }

    public Double getMaxPep() {
        return maxPep;
    }

    public void setMaxPep(Double maxPep) {
        this.maxPep = maxPep;
    }
    
    public boolean hasPepFilter() {
        return (minPep != null || maxPep != null);
    }
    
    public String makePepFilterSql() {
        return makeFilterSql(SORT_BY.PEP.getColumnName(), minPep, maxPep);
    }
    
    //-------------------------------------------------------------
    // Discriminant Score (SVM score) FILTER
    //-------------------------------------------------------------
    public Double getMinDs() {
        return minDs;
    }

    public void setMinDs(Double minDs) {
        this.minDs = minDs;
    }

    public Double getMaxDs() {
        return maxDs;
    }

    public void setMaxDs(Double maxDs) {
        this.maxDs = maxDs;
    }
    
    public boolean hasDsFilter() {
        return (minDs != null || maxDs != null);
    }
    
    public String makeDsFilterSql() {
        return makeFilterSql(SORT_BY.DS.getColumnName(), minDs, maxDs);
    }
}
