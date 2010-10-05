/**
 * PercolatorSpectraRetTimeDistributionGetter.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */
package org.yeastrc.experiment.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredSpectraResultDAO;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorBinnedSpectraResult;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredSpectraResult;
import org.yeastrc.ms.service.percolator.stats.PercolatorFilteredSpectraDistributionCalculator;

/**
 * 
 */
public class PercolatorSpectraRetTimeDistributionGetter {

	private int analysisId;
	private double scoreCutoff;

	private static final Logger log = Logger.getLogger(PercolatorSpectraRetTimeDistributionGetter.class);
	
	public PercolatorSpectraRetTimeDistributionGetter(int analysisId, double scoreCutoff) {
        this.analysisId = analysisId;
        this.scoreCutoff = scoreCutoff;
    }
	
	public PercolatorSpectraRetTimeDistribution getDistribution() {
		
		// Look in the database first for pre-calculated results
		PercolatorFilteredSpectraResultDAO dao = DAOFactory.instance().getPrecolatorFilteredSpectraResultDAO();
		List<PercolatorFilteredSpectraResult> filteredResults = dao.loadForAnalysis(analysisId);
		
		if(filteredResults == null || filteredResults.size() == 0) {
			
			// Results were not found in the database; calculate now
			PercolatorFilteredSpectraDistributionCalculator calc = new PercolatorFilteredSpectraDistributionCalculator(analysisId, scoreCutoff);
			filteredResults = calc.getFilteredResults();
		}
		
		if(filteredResults == null || filteredResults.size() == 0) {
			log.error("No results for searchAnalysisID: "+analysisId);
			return null;
		}
		
		int[] allSpectraCounts = null;
	    int[] filteredSpectraCounts = null;
	    
	    int maxPsmCount = 0;
	    double maxRt = 0;
	    
	    List<FileStats> fileStats = new ArrayList<FileStats>(filteredResults.size());
	    MsRunSearchAnalysisDAO rsaDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
	    
	    maxRt = getMaxRt(filteredResults.get(0).getBinnedResults());
	    int binIncr = getBinIncrement(maxRt);
	    int numBins = (int)Math.ceil(maxRt / binIncr);
	    
		for(PercolatorFilteredSpectraResult res: filteredResults) {
			
			int runSearchAnalysisId = res.getRunSearchAnalysisId();
			String filename = rsaDao.loadFilenameForRunSearchAnalysis(runSearchAnalysisId);
			FileStats stats = new FileStats(res.getRunSearchAnalysisId(), filename);
			stats.setGoodCount(res.getFiltered());
			stats.setTotalCount(res.getTotal());
			fileStats.add(stats);
			
			List<PercolatorBinnedSpectraResult> binnedResults = res.getBinnedResults();
			Collections.sort(binnedResults, new Comparator<PercolatorBinnedSpectraResult>() {
				@Override
				public int compare(PercolatorBinnedSpectraResult o1,PercolatorBinnedSpectraResult o2) {
					return Double.valueOf(o1.getBinStart()).compareTo(o2.getBinStart());
				}
			});
			
			if(allSpectraCounts == null)
				allSpectraCounts = new int[numBins];
			if(filteredSpectraCounts == null)
				filteredSpectraCounts = new int[numBins];
			
			maxRt = Math.max(maxRt, binnedResults.get(binnedResults.size() - 1).getBinEnd());
			
			int idx = 0;
			for(int j = 0; j < binnedResults.size(); j++) {
				PercolatorBinnedSpectraResult binned = binnedResults.get(j);
				allSpectraCounts[idx] += binned.getTotal();
				filteredSpectraCounts[idx] += binned.getFiltered();
				maxPsmCount = Math.max(allSpectraCounts[idx], maxPsmCount);
				if(j > 0 && j % binIncr == 0)
					idx++;
			}
		}
		
		PercolatorSpectraRetTimeDistribution distribution = new PercolatorSpectraRetTimeDistribution();
		distribution.setScoreCutoff(this.scoreCutoff);
		distribution.setNumBins(allSpectraCounts.length);
		distribution.setMaxRT(maxRt);
		distribution.setMaxSpectraCount(maxPsmCount);
		distribution.setFilteredSpectraCounts(filteredSpectraCounts);
		distribution.setAllSpectraCounts(allSpectraCounts);
		distribution.setBinSize(binIncr);
		distribution.setFileStatsList(fileStats);
		
		return distribution;
		
	}

	private int getBinIncrement(double maxRt) {
		
		// PercolatorFilteredPsmDistributionCalculator bins results in 1 unit RT bins.
		// We will re-bin them if the number of bins is too large
		int numBins = (int)Math.ceil(maxRt);
		// get a number closest to 50
		int diff = Math.abs(numBins - 50);
		
		int incr = 1;
		for(int i = 2; ; i++) {
			int nb = numBins / i;
			int d = Math.abs(nb - 50);
			if(d < diff) {
				diff = d;
				incr = i;
			}
			else
				break;
		}
		
		return incr;
	}

	private double getMaxRt(List<PercolatorBinnedSpectraResult> binnedResults) {
		
		double max = 0;
		for(PercolatorBinnedSpectraResult res: binnedResults) {
			max = Math.max(max, res.getBinEnd());
		}
		return max;
	}
}
