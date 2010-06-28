package org.yeastrc.experiment.stats;

import org.yeastrc.experiment.File;
import org.yeastrc.www.util.RoundingUtils;

public class FileStats implements File, Comparable<FileStats>{

    private final String filename;
    private final int id;
    
    private int totalCount;
    private int goodCount;
    
    public FileStats(int id, String fileName) {
        this.id = id;
        this.filename = fileName;
    }
    
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }

    public double getPercentGoodCount() {
    	return RoundingUtils.getInstance().roundOne(((double)goodCount/(double)totalCount)*100.0);
    }
    
    public String getFileName() {
        return filename;
    }
    
    public int getId() {
        return id;
    }

    @Override
    public int compareTo(FileStats o) {
        return filename.compareTo(o.filename);
    }
}
