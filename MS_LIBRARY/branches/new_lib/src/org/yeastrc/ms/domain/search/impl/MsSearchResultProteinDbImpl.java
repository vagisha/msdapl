/**
 * MsSearchResultProteinDbImpl.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;


public class MsSearchResultProteinDbImpl implements MsSearchResultProteinDb {

    private int id; // unique id (database) for this protein match
    private int resultId;
    private int proteinId; // database id of the protein (from NR_SEQ database)
    
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
    
    public int getResultId() {
        return resultId;
    }
    /**
     * @param resultId the resultId to set
     */
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
   
    public int getProteinId() {
        return proteinId;
    }
    
    public void setProteinId(int id) {
        this.proteinId = id;
    }
}
