package org.yeastrc.www.proteinfer.idpicker;

import org.yeastrc.ms.domain.search.MsSearchResult;

import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class WIdPickerSpectrumMatch {
    
    private int scanNumber;
    private MsSearchResult spectrumMatch;
    private ProteinferSpectrumMatch idpPsm;
    
    public WIdPickerSpectrumMatch(ProteinferSpectrumMatch idpPsm, MsSearchResult psm) {
        this.idpPsm = idpPsm;
        this.spectrumMatch = psm;
    }

    public int getScanNumber() {
        return scanNumber;
    }

    public void setScanNumber(int scanNumber) {
        this.scanNumber = scanNumber;
    }

    public int getScanId() {
        return spectrumMatch.getScanId();
    }
    
    public int getRunSearchResultId() {
        return idpPsm.getMsRunSearchResultId();
    }
    
    public ProteinferSpectrumMatch getProteinferSpectrumMatch() {
        return idpPsm;
    }

    public MsSearchResult getSpectrumMatch() {
        return spectrumMatch;
    }

    public String getModifiedSequence() {
        return removeTerminalResidues(spectrumMatch.getResultPeptide().getModifiedPeptide());
    }
    
    private static String removeTerminalResidues(String peptide) {
        int f = peptide.indexOf('.');
        int l = peptide.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? peptide.length() : l;
        return peptide.substring(f, l);
    }
}
