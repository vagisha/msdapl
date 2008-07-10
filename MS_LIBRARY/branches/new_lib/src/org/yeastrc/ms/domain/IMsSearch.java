package org.yeastrc.ms.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface IMsSearch {

    /**
     * @return the originalFileType
     */
    public abstract String getOriginalFileType();

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
     * @return the searchDuration
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

    /**
     * @return the searchDatabases
     */
    public abstract List<? extends IMsSearchDatabase> getSearchDatabases();

    /**
     * @return the staticModifications
     */
    public abstract List<? extends IMsSearchModification> getStaticModifications();

    /**
     * @return the dynamicModifications
     */
    public abstract List<? extends IMsSearchModification> getDynamicModifications();

}