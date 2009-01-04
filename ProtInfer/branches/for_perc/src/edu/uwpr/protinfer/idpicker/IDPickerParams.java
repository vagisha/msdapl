package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerFilter;

public class IDPickerParams {

    private float maxAbsoluteFdr = 0.05f;
    private float maxRelativeFdr = 0.05f;
    private float decoyRatio = 1.0f;
//    private int minDistinctPeptides = 1;
    private boolean doParsimonyAnalysis = true;
    private boolean doFdrCalculation = true;
    private String decoyPrefix = "";
    private boolean useIdPickerFDRFormula = true;
    
    List<IdPickerFilter> moreFilters = new ArrayList<IdPickerFilter>();
    
    
    public boolean useIdPickerFDRFormula() {
        return useIdPickerFDRFormula;
    }
    public void setUseIdPickerFDRFormula(boolean useIdPickerFDRFormula) {
        this.useIdPickerFDRFormula = useIdPickerFDRFormula;
    }
    public String getDecoyPrefix() {
        return decoyPrefix;
    }
    public void setDecoyPrefix(String decoyPrefix) {
        this.decoyPrefix = decoyPrefix;
    }
    public float getMaxAbsoluteFdr() {
        return maxAbsoluteFdr;
    }
    public void setMaxAbsoluteFdr(float maxAbsoluteFdr) {
        this.maxAbsoluteFdr = maxAbsoluteFdr;
    }
    public float getMaxRelativeFdr() {
        return maxRelativeFdr;
    }
    public void setMaxRelativeFdr(float maxRelativeFdr) {
        this.maxRelativeFdr = maxRelativeFdr;
    }
    public float getDecoyRatio() {
        return decoyRatio;
    }
    public void setDecoyRatio(float decoyRatio) {
        this.decoyRatio = decoyRatio;
    }
//    public int getMinDistinctPeptides() {
//        return minDistinctPeptides;
//    }
//    public void setMinDistinctPeptides(int minDistinctPeptides) {
//        this.minDistinctPeptides = minDistinctPeptides;
//    }
    public boolean getDoParsimonyAnalysis() {
        return doParsimonyAnalysis;
    }
    public void setDoParsimonyAnalysis(boolean doParsimonyAnalysis) {
        this.doParsimonyAnalysis = doParsimonyAnalysis;
    }
    
    public boolean getDoFdrCalculation() {
        return doFdrCalculation;
    }
    public void setDoFdrCalculation(boolean doFdrCalculation) {
        this.doFdrCalculation = doFdrCalculation;
    }
    
    public List<IdPickerFilter> getMoreFilters() {
        return moreFilters;
    }
    public void addMoreFilters(List<IdPickerFilter> moreFilters) {
        this.moreFilters = moreFilters;
    }
    
}
