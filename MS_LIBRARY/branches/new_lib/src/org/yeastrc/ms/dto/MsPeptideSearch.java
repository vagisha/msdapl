/**
 * MsPeptideSearch.java
 * @author Vagisha Sharma
 * July 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MsPeptideSearch implements IMsPeptideSearch {

    private int id; // unique id (database) for this search result
    private int runId; // MS run on which the search was performed
    
    private String originalFileType;
    private String searchEngineName;
    private String searchEngineVersion;
    private Date searchDate;
    private int searchDuration; // number of minutes for the search
    private String precursorMassType; // monoisotopic or average
    private BigDecimal precursorMassTolerance;
    private String fragmentMassType; // monoisotopic or average
    private BigDecimal fragmentMassTolerance;
    
    private List<MsSequenceDatabase> searchDatabases;
    
    private List<MsSearchMod> staticModifications;
    
    private List<MsSearchDynamicMod> dynamicModifications;
    
    public MsPeptideSearch() {
        searchDatabases = new ArrayList<MsSequenceDatabase>();
        staticModifications = new ArrayList<MsSearchMod>();
        dynamicModifications = new ArrayList<MsSearchDynamicMod>();
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return the runId
     */
    public int getRunId() {
        return runId;
    }

    /**
     * @param runId the runId to set
     */
    public void setRunId(int runId) {
        this.runId = runId;
    }
    
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getOriginalFileType()
     */
    public String getOriginalFileType() {
        return originalFileType;
    }
    /**
     * @param originalFileType the originalFileType to set
     */
    public void setOriginalFileType(String originalFileType) {
        this.originalFileType = originalFileType;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getSearchEngineName()
     */
    public String getSearchEngineName() {
        return searchEngineName;
    }
    /**
     * @param searchEngineName the searchEngineName to set
     */
    public void setSearchEngineName(String searchEngineName) {
        this.searchEngineName = searchEngineName;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getSearchEngineVersion()
     */
    public String getSearchEngineVersion() {
        return searchEngineVersion;
    }
    /**
     * @param searchEngineVersion the searchEngineVersion to set
     */
    public void setSearchEngineVersion(String searchEngineVersion) {
        this.searchEngineVersion = searchEngineVersion;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getSearchDate()
     */
    public Date getSearchDate() {
        return searchDate;
    }
    /**
     * @param searchDate the searchDate to set
     */
    public void setSearchDate(Date searchDate) {
        this.searchDate = searchDate;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getSearchDuration()
     */
    public int getSearchDuration() {
        return searchDuration;
    }
    /**
     * @param searchDuration the searchDuration to set
     */
    public void setSearchDuration(int searchDuration) {
        this.searchDuration = searchDuration;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getPrecursorMassType()
     */
    public String getPrecursorMassType() {
        return precursorMassType;
    }
    /**
     * @param precursorMassType the precursorMassType to set
     */
    public void setPrecursorMassType(String precursorMassType) {
        this.precursorMassType = precursorMassType;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getPrecursorMassTolerance()
     */
    public BigDecimal getPrecursorMassTolerance() {
        return precursorMassTolerance;
    }
    /**
     * @param precursorMassTolerance the precursorMassTolerance to set
     */
    public void setPrecursorMassTolerance(BigDecimal precursorMassTolerance) {
        this.precursorMassTolerance = precursorMassTolerance;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getFragmentMassType()
     */
    public String getFragmentMassType() {
        return fragmentMassType;
    }
    /**
     * @param fragmentMassType the fragmentMassType to set
     */
    public void setFragmentMassType(String fragmentMassType) {
        this.fragmentMassType = fragmentMassType;
    }
    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getFragmentMassTolerance()
     */
    public BigDecimal getFragmentMassTolerance() {
        return fragmentMassTolerance;
    }
    /**
     * @param fragmentMassTolerance the fragmentMassTolerance to set
     */
    public void setFragmentMassTolerance(BigDecimal fragmentMassTolerance) {
        this.fragmentMassTolerance = fragmentMassTolerance;
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getSearchDatabases()
     */
    public List<MsSequenceDatabase> getSearchDatabases() {
        return searchDatabases;
    }

    /**
     * @param searchDatabases the searchDatabases to set
     */
    public void setSearchDatabases(List<MsSequenceDatabase> searchDatabases) {
        this.searchDatabases = searchDatabases;
    }

    public void addSearchDatabase(MsSequenceDatabase database) {
        searchDatabases.add(database);
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getStaticModifications()
     */
    public List<MsSearchMod> getStaticModifications() {
        return staticModifications;
    }

    /**
     * @param staticModifications the staticModifications to set
     */
    public void setStaticModifications(List<MsSearchMod> staticModifications) {
        this.staticModifications = staticModifications;
    }
    
    public void addStaticModification(MsSearchMod mod) {
        staticModifications.add(mod);
    }

    /* (non-Javadoc)
     * @see org.yeastrc.ms.dto.IMsPeptideSearch#getDynamicModifications()
     */
    public List<MsSearchDynamicMod> getDynamicModifications() {
        return dynamicModifications;
    }

    /**
     * @param dynamicModifications the dynamicModifications to set
     */
    public void setDynamicModifications(
            List<MsSearchDynamicMod> dynamicModifications) {
        this.dynamicModifications = dynamicModifications;
    }
    
    public void addDynamicModification(MsSearchDynamicMod mod) {
        dynamicModifications.add(mod);
    }
  
}
