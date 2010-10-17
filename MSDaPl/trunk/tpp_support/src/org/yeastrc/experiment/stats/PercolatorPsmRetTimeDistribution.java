/**
 * PercolatorPsmRetTimeDistrubution.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */
package org.yeastrc.experiment.stats;

import java.util.List;

/**
 * 
 */
public class PercolatorPsmRetTimeDistribution {

	private List<FileStats> fileStatsList;
	private double binSize;
	private int numBins;
	private double scoreCutoff;
	private double maxRT;
	private int maxPsmCount;
	private int[] allPsmCounts;
    private int[] filteredPsmCounts;
	
	
	public List<FileStats> getFileStatsList() {
		return fileStatsList;
	}

	public void setFileStatsList(List<FileStats> fileStatsList) {
		this.fileStatsList = fileStatsList;
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

	public int getMaxPsmCount() {
        return this.maxPsmCount;
    }
	
	public void setMaxPsmCount(int maxPsmCount) {
		this.maxPsmCount = maxPsmCount;
	}
	
	public int[] getAllPsmDistribution() {
        return allPsmCounts;
    }
    
	public void setAllPsmCounts(int[] allPsmCounts) {
		this.allPsmCounts = allPsmCounts;
	}
	
	public int[] getFilteredPsmDistribution() {
        return filteredPsmCounts;
    }
	
	public void setFilteredPsmCounts(int[] filteredPsmCounts) {
		this.filteredPsmCounts = filteredPsmCounts;
	}

}
