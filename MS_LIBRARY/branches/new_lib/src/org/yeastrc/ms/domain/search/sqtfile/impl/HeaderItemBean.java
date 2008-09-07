/**
 * HeaderItemBean.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sqtfile.impl;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;

/**
 * 
 */
public class HeaderItemBean extends HeaderItem implements SQTHeaderItem {

    private int id;
    private int runSearchId;
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return the searchId
     */
    public int getRunSearchId() {
        return runSearchId;
    }

    public void setRunSearchId(int runSearchId) {
        this.runSearchId = runSearchId;
    }
}
