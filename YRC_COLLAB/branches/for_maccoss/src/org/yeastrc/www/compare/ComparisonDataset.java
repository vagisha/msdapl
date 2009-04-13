/**
 * ComparisonDataset.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ComparisonDataset {

    private List<Dataset> datasets;
    private List<ComparisonProtein> proteins;
    private int[][] proteinCounts;
    
    public ComparisonDataset() {
        this.datasets = new ArrayList<Dataset>();
        this.proteins = new ArrayList<ComparisonProtein>();
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public List<ComparisonProtein> getProteins() {
        return proteins;
    }
    
    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }
    
    public void addProtein(ComparisonProtein protein) {
        this.proteins.add(protein);
    }
    
    public int getDatasetCount() {
        return datasets.size();
    }
    
    public int getProteinCount() {
        return proteins.size();
    }
    
    private void initProteinCounts() {
        
        proteinCounts = new int[datasets.size()][datasets.size()];
        for(int i = 0; i < datasets.size(); i++) {
            for(int j = 0; j < datasets.size(); j++)
                proteinCounts[i][j]++;
        }
        
        for(ComparisonProtein protein: proteins) {
            
            for(int i = 0; i < datasets.size(); i++) {
                
                Dataset dsi = datasets.get(i);
                if(protein.isInDataset(dsi)) {
                
                    proteinCounts[i][i]++;
                    
                    for(int j = i+1; j < datasets.size(); j++) {

                        Dataset dsj = datasets.get(j);
                        if(protein.isInDataset(dsj)) {
                            proteinCounts[i][j]++;
                            proteinCounts[j][i]++;
                        }
                    }
                }
            }
        }
    }
    
    public int getProteinCount(int datasetIndex) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        return proteinCounts[datasetIndex][datasetIndex];
    }

    public int getCommonProteinCount(int datasetIndex1, int datasetIndex2) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        return proteinCounts[datasetIndex1][datasetIndex2];
    }
    

    public int getCommonProteinsPerc(int datasetIndex1, int datasetIndex2) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        
        int ds1Count = proteinCounts[datasetIndex1][datasetIndex2];
        int ds2Count = proteinCounts[datasetIndex2][datasetIndex2];
        if(ds1Count > ds2Count && ds1Count > 0) {
            return calculatePercent(proteinCounts[datasetIndex1][datasetIndex2], ds1Count);
        }
        else if(ds2Count > 0) {
            return calculatePercent(proteinCounts[datasetIndex1][datasetIndex2], ds2Count);
        }
        return 0;
    }
    
    private static int calculatePercent(int num1, int num2) {
        return (int)((num1*100.0)/num2);
    }
}
