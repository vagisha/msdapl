/**
 * MsRunSearchAnalysisDAO.java
 * @author Vagisha Sharma
 * Dec 29, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis;

import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;

/**
 * 
 */
public interface MsRunSearchAnalysisUploadDAO {

    public abstract int save(MsRunSearchAnalysis runSearchAnalysis);
    
}
