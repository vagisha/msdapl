package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResult;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideIon;

public class WIdPickerPeptideIonWSpectra <T extends MsSearchResult> {

    private String sequence;
    private int charge;
    private double bestFdr;
    private boolean uniqueToProteinGrp = false;
    
    private List<WIdPickerSpectrumMatch<T>> psmList;
    
    public WIdPickerPeptideIonWSpectra(IdPickerPeptideIon ion) {
        this.sequence = ion.getSequence();
        this.charge = ion.getCharge();
        psmList = new ArrayList<WIdPickerSpectrumMatch<T>>();
    }
    
    public void addMsSearchResult(WIdPickerSpectrumMatch<T> result) {
        psmList.add(result);
    }
    
    public List<WIdPickerSpectrumMatch<T>> getPsmList() {
        return psmList;
    }
    
    public String getSequence() {
        return sequence;
    }
    
    public int getCharge() {
        return charge;
    }
    
    public double getBestFdr() {
        double best = Double.MAX_VALUE;
        for(WIdPickerSpectrumMatch<T> psm: psmList) {
            best = Math.min(best, psm.getFdr());
        }
        return best;
    }
    
    public boolean isUniqueToProteinGroup() {
        return uniqueToProteinGrp;
    }
    
    public void setIdUniqueToProteinGroup(boolean unique) {
        this.uniqueToProteinGrp = unique;
    }
    
    public int getSpectralCount() {
        return psmList.size();
    }
}
