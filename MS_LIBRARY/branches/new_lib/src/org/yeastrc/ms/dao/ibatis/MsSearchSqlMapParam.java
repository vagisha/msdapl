/**
 * MsSearchDb.java
 * @author Vagisha Sharma
 * Jul 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.yeastrc.ms.domain.MsSearch;
import org.yeastrc.ms.domain.MsSearchDatabase;
import org.yeastrc.ms.domain.MsSearchModification;

/**
 * 
 */
public class MsSearchSqlMapParam implements MsSearch {

    private int runId;
    private MsSearch search;
    
    public MsSearchSqlMapParam(int runId, MsSearch search) {
        this.runId = runId;
        this.search = search;
    }

    /**
     * @return the runId
     */
    public int getRunId() {
        return runId;
    }
    
    public List<? extends MsSearchModification> getDynamicModifications() {
        return search.getDynamicModifications();
    }

    public List<? extends MsSearchDatabase> getSearchDatabases() {
        return search.getSearchDatabases();
    }

    public List<? extends MsSearchModification> getStaticModifications() {
        return search.getStaticModifications();
    }

    public BigDecimal getFragmentMassTolerance() {
        return search.getFragmentMassTolerance();
    }

    public String getFragmentMassType() {
        return search.getFragmentMassType();
    }

    public SearchFileFormat getOriginalFileType() {
        return search.getOriginalFileType();
    }

    public BigDecimal getPrecursorMassTolerance() {
        return search.getPrecursorMassTolerance();
    }

    public String getPrecursorMassType() {
        return search.getPrecursorMassType();
    }

    public Date getSearchDate() {
        return search.getSearchDate();
    }

    public int getSearchDuration() {
        return search.getSearchDuration();
    }

    public String getSearchEngineName() {
        return search.getSearchEngineName();
    }

    public String getSearchEngineVersion() {
        return search.getSearchEngineVersion();
    }
   
}
