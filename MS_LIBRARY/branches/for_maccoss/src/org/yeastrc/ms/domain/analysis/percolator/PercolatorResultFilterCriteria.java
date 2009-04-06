/**
 * PercolatorResultsFilter.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.percolator;

import org.yeastrc.ms.domain.search.ResultFilterCriteria;

/**
 * 
 */
public class PercolatorResultFilterCriteria extends ResultFilterCriteria {

    private Double qValue;
    private Double pep;
    
    public boolean hasFilters() {
        if(super.hasFilters())
            return true;
        
        return (qValue != null || pep != null);
    }
    
    public boolean superHasFilters() {
        return super.hasFilters();
    }
    
    public Double getQValue() {
        return qValue;
    }
    public void setQValue(Double value) {
        qValue = value;
    }
    
    public Double getPep() {
        return pep;
    }
    public void setPep(Double pep) {
        this.pep = pep;
    }
    
    
}
