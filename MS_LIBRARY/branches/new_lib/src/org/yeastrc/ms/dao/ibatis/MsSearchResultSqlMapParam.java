package org.yeastrc.ms.dao.ibatis;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.MsSearchResult;
import org.yeastrc.ms.domain.MsSearchResultPeptide;
import org.yeastrc.ms.domain.MsSearchResultProtein;

public class MsSearchResultSqlMapParam implements MsSearchResult {

    private int searchId;
    private int scanId;
    private MsSearchResult result;
    
    public MsSearchResultSqlMapParam(int searchId, int scanId, MsSearchResult result) {
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


    public BigDecimal getCalculatedMass() {
        return result.getCalculatedMass();
    }

    public int getCharge() {
        return result.getCharge();
    }

    public int getNumIonsMatched() {
        return result.getNumIonsMatched();
    }

    public int getNumIonsPredicted() {
        return result.getNumIonsPredicted();
    }

    public List<? extends MsSearchResultProtein> getProteinMatchList() {
        return result.getProteinMatchList();
    }

    public MsSearchResultPeptide getResultPeptide() {
        return result.getResultPeptide();
    }

    public ValidationStatus getValidationStatus() {
        return result.getValidationStatus();
    }
}
