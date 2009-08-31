/**
 * SearchAnalysis.java
 * @author Vagisha Sharma
 * Apr 2, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.sql.Date;
import java.util.List;

import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.Program;

/**
 * 
 */
public class SearchAnalysis implements MsSearchAnalysis {

    
    private final MsSearchAnalysis analysis;
    private List<AnalysisFile> files;
    
    public SearchAnalysis(MsSearchAnalysis analysis) {
        this.analysis = analysis;
    }
    
    public void setFiles(List<AnalysisFile> files) {
        this.files = files;
    }
    
    public List<AnalysisFile> getFiles() {
        return files;
    }
    
    @Override
    public Program getAnalysisProgram() {
        return analysis.getAnalysisProgram();
    }

    @Override
    public String getAnalysisProgramVersion() {
        return analysis.getAnalysisProgramVersion();
    }

    @Override
    public int getId() {
        return analysis.getId();
    }

    @Override
    public Date getUploadDate() {
        return analysis.getUploadDate();
    }

}