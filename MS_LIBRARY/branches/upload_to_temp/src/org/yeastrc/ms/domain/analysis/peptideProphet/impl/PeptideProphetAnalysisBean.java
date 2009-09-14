/**
 * PeptideProphetAnalysisBean.java
 * @author Vagisha Sharma
 * Sep 11, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet.impl;

import org.yeastrc.ms.domain.analysis.impl.SearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetAnalysis;

/**
 * 
 */
public class PeptideProphetAnalysisBean extends SearchAnalysisBean implements PeptideProphetAnalysis {

    private String fileName;
    
    @Override
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
