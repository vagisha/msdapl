/**
 * PercolatorPsmFilteredResultDAO.java
 * @author Vagisha Sharma
 * Sep 29, 2010
 */
package org.yeastrc.ms.dao.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredPsmResult;

/**
 * 
 */
public interface PercolatorFilteredPsmResultDAO {

	public double getAverageFilteredPercent();
	
	public double getStdevFilteredPercent();
	
	public double getAverageFilteredPercentForInstrument(int instrumentId);
	
	public double getStdevFilteredPercentForInstrument(int instrumentId);
	
	public PercolatorFilteredPsmResult load(int runSearchAnalysisId);
	
	public List<PercolatorFilteredPsmResult> loadForAnalysis(int searchAnalysisId);
	
	public void save(PercolatorFilteredPsmResult result);
	
	public void delete(int runSearchAnalysisId);
}
