/**
 * Experiment.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzymeDb;
import org.yeastrc.ms.domain.search.MsSearchDatabaseDb;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.MsSearchModificationDb;

/**
 * @param <MsRun>
 * 
 */
public class MsSearchDbImpl implements MsSearchDb {

    private int id;
    private Date uploadDate;
    private Date searchDate;
    private String serverAddress;
    private String serverDirectory;
    private String analysisProgramName;
    private String analysisProgramVersion;
    
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
     * @return the serverAddress
     */
    public String getServerAddress() {
        return serverAddress;
    }
    /**
     * @param serverAddress the serverAddress to set
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    /**
     * @return the serverDirectory
     */
    public String getServerDirectory() {
        return serverDirectory;
    }
    /**
     * @param directory the serverDirectory to set
     */
    public void setServerDirectory(String directory) {
        this.serverDirectory = directory;
    }

    public String getAnalysisProgramName() {
        return analysisProgramName;
    }
    
    public void setAnalysisProgramName(String programName) {
        this.analysisProgramName = programName;
    }
    
    public String getAnalysisProgramVersion() {
        return analysisProgramVersion;
    }
    
    public void setAnalysisProgramVersion(String programVersion) {
        this.analysisProgramVersion = programVersion;
    }

    public void setUploadDate(Date date) {
        this.uploadDate = date;
    }
    
    @Override
    public Date getUploadDate() {
        return uploadDate;
    }
    
    public void setSearchDate(Date date) {
        this.searchDate = date;
    }
    
    @Override
    public Date getSearchDate() {
        return searchDate;
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
