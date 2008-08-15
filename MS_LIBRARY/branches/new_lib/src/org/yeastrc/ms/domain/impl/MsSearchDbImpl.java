/**
 * Experiment.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.impl;

import java.sql.Date;

import org.yeastrc.ms.domain.search.MsSearchDb;

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
    
   
}
