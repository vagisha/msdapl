package org.yeastrc.ms.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface MsSearch extends MsSearchBase {

    /**
     * @return the searchDatabases
     */
    public abstract List<MsSearchDatabase> getSearchDatabases();

    /**
     * @return the staticModifications
     */
    public abstract List<MsSearchModification> getStaticModifications();

    /**
     * @return the dynamicModifications
     */
    public abstract List<MsSearchModification> getDynamicModifications();
    
    /**
     * @return the enzymes used for this search
     */
    public abstract List<MsEnzyme> getEnzymeList();
    
}

interface MsSearchBase {
    
    /**
     * @return the originalFileType
     */
    public abstract SearchFileFormat getSearchFileFormat();

    /**
     * @return the searchEngineName
     */
    public abstract String getSearchEngineName();

    /**
     * @return the searchEngineVersion
     */
    public abstract String getSearchEngineVersion();

    /**
     * @return the searchDate
     */
    public abstract Date getSearchDate();

    /**
     * @return the time taken for the search (in minutes)
     */
    public abstract int getSearchDuration();

    /**
     * @return the precursorMassType
     */
    public abstract String getPrecursorMassType();

    /**
     * @return the precursorMassTolerance
     */
    public abstract BigDecimal getPrecursorMassTolerance();

    /**
     * @return the fragmentMassType
     */
    public abstract String getFragmentMassType();

    /**
     * @return the fragmentMassTolerance
     */
    public abstract BigDecimal getFragmentMassTolerance();
}