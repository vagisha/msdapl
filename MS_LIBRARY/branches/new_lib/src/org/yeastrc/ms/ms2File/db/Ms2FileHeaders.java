/**
 * Ms2FileHeaders.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.ms2File.db;

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
    public String getIAnalyzer() {
        return iAnalyzer;
    }
    /**
     * @param analyzer the iAnalyzer to set
     */
    public void setIAnalyzer(String analyzer) {
        iAnalyzer = analyzer;
    }
    /**
     * @return the iAnalyzerVersion
     */
    public String getIAnalyzerVersion() {
        return iAnalyzerVersion;
    }
    /**
     * @param analyzerVersion the iAnalyzerVersion to set
     */
    public void setIAnalyzerVersion(String analyzerVersion) {
        iAnalyzerVersion = analyzerVersion;
    }
    /**
     * @return the iAnalyzerOptions
     */
    public String getIAnalyzerOptions() {
        return iAnalyzerOptions;
    }
    /**
     * @param analyzerOptions the iAnalyzerOptions to set
     */
    public void setIAnalyzerOptions(String analyzerOptions) {
        iAnalyzerOptions = analyzerOptions;
    }
    /**
     * @return the dAnalyzer
     */
    public String getDAnalyzer() {
        return dAnalyzer;
    }
    /**
     * @param analyzer the dAnalyzer to set
     */
    public void setDAnalyzer(String analyzer) {
        dAnalyzer = analyzer;
    }
    /**
     * @return the dAnalyzerVersion
     */
    public String getDAnalyzerVersion() {
        return dAnalyzerVersion;
    }
    /**
     * @param analyzerVersion the dAnalyzerVersion to set
     */
    public void setDAnalyzerVersion(String analyzerVersion) {
        dAnalyzerVersion = analyzerVersion;
    }
    /**
     * @return the dAnalyzerOptions
     */
    public String getDAnalyzerOptions() {
        return dAnalyzerOptions;
    }
    /**
     * @param analyzerOptions the dAnalyzerOptions to set
     */
    public void setDAnalyzerOptions(String analyzerOptions) {
        dAnalyzerOptions = analyzerOptions;
    }
}
