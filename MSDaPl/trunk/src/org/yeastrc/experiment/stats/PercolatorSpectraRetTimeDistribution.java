/**
 * PercolatorSpectraRetTimeDistribution.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */
package org.yeastrc.experiment.stats;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 */
public class PercolatorSpectraRetTimeDistribution {

	private List<FileStats> fileStatsList;
	private double binSize;
	private int numBins;
	private double scoreCutoff;
	private double maxRT;
	private int maxSpectraCount;
	private int[] allSpectraCounts;
    private int[] filteredSpectraCounts;
	
	
	public List<FileStats> getFileStatsList() {
		Collections.sort(fileStatsList, new Comparator<FileStats>() {
			@Override
			public int compare(FileStats o1, FileStats o2) {
				return o1.getFileName().compareTo(o2.getFileName());
			}
		});
		return fileStatsList;
	}

	public void setFileStatsList(List<FileStats> fileStatsList) {
		this.fileStatsList = fileStatsList;
	}

	public String getGoogleChartUrl() {
		 return DistributionPlotter.getInstance().plotGoogleChartForScan_RTDistribution(this);
	}
	
	public double getScoreCutoff() {
		return scoreCutoff;
	}

	public void setScoreCutoff(double scoreCutoff) {
		this.scoreCutoff = scoreCutoff;
	}

	public double getBinSize() {
        return binSize;
    }
	
	public void setBinSize(double binSize) {
		this.binSize = binSize;
	}

	public int getNumBins() {
        return numBins;
    }
	
	public void setNumBins(int numBins) {
		this.numBins = numBins;
	}

	public double getMaxRT() {
        return this.maxRT;
    }
	
	public void setMaxRT(double maxRT) {
		this.maxRT = maxRT;
	}

	public int getMaxSpectraCount() {
        return this.maxSpectraCount;
    }
	
	public void setMaxSpectraCount(int maxSpectraCount) {
		this.maxSpectraCount = maxSpectraCount;
	}
	
	public int[] getAllSpectraDistribution() {
        return allSpectraCounts;
    }
    
	public void setAllSpectraCounts(int[] allSpectraCounts) {
		this.allSpectraCounts = allSpectraCounts;
	}
	
	public int[] getFilteredSpectraDistribution() {
        return filteredSpectraCounts;
    }
	
	public void setFilteredSpectraCounts(int[] filteredSpectraCounts) {
		this.filteredSpectraCounts = filteredSpectraCounts;
	}

}
