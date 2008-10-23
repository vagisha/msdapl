package edu.uwpr.protinfer;


public class SearchHit {

    private PeptideSequenceMatch psm;
    private PeptideHit peptide;
    
    private Boolean isDecoyHit = null;
    private Boolean isTargetHit = null;
    
    public SearchHit(SearchSource source, int scanNumber, int charge, double score, PeptideHit peptide) {
        this.peptide = peptide;
        psm = new PeptideSequenceMatch(source, scanNumber, charge, score);
    }
    
    public double getScore() {
        return psm.getScore();
    }
    
    public int getCharge() {
        return psm.getCharge();
    }
    
    public int getScanNumber() {
        return psm.getScanNumber();
    }
    
    public PeptideHit getPeptideHit() {
        return peptide;
    }
    
    public int getScanId() {
        return psm.getScanId();
    }

    public void setScanId(int scanId) {
        psm.setScanId(scanId);
    }

    public int getHitId() {
        return psm.getHitId();
    }

    public void setHitId(int hitId) {
        psm.setHitId(hitId);
    }

    public String toString() {
        return psm.toString()+"\n"+peptide.toString();
    }
    
    public PeptideSequenceMatch getPeptideSequenceMatch() {
        return psm;
    }
    
    public boolean isDecoyHit() {
        if (isDecoyHit != null) {
            return isDecoyHit;
        }
        else {
            determineDecoyTargetHit();
            return isDecoyHit;
            
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

    public boolean isTargetHit() {
        if (isTargetHit != null) {
            return isTargetHit;
        }
        else {
            determineDecoyTargetHit();
            return isTargetHit;
        }
    }
}
