/**
 * DataSetInformation.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.text.DecimalFormat;

/**
 * 
 */
public class DatasetProteinInformation {

    private boolean present;
    private boolean parsimonious;
    private boolean grouped;
    private int spectrumCount;
    private double nsaf = -1.0;
    private final Dataset dataset;
    
    public DatasetProteinInformation(Dataset dataset) {
        this.dataset = dataset;
    }
    
    public Dataset getDataset() {
        return dataset;
    }
    
    public int getDatasetId() {
        return dataset.getDatasetId();
    }
    
    public DatasetSource getDatasetSource() {
        return dataset.getSource();
    }
    
    public boolean isPresent() {
        return present;
    }
    public void setPresent(boolean present) {
        this.present = present;
    }
    public boolean isParsimonious() {
        return parsimonious;
    }
    public void setParsimonious(boolean parsimonious) {
        this.parsimonious = parsimonious;
    }

    public int getSpectrumCount() {
        return spectrumCount;
    }

    public float getNormalizedSpectrumCount() {
        return spectrumCount * dataset.getSpectrumCountNormalizationFactor();
    }
    public void setSpectrumCount(int spectrumCount) {
        this.spectrumCount = spectrumCount;
    }

    public boolean isGrouped() {
        return grouped;
    }

    public void setGrouped(boolean grouped) {
        this.grouped = grouped;
    }

    public double getNsaf() {
        return nsaf;
    }

    public void setNsaf(double nsaf) {
        this.nsaf = nsaf;
    }
    
    public String getNsafFormatted() {
        String format = "0.000000";
        DecimalFormat df = new DecimalFormat(format);
        return df.format(nsaf);
    }
}
