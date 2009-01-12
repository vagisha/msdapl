package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.List;

public class ProteinInferInputSummary {

    private int searchId;
    private String searchProgram;
    private String searchProgramVersion;
    
    private int searchAnalysisId;
    private String analysisProgram;
    private String analysisProgramVersion;
    
    private String searchDatabase;
    private List<ProteinInferIputFile> files;
    
    public ProteinInferInputSummary() {
        files = new ArrayList<ProteinInferIputFile>();
    }
    
    public List<ProteinInferIputFile> getInputFiles() {
        return files;
    }
    
    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public void setInputFiles(List<ProteinInferIputFile> files) {
        this.files = files;
    }
    
    // to be used by struts indexed properties
    public ProteinInferIputFile getInputFile(int index) {
        while(index >= files.size())
            files.add(new ProteinInferIputFile());
        return files.get(index);
    }
    
    public void addRunSearch(ProteinInferIputFile runSearch) {
        files.add(runSearch);
    }
    
    public String getSearchProgram() {
        return searchProgram;
    }

    public void setSearchProgram(String searchProgram) {
        this.searchProgram = searchProgram;
    }
    
    public String getSearchProgramVersion() {
        return searchProgramVersion;
    }

    public void setSearchProgramVersion(String searchProgramVersion) {
        this.searchProgramVersion = searchProgramVersion;
    }

    
    public int getSearchAnalysisId() {
        return searchAnalysisId;
    }

    public void setSearchAnalysisId(int searchAnalysisId) {
        this.searchAnalysisId = searchAnalysisId;
    }
    
    public String getAnalysisProgram() {
        return analysisProgram;
    }

    public void setAnalysisProgram(String analysisProgram) {
        this.analysisProgram = analysisProgram;
    }

    public String getAnalysisProgramVersion() {
        return analysisProgramVersion;
    }

    public void setAnalysisProgramVersion(String analysisProgramVersion) {
        this.analysisProgramVersion = analysisProgramVersion;
    }

    public String getSearchDatabase() {
        return searchDatabase;
    }

    public void setSearchDatabase(String searchDatabase) {
        this.searchDatabase = searchDatabase;
    }
    
    
    public static final class ProteinInferIputFile {
        private int inputId; // could be runSearchID or runSearchAnalysisID
        private String runName;
        private boolean selected = false;
        
        public ProteinInferIputFile() {}
        
        public ProteinInferIputFile(int inputId, String runName) {
            this.inputId = inputId;
            this.runName = runName;
        }
        
        public void setInputId(int inputId) {
            this.inputId = inputId;
        }

        public void setRunName(String runName) {
            this.runName = runName;
        }

        public boolean getIsSelected() {
            return selected;
        }

        public void setIsSelected(boolean selected) {
            this.selected = selected;
        }

        public int getInputId() {
            return inputId;
        }

        public String getRunName() {
            return runName;
        }
    }
}
