/**
 * Ms2FileChargeIndependentAnalysisDAO.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ms2File;

import java.util.List;

import org.yeastrc.ms.ms2File.Ms2FileChargeIndependentAnalysis;

/**
 * 
 */
public interface Ms2FileChargeIndependentAnalysisDAO {

    public abstract void save(Ms2FileChargeIndependentAnalysis analysis);

    public abstract List<Ms2FileChargeIndependentAnalysis> loadAnalysisForScan(int scanId);
}
