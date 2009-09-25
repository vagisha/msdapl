package org.yeastrc.www.proteinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

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
    
    public int getResultId() {
        return idpPsm.getResultId();
    }
    
    public ProteinferSpectrumMatch getProteinferSpectrumMatch() {
        return idpPsm;
    }

    public MsSearchResult getSpectrumMatch() {
        return spectrumMatch;
    }

    public String getModifiedSequence() {
        try {
            return removeTerminalResidues(spectrumMatch.getResultPeptide().getModifiedPeptide());
        }
        catch (ModifiedSequenceBuilderException e) {
            return null;
        }
    }
    
    private static String removeTerminalResidues(String peptide) {
        int f = peptide.indexOf('.');
        int l = peptide.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? peptide.length() : l;
        return peptide.substring(f, l);
    }
}
