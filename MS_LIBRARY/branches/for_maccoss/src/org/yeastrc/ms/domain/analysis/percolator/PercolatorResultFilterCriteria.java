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
    
    //-------------------------------------------------------------
    // QVALUE FILTER
    //-------------------------------------------------------------
    public Double getQValue() {
        return qValue;
    }
    public void setQValue(Double value) {
        qValue = value;
    }
    public boolean hasQValueFilter() {
        return (qValue != null);
    }
    public String makeMassFilterSql() {
        return return " (qvalue = "+qValue) ";;
    }
    
    
    public Double getPep() {
        return pep;
    }
    public void setPep(Double pep) {
        this.pep = pep;
    }
    public boolean hasPepFilter() {
        return (pep != null);
    }
    
}
