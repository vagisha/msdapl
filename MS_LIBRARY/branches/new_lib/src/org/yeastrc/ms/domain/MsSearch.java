package org.yeastrc.ms.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface MsSearch {

    public static enum SearchFileFormat {

        SQT, PEPXML, UNKNOWN;

        public static SearchFileFormat getFileFormatForString(String extString) {
            if (extString.equalsIgnoreCase(SearchFileFormat.SQT.name()))
                return SearchFileFormat.SQT;
            else if (extString.equals(SearchFileFormat.PEPXML.name()))
                return SearchFileFormat.PEPXML;
            else return SearchFileFormat.UNKNOWN;
        }
    };
    
    /**
     * @return the originalFileType
     */
    public abstract SearchFileFormat getOriginalFileType();

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

    /**
     * @return the searchDatabases
     */
    public abstract List<? extends MsSearchDatabase> getSearchDatabases();

    /**
     * @return the staticModifications
     */
    public abstract List<? extends MsSearchModification> getStaticModifications();

    /**
     * @return the dynamicModifications
     */
    public abstract List<? extends MsSearchModification> getDynamicModifications();

}