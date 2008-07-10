/**
 * SQTSearchResultDb.java
 * @author Vagisha Sharma
 * Jul 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import org.yeastrc.ms.domain.sqtFile.ISQTSearchResult;

/**
 * 
 */
public class SQTSearchResultDb {
    
    private int resultId;
    private ISQTSearchResult result;
    
    public SQTSearchResultDb(int resultID, ISQTSearchResult result) {
        this.resultId = resultId;
        this.result = result;
    }

    /**
     * @return the resultId
     */
    public int getResultId() {
        return resultId;
    }

    /**
     * @return the result
     */
    public ISQTSearchResult getResult() {
        return result;
    }
    
    
}
