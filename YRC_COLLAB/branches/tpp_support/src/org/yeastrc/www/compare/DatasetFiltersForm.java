/**
 * DatasetFiltersForm.java
 * @author Vagisha Sharma
 * Sep 7, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.SelectableDataset;

/**
 * 
 */
public class DatasetFiltersForm extends ActionForm {

    private List<SelectableDataset> andList = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> orList  = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> notList = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> xorList = new ArrayList<SelectableDataset>();
    
    private int parsimoniousParam = ProteinDatasetComparer.PARSIM_ONE; // parsimonious in at least one
    
    private boolean groupIndistinguishableProteins = false;
    
    private String minCoverage = "0.0";
    private String maxCoverage = "100.0";
    private String minPeptides = "1";
    private String maxPeptides;
    private String minUniquePeptides = "0";
    private String maxUniquePeptides;
    private String minSpectrumMatches = "1";
    private String maxSpectrumMatches;
    
    private String accessionLike = null;
    private String descriptionLike = null;
    private String[] validationStatus = new String[]{"All"};

    private boolean hasProteinProphetDatasets = false;
    private String errorRate = "0.01";
    private boolean useProteinGroupProbability = false;
    
    public void reset() {
        minCoverage = "0.0";
        minPeptides = "1";
        minUniquePeptides = "0";
        minSpectrumMatches = "1";
        
        accessionLike = null;
        descriptionLike = null;
        errorRate = "0.01";
        
        groupIndistinguishableProteins = false;
        useProteinGroupProbability = false;
    }
    
    
    // ------------------------------------------------------------------------------------
    // FILTERING OPTIONS
    // ------------------------------------------------------------------------------------
    // MIN COVERAGE
    public String getMinCoverage() {
        return minCoverage;
    }
    public double getMinCoverageDouble() {
        if(minCoverage == null || minCoverage.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minCoverage);
    }
    public void setMinCoverage(String minCoverage) {
        this.minCoverage = minCoverage;
    }
    
    // MAX COVERAGE
    public String getMaxCoverage() {
        return maxCoverage;
    }
    public double getMaxCoverageDouble() {
        if(maxCoverage == null || maxCoverage.trim().length() == 0)
            return 100.0;
        else
            return Double.parseDouble(maxCoverage);
    }
    public void setMaxCoverage(String maxCoverage) {
        this.maxCoverage = maxCoverage;
    }
    
    // MIN PEPTIDES
    public String getMinPeptides() {
        return minPeptides;
    }
    public int getMinPeptidesInteger() {
        if(minPeptides == null || minPeptides.trim().length() == 0)
            return 1;
        return Integer.parseInt(minPeptides);
    }
    public void setMinPeptides(String minPeptides) {
        this.minPeptides = minPeptides;
    }

    // MAX PEPTIDES
    public String getMaxPeptides() {
        return maxPeptides;
    }
    public int getMaxPeptidesInteger() {
        if(maxPeptides == null || maxPeptides.trim().length() == 0)
            return Integer.MAX_VALUE;
        return Integer.parseInt(maxPeptides);
    }
    public void setMaxPeptides(String maxPeptides) {
        this.maxPeptides = maxPeptides;
    }
    
    // MIN UNIQUE PEPTIDES
    public String getMinUniquePeptides() {
        return minUniquePeptides;
    }
    public int getMinUniquePeptidesInteger() {
        if(minUniquePeptides == null || minUniquePeptides.trim().length() == 0)
            return 0;
        else
            return Integer.parseInt(minUniquePeptides);
    }
    public void setMinUniquePeptides(String minUniquePeptides) {
        this.minUniquePeptides = minUniquePeptides;
    }

    // MAX UNIQUE PEPTIDES
    public String getMaxUniquePeptides() {
        return maxUniquePeptides;
    }
    public int getMaxUniquePeptidesInteger() {
        if(maxUniquePeptides == null || maxUniquePeptides.trim().length() == 0)
            return Integer.MAX_VALUE;
        else
            return Integer.parseInt(maxUniquePeptides);
    }
    public void setMaxUniquePeptides(String maxUniquePeptides) {
        this.maxUniquePeptides = maxUniquePeptides;
    }

