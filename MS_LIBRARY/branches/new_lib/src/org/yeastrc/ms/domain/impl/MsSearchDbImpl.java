/**
 * MsPeptideSearch.java
 * @author Vagisha Sharma
 * July 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.MsSearchDb;
import org.yeastrc.ms.domain.general.MsEnzymeDb;
import org.yeastrc.ms.domain.search.MsSearchDatabaseDb;
import org.yeastrc.ms.domain.search.MsSearchModificationDb;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.impl.MsSearchDatabaseDbImpl;

public class MsSearchDbImpl implements MsSearchDb {

    private int id; // unique id (database) for this search result
    private int experimentId; // id of the experiment this search belongs to
    private int runId; // MS run on which the search was performed
    
    private SearchFileFormat originalFileType;
    private String searchEngineName;
    private String searchEngineVersion;
    private Date searchDate;
    private int searchDuration; // number of minutes for the search
    private String precursorMassType; // monoisotopic or average
    private BigDecimal precursorMassTolerance;
    private String fragmentMassType; // monoisotopic or average
    private BigDecimal fragmentMassTolerance;
    
    private List<? super MsSearchDatabaseDb> searchDatabases;
    
    private List<? super MsSearchModificationDb> staticModifications;
    
    private List<? super MsSearchModificationDb> dynamicModifications;
    
    private List<? super MsEnzymeDb> enzymes;
    
    public MsSearchDbImpl() {
        searchDatabases = new ArrayList<MsSearchDatabaseDb>();
        staticModifications = new ArrayList<MsSearchModificationDb>();
        dynamicModifications = new ArrayList<MsSearchModificationDb>();
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
    
    /**
     * @return the experimentId
     */
    public int getExperimentId() {
        return experimentId;
    }
    
    /**
     * @param experimentId the experimentId to set
     */
    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }
    
    public SearchFileFormat getSearchFileFormat() {
        return originalFileType;
    }
    /**
     * @param originalFileType the originalFileType to set
     */
    public void setSearchFileFormat(SearchFileFormat format) {
        this.originalFileType = format;
    }
    
    public String getSearchEngineName() {
        return searchEngineName;
    }
    /**
     * @param searchEngineName the searchEngineName to set
     */
    public void setSearchEngineName(String searchEngineName) {
        this.searchEngineName = searchEngineName;
    }
    
    public String getSearchEngineVersion() {
        return searchEngineVersion;
    }
    /**
     * @param searchEngineVersion the searchEngineVersion to set
     */
    public void setSearchEngineVersion(String searchEngineVersion) {
        this.searchEngineVersion = searchEngineVersion;
    }
    
    public Date getSearchDate() {
        return searchDate;
    }
    /**
     * @param searchDate the searchDate to set
     */
    public void setSearchDate(Date searchDate) {
        this.searchDate = searchDate;
    }
    
    public int getSearchDuration() {
        return searchDuration;
    }
    /**
     * @param searchDuration the searchDuration to set
     */
    public void setSearchDuration(int searchDuration) {
        this.searchDuration = searchDuration;
    }
    
    public String getPrecursorMassType() {
        return precursorMassType;
    }
    /**
     * @param precursorMassType the precursorMassType to set
     */
    public void setPrecursorMassType(String precursorMassType) {
        this.precursorMassType = precursorMassType;
    }

    public BigDecimal getPrecursorMassTolerance() {
        return precursorMassTolerance;
    }
    /**
     * @param precursorMassTolerance the precursorMassTolerance to set
     */
    public void setPrecursorMassTolerance(BigDecimal precursorMassTolerance) {
        this.precursorMassTolerance = precursorMassTolerance;
    }

    public String getFragmentMassType() {
        return fragmentMassType;
    }
    /**
     * @param fragmentMassType the fragmentMassType to set
     */
    public void setFragmentMassType(String fragmentMassType) {
        this.fragmentMassType = fragmentMassType;
    }

    public BigDecimal getFragmentMassTolerance() {
        return fragmentMassTolerance;
    }
    /**
     * @param fragmentMassTolerance the fragmentMassTolerance to set
     */
    public void setFragmentMassTolerance(BigDecimal fragmentMassTolerance) {
        this.fragmentMassTolerance = fragmentMassTolerance;
    }

    //------------------------------------------------------------------------------------------------------
    // database(s) used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsSearchDatabaseDb> getSearchDatabases() {
        return (List<MsSearchDatabaseDb>) searchDatabases;
    }

    /**
     * @param searchDatabases the searchDatabases to set
     */
    public void setSearchDatabases(List<? super MsSearchDatabaseDb> searchDatabases) {
        this.searchDatabases = searchDatabases;
    }

    public void addSearchDatabase(MsSearchDatabaseDbImpl database) {
        searchDatabases.add(database);
    }

    //------------------------------------------------------------------------------------------------------
    // static modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsSearchModificationDb> getStaticModifications() {
        return (List<MsSearchModificationDb>) staticModifications;
    }

    /**
     * @param staticModifications the staticModifications to set
     */
    public void setStaticModifications(List<? super MsSearchModificationDb> staticModifications) {
        this.staticModifications = staticModifications;
    }
    
    public void addStaticModification(MsSearchModificationDb mod) {
        staticModifications.add(mod);
    }

    //------------------------------------------------------------------------------------------------------
    // dynamic modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsSearchModificationDb> getDynamicModifications() {
        return (List<MsSearchModificationDb>) dynamicModifications;
    }

    /**
     * @param dynamicModifications the dynamicModifications to set
     */
    public void setDynamicModifications(List<? super MsSearchModificationDb> dynamicModifications) {
        this.dynamicModifications = dynamicModifications;
    }
    
    public void addDynamicModification(MsSearchModificationDb mod) {
        dynamicModifications.add(mod);
    }

    //------------------------------------------------------------------------------------------------------
    // enzymes used for the search
    //------------------------------------------------------------------------------------------------------
    @Override
    public List<MsEnzymeDb> getEnzymeList() {
        return (List<MsEnzymeDb>) enzymes;
    }
    
    public void addEnzyme(MsEnzymeDb enzyme) {
        enzymes.add(enzyme);
    }
    
    public void setEnzymeList(List<? super MsEnzymeDb> enzymes) {
        this.enzymes = enzymes;
    }
}
