/**
 * PercolatorFilteredSpectraResultsDAO.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */
package org.yeastrc.ms.dao.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredSpectraResult;

/**
 * 
 */
public interface PercolatorFilteredSpectraResultDAO {

	public double getAverageFilteredPercent();
	
	public double getStdevFilteredPercent();
	
	public double getAverageFilteredPercentForInstrument(int instrumentId);
	
	public double getStdevFilteredPercentForInstrument(int instrumentId);
	
	public PercolatorFilteredSpectraResult load(int runSearchAnalysisId);
	
	public List<PercolatorFilteredSpectraResult> loadForAnalysis(int searchAnalysisId);
	
	public void save(PercolatorFilteredSpectraResult result);
	
	public void delete(int runSearchAnalysisId);
}
