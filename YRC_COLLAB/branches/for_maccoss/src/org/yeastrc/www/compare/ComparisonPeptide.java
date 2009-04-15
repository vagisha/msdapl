/**
 * ComparisonPeptide.java
 * @author Vagisha Sharma
 * Apr 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ComparisonPeptide {

    private int nrseqProteinId;
    private String sequence;
    
    List<DatasetPeptideInformation> datasetInfo;
    
    public ComparisonPeptide(int nrseqId, String sequence) {
        this.nrseqProteinId = nrseqId;
        this.sequence = sequence;
        datasetInfo = new ArrayList<DatasetPeptideInformation>();
    }
    
    public int getNrseqId() {
        return nrseqProteinId;
    }

    public List<DatasetPeptideInformation> getDatasetInfo() {
        return datasetInfo;
    }
    
    public void setDatasetInformation(List<DatasetPeptideInformation> infoList) {
        this.datasetInfo = infoList;
    }
    
    public void addDatasetInformation(DatasetPeptideInformation info) {
        datasetInfo.add(info);
    }
    
    public DatasetPeptideInformation getDatasetPeptideInformation(Dataset dataset) {
        
        for(DatasetPeptideInformation dsInfo: datasetInfo) {
            if(dataset.equals(dsInfo.getDataset())) {
                return dsInfo;
            }
        }
        return null;
    }
    
    public boolean isInDataset(Dataset dataset) {
        DatasetPeptideInformation dpi = getDatasetPeptideInformation(dataset);
        if(dpi != null)
            return dpi.isPresent();
        return false;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

}
