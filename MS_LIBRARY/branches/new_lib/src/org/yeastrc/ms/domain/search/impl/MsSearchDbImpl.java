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

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearchDatabaseDb;
import org.yeastrc.ms.domain.search.MsSearchDb;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.SearchProgram;

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
    private SearchProgram analysisProgram;
    private String analysisProgramVersion;
    
    private List<? super MsSearchDatabaseDb> searchDatabases;
    
    private List<? super MsResidueModification> staticResidueMods;
    
    private List<? super MsResidueModification> dynamicResidueMods;
    
    private List<? super MsTerminalModification> staticTerminalMods;
    
    private List<? super MsTerminalModification> dynamicTerminalMods;
    
    private List<? super MsEnzyme> enzymes;
    
    public MsSearchDbImpl() {
        searchDatabases = new ArrayList<MsSearchDatabaseDb>();
        staticResidueMods = new ArrayList<MsResidueModification>();
        dynamicResidueMods = new ArrayList<MsResidueModification>();
        staticTerminalMods = new ArrayList<MsTerminalModification>();
        dynamicTerminalMods = new ArrayList<MsTerminalModification>();
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

    public SearchProgram getSearchProgram() {
        return analysisProgram;
    }
    
    public void setSearchProgram(SearchProgram program) {
        this.analysisProgram = program;
    }
    
    public String getSearchProgramVersion() {
        return analysisProgramVersion;
    }
    
    public void setSearchProgramVersion(String programVersion) {
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
    // static residue modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsResidueModification> getStaticResidueMods() {
        return (List<MsResidueModification>) staticResidueMods;
    }

    /**
     * @param staticResidueMods the staticModifications to set
     */
    public void setStaticResidueMods(List<? super MsResidueModification> staticModifications) {
        this.staticResidueMods = staticModifications;
    }
    
    public void addStaticResidueModification(MsResidueModification mod) {
        staticResidueMods.add(mod);
    }

    //------------------------------------------------------------------------------------------------------
    // dynamic residue modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsResidueModification> getDynamicResidueMods() {
        return (List<MsResidueModification>) dynamicResidueMods;
    }

    /**
     * @param dynamicResidueMods the dynamicModifications to set
     */
    public void setDynamicResidueMods(List<? super MsResidueModification> dynamicModifications) {
        this.dynamicResidueMods = dynamicModifications;
    }
    
    public void addDynamicResidueModification(MsResidueModification mod) {
        dynamicResidueMods.add(mod);
    }
    
    //------------------------------------------------------------------------------------------------------
    // static terminal modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsTerminalModification> getStaticTerminalMods() {
        return (List<MsTerminalModification>) staticTerminalMods;
    }

    /**
     * @param staticResidueMods the staticModifications to set
     */
    public void setStaticTerminalMods(List<? super MsTerminalModification> termStaticMods) {
        this.staticTerminalMods = termStaticMods;
    }
    
    //------------------------------------------------------------------------------------------------------
    // dynamic terminal modifications used for the search
    //------------------------------------------------------------------------------------------------------
    public List<MsTerminalModification> getDynamicTerminalMods() {
        return (List<MsTerminalModification>) dynamicTerminalMods;
    }

    /**
     * @param dynamicResidueMods the dynamicModifications to set
     */
    public void setDynamicTerminalMods(List<? super MsTerminalModification> termDynaMods) {
        this.dynamicTerminalMods = termDynaMods;
    }

    //------------------------------------------------------------------------------------------------------
    // enzymes used for the search
    //------------------------------------------------------------------------------------------------------
    @Override
    public List<MsEnzyme> getEnzymeList() {
        return (List<MsEnzyme>) enzymes;
    }
    
    public void addEnzyme(MsEnzyme enzyme) {
        enzymes.add(enzyme);
    }
    
    public void setEnzymeList(List<? super MsEnzyme> enzymes) {
        this.enzymes = enzymes;
    }
}
