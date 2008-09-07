/**
 * ResultResidueModBean.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import org.yeastrc.ms.domain.search.MsResultResidueMod;

/**
 * 
 */
public class ResultResidueModBean extends ResultResidueMod implements
        MsResultResidueMod {

    private int modId;
    private int resultId;
    
    
    /**
     * @return the modId
     */
    public int getModificationId() {
        return modId;
    }
    /**
     * @param modId the modId to set
     */
    public void setModificationId(int modId) {
        this.modId = modId;
    }
    /**
     * @return the resultId
     */
    public int getResultId() {
        return resultId;
    }
    /**
     * @param resultId the resultId to set
     */
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
}
