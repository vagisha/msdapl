/**
 * DataSetInformation.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.dataset;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * 
 */
public class DatasetProteinInformation implements Serializable {

    private boolean present;
    private boolean parsimonious;
    private boolean grouped;
    private int sequenceCount;
    // private int uniqSeqCount;
    private int ionCount;
    private int uniqIonCount;
    private int spectrumCount;
    private double nsaf = -1.0;
    private final Dataset dataset;
    
    private static final DecimalFormat df = new DecimalFormat("0.000000");
    
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

    public int getSequenceCount() {
    	return sequenceCount;
    }
    
//    public int getUniqueSequenceCount() {
//    	return uniqSeqCount;
//    }
    
    public int getIonCount() {
    	return ionCount;
    }
    
    public int getUniqueIonCount() {
    	return uniqIonCount;
    }
    
    public int getSpectrumCount() {
        return spectrumCount;
    }

    public float getNormalizedSpectrumCount() {
    	if(this.isPresent())
    		return spectrumCount * dataset.getSpectrumCountNormalizationFactor();
    	else
    		return 0;
    }
    
    public int getNormalizedSpectrumCountRounded() {
        return Math.round(getNormalizedSpectrumCount());
    }
    
    public void setSpectrumCount(int spectrumCount) {
        this.spectrumCount = spectrumCount;
    }
    
    public void setSequenceCount(int sequenceCount) {
        this.sequenceCount = sequenceCount;
    }
    
//    public void setUniqueSequenceCount(int sequenceCount) {
//        this.uniqSeqCount = sequenceCount;
//    }
    
    public void setIonCount(int ionCount) {
        this.ionCount = ionCount;
    }
    
    public void setUniqueIonCount(int ionCount) {
        this.uniqIonCount = ionCount;
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
        return df.format(nsaf);
    }
}
