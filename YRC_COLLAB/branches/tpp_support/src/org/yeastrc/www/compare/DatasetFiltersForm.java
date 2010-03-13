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
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.FilterableDataset;
import org.yeastrc.www.compare.dataset.ProteinProphetDataset;
import org.yeastrc.www.compare.dataset.SelectableDataset;

/**
 * 
 */
public class DatasetFiltersForm extends ActionForm {

    private List<SelectableDataset> andList = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> orList  = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> notList = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> xorList = new ArrayList<SelectableDataset>();
    
    private int parsimoniousParam = ProteinDatasetComparer.PARSIM.ONE.getNumericValue(); // parsimonious in at least one
    
    private boolean groupIndistinguishableProteins = false;
    
    private String minMolWt;
    private String maxMolWt;
    private String minPi;
    private String maxPi;
    
    private String accessionLike = null;
    private String descriptionLike = null;
    private String descriptionNotLike = null;
    
    private boolean keepProteinGroups = false;

    // FOR PROTEIN-PROPHET
    private boolean hasProteinProphetDatasets = false;
    private String errorRate = "0.01";
    private boolean useProteinGroupProbability = false;
    
    
    
    public void reset() {
        accessionLike = null;
        descriptionLike = null;
        errorRate = "0.01";
        
        groupIndistinguishableProteins = false;
        useProteinGroupProbability = false;
        
        keepProteinGroups = false;
    }
    
    public boolean isKeepProteinGroups() {
        return keepProteinGroups;
    }

    public void setKeepProteinGroups(boolean keepProteinGroups) {
        this.keepProteinGroups = keepProteinGroups;
    }
    
    // ------------------------------------------------------------------------------------
    // FILTERING OPTIONS
    // ------------------------------------------------------------------------------------
    
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
    
    public String getDescriptionNotLike() {
        if(descriptionNotLike == null || descriptionNotLike.trim().length() == 0)
            return null;
        else
            return descriptionNotLike.trim();
            
    }
    
    public void setDescriptionNotLike(String descriptionNotLike) {
        this.descriptionNotLike = descriptionNotLike;
    }
    
    //-----------------------------------------------------------------------------
    // Molecular Weight
    //-----------------------------------------------------------------------------
    public String getMinMolecularWt() {
        return minMolWt;
    }
    public Double getMinMolecularWtDouble() {
        if(minMolWt != null && minMolWt.trim().length() > 0)
            return Double.parseDouble(minMolWt);
        return 0.0;
    }
    public void setMinMolecularWt(String molWt) {
        this.minMolWt = molWt;
    }
    
    public String getMaxMolecularWt() {
        return maxMolWt;
    }
    public Double getMaxMolecularWtDouble() {
        if(maxMolWt != null && maxMolWt.trim().length() > 0)
            return Double.parseDouble(maxMolWt);
        return Double.MAX_VALUE;
    }
    public void setMaxMolecularWt(String molWt) {
        this.maxMolWt = molWt;
    }
    
    //-----------------------------------------------------------------------------
    // pI
    //-----------------------------------------------------------------------------
    public String getMinPi() {
        return minPi;
    }
    public Double getMinPiDouble() {
        if(minPi != null && minPi.trim().length() > 0)
            return Double.parseDouble(minPi);
        return 0.0;
    }
    public void setMinPi(String pi) {
        this.minPi = pi;
    }
    
    public String getMaxPi() {
        return maxPi;
    }
    public Double getMaxPiDouble() {
        if(maxPi != null && maxPi.trim().length() > 0)
            return Double.parseDouble(maxPi);
        return Double.MAX_VALUE;
    }
    public void setMaxPi(String pi) {
        this.maxPi = pi;
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
    
    // -------------------------------------------------------------------------------
    // BOOLEAN FILTERS
    // -------------------------------------------------------------------------------
    public DatasetBooleanFilters getSelectedBooleanFilters() {
        
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
        
        DatasetBooleanFilters filters = new DatasetBooleanFilters();
        filters.setAndFilters(andFilters);
        filters.setOrFilters(orFilters);
        filters.setNotFilters(notFilters);
        filters.setXorFilters(xorFilters);
        return filters;
    }
    
    // -------------------------------------------------------------------------------
    // PROTEIN PROPERTIES FILTERS
    // -------------------------------------------------------------------------------
    public ProteinPropertiesFilters getProteinPropertiesFilters() {
    	
        ProteinPropertiesFilters filters = new ProteinPropertiesFilters();
        
        filters.setMinMolecularWt(this.getMinMolecularWtDouble());
        filters.setMaxMolecularWt(this.getMaxMolecularWtDouble());
        filters.setMinPi(this.getMinMolecularWtDouble());
        filters.setMaxPi(this.getMaxPiDouble());
        
        filters.setAccessionLike(this.getAccessionLike());
        filters.setDescriptionLike(this.getDescriptionLike());
        filters.setDescriptionNotLike(this.getDescriptionNotLike());
        
        return filters;
    }
    
    // -------------------------------------------------------------------------------
    // FILTER CRITERIA
    // -------------------------------------------------------------------------------
    public ProteinFilterCriteria getFilterCriteria() {
        ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
        
        if(this.parsimoniousParam == ProteinDatasetComparer.PARSIM.NONE.getNumericValue()) {
        	// get everything
            filterCriteria.setParsimonious(true);
            filterCriteria.setNonParsimonious(true);
        }
        else {
            filterCriteria.setParsimonious(true);
            filterCriteria.setNonParsimonious(false);
        }
       
        return filterCriteria;
    }
    
    public ProteinProphetFilterCriteria getProteinProphetFilterCriteria(FilterableDataset dataset) {
    	
        ProteinProphetFilterCriteria filterCriteria = new ProteinProphetFilterCriteria(this.getFilterCriteria());
        
        double minProbability = ((ProteinProphetDataset)dataset).getRoc().getMinProbabilityForError(this.getErrorRateDouble());
        if(this.getUseProteinGroupProbability())
            filterCriteria.setMinGroupProbability(minProbability);
        else filterCriteria.setMinProteinProbability(minProbability);
        ((ProteinProphetDataset)dataset).setProteinFilterCriteria(filterCriteria);
        
        return filterCriteria;
    }
    
    
}
