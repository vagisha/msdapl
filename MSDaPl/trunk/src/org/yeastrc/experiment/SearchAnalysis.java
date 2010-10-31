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

import org.yeastrc.jobqueue.MsAnalysisUploadJob;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.yates.YatesRun;


/**
 * 
 */
public class SearchAnalysis implements MsSearchAnalysis {

    
    private final MsSearchAnalysis analysis;
    private String analysisName;
    private List<AnalysisFile> files;
    private MsAnalysisUploadJob job;
    
    private YatesRun dtaSelect;
    private List<ExperimentProteinProphetRun> prophetRuns;
    private List<ExperimentProteinferRun> protInferRuns;
    
    private static final Pattern tppVersionPattern = Pattern.compile("TPP\\s+(v\\d+\\.\\d+)");
    
    public SearchAnalysis(MsSearchAnalysis analysis) {
        this.analysis = analysis;
    }
    
    public void setJob(MsAnalysisUploadJob job) {
    	this.job = job;
    }
    
    public MsAnalysisUploadJob getJob() {
    	return job;
    }
    
    public boolean hasJob() {
    	return job != null;
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
    			version = m.group(1);
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
    
    public boolean isComplete() {
        return this.job == null || this.job.isComplete();
    }
    
    public List<ExperimentProteinferRun> getProtInferRuns() {
        return protInferRuns;
    }

    public void setProtInferRuns(List<ExperimentProteinferRun> protInferRuns) {
        this.protInferRuns = protInferRuns;
    }
    
    public boolean getHasProtInferResults() {
        return dtaSelect != null || 
        (protInferRuns != null && protInferRuns.size() > 0) ||
        (prophetRuns != null && prophetRuns.size() > 0);
    }
    
    public YatesRun getDtaSelect() {
        return dtaSelect;
    }

    public void setDtaSelect(YatesRun dtaSelect) {
        this.dtaSelect = dtaSelect;
    }

    public List<ExperimentProteinProphetRun> getProteinProphetRuns() {
        return prophetRuns;
    }
    
    public void setProteinProphetRun(List<ExperimentProteinProphetRun> runs) {
        this.prophetRuns = runs;
    }

	@Override
	public String getComments() {
		return this.analysis.getComments();
	}

	@Override
	public void setComments(String comments) {
		throw new UnsupportedOperationException();
	}

}
