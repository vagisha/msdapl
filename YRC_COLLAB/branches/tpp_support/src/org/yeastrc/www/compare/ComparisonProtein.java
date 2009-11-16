/**
 * ComparisonProtein.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;

/**
 * 
 */
public class ComparisonProtein {

    private final int nrseqId;
    private int groupId;
    private String fastaName;
    private String commonName;
    private String description;
    private int maxPeptideCount;
    
    private List<DatasetProteinInformation> datasetInfo;
    
    public ComparisonProtein(int nrseqId) {
        this.nrseqId = nrseqId;
        datasetInfo = new ArrayList<DatasetProteinInformation>();
    }
    
    public int getNrseqId() {
        return nrseqId;
    }

    public List<DatasetProteinInformation> getDatasetInfo() {
        return datasetInfo;
    }
    
    public void setDatasetInformation(List<DatasetProteinInformation> infoList) {
        this.datasetInfo = infoList;
    }
    
    public void addDatasetInformation(DatasetProteinInformation info) {
        datasetInfo.add(info);
    }
    
    public DatasetProteinInformation getDatasetProteinInformation(Dataset dataset) {
        
        for(DatasetProteinInformation dsInfo: datasetInfo) {
            if(dataset.equals(dsInfo.getDataset())) {
                return dsInfo;
            }
        }
        return null;
    }
    
    public boolean isInDataset(Dataset dataset) {
        DatasetProteinInformation dpi = getDatasetProteinInformation(dataset);
        if(dpi != null)
            return dpi.isPresent();
        return false;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String name) {
        this.commonName = name;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxPeptideCount() {
        return maxPeptideCount;
    }

    public void setMaxPeptideCount(int maxPeptideCount) {
        this.maxPeptideCount = maxPeptideCount;
    }

    public String getFastaName() {
        return fastaName;
    }

    public void setFastaName(String systematicName) {
        this.fastaName = systematicName;
    }
    
    public boolean isParsimonious() {
        for(DatasetProteinInformation dpi: this.datasetInfo) {
            if(dpi.isParsimonious())
                return true;
        }
        return false;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
