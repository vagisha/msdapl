/**
 * Dataset.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

/**
 * 
 */
public class Dataset {

    private int datasetId;
    private DatasetSource source;
//    private int spectrumCount;
//    private int maxPeptideSpectrumCount;
    
    public Dataset() {}
    
    public Dataset(int datasetId, DatasetSource source) {
        this.datasetId = datasetId;
        this.source = source;
    }
    
    public int getDatasetId() {
        return datasetId;
    }
    
    public DatasetSource getSource() {
        return source;
    }
    public String getSourceString() {
        return source.name();
    }
    
    
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(!(o instanceof Dataset))
            return false;
        
        Dataset that = (Dataset)o;
        return (this.datasetId == that.datasetId && this.source == that.source);
    }
    
    public int hashCode() {
        return source.hashCode() + Integer.valueOf(datasetId).hashCode();
    }

    public void setDatasetId(int datasetId) {
        this.datasetId = datasetId;
    }

    public void setSource(DatasetSource source) {
        this.source = source;
    }
    
    public void setSourceString(String sourceStr) {
        this.source = DatasetSource.instance(sourceStr);
    }

//    public int getSpectrumCount() {
//        return spectrumCount;
//    }
//
//    public void setSpectrumCount(int spectrumCount) {
//        this.spectrumCount = spectrumCount;
//    }
//
//    public int getMaxPeptideSpectrumCount() {
//        return maxPeptideSpectrumCount;
//    }
//
//    public void setMaxPeptideSpectrumCount(int maxPeptideSpectrumCount) {
//        this.maxPeptideSpectrumCount = maxPeptideSpectrumCount;
//    }
}
