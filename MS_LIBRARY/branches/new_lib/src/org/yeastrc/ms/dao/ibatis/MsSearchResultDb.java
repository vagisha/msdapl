package org.yeastrc.ms.dao.ibatis;

import org.yeastrc.ms.domain.IMsSearchResult;

public class MsSearchResultDb {

    private int searchId;
    private int scanId;
    private IMsSearchResult result;
    
    public MsSearchResultDb(int searchId, int scanId, IMsSearchResult result) {
        this.searchId = searchId;
        this.scanId = scanId;
        this.result = result;
    }

    /**
     * @return the searchId
     */
    public int getSearchId() {
        return searchId;
    }

    /**
     * @return the scanId
     */
    public int getScanId() {
        return scanId;
    }

    /**
     * @return the result
     */
    public IMsSearchResult getResult() {
        return result;
    }
}
