/**
 * SQTSearchResultDb.java
 * @author Vagisha Sharma
 * Jul 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile.ibatis;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.sqtFile.SQTSearchResult;

/**
 * 
 */
public class SQTSearchResultSqlMapParam {
    
    private int resultId;
    private SQTSearchResult result;
    
    public SQTSearchResultSqlMapParam(int resultId, SQTSearchResult result) {
        this.resultId = resultId;
        this.result = result;
    }

    /**
     * @return the resultId
     */
    public int getResultId() {
        return resultId;
    }

    public BigDecimal getDeltaCN() {
        return result.getDeltaCN();
    }

    public BigDecimal getSp() {
        return result.getSp();
    }

    public int getSpRank() {
        return result.getSpRank();
    }

    public BigDecimal getxCorr() {
        return result.getxCorr();
    }

    public int getxCorrRank() {
        return result.getxCorrRank();
    }
  
}
