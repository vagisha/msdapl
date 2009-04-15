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
public class SelectableDataset extends Dataset {

    private boolean selected = false;

    public SelectableDataset() {}
    
    public SelectableDataset(Dataset dataset) {
        super(dataset.getDatasetId(), dataset.getSource());
    }
    
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