    // MIN SPECTRUM MATCHES
    public String getMinSpectrumMatches() {
        return minSpectrumMatches;
    }
    public int getMinSpectrumMatchesInteger() {
        if(minSpectrumMatches == null || minSpectrumMatches.trim().length() == 0)
            return 1;
        else
            return Integer.parseInt(minSpectrumMatches);
    }
    public void setMinSpectrumMatches(String minSpectrumMatches) {
        this.minSpectrumMatches = minSpectrumMatches;
    }
    
    // MAX SPECTRUM MATCHES
    public String getMaxSpectrumMatches() {
        return maxSpectrumMatches;
    }
    public int getMaxSpectrumMatchesInteger() {
        if(maxSpectrumMatches == null || maxSpectrumMatches.trim().length() == 0)
            return Integer.MAX_VALUE;
        return Integer.parseInt(maxSpectrumMatches);
    }
    public void setMaxSpectrumMatches(String maxSpectrumMatches) {
        this.maxSpectrumMatches = maxSpectrumMatches;
    }
    
    // USE PARSIMONIOUS AND NON-PARSIMONIOUS PROTEINS
    public int getParsimoniousParam() {
        return parsimoniousParam;
    }
    
    public void setParsimoniousParam(int parsimParam) {
        this.parsimoniousParam = parsimParam;
    }
    

    // GROUP INDISTINGUISHABLE PROTEINS
    public boolean getGroupIndistinguishableProteins() {
        return groupIndistinguishableProteins;
    }

    public void setGroupIndistinguishableProteins(boolean groupProteins) {
        this.groupIndistinguishableProteins = groupProteins;
    }
    
    // ACCESSION STRING FILTERS
    public String getAccessionLike() {
        if(accessionLike == null || accessionLike.trim().length() == 0)
            return null;
        else
            return accessionLike.trim();
            
    }
    
    public void setAccessionLike(String accessionLike) {
        this.accessionLike = accessionLike;
    }
    
    // DESCRIPTION STRING FILTERS
    public String getDescriptionLike() {
        if(descriptionLike == null || descriptionLike.trim().length() == 0)
            return null;
        else
            return descriptionLike.trim();
            
    }
    
    public void setDescriptionLike(String descriptionLike) {
        this.descriptionLike = descriptionLike;
    }

