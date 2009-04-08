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
    
    public boolean hasFilters() {
        if(super.hasFilters())
            return true;
        
        return (hasQValueFilter() ||
                hasPepFilter());
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
}
