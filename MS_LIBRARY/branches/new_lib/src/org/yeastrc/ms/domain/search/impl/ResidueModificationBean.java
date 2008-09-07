/**
 * ResidueModificationBean.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import org.yeastrc.ms.domain.search.MsResidueModification;

/**
 * 
 */
public class ResidueModificationBean extends ResidueModification implements
        MsResidueModification {

    private int id;
    private int searchId;
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return the searchId
     */
    public int getSearchId() {
        return searchId;
    }

    /**
     * @param searchId the serachId to set
     */
    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }
}
