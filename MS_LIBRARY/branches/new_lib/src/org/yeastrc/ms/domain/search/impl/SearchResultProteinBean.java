/**
 * MsSearchResultProteinDbImpl.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import org.yeastrc.ms.domain.search.MsSearchResultProtein;


public class SearchResultProteinBean implements MsSearchResultProtein {

    private int resultId;
    private int proteinId; // database id of the protein (from NR_SEQ database)
    
    public SearchResultProteinBean() {}
    
    public SearchResultProteinBean(int resultId, int proteinId) {
        this.resultId = resultId;
        this.proteinId = proteinId;
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
    
    public boolean equals(Object obj) {
        if (obj == this)   return true;
        if (!(obj instanceof SearchResultProteinBean)) return false;
        
        SearchResultProteinBean that = (SearchResultProteinBean) obj;
        return (this.proteinId == that.getProteinId() && 
                this.resultId == that.getResultId());
    }
    
    public int hashCode(){
        return (resultId+""+proteinId).hashCode();
    }
}
