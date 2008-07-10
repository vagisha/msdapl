/**
 * MsSearchDb.java
 * @author Vagisha Sharma
 * Jul 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import org.yeastrc.ms.domain.IMsSearch;

/**
 * 
 */
public class MsSearchDb {

    private int runId;
    private IMsSearch search;
    
    public MsSearchDb(int runId, IMsSearch search) {
        this.runId = runId;
        this.search = search;
    }

    /**
     * @return the runId
     */
    public int getRunId() {
        return runId;
    }

    /**
     * @return the search
     */
    public IMsSearch getSearch() {
        return search;
    }
}
