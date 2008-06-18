/**
 * Ms2FileHeaders.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto.ms2File;

/**
 * 
 */
public class Ms2FileHeaders {

    private int runId;                  // id (database) of the run
    
    private String iAnalyzer;           // software used to conduct charge-state-independent analysis of spectra
    private String iAnalyzerVersion;    // version number of the iAnalyzer
    private String iAnalyzerOptions;    // options used for the iAnalyzer
    
    private String dAnalyzer;           // software used to conduct charge-state-dependent analysis of spectra
    private String dAnalyzerVersion;    // version number of the dAnalyzer
    private String dAnalyzerOptions;    // options used for the dAnalyzer
    
    /**
     * @return the runId
     */
    public int getRunId() {
        return runId;
    }
    /**
     * @param runId the runId to set
     */
    public void setRunId(int runId) {
        this.runId = runId;
    }
    /**
     * @return the iAnalyzer
     */
    public String getiAnalyzer() {
        return iAnalyzer;
    }
    /**
     * @param analyzer the iAnalyzer to set
     */
    public void setiAnalyzer(String analyzer) {
        iAnalyzer = analyzer;
    }
    /**
     * @return the iAnalyzerVersion
     */
    public String getiAnalyzerVersion() {
        return iAnalyzerVersion;
    }
    /**
     * @param analyzerVersion the iAnalyzerVersion to set
     */
    public void setiAnalyzerVersion(String analyzerVersion) {
        iAnalyzerVersion = analyzerVersion;
    }
    /**
     * @return the iAnalyzerOptions
     */
    public String getiAnalyzerOptions() {
        return iAnalyzerOptions;
    }
    /**
     * @param analyzerOptions the iAnalyzerOptions to set
     */
    public void setiAnalyzerOptions(String analyzerOptions) {
        iAnalyzerOptions = analyzerOptions;
    }
    /**
     * @return the dAnalyzer
     */
    public String getdAnalyzer() {
        return dAnalyzer;
    }
    /**
     * @param analyzer the dAnalyzer to set
     */
    public void setdAnalyzer(String analyzer) {
        dAnalyzer = analyzer;
    }
    /**
     * @return the dAnalyzerVersion
     */
    public String getdAnalyzerVersion() {
        return dAnalyzerVersion;
    }
    /**
     * @param analyzerVersion the dAnalyzerVersion to set
     */
    public void setdAnalyzerVersion(String analyzerVersion) {
        dAnalyzerVersion = analyzerVersion;
    }
    /**
     * @return the dAnalyzerOptions
     */
    public String getdAnalyzerOptions() {
        return dAnalyzerOptions;
    }
    /**
     * @param analyzerOptions the dAnalyzerOptions to set
     */
    public void setdAnalyzerOptions(String analyzerOptions) {
        dAnalyzerOptions = analyzerOptions;
    }
}
