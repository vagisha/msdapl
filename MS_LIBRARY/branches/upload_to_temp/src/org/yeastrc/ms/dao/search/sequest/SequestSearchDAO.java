/**
 * SequestSearchDAO.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sequest;

import org.yeastrc.ms.dao.search.GenericSearchDAO;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;

/**
 * 
 */
public interface SequestSearchDAO extends GenericSearchDAO <SequestSearchIn, SequestSearch>{

    public abstract String getSearchParamValue(int searchId, String paramName);
    
    /**
     * Returns the parent mass type (Average or Monoisotopic) used for the search
     * @param searchId
     * @return
     */
    public abstract MassType getParentMassType(int searchId);
    
    /**
     * Returns the fragment mass type (Average or Monoisotopic) used for the search
     * @param searchId
     * @return
     */
    public abstract MassType getFragmentMassType(int searchId);
    
    /**
     * Returns true if the value of print_expect_score is 1
     * @return
     */
    public abstract boolean hasEvalue(int searchId);
}
