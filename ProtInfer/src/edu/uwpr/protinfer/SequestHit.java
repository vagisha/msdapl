package edu.uwpr.protinfer;

import java.math.BigDecimal;

import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.PeptideSpectrumMatch;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.infer.SearchSource;


public class SequestHit implements PeptideSpectrumMatch<SequestSpectrumMatch >{

    private SequestSpectrumMatch spectrumMatch;
    private PeptideHit peptide;
    
    private Boolean isDecoyHit = null;
    private Boolean isTargetHit = null;
    
    public SequestHit(SearchSource source, int scanNumber, int charge, PeptideHit peptide) {
        this.peptide = peptide;
        spectrumMatch = new SequestSpectrumMatch(source, scanNumber, charge, peptide.getModifiedSequence());
    }
    
    public void setXcorr(BigDecimal xcorr) {
        spectrumMatch.setXcorr(xcorr);
    }
    
    public BigDecimal getXcorr() {
        return spectrumMatch.getXcorr();
    }
    
    public void setDeltaCn(BigDecimal deltaCn) {
        spectrumMatch.setDeltaCn(deltaCn);
    }
    
    public BigDecimal getDeltaCn() {
        return spectrumMatch.getDeltaCn();
    }
    
    public int getCharge() {
        return spectrumMatch.getCharge();
    }
    
    public int getScanNumber() {
        return spectrumMatch.getScanNumber();
    }
    
    public PeptideHit getPeptideHit() {
        return peptide;
    }
    
    public int getScanId() {
        return spectrumMatch.getScanId();
    }

    public void setScanId(int scanId) {
        spectrumMatch.setScanId(scanId);
    }

    public int getHitId() {
        return spectrumMatch.getHitId();
    }

    public void setHitId(int hitId) {
        spectrumMatch.setHitId(hitId);
    }

    public String toString() {
        return spectrumMatch.toString()+"\n"+peptide.toString();
    }
    
    public SequestSpectrumMatch getSpectrumMatch() {
        return spectrumMatch;
    }
    
    public boolean isDecoyMatch() {
        if (isDecoyHit != null) {
            return isDecoyHit;
        }
        else {
            determineDecoyTargetHit();
            return isDecoyHit;
            
        }
    }
    
    public boolean isTargetMatch() {
        if (isTargetHit != null) {
            return isTargetHit;
        }
        else {
            determineDecoyTargetHit();
            return isTargetHit;
        }
    }
    
    private void determineDecoyTargetHit() {
        isDecoyHit = false;
        isTargetHit = false;
        for (ProteinHit prot: peptide.getProteinList()) {
            if (prot.getProtein().isDecoy())
                isDecoyHit = true;
            else
                isTargetHit = true;
        }
    }

    @Override
    public String getPeptideSequence() {
        return peptide.getModifiedSequence();
    }

    @Override
    public SearchSource getSearchSource() {
        return spectrumMatch.getSearchSource();
    }
}
