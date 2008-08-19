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
import org.yeastrc.ms.domain.search.MsResidueModificationDb;
import org.yeastrc.ms.domain.search.MsSearchDatabaseDb;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.MsTerminalModificationDb;

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
    
    private List<? super MsResidueModificationDb> staticResidueMods;
    
    private List<? super MsResidueModificationDb> dynamicResidueMods;
    
    private List<? super MsTerminalModificationDb> terminalStaticMods;
    
    private List<? super MsTerminalModificationDb> terminalDynamicMods;
    
    private List<? super MsEnzymeDb> enzymes;
    
    public MsSearchDbImpl() {
        searchDatabases = new ArrayList<MsSearchDatabaseDb>();
        staticResidueMods = new ArrayList<MsResidueModificationDb>();
        dynamicResidueMods = new ArrayList<MsResidueModificationDb>();
        terminalStaticMods = new ArrayList<MsTerminalModificationDb>();
        terminalDynamicMods = new ArrayList<MsTerminalModificationDb>();
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
    public List<MsResidueModificationDb> getStaticModifications() {
        return (List<MsResidueModificationDb>) staticResidueMods;
    }

    /**
     * @param staticResidueMods the staticModifications to set
     */
    public void setStaticModifications(List<? super MsResidueModificationDb> staticModifications) {
        this.staticResidueMods = staticModifications;
    }
    
    public void addStaticModification(MsResidueModificationDb mod) {
        staticResidueMods.add(mod);
    }

    //------------------------------------------------------------------------------------------------------
    // dynamic modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsResidueModificationDb> getDynamicModifications() {
        return (List<MsResidueModificationDb>) dynamicResidueMods;
    }

    /**
     * @param dynamicResidueMods the dynamicModifications to set
     */
    public void setDynamicModifications(List<? super MsResidueModificationDb> dynamicModifications) {
        this.dynamicResidueMods = dynamicModifications;
    }
    
    public void addDynamicModification(MsResidueModificationDb mod) {
        dynamicResidueMods.add(mod);
    }
    
    //------------------------------------------------------------------------------------------------------
    // terminal static modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsTerminalModificationDb> getTerminalStaticModifications() {
        return (List<MsTerminalModificationDb>) terminalStaticMods;
    }

    /**
     * @param staticResidueMods the staticModifications to set
     */
    public void setTerminalStaticModifications(List<? super MsTerminalModificationDb> termStaticMods) {
        this.terminalStaticMods = termStaticMods;
    }
    
    //------------------------------------------------------------------------------------------------------
    // dynamic modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsTerminalModificationDb> getTerminalDynamicModifications() {
        return (List<MsTerminalModificationDb>) terminalDynamicMods;
    }

    /**
     * @param dynamicResidueMods the dynamicModifications to set
     */
    public void setTerminalDynamicModifications(List<? super MsTerminalModificationDb> termDynaMods) {
        this.terminalDynamicMods = termDynaMods;
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
