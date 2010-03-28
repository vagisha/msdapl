/**
 * SearchAnalysis.java
 * @author Vagisha Sharma
 * Apr 2, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.sql.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.Program;


/**
 * 
 */
public class SearchAnalysis implements MsSearchAnalysis {

    
    private final MsSearchAnalysis analysis;
    private String analysisName;
    private List<AnalysisFile> files;
    
    private static final Pattern tppVersionPattern = Pattern.compile("(TPP\\s+v\\d+\\.\\d+)");
    
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
    
    public String getAnalysisProgramVersionShort() {
    	String version = getAnalysisProgramVersion();
    	
    	if(analysis.getAnalysisProgram() == Program.PEPTIDE_PROPHET) {
    		Matcher m = tppVersionPattern.matcher(version);
    		if(m.find()) {
    			version = m.group();
    		}
    	}
    	return version;
    }

    @Override
    public int getId() {
        return analysis.getId();
    }

    @Override
    public Date getUploadDate() {
        return analysis.getUploadDate();
    }

    public String getAnalysisName() {
        return analysisName;
    }

    public void setAnalysisName(String analysisName) {
        this.analysisName = analysisName;
    }

    @Override
    public void setId(int analysisId) {
        throw new UnsupportedOperationException();
    }

}
