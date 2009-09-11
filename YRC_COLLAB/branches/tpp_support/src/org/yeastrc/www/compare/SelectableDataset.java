/**
 * SelectableDataset.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

/**
 * 
 */
public class SelectableDataset {

    private boolean selected = false;
    private int datasetId;
    private DatasetSource source;
    private String datasetComments;
    private int datasetIndex;

   
    public SelectableDataset() {}
    
    public SelectableDataset(Dataset dataset) {
        this.datasetId = dataset.getDatasetId();
        this.source = dataset.getSource();
        this.datasetComments = dataset.getDatasetComments();
//        super(dataset.getDatasetId(), dataset.getSource());
    }
    
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public int getDatasetId() {
        return datasetId;
    }
    
    public void setDatasetId(int datasetId) {
        this.datasetId = datasetId;
    }
    
    public DatasetSource getSource() {
        return source;
    }
    
    public String getSourceString() {
        return source.name();
    }
    
    public void setSource(DatasetSource source) {
        this.source = source;
    }
    
    public void setSourceString(String sourceStr) {
        this.source = DatasetSource.instance(sourceStr);
    }
    
    public int getRed() {
        return DatasetColor.get(this.datasetIndex).R;
    }
    
    public int getGreen() {
        return DatasetColor.get(this.datasetIndex).G;
    }
    
    public int getBlue() {
        return DatasetColor.get(this.datasetIndex).B;
    }

    public String getDatasetComments() {
        return datasetComments;
    }

    public void setDatasetComments(String datasetComments) {
        this.datasetComments = datasetComments;
    }

    public int getDatasetIndex() {
        return datasetIndex;
    }

    public void setDatasetIndex(int datasetIndex) {
        this.datasetIndex = datasetIndex;
    }
}
