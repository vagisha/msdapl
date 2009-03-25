package org.yeastrc.ms.domain.analysis.impl;

import java.sql.Date;

import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.Program;

public class SearchAnalysisBean implements MsSearchAnalysis {

    private int id;
//    private int searchId;
    private Date uploadDate;
    private Program analysisProgram;
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
    
//    public int getSearchId() {
//        return this.searchId;
//    }
//    
//    public void setSearchId(int searchId) {
//        this.searchId = searchId;
//    }
    
    public Program getAnalysisProgram() {
        return analysisProgram;
    }
    
    public void setAnalysisProgram(Program program) {
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
