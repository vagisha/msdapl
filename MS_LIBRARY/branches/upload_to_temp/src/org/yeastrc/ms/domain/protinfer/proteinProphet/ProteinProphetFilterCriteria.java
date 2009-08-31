/**
 * ProteinProphetFilterCriteria.java
 * @author Vagisha Sharma
 * Aug 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;

/**
 * 
 */
public class ProteinProphetFilterCriteria extends ProteinFilterCriteria {

    private double minProbability;
    private double maxProbability;
    
    public static SORT_BY defaultSortBy() {
        return SORT_BY.PROTEIN_PROPHET_GROUP;
    }
    
    public static SORT_ORDER defaultSortOrder() {
        return SORT_ORDER.ASC;
    }
    
    public boolean equals(ProteinProphetFilterCriteria o) {
        if(!super.equals(o))
            return false;
        if(!(o instanceof ProteinProphetFilterCriteria))
            return false;
        ProteinProphetFilterCriteria that = (ProteinProphetFilterCriteria)o;
        if(this.minProbability != that.minProbability)          return false;
        if(this.maxProbability != that.maxProbability)          return false;

        return true;
    }

    public double getMinProbability() {
        return minProbability;
    }

    public void setMinProbability(double minProbability) {
        this.minProbability = minProbability;
    }

    public double getMaxProbability() {
        return maxProbability;
    }

    public void setMaxProbability(double maxProbability) {
        this.maxProbability = maxProbability;
    }
    
}