    // VALIDATION STATUS
    public String[] getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String[] validationStatus) {
        this.validationStatus = validationStatus;
    }
    
    public void setValidationStatusString(String validationStatus) {
        if(validationStatus == null)
            this.validationStatus = new String[0];
        validationStatus = validationStatus.trim();
        String tokens[] = validationStatus.split(",");
        this.validationStatus = new String[tokens.length];
        int idx = 0;
        for(String tok: tokens) {
            this.validationStatus[idx++] = tok.trim();
        }
    }
    
    public String getValidationStatusString() {
        if(this.validationStatus == null)
            return null;
        StringBuilder buf = new StringBuilder();
        for(String status: validationStatus) {
            buf.append(",");
            buf.append(status);
        }
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        return buf.toString();
    }
    
    // -------------------------------------------------------------------------------
    // FILTER CRITERIA
    // -------------------------------------------------------------------------------
    public ProteinFilterCriteria getFilterCriteria() {
        ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
        filterCriteria.setCoverage(this.getMinCoverageDouble());
        filterCriteria.setMaxCoverage(this.getMaxCoverageDouble());
        filterCriteria.setNumPeptides(this.getMinPeptidesInteger());
        filterCriteria.setNumMaxPeptides(this.getMaxPeptidesInteger());
        filterCriteria.setNumUniquePeptides(this.getMinUniquePeptidesInteger());
        filterCriteria.setNumMaxUniquePeptides(this.getMaxUniquePeptidesInteger());
        filterCriteria.setNumSpectra(this.getMinSpectrumMatchesInteger());
        filterCriteria.setNumMaxSpectra(this.getMaxSpectrumMatchesInteger());
        if(this.parsimoniousParam == ProteinDatasetComparer.PARSIM_NONE)
            filterCriteria.setParsimonious(false);
        else
            filterCriteria.setParsimonious(true);
        filterCriteria.setAccessionLike(this.getAccessionLike());
        filterCriteria.setDescriptionLike(this.getDescriptionLike());
        return filterCriteria;
    }
    
    // -------------------------------------------------------------------------------
    // DATASET FILTERS
    // -------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------
    // AND list
    //-----------------------------------------------------------------------------
    public SelectableDataset getAndDataset(int index) {
        while(index >= andList.size()) {
            andList.add(new SelectableDataset());
        }
        return andList.get(index);
    }
    
    public void setAndList(List<SelectableDataset> andList) {
        this.andList = andList;
    }
    
    public List<SelectableDataset> getAndList() {
        return andList;
    }
    
    //-----------------------------------------------------------------------------
    // OR list
    //-----------------------------------------------------------------------------
    public SelectableDataset getOrDataset(int index) {
        while(index >= orList.size()) {
            orList.add(new SelectableDataset());
        }
        return orList.get(index);
    }
    
    public void setOrList(List<SelectableDataset> orList) {
        this.orList = orList;
    }
    
    public List<SelectableDataset> getOrList() {
        return orList;
    }
    
    //-----------------------------------------------------------------------------
    // NOT list
    //-----------------------------------------------------------------------------
    public SelectableDataset getNotDataset(int index) {
        while(index >= notList.size()) {
            notList.add(new SelectableDataset());
        }
        return notList.get(index);
    }
    
    public void setNotList(List<SelectableDataset> notList) {
        this.notList = notList;
    }
    
    public List<SelectableDataset> getNotList() {
        return notList;
    }
    
    //-----------------------------------------------------------------------------
    // XOR list
    //-----------------------------------------------------------------------------
    public SelectableDataset getXorDataset(int index) {
        while(index >= xorList.size()) {
            xorList.add(new SelectableDataset());
        }
        return xorList.get(index);
    }
    
    public void setXorList(List<SelectableDataset> xorList) {
        this.xorList = xorList;
    }
    
    public List<SelectableDataset> getXorList() {
        return xorList;
    }

    
    //-----------------------------------------------------------------------------
    // Protein Prophet datasets
    //-----------------------------------------------------------------------------
    public boolean getHasProteinProphetDatasets() {
        return hasProteinProphetDatasets ;
    }
    
    public void setHasProteinProphetDatasets(boolean hasProteinProphetDatasets) {
        this.hasProteinProphetDatasets = hasProteinProphetDatasets;
    }
    
    public String getErrorRate() {
        return errorRate;
    }
    public double getErrorRateDouble() {
        if(errorRate == null || errorRate.trim().length() == 0)
            return 0.01;
        else
            return Double.parseDouble(errorRate);
    }
    public void setErrorRate(String errorRate) {
        this.errorRate = errorRate;
    }
    
    public boolean getUseProteinGroupProbability() {
        return this.useProteinGroupProbability;
    }
    
    public void setUseProteinGroupProbability(boolean useProteinGroupProbability) {
        this.useProteinGroupProbability = useProteinGroupProbability;
    }
    
    //-----------------------------------------------------------------------------
    // Total
    //-----------------------------------------------------------------------------
    public int getSelectedRunCount() {
        return andList.size();
    }
    
    public List<Integer> getAllSelectedRunIds() {
        
        List<Integer> all = new ArrayList<Integer>();
        for (SelectableDataset dataset: andList) {
            all.add(dataset.getDatasetId());
        }
        return all;
    }
    
    
    public ProteinDatasetComparisonFilters getSelectedBooleanFilters() {
        
        List<SelectableDataset> andDataset = getAndList();
        List<SelectableDataset> orDataset = getOrList();
        List<SelectableDataset> notDataset = getNotList();
        List<SelectableDataset> xorDataset = getXorList();
        
        List<Dataset> andFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: andDataset) {
            if(sds.isSelected())    andFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }
        
        List<Dataset> orFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: orDataset) {
            if(sds.isSelected())    orFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }
        
        List<Dataset> notFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: notDataset) {
            if(sds.isSelected())    notFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }
        
        List<Dataset> xorFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: xorDataset) {
            if(sds.isSelected())    xorFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }
        
        ProteinDatasetComparisonFilters filters = new ProteinDatasetComparisonFilters();
        filters.setAndFilters(andFilters);
        filters.setOrFilters(orFilters);
        filters.setNotFilters(notFilters);
        filters.setXorFilters(xorFilters);
        return filters;
    }
    
}
