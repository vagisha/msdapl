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

    private final int datasetId;
    private final DatasetSource source;
    
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
}
