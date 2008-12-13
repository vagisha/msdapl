package org.yeastrc.ms.domain.analysis.impl;

import java.sql.Date;

import org.yeastrc.ms.domain.analysis.MsPostSearchAnalysis;
import org.yeastrc.ms.domain.search.SearchProgram;

public class PostSearchAnalysisBean implements MsPostSearchAnalysis {

    private int id;
    private int searchId;
    private Date uploadDate;
    private String serverDirectory;
    private SearchProgram analysisProgram;
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
    
    public int getSearchId() {
        return this.searchId;
    }
    
    public void setSearchId(int searchId) {
        this.searchId = searchId;
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

    public SearchProgram getAnalysisProgram() {
        return analysisProgram;
    }
    
    public void setAnalysisProgram(SearchProgram program) {
        this.analysisProgram = program;
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
}
