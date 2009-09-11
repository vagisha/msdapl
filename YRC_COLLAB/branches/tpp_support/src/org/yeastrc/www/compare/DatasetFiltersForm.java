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

/**
 * 
 */
public class DatasetFiltersForm extends ActionForm {

    
//    private List<ProteinferRunFormBean> proteinferRunList = new ArrayList<ProteinferRunFormBean>();
    private List<ProteinProphetRunFormBean> proteinProphetRunList = new ArrayList<ProteinProphetRunFormBean>();
//    private List<DTASelectRunFormBean> dtaRuns = new ArrayList<DTASelectRunFormBean>();
    
    
    private List<SelectableDataset> andList = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> orList  = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> notList = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> xorList = new ArrayList<SelectableDataset>();
    
    private boolean useAllProteins = true;
    private boolean groupProteins = true;
    
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
    
    
    public void reset() {
        minCoverage = "0.0";
        minPeptides = "1";
        minUniquePeptides = "0";
        minSpectrumMatches = "1";
        useAllProteins = true;
        groupProteins = true;
        accessionLike = null;
        descriptionLike = null;
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
    public boolean getUseAllProteins() {
        return useAllProteins;
    }

    public void setUseAllProteins(boolean useAllProteins) {
        this.useAllProteins = useAllProteins;
    }
    
    public boolean getOnlyParsimonious() {
        return !useAllProteins;
    }
    
    public void setOnlyParsimonious(boolean onlyParsimonious) {
        this.useAllProteins = !onlyParsimonious;
    }
    
    // GROUP INDISTINGUISHABLE PROTEINS
    public boolean getGroupIndistinguishableProteins() {
        return groupProteins;
    }

    public void setGroupIndistinguishableProteins(boolean groupProteins) {
        this.groupProteins = groupProteins;
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
        filterCriteria.setParsimonious(this.getUseAllProteins());
        filterCriteria.setAccessionLike(this.getAccessionLike());
        filterCriteria.setDescriptionLike(this.getDescriptionLike());
        return filterCriteria;
    }
    
    public void setFilterCriteria(ProteinFilterCriteria filterCriteria) {
        this.minCoverage = String.valueOf(filterCriteria.getCoverage());
        this.maxCoverage = String.valueOf(filterCriteria.getMaxCoverage());
        this.minPeptides = String.valueOf(filterCriteria.getNumPeptides());
        this.maxPeptides = String.valueOf(filterCriteria.getNumMaxPeptides());
        this.minUniquePeptides = String.valueOf(filterCriteria.getNumUniquePeptides());
        this.maxUniquePeptides = String.valueOf(filterCriteria.getNumMaxUniquePeptides());
        this.minSpectrumMatches = String.valueOf(filterCriteria.getNumSpectra());
        this.maxSpectrumMatches = String.valueOf(filterCriteria.getNumMaxSpectra());
        this.useAllProteins = filterCriteria.getParsimonious();
        this.accessionLike = filterCriteria.getAccessionLike();
        this.descriptionLike = filterCriteria.getDescriptionLike();
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

    
    // -------------------------------------------------------------------------------
    // DATASET BEING COMPARED
    // -------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------
    // Protein inference datasets
    //-----------------------------------------------------------------------------
//    public ProteinferRunFormBean getProteinferRun(int index) {
//        while(index >= proteinferRunList.size())
//            proteinferRunList.add(new ProteinferRunFormBean());
//        return proteinferRunList.get(index);
//    }
//    
//    public void setProteinferRunList(List <ProteinferRunFormBean> piRuns) {
//        this.proteinferRunList = piRuns;
//    }
//    
//    public List <ProteinferRunFormBean> getProteinferRunList() {
//        return proteinferRunList;
//    }
//    
//    public List<Integer> getSelectedProteinferRunIds() {
//        List<Integer> ids = new ArrayList<Integer>();
//        for(ProteinferRunFormBean run: proteinferRunList) {
//            if(run != null && run.isSelected())
//                ids.add(run.getRunId());
//        }
//        return ids;
//    }
    
    //-----------------------------------------------------------------------------
    // Protein Prophet datasets
    //-----------------------------------------------------------------------------
    public ProteinProphetRunFormBean getProteinProphetRun(int index) {
        while(index >= proteinProphetRunList.size())
            proteinProphetRunList.add(new ProteinProphetRunFormBean());
        return proteinProphetRunList.get(index);
    }
    
    public void setProteinProphetRunList(List <ProteinProphetRunFormBean> proteinProphetRuns) {
        this.proteinProphetRunList = proteinProphetRuns;
    }
    
    public List <ProteinProphetRunFormBean> getProteinProphetRunList() {
        return proteinProphetRunList;
    }
    
    public List<Integer> getSelectedProteinProphetRunIds() {
        List<Integer> ids = new ArrayList<Integer>();
        for(ProteinferRunFormBean run: proteinProphetRunList) {
            if(run != null && run.isSelected())
                ids.add(run.getRunId());
        }
        return ids;
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
    
}
