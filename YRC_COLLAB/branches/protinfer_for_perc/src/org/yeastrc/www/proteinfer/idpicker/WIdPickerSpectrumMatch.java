package org.yeastrc.www.proteinfer.idpicker;

import org.yeastrc.ms.domain.search.MsSearchResult;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch;

public class WIdPickerSpectrumMatch <T extends MsSearchResult> {
    
    private int scanNumber;
    private T spectrumMatch;
    private IdPickerSpectrumMatch idpPsm;
    
    public WIdPickerSpectrumMatch() {}

    public int getScanNumber() {
        return scanNumber;
    }

    public T getSpectrumMatch() {
        return spectrumMatch;
    }

    public double getFdr() {
        return idpPsm.getFdrRounded();
    }

    public int getScanId() {
        return spectrumMatch.getScanId();
    }
    
    public int getSearchResultId() {
        return idpPsm.getMsRunSearchResultId();
    }
    
    public IdPickerSpectrumMatch getIdPickerSpectrumMatch() {
        return idpPsm;
    }

    public void setIdPickerSpectrumMatch(IdPickerSpectrumMatch idpPsm) {
        this.idpPsm = idpPsm;
    }

    public void setScanNumber(int scanNumber) {
        this.scanNumber = scanNumber;
    }

    public void setSpectrumMatch(T spectrumMatch) {
        this.spectrumMatch = spectrumMatch;
    }
    
    public String getSequence() {
        return spectrumMatch.getResultPeptide().getPeptideSequence();
    }
    
    public String getModifiedSequence() {
        return spectrumMatch.getResultPeptide().getModifiedPeptideSequence();
    }
}
