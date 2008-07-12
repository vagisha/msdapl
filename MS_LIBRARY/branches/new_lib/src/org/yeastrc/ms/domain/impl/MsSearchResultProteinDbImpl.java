/**
 * MsSearchResultProteinDbImpl.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.impl;

import org.yeastrc.ms.domain.MsSearchResultProteinDb;


public class MsSearchResultProteinDbImpl implements MsSearchResultProteinDb {

    private int id; // unique id (database) for this protein match
    private int resultId;
    private String accession;
    private String description;
    
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
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsSearchResultProtein#getResultId()
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
    /**
     * @return the accession
     */
    public String getAccession() {
        return accession;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsSearchResultProtein#setAccession(java.lang.String)
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
